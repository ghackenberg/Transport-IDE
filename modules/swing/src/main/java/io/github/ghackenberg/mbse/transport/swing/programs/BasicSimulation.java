package io.github.ghackenberg.mbse.transport.swing.programs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Exporter;
import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.controllers.GreedyController;
import io.github.ghackenberg.mbse.transport.core.controllers.ManualController;
import io.github.ghackenberg.mbse.transport.core.controllers.RandomController;
import io.github.ghackenberg.mbse.transport.core.controllers.SmartController;
import io.github.ghackenberg.mbse.transport.core.controllers.SwitchableController;
import io.github.ghackenberg.mbse.transport.core.exceptions.ArgumentsException;
import io.github.ghackenberg.mbse.transport.core.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.core.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.core.exporters.CSVExporter;
import io.github.ghackenberg.mbse.transport.swing.dialogs.ModelOpenDialog;
import io.github.ghackenberg.mbse.transport.swings.viewers.ModelViewer;
import io.github.ghackenberg.mbse.transport.swings.viewers.SingleViewer;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.single.DemandDistancesChartViewer;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.single.DemandTimesChartViewer;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.single.IntersectionCrossingsChartViewer;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.single.SegmentTraversalsChartViewer;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.single.VehicleBatteriesChartViewer;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.single.VehicleDistancesChartViewer;

public class BasicSimulation {

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
			Model model = parser.parse(modelFolder);
			// Create controller
			SwitchableController controller = new SwitchableController();
			controller.addController(new RandomController());
			controller.addController(new ManualController());
			controller.addController(new GreedyController(model));
			controller.addController(new SmartController(model));
			controller.reset();
			// Create simulator
			Simulator simulator = new Simulator("Static", model, controller, 1000.0 / 30.0, 1.0, runsFolder);
			// Create simulators
			List<Simulator> simulators = new ArrayList<>();
			simulators.add(simulator);
			// Create exporter
			Exporter exporter = new CSVExporter(".");
			// Create viewer
			SingleViewer viewer = new SingleViewer(simulator, controller);
			viewer.addViewer(0, 0, 1, 2, new ModelViewer(model, simulator.getStatistics()));
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
				exporter.export(model, simulator.getStatistics());
			});
			simulator.setHandleFinished(() -> {
				viewer.handleFinished();
				exporter.export(model, simulator.getStatistics());
			});
			simulator.setHandleException(exception -> {
				viewer.handleException(exception);
			});
			simulator.start();
			// Print time
			System.out.println("Finished in " + Math.round(model.state.get().time)+ "ms");
		} catch (ArgumentsException exception) {
			
			System.err.println(exception.getMessage());
			
		} catch (MissingException exception) {
			
			System.err.println(exception.getMessage());
			
		} catch (DirectoryException exception) {
			
			System.err.println(exception.getMessage());
			
		}
		
	}

}
