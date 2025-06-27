package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
	
	// Dynamische Eigenschaften (simuliert)
	public double loadLevel;
	public double batteryLevel;
	public double speed;
	public int lane;
	public final Location location = new Location();
	public final List<Demand> demands = new ArrayList<>();
	public final List<Vehicle> collisions = new ArrayList<>();
	public Station station;
	
	// Statische Eigenschaften (geparst)
	public String name;
	public double length;
	public double loadCapacity;
	public double batteryCapacity;
	public double initialBatteryLevel;
	public double initialSpeed;
	public Location initialLocation;
	
	public void reset() {
		demands.clear();
		collisions.clear();
		loadLevel = 0;
		batteryLevel = initialBatteryLevel;
		speed = initialSpeed;
		lane = -1;
		location.setSegment(initialLocation.getSegment());
		location.setDistance(initialLocation.getDistance());
		station = null;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
