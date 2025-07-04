package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import java.util.Map;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.helpers.GenericListChangeListener;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.NonInvertibleTransformException;

public class ModelViewerFlat extends ModelViewer<IntersectionViewerFlat, SegmentViewerFlat, StationViewerFlat, VehicleViewerFlat, DemandViewerFlat, Pane> {
	
	private double minX;
	private double minY;
	
	private double deltaX;
	private double deltaY;
	
	public final Group intersectionLayer = new Group();
	public final Group segmentLayer = new Group();
	public final Group stationLayer = new Group();
	public final Group vehicleLayer = new Group();
	public final Group demandLayer = new Group();
	
	public ModelViewerFlat(Model model) {
		this(model, true);
	}
	
	public ModelViewerFlat(Model model, boolean showDemands) {
		super(model, new Pane());
		
		minX = model.intersections.size() > 0 ? model.minX.get() - 3 : -10;
		minY = model.intersections.size() > 0 ? model.minY.get() - 3 : -10;
		
		deltaX = model.intersections.size() > 0 ? model.deltaX.get() + 6 : 20;
		deltaY = model.intersections.size() > 0 ? model.deltaY.get() + 6 : 20;
		
		canvas.setPrefWidth(0);
		canvas.setPrefHeight(0);
		canvas.getChildren().add(segmentLayer);
		canvas.getChildren().add(intersectionLayer);
		canvas.getChildren().add(stationLayer);
		canvas.getChildren().add(vehicleLayer);
		canvas.getChildren().add(demandLayer);
		
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
		add(intersection, new IntersectionViewerFlat(model, intersection), intersections, intersectionLayer, intersectionViewers);
	}
	
	private void add(Segment segment) {
		add(segment, new SegmentViewerFlat(model, segment), segments, segmentLayer, segmentViewers);
	}
	
	private void add(Station station) {
		add(station, new StationViewerFlat(model, station), stations, stationLayer, stationViewers);
	}
	
	private void add(Vehicle vehicle) {
		add(vehicle, new VehicleViewerFlat(model, vehicle), vehicles, vehicleLayer, vehicleViewers);
	}
	
	private void add(Demand demand) {
		add(demand, new DemandViewerFlat(model, demand), demands, demandLayer, demandViewers);
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
