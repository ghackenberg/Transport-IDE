package io.github.ghackenberg.mbse.transport.fx.events;

import javafx.event.Event;
import javafx.event.EventType;

public class AbstractEvent<T> extends Event {

	private static final long serialVersionUID = 6595395175846649119L;
	
	private T entity;
	
	public AbstractEvent(T entity) {
		super(EventType.ROOT);
		
		this.entity = entity;
	}
	
	public T getEntity() {
		return entity;
	}

}
