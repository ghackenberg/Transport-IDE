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
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class ModelViewer extends Pane {
	
	private final double scrollFactor = 0.1;
	
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

	private final Map<Intersection, IntersectionViewer> intersectionViewers = new HashMap<>();
	private final Map<Segment, SegmentViewer> segmentViewers = new HashMap<>();
	private final Map<Station, StationViewer> stationViewers = new HashMap<>();
	private final Map<Vehicle, VehicleViewer> vehicleViewers = new HashMap<>();
	private final Map<Demand, DemandViewer> demandViewers = new HashMap<>();
	
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
			if (deltaX + event.getDeltaY() * scrollFactor > 0 && deltaY + event.getDeltaY() * scrollFactor > 0) {
				
				deltaX += event.getDeltaY() * scrollFactor;
				deltaY += event.getDeltaY() * scrollFactor;
				
				minX -= event.getDeltaY() * scrollFactor / 2;
				minY -= event.getDeltaY() * scrollFactor / 2;
				
				updateTransform();
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
		IntersectionViewer viewer = new IntersectionViewer(model, intersection);
		
		intersectionLayer.getChildren().add(viewer);
		
		intersectionViewers.put(intersection, viewer);
	}
	
	private void add(Segment segment) {
		SegmentViewer viewer = new SegmentViewer(model, segment);
		
		segmentLayer.getChildren().add(viewer);
		
		segmentViewers.put(segment, viewer);
	}
	
	private void add(Station station) {
		StationViewer viewer = new StationViewer(model, station);
		
		stationLayer.getChildren().add(viewer);
		
		stationViewers.put(station, viewer);
	}
	
	private void add(Vehicle vehicle) {
		VehicleViewer viewer = new VehicleViewer(model, vehicle);
		
		vehicleLayer.getChildren().add(viewer);
		
		vehicleViewers.put(vehicle, viewer);
	}
	
	private void add(Demand demand) {
		DemandViewer viewer = new DemandViewer(model, demand);
		
		demandLayer.getChildren().add(viewer);
		
		demandViewers.put(demand, viewer);
	}
	
	// Remove
	
	private void remove(Intersection intersection) {
		intersectionLayer.getChildren().remove(intersectionViewers.remove(intersection));
	}
	
	private void remove(Segment segment) {
		segmentLayer.getChildren().remove(segmentViewers.remove(segment));
	}
	
	private void remove(Station station) {
		stationLayer.getChildren().remove(stationViewers.remove(station));
	}
	
	private void remove(Vehicle vehicle) {
		vehicleLayer.getChildren().remove(vehicleViewers.remove(vehicle));
	}
	
	private void remove(Demand demand) {
		demandLayer.getChildren().remove(demandViewers.remove(demand));
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
