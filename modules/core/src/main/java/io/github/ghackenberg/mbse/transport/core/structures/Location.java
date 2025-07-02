package io.github.ghackenberg.mbse.transport.core.structures;

import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Location {
	
	// Properties
	
	public final ObjectProperty<Segment> segment = new SimpleObjectProperty<>();
	public final DoubleProperty distance = new SimpleDoubleProperty();
	public final DoubleProperty angle = new SimpleDoubleProperty();
	
	// Structures
	
	public final Coordinate coordinate = new Coordinate();
	
	// Constructors
	
	public Location() {
		final InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				recompute();
			}
		};
		
		// Change segment
		segment.addListener((observable, oldValue, newValue)-> {
			
			// Old value
			
			if (oldValue != null) {
				// Change of start intersection
				oldValue.start.coordinate.x.removeListener(listener);
				oldValue.start.coordinate.y.removeListener(listener);
				oldValue.start.coordinate.z.removeListener(listener);
				
				// Change of end intersection
				oldValue.tangent.x.removeListener(listener);
				oldValue.tangent.y.removeListener(listener);
				oldValue.tangent.z.removeListener(listener);
			}
			
			// New value
			
			if (newValue != null) {
				// Change coordinate of start intersection
				newValue.start.coordinate.x.addListener(listener);
				newValue.start.coordinate.y.addListener(listener);
				newValue.start.coordinate.z.addListener(listener);
				
				// Change coordinate of end intersection
				newValue.tangent.x.addListener(listener);
				newValue.tangent.y.addListener(listener);
				newValue.tangent.z.addListener(listener);
			}
			
			recompute();
		});
		// Change distance
		distance.addListener(listener);
	}
	
	public Location(Segment segment, double distance) {
		this();
		
		this.segment.set(segment);
		this.distance.set(distance);
	}
	
	// Methods
	
	private void recompute() {
		if (segment.get() != null) {
			Coordinate start = segment.get().start.coordinate;
			
			Coordinate tangent = segment.get().tangent;
			
			coordinate.x.set(start.x.get() + tangent.x.get() * distance.get());
			coordinate.y.set(start.y.get() + tangent.y.get() * distance.get());
			coordinate.z.set(start.z.get() + tangent.z.get() * distance.get());
			
			angle.set(segment.get().angle.get());
		} else {
			coordinate.x.set(0);
			coordinate.y.set(0);
			coordinate.z.set(0);
			
			angle.set(0);
		}
	}
	
	@Override
	public String toString() {
		return segment.get() + ":" + Math.round(distance.get() / segment.get().length.get() * 100);
	}
	
}
