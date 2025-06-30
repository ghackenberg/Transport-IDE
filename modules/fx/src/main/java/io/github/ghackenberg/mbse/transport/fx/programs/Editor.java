package io.github.ghackenberg.mbse.transport.fx.programs;

import java.io.File;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.core.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Editor extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private Model model;
	
	private ModelViewer viewer;
	
	private GridPane grid;
	
	private BorderPane root;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Menü
		
		MenuItem open = new MenuItem("Open");
		open.setOnAction(menuEvent -> {			
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(new File("."));
			
			File directory = chooser.showDialog(primaryStage);
			
			if (directory != null) {
				File demands = new File(directory, "demands.txt");
				File intersections = new File(directory, "intersections.txt");
				File segments = new File(directory, "segments.txt");
				File stations = new File(directory, "stations.txt");
				File vehicles = new File(directory, "vehicles.txt");
				
				try {
					model = new Parser().parse(intersections, segments, stations, vehicles, demands);
					
					viewer = new ModelViewer(model);
					viewer.setOnIntersectionSelected(entityEvent -> {
						TextField name = new TextField(entityEvent.getEntity().name.get());
						entityEvent.getEntity().name.bind(name.textProperty());
						
						TextField x = new TextField("" + entityEvent.getEntity().coordinate.x.get());
						x.setOnAction(event -> {
							entityEvent.getEntity().coordinate.x.set(Double.parseDouble(x.getText()));
						});
						x.focusedProperty().addListener(event -> {
							entityEvent.getEntity().coordinate.x.set(Double.parseDouble(x.getText()));
						});
						
						TextField y = new TextField("" + entityEvent.getEntity().coordinate.y.get());
						y.setOnAction(event -> {
							entityEvent.getEntity().coordinate.y.set(Double.parseDouble(y.getText()));
						});
						y.focusedProperty().addListener(event -> {
							entityEvent.getEntity().coordinate.y.set(Double.parseDouble(y.getText()));
						});
						
						TextField z = new TextField("" + entityEvent.getEntity().coordinate.z.get());
						z.setOnAction(event -> {
							entityEvent.getEntity().coordinate.z.set(Double.parseDouble(z.getText()));
						});
						z.focusedProperty().addListener(event -> {
							entityEvent.getEntity().coordinate.z.set(Double.parseDouble(z.getText()));
						});
						
						grid.getChildren().clear();
						
						grid.add(new Label("Name"), 0, 0);
						grid.add(name, 1, 0);
						
						grid.add(new Label("X"), 0, 1);
						grid.add(x, 1, 1);
						
						grid.add(new Label("Y"), 0, 2);
						grid.add(y, 1, 2);
						
						grid.add(new Label("Z"), 0, 3);
						grid.add(z, 1, 3);
					});
					viewer.setOnSegmentSelected(e -> {
						// TODO
					});
					viewer.setOnStationSelected(e -> {
						// TODO
					});
					viewer.setOnVehicleSelected(e -> {
						// TODO
					});
					viewer.setOnDemandSelected(e -> {
						// TODO
					});
					
					root.setCenter(viewer);
				} catch (MissingException e) {
					e.printStackTrace();
				} catch (DirectoryException e) {
					e.printStackTrace();
				}	
			}
		});
		
		MenuItem save = new MenuItem("Save");
		save.setOnAction(event -> {
			new Alert(AlertType.ERROR, "Not implemented yet!").showAndWait();
		});
		
		MenuItem saveAs = new MenuItem("Save as");
		
		MenuItem close = new MenuItem("Close");
		
		Menu file = new Menu("File");
		file.getItems().add(open);
		file.getItems().add(save);
		file.getItems().add(saveAs);
		file.getItems().add(close);
		
		Menu edit = new Menu("Edit");
		
		Menu help = new Menu("Help");
		
		MenuBar menu = new MenuBar();
		menu.getMenus().add(file);
		menu.getMenus().add(edit);
		menu.getMenus().add(help);
		
		// Rechts
		
		grid = new GridPane();
		
		grid.setPadding(new Insets(10));
		
		grid.setHgap(10);
		grid.setVgap(10);
		
		// Unten
		
		ToolBar tool = new ToolBar(new Label("FH Wels"));
		
		// Haupt
		
		root = new BorderPane();
		root.setTop(menu);
		root.setRight(grid);
		root.setBottom(tool);
		
		Scene scene = new Scene(root, 640, 480);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Transport-IDE Editor");
		primaryStage.show();
	}

}
