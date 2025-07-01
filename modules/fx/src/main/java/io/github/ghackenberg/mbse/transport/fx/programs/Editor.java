package io.github.ghackenberg.mbse.transport.fx.programs;

import java.io.File;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.core.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.fx.viewers.DemandViewer;
import io.github.ghackenberg.mbse.transport.fx.viewers.IntersectionViewer;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import io.github.ghackenberg.mbse.transport.fx.viewers.SegmentViewer;
import io.github.ghackenberg.mbse.transport.fx.viewers.StationViewer;
import io.github.ghackenberg.mbse.transport.fx.viewers.VehicleViewer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
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
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Editor extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private Model model = new Model();
	
	private ModelViewer viewer = null;
	
	private final MenuItem open = new MenuItem("Open");
	private final MenuItem save = new MenuItem("Save");
	private final MenuItem saveAs = new MenuItem("Save as");
	private final MenuItem close = new MenuItem("Close");

	private final Menu file = new Menu("File", null, open, save, saveAs, close);
	private final Menu edit = new Menu("Edit");
	private final Menu help = new Menu("Help");
	
	private final MenuBar top = new MenuBar(file, edit, help);
	
	private final ToolBar bottom = new ToolBar(new Label("(c) 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, FH Upper Austria"));
	
	private final GridPane right = new GridPane();
	
	private final BorderPane root = new BorderPane(viewer, top, right, bottom, null);
	
	private final Scene scene = new Scene(root, 640, 480);

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		reloadModelViewer();
		
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
					
					reloadModelViewer();
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
		
		// Stage
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Transport-IDE Editor");
		primaryStage.show();
	}
	
	private void reloadModelViewer() {
		viewer = new ModelViewer(model);
		
		viewer.setOnMouseClicked(mouseEvent -> {
			
			Node node = mouseEvent.getPickResult().getIntersectedNode();
			
			while (node != null) {
				if (node instanceof IntersectionViewer) {
					Intersection intersection = ((IntersectionViewer) node).entity;
					
					TextField name = new TextField(intersection.name.get());
					intersection.name.bind(name.textProperty());
					
					TextField x = new TextField("" + intersection.coordinate.x.get());
					x.setOnAction(event -> {
						intersection.coordinate.x.set(Double.parseDouble(x.getText()));
					});
					x.focusedProperty().addListener(event -> {
						intersection.coordinate.x.set(Double.parseDouble(x.getText()));
					});
					
					TextField y = new TextField("" + intersection.coordinate.y.get());
					y.setOnAction(event -> {
						intersection.coordinate.y.set(Double.parseDouble(y.getText()));
					});
					y.focusedProperty().addListener(event -> {
						intersection.coordinate.y.set(Double.parseDouble(y.getText()));
					});
					
					TextField z = new TextField("" + intersection.coordinate.z.get());
					z.setOnAction(event -> {
						intersection.coordinate.z.set(Double.parseDouble(z.getText()));
					});
					z.focusedProperty().addListener(event -> {
						intersection.coordinate.z.set(Double.parseDouble(z.getText()));
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
					
					break;
				} else if (node instanceof SegmentViewer) {
					right.getChildren().clear();
					
					// TODO
					
					break;
				} else if (node instanceof StationViewer) {
					right.getChildren().clear();
					
					// TODO
					
					break;
				} else if (node instanceof VehicleViewer) {
					right.getChildren().clear();
					
					// TODO
					
					break;
				} else if (node instanceof DemandViewer) {
					right.getChildren().clear();
					
					// TODO
					
					break;
				} else if (node instanceof ModelViewer) {
					right.getChildren().clear();

					try {
						double x = mouseEvent.getSceneX();
						double y = mouseEvent.getSceneY();
						
						Point2D world = ((ModelViewer) node).canvas.getLocalToSceneTransform().createInverse().transform(x, y);
						
						Intersection intersection = new Intersection();
						intersection.name.set("" + (model.intersections.size() + 1));
						intersection.coordinate.x.set(world.getX());
						intersection.coordinate.y.set(world.getY());
						intersection.coordinate.z.set(0);
						
						model.intersections.add(intersection);
					} catch (NonInvertibleTransformException e) {
						e.printStackTrace();
					}
					
					break;
				} else {
					node = node.getParent();	
				}
			}
		});
		
		root.setCenter(viewer);
	}

}
