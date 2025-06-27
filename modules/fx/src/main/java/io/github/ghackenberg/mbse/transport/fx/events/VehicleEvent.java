package io.github.ghackenberg.mbse.transport.fx.events;

import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class VehicleEvent extends AbstractEvent<Vehicle> {

	private static final long serialVersionUID = 4236667120788993935L;
	
	public VehicleEvent(Vehicle vehicle) {
		super(vehicle);
	}

}
