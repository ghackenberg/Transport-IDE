package io.github.ghackenberg.mbse.transport.simulator.exceptions;

import io.github.ghackenberg.mbse.transport.model.Vehicle;

public class InvalidSpeedException extends InvalidException {

	private static final long serialVersionUID = 903032198765762413L;
	
	public InvalidSpeedException(Vehicle vehicle, double speed) {
		super("Speed " + speed + " of vehicle " + vehicle.name + " on segment " + vehicle.location.segment + " is invalid");
	}

}
