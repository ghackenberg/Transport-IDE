package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Segment {

	// Dynamische Eigenschaften (simuliert)
	public double load;
	public List<Vehicle> collisions;
	
	// Statische Eigenschaften (geparst)
	public Intersection start;
	public Intersection end;
	
	private final DoubleProperty lanes = new SimpleDoubleProperty();
	private final DoubleProperty speed = new SimpleDoubleProperty();
	
	public double getLanes() {
		return lanes.get();
	}
	public void setLanes(double value) {
		lanes.set(value);
	}
	public DoubleProperty lanesProperty() {
		return lanes;
	}
	
	public double getSpeed() {
		return speed.get();
	}
	public void setSpeed(double value) {
		speed.set(value);
	}
	public DoubleProperty speedProperty() {
		return speed;
	}
	
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
