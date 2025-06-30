package io.github.ghackenberg.mbse.transport.core.entities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class LocationTime {
	
	// Properties
	
	public final DoubleProperty time = new SimpleDoubleProperty();
	
	// Structures
	
	public final Location location;
	
	// Constructors
	
	public LocationTime() {
		location = new Location();
	}
	
	public LocationTime(Location location, double time) {
		this.location = location;
		this.time.set(time);
	}
	
	public LocationTime(Segment segment, double distance, double time) {
		this.location = new Location(segment, distance);
		this.time.set(time);
	}
	
	// Methods
	
	@Override
	public String toString() {
		return location.toString() + "@" + time;
	}
	
}
