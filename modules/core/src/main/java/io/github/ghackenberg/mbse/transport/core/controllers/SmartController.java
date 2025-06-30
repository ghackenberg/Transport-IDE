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
			double distance = getDistance(vehicle.state.get().segment, vehicle.state.get().distance, otherStation.location.segment.get(), otherStation.location.distance.get(), null);
			if (minimumWeight > distance) {
				minimumWeight = distance;
			}
		}
		
		return vehicle.state.get().batteryLevel < minimumWeight;
	}
	
	@Override
	public boolean unselectStation(Vehicle vehicle) {
		return false;
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		double minimumDistance = Double.MAX_VALUE;
		
		for (Station station : model.stations) {
			double distance = getDistance(vehicle.state.get().segment, vehicle.state.get().distance, station.location.segment.get(), station.location.distance.get(), null);
			if (minimumDistance > distance) {
				minimumDistance = distance;
			}
		}
		
		for (Station station : model.stations) {
			if (vehicle.state.get().segment == station.location.segment.get()) {
				if (vehicle.state.get().distance == station.location.distance.get()) {
					if (vehicle.state.get().batteryLevel < minimumDistance) {
						return 0;
					}
				}
			}
		}
		
		return vehicle.state.get().segment.speed.get();
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
		
		if (vehicle.state.get().demands.size() > 0) {
			// Find closest demand dropoff segment
			
			for (Demand demand : vehicle.state.get().demands) {
				double distance = getDistance(vehicle.state.get().segment, vehicle.state.get().distance, demand.drop.location.segment.get(), demand.drop.location.distance.get(), next);
				if (minimumDistance > distance && next.value != null) {
					if (vehicle.state.get().batteryLevel >= distance + getMinimumStationDistance(demand.drop.location.segment.get(), demand.drop.location.distance.get())) {
						minimumDistance = distance;
						minimumSegment = next.value;
					}
				}
			}
		} else {
			// Find closest demand pickup segment
			
			for (Demand demand : model.demands) {
				if (demand.state.get().done == false && demand.state.get().vehicle == null && demand.pick.time.get() <= model.state.get().time) {
					if (vehicle.state.get().loadLevel + demand.size.get() <= vehicle.loadCapacity.get()) {
						double distance = getDistance(vehicle.state.get().segment, vehicle.state.get().distance, demand.state.get().segment, demand.state.get().distance, next);
						if (minimumDistance > distance && next.value != null) {
							if (vehicle.state.get().batteryLevel >= distance + getMinimumStationDistance(demand.state.get().segment, demand.state.get().distance)) {
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
				double distance = getDistance(vehicle.state.get().segment, vehicle.state.get().distance, station.location.segment.get(), station.location.distance.get(), next);
				if (minimumDistance > distance && next.value != null) {
					minimumDistance = distance;
					minimumSegment = next.value;
				}
			}
		}
		
		if (minimumSegment == null) {
			// Select random segment
			
			List<Segment> candidates = vehicle.state.get().segment.end.outgoing;
			
			minimumSegment = candidates.get((int) (Math.random() * candidates.size()));
		}
		
		return minimumSegment;
	}
	
	class Reference<T> {
		public T value;
	}
	
	private double getDistance(Segment aSeg, double aDis, Segment bSeg, double bDis, Reference<Segment> next) {
		if (aSeg == bSeg && aDis < bDis) {
			return bDis - aDis;
		} else if (aSeg.end == bSeg.start) {
			if (next != null) {
				next.value = bSeg;
			}
			return aSeg.length.get() - aDis + bDis;
		} else {
			SingleSourcePaths<Intersection, Segment> paths = algorithm.getPaths(aSeg.end);
			GraphPath<Intersection, Segment> path = paths.getPath(bSeg.start);
			if (path.getLength() > 0) {
				if (next != null) {
					next.value = path.getEdgeList().get(0);
				}
				return aSeg.length.get() - aDis + path.getWeight() + bDis;
			} else {
				return Double.MAX_VALUE;
			}
		}
	}
	
	private double getMinimumStationDistance(Segment aSeg, double aDis) {
		double minimumDistance = Double.MAX_VALUE;
		for (Station station : model.stations) {
			if (station.location.segment.get() == aSeg && station.location.distance.get() == aDis) {
				return 0;
			} else {
				double distance = getDistance(aSeg, aDis, station.location.segment.get(), station.location.distance.get(), null);
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
