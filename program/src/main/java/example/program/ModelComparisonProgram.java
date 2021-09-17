package example.program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import example.controller.Controller;
import example.controller.implementations.SmartController;
import example.model.Demand;
import example.model.Model;
import example.model.Segment;
import example.model.Station;
import example.parser.Parser;
import example.parser.exceptions.DirectoryException;
import example.parser.exceptions.MissingException;
import example.program.dialogs.ModelOpenDialog;
import example.program.exceptions.ArgumentsException;
import example.simulator.Simulator;
import example.simulator.Synchronizer;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.MultipleViewer;

public class ModelComparisonProgram {

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
			
			List<ExampleStatistics> statistics = new ArrayList<>();
			
			List<Simulator<ExampleStatistics>> simulators = new ArrayList<>();
			
			for (int index = 0; index < models.size(); index++) {
				File runsFolder = folders.get(index);
				
				Model model = models.get(index);
				
				// Controller
				
				Controller controller = new SmartController(model);
				controller.reset();
				
				controllers.add(controller);
				
				// Statistics

				ExampleStatistics statistic = new ExampleStatistics(model);
				statistic.reset();
				
				statistics.add(statistic);
				
				// Simulator
				
				Simulator<ExampleStatistics> simulator = new Simulator<>(model, controller, statistic, maxModelTimeStep, ratioModelRealTime, runsFolder, synchronizer);
				
				simulators.add(simulator);
			}
			
			// Create viewer
			
			new MultipleViewer(simulators);
			
			// Start simulators
			
			for (Simulator<ExampleStatistics> simulator : simulators) {
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
