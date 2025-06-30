package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Vehicle {
	
	public double loadLevel;
	public double batteryLevel;
	public double speed;
	public int lane;
	public Station station;
	
	// Properties
	
	public final StringProperty name = new SimpleStringProperty();
	public final DoubleProperty length = new SimpleDoubleProperty();
	public final DoubleProperty loadCapacity = new SimpleDoubleProperty();
	public final DoubleProperty batteryCapacity = new SimpleDoubleProperty();
	public final DoubleProperty initialBatteryLevel = new SimpleDoubleProperty();
	public final DoubleProperty initialSpeed = new SimpleDoubleProperty();
	
	// Structures
	
	public final Location initialLocation = new Location();
	public final Location location = new Location();
	
	public final List<Demand> demands = new ArrayList<>();
	public final List<Vehicle> collisions = new ArrayList<>();
	
	// Methods
	
	public void reset() {
		demands.clear();
		collisions.clear();
		loadLevel = 0;
		batteryLevel = initialBatteryLevel.get();
		speed = initialSpeed.get();
		lane = -1;
		location.segment.set(initialLocation.segment.get());
		location.distance.set(initialLocation.distance.get());
		station = null;
	}
	
	@Override
	public String toString() {
		return name.get();
	}
	
}
