package io.github.ghackenberg.mbse.transport.core.entities;

public class Demand {

	// Dynamische Eigenschaften (simuliert)
	public Vehicle vehicle;
	public boolean done;
	public LocationTime pickup;
	
	// Statische Eigenschaften (geparst)
	public LocationTime initialPickup;
	public LocationTime dropoff;
	public double size;
	
	public Demand() {
		
	}
	
	public Demand(LocationTime pickup, LocationTime dropoff, double size) {
		this.initialPickup = new LocationTime(pickup.location, pickup.time);
		this.pickup = pickup;
		this.dropoff = dropoff;
		this.size = size;
	}
	
	public Demand(Segment pickupSegment, double pickupDistance, double pickupTime, Segment dropoffSegment, double dropoffDistance, double dropoffTime, double size) {
		this.initialPickup = new LocationTime(pickupSegment, pickupDistance, pickupTime);
		this.pickup = new LocationTime(pickupSegment, pickupDistance, pickupTime);
		this.dropoff = new LocationTime(dropoffSegment, dropoffDistance, dropoffTime);
		this.size = size;
	}
	
	public void reset() {
		vehicle = null;
		
		done = false;
		
		pickup.location.segment = initialPickup.location.segment;
		pickup.location.distance = initialPickup.location.distance;
	}
	
	@Override
	public String toString( ) {
		return pickup.toString() + " " + dropoff.toString();
	}
	
}
