package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;

public abstract class StationViewer extends EntityViewer<Station> {
	
	public final Station.State entityState = entity.state.get();
	
	public final BooleanProperty loading = new SimpleBooleanProperty(false);

	public StationViewer(Model model, Station entity) {
		super(model, entity);
		
		// TODO pick proper colors for loading state
		
		ObjectBinding<Color> colorSelected = Bindings.when(loading).then(Color.GREEN).otherwise(Color.VIOLET);
		ObjectBinding<Color> colorNotSelected = Bindings.when(loading).then(Color.RED).otherwise(Color.DARKVIOLET);
		
		color = Bindings.when(selected).then(colorSelected).otherwise(colorNotSelected);
	}

	@Override
	public void update() {
		if (entityState.vehicle != null) {
			loading.set(true);
		} else {
			loading.set(false);
		}
	}

}
