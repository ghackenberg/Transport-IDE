package io.github.ghackenberg.mbse.transport.core.entities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Location {

	// Statische Eigenschaften (geparst) oder dynamische Eigenschaften (simuliert)
	
	private ObjectProperty<Segment> segment = new SimpleObjectProperty<>();
	private DoubleProperty distance = new SimpleDoubleProperty();
	
	// Berechnete Eigenschaften
	
	private Coordinate coordinate = new Coordinate();
	
	public Location() {
		segment.addListener((observable, oldValue, newValue)-> {
			
			newValue.start.getCoordinate().xProperty().addListener(e -> {
				recomputeCoordinate();
			});
			newValue.start.getCoordinate().yProperty().addListener(e -> {
				recomputeCoordinate();
			});
			newValue.start.getCoordinate().zProperty().addListener(e -> {
				recomputeCoordinate();
			});
			
			newValue.end.getCoordinate().xProperty().addListener(e -> {
				recomputeCoordinate();
			});
			newValue.end.getCoordinate().yProperty().addListener(e -> {
				recomputeCoordinate();
			});
			newValue.end.getCoordinate().zProperty().addListener(e -> {
				recomputeCoordinate();
			});
			
			recomputeCoordinate();
		});
		distance.addListener(event -> {
			recomputeCoordinate();
		});
	}
	
	public Location(Segment segment, double distance) {
		this();
		this.setSegment(segment);
		this.setDistance(distance);
	}
	
	public Segment getSegment() {
		return segment.get();
	}
	public void setSegment(Segment value) {
		segment.set(value);
	}
	public ObjectProperty<Segment> segmentProperty() {
		return segment;
	}
	
	public double getDistance() {
		return distance.get();
	}
	public void setDistance(double value) {
		distance.set(value);
	}
	public DoubleProperty distanceProperty() {
		return distance;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	private void recomputeCoordinate() {
		
		Coordinate start = getSegment().start.getCoordinate();
		Coordinate end = getSegment().end.getCoordinate();
		
		double len = getSegment().getLength();
		double prg = getDistance() / len;
		
		coordinate.setX(start.getX() + (end.getX() - start.getX()) * prg);
		coordinate.setY(start.getY() + (end.getY() - start.getY()) * prg);
		coordinate.setZ(start.getZ() + (end.getZ() - start.getZ()) * prg);
		
	}
	
	@Override
	public String toString() {
		return getSegment() + ":" + Math.round(getDistance() / getSegment().getLength() * 100);
	}
	
}
