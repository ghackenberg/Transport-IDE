package example.simulator.exceptions;

import java.util.List;

import example.model.Segment;
import example.model.Vehicle;

public class CollisionException extends InvalidException {
	
	private static final long serialVersionUID = -6040773048326874260L;

	public CollisionException(Segment segment, List<Vehicle> vehicles) {
		super("Collision on segment " + segment + " between vehicles " + vehicles);
	}

}
