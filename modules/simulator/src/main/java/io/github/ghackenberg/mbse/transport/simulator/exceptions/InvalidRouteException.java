package example.simulator.exceptions;

import example.model.Segment;
import example.model.Vehicle;

public class InvalidRouteException extends InvalidException {

	private static final long serialVersionUID = 3091379040912756562L;

	public InvalidRouteException(Vehicle vehicle, Segment previous, Segment next) {
		super("Next segment " + next + " after previous segment " + previous + " of vehicle " + vehicle.name + " is invalid");
	}

}
