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
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public abstract class ModelViewer<I extends IntersectionViewer, Se extends SegmentViewer, St extends StationViewer, V extends VehicleViewer, D extends DemandViewer, C extends Node> extends Pane {
	
	protected double minX;
	protected double minY;
	protected double minZ;
	
	protected double deltaX;
	protected double deltaY;
	protected double deltaZ;

	public final Model model;
	public final Model.State modelState;
	
	public final Group intersectionLayer = new Group();
	public final Group segmentLayer = new Group();
	public final Group stationLayer = new Group();
	public final Group vehicleLayer = new Group();
	public final Group demandLayer = new Group();
	
	public final C canvas;
	
	private final Text time = new Text();
	
	private final Rectangle clip = new Rectangle();
	
	public final ObservableList<I> intersections = FXCollections.observableArrayList();
	public final ObservableList<Se> segments = FXCollections.observableArrayList();
	public final ObservableList<St> stations = FXCollections.observableArrayList();
	public final ObservableList<V> vehicles = FXCollections.observableArrayList();
	public final ObservableList<D> demands = FXCollections.observableArrayList();

	public final Map<Intersection, I> intersectionViewers = new HashMap<>();
	public final Map<Segment, Se> segmentViewers = new HashMap<>();
	public final Map<Station, St> stationViewers = new HashMap<>();
	public final Map<Vehicle, V> vehicleViewers = new HashMap<>();
	public final Map<Demand, D> demandViewers = new HashMap<>();

	public ModelViewer(Model model, C canvas, boolean showDemands) {
		this.model = model;
		this.modelState = model.state.get();
		
		this.canvas = canvas;
		
		minX = model.intersections.size() > 0 ? model.minX.get() - 3 : -10;
		minY = model.intersections.size() > 0 ? model.minY.get() - 3 : -10;
		minZ = model.intersections.size() > 0 ? model.minZ.get() - 3 : -10;
		
		deltaX = model.intersections.size() > 0 ? model.deltaX.get() + 6 : 20;
		deltaY = model.intersections.size() > 0 ? model.deltaY.get() + 6 : 20;
		deltaZ = model.intersections.size() > 0 ? model.deltaZ.get() + 6 : 20;
		
		intersectionLayer.setDepthTest(DepthTest.ENABLE);
		segmentLayer.setDepthTest(DepthTest.ENABLE);
		stationLayer.setDepthTest(DepthTest.ENABLE);
		vehicleLayer.setDepthTest(DepthTest.ENABLE);
		demandLayer.setDepthTest(DepthTest.ENABLE);
		
		setStyle("-fx-background-color: white;");

		clip.widthProperty().bind(widthProperty());
		clip.heightProperty().bind(heightProperty());
		
		getChildren().add(canvas);
		getChildren().add(time);
		
		setDepthTest(DepthTest.ENABLE);
		
		setClip(clip);
		
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
	
	// Create
	
	protected abstract I create(Intersection intersection);
	protected abstract Se create(Segment segment);
	protected abstract St create(Station station);
	protected abstract V create(Vehicle vehicle);
	protected abstract D create(Demand demand);
	
	// Add
	
	private void add(Intersection intersection) {
		add(intersection, create(intersection), intersections, intersectionLayer, intersectionViewers);
	}
	
	private void add(Segment segment) {
		add(segment, create(segment), segments, segmentLayer, segmentViewers);
	}
	
	private void add(Station station) {
		add(station, create(station), stations, stationLayer, stationViewers);
	}
	
	private void add(Vehicle vehicle) {
		add(vehicle, create(vehicle), vehicles, vehicleLayer, vehicleViewers);
	}
	
	private void add(Demand demand) {
		add(demand, create(demand), demands, demandLayer, demandViewers);
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
		for (I viewer : intersectionViewers.values()) {
			viewer.update();
		}
		for (Se viewer : segmentViewers.values()) {
			viewer.update();
		}
		for (St viewer : stationViewers.values()) {
			viewer.update();
		}
		for (D viewer : demandViewers.values()) {
			viewer.update();
		}
		for (V viewer : vehicleViewers.values()) {
			viewer.update();
		}
		if (modelState != null) {
			time.setText("" + Math.round(modelState.time) + " ms");
		}
	}
	
}
