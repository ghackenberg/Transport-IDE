package io.github.ghackenberg.mbse.transport.swings.viewers.charts.single;

import java.awt.Color;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.SingleChartViewer;

public class VehicleDistancesChartViewer extends SingleChartViewer {

	public VehicleDistancesChartViewer(List<Simulator> simulators, int index) {
		super(simulators, index, "Vehicle distances", "Vehicles", "Distance");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	@Override
	public void update() {
		// Update values
		
		for (Vehicle vehicle : model.vehicles) {
			double distance = statistics.vehicleDistances.get(vehicle);
			dataset.addValue(distance, "Distances", vehicle.name);
		}
		
		// Update range
		
		double max = 0;
		
		for (Simulator simulator : simulators) {
			for (double distance : simulator.getStatistics().vehicleDistances.values()) {
				max = Math.max(distance, max);
			}
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
