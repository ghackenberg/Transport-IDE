package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.structures.Location;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Vehicle {
	
	public class State {

		public Segment segment = initialLocation.segment.get();
		
		public double distance = initialLocation.distance.get();
		
		public int lane = -1;
		
		public double speed = initialSpeed.get();
		
		public double batteryLevel = initialBatteryLevel.get();
		
		public double loadLevel = 0;
		
		public Station station = null;
		
		public final List<Demand> demands = new ArrayList<>();
		
		public final List<Vehicle> collisions = new ArrayList<>();
		
	}
	
	public final ThreadLocal<State> state = new ThreadLocal<>();
	
	// Properties
	
	public final StringProperty name = new SimpleStringProperty();
	public final DoubleProperty length = new SimpleDoubleProperty();
	public final DoubleProperty loadCapacity = new SimpleDoubleProperty();
	public final DoubleProperty batteryCapacity = new SimpleDoubleProperty();
	public final DoubleProperty initialBatteryLevel = new SimpleDoubleProperty();
	public final DoubleProperty initialSpeed = new SimpleDoubleProperty();
	
	// Structures
	
	public final Location initialLocation = new Location();
	
	// Methods
	
	@Override
	public String toString() {
		return name.get();
	}
	
}
