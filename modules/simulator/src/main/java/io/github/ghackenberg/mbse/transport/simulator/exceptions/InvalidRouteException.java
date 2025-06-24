package io.github.ghackenberg.mbse.transport.simulator.exceptions;

import io.github.ghackenberg.mbse.transport.model.Segment;
import io.github.ghackenberg.mbse.transport.model.Vehicle;

public class InvalidRouteException extends InvalidException {

	private static final long serialVersionUID = 3091379040912756562L;

	public InvalidRouteException(Vehicle vehicle, Segment previous, Segment next) {
		super("Next segment " + next + " after previous segment " + previous + " of vehicle " + vehicle.name + " is invalid");
	}

}
