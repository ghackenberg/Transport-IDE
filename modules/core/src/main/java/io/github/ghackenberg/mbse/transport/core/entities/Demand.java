package io.github.ghackenberg.mbse.transport.core.entities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Demand {

	// Dynamische Eigenschaften (simuliert)
	public Vehicle vehicle;
	public boolean done;
	
	private final Location location = new Location();
	
	// Statische Eigenschaften (geparst)
	private final DoubleProperty size = new SimpleDoubleProperty();
	
	private final LocationTime pickup;
	private final LocationTime dropoff;
	
	public Demand() {
		 pickup = new LocationTime();
		 dropoff = new LocationTime();
	}
	
	public Demand(LocationTime pick, LocationTime drop, double size) {
		pickup = pick;
		dropoff = drop;
		
		setSize(size);
	}
	
	public Demand(Location pickLoc, double pickTim, Location dropLoc, double dropTim, double size) {
		pickup = new LocationTime(pickLoc, pickTim);
		dropoff = new LocationTime(dropLoc, dropTim);
		
		setSize(size);
	}
	
	public Demand(Segment pickSeg, double pickDis, double pickTim, Segment dropSeg, double dropDis, double dropTim, double size) {
		pickup = new LocationTime(pickSeg, pickDis, pickTim);
		dropoff = new LocationTime(dropSeg, dropDis, dropTim);
		
		setSize(size);
	}
	
	public double getSize() {
		return size.get();
	}
	public void setSize(double value) {
		size.set(value);
	}
	public DoubleProperty sizeProperty() {
		return size;
	}
	
	public LocationTime getPickup() {
		return pickup;
	}
	public LocationTime getDropoff() {
		return dropoff;
	}
	public Location getLocation() {
		return location;
	}
	
	public void reset() {
		vehicle = null;
		
		done = false;
		
		location.setSegment(pickup.getLocation().getSegment());
		location.setDistance(pickup.getLocation().getDistance());
	}
	
	@Override
	public String toString( ) {
		return location.toString() + " " + dropoff.toString();
	}
	
}
