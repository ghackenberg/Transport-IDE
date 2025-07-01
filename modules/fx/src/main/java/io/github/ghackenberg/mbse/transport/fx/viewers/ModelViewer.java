package io.github.ghackenberg.mbse.transport.fx.viewers;

import java.util.HashMap;
import java.util.Map;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.helpers.GenericListChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;

public class ModelViewer extends Pane {
	
	private double minX;
	private double minY;
	
	private double deltaX;
	private double deltaY;
	
	private final Model model;
	
	private final Group intersectionLayer = new Group();
	private final Group segmentLayer = new Group();
	private final Group stationLayer = new Group();
	private final Group vehicleLayer = new Group();
	private final Group demandLayer = new Group();
	
	public final Pane canvas = new Pane(segmentLayer, intersectionLayer, stationLayer, vehicleLayer, demandLayer);
	
	private final Rectangle clip = new Rectangle();
	
	public final ObservableList<IntersectionViewer> intersections = FXCollections.observableArrayList();
	public final ObservableList<SegmentViewer> segments = FXCollections.observableArrayList();
	public final ObservableList<StationViewer> stations = FXCollections.observableArrayList();
	public final ObservableList<VehicleViewer> vehicles = FXCollections.observableArrayList();
	public final ObservableList<DemandViewer> demands = FXCollections.observableArrayList();

	public final Map<Intersection, IntersectionViewer> intersectionViewers = new HashMap<>();
	public final Map<Segment, SegmentViewer> segmentViewers = new HashMap<>();
	public final Map<Station, StationViewer> stationViewers = new HashMap<>();
	public final Map<Vehicle, VehicleViewer> vehicleViewers = new HashMap<>();
	public final Map<Demand, DemandViewer> demandViewers = new HashMap<>();
	
	public ModelViewer(Model model) {
		this(model, true);
	}
	
	public ModelViewer(Model model, boolean showDemands) {		
		this.model = model;
		
		minX = model.minX.get();
		minY = model.minY.get();
		
		deltaX = Math.max(model.deltaX.get(), 20);
		deltaY = Math.max(model.deltaY.get(), 20);
		
		canvas.setPrefWidth(0);
		canvas.setPrefHeight(0);

		clip.widthProperty().bind(widthProperty());
		clip.heightProperty().bind(heightProperty());
		
		setStyle("-fx-background-color: white;");
		
		getChildren().add(canvas);
		
		setClip(clip);
		
		widthProperty().addListener(event -> updateTransform());
		heightProperty().addListener(event -> updateTransform());
		
		setOnScroll(event -> {
			try {
				double scrollX = -event.getDeltaX() * Math.max(deltaX, deltaY) * 0.005;
				double scrollY = -event.getDeltaY() * Math.max(deltaX, deltaY) * 0.005;
				
				if (event.isShiftDown()) {
					// Horizontal scroll
					
					minX += scrollX;
					
				} else if (event.isControlDown()) {
					// Vertical scroll
					
					minY += scrollY;
					
				} else if (deltaX + scrollY > 0 && deltaY + scrollY > 0) {
					// Pan and zoom
					
					deltaX += scrollY;
					deltaY += scrollY;
					
					Point2D point = canvas.getLocalToSceneTransform().createInverse().transform(event.getSceneX(), event.getSceneY());
					
					minX -= scrollY * (point.getX() - minX) / deltaX;
					minY -= scrollY * (point.getY() - minY) / deltaY;
				}
				
				updateTransform();
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
		
		// Intersections
		
		model.intersections.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
		for (Intersection intersection : model.intersections) {
			add(intersection);
		}
		
		// Segments
		
		model.segments.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
		for (Segment segment : model.segments) {
			add(segment);
		}
		
		// Stations
		
		model.stations.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
		for (Station station : model.stations) {
			add(station);
		}
		
		// Vehicles
		
		model.vehicles.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
		for (Vehicle vehicle : model.vehicles) {
			add(vehicle);
		}
		
		// Demands

		if (showDemands) {
			model.demands.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
			for (Demand demand : model.demands) {
				add(demand);
			}
		}
	}
	
	// Add
	
	private void add(Intersection intersection) {
		add(intersection, new IntersectionViewer(model, intersection), intersections, intersectionLayer, intersectionViewers);
	}
	
	private void add(Segment segment) {
		add(segment, new SegmentViewer(model, segment), segments, segmentLayer, segmentViewers);
	}
	
	private void add(Station station) {
		add(station, new StationViewer(model, station), stations, stationLayer, stationViewers);
	}
	
	private void add(Vehicle vehicle) {
		add(vehicle, new VehicleViewer(model, vehicle), vehicles, vehicleLayer, vehicleViewers);
	}
	
	private void add(Demand demand) {
		add(demand, new DemandViewer(model, demand), demands, demandLayer, demandViewers);
	}
	
	private <T, S extends Node> void add(T entity, S viewer, ObservableList<S> viewers, Group layer, Map<T, S> viewerMap) {
		viewers.add(viewer);
		
		layer.getChildren().add(viewer);
		
		viewerMap.put(entity, viewer);
	}
	
	// Remove
	
	private void remove(Intersection intersection) {
		remove(intersection, intersections, intersectionLayer, intersectionViewers);
	}
	
	private void remove(Segment segment) {
		remove(segment, segments, segmentLayer, segmentViewers);
	}
	
	private void remove(Station station) {
		remove(station, stations, stationLayer, stationViewers);
	}
	
	private void remove(Vehicle vehicle) {
		remove(vehicle, vehicles, vehicleLayer, vehicleViewers);
	}
	
	private void remove(Demand demand) {
		remove(demand, demands, demandLayer, demandViewers);
	}
	
	private <T, S extends Node> void remove(T entity, ObservableList<S> viewers, Group layer, Map<T, S> viewerMap) {
		S viewer = viewerMap.remove(entity);
		
		layer.getChildren().remove(viewer);
		
		viewers.remove(viewer);
	}
	
	// Update
	
	public void update() {
		for (SegmentViewer viewer : segmentViewers.values()) {
			viewer.update();
		}
		for (IntersectionViewer viewer : intersectionViewers.values()) {
			viewer.update();
		}
		for (StationViewer viewer : stationViewers.values()) {
			viewer.update();
		}
		for (DemandViewer viewer : demandViewers.values()) {
			viewer.update();
		}
		for (VehicleViewer viewer : vehicleViewers.values()) {
			viewer.update();
		}
	}
	
	// Compute
	
	private void updateTransform() {
		double width = getWidth();
		double height = getHeight();
		
		double factor = Math.min(width / deltaX, height / deltaY);
		
		canvas.setTranslateX(- minX * factor + (width - deltaX * factor) / 2);
		canvas.setTranslateY(- minY * factor + (height - deltaY * factor) / 2);
		
		canvas.setScaleX(factor);
		canvas.setScaleY(factor);
	}
	
}
