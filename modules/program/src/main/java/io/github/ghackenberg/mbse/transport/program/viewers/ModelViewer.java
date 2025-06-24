package example.program.viewers;

import java.util.ArrayList;
import java.util.List;

import example.model.Demand;
import example.model.Intersection;
import example.model.Model;
import example.model.Segment;
import example.model.Station;
import example.model.Vehicle;
import javafx.scene.Group;
import javafx.scene.layout.Pane;

public class ModelViewer extends Pane {
	
	private double minLat = Double.MAX_VALUE;
	private double maxLat = -Double.MAX_VALUE;
	
	private double minLng = Double.MAX_VALUE;
	private double maxLng = -Double.MAX_VALUE;
	
	private double deltaLat;
	private double deltaLng;

	private Group innerTranslate;
	private Group outerTranslate;
	private Group scale;

	private List<SegmentViewer> segments = new ArrayList<>();
	private List<IntersectionViewer> intersections = new ArrayList<>();
	private List<StationViewer> stations = new ArrayList<>();
	private List<DemandViewer> demands = new ArrayList<>();
	private List<VehicleViewer> vehicles = new ArrayList<>();
	
	public ModelViewer(Model model) {
		this(model, true);
	}
	
	public ModelViewer(Model model, boolean showDemands) {
		setStyle("-fx-background-color: white;");
		
		setPrefWidth(200);
		setPrefHeight(200);
		
		widthProperty().addListener(event -> zoom());
		heightProperty().addListener(event -> zoom());
		
		for (Intersection intersection : model.intersections) {
			minLat = Math.min(minLat, intersection.coordinate.latitude);
			maxLat = Math.max(maxLat, intersection.coordinate.latitude);
		}
		
		for (Intersection intersection : model.intersections) {
			minLng = Math.min(minLng, intersection.coordinate.longitude);
			maxLng = Math.max(maxLng, intersection.coordinate.longitude);
		}
		
		deltaLat = maxLat - minLat;
		deltaLng = maxLng - minLng;
		
		// Group
		
		innerTranslate = new Group();
		
		scale = new Group();
		scale.getChildren().add(innerTranslate);
		
		outerTranslate = new Group();
		outerTranslate.getChildren().add(scale);
		
		getChildren().add(outerTranslate);
		
		// Segments
		
		for (Segment segment : model.segments) {
			SegmentViewer viewer = new SegmentViewer(model, segment);
			
			innerTranslate.getChildren().add(viewer);
			
			segments.add(viewer);
		}
		
		// Intersections
		
		for (Intersection intersection : model.intersections) {
			IntersectionViewer viewer = new IntersectionViewer(model, intersection);
			
			innerTranslate.getChildren().add(viewer);
			
			intersections.add(viewer);
		}
		
		// Stations
		
		for (Station station : model.stations) {
			StationViewer viewer = new StationViewer(station);
			
			innerTranslate.getChildren().add(viewer);
			
			stations.add(viewer);
		}
		
		// Demands
		
		if (showDemands) {
			for (Demand demand : model.demands) {
				DemandViewer viewer = new DemandViewer(model, demand);
				
				innerTranslate.getChildren().add(viewer);
				
				demands.add(viewer);
			}
		}
		
		// Vehicles
		
		for (Vehicle vehicle : model.vehicles) {
			VehicleViewer viewer = new VehicleViewer(model, vehicle);
			
			innerTranslate.getChildren().add(viewer);
			
			vehicles.add(viewer);
		}
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
	}
	
	private void zoom() {
		double width = getWidth() - 20;
		double height = getHeight() - 20;
		
		double factor = Math.min(width / deltaLat, height / deltaLng);
		
		innerTranslate.setTranslateX(0 - minLat - deltaLat / 2);
		innerTranslate.setTranslateY(0 - minLng - deltaLng / 2);
		
		scale.setScaleX(factor);
		scale.setScaleY(factor);
		
		outerTranslate.setTranslateX(getWidth() / 2);
		outerTranslate.setTranslateY(getHeight() / 2);
	}
	
}
