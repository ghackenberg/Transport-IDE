package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.structures.Vector;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;

public abstract class DemandViewer extends EntityViewer<Demand> {
	
	public final Demand.State entityState = entity.state.get();
	
	public final Location location;
	
	public final Vector start;
	public final Vector end;
	
	public final BooleanProperty overdue = new SimpleBooleanProperty(false);

	public DemandViewer(Model model, Demand entity) {
		super(model, entity);
		
		// Location
		
		if (entityState == null) {
			location = entity.pick.location;
		} else {
			location = new Location();
			location.segment.set(entityState.segment);
			location.distance.set(entityState.distance);
			location.lane.set(-1);
		}
		
		// Start
		
		start = location.coordinate;
		
		// End
		
		end = entity.drop.location.coordinate;
		
		// Color
		
		// TODO pick proper overdue colors
		
		ObjectBinding<Color> selectedColor = Bindings.when(overdue).then(Color.RED).otherwise(Color.GREEN);
		ObjectBinding<Color> notSelectedColor = Bindings.when(overdue).then(Color.PINK).otherwise(Color.LIMEGREEN);
		
		color = Bindings.when(selected).then(selectedColor).otherwise(notSelectedColor);
	}
	
	@Override
	public void update() {
		location.segment.set(entityState.segment);
		location.distance.set(entityState.distance);
		location.lane.set(entityState.lane);
		
		// Visible
		
		if (!entityState.done && modelState.time >= entity.pick.time.get()) {
			setVisible(true);
		} else {
			setVisible(false);
		}
		
		// Overdue

		if (!entityState.done && modelState.time > entity.drop.time.get()) {
			overdue.set(true);
		} else {
			overdue.set(false);
		}
	}

}
