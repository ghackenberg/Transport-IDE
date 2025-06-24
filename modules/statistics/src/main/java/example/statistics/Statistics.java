package example.statistics;

import java.util.HashMap;
import java.util.Map;

import example.model.Demand;
import example.model.Intersection;
import example.model.Model;
import example.model.Segment;
import example.model.Station;
import example.model.Vehicle;

public class Statistics {
	
	private Model model;
	
	public Map<Vehicle, Double> vehicleDistances = new HashMap<>();
	public Map<Demand, Double> demandDistances = new HashMap<>();
	public Map<Demand, Map<Double, Vehicle>> demandPickupDeclineTimes = new HashMap<>();
	public Map<Demand, Map<Double, Vehicle>> demandPickupAcceptTimes = new HashMap<>();
	public Map<Demand, Map<Double, Vehicle>> demandDropoffTimes = new HashMap<>();
	public Map<Segment, Integer> segmentTraversals = new HashMap<>();
	public Map<Intersection, Integer> intersectionCrossings = new HashMap<>();
	public Map<Station, Map<Double, Vehicle>> chargeStartTimes = new HashMap<>();
	public Map<Station, Map<Double, Vehicle>> chargeEndTimes = new HashMap<>();
	
	public Statistics(Model model) {
		this.model = model;
	}

	public void recordCrossing(Vehicle vehicle, Segment previous, Segment next, double time) {
		// Update intersection crossings
		intersectionCrossings.put(previous.end, intersectionCrossings.get(previous.end) + 1);
		// Update segment traversals
		segmentTraversals.put(next, segmentTraversals.get(next) + 1);
	}

	public void recordPickupDecline(Vehicle vehicle, Demand demand, double time) {
		// Update demand pickup decline times
		demandPickupDeclineTimes.get(demand).put(time, vehicle);
	}
	
	public void recordPickupAccept(Vehicle vehicle, Demand demand, double time) {
		// Update demand pickup accept time
		demandPickupAcceptTimes.get(demand).put(time, vehicle);
	}

	public void recordDropoff(Vehicle vehicle, Demand demand, double time) {
		// Update demand dropoff time
		demandDropoffTimes.get(demand).put(time, vehicle);
	}

	public void recordSpeed(Vehicle vehicle, double speed, double time) {
		
	}

	public void recordDistance(Vehicle vehicle, double distance, double time) {
		// Update vehicle distance
		vehicleDistances.put(vehicle, vehicleDistances.get(vehicle) + distance);
		// Process vehicle demands
		vehicle.demands.forEach(demand -> {
			// Update demand distance
			demandDistances.put(demand, demandDistances.get(demand) + distance);
		});
	}

	public void recordChargeStart(Vehicle vehicle, Station station, double time) {
		// Update charge start times
		chargeStartTimes.get(station).put(time, vehicle);
	}

	public void recordChargeEnd(Vehicle vehicle, Station station, double time) {
		// Update charge end times
		chargeEndTimes.get(station).put(time, vehicle);
	}

	public void recordStep(double step, double time) {
		
	}
	
	public void reset() {
		vehicleDistances.clear();
		demandDistances.clear();
		demandPickupDeclineTimes.clear();
		demandPickupAcceptTimes.clear();
		demandDropoffTimes.clear();
		segmentTraversals.clear();
		intersectionCrossings.clear();
		chargeStartTimes.clear();
		chargeEndTimes.clear();
		
		// Process intersections
		model.intersections.forEach(intersection -> {
			// Initialize intersection crossings
			intersectionCrossings.put(intersection, 0);
		});
		// Process segments
		model.segments.forEach(segment -> {
			// Initialize segment traversals
			segmentTraversals.put(segment, 0);
		});
		// Process vehicles
		model.vehicles.forEach(vehicle -> {
			// Initialize vehicle distances
			vehicleDistances.put(vehicle, 0.0);
			// Update segment traversals
			segmentTraversals.put(vehicle.location.segment, segmentTraversals.get(vehicle.location.segment) + 1);
		});
		// Process demands
		model.demands.forEach(demand -> {
			// Initialize demand pickup decline times
			demandPickupDeclineTimes.put(demand, new HashMap<>());
			// Initialize demand pickup accept times
			demandPickupAcceptTimes.put(demand, new HashMap<>());
			// Initialize demand dropoff times
			demandDropoffTimes.put(demand, new HashMap<>());
			// Initialize demand distances
			demandDistances.put(demand, 0.0);
		});
		// Process stations
		model.stations.forEach(station -> {
			// Initialize charge start times
			chargeStartTimes.put(station, new HashMap<>());
			// Initialize charge end times 
			chargeEndTimes.put(station, new HashMap<>());
		});
	}
	
}
