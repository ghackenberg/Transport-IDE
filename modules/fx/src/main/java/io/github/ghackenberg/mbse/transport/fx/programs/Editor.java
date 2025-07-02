package io.github.ghackenberg.mbse.transport.fx.programs;

import java.io.File;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.core.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.fx.helpers.GenericListChangeListener;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Editor extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private Model model = new Model();
	
	private ModelViewer modelViewer = null;
	
	private final BooleanProperty segmentPreviewVisible = new SimpleBooleanProperty(false);
	private final DoubleProperty segmentPreviewEndX = new SimpleDoubleProperty();
	private final DoubleProperty segmentPreviewEndY = new SimpleDoubleProperty();
	
	private final Line segmentPreviewLine = new Line();
	private final Circle segmentPreviewHead = new Circle();
	
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
	
	private final GridPane right = new GridPane();
	
	private final BorderPane root = new BorderPane(modelViewer, top, right, bottom, null);
	
	private final Scene scene = new Scene(root, 640, 480);

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		reload();
		
		// Top
		
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
		
		// Center
		
		segmentPreviewLine.visibleProperty().bind(segmentPreviewVisible);
		segmentPreviewLine.endXProperty().bind(segmentPreviewEndX);
		segmentPreviewLine.endYProperty().bind(segmentPreviewEndY);
	
		segmentPreviewLine.setStroke(Color.LIGHTGRAY);
		segmentPreviewLine.setStrokeWidth(1);
		segmentPreviewLine.setStrokeDashOffset(1);
		segmentPreviewLine.setStrokeLineCap(StrokeLineCap.BUTT);
		
		segmentPreviewHead.visibleProperty().bind(segmentPreviewVisible);
		segmentPreviewHead.centerXProperty().bind(segmentPreviewEndX);
		segmentPreviewHead.centerYProperty().bind(segmentPreviewEndY);
		
		segmentPreviewHead.setRadius(0.5);
		segmentPreviewHead.setFill(Color.GRAY);
		
		// Stage
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Transport-IDE");
		primaryStage.show();
	}
	
	private void reload() {
		modelViewer = new ModelViewer(model);
		
		modelViewer.segmentLayer.getChildren().add(segmentPreviewLine);
		modelViewer.segmentLayer.getChildren().add(segmentPreviewHead);
		
		setup(modelViewer.intersections, this::detach, this::attach);
		setup(modelViewer.segments, this::detach, this::attach);
		setup(modelViewer.stations, this::detach, this::attach);
		setup(modelViewer.vehicles, this::detach, this::attach);
		setup(modelViewer.demands, this::detach, this::attach);
		
		modelViewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				try {
					double x = event.getSceneX();
					double y = event.getSceneY();
					
					Point2D world = modelViewer.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
					
					Intersection intersection = new Intersection();
					intersection.name.set("Intersection " + (model.intersections.size() + 1));
					intersection.coordinate.x.set(world.getX());
					intersection.coordinate.y.set(world.getY());
					intersection.coordinate.z.set(0);
					
					model.intersections.add(intersection);
					
					select(modelViewer.intersectionViewers.get(intersection));
					
					event.consume();
				} catch (NonInvertibleTransformException e) {
					e.printStackTrace();
				}
			}
		});
		modelViewer.setOnMouseDragEntered(event -> {
			if (event.getGestureSource() instanceof IntersectionViewer) {
				if (event.isControlDown()) {
					segmentPreviewVisible.set(true);
					event.consume();
				}
			}
		});
		modelViewer.setOnMouseDragOver(event -> {
			try {
				if (event.getGestureSource() instanceof IntersectionViewer) {
					IntersectionViewer viewer = (IntersectionViewer) event.getGestureSource();
					
					double x = event.getSceneX();
					double y = event.getSceneY();
					
					Point2D world = modelViewer.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
	
					if (event.isControlDown()) {
						segmentPreviewEndX.set(world.getX());
						segmentPreviewEndY.set(world.getY());
					} else {
						viewer.entity.coordinate.x.set(world.getX());
						viewer.entity.coordinate.y.set(world.getY());
					}

					event.consume();
				}
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
		modelViewer.setOnMouseDragExited(event -> {
			segmentPreviewVisible.set(false);
			event.consume();
		});
		modelViewer.setOnMouseDragReleased(event -> {
			try {
				if (event.getGestureSource() instanceof IntersectionViewer) {
					if (event.isControlDown()) {
						IntersectionViewer viewer = (IntersectionViewer) event.getGestureSource();
						
						double x = event.getSceneX();
						double y = event.getSceneY();
						
						Point2D world = modelViewer.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
		
						Intersection other = new Intersection();
						
						other.name.set("Intersection " + (model.intersections.size() + 1));
						other.coordinate.x.set(world.getX());
						other.coordinate.y.set(world.getY());
						other.coordinate.z.set(world.getY());
						
						model.intersections.add(other);
						
						Segment segment = new Segment(viewer.entity, other);
						
						model.segments.add(segment);
						
						select(modelViewer.segmentViewers.get(segment));
		
						event.consume();
					}
				}
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
		
		root.setCenter(modelViewer);
	}
	
	private <T> void setup(ObservableList<T> list, GenericListChangeListener.Handler<T> remove, GenericListChangeListener.Handler<T> add) {
		list.addListener(new GenericListChangeListener<T>(remove, add));
		for (T item : list) {
			add.handle(item);
		}
	}
	
	private void attach(IntersectionViewer viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			select(viewer);
			viewer.startFullDrag();
			if (event.isControlDown()) {
				segmentPreviewVisible.set(true);
				
				segmentPreviewLine.startXProperty().bind(viewer.entity.coordinate.x);
				segmentPreviewLine.startYProperty().bind(viewer.entity.coordinate.y);
				
				segmentPreviewEndX.set(viewer.entity.coordinate.x.get());
				segmentPreviewEndY.set(viewer.entity.coordinate.y.get());
			}
			event.consume();
		});
		viewer.setOnMouseDragEntered(event -> {
			if (event.getGestureSource() instanceof IntersectionViewer && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					viewer.selected.set(true);
					
					segmentPreviewEndX.set(viewer.entity.coordinate.x.get());
					segmentPreviewEndY.set(viewer.entity.coordinate.y.get());
					
					event.consume();
				}
			}
		});
		viewer.setOnMouseDragOver(event -> {
			if (event.getGestureSource() instanceof IntersectionViewer && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					event.consume();
				}
			}
		});
		viewer.setOnMouseDragExited(event -> {
			if (event.getGestureSource() instanceof IntersectionViewer && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					viewer.selected.set(false);
					event.consume();
				}
			}
		});
		viewer.setOnMouseDragReleased(event -> {
			if (event.getGestureSource() instanceof IntersectionViewer && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					segmentPreviewVisible.set(false);
					
					IntersectionViewer other = (IntersectionViewer) event.getGestureSource();
					
					Segment segment = new Segment(other.entity, viewer.entity);
					
					other.entity.outgoing.add(segment);
					viewer.entity.incoming.add(segment);
					
					model.segments.add(segment);
					
					select(modelViewer.segmentViewers.get(segment));
					
					event.consume();
				}
			}
		});
	}
	private void attach(SegmentViewer viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
	}
	private void attach(StationViewer viewer) {
		// TODO
	}
	private void attach(VehicleViewer viewer) {
		// TODO
	}
	private void attach(DemandViewer viewer) {
		// TODO
	}
	
	private void detach(IntersectionViewer viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
		viewer.setOnMouseDragEntered(null);
		viewer.setOnMouseDragExited(null);
		viewer.setOnMouseDragReleased(null);
	}
	private void detach(SegmentViewer viewer) {
		viewer.setOnMouseClicked(null);
	}
	private void detach(StationViewer viewer) {
		// TODO
	}
	private void detach(VehicleViewer viewer) {
		// TODO
	}
	private void detach(DemandViewer viewer) {
		// TODO
	}
	
	private void select(EntityViewer<?> viewer) {
		if (selected != null) {
			selected.selected.set(false);
		}
		
		right.getChildren().clear();
		
		if (viewer != null) {
			viewer.selected.set(true);
			
			selected = viewer;
			
			if (viewer instanceof IntersectionViewer) {
				show((IntersectionViewer) viewer);
			} else if (viewer instanceof SegmentViewer) {
				show((SegmentViewer) viewer);
			} else if (viewer instanceof StationViewer) {
				show((StationViewer) viewer);
			} else if (viewer instanceof VehicleViewer) {
				show((VehicleViewer) viewer);
			} else if (viewer instanceof DemandViewer) {
				show((DemandViewer) viewer);
			}
		}
	}
	
	private void show(IntersectionViewer viewer) {
		TextField type = new TextField("Intersection");
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
	
	private void show(SegmentViewer viewer) {
		TextField type = new TextField("Segment");
		type.setDisable(true);
		
		TextField start = new TextField();
		start.textProperty().bind(viewer.entity.start.name);
		start.setDisable(true);
		
		TextField end = new TextField();
		end.textProperty().bind(viewer.entity.end.name);
		end.setDisable(true);
		
		TextField lanes = new TextField("" + viewer.entity.lanes.get());
		lanes.setOnAction(event -> {
			viewer.entity.lanes.set(Double.parseDouble(lanes.getText()));
		});
		lanes.focusedProperty().addListener(event -> {
			viewer.entity.lanes.set(Double.parseDouble(lanes.getText()));
		});
		
		TextField speed = new TextField("" + viewer.entity.speed.get());
		speed.setOnAction(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
		speed.focusedProperty().addListener(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
		
		right.add(new Label("Type"), 0, 0);
		right.add(type, 1, 0);
		
		right.add(new Label("Start"), 0, 1);
		right.add(start, 1, 1);
		
		right.add(new Label("End"), 0, 2);
		right.add(end, 1, 2);
		
		right.add(new Label("Lanes"), 0, 3);
		right.add(lanes, 1, 3);
		
		right.add(new Label("Speed"), 0, 4);
		right.add(speed, 1, 4);
	}
	
	private void show(StationViewer viewer) {
		// TODO
	}
	
	private void show(VehicleViewer viewer) {
		// TODO
	}
	
	private void show(DemandViewer viewer) {
		// TODO
	}

}
