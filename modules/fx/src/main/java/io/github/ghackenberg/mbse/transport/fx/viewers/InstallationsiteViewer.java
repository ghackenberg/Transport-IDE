
package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Installationsite;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;

public abstract class InstallationsiteViewer extends EntityViewer<Installationsite> {
	
	public final Installationsite.State entityState = entity.state.get();
	
	public final BooleanProperty loading = new SimpleBooleanProperty(false);

	public InstallationsiteViewer(Model model, Installationsite entity) {
		super(model, entity);
		
		// TODO pick proper colors for loading state
		
		ObjectBinding<Color> colorSelected = Bindings.when(loading).then(Color.CORAL).otherwise(Color.CORAL);
		ObjectBinding<Color> colorNotSelected = Bindings.when(loading).then(Color.ORANGE).otherwise(Color.ORANGE);
		
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
