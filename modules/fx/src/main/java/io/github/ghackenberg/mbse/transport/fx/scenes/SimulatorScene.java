package io.github.ghackenberg.mbse.transport.fx.scenes;

import java.io.File;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.controllers.SmartController;
import io.github.ghackenberg.mbse.transport.fx.charts.BatteryChart;
import io.github.ghackenberg.mbse.transport.fx.helpers.GridHelper;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class SimulatorScene extends Scene {
	
	private final Thread thread;
	
	private final Button start = new Button("Start");
	private final Button stop = new Button("Stop");
	private final Button pause = new Button("Pause");
	private final Button resume = new Button("Resume");
	
	private final ToolBar top = new ToolBar(start, stop, pause, resume);
	
	private final ToolBar bottom = new ToolBar(new Label("© 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, FH Upper Austria"));
	
	private final GridPane right = new GridPane();
	
	private final BorderPane root = new BorderPane(null, top, right, bottom, null);

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
		
		right.setPrefWidth(300);
		
		right.setPadding(new Insets(10));
		
		right.setHgap(10);
		right.setVgap(10);
		
		right.getColumnConstraints().add(GridHelper.createColumnConstraints(false, Priority.NEVER));
		right.getColumnConstraints().add(GridHelper.createColumnConstraints(true, Priority.ALWAYS));
		
		// Root
		
		setRoot(root);
		
		// Thread
		
		thread = new Thread(() -> {
			model.initialize();
			
			ModelViewer viewer = new ModelViewer(model);
			BatteryChart chart = new BatteryChart(model);
			
			Platform.runLater(() -> {
				root.setCenter(viewer);
				right.add(chart, 0, 0);
			});
			
			Simulator simulator = new Simulator("Run", model, new SmartController(model), 1, 1, folder);
			simulator.setHandleUpdated(() -> {
				Platform.runLater(() -> {
					viewer.update();
					chart.update();
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
