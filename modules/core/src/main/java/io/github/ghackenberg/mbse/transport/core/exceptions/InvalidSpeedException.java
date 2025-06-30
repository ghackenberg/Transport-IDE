package io.github.ghackenberg.mbse.transport.core.exceptions;

import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class InvalidSpeedException extends InvalidException {

	private static final long serialVersionUID = 903032198765762413L;
	
	public InvalidSpeedException(Vehicle vehicle, double speed) {
		super("Speed " + speed + " of vehicle " + vehicle.name + " on segment " + vehicle.location.segment.get() + " is invalid");
	}

}
