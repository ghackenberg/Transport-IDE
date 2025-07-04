package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;

public abstract class StationViewer extends EntityViewer<Station> {
	
	public final Station.State entityState = entity.state.get();

	public final DoubleProperty laneOffsetX = new SimpleDoubleProperty(0);
	public final DoubleProperty laneOffsetY = new SimpleDoubleProperty(0);

	public StationViewer(Model model, Station entity) {
		super(model, entity, Color.VIOLET, Color.DARKVIOLET);
	}

	@Override
	public void update() {
		
	}

}
