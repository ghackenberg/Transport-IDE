package io.github.ghackenberg.mbse.transport.core.entities;

import io.github.ghackenberg.mbse.transport.core.structures.Location;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Station {
	
	public class State {
		
		public Vehicle vehicle = null;
		
	}
	
	public final ThreadLocal<State> state = new ThreadLocal<>();

	// Properties
	
	public final DoubleProperty speed = new SimpleDoubleProperty();
	
	// Structures
	
	public final Location location = new Location();
	
	// Methods
	
	@Override
	public String toString() {
		return location.toString();
	}

}
