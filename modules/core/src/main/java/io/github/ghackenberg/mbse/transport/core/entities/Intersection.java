package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Intersection {
	
	// Properties
	
	public final StringProperty name = new SimpleStringProperty();
	
	// Structures
	
	public final Coordinate coordinate = new Coordinate();
	
	public final List<Segment> incoming = new ArrayList<>();
	public final List<Segment> outgoing = new ArrayList<>();
	
}
