package io.github.ghackenberg.mbse.transport.fx.events;

import io.github.ghackenberg.mbse.transport.core.entities.Intersection;

public class IntersectionEvent extends AbstractEvent<Intersection> {

	private static final long serialVersionUID = 4236667120788993935L;
	
	public IntersectionEvent(Intersection intersection) {
		super(intersection);
	}

}
