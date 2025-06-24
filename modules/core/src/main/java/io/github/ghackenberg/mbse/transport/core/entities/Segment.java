package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.List;

public class Segment {

	// Dynamische Eigenschaften (simuliert)
	public double load;
	public List<Vehicle> collisions;
	
	// Statische Eigenschaften (geparst)
	public Intersection start;
	public Intersection end;
	public double lanes;
	public double speed;
	
	public Coordinate getCenter() {
		double dx = end.coordinate.latitude - start.coordinate.latitude;
		double dy = end.coordinate.longitude - start.coordinate.longitude;
		double dz = end.coordinate.elevation - start.coordinate.elevation;
		
		Coordinate coord = new Coordinate();
		
		coord.latitude = start.coordinate.latitude + dx / 2;
		coord.longitude = start.coordinate.longitude + dy / 2;
		coord.elevation = start.coordinate.elevation + dz / 2;
		
		return coord;
	}
	
	public double getAngle() {
		double len = getLength();
		
		double dx = end.coordinate.latitude - start.coordinate.latitude;
		double dy = end.coordinate.longitude - start.coordinate.longitude;
		
		dx /= len;
		dy /= len;
		
		return Math.atan2(dy, dx);
	}
	
	public double getLength() {
		double dx = end.coordinate.latitude - start.coordinate.latitude;
		double dy = end.coordinate.longitude - start.coordinate.longitude;
		double dz = end.coordinate.elevation - start.coordinate.elevation;
		
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	@Override
	public String toString() {
		return start.name + "->" + end.name;
	}

}
