package io.github.ghackenberg.mbse.transport.fx.programs;

import java.io.File;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.core.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Editor extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private Model model;
	
	private ModelViewer viewer;
	
	private final MenuItem open = new MenuItem("Open");
	private final MenuItem save = new MenuItem("Save");
	private final MenuItem saveAs = new MenuItem("Save as");
	private final MenuItem close = new MenuItem("Close");

	private final Menu file = new Menu("File", null, open, save, saveAs, close);
	private final Menu edit = new Menu("Edit");
	private final Menu help = new Menu("Help");
	
	private final MenuBar top = new MenuBar(file, edit, help);
	
	private final ToolBar bottom = new ToolBar();
	
	private final GridPane right = new GridPane();

	private final ImageView info = new ImageView("info.png");

	private final VBox welcome = new VBox(info, new Label("Open model to start editing!"));
	
	private final BorderPane root = new BorderPane(welcome, top, right, bottom, null);
	
	private final Scene scene = new Scene(root, 640, 480);

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Menü
		
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
						
						right.getChildren().clear();
						
						right.add(new Label("Name"), 0, 0);
						right.add(name, 1, 0);
						
						right.add(new Label("X"), 0, 1);
						right.add(x, 1, 1);
						
						right.add(new Label("Y"), 0, 2);
						right.add(y, 1, 2);
						
						right.add(new Label("Z"), 0, 3);
						right.add(z, 1, 3);
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
		
		save.setOnAction(event -> {
			new Alert(AlertType.ERROR, "Not implemented yet!").showAndWait();
		});
		
		// Right
		
		right.setPadding(new Insets(10));
		
		right.setHgap(10);
		right.setVgap(10);
		
		// Welcome
		
		info.setFitWidth(64);
		info.setFitHeight(64);
		
		welcome.setSpacing(10);
		welcome.setAlignment(Pos.CENTER);
		
		// Stage
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Transport-IDE Editor");
		primaryStage.show();
	}

}
