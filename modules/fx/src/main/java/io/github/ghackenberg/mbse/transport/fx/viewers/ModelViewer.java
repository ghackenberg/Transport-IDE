package io.github.ghackenberg.mbse.transport.fx.viewers;

import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.events.DemandEvent;
import io.github.ghackenberg.mbse.transport.fx.events.IntersectionEvent;
import io.github.ghackenberg.mbse.transport.fx.events.SegmentEvent;
import io.github.ghackenberg.mbse.transport.fx.events.StationEvent;
import io.github.ghackenberg.mbse.transport.fx.events.VehicleEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class ModelViewer extends Pane {
	
	private final Model.State modelState;
	
	private double minX = Double.MAX_VALUE;
	private double maxX = -Double.MAX_VALUE;
	
	private double minY = Double.MAX_VALUE;
	private double maxY = -Double.MAX_VALUE;
	
	private double deltaX;
	private double deltaY;

	private final Group innerTranslate;
	private final Group outerTranslate;
	private final Group scale;
	
	private final Text text;
	
	private EventHandler<IntersectionEvent> onIntersectionSelected;
	private EventHandler<SegmentEvent> onSegmentSelected;
	private EventHandler<StationEvent> onStationSelected;
	private EventHandler<VehicleEvent> onVehicleSelected;
	private EventHandler<DemandEvent> onDemandSelected;

	private final List<SegmentViewer> segments = new ArrayList<>();
	private final List<IntersectionViewer> intersections = new ArrayList<>();
	private final List<StationViewer> stations = new ArrayList<>();
	private final List<DemandViewer> demands = new ArrayList<>();
	private final List<VehicleViewer> vehicles = new ArrayList<>();
	
	public ModelViewer(Model model) {
		this(model, true);
	}
	
	public ModelViewer(Model model, boolean showDemands) {
		this.modelState = model.state.get();
		
		setStyle("-fx-background-color: white;");
		
		setPrefWidth(200);
		setPrefHeight(200);
		
		widthProperty().addListener(event -> zoom());
		heightProperty().addListener(event -> zoom());
		
		// TODO
		
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
		
		// Text

		if (modelState != null) {
			text = new Text("" + modelState.time);
		} else {
			text = null;
		}
		
		// Group
		
		innerTranslate = new Group();
		
		scale = new Group();
		scale.getChildren().add(innerTranslate);
		
		outerTranslate = new Group();
		outerTranslate.getChildren().add(scale);
		
		getChildren().add(outerTranslate);
		if (modelState != null) {
			getChildren().add(text);
		}
		
		// Segments
		
		for (Segment segment : model.segments) {
			SegmentViewer viewer = new SegmentViewer(model, segment);
			viewer.setOnSelected(event -> {
				if (onSegmentSelected != null) {
					onSegmentSelected.handle(event);
				}
			});
			
			innerTranslate.getChildren().add(viewer);
			
			segments.add(viewer);
		}
		
		// Intersections
		
		for (Intersection intersection : model.intersections) {
			IntersectionViewer viewer = new IntersectionViewer(model, intersection);
			viewer.setOnSelected(event -> {
				if (onIntersectionSelected != null) {
					onIntersectionSelected.handle(event);
				}
			});
			
			innerTranslate.getChildren().add(viewer);
			
			intersections.add(viewer);
		}
		
		// Stations
		
		for (Station station : model.stations) {
			StationViewer viewer = new StationViewer(model, station);
			viewer.setOnSelected(event -> {
				if (onStationSelected != null) {
					onStationSelected.handle(event);
				}
			});
			
			innerTranslate.getChildren().add(viewer);
			
			stations.add(viewer);
		}
		
		// Demands
		
		if (showDemands) {
			for (Demand demand : model.demands) {
				DemandViewer viewer = new DemandViewer(model, demand);
				viewer.setOnSelected(event -> {
					if (onDemandSelected != null) {
						onDemandSelected.handle(event);
					}
				});
				
				innerTranslate.getChildren().add(viewer);
				
				demands.add(viewer);
			}
		}
		
		// Vehicles
		
		for (Vehicle vehicle : model.vehicles) {
			VehicleViewer viewer = new VehicleViewer(model, vehicle);
			viewer.setOnSelected(event -> {
				if (onVehicleSelected != null) {
					onVehicleSelected.handle(event);
				}
			});
			
			innerTranslate.getChildren().add(viewer);
			
			vehicles.add(viewer);
		}
	}
	
	public void setOnIntersectionSelected(EventHandler<IntersectionEvent> handler) {
		onIntersectionSelected = handler;
	}
	
	public void setOnSegmentSelected(EventHandler<SegmentEvent> handler)  {
		onSegmentSelected = handler;
	}
	
	public void setOnStationSelected(EventHandler<StationEvent> handler) {
		onStationSelected = handler;
	}
	
	public void setOnVehicleSelected(EventHandler<VehicleEvent> handler) {
		onVehicleSelected = handler;
	}
	
	public void setOnDemandSelected(EventHandler<DemandEvent> handler) {
		onDemandSelected = handler;
	}
	
	public void update() {
		for (SegmentViewer viewer : segments) {
			viewer.update();
		}
		for (IntersectionViewer viewer : intersections) {
			viewer.update();
		}
		for (StationViewer viewer : stations) {
			viewer.update();
		}
		for (DemandViewer viewer : demands) {
			viewer.update();
		}
		for (VehicleViewer viewer : vehicles) {
			viewer.update();
		}
		text.setText("" + modelState.time);
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
