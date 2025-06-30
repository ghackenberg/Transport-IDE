package io.github.ghackenberg.mbse.transport.core.controllers;

import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Controller;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class RandomController implements Controller {

	@Override
	public boolean selectDemand(Vehicle vehicle, Demand demand) {
		return Math.random() > 0.5;
	}

	@Override
	public boolean selectStation(Vehicle vehicle, Station station) {
		return Math.random() > 0.5;
	}
	
	@Override
	public boolean unselectStation(Vehicle vehicle) {
		return false;
	}

	@Override
	public double selectSpeed(Vehicle vehicle) {
		return vehicle.location.segment.get().speed.get();
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
		List<Segment> outgoing = vehicle.location.segment.get().end.outgoing;
		return outgoing.get((int) (Math.random() * outgoing.size()));
	}
	
	@Override
	public void reset() {
		
	}
	
	@Override
	public String toString() {
		return "Random controller";
	}

}
