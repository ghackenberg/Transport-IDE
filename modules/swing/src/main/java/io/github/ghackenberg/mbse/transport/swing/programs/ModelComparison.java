package io.github.ghackenberg.mbse.transport.swing.programs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Controller;
import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.core.Synchronizer;
import io.github.ghackenberg.mbse.transport.core.controllers.SmartController;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.exceptions.ArgumentsException;
import io.github.ghackenberg.mbse.transport.core.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.core.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.swing.dialogs.ModelOpenDialog;
import io.github.ghackenberg.mbse.transport.swings.viewers.MultipleViewer;

public class ModelComparison {

	public static void main(String[] args) {
		try {
			// Create parser
			
			Parser parser = new Parser();
			
			// Parse models
			
			List<File> folders = new ArrayList<>();
			
			List<Model> models = new ArrayList<>();
			
			do {
				// Choose folder
				
				File modelFolder = ModelOpenDialog.choose();
				
				if (modelFolder == null) {
					break;
				}
				
				// Check runs folder

				File runsFolder = new File(modelFolder, "runs");
				
				if (!runsFolder.exists())
					runsFolder.mkdir();
				else if (!runsFolder.isDirectory())
					throw new ArgumentsException("Path to model contains a runs file");

				File indexRunsFolder = new File(runsFolder, "run-" + folders.size());
				
				if (!indexRunsFolder.exists())
					indexRunsFolder.mkdir();
				else if (!indexRunsFolder.isDirectory())
					throw new ArgumentsException("Path to model runs contains a run-" + folders.size() + " file");
				
				// Parse
				
				Model model = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "stations.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
				
				model.name = modelFolder.getName();
				
				model.demands.clear();
				
				folders.add(indexRunsFolder);
				
				models.add(model);
			} while (true);
			
			// Generate demands
			
			for (int index = 0; index < 100; index++) {
				// Select size
				
				double size = Math.random() * 4;
				
				// Select times
				
				double pickupTime = Math.random() * 1000000;
				double dropoffTime = pickupTime + Math.random() * 100000;
				
				// Select segments
				
				int pickupSegmentNumber = (int) (Math.random() * models.get(0).segments.size());
				int dropoffSegmentNumber = (int) (Math.random() * models.get(0).segments.size());
				
				// Select distance (in percent)
				
				double pickupDistance = Math.random();
				double dropoffDistance = Math.random();
				
				// Check segment validity
				
				boolean valid = true;
				
				for (Model model : models) {
					for (Station station : model.stations) {
						if (station.location.segment == model.segments.get(pickupSegmentNumber)) {
							valid = false;
						} else if (station.location.segment == model.segments.get(dropoffSegmentNumber)) {
							valid = false;
						}
						if (!valid) {
							break;
						}
					}
					if (!valid) {
						break;
					}
				}
				if (!valid) {
					index--;
					continue;
				}
				
				// Process models
				
				for (Model model : models) {
					Segment pickupSegment = model.segments.get(pickupSegmentNumber);
					Segment dropoffSegment = model.segments.get(dropoffSegmentNumber);
					
					Demand demand = new Demand(pickupSegment, pickupDistance * pickupSegment.getLength(), pickupTime, dropoffSegment, dropoffDistance * dropoffSegment.getLength(), dropoffTime, size);
					
					model.demands.add(demand);
				}
			}
			
			// Sort demands and reset models
			
			for (Model model : models) {
				model.demands.sort((first, second) -> (int) Math.signum(first.pickup.time - second.pickup.time));
				
				model.reset();
			}
			
			// Create synchronizer
			
			Synchronizer synchronizer = new Synchronizer(models.size());
			
			// Define settings
			
			double maxModelTimeStep = 1000;
			double ratioModelRealTime = 30;
			
			// Generate controllers, statistics, and simulators
			
			List<Controller> controllers = new ArrayList<>();
			
			List<Statistics> statistics = new ArrayList<>();
			
			List<Simulator> simulators = new ArrayList<>();
			
			for (int index = 0; index < models.size(); index++) {
				File runsFolder = folders.get(index);
				
				Model model = models.get(index);
				
				// Controller
				
				Controller controller = new SmartController(model);
				controller.reset();
				
				controllers.add(controller);
				
				// Statistics

				Statistics statistic = new Statistics(model);
				statistic.reset();
				
				statistics.add(statistic);
				
				// Simulator
				
				Simulator simulator = new Simulator(model.name, model, controller, statistic, maxModelTimeStep, ratioModelRealTime, runsFolder, synchronizer);
				
				simulators.add(simulator);
			}
			
			// Create viewer
			
			new MultipleViewer(simulators);
			
			// Start simulators
			
			for (Simulator simulator : simulators) {
				simulator.start();
			}
		} catch (MissingException e) {
			e.printStackTrace();
		} catch (DirectoryException e) {
			e.printStackTrace();
		} catch (ArgumentsException e) {
			e.printStackTrace();
		}
	}
	
}
