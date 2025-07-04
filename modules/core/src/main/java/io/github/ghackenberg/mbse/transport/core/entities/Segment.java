package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.List;

import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Segment {

	public class State {
		
		public double load = 0;
		
		public List<Vehicle> collisions = null;
		
	}
	
	public final ThreadLocal<State> state = new ThreadLocal<>();
	
	// Constants
	
	public final Intersection start;
	public final Intersection end;
	
	// Properties
	
	public final DoubleProperty lanes = new SimpleDoubleProperty(1);
	public final DoubleProperty speed = new SimpleDoubleProperty(50);

	public final DoubleProperty length = new SimpleDoubleProperty();
	public final DoubleProperty angle = new SimpleDoubleProperty();
	
	// Structures
	
	public final Coordinate center = new Coordinate();
	public final Coordinate tangent = new Coordinate();
	public final Coordinate tangentNormal = new Coordinate();
	
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
		
		recompute();
	}
	
	// Methods
	
	private void recompute() {
		recomputeCenter();
		recomputeLength();
		recomputeTangent();
		recomputeTangentNormal();
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
	
	private void recomputeLength() {
		double dx = end.coordinate.x.get() - start.coordinate.x.get();
		double dy = end.coordinate.y.get() - start.coordinate.y.get();
		double dz = end.coordinate.z.get() - start.coordinate.z.get();
		
		length.set(Math.sqrt(dx * dx + dy * dy + dz * dz));
	}
	
	private void recomputeTangent() {
		double len = length.get();
		
		double dx = end.coordinate.x.get() - start.coordinate.x.get();
		double dy = end.coordinate.y.get() - start.coordinate.y.get();
		double dz = end.coordinate.z.get() - start.coordinate.z.get();
		
		tangent.x.set(dx / len);
		tangent.y.set(dy / len);
		tangent.z.set(dz / len);
	}
	
	private void recomputeTangentNormal() {
		// TODO (issue #15, #16, #17) recompute normal of tangent! reduce to x/y-normal! do not consider z!
	}
	
	private void recomputeAngle() {
		double len = length.get();
		
		double dx = end.coordinate.x.get() - start.coordinate.x.get();
		double dy = end.coordinate.y.get() - start.coordinate.y.get();
		
		dx /= len;
		dy /= len;
		
		angle.set(Math.atan2(dy, dx));
	}
	
	@Override
	public String toString() {
		return start.name.get() + "->" + end.name.get();
	}

}
