package example.simulator.exceptions;

import example.model.Vehicle;

public class InvalidTimeoutException extends InvalidException {

	private static final long serialVersionUID = 903032198765762413L;
	
	public InvalidTimeoutException(Vehicle vehicle, double timeout) {
		super("Speed timeout " + timeout + " of vehicle " + vehicle.name + " is invalid");
	}

}
