package io.github.ghackenberg.mbse.transport.core.entities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class LocationTime {

	// Statische Eigenschaften (geparst)
	private final DoubleProperty time = new SimpleDoubleProperty();
	
	private final Location location;
	
	public LocationTime() {
		location = new Location();
	}
	
	public LocationTime(Location location, double time) {
		this.location = location;
		setTime(time);
	}
	
	public LocationTime(Segment segment, double distance, double time) {
		this.location = new Location(segment, distance);
		setTime(time);
	}
	
	public double getTime() {
		return time.get();
	}
	public void setTime(double value) {
		time.set(value);
	}
	public DoubleProperty timeProperty() {
		return time;
	}
	
	public Location getLocation() {
		return location;
	}
	
	@Override
	public String toString() {
		return location.toString() + "@" + time;
	}
	
}
