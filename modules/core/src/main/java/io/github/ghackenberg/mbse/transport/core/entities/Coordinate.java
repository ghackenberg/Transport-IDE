package io.github.ghackenberg.mbse.transport.core.entities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Coordinate {
	
	// Statische Eigenschaften (geparst) oder dynamische Eigenschaften (simuliert)
	private DoubleProperty x = new SimpleDoubleProperty();
	private DoubleProperty y = new SimpleDoubleProperty();
	private DoubleProperty z = new SimpleDoubleProperty();
	
	public double getX() {
		return x.get();
	}
	public void setX(double value) {
		x.set(value);
	}
	public DoubleProperty xProperty() {
		return x;
	}
	
	public double getY() {
		return y.get();
	}
	public void setY(double value) {
		y.set(value);
	}
	public DoubleProperty yProperty() {
		return y;
	}
	
	public double getZ() {
		return z.get();
	}
	public void setZ(double value) {
		z.set(value);
	}
	public DoubleProperty zProperty() {
		return z;
	}
	
}
