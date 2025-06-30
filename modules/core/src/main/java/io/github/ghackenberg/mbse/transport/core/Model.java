package io.github.ghackenberg.mbse.transport.core;

import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

	public class State {
		
		public double time = 0;
		
	}
	
	public final ThreadLocal<State> state = new ThreadLocal<>();
	
	// Properties
	
	public final StringProperty name = new SimpleStringProperty();
	
	// Structures
	
	public ObservableList<Intersection> intersections = FXCollections.observableArrayList();
	public ObservableList<Segment> segments = FXCollections.observableArrayList();
	public ObservableList<Station> stations = FXCollections.observableArrayList();
	public ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
	public ObservableList<Demand> demands = FXCollections.observableArrayList();
	
	// Methods
	
	public Intersection getIntersection(String name) {
		for (Intersection intersection : intersections) {
			if (intersection.name.get().equals(name)) {
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
			if (vehicle.name.get().equals(name)) {
				return vehicle;
			}
		}
		return null;
	}
	
	public void initialize() {
		state.set(new State());
		
		for (Segment segment : segments) {
			segment.state.set(segment.new State());
		}
		for (Station station : stations) {
			station.state.set(station.new State());
		}
		for (Vehicle vehicle : vehicles) {
			vehicle.state.set(vehicle.new State());
		}
		for (Demand demand : demands) {
			demand.state.set(demand.new State());
		}
	}
	
}
