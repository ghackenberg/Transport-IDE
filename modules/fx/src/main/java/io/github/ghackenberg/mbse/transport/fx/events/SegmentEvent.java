package io.github.ghackenberg.mbse.transport.fx.events;

import io.github.ghackenberg.mbse.transport.core.entities.Segment;

public class SegmentEvent extends AbstractEvent<Segment> {

	private static final long serialVersionUID = 4236667120788993935L;
	
	public SegmentEvent(Segment segment) {
		super(segment);
	}

}
