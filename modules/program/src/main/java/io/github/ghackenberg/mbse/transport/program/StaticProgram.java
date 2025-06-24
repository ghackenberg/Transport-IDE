package io.github.ghackenberg.mbse.transport.program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.controller.implementations.GreedyController;
import io.github.ghackenberg.mbse.transport.controller.implementations.ManualController;
import io.github.ghackenberg.mbse.transport.controller.implementations.RandomController;
import io.github.ghackenberg.mbse.transport.controller.implementations.SmartController;
import io.github.ghackenberg.mbse.transport.controller.implementations.SwitchableController;
import io.github.ghackenberg.mbse.transport.exporter.Exporter;
import io.github.ghackenberg.mbse.transport.exporter.implementations.CSVExporter;
import io.github.ghackenberg.mbse.transport.model.Model;
import io.github.ghackenberg.mbse.transport.parser.Parser;
import io.github.ghackenberg.mbse.transport.parser.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.parser.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.program.dialogs.ModelOpenDialog;
import io.github.ghackenberg.mbse.transport.program.exceptions.ArgumentsException;
import io.github.ghackenberg.mbse.transport.simulator.Simulator;
import io.github.ghackenberg.mbse.transport.statistics.Statistics;
import io.github.ghackenberg.mbse.transport.viewer.ModelViewer;
import io.github.ghackenberg.mbse.transport.viewer.SingleViewer;
import io.github.ghackenberg.mbse.transport.viewer.charts.single.DemandDistancesChartViewer;
import io.github.ghackenberg.mbse.transport.viewer.charts.single.DemandTimesChartViewer;
import io.github.ghackenberg.mbse.transport.viewer.charts.single.IntersectionCrossingsChartViewer;
import io.github.ghackenberg.mbse.transport.viewer.charts.single.SegmentTraversalsChartViewer;
import io.github.ghackenberg.mbse.transport.viewer.charts.single.VehicleBatteriesChartViewer;
import io.github.ghackenberg.mbse.transport.viewer.charts.single.VehicleDistancesChartViewer;

public class StaticProgram {

	public static void main(String[] args) {
		
		System.out.println("SingleProgram.main");
		
		try {
			File modelFolder = ModelOpenDialog.choose();
			
			if (modelFolder == null) {
				return;
			}
			
			File runsFolder = new File(modelFolder, "runs");
			
			if (!runsFolder.exists())
				runsFolder.mkdir();
			else if (!runsFolder.isDirectory())
				throw new ArgumentsException("Path to model contains a runs file");
			
			// Create parser
			Parser parser = new Parser();
			// Parser model
			Model model = parser.parse(new File(modelFolder, "intersections.txt"), new File(modelFolder, "segments.txt"), new File(modelFolder, "stations.txt"), new File(modelFolder, "vehicles.txt"), new File(modelFolder, "demands.txt"));
			model.reset();
			// Create controller
			SwitchableController controller = new SwitchableController();
			controller.addController(new RandomController());
			controller.addController(new ManualController());
			controller.addController(new GreedyController(model));
			controller.addController(new SmartController(model));
			controller.reset();
			// Create statistics
			Statistics statistics = new Statistics(model);
			statistics.reset();
			// Create simulator
			Simulator simulator = new Simulator("Static", model, controller, statistics, 1000.0 / 30.0, 1.0, runsFolder);
			// Create simulators
			List<Simulator> simulators = new ArrayList<>();
			simulators.add(simulator);
			// Create exporter
			Exporter exporter = new CSVExporter(".");
			// Create viewer
			SingleViewer viewer = new SingleViewer(simulator, controller);
			viewer.addViewer(0, 0, 1, 2, new ModelViewer(model, statistics));
			viewer.addViewer(1, 0, 1, 1, new VehicleBatteriesChartViewer(simulators, 0));
			viewer.addViewer(1, 1, 1, 1, new VehicleDistancesChartViewer(simulators, 0));
			viewer.addViewer(2, 0, 1, 1, new DemandTimesChartViewer(simulators, 0));
			viewer.addViewer(2, 1, 1, 1, new DemandDistancesChartViewer(simulators, 0));
			viewer.addViewer(3, 0, 1, 1, new SegmentTraversalsChartViewer(simulators, 0));
			viewer.addViewer(3, 1, 1, 1, new IntersectionCrossingsChartViewer(simulators, 0));
			// Start simulator 
			simulator.setHandleUpdated(() -> {
				viewer.handleUpdated();
			});
			simulator.setHandleStopped(() -> {
				viewer.handleStopped();
				exporter.export(model, statistics);
			});
			simulator.setHandleFinished(() -> {
				viewer.handleFinished();
				exporter.export(model, statistics);
			});
			simulator.setHandleException(exception -> {
				viewer.handleException(exception);
			});
			simulator.start();
			// Print time
			System.out.println("Finished in " + Math.round(model.time)+ "ms");
		} catch (ArgumentsException exception) {
			
			System.err.println(exception.getMessage());
			
		} catch (MissingException exception) {
			
			System.err.println(exception.getMessage());
			
		} catch (DirectoryException exception) {
			
			System.err.println(exception.getMessage());
			
		}
		
	}

}
