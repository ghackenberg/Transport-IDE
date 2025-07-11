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
	public final DoubleProperty lane = new SimpleDoubleProperty();
	
	public final DoubleProperty angleX = new SimpleDoubleProperty();
	public final DoubleProperty angleY = new SimpleDoubleProperty();
	public final DoubleProperty angleZ = new SimpleDoubleProperty();
	
	// Structures
	
	public final Vector coordinate = new Vector();
	
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
				// Change of start coordinate
				oldValue.start.coordinate.x.removeListener(listener);
				oldValue.start.coordinate.y.removeListener(listener);
				oldValue.start.coordinate.z.removeListener(listener);
				
				// Change of tangent
				oldValue.tangent.x.removeListener(listener);
				oldValue.tangent.y.removeListener(listener);
				oldValue.tangent.z.removeListener(listener);
				
				// Change of tangent normal
				oldValue.tangentNormal.x.removeListener(listener);
				oldValue.tangentNormal.y.removeListener(listener);
				oldValue.tangentNormal.z.removeListener(listener);
				
				// Change of lanes
				oldValue.lanes.removeListener(listener);
			}
			
			// New value
			
			if (newValue != null) {
				// Change of start coordinate
				newValue.start.coordinate.x.addListener(listener);
				newValue.start.coordinate.y.addListener(listener);
				newValue.start.coordinate.z.addListener(listener);
				
				// Change of tangent
				newValue.tangent.x.addListener(listener);
				newValue.tangent.y.addListener(listener);
				newValue.tangent.z.addListener(listener);
				
				// Change of tangentNormal
				newValue.tangentNormal.x.addListener(listener);
				newValue.tangentNormal.y.addListener(listener);
				newValue.tangentNormal.z.addListener(listener);
				
				// Change of lanes
				newValue.lanes.addListener(listener);
			}
			
			recompute();
		});
		
		// Change distance
		distance.addListener(listener);
		
		// Change lane
		lane.addListener(listener);
	}
	
	public Location(Segment segment, double distance, double lane) {
		this();
		
		this.segment.set(segment);
		this.distance.set(distance);
		this.lane.set(lane);
	}
	
	// Methods
	
	private void recompute() {
		if (segment.get() != null) {
			Segment segment = this.segment.get();
			
			Vector start = segment.start.coordinate;
			Vector tangent = segment.tangent;
			Vector tangentNormal = segment.tangentNormal;
			double lanes = segment.lanes.get();
			
			double distance = this.distance.get();
			double lane = this.lane.get();
			
			double sX = start.x.get();
			double sY = start.y.get();
			double sZ = start.z.get();
			
			double tX = tangent.x.get();
			double tY = tangent.y.get();
			double tZ = tangent.z.get();
			
			double tNX = tangentNormal.x.get();
			double tNY = tangentNormal.y.get();
			double tNZ = tangentNormal.z.get();
			
			double cx = sX + tX * distance;
			double cy = sY + tY * distance;
			double cz = sZ + tZ * distance;
			
			double x = cx + tNX * (lanes - 1) / 2 - lane * tNX;
			double y = cy + tNY * (lanes - 1) / 2 - lane * tNY;
			double z = cz + tNZ * (lanes - 1) / 2 - lane * tNZ;
			
			coordinate.x.set(x);
			coordinate.y.set(y);
			coordinate.z.set(z);

			angleX.set(segment.angleX.get());
			angleY.set(segment.angleY.get());
			angleZ.set(segment.angleZ.get());
		} else {
			coordinate.x.set(0);
			coordinate.y.set(0);
			coordinate.z.set(0);

			angleX.set(0);
			angleY.set(0);
			angleZ.set(0);
		}
	}
	
	@Override
	public String toString() {
		return segment.get() + ":" + Math.round(distance.get() / segment.get().length.get() * 100);
	}
	
}
