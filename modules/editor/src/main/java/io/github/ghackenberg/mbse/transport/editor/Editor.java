package example.editor;

import java.io.File;

import example.model.Intersection;
import example.model.Model;
import example.model.Segment;
import example.model.Station;
import example.model.Vehicle;
import example.parser.Parser;
import example.parser.exceptions.DirectoryException;
import example.parser.exceptions.MissingException;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Editor extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private Model model;
	
	private ListView<String> list;
	
	private TableView<String> table;
	
	private Pane pane;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Menü
		
		MenuItem open = new MenuItem("Open");
		open.setOnAction(event -> {			
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
					
					paintModel();
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
		
		// Links
		
		list = new ListView<>();
		list.getItems().add("Intersection");
		list.getItems().add("Segment");
		list.getItems().add("Station");
		list.getItems().add("Demand");
		list.getItems().add("Vehicle");
		
		// Rechts
		
		TableColumn<String, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(row -> new SimpleStringProperty(row.getValue()));
		
		TableColumn<String, String> valueCol = new TableColumn<>("Value");
		valueCol.setCellValueFactory(row -> new SimpleStringProperty("Dummy"));
		
		table = new TableView<>();
		table.getColumns().add(nameCol);
		table.getColumns().add(valueCol);
		table.getItems().add("X");
		table.getItems().add("Y");
		table.getItems().add("Z");
		
		// Mitte
		
		pane = new Pane();
		
		// Unten
		
		ToolBar tool = new ToolBar(new Label("FH Wels"));
		
		// Haupt
		
		BorderPane root = new BorderPane();
		root.setTop(menu);
		root.setLeft(list);
		root.setRight(table);
		root.setCenter(pane);
		root.setBottom(tool);
		
		Scene scene = new Scene(root, 640, 480);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Transport-IDE Editor");
		primaryStage.show();
	}
	
	private void paintModel() {
		pane.getChildren().clear();
		
		for (Segment segment : model.segments) {
			paintSegment(segment);
		}
		for (Intersection intersection : model.intersections) {
			paintIntersection(intersection);
		}
		for (Station station : model.stations) {
			paintStation(station);
		}
		for (Vehicle vehicle : model.vehicles) {
			paintVehicle(vehicle);
		}
	}
	
	private void paintSegment(Segment segment)  {
		// TODO implement
		
		Circle circle = new Circle();
		
		circle.setFill(Color.GREEN);
		circle.setStroke(Color.RED);
		
		circle.centerXProperty().bind(pane.widthProperty().divide(2));
		circle.centerYProperty().bind(pane.heightProperty().divide(2));
		circle.radiusProperty().bind(pane.widthProperty().divide(10));
		circle.strokeWidthProperty().bind(pane.widthProperty().divide(20));
		
		pane.getChildren().add(circle);
	}
	
	private void paintIntersection(Intersection intersection) {
		// TODO implement
		
		Circle circle = new Circle();
		
		circle.setFill(Color.GREEN);
		circle.setStroke(Color.RED);
		
		circle.centerXProperty().bind(pane.widthProperty().divide(2));
		circle.centerYProperty().bind(pane.heightProperty().divide(2));
		circle.radiusProperty().bind(pane.widthProperty().divide(10));
		circle.strokeWidthProperty().bind(pane.widthProperty().divide(20));
		
		pane.getChildren().add(circle);
	}
	
	private void paintStation(Station station) {
		// TODO implement
		
		Circle circle = new Circle();
		
		circle.setFill(Color.GREEN);
		circle.setStroke(Color.RED);
		
		circle.centerXProperty().bind(pane.widthProperty().divide(2));
		circle.centerYProperty().bind(pane.heightProperty().divide(2));
		circle.radiusProperty().bind(pane.widthProperty().divide(10));
		circle.strokeWidthProperty().bind(pane.widthProperty().divide(20));
		
		pane.getChildren().add(circle);
	}
	
	private void paintVehicle(Vehicle vehicle) {
		// TODO implement
		
		Circle circle = new Circle();
		
		circle.setFill(Color.GREEN);
		circle.setStroke(Color.RED);
		
		circle.centerXProperty().bind(pane.widthProperty().divide(2));
		circle.centerYProperty().bind(pane.heightProperty().divide(2));
		circle.radiusProperty().bind(pane.widthProperty().divide(10));
		circle.strokeWidthProperty().bind(pane.widthProperty().divide(20));
		
		pane.getChildren().add(circle);
	}

}
