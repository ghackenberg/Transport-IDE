package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import javafx.scene.paint.Color;

public abstract class DemandViewer extends EntityViewer<Demand> {
	
	// TODO (issue #17) add lane offset x,y properties
	
	public final Demand.State entityState = entity.state.get();
	
	public final Location location;
	
	public final Coordinate start;
	public final Coordinate end;

	public DemandViewer(Model model, Demand entity) {
		super(model, entity, Color.LIMEGREEN, Color.GREEN);
		
		// Location
		
		if (entityState == null) {
			location = entity.pick.location;
		} else {
			location = new Location();
			location.segment.set(entityState.segment);
			location.distance.set(entityState.distance);
		}
		
		// Start
		
		start = location.coordinate;
		
		// End
		
		end = entity.drop.location.coordinate;
	}
	
	@Override
	public void update() {
		location.segment.set(entityState.segment);
		location.distance.set(entityState.distance);
		
		// TODO (issue #17) recompute lane offset
		
		if (!entityState.done && modelState.time >= entity.pick.time.get()) {
			setVisible(true);
		} else {
			setVisible(false);
		}
	}

}
