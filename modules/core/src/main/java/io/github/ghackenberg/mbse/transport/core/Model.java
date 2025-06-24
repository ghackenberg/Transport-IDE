package io.github.ghackenberg.mbse.transport.core;

import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class Model {

	// Dynamische Eigenschaften (simuliert)
	public double time;
	
	// Statische Eigenschaften (geparst)
	public String name = "Model";
	
	public List<Intersection> intersections = new ArrayList<>();
	public List<Segment> segments = new ArrayList<>();
	public List<Station> stations = new ArrayList<>();
	public List<Vehicle> vehicles = new ArrayList<>();
	public List<Demand> demands = new ArrayList<>();
	
	public Intersection getIntersection(String name) {
		for (Intersection intersection : intersections) {
			if (intersection.name.equals(name)) {
				return intersection;
			}
		}
		return null;
	}
	
	public Segment getSegment(Intersection start, Intersection end) {
		for (Segment segment : segments) {
			if (segment.start == start && segment.end == end) {
				return segment;
			}
		}
		return null;
	}
	
	public Vehicle getVehicle(String name) {
		for (Vehicle vehicle : vehicles) {
			if (vehicle.name.equals(name)) {
				return vehicle;
			}
		}
		return null;
	}
	
	public void reset() {
		time = 0;
		
		demands.forEach(demand -> demand.reset());
		vehicles.forEach(vehicle -> vehicle.reset());
		stations.forEach(station -> station.reset());
	}
	
}
