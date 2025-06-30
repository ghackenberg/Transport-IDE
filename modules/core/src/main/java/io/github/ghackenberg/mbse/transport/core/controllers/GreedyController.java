package io.github.ghackenberg.mbse.transport.core.controllers;

import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Controller;
import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class GreedyController implements Controller {
	
	private final Model model;
	
	public GreedyController(Model model) {
		this.model = model;
	}

	@Override
	public boolean selectDemand(Vehicle vehicle, Demand demand) {
		return true;
	}

	@Override
	public boolean selectStation(Vehicle vehicle, Station station) {
		return true;
	}
	
	@Override
	public boolean unselectStation(Vehicle vehicle) {
		return false;
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		return vehicle.location.segment.get().speed.get();
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
		// Try station
		if (vehicle.batteryLevel / vehicle.batteryCapacity.get() < 0.5) {
			List<Segment> charge = new ArrayList<>();
			
			for (Station station : model.stations)  {
				if (station.vehicle == null) {
					if (vehicle.location.segment.get().end.outgoing.contains(station.location.segment.get())) {
						charge.add(station.location.segment.get());
					}
				}
			}
			
			if (charge.size() > 0) {
				return charge.get((int) (Math.random() * charge.size()));
			}
		}
		
		// Try dropoff
		List<Segment> dropoff = new ArrayList<>();
		
		for (Demand demand : vehicle.demands) {
			if (vehicle.location.segment.get().end.outgoing.contains(demand.drop.location.segment.get())) {
				dropoff.add(demand.drop.location.segment.get());
			}
		}
		
		if (dropoff.size() > 0) {
			return dropoff.get((int) (Math.random() * dropoff.size()));
		}
		
		// Try pickup
		if (vehicle.demands.size() == 0) {
			List<Segment> pickup = new ArrayList<>();
			
			for (Demand demand : model.demands) {
				if (demand.done == false && demand.vehicle == null && demand.pick.time.get() <= model.time) {
					if (vehicle.location.segment.get().end.outgoing.contains(demand.location.segment.get())) {
						if (vehicle.loadLevel + demand.size.get() <= vehicle.loadCapacity.get()) {
							pickup.add(demand.location.segment.get());
						}
					}
				}
			}
			
			if (pickup.size() > 0) {
				return pickup.get((int) (Math.random() * pickup.size()));
			}
		}
		
		// Try random
		List<Segment> outgoing = vehicle.location.segment.get().end.outgoing;
		
		return outgoing.get((int) (Math.random() * outgoing.size()));
	}
	
	@Override
	public void reset() {
		
	}
	
	@Override
	public String toString() {
		return "Greedy controller";
	}

}
