package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.fx.events.AbstractEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;

public abstract class EntityViewer<T, S extends AbstractEvent<T>> extends Group {

	protected final Model model;
	protected final Model.State modelState;
	
	protected final T entity;
	
	private EventHandler<S> onSelected;
	
	public EntityViewer(Model model, T entity) {
		this.model = model;
		this.modelState = model.state.get();
		
		this.entity = entity;
	}
	
	public void setOnSelected(EventHandler<S> handler) {
		this.onSelected = handler;
	}
	
	protected EventHandler<S> getOnSelected() {
		return onSelected;
	}
	
	public abstract void update();
	
}
