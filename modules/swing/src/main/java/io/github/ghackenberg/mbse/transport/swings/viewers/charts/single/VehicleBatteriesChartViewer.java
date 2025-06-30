package io.github.ghackenberg.mbse.transport.swings.viewers.charts.single;

import java.awt.Color;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.SingleChartViewer;

public class VehicleBatteriesChartViewer extends SingleChartViewer {

	public VehicleBatteriesChartViewer(List<Simulator> simulators, int index) {
		super(simulators, index, "Vehicle batteries", "Vehicles", "Energy");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	public VehicleBatteriesChartViewer(Model model, Statistics statistics) {
		super(model, statistics, "Vehicle batteries", "Vehicles", "Energy");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	@Override
	public void update() {
		// Update values
		
		for (Vehicle vehicle : model.vehicles) {
			double batteryLevel = vehicle.batteryLevel;
			double batteryCapacity = vehicle.batteryCapacity.get();
			dataset.addValue(batteryLevel, "Battery level", vehicle.name.get());
			dataset.addValue(batteryCapacity - batteryLevel, "Battery capacity", vehicle.name.get());
		}
		
		// Update range
		
		double max = 0;
		
		for (Vehicle vehicle : model.vehicles) {
			max = Math.max(vehicle.batteryCapacity.get(), max);
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
