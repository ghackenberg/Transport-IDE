package io.github.ghackenberg.mbse.transport.core.entities;

public class LocationTime {

	// Statische Eigenschaften (geparst)
	public Location location;
	public double time;
	
	public LocationTime() {
		
	}
	
	public LocationTime(Location location, double time) {
		this.location = location;
		this.time = time;
	}
	
	public LocationTime(Segment segment, double distance, double time) {
		this.location = new Location(segment, distance);
		this.time = time;
	}
	
	@Override
	public String toString() {
		return location.toString() + "@" + time;
	}
	
}
