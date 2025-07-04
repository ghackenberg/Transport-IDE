package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;

public abstract class VehicleViewer extends EntityViewer<Vehicle> {
	
	public final Vehicle.State entityState = entity.state.get();
	
	public final Location location;
	
	public final DoubleProperty laneOffsetX = new SimpleDoubleProperty(0);
	public final DoubleProperty laneOffsetY = new SimpleDoubleProperty(0);

	public VehicleViewer(Model model, Vehicle entity) {
		super(model, entity, Color.DEEPSKYBLUE, Color.DODGERBLUE);
		
		// Location
		
		if (entityState == null) {
			location = entity.initialLocation;
		} else {
			location = new Location();
			location.segment.set(entityState.segment);
			location.distance.set(entityState.distance);
		}
	}

	@Override
	public void update() {
		location.segment.set(entityState.segment);
		location.distance.set(entityState.distance);

		// TODO (issue #15) compute lane offset from segment tanget normal multiplied by lane number
	}

}
