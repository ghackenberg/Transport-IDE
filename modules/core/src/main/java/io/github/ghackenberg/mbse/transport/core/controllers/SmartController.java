package io.github.ghackenberg.mbse.transport.core.controllers;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import io.github.ghackenberg.mbse.transport.core.Controller;
import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.adapters.GraphAdapter;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Location;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class SmartController implements Controller {
	
	private final Model model;
	private final Graph<Intersection, Segment> graph; 
	private final ShortestPathAlgorithm<Intersection, Segment> algorithm;
	
	public SmartController(Model model) {
		this.model = model;
		graph = new GraphAdapter(model);
		algorithm = new DijkstraShortestPath<>(graph);
	}

	@Override
	public boolean selectDemand(Vehicle vehicle, Demand demand) {
		return true;
	}

	@Override
	public boolean selectStation(Vehicle vehicle, Station station) {
		double minimumWeight = Double.MAX_VALUE;
		
		for (Station otherStation : model.stations) {
			double distance = getDistance(vehicle.location, otherStation.location, null);
			if (minimumWeight > distance) {
				minimumWeight = distance;
			}
		}
		
		return vehicle.batteryLevel < minimumWeight;
	}
	
	@Override
	public boolean unselectStation(Vehicle vehicle) {
		return false;
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		double minimumDistance = Double.MAX_VALUE;
		
		for (Station station : model.stations) {
			double distance = getDistance(vehicle.location, station.location, null);
			if (minimumDistance > distance) {
				minimumDistance = distance;
			}
		}
		
		for (Station station : model.stations) {
			if (vehicle.location.getSegment() == station.location.getSegment()) {
				if (vehicle.location.getDistance() == station.location.getDistance()) {
					if (vehicle.batteryLevel < minimumDistance) {
						return 0;
					}
				}
			}
		}
		
		return vehicle.location.getSegment().speed;
	}

	@Override
	public double selectMaximumSpeedSelectionTimeout(Vehicle vehicle) {
		return Double.MAX_VALUE;
	}

	@Override
	public double selectMaximumStationSelectionTimeout(Vehicle vehicle) {
		return Double.MAX_VALUE;
	}

	@Override
	public Segment selectSegment(Vehicle vehicle) {
		double minimumDistance = Double.MAX_VALUE;
		Segment minimumSegment = null;
		
		Reference<Segment> next = new Reference<>();
		
		if (vehicle.demands.size() > 0) {
			// Find closest demand dropoff segment
			
			for (Demand demand : vehicle.demands) {
				double distance = getDistance(vehicle.location, demand.dropoff.location, next);
				if (minimumDistance > distance && next.value != null) {
					if (vehicle.batteryLevel >= distance + getMinimumStationDistance(demand.dropoff.location)) {
						minimumDistance = distance;
						minimumSegment = next.value;
					}
				}
			}
		} else {
			// Find closest demand pickup segment
			
			for (Demand demand : model.demands) {
				if (demand.done == false && demand.vehicle == null && demand.pickup.time <= model.time) {
					if (vehicle.loadLevel + demand.size <= vehicle.loadCapacity) {
						double distance = getDistance(vehicle.location, demand.pickup.location, next);
						if (minimumDistance > distance && next.value != null) {
							if (vehicle.batteryLevel >= distance + getMinimumStationDistance(demand.pickup.location)) {
								minimumDistance = distance;
								minimumSegment = next.value;
							}
						}
					}
				}
			}
		}
		
		if (minimumSegment == null) {
			// Find closest station segment
			
			for (Station station : model.stations) {
				double distance = getDistance(vehicle.location, station.location, next);
				if (minimumDistance > distance && next.value != null) {
					minimumDistance = distance;
					minimumSegment = next.value;
				}
			}
		}
		
		if (minimumSegment == null) {
			// Select random segment
			
			List<Segment> candidates = vehicle.location.getSegment().end.outgoing;
			
			minimumSegment = candidates.get((int) (Math.random() * candidates.size()));
		}
		
		return minimumSegment;
	}
	
	class Reference<T> {
		public T value;
	}
	
	private double getDistance(Location a, Location b, Reference<Segment> next) {
		if (a.getSegment() == b.getSegment() && a.getDistance() < b.getDistance()) {
			return b.getDistance() - a.getDistance();
		} else if (a.getSegment().end == b.getSegment().start) {
			if (next != null) {
				next.value = b.getSegment();
			}
			return a.getSegment().getLength() - a.getDistance() + b.getDistance();
		} else {
			SingleSourcePaths<Intersection, Segment> paths = algorithm.getPaths(a.getSegment().end);
			GraphPath<Intersection, Segment> path = paths.getPath(b.getSegment().start);
			if (path.getLength() > 0) {
				if (next != null) {
					next.value = path.getEdgeList().get(0);
				}
				return a.getSegment().getLength() - a.getDistance() + path.getWeight() + b.getDistance();
			} else {
				return Double.MAX_VALUE;
			}
		}
	}
	
	private double getMinimumStationDistance(Location a) {
		double minimumDistance = Double.MAX_VALUE;
		for (Station station : model.stations) {
			if (station.location.getSegment() == a.getSegment() && station.location.getDistance() == a.getDistance()) {
				return 0;
			} else {
				double distance = getDistance(a, station.location, null);
				if (minimumDistance > distance) {
					minimumDistance = distance;
				}
			}
		}
		return minimumDistance;
	}
	
	@Override
	public void reset() {
		
	}
	
	@Override
	public String toString() {
		return "Smart controller";
	}

}
