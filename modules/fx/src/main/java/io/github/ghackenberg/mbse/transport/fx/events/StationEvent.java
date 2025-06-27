package io.github.ghackenberg.mbse.transport.fx.events;

import io.github.ghackenberg.mbse.transport.core.entities.Station;

public class StationEvent extends AbstractEvent<Station> {

	private static final long serialVersionUID = 4236667120788993935L;
	
	public StationEvent(Station station) {
		super(station);
	}

}
