package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Segment {

	public double load;
	public List<Vehicle> collisions;
	
	// Constants
	
	public final Intersection start;
	public final Intersection end;
	
	// Properties
	
	public final DoubleProperty lanes = new SimpleDoubleProperty();
	public final DoubleProperty speed = new SimpleDoubleProperty();
	
	public final DoubleProperty angle = new SimpleDoubleProperty();
	public final DoubleProperty length = new SimpleDoubleProperty();
	
	// Structures
	
	public final Coordinate center = new Coordinate();
	
	// Constructors
	
	public Segment(Intersection start, Intersection end) {
		this.start = start;
		this.end = end;
		
		final InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				recompute();	
			}
		};
		
		start.coordinate.x.addListener(listener);
		start.coordinate.y.addListener(listener);
		start.coordinate.z.addListener(listener);
		
		end.coordinate.x.addListener(listener);
		end.coordinate.y.addListener(listener);
		end.coordinate.z.addListener(listener);
	}
	
	// Methods
	
	private void recompute() {
		recomputeCenter();
		recomputeLength();
		recomputeAngle();
	}
	
	private void recomputeCenter() {
		double dx = end.coordinate.x.get() - start.coordinate.x.get();
		double dy = end.coordinate.y.get() - start.coordinate.y.get();
		double dz = end.coordinate.z.get() - start.coordinate.z.get();
		
		center.x.set(start.coordinate.x.get() + dx / 2);
		center.y.set(start.coordinate.y.get() + dy / 2);
		center.z.set(start.coordinate.z.get() + dz / 2);
	}
	
	private void recomputeAngle() {
		double len = length.get();
		
		double dx = end.coordinate.x.get() - start.coordinate.x.get();
		double dy = end.coordinate.y.get() - start.coordinate.y.get();
		
		dx /= len;
		dy /= len;
		
		angle.set(Math.atan2(dy, dx));
	}
	
	private void recomputeLength() {
		double dx = end.coordinate.x.get() - start.coordinate.x.get();
		double dy = end.coordinate.y.get() - start.coordinate.y.get();
		double dz = end.coordinate.z.get() - start.coordinate.z.get();
		
		length.set(Math.sqrt(dx * dx + dy * dy + dz * dz));
	}
	
	@Override
	public String toString() {
		return start.name.get() + "->" + end.name.get();
	}

}
