package io.github.ghackenberg.mbse.transport.core.structures;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;

public class Vector {
	
	// Properties
	
	public final DoubleProperty x = new SimpleDoubleProperty();
	public final DoubleProperty y = new SimpleDoubleProperty();
	public final DoubleProperty z = new SimpleDoubleProperty();
	
	// Constructors
	
	public Vector() {
		this(0, 0, 0);
	}
	
	public Vector(double x, double y, double z) {
		this.x.set(x);
		this.y.set(y);
		this.z.set(z);
	}

	// Methods

	public Point3D toPoint3D() {
		return new Point3D(x.get(), y.get(), z.get());
	}

	public void assign(Point3D point) {
		x.set(point.getX());
		y.set(point.getY());
		z.set(point.getZ());
	}
	
}
