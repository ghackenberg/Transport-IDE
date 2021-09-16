package example.controller.implementations;

import java.util.List;

import javax.swing.JOptionPane;

import example.controller.Controller;
import example.model.Demand;
import example.model.Segment;
import example.model.Station;
import example.model.Vehicle;

public class ManualController implements Controller {
	
	@Override
	public boolean selectAssignment(Vehicle vehicle, Demand demand) {
		return JOptionPane.showConfirmDialog(null, "Should vehicle " + vehicle + " pick up " + demand + "?", "Pickup choice", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0;
	}

	@Override
	public boolean selectStation(Vehicle vehicle, Station station) {
		return JOptionPane.showConfirmDialog(null, "Should vehicle " + vehicle + " charge at " + station + "?", "Charge choice", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0;
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		return vehicle.initialSpeed; // Math.random() * vehicle.location.segment.speed;
	}

	@Override
	public double selectSpeedUpdateTimeout(Vehicle vehicle) {
		return Double.MAX_VALUE;
	}

	@Override
	public Segment selectSegment(Vehicle vehicle) {
		List<Segment> outgoing = vehicle.location.segment.end.outgoing;
		if (outgoing.size() == 1) {
			return outgoing.get(0);
		} else {
			int index = JOptionPane.showOptionDialog(null, "Which segment should vehicle " + vehicle + " take?", "Route choice", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, vehicle.location.segment.end.outgoing.toArray(), vehicle.location.segment.end.outgoing.get(0));
			if (index >= 0) {
				return outgoing.get(index);
			} else {
				return outgoing.get((int) Math.floor(Math.random() * outgoing.size()));
			}
		}
	}
	
	@Override
	public void reset() {
		
	}
	
	@Override
	public String toString() {
		return "Manual controller";
	}

}
