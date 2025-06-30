package io.github.ghackenberg.mbse.transport.core.controllers;

import java.util.List;

import javax.swing.JOptionPane;

import io.github.ghackenberg.mbse.transport.core.Controller;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class ManualController implements Controller {
	
	@Override
	public boolean selectDemand(Vehicle vehicle, Demand demand) {
		return JOptionPane.showConfirmDialog(null, "Should vehicle " + vehicle + " pick up " + demand + "?", "Pickup choice", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0;
	}

	@Override
	public boolean selectStation(Vehicle vehicle, Station station) {
		return JOptionPane.showConfirmDialog(null, "Should vehicle " + vehicle + " charge at " + station + "?", "Charge choice", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0;
	}
	
	@Override
	public boolean unselectStation(Vehicle vehicle) {
		return false;
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		return vehicle.initialSpeed.get(); // Math.random() * vehicle.location.segment.speed;
	}

	@Override
	public double selectMaximumSpeedSelectionTimeout(Vehicle vehicle) {
		return Double.MAX_VALUE;
	}

	@Override
	public double selectMaximumStationSelectionTimeout(Vehicle vehicle) {
		return Double.MAX_VALUE;
	}

	@Override
	public Segment selectSegment(Vehicle vehicle) {
		List<Segment> outgoing = vehicle.state.get().segment.end.outgoing;
		if (outgoing.size() == 1) {
			return outgoing.get(0);
		} else {
			int index = JOptionPane.showOptionDialog(null, "Which segment should vehicle " + vehicle + " take?", "Route choice", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, vehicle.state.get().segment.end.outgoing.toArray(), vehicle.state.get().segment.end.outgoing.get(0));
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
