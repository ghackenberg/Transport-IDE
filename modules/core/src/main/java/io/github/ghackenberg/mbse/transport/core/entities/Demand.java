package io.github.ghackenberg.mbse.transport.core.entities;

import io.github.ghackenberg.mbse.transport.core.structures.Location;
import io.github.ghackenberg.mbse.transport.core.structures.LocationTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Demand {

	public class State {
		
		public boolean done = false;
		
		public Vehicle vehicle = null;
		
		public Segment segment = pick.location.segment.get();
		
		public double distance = pick.location.distance.get();
		
	}
	
	public final ThreadLocal<State> state = new ThreadLocal<>();
	
	// Properties
	
	public final DoubleProperty size = new SimpleDoubleProperty();
	
	// Structures
	
	public final LocationTime pick;
	public final LocationTime drop;
	
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
	
	@Override
	public String toString( ) {
		return pick.toString() + " " + drop.toString();
	}
	
}
