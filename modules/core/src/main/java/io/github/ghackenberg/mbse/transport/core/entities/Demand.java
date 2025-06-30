package io.github.ghackenberg.mbse.transport.core.entities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Demand {

	public Vehicle vehicle;
	public boolean done;
	
	// Properties
	
	public final DoubleProperty size = new SimpleDoubleProperty();
	
	// Structures
	
	public final LocationTime pick;
	public final LocationTime drop;
	
	public final Location location = new Location();
	
	// Constructors
	
	public Demand() {
		 pick = new LocationTime();
		 drop = new LocationTime();
	}
	
	public Demand(LocationTime pick, LocationTime drop, double size) {
		this.pick = pick;
		this.drop = drop;
		
		this.size.set(size);
	}
	
	public Demand(Location pickLoc, double pickTim, Location dropLoc, double dropTim, double size) {
		pick = new LocationTime(pickLoc, pickTim);
		drop = new LocationTime(dropLoc, dropTim);
		
		this.size.set(size);
	}
	
	public Demand(Segment pickSeg, double pickDis, double pickTim, Segment dropSeg, double dropDis, double dropTim, double size) {
		pick = new LocationTime(pickSeg, pickDis, pickTim);
		drop = new LocationTime(dropSeg, dropDis, dropTim);
		
		this.size.set(size);
	}
	
	// Methods
	
	public void reset() {
		vehicle = null;
		
		done = false;
		
		location.segment.set(pick.location.segment.get());
		location.distance.set(pick.location.distance.get());
	}
	
	@Override
	public String toString( ) {
		return location.toString() + " " + drop.toString();
	}
	
}
