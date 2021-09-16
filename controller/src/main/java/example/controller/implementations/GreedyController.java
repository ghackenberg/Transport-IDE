package example.controller.implementations;

import java.util.ArrayList;
import java.util.List;

import example.controller.Controller;
import example.model.Demand;
import example.model.Model;
import example.model.Segment;
import example.model.Station;
import example.model.Vehicle;

public class GreedyController implements Controller {
	
	private final Model model;
	
	public GreedyController(Model model) {
		this.model = model;
	}

	@Override
	public boolean selectDemand(Vehicle vehicle, Demand demand) {
		return vehicle.demands.size() == 0;
	}

	@Override
	public boolean selectStation(Vehicle vehicle, Station station) {
		return Math.random() > vehicle.batteryLevel / vehicle.batteryCapacity;
	}
	
	@Override
	public boolean unselectStation(Vehicle vehicle) {
		return false;
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		return vehicle.location.segment.speed;
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
		if (vehicle.batteryLevel / vehicle.batteryCapacity < 0.5) {
			List<Segment> charge = new ArrayList<>();
			
			for (Station station : model.stations)  {
				if (station.vehicle == null) {
					if (vehicle.location.segment.end.outgoing.contains(station.location.segment)) {
						charge.add(station.location.segment);
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
			if (vehicle.location.segment.end.outgoing.contains(demand.dropoff.location.segment)) {
				dropoff.add(demand.dropoff.location.segment);
			}
		}
		
		if (dropoff.size() > 0) {
			return dropoff.get((int) (Math.random() * dropoff.size()));
		}
		
		// Try pickup
		if (vehicle.demands.size() == 0) {
			List<Segment> pickup = new ArrayList<>();
			
			for (Demand demand : model.demands) {
				if (demand.done == false && demand.vehicle == null && demand.pickup.time <= model.time) {
					if (vehicle.location.segment.end.outgoing.contains(demand.pickup.location.segment)) {
						if (vehicle.loadLevel + demand.size <= vehicle.loadCapacity) {
							pickup.add(demand.pickup.location.segment);
						}
					}
				}
			}
			
			if (pickup.size() > 0) {
				return pickup.get((int) (Math.random() * pickup.size()));
			}
		}
		
		// Try random
		List<Segment> outgoing = vehicle.location.segment.end.outgoing;
		
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
