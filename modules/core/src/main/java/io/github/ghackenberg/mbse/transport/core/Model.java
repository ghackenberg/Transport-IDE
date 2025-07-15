package io.github.ghackenberg.mbse.transport.core;

import io.github.ghackenberg.mbse.transport.core.entities.Camera;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.structures.Vector;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * A container for all the model elements.
 */
public class Model {

	/**
	 * Dynamic state properties of a model.
	 */
	public class State {
		
		public double time = 0;
		
	}
	
	public final ThreadLocal<State> state = new ThreadLocal<>();
	
	// Properties
	
	public final StringProperty name = new SimpleStringProperty();
	
	// Structures
	
	public final Vector min = new Vector();
	public final Vector max = new Vector();
	public final Vector size = new Vector();
	public final Vector center = new Vector();
	
	public ObservableList<Intersection> intersections = FXCollections.observableArrayList();
	public ObservableList<Segment> segments = FXCollections.observableArrayList();
	public ObservableList<Station> stations = FXCollections.observableArrayList();
	public ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
	public ObservableList<Demand> demands = FXCollections.observableArrayList();
	public ObservableList<Camera> cameras = FXCollections.observableArrayList();
	
	// Constructors
	
	public Model() {
		// Min and max

		final InvalidationListener boundsListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				recomputeBounds();	
			}
		};

		intersections.addListener(new ListChangeListener<>() {
			@Override
			public void onChanged(Change<? extends Intersection> c) {
				while (c.next()) {
					for (Intersection intersection : c.getRemoved()) {
						intersection.lanes.removeListener(boundsListener);
						
						intersection.coordinate.x.removeListener(boundsListener);
						intersection.coordinate.y.removeListener(boundsListener);
						intersection.coordinate.z.removeListener(boundsListener);
					}
					for (Intersection intersection : c.getAddedSubList()) {
						intersection.lanes.addListener(boundsListener);
						
						intersection.coordinate.x.addListener(boundsListener);
						intersection.coordinate.y.addListener(boundsListener);
						intersection.coordinate.z.addListener(boundsListener);
					}
				}
				recomputeBounds();
				recomputeClips();
			}
		});

		// Near and far

		final InvalidationListener clipsListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				recomputeClips();
			}
		};

		cameras.addListener(new ListChangeListener<>() {
			@Override
			public void onChanged(Change<? extends Camera> c) {
				while (c.next()) {
					for (Camera camera : c.getRemoved()) {
						camera.eye.x.removeListener(clipsListener);
						camera.eye.y.removeListener(clipsListener);
						camera.eye.z.removeListener(clipsListener);

						camera.direction.x.removeListener(clipsListener);
						camera.direction.y.removeListener(clipsListener);
						camera.direction.z.removeListener(clipsListener);
					}
					for (Camera camera : c.getAddedSubList()) {
						camera.eye.x.addListener(clipsListener);
						camera.eye.y.addListener(clipsListener);
						camera.eye.z.addListener(clipsListener);

						camera.direction.x.addListener(clipsListener);
						camera.direction.y.addListener(clipsListener);
						camera.direction.z.addListener(clipsListener);
					}
				}
				recomputeClips();
			}
		});

		// Size

		size.x.bind(max.x.subtract(min.x));
		size.y.bind(max.y.subtract(min.y));
		size.z.bind(max.z.subtract(min.z));

		size.x.addListener(event -> recomputeClips());
		size.y.addListener(event -> recomputeClips());
		size.z.addListener(event -> recomputeClips());

		// Center

		center.x.bind(min.x.add(size.x.divide(2)));
		center.y.bind(min.y.add(size.y.divide(2)));
		center.z.bind(min.z.add(size.z.divide(2)));

		center.x.addListener(event -> recomputeClips());
		center.y.addListener(event -> recomputeClips());
		center.z.addListener(event -> recomputeClips());

		// Recompute

		recomputeBounds();
	}
	
	// Methods

	// - Project

	public class Projection {
		public final Segment seg;
		public final double dot;
		public final double len;

		public Projection(Segment seg, double dot, double len) {
			this.seg = seg;
			this.dot = dot;
			this.len = len;
		}
	}

	public Projection project(double x, double y) {
		Segment minSeg = null;
		double minDot = 0;
		double minLen = Double.MAX_VALUE;
		
		for (Segment segment : segments) {

			Segment.Projection tuple = segment.project(x, y);
			
			if (tuple.len < minLen) {
				minSeg = segment;
				minDot = tuple.dot;
				minLen = tuple.len;
			}
		}

		return new Projection(minSeg, minDot, minLen);
	}

	// - Get
	
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

	// - Delete
	
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

	// - Initialize
	
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
	
	// - Recompute
	
	private void recomputeBounds() {
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
		
		min.x.set(minX);
		max.x.set(maxX);
		
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
		
		min.y.set(minY);
		max.y.set(maxY);
		
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
		
		min.z.set(minZ);
		max.z.set(maxZ);
	}

	private void recomputeClips() {
		for (Camera camera : cameras) {
			double distance = center.toPoint3D().subtract(camera.eye.toPoint3D()).magnitude();

			double x = size.x.get() / 2;
			double y = size.y.get() / 2;
			double z = size.z.get() / 2;

			double radius = Math.sqrt(x * x + y * y + z * z) * 1.5;

			double near = Math.max(distance - radius, 0.01);
			double far = Math.max(distance + radius, 0.02);

			camera.near.set(near);
			camera.far.set(far);
		}
	}
	
}
