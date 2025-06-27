package io.github.ghackenberg.mbse.transport.fx.events;

import io.github.ghackenberg.mbse.transport.core.entities.Demand;

public class DemandEvent extends AbstractEvent<Demand> {

	private static final long serialVersionUID = 4236667120788993935L;
	
	public DemandEvent(Demand demand) {
		super(demand);
	}

}
