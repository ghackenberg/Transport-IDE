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
		double dx = end.getCoordinate().getX() - start.getCoordinate().getX();
		double dy = end.getCoordinate().getY() - start.getCoordinate().getY();
		double dz = end.getCoordinate().getZ() - start.getCoordinate().getZ();
		
		Coordinate coord = new Coordinate();
		
		coord.setX(start.getCoordinate().getX() + dx / 2);
		coord.setY(start.getCoordinate().getY() + dy / 2);
		coord.setZ(start.getCoordinate().getZ() + dz / 2);
		
		return coord;
	}
	
	public double getAngle() {
		double len = getLength();
		
		double dx = end.getCoordinate().getX() - start.getCoordinate().getX();
		double dy = end.getCoordinate().getY() - start.getCoordinate().getY();
		
		dx /= len;
		dy /= len;
		
		return Math.atan2(dy, dx);
	}
	
	public double getLength() {
		double dx = end.getCoordinate().getX() - start.getCoordinate().getX();
		double dy = end.getCoordinate().getY() - start.getCoordinate().getY();
		double dz = end.getCoordinate().getZ() - start.getCoordinate().getZ();
		
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	@Override
	public String toString() {
		return start.getName() + "->" + end.getName();
	}

}
