package io.github.ghackenberg.mbse.transport.core.entities;

public class Station {
	
	// Dynamische Eigenschaften (simuliert)
	public Vehicle vehicle;

	// Statische Eigenschaften (geparst)
	public double speed;
	public Location location;
	
	public void reset() {
		vehicle = null;
	}
	
	@Override
	public String toString() {
		return location.toString();
	}

}
