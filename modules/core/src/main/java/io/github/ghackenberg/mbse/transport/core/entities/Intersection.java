package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Intersection {
	
	// Statische Eigenschaften (geparst)
	private StringProperty name = new SimpleStringProperty();
	
	private Coordinate coordinate = new Coordinate();
	
	public List<Segment> incoming = new ArrayList<>();
	public List<Segment> outgoing = new ArrayList<>();
	
	public String getName() {
		return name.get();
	}
	public void setName(String value) {
		name.set(value);
	}
	public StringProperty nameProperty() {
		return name;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
}
