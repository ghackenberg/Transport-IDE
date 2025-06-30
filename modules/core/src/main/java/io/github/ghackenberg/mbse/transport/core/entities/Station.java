package io.github.ghackenberg.mbse.transport.core.entities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Station {
	
	public Vehicle vehicle;

	// Properties
	
	public final DoubleProperty speed = new SimpleDoubleProperty();
	
	// Structures
	
	public final Location location = new Location();
	
	// Methods
	
	public void reset() {
		vehicle = null;
	}
	
	@Override
	public String toString() {
		return location.toString();
	}

}
