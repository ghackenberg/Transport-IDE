package io.github.ghackenberg.mbse.transport.fx.scenes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditorScene extends Scene {

	private File folder = new File("runs");
	
	private Model model = new Model();
	
	private ModelViewer modelViewer = null;
	
	private final BooleanProperty segmentPreviewVisible = new SimpleBooleanProperty(false);
	private final DoubleProperty segmentPreviewEndX = new SimpleDoubleProperty();
	private final DoubleProperty segmentPreviewEndY = new SimpleDoubleProperty();
	
	private final Line segmentPreviewLine = new Line();
	private final Circle segmentPreviewHead = new Circle();
	
	private double dragDemandDot;
	
	private Node dragDemandNode;

	private final Button clear = new Button("Clear", ImageViewHelper.load("clear.png", 16, 16));
	private final Button open = new Button("Open", ImageViewHelper.load("open.png", 16, 16));
	private final Button save = new Button("Save", ImageViewHelper.load("save.png", 16, 16));
	private final Button run = new Button("Run", ImageViewHelper.load("run.png", 16, 16));
	
	private final ToolBar top = new ToolBar(clear, open, save, run);
	
	private final ToolBar bottom = new ToolBar(new Label("© 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, FH Upper Austria"));
	
	private EntityViewer<?> selected;
	
	private final GridPane right = new GridPane();
	
	private final BorderPane root = new BorderPane(modelViewer, top, right, bottom, null);
	
	public EditorScene() {
		super(new Label("Loading ..."), 800, 600);
		
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		// Top
		
		clear.setOnAction(event -> {
			folder = new File("runs");
			
			model = new Model();
			
			reload();
		});
		
		open.setOnAction(event -> {			
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(new File("."));
			
			File directory = chooser.showDialog(getWindow());
			
			if (directory != null) {
				File demands = new File(directory, "demands.txt");
				File intersections = new File(directory, "intersections.txt");
				File segments = new File(directory, "segments.txt");
				File stations = new File(directory, "stations.txt");
				File vehicles = new File(directory, "vehicles.txt");
				
				try {
					model = new Parser().parse(intersections, segments, stations, vehicles, demands);
					
					folder = new File(directory, "runs");
					
					if (!folder.exists()) {
						folder.mkdir();
					}
					
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
		
		run.setOnAction(event -> {
			SimulatorScene subScene = new SimulatorScene(model, folder, getWidth(), getHeight());
			
			Stage subStage = new Stage();
			
			subStage.initModality(Modality.APPLICATION_MODAL);
			subStage.initOwner(getWindow());
			subStage.setTitle("Run");
			subStage.setScene(subScene);
			subStage.setX(getWindow().getX() + 20);
			subStage.setY(getWindow().getY() + 20);
			subStage.show();
		});
		
		// Right
		
		right.setPrefWidth(300);
		
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
		
		reload();
		
		// Root
		
		setRoot(root);
	}
	
	private void reload() {
		select(null);
		
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
				double x = event.getSceneX();
				double y = event.getSceneY();
				
				Point2D world = modelViewer.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
				
				if (event.getGestureSource() instanceof IntersectionViewer) {
					IntersectionViewer viewer = (IntersectionViewer) event.getGestureSource();
					if (event.isControlDown()) {
						segmentPreviewEndX.set(world.getX());
						segmentPreviewEndY.set(world.getY());
					} else {
						Map<Station, Double> stationDist = new HashMap<>();
						Map<Vehicle, Double> vehicleDist = new HashMap<>();
						Map<Demand, Double> demandPickDist = new HashMap<>();
						Map<Demand, Double> demandDropDist = new HashMap<>();
						
						for (Station station : model.stations) {
							stationDist.put(station, station.location.distance.get() / station.location.segment.get().length.get());
						}
						for (Vehicle vehicle : model.vehicles) {
							vehicleDist.put(vehicle, vehicle.initialLocation.distance.get() / vehicle.initialLocation.segment.get().length.get());
						}
						for (Demand demand : model.demands) {
							demandPickDist.put(demand, demand.pick.location.distance.get() / demand.pick.location.segment.get().length.get());
							demandDropDist.put(demand, demand.drop.location.distance.get() / demand.drop.location.segment.get().length.get());
						}
							
						viewer.entity.coordinate.x.set(world.getX());
						viewer.entity.coordinate.y.set(world.getY());
						
						for (Station station : model.stations) {
							station.location.distance.set(stationDist.get(station) * station.location.segment.get().length.get());
						}
						for (Vehicle vehicle : model.vehicles) {
							vehicle.initialLocation.distance.set(vehicleDist.get(vehicle) * vehicle.initialLocation.segment.get().length.get());
						}
						for (Demand demand : model.demands) {
							demand.pick.location.distance.set(demandPickDist.get(demand) * demand.pick.location.segment.get().length.get());
							demand.drop.location.distance.set(demandDropDist.get(demand) * demand.drop.location.segment.get().length.get());
						}
					}

					event.consume();
				} else if (event.getGestureSource() instanceof StationViewer) {
					StationViewer viewer = (StationViewer) event.getGestureSource();
					
					double minLen = Double.MAX_VALUE;
					double minDot = 0;
					Segment minSeg = null;
					
					for (Segment segment : model.segments) {
						double sx = segment.start.coordinate.x.get();
						double sy = segment.start.coordinate.y.get();
						
						double tx = segment.tangent.x.get();
						double ty = segment.tangent.y.get();
						
						double dx = world.getX() - sx;
						double dy = world.getY() - sy;
						
						double dot = Math.min(segment.length.get(), Math.max(0, tx * dx + ty * dy));
						
						double lx = sx + dot * tx;
						double ly = sy + dot * ty;
						
						double nx = world.getX() - lx;
						double ny = world.getY() - ly;
						
						double len = Math.sqrt(nx * nx + ny * ny);
						
						if (len < minLen) {
							minLen = len;
							minSeg = segment;
							minDot = dot;
						}
					}
					
					if (minSeg != null) {
						viewer.entity.location.segment.set(minSeg);
						viewer.entity.location.distance.set(minDot);
					}
					
					event.consume();
				} else if (event.getGestureSource() instanceof VehicleViewer) {
					VehicleViewer viewer = (VehicleViewer) event.getGestureSource();
					
					double minLen = Double.MAX_VALUE;
					double minDot = 0;
					Segment minSeg = null;
					
					for (Segment segment : model.segments) {
						double sx = segment.start.coordinate.x.get();
						double sy = segment.start.coordinate.y.get();
						
						double tx = segment.tangent.x.get();
						double ty = segment.tangent.y.get();
						
						double dx = world.getX() - sx;
						double dy = world.getY() - sy;
						
						double dot = Math.min(segment.length.get(), Math.max(0, tx * dx + ty * dy));
						
						double lx = sx + dot * tx;
						double ly = sy + dot * ty;
						
						double nx = world.getX() - lx;
						double ny = world.getY() - ly;
						
						double len = Math.sqrt(nx * nx + ny * ny);
						
						if (len < minLen) {
							minLen = len;
							minSeg = segment;
							minDot = dot;
						}
					}
					
					if (minSeg != null) {
						viewer.entity.initialLocation.segment.set(minSeg);
						viewer.entity.initialLocation.distance.set(minDot);
					}
					
					event.consume();
				} else if (event.getGestureSource() instanceof DemandViewer) {
					DemandViewer viewer = (DemandViewer) event.getGestureSource();
					
					double minLen = Double.MAX_VALUE;
					double minDot = 0;
					Segment minSeg = null;
					
					for (Segment segment : model.segments) {
						double sx = segment.start.coordinate.x.get();
						double sy = segment.start.coordinate.y.get();
						
						double tx = segment.tangent.x.get();
						double ty = segment.tangent.y.get();
						
						double dx = world.getX() - sx;
						double dy = world.getY() - sy;
						
						double dot = Math.min(segment.length.get(), Math.max(0, tx * dx + ty * dy));
						
						double lx = sx + dot * tx;
						double ly = sy + dot * ty;
						
						double nx = world.getX() - lx;
						double ny = world.getY() - ly;
						
						double len = Math.sqrt(nx * nx + ny * ny);
						
						if (len < minLen) {
							minLen = len;
							minSeg = segment;
							minDot = dot;
						}
					}
					
					if (minSeg != null) {
						if (dragDemandNode == viewer.source || dragDemandNode == viewer.sourceText) {
							viewer.entity.pick.location.segment.set(minSeg);
							viewer.entity.pick.location.distance.set(minDot);
						} else {
							viewer.entity.drop.location.segment.set(minSeg);
							viewer.entity.drop.location.distance.set(minDot);
						}
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
						other.coordinate.z.set(0);
						
						model.intersections.add(other);
						
						Segment segment = new Segment(viewer.entity, other);
						
						viewer.entity.outgoing.add(segment);
						other.incoming.add(segment);
						
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
			try {
				if (event.isStillSincePress()) {
					double x = event.getSceneX();
					double y = event.getSceneY();
					
					Point2D world = modelViewer.canvas.getLocalToSceneTransform().createInverse().transform(x, y);

					double sx = viewer.entity.start.coordinate.x.get();
					double sy = viewer.entity.start.coordinate.y.get();
					
					double tx = viewer.entity.tangent.x.get();
					double ty = viewer.entity.tangent.y.get();
					
					double dx = world.getX() - sx;
					double dy = world.getY() - sy;
					
					double dot = tx * dx + ty * dy;
					
					if (event.isControlDown()) {
						Station station = new Station();
						
						station.speed.set(1);
						station.location.segment.set(viewer.entity);
						station.location.distance.set(dot);
						
						model.stations.add(station);
						
						select(modelViewer.stationViewers.get(station));
					} else if (event.isShiftDown()) {
						Vehicle vehicle = new Vehicle();
						
						vehicle.name.set("Vehicle " + (model.vehicles.size() + 1));
						vehicle.length.set(1);
						vehicle.loadCapacity.set(1);
						vehicle.batteryCapacity.set(1000);
						vehicle.initialBatteryLevel.set(1000);
						vehicle.initialSpeed.set(1);
						vehicle.initialLocation.segment.set(viewer.entity);
						vehicle.initialLocation.distance.set(dot);
						
						model.vehicles.add(vehicle);
						
						select(modelViewer.vehicleViewers.get(vehicle));
					} else {
						select(viewer);
					}
					event.consume();
				}
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
		viewer.setOnDragDetected(event -> {
			try {
				double x = event.getSceneX();
				double y = event.getSceneY();
				
				Point2D world = modelViewer.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
	
				double sx = viewer.entity.start.coordinate.x.get();
				double sy = viewer.entity.start.coordinate.y.get();
				
				double tx = viewer.entity.tangent.x.get();
				double ty = viewer.entity.tangent.y.get();
				
				double dx = world.getX() - sx;
				double dy = world.getY() - sy;
				
				dragDemandDot = tx * dx + ty * dy;
				
				viewer.startFullDrag();
				event.consume();
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
		viewer.setOnMouseDragReleased(event -> {
			try {
				if (event.getGestureSource() instanceof SegmentViewer) {
					double x = event.getSceneX();
					double y = event.getSceneY();
					
					Point2D world = modelViewer.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
	
					double sx = viewer.entity.start.coordinate.x.get();
					double sy = viewer.entity.start.coordinate.y.get();
					
					double tx = viewer.entity.tangent.x.get();
					double ty = viewer.entity.tangent.y.get();
					
					double dx = world.getX() - sx;
					double dy = world.getY() - sy;
					
					double dot = tx * dx + ty * dy;
					
					SegmentViewer other = (SegmentViewer) event.getGestureSource();
					
					Demand demand = new Demand();
					
					demand.size.set(1);
					
					demand.pick.location.segment.set(other.entity);
					demand.pick.location.distance.set(dragDemandDot);
					
					demand.drop.location.segment.set(viewer.entity);
					demand.drop.location.distance.set(dot);
					
					model.demands.add(demand);
					
					select(modelViewer.demandViewers.get(demand));
					
					event.consume();
				}
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
	}
	private void attach(StationViewer viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}
	private void attach(VehicleViewer viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}
	private void attach(DemandViewer viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			dragDemandNode = event.getPickResult().getIntersectedNode();
			if (dragDemandNode == viewer.source || dragDemandNode == viewer.sourceText || dragDemandNode == viewer.target || dragDemandNode == viewer.targetText) {
				viewer.startFullDrag();
				event.consume();	
			}
		});
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
		viewer.setOnDragDetected(null);
	}
	private void detach(StationViewer viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}
	private void detach(VehicleViewer viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}
	private void detach(DemandViewer viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
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
			model.delete(viewer.entity);
			select(null);
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
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
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
		
		right.add(delete, 1, 5);
	}
	
	private void show(StationViewer viewer) {
		TextField type = new TextField("Station");
		type.setDisable(true);
		
		TextField speed = new TextField("" + viewer.entity.speed.get());
		speed.setOnAction(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
		speed.focusedProperty().addListener(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
		});
		
		right.add(new Label("Type"), 0, 0);
		right.add(type, 1, 0);
		
		right.add(new Label("Speed"), 0, 1);
		right.add(speed, 1, 1);
		
		right.add(delete, 1, 2);
	}
	
	private void show(VehicleViewer viewer) {
		TextField type = new TextField("Vehicle");
		type.setDisable(true);
		
		TextField name = new TextField(viewer.entity.name.get());
		viewer.entity.name.bind(name.textProperty());
		
		TextField length = new TextField("" + viewer.entity.length.get());
		length.setOnAction(event -> {
			viewer.entity.length.set(Double.parseDouble(length.getText()));
		});
		length.focusedProperty().addListener(event -> {
			viewer.entity.length.set(Double.parseDouble(length.getText()));
		});
		
		TextField batteryCapacity = new TextField("" + viewer.entity.batteryCapacity.get());
		batteryCapacity.setOnAction(event -> {
			viewer.entity.batteryCapacity.set(Double.parseDouble(batteryCapacity.getText()));
		});
		batteryCapacity.focusedProperty().addListener(event -> {
			viewer.entity.batteryCapacity.set(Double.parseDouble(batteryCapacity.getText()));
		});
		
		TextField loadCapacity = new TextField("" + viewer.entity.loadCapacity.get());
		loadCapacity.setOnAction(event -> {
			viewer.entity.loadCapacity.set(Double.parseDouble(loadCapacity.getText()));
		});
		loadCapacity.focusedProperty().addListener(event -> {
			viewer.entity.loadCapacity.set(Double.parseDouble(loadCapacity.getText()));
		});
		
		TextField initialBatteryLevel = new TextField("" + viewer.entity.initialBatteryLevel.get());
		initialBatteryLevel.setOnAction(event -> {
			viewer.entity.initialBatteryLevel.set(Double.parseDouble(initialBatteryLevel.getText()));
		});
		initialBatteryLevel.focusedProperty().addListener(event -> {
			viewer.entity.initialBatteryLevel.set(Double.parseDouble(initialBatteryLevel.getText()));
		});
		
		TextField initialSpeed = new TextField("" + viewer.entity.initialSpeed.get());
		initialSpeed.setOnAction(event -> {
			viewer.entity.initialSpeed.set(Double.parseDouble(initialSpeed.getText()));
		});
		initialSpeed.focusedProperty().addListener(event -> {
			viewer.entity.initialSpeed.set(Double.parseDouble(initialSpeed.getText()));
		});
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
		});
		
		right.add(new Label("Type"), 0, 0);
		right.add(type, 1, 0);
		
		right.add(new Label("Name"), 0, 1);
		right.add(name, 1, 1);
		
		right.add(new Label("Length"), 0, 2);
		right.add(length, 1, 2);
		
		right.add(new Label("Battery capacity"), 0, 3);
		right.add(batteryCapacity, 1, 3);
		
		right.add(new Label("Load capacity"), 0, 4);
		right.add(loadCapacity, 1, 4);
		
		right.add(new Label("Initial battery level"), 0, 5);
		right.add(initialBatteryLevel, 1, 5);
		
		right.add(new Label("Initial speed"), 0, 6);
		right.add(initialSpeed, 1, 6);
		
		right.add(delete, 1, 7);
	}
	
	private void show(DemandViewer viewer) {
		TextField type = new TextField("Demand");
		type.setDisable(true);
		
		TextField size = new TextField("" + viewer.entity.size.get());
		size.setOnAction(event -> {
			viewer.entity.size.set(Double.parseDouble(size.getText()));
		});
		size.focusedProperty().addListener(event -> {
			viewer.entity.size.set(Double.parseDouble(size.getText()));
		});
		
		TextField pick = new TextField("" + viewer.entity.pick.time.get());
		pick.setOnAction(event -> {
			viewer.entity.pick.time.set(Double.parseDouble(pick.getText()));
		});
		pick.focusedProperty().addListener(event -> {
			viewer.entity.pick.time.set(Double.parseDouble(pick.getText()));
		});
		
		TextField drop = new TextField("" + viewer.entity.drop.time.get());
		drop.setOnAction(event -> {
			viewer.entity.drop.time.set(Double.parseDouble(drop.getText()));
		});
		drop.focusedProperty().addListener(event -> {
			viewer.entity.drop.time.set(Double.parseDouble(drop.getText()));
		});
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
		});
		
		right.add(new Label("Type"), 0, 0);
		right.add(type, 1, 0);
		
		right.add(new Label("Size"), 0, 1);
		right.add(size, 1, 1);
		
		right.add(new Label("Pick"), 0, 2);
		right.add(pick, 1, 2);
		
		right.add(new Label("Drop"), 0, 3);
		right.add(drop, 1, 3);
		
		right.add(delete, 1, 4);
	}
	
}
