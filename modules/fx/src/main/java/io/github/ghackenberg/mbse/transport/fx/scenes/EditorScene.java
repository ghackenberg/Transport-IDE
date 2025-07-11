package io.github.ghackenberg.mbse.transport.fx.scenes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.Serializer;
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
import io.github.ghackenberg.mbse.transport.fx.helpers.PersistentMemoryHelper;
import io.github.ghackenberg.mbse.transport.fx.viewers.EntityViewer;
import io.github.ghackenberg.mbse.transport.fx.viewers.deep.ModelViewerDeep;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.DemandViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.IntersectionViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.ModelViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.SegmentViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.StationViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.VehicleViewerFlat;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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

/**
 * Editor scene providing a toolbar on top, a property grid on the right, and the 2D and 3D viewers in the center.
 */
public class EditorScene extends Scene {

	private File modelRunsFolder = new File("runs");
	
	private Model model = new Model();
	
	private ModelViewerFlat modelViewerFlat;
	private ModelViewerDeep modelViewerDeep;
	
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
	private final Button help = new Button("Help", ImageViewHelper.load("help.png", 16, 16));
	
	private final ToggleButton deep = new ToggleButton("3D", ImageViewHelper.load("deep.png", 16, 16));
	
	private final ToolBar top = new ToolBar(clear, open, save, run, help, deep);
	
	private final ToolBar bottom = new ToolBar(new Label("© 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, FH Upper Austria"));
	
	private EntityViewer<?> selected;
	
	private final GridPane grid = new GridPane();
	
	private final ScrollPane right = new ScrollPane(grid);
	
	private final BorderPane root = new BorderPane(null, top, right, bottom, null);
	
	public EditorScene() {
		super(new Label("Loading ..."), 800, 600);
		
		if (!modelRunsFolder.exists()) {
			modelRunsFolder.mkdirs();
		}
		
		// Top
		
		clear.setOnAction(event -> {
			modelRunsFolder = new File("runs");
			
			model = new Model();
			
			reload();
		});
		
		open.setOnAction(event -> {			
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(PersistentMemoryHelper.getContextFolder());
			
			File directory = chooser.showDialog(getWindow());
			
			if (directory != null) {
				try {
					PersistentMemoryHelper.setContextFolder(directory.getParentFile());
					
					model = new Parser().parse(directory);
					
					modelRunsFolder = new File(directory, "runs");
					
					if (!modelRunsFolder.exists()) {
						modelRunsFolder.mkdir();
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
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(PersistentMemoryHelper.getContextFolder());
			
			File directory = chooser.showDialog(getWindow());
			
			if (directory != null) {
				PersistentMemoryHelper.setContextFolder(directory.getParentFile());
				
				new Serializer().serialize(model, directory);
				
				modelRunsFolder = new File(directory, "runs");
				
				if (!modelRunsFolder.exists()) {
					modelRunsFolder.mkdir();
				}
			}
		});
		
		run.setOnAction(event -> {
			if (modelRunsFolder != null) {
				Stage parentStage = (Stage) getWindow();
				
				SimulatorScene scene = new SimulatorScene(model, modelRunsFolder, 800, 600);
				
				Stage stage = new Stage();
	
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initOwner(parentStage);
				
				stage.getIcons().addAll(parentStage.getIcons());
				
				stage.setTitle("ITS-MSE Simulator");
				stage.setMaximized(parentStage.isMaximized());
				stage.setX(getWindow().getX() + 20);
				stage.setY(getWindow().getY() + 20);
				stage.setScene(scene);
				
				stage.show();
			} else {
				new Alert(AlertType.ERROR, "Please save model first!").showAndWait();
			}
		});
		
		help.setOnAction(event -> {
			Stage parentStage = (Stage) getWindow();
			
			HelpScene scene = new HelpScene(800, 600);
			
			Stage stage = new Stage();

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(parentStage);
			
			stage.getIcons().addAll(parentStage.getIcons());
			
			stage.setTitle("ITS-MSE Help");
			stage.setMaximized(parentStage.isMaximized());
			stage.setX(getWindow().getX() + 20);
			stage.setY(getWindow().getY() + 20);
			stage.setScene(scene);
			
			stage.show();
		});
		
		deep.setOnAction(event -> {
			if (deep.isSelected()) {
				root.setCenter(modelViewerDeep);
			} else {
				root.setCenter(modelViewerFlat);
			}
		});
		
		// Right
		
		grid.setPrefWidth(300);
		
		grid.setPadding(new Insets(10));
		
		grid.setHgap(10);
		grid.setVgap(10);
		
		grid.getColumnConstraints().add(GridHelper.createColumnConstraints(false, Priority.NEVER));
		grid.getColumnConstraints().add(GridHelper.createColumnConstraints(true, Priority.ALWAYS));
		
		right.setFitToWidth(true);
		right.setFitToHeight(true);
		
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
		
		modelViewerFlat = new ModelViewerFlat(model);
		modelViewerDeep = new ModelViewerDeep(model);
		
		modelViewerFlat.segmentLayer.getChildren().add(segmentPreviewLine);
		modelViewerFlat.segmentLayer.getChildren().add(segmentPreviewHead);
		
		setup(modelViewerFlat.intersections, this::detach, this::attach);
		setup(modelViewerFlat.segments, this::detach, this::attach);
		setup(modelViewerFlat.stations, this::detach, this::attach);
		setup(modelViewerFlat.vehicles, this::detach, this::attach);
		setup(modelViewerFlat.demands, this::detach, this::attach);
		
		modelViewerFlat.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				try {
					double x = event.getSceneX();
					double y = event.getSceneY();
					
					Point2D world = modelViewerFlat.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
					
					Intersection intersection = new Intersection();
					intersection.name.set("Intersection " + (model.intersections.size() + 1));
					intersection.coordinate.x.set(world.getX());
					intersection.coordinate.y.set(world.getY());
					intersection.coordinate.z.set(0);
					
					model.intersections.add(intersection);
					
					select(modelViewerFlat.intersectionViewers.get(intersection));
					
					event.consume();
				} catch (NonInvertibleTransformException e) {
					e.printStackTrace();
				}
			}
		});
		modelViewerFlat.setOnMouseDragEntered(event -> {
			if (event.getGestureSource() instanceof IntersectionViewerFlat) {
				if (event.isControlDown()) {
					segmentPreviewVisible.set(true);
					event.consume();
				}
			}
		});
		modelViewerFlat.setOnMouseDragOver(event -> {
			try {
				double x = event.getSceneX();
				double y = event.getSceneY();
				
				Point2D world = modelViewerFlat.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
				
				if (event.getGestureSource() instanceof IntersectionViewerFlat) {
					IntersectionViewerFlat viewer = (IntersectionViewerFlat) event.getGestureSource();
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
				} else if (event.getGestureSource() instanceof StationViewerFlat) {
					StationViewerFlat viewer = (StationViewerFlat) event.getGestureSource();
					
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
				} else if (event.getGestureSource() instanceof VehicleViewerFlat) {
					VehicleViewerFlat viewer = (VehicleViewerFlat) event.getGestureSource();
					
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
				} else if (event.getGestureSource() instanceof DemandViewerFlat) {
					DemandViewerFlat viewer = (DemandViewerFlat) event.getGestureSource();
					
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
		modelViewerFlat.setOnMouseDragExited(event -> {
			segmentPreviewVisible.set(false);
			event.consume();
		});
		modelViewerFlat.setOnMouseDragReleased(event -> {
			try {
				if (event.getGestureSource() instanceof IntersectionViewerFlat) {
					if (event.isControlDown()) {
						IntersectionViewerFlat viewer = (IntersectionViewerFlat) event.getGestureSource();
						
						double x = event.getSceneX();
						double y = event.getSceneY();
						
						Point2D world = modelViewerFlat.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
		
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
						
						select(modelViewerFlat.segmentViewers.get(segment));
		
						event.consume();
					}
				}
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
		
		if (deep.isSelected()) {
			root.setCenter(modelViewerDeep);
		} else {
			root.setCenter(modelViewerFlat);
		}
	}
	
	private <T> void setup(ObservableList<T> list, GenericListChangeListener.Handler<T> remove, GenericListChangeListener.Handler<T> add) {
		list.addListener(new GenericListChangeListener<T>(remove, add));
		for (T item : list) {
			add.handle(item);
		}
	}
	
	private void attach(IntersectionViewerFlat viewer) {
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
			if (event.getGestureSource() instanceof IntersectionViewerFlat && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					viewer.selected.set(true);
					
					segmentPreviewEndX.set(viewer.entity.coordinate.x.get());
					segmentPreviewEndY.set(viewer.entity.coordinate.y.get());
					
					event.consume();
				}
			}
		});
		viewer.setOnMouseDragOver(event -> {
			if (event.getGestureSource() instanceof IntersectionViewerFlat && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					event.consume();
				}
			}
		});
		viewer.setOnMouseDragExited(event -> {
			if (event.getGestureSource() instanceof IntersectionViewerFlat && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					viewer.selected.set(false);
					event.consume();
				}
			}
		});
		viewer.setOnMouseDragReleased(event -> {
			if (event.getGestureSource() instanceof IntersectionViewerFlat && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					segmentPreviewVisible.set(false);
					
					IntersectionViewerFlat other = (IntersectionViewerFlat) event.getGestureSource();
					
					Segment segment = new Segment(other.entity, viewer.entity);
					
					other.entity.outgoing.add(segment);
					viewer.entity.incoming.add(segment);
					
					model.segments.add(segment);
					
					select(modelViewerFlat.segmentViewers.get(segment));
					
					event.consume();
				}
			}
		});
	}
	private void attach(SegmentViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			try {
				if (event.isStillSincePress()) {
					double x = event.getSceneX();
					double y = event.getSceneY();
					
					Point2D world = modelViewerFlat.canvas.getLocalToSceneTransform().createInverse().transform(x, y);

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
						
						select(modelViewerFlat.stationViewers.get(station));
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
						
						select(modelViewerFlat.vehicleViewers.get(vehicle));
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
				
				Point2D world = modelViewerFlat.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
	
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
				if (event.getGestureSource() instanceof SegmentViewerFlat) {
					double x = event.getSceneX();
					double y = event.getSceneY();
					
					Point2D world = modelViewerFlat.canvas.getLocalToSceneTransform().createInverse().transform(x, y);
	
					double sx = viewer.entity.start.coordinate.x.get();
					double sy = viewer.entity.start.coordinate.y.get();
					
					double tx = viewer.entity.tangent.x.get();
					double ty = viewer.entity.tangent.y.get();
					
					double dx = world.getX() - sx;
					double dy = world.getY() - sy;
					
					double dot = tx * dx + ty * dy;
					
					SegmentViewerFlat other = (SegmentViewerFlat) event.getGestureSource();
					
					Demand demand = new Demand();
					
					demand.size.set(1);
					
					demand.pick.location.segment.set(other.entity);
					demand.pick.location.distance.set(dragDemandDot);
					
					demand.drop.location.segment.set(viewer.entity);
					demand.drop.location.distance.set(dot);
					
					model.demands.add(demand);
					
					select(modelViewerFlat.demandViewers.get(demand));
					
					event.consume();
				}
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
	}
	private void attach(StationViewerFlat viewer) {
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
	private void attach(VehicleViewerFlat viewer) {
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
	private void attach(DemandViewerFlat viewer) {
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
	
	private void detach(IntersectionViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
		viewer.setOnMouseDragEntered(null);
		viewer.setOnMouseDragExited(null);
		viewer.setOnMouseDragReleased(null);
	}
	private void detach(SegmentViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}
	private void detach(StationViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}
	private void detach(VehicleViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}
	private void detach(DemandViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}
	
	private void select(EntityViewer<?> viewer) {
		if (selected != null) {
			selected.selected.set(false);
		}
		
		grid.getChildren().clear();
		
		if (viewer != null) {
			viewer.selected.set(true);
			
			selected = viewer;
			
			if (viewer instanceof IntersectionViewerFlat) {
				show((IntersectionViewerFlat) viewer);
			} else if (viewer instanceof SegmentViewerFlat) {
				show((SegmentViewerFlat) viewer);
			} else if (viewer instanceof StationViewerFlat) {
				show((StationViewerFlat) viewer);
			} else if (viewer instanceof VehicleViewerFlat) {
				show((VehicleViewerFlat) viewer);
			} else if (viewer instanceof DemandViewerFlat) {
				show((DemandViewerFlat) viewer);
			}
		}
	}
	
	private void show(IntersectionViewerFlat viewer) {
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
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Name"), 0, 1);
		grid.add(name, 1, 1);
		
		grid.add(new Label("X"), 0, 2);
		grid.add(x, 1, 2);
		
		grid.add(new Label("Y"), 0, 3);
		grid.add(y, 1, 3);
		
		grid.add(new Label("Z"), 0, 4);
		grid.add(z, 1, 4);
		
		grid.add(delete, 1, 5);
	}
	
	private void show(SegmentViewerFlat viewer) {
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
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Start"), 0, 1);
		grid.add(start, 1, 1);
		
		grid.add(new Label("End"), 0, 2);
		grid.add(end, 1, 2);
		
		grid.add(new Label("Lanes"), 0, 3);
		grid.add(lanes, 1, 3);
		
		grid.add(new Label("Speed"), 0, 4);
		grid.add(speed, 1, 4);
		
		grid.add(delete, 1, 5);
	}
	
	private void show(StationViewerFlat viewer) {
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
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Speed"), 0, 1);
		grid.add(speed, 1, 1);
		
		grid.add(delete, 1, 2);
	}
	
	private void show(VehicleViewerFlat viewer) {
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
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Name"), 0, 1);
		grid.add(name, 1, 1);
		
		grid.add(new Label("Length"), 0, 2);
		grid.add(length, 1, 2);
		
		grid.add(new Label("Battery capacity"), 0, 3);
		grid.add(batteryCapacity, 1, 3);
		
		grid.add(new Label("Load capacity"), 0, 4);
		grid.add(loadCapacity, 1, 4);
		
		grid.add(new Label("Initial battery level"), 0, 5);
		grid.add(initialBatteryLevel, 1, 5);
		
		grid.add(new Label("Initial speed"), 0, 6);
		grid.add(initialSpeed, 1, 6);
		
		grid.add(delete, 1, 7);
	}
	
	private void show(DemandViewerFlat viewer) {
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
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Size"), 0, 1);
		grid.add(size, 1, 1);
		
		grid.add(new Label("Pick"), 0, 2);
		grid.add(pick, 1, 2);
		
		grid.add(new Label("Drop"), 0, 3);
		grid.add(drop, 1, 3);
		
		grid.add(delete, 1, 4);
	}
	
}
