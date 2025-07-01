package io.github.ghackenberg.mbse.transport.fx.programs;

import java.io.File;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.core.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.fx.helpers.GridHelper;
import io.github.ghackenberg.mbse.transport.fx.helpers.ImageViewHelper;
import io.github.ghackenberg.mbse.transport.fx.viewers.DemandViewer;
import io.github.ghackenberg.mbse.transport.fx.viewers.EntityViewer;
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
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Editor extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private Model model = new Model();
	
	private ModelViewer viewer = null;
	
	private final MenuItem open = new MenuItem("Open", ImageViewHelper.load("open.png", 16, 16));
	private final MenuItem save = new MenuItem("Save", ImageViewHelper.load("save.png", 16, 16));
	private final MenuItem saveAs = new MenuItem("Save as ...", ImageViewHelper.load("save.png", 16, 16));
	private final MenuItem close = new MenuItem("Close", ImageViewHelper.load("close.png", 16, 16));

	private final Menu file = new Menu("File", null, open, save, saveAs, close);
	private final Menu edit = new Menu("Edit");
	private final Menu help = new Menu("Help");
	
	private final MenuBar top = new MenuBar(file, edit, help);
	
	private final ToolBar bottom = new ToolBar(new Label("© 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, FH Upper Austria"));
	
	private EntityViewer<?> selected;
	
	private final ListView<IntersectionViewer> intersectionList = new ListView<>();
	private final ListView<SegmentViewer> segmentList = new ListView<>();
	private final ListView<StationViewer> stationList = new ListView<>();
	private final ListView<VehicleViewer> vehicleList = new ListView<>();
	private final ListView<DemandViewer> demandList = new ListView<>();
	
	private final TitledPane intersectionPane = new TitledPane("Intersections", intersectionList);
	private final TitledPane segmentPane = new TitledPane("Segments", segmentList);
	private final TitledPane stationPane = new TitledPane("Stations", stationList);
	private final TitledPane vehiclePane = new TitledPane("Vehicles", vehicleList);
	private final TitledPane demandPane = new TitledPane("Demands", demandList);
	
	private final Accordion left = new Accordion(intersectionPane, segmentPane, stationPane, vehiclePane, demandPane);
	
	private final GridPane right = new GridPane();
	
	private final BorderPane root = new BorderPane(viewer, top, right, bottom, left);
	
	private final Scene scene = new Scene(root, 640, 480);

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		reload();
		
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
					
					reload();
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
		
		right.setPrefWidth(220);
		
		right.setPadding(new Insets(10));
		
		right.setHgap(10);
		right.setVgap(10);
		
		right.getColumnConstraints().add(GridHelper.createColumnConstraints(false, Priority.NEVER));
		right.getColumnConstraints().add(GridHelper.createColumnConstraints(true, Priority.ALWAYS));
		
		// Left
		
		intersectionList.setCellFactory(event -> {
			return new ListCell<>() {
				@Override
				protected void updateItem(IntersectionViewer viewer, boolean empty) {
					super.updateItem(viewer, empty);
					if (viewer != null) {
						textProperty().bind(viewer.entity.name);
						setGraphic(ImageViewHelper.load("intersection.png", 16, 16));
					} else {
						textProperty().unbind();
						setText(null);
						setGraphic(null);
					}
				}
			};
		});
		
		intersectionList.getSelectionModel().selectedItemProperty().addListener((list, oldVal, newVal) -> {
			select((EntityViewer<?>) newVal);
		});
		segmentList.getSelectionModel().selectedItemProperty().addListener((list, oldVal, newVal) -> {
			select((EntityViewer<?>) newVal);
		});
		stationList.getSelectionModel().selectedItemProperty().addListener((list, oldVal, newVal) -> {
			select((EntityViewer<?>) newVal);
		});
		vehicleList.getSelectionModel().selectedItemProperty().addListener((list, oldVal, newVal) -> {
			select((EntityViewer<?>) newVal);
		});
		demandList.getSelectionModel().selectedItemProperty().addListener((list, oldVal, newVal) -> {
			select((EntityViewer<?>) newVal);
		});
		
		left.setExpandedPane(intersectionPane);
		
		// Stage
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Transport-IDE");
		primaryStage.show();
	}
	
	private void reload() {
		viewer = new ModelViewer(model);
		viewer.setOnMouseClicked(mouseEvent -> {
			
			Node node = mouseEvent.getPickResult().getIntersectedNode();
			
			while (node != null) {
				if (node instanceof EntityViewer) {
					select((EntityViewer<?>) node);
					break;
				} else if (node instanceof ModelViewer) {
					try {
						double x = mouseEvent.getSceneX();
						double y = mouseEvent.getSceneY();
						
						Point2D world = ((ModelViewer) node).canvas.getLocalToSceneTransform().createInverse().transform(x, y);
						
						Intersection intersection = new Intersection();
						intersection.name.set("Intersection " + (model.intersections.size() + 1));
						intersection.coordinate.x.set(world.getX());
						intersection.coordinate.y.set(world.getY());
						intersection.coordinate.z.set(0);
						
						model.intersections.add(intersection);
						
						select((EntityViewer<?>) viewer.intersectionViewers.get(intersection));
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
		
		intersectionList.setItems(viewer.intersections);
		segmentList.setItems(viewer.segments);
		stationList.setItems(viewer.stations);
		vehicleList.setItems(viewer.vehicles);
		demandList.setItems(viewer.demands);
	}
	
	private void select(EntityViewer<?> viewer) {
		if (selected != null) {
			selected.selected.set(false);
		}
		
		viewer.selected.set(true);
		
		selected = viewer;
		
		if (viewer instanceof IntersectionViewer) {
			select((IntersectionViewer) viewer);
		} else if (viewer instanceof SegmentViewer) {
			select((SegmentViewer) viewer);
		} else if (viewer instanceof StationViewer) {
			select((StationViewer) viewer);
		} else if (viewer instanceof VehicleViewer) {
			select((VehicleViewer) viewer);
		} else if (viewer instanceof DemandViewer) {
			select((DemandViewer) viewer);
		}
	}
	
	private void select(IntersectionViewer viewer) {
		intersectionList.getSelectionModel().select(viewer);
		
		TextField type = new TextField("Intersecrtion");
		type.setDisable(true);
		
		TextField name = new TextField(viewer.entity.name.get());
		viewer.entity.name.bind(name.textProperty());
		
		TextField x = new TextField("" + viewer.entity.coordinate.x.get());
		x.setOnAction(event -> {
			viewer.entity.coordinate.x.set(Double.parseDouble(x.getText()));
		});
		x.focusedProperty().addListener(event -> {
			viewer.entity.coordinate.x.set(Double.parseDouble(x.getText()));
		});
		
		TextField y = new TextField("" + viewer.entity.coordinate.y.get());
		y.setOnAction(event -> {
			viewer.entity.coordinate.y.set(Double.parseDouble(y.getText()));
		});
		y.focusedProperty().addListener(event -> {
			viewer.entity.coordinate.y.set(Double.parseDouble(y.getText()));
		});
		
		TextField z = new TextField("" + viewer.entity.coordinate.z.get());
		z.setOnAction(event -> {
			viewer.entity.coordinate.z.set(Double.parseDouble(z.getText()));
		});
		z.focusedProperty().addListener(event -> {
			viewer.entity.coordinate.z.set(Double.parseDouble(z.getText()));
		});
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.intersections.remove(viewer.entity);
		});
		
		right.getChildren().clear();
		
		right.add(new Label("Type"), 0, 0);
		right.add(type, 1, 0);
		
		right.add(new Label("Name"), 0, 1);
		right.add(name, 1, 1);
		
		right.add(new Label("X"), 0, 2);
		right.add(x, 1, 2);
		
		right.add(new Label("Y"), 0, 3);
		right.add(y, 1, 3);
		
		right.add(new Label("Z"), 0, 4);
		right.add(z, 1, 4);
		
		right.add(delete, 1, 5);
	}
	
	private void select(SegmentViewer viewer) {
		right.getChildren().clear();
		// TODO
	}
	
	private void select(StationViewer viewer) {
		right.getChildren().clear();
		// TODO
	}
	
	private void select(VehicleViewer viewer) {
		right.getChildren().clear();
		// TODO
	}
	
	private void select(DemandViewer viewer) {
		right.getChildren().clear();
		// TODO
	}

}
