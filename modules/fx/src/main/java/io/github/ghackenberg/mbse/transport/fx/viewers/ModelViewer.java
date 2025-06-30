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
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.transform.NonInvertibleTransformException;

public class ModelViewer extends Pane {
	
	private final Model model;
	private final Model.State modelState;
	
	private double minX = Double.MAX_VALUE;
	private double maxX = -Double.MAX_VALUE;
	
	private double minY = Double.MAX_VALUE;
	private double maxY = -Double.MAX_VALUE;
	
	private double deltaX;
	private double deltaY;
	
	private final Group intersectionLayer = new Group();
	private final Group segmentLayer = new Group();
	private final Group stationLayer = new Group();
	private final Group vehicleLayer = new Group();
	private final Group demandLayer = new Group();

	private final Group innerTranslate = new Group(segmentLayer, intersectionLayer, stationLayer, demandLayer, vehicleLayer);
	private final Group scale = new Group(innerTranslate);
	private final Group outerTranslate = new Group(scale);
	
	private final Text text;

	private final Map<Intersection, IntersectionViewer> intersectionViewers = new HashMap<>();
	private final Map<Segment, SegmentViewer> segmentViewers = new HashMap<>();
	private final Map<Station, StationViewer> stationViewers = new HashMap<>();
	private final Map<Vehicle, VehicleViewer> vehicleViewers = new HashMap<>();
	private final Map<Demand, DemandViewer> demandViewers = new HashMap<>();
	
	private final InvalidationListener listener;
	
	public ModelViewer(Model model) {
		this(model, true);
	}
	
	public ModelViewer(Model model, boolean showDemands) {
		this.model = model;
		this.modelState = model.state.get();
		
		final ModelViewer self = this;
		
		listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				self.resize();
			}
		};
		
		setStyle("-fx-background-color: white;");
		
		setPrefWidth(200);
		setPrefHeight(200);
		
		widthProperty().addListener(event -> zoom());
		heightProperty().addListener(event -> zoom());
		
		setOnMouseClicked(event -> {
			try {
				event.consume();
				
				double x = event.getSceneX();
				double y = event.getSceneY();
				
				Point2D world = innerTranslate.getLocalToSceneTransform().createInverse().transform(x, y);
				
				Intersection intersection = new Intersection();
				intersection.name.set("Intersection " + (model.intersections.size() + 1));
				intersection.coordinate.x.set(world.getX());
				intersection.coordinate.y.set(world.getY());
				intersection.coordinate.z.set(0);
				
				model.intersections.add(intersection);
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
		
		resize();
		
		// Text

		if (modelState != null) {
			text = new Text("" + modelState.time);
		} else {
			text = null;
		}
		
		// Group
		
		getChildren().add(outerTranslate);
		if (modelState != null) {
			getChildren().add(text);
		}
		
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
		intersection.coordinate.x.addListener(listener);
		intersection.coordinate.y.addListener(listener);
		intersection.coordinate.z.addListener(listener);
		
		IntersectionViewer viewer = new IntersectionViewer(model, intersection);
		
		intersectionLayer.getChildren().add(viewer);
		
		intersectionViewers.put(intersection, viewer);
		
		resize();
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
		intersection.coordinate.x.removeListener(listener);
		intersection.coordinate.y.removeListener(listener);
		intersection.coordinate.z.removeListener(listener);
		
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
		text.setText("" + modelState.time);
	}
	
	// Resize, recompute, zoom
	
	private void resize() {
		
		recompute();
		
		zoom();
		
	}
	
	private void recompute() {
		for (Intersection intersection : model.intersections) {
			minX = Math.min(minX, intersection.coordinate.x.get());
			maxX = Math.max(maxX, intersection.coordinate.x.get());
		}
		
		for (Intersection intersection : model.intersections) {
			minY = Math.min(minY, intersection.coordinate.y.get());
			maxY = Math.max(maxY, intersection.coordinate.y.get());
		}
		
		deltaX = maxX - minX;
		deltaY = maxY - minY;
	}
	
	private void zoom() {
		double width = getWidth() - 20;
		double height = getHeight() - 20;
		
		double factor = Math.min(width / deltaX, height / deltaY);
		
		innerTranslate.setTranslateX(0 - minX - deltaX / 2);
		innerTranslate.setTranslateY(0 - minY - deltaY / 2);
		
		scale.setScaleX(factor);
		scale.setScaleY(factor);
		
		outerTranslate.setTranslateX(getWidth() / 2);
		outerTranslate.setTranslateY(getHeight() / 2);
	}
	
}
