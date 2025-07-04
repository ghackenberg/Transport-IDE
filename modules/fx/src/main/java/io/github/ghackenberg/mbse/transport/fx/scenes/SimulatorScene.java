package io.github.ghackenberg.mbse.transport.fx.scenes;

import java.io.File;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.core.controllers.SmartController;
import io.github.ghackenberg.mbse.transport.fx.charts.DemandDistanceChart;
import io.github.ghackenberg.mbse.transport.fx.charts.IntersectionCrossingChart;
import io.github.ghackenberg.mbse.transport.fx.charts.SegmentTraversalChart;
import io.github.ghackenberg.mbse.transport.fx.charts.StationChargeChart;
import io.github.ghackenberg.mbse.transport.fx.charts.VehicleBatteryChart;
import io.github.ghackenberg.mbse.transport.fx.helpers.GridHelper;
import io.github.ghackenberg.mbse.transport.fx.helpers.ImageViewHelper;
import io.github.ghackenberg.mbse.transport.fx.viewers.deep.ModelViewerDeep;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.ModelViewerFlat;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class SimulatorScene extends Scene {
	
	private final Thread thread;
	
	private final Button start = new Button("Start", ImageViewHelper.load("start.png", 16, 16));
	private final Button stop = new Button("Stop", ImageViewHelper.load("stop.png", 16, 16));
	private final Button pause = new Button("Pause", ImageViewHelper.load("pause.png", 16, 16));
	private final Button resume = new Button("Resume", ImageViewHelper.load("resume.png", 16, 16));
	
	private final GridPane charts = new GridPane();
	
	private final GridPane center = new GridPane();
	
	private final ToolBar top = new ToolBar(start, stop, pause, resume);
	
	private final ToolBar bottom = new ToolBar(new Label("© 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, FH Upper Austria"));
	
	private final ScrollPane right = new ScrollPane(charts);
	
	private final BorderPane root = new BorderPane(center, top, right, bottom, null);

	public SimulatorScene(Model model, File folder, double width, double height) {
		super(new Label("Loading ..."), width, height);
		
		// Top

		start.setOnAction(event -> {
			new Alert(AlertType.WARNING, "Not implemented yet!").showAndWait();
		});
		stop.setOnAction(event -> {
			new Alert(AlertType.WARNING, "Not implemented yet!").showAndWait();
		});
		pause.setOnAction(event -> {
			new Alert(AlertType.WARNING, "Not implemented yet!").showAndWait();
		});
		resume.setOnAction(event -> {
			new Alert(AlertType.WARNING, "Not implemented yet!").showAndWait();
		});
		
		start.setDisable(true);
		resume.setDisable(true);
		
		// Right
		
		charts.setPrefWidth(300);
		
		charts.setPadding(new Insets(10));
		
		charts.setHgap(10);
		charts.setVgap(10);
		
		charts.getColumnConstraints().add(GridHelper.createColumnConstraints(false, Priority.ALWAYS));
		
		charts.getRowConstraints().add(GridHelper.createRowConstraints(true, Priority.ALWAYS));
		charts.getRowConstraints().add(GridHelper.createRowConstraints(true, Priority.ALWAYS));
		charts.getRowConstraints().add(GridHelper.createRowConstraints(true, Priority.ALWAYS));
		
		right.setFitToWidth(true);
		right.setFitToHeight(true);
		
		// Center
		
		center.setHgap(10);
		center.setVgap(10);
		
		center.setPadding(new Insets(10));
		
		center.getColumnConstraints().add(GridHelper.createColumnConstraints(true, Priority.ALWAYS));
		
		center.getRowConstraints().add(GridHelper.createRowConstraints(50));
		center.getRowConstraints().add(GridHelper.createRowConstraints(50));
		
		// Root
		
		setRoot(root);
		
		// Thread
		
		thread = new Thread(() -> {
			model.initialize();
			
			Simulator simulator = new Simulator("Run", model, new SmartController(model), 1, 1, folder);
			Statistics statistics = simulator.getStatistics();
			
			ModelViewerFlat viewerFlat = new ModelViewerFlat(model);
			ModelViewerDeep viewerDeep = new ModelViewerDeep(model);
			
			IntersectionCrossingChart intersectionChart = new IntersectionCrossingChart(model, statistics);
			SegmentTraversalChart segmentChart = new SegmentTraversalChart(model, statistics);
			StationChargeChart stationChart = new StationChargeChart(model, statistics);
			VehicleBatteryChart vehicleBatteryChart = new VehicleBatteryChart(model);
			DemandDistanceChart demandChart = new DemandDistanceChart(model, statistics);
			
			Platform.runLater(() -> {
				center.getChildren().clear();
				center.add(viewerFlat, 0, 0);
				center.add(viewerDeep, 0, 1);
				
				charts.add(intersectionChart, 0, 0);
				charts.add(segmentChart, 0, 1);
				charts.add(stationChart, 0, 2);
				charts.add(vehicleBatteryChart, 0, 3);
				charts.add(demandChart, 0, 4);
			});
			
			simulator.setHandleUpdated(() -> {
				Platform.runLater(() -> {
					viewerFlat.update();
					viewerDeep.update();
					
					intersectionChart.update();
					segmentChart.update();
					stationChart.update();
					vehicleBatteryChart.update();
					demandChart.update();
				});
			});
			simulator.loop();
			
			AlertType type;
			String text;
			
			if (simulator.isFinished()) {	
				type = AlertType.INFORMATION;
				text = "Finished!";
			} else if (simulator.isEmpty()) {
				type = AlertType.ERROR;
				text = "Empty!";
			} else if (Double.isInfinite(model.state.get().time)) {
				type = AlertType.ERROR;
				text = "Infinite!";
			} else {
				type = AlertType.ERROR;
				text = "Collision!";
			}
			
			Platform.runLater(() -> {
				start.setDisable(false);
				stop.setDisable(true);
				pause.setDisable(true);
				resume.setDisable(true);
				
				new Alert(type, text).showAndWait();
			});
		});
		thread.start();
	}
	
}
