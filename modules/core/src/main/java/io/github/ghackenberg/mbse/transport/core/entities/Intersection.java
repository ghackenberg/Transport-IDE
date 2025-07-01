package io.github.ghackenberg.mbse.transport.core.entities;

import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Intersection {
	
	// Properties
	
	public final StringProperty name = new SimpleStringProperty();
	public final DoubleProperty lanes = new SimpleDoubleProperty();
	
	// Structures
	
	public final Coordinate coordinate = new Coordinate();
	
	public final ObservableList<Segment> incoming = FXCollections.observableArrayList();
	public final ObservableList<Segment> outgoing = FXCollections.observableArrayList();
	
	// Constructors
	
	public Intersection() {
		final InvalidationListener laneListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				recompute();
			}
		};
		
		final ListChangeListener<Segment> listListener = new ListChangeListener<>() {
			@Override
			public void onChanged(Change<? extends Segment> c) {
				while (c.next()) {
					for (Segment segment : c.getRemoved()) {
						segment.lanes.removeListener(laneListener);
					}
					for (Segment segment : c.getAddedSubList()) {
						segment.lanes.addListener(laneListener);
					}
				}
				recompute();
			}
		};
		
		incoming.addListener(listListener);
		outgoing.addListener(listListener);
		
		recompute();
	}
	
	// Methods
	
	private void recompute() {
		double max = 1;
		
		for (Segment segment : incoming) {
			max = Math.max(max, segment.lanes.get());
		}
		for (Segment segment : outgoing) {
			max = Math.max(max, segment.lanes.get());
		}
		
		lanes.set(max);
	}
	
}
