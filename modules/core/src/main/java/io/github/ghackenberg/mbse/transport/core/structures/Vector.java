package io.github.ghackenberg.mbse.transport.core.structures;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

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
	
}
