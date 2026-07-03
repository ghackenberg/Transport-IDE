
package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Warehouse;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;

public abstract class WarehouseViewer extends EntityViewer<Warehouse> {
	
	public final Warehouse.State entityState = entity.state.get();
	
	public final BooleanProperty loading = new SimpleBooleanProperty(false);

	public WarehouseViewer(Model model, Warehouse entity) {
		super(model, entity);
		
		// TODO pick proper colors for loading state
		
		ObjectBinding<Color> colorSelected = Bindings.when(loading).then(Color.DIMGRAY).otherwise(Color.DIMGRAY);
		ObjectBinding<Color> colorNotSelected = Bindings.when(loading).then(Color.BLACK).otherwise(Color.BLACK);
		
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
