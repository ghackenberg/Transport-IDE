package example.viewer.charts.single;

import java.awt.Color;
import java.util.List;

import example.model.Vehicle;
import example.simulator.Simulator;
import example.viewer.charts.SingleChartViewer;

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
