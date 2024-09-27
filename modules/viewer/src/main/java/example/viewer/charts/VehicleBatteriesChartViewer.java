package example.viewer.charts;

import java.awt.Color;
import java.util.List;

import example.model.Model;
import example.model.Vehicle;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public class VehicleBatteriesChartViewer extends ChartViewer {

	public VehicleBatteriesChartViewer(Model model, ExampleStatistics statistics, List<ExampleStatistics> baseline) {
		super(model, statistics, baseline, "Vehicle batteries", "Vehicles", "Energy");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	@Override
	public void update() {
		// Update values
		
		for (Vehicle vehicle : model.vehicles) {
			double batteryLevel = vehicle.batteryLevel;
			double batteryCapacity = vehicle.batteryCapacity;
			dataset.addValue(batteryLevel, "Battery level", vehicle.name);
			dataset.addValue(batteryCapacity - batteryLevel, "Battery capacity", vehicle.name);
		}
		
		// Update range
		
		double max = 0;
		
		for (Vehicle vehicle : model.vehicles) {
			max = Math.max(vehicle.batteryCapacity, max);
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
