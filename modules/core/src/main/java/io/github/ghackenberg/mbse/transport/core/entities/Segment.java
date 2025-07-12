package io.github.ghackenberg.mbse.transport.core.entities;

import java.util.List;

import io.github.ghackenberg.mbse.transport.core.structures.Vector;
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
	
	public final DoubleProperty angleX = new SimpleDoubleProperty();
	public final DoubleProperty angleY = new SimpleDoubleProperty();
	public final DoubleProperty angleZ = new SimpleDoubleProperty();
	
	// Structures
	
	public final Vector center = new Vector();
	public final Vector tangent = new Vector();
	public final Vector tangentNormal = new Vector();
	
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

	public class Projection {
		public final double dot;
		public final double len;

		public Projection(double dot, double len) {
			this.dot = dot;
			this.len = len;
		}
	}

	public Projection project(double x, double y) {
		double sx = start.coordinate.x.get();
		double sy = start.coordinate.y.get();
		
		double tx = tangent.x.get();
		double ty = tangent.y.get();
		
		double dx = x - sx;
		double dy = y - sy;
		
		double dot = Math.min(length.get(), Math.max(0, tx * dx + ty * dy));
					
		double lx = sx + dot * tx;
		double ly = sy + dot * ty;
		
		double nx = x - lx;
		double ny = y - ly;

		double len = Math.sqrt(nx * nx + ny * ny);

		return new Projection(dot, len);
	}
	
	private void recompute() {
		recomputeCenter();
		recomputeLength();
		recomputeTangent();
		recomputeTangentNormal();
		recomputeAngles();
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
		double dx = end.coordinate.x.get() - start.coordinate.x.get();
		double dy = end.coordinate.y.get() - start.coordinate.y.get();
		double dz = end.coordinate.z.get() - start.coordinate.z.get();
		
		double len = length.get();
		
		tangent.x.set(dx / len);
		tangent.y.set(dy / len);
		tangent.z.set(dz / len);
	}
	
	private void recomputeTangentNormal() {
		double dx = end.coordinate.x.get() - start.coordinate.x.get();
		double dy = end.coordinate.y.get() - start.coordinate.y.get();
		
		double len = Math.sqrt(dx * dx + dy * dy);
		
		tangentNormal.x.set(-dy / len);
		tangentNormal.y.set(dx / len);
		tangentNormal.z.set(0);
	}
	
	private void recomputeAngles() {
		double dx = end.coordinate.x.get() - start.coordinate.x.get();
		double dy = end.coordinate.y.get() - start.coordinate.y.get();
		double dz = end.coordinate.z.get() - start.coordinate.z.get();

		angleX.set(recomputeAngle(dy, dz));
		angleY.set(recomputeAngle(dx, dz));
		angleZ.set(recomputeAngle(dx, dy));
	}
	
	private double recomputeAngle(double dx, double dy) {
		double len = Math.sqrt(dx * dx + dy * dy);
		
		dx /= len;
		dy /= len;
		
		return Math.atan2(dy, dx);
	}
	
	@Override
	public String toString() {
		return start.name.get() + "->" + end.name.get();
	}

}
