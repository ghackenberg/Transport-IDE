package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.fx.events.AbstractEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;

public class EntityViewer<T, S extends AbstractEvent<T>> extends Group {

	private T entity;
	
	private EventHandler<S> onSelected;
	
	public EntityViewer(T entity) {
		this.entity = entity;
	}
	
	public T getEntity() {
		return entity;
	}
	
	public void setOnSelected(EventHandler<S> handler) {
		this.onSelected = handler;
	}
	
	protected EventHandler<S> getOnSelected() {
		return onSelected;
	}
	
}
