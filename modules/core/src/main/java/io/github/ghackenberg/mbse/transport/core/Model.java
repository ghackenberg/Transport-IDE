package io.github.ghackenberg.mbse.transport.core;

import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Model {

	public class State {
		
		public double time = 0;
		
	}
	
	public final ThreadLocal<State> state = new ThreadLocal<>();
	
	// Properties
	
	public final StringProperty name = new SimpleStringProperty();
	
	public final DoubleProperty minX = new SimpleDoubleProperty();
	public final DoubleProperty minY = new SimpleDoubleProperty();
	public final DoubleProperty minZ = new SimpleDoubleProperty();
	
	public final DoubleProperty maxX = new SimpleDoubleProperty();
	public final DoubleProperty maxY = new SimpleDoubleProperty();
	public final DoubleProperty maxZ = new SimpleDoubleProperty();
	
	public final DoubleProperty deltaX = new SimpleDoubleProperty();
	public final DoubleProperty deltaY = new SimpleDoubleProperty();
	public final DoubleProperty deltaZ = new SimpleDoubleProperty();
	
	// Structures
	
	public ObservableList<Intersection> intersections = FXCollections.observableArrayList();
	public ObservableList<Segment> segments = FXCollections.observableArrayList();
	public ObservableList<Station> stations = FXCollections.observableArrayList();
	public ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
	public ObservableList<Demand> demands = FXCollections.observableArrayList();
	
	// Constructors
	
	public Model() {
		final InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				recompute();	
			}
		};
		intersections.addListener(new ListChangeListener<>() {
			@Override
			public void onChanged(Change<? extends Intersection> c) {
				while (c.next()) {
					for (Intersection intersection : c.getRemoved()) {
						intersection.lanes.removeListener(listener);
						
						intersection.coordinate.x.removeListener(listener);
						intersection.coordinate.y.removeListener(listener);
						intersection.coordinate.z.removeListener(listener);
					}
					for (Intersection intersection : c.getAddedSubList()) {
						intersection.lanes.addListener(listener);
						
						intersection.coordinate.x.addListener(listener);
						intersection.coordinate.y.addListener(listener);
						intersection.coordinate.z.addListener(listener);
					}
				}
				recompute();
			}
		});
		recompute();
	}
	
	// Methods
	
	private void recompute() {
		// X
		
		double minX = +Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		
		for (Intersection intersection : intersections) {
			minX = Math.min(minX, intersection.coordinate.x.get() - intersection.lanes.get() / 2);
			maxX = Math.max(maxX, intersection.coordinate.x.get() + intersection.lanes.get() / 2);
		}
		
		if (intersections.size() == 0) {
			minX = 0;
			maxX = 0;
		}
		
		this.minX.set(minX);
		this.maxX.set(maxX);
		
		deltaX.set(maxX - minX);
		
		// Y
		
		double minY = +Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		
		for (Intersection intersection : intersections) {
			minY = Math.min(minY, intersection.coordinate.y.get() - intersection.lanes.get() / 2);
			maxY = Math.max(maxY, intersection.coordinate.y.get() + intersection.lanes.get() / 2);
		}
		
		if (intersections.size() == 0) {
			minY = 0;
			maxY = 0;
		}
		
		this.minY.set(minY);
		this.maxY.set(maxY);
		
		deltaY.set(maxY - minY);
		
		// Z
		
		double minZ = +Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		for (Intersection intersection : intersections) {
			minZ = Math.min(minZ, intersection.coordinate.z.get() - intersection.lanes.get() / 2);
			maxZ = Math.max(maxZ, intersection.coordinate.z.get() + intersection.lanes.get() / 2);
		}
		
		if (intersections.size() == 0) {
			minZ = 0;
			maxZ = 0;
		}
		
		this.minZ.set(minZ);
		this.maxZ.set(maxZ);
		
		deltaZ.set(maxZ - minZ);
	}
	
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
	
	public void delete(Intersection intersection) {
		while (intersection.incoming.size() > 0) {
			delete(intersection.incoming.get(0));
		}
		
		while (intersection.outgoing.size() > 0) {
			delete(intersection.outgoing.get(0));
		}
		
		intersections.remove(intersection);
	}
	
	public void delete(Segment segment) {
		for (int i = 0; i < stations.size(); i++) {
			Station station = stations.get(i);
			if (station.location.segment.get() == segment) {
				stations.remove(i--);
			}
		}
		
		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle vehicle = vehicles.get(i);
			if (vehicle.initialLocation.segment.get() == segment) {
				vehicles.remove(i--);
			}
		}
		
		for (int i = 0; i < demands.size(); i++) {
			Demand demand = demands.get(i);
			if (demand.pick.location.segment.get() == segment || demand.drop.location.segment.get() == segment) {
				demands.remove(i--);
			}
		}
		
		segment.start.outgoing.remove(segment);
		segment.end.incoming.remove(segment);
		
		segments.remove(segment);
	}
	
	public void delete(Station station) {
		stations.remove(station);
	}
	
	public void delete(Vehicle vehicle) {
		vehicles.remove(vehicle);
	}
	
	public void delete(Demand demand) {
		demands.remove(demand);
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
