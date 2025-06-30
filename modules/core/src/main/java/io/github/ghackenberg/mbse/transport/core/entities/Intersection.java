package io.github.ghackenberg.mbse.transport.core.entities;

import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Intersection {
	
	// Properties
	
	public final StringProperty name = new SimpleStringProperty();
	
	// Structures
	
	public final Coordinate coordinate = new Coordinate();
	
	public final ObservableList<Segment> incoming = FXCollections.observableArrayList();
	public final ObservableList<Segment> outgoing = FXCollections.observableArrayList();
	
}
