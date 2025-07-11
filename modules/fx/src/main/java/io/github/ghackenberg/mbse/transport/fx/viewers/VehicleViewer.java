package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import javafx.scene.paint.Color;

public abstract class VehicleViewer extends EntityViewer<Vehicle> {
	
	public final Vehicle.State entityState = entity.state.get();
	
	public final Location location;

	public VehicleViewer(Model model, Vehicle entity) {
		super(model, entity, Color.DEEPSKYBLUE, Color.DODGERBLUE);
		
		// Location
		
		if (entityState == null) {
			location = entity.initialLocation;
		} else {
			location = new Location();
			location.segment.set(entityState.segment);
			location.distance.set(entityState.distance);
			location.lane.set(entityState.lane);
		}
	}

	@Override
	public void update() {
		location.segment.set(entityState.segment);
		location.distance.set(entityState.distance);
		location.lane.set(entityState.lane);
	}

}
