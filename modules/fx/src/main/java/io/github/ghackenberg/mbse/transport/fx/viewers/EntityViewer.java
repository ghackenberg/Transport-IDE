package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.fx.events.AbstractEvent;
import javafx.scene.Group;

public abstract class EntityViewer<T, S extends AbstractEvent<T>> extends Group {

	public final Model model;
	public final Model.State modelState;
	
	public final T entity;
	
	public EntityViewer(Model model, T entity) {
		this.model = model;
		this.modelState = model.state.get();
		
		this.entity = entity;
	}
	
	public abstract void update();
	
}
