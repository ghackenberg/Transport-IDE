package io.github.ghackenberg.mbse.transport.core;

import java.util.HashMap;
import java.util.Map;

import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class Statistics {
	
	private Model model;
	
	public Map<Vehicle, Double> vehicleDistances = new HashMap<>();
	public Map<Demand, Double> demandDistances = new HashMap<>();
	public Map<Demand, Map<Double, Vehicle>> demandPickDeclineTimes = new HashMap<>();
	public Map<Demand, Map<Double, Vehicle>> demandPickAcceptTimes = new HashMap<>();
	public Map<Demand, Map<Double, Vehicle>> demandDropTimes = new HashMap<>();
	public Map<Segment, Integer> segmentTraversals = new HashMap<>();
	public Map<Intersection, Integer> intersectionCrossings = new HashMap<>();
	public Map<Station, Map<Double, Vehicle>> stationVehicleChargeStartTimes = new HashMap<>();
	public Map<Station, Map<Double, Vehicle>> stationVehicleCargeEndTimes = new HashMap<>();
	public Map<Vehicle, Double> vehicleChargeAmounts = new HashMap<>();
	public Map<Station, Double> stationChargeAmounts = new HashMap<>();
	
	public Statistics(Model model) {
		this.model = model;
	}

	public void recordVehicleIntersectionCrossing(Vehicle vehicle, Segment previous, Segment next, double time) {
		// Update intersection crossings
		intersectionCrossings.put(previous.end, intersectionCrossings.get(previous.end) + 1);
		// Update segment traversals
		segmentTraversals.put(next, segmentTraversals.get(next) + 1);
	}

	public void recordVehicleDemandPickDecline(Vehicle vehicle, Demand demand, double time) {
		// Update demand pickup decline times
		demandPickDeclineTimes.get(demand).put(time, vehicle);
	}
	
	public void recordVehicleDemandPickAccept(Vehicle vehicle, Demand demand, double time) {
		// Update demand pickup accept time
		demandPickAcceptTimes.get(demand).put(time, vehicle);
	}

	public void recordVehicleDemandDrop(Vehicle vehicle, Demand demand, double time) {
		// Update demand dropoff time
		demandDropTimes.get(demand).put(time, vehicle);
	}

	public void recordVehicleSpeed(Vehicle vehicle, double speed, double time) {
		
	}

	public void recordVehicleAndDemandDistance(Vehicle vehicle, double distance, double time) {
		// Update vehicle distance
		vehicleDistances.put(vehicle, vehicleDistances.get(vehicle) + distance);
		// Process vehicle demands
		vehicle.state.get().demands.forEach(demand -> {
			// Update demand distance
			demandDistances.put(demand, demandDistances.get(demand) + distance);
		});
	}

	public void recordVehicleStationChargeStart(Vehicle vehicle, Station station, double time) {
		// Update charge start times
		stationVehicleChargeStartTimes.get(station).put(time, vehicle);
	}

	public void recordVehicleStationChargeEnd(Vehicle vehicle, Station station, double time) {
		// Update charge end times
		stationVehicleCargeEndTimes.get(station).put(time, vehicle);
	}
	
	public void recordVehicleCharge(Vehicle vehicle, double amount) {
		vehicleChargeAmounts.put(vehicle, vehicleChargeAmounts.get(vehicle) + amount);
	}
	
	public void recordStationCharge(Station station, double amount) {
		stationChargeAmounts.put(station, stationChargeAmounts.get(station) + amount);
	}

	public void recordStep(double step, double time) {
		
	}
	
	public void reset() {
		intersectionCrossings.clear();
		
		segmentTraversals.clear();
		
		vehicleDistances.clear();
		vehicleChargeAmounts.clear();
		
		demandDistances.clear();
		demandPickDeclineTimes.clear();
		demandPickAcceptTimes.clear();
		demandDropTimes.clear();
		
		stationVehicleChargeStartTimes.clear();
		stationVehicleCargeEndTimes.clear();
		stationChargeAmounts.clear();
		
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
			// Update segment traversals
			segmentTraversals.put(vehicle.state.get().segment, segmentTraversals.get(vehicle.state.get().segment) + 1);
			// Initialize vehicle distances
			vehicleDistances.put(vehicle, 0.0);
			// Initialize charge amounts
			vehicleChargeAmounts.put(vehicle, 0.0);
		});
		
		// Process demands
		model.demands.forEach(demand -> {
			// Initialize demand pickup decline times
			demandPickDeclineTimes.put(demand, new HashMap<>());
			// Initialize demand pickup accept times
			demandPickAcceptTimes.put(demand, new HashMap<>());
			// Initialize demand dropoff times
			demandDropTimes.put(demand, new HashMap<>());
			// Initialize demand distances
			demandDistances.put(demand, 0.0);
		});
		
		// Process stations
		model.stations.forEach(station -> {
			// Initialize charge start times
			stationVehicleChargeStartTimes.put(station, new HashMap<>());
			// Initialize charge end times 
			stationVehicleCargeEndTimes.put(station, new HashMap<>());
			// Initialize charge amounts
			stationChargeAmounts.put(station, 0.0);
		});
	}
	
}
