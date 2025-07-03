package io.github.ghackenberg.mbse.transport.fx.charts;

import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

public class VehicleBatteryChart extends StackedBarChart<String, Number> {
	
	private final Model model;
	
	private final List<Vehicle.State> states = new ArrayList<>();
	
	private final XYChart.Series<String, Number> a = new XYChart.Series<>();
	private final XYChart.Series<String, Number> b = new XYChart.Series<>();

	public VehicleBatteryChart(Model model) {
		super(new CategoryAxis(), new NumberAxis());
		
		this.model = model;
		
		setTitle("Vehicle batteries");
		setAnimated(false);
		setLegendVisible(false);
		setStyle("-fx-background-color: white;");
		
		getData().add(a);
		getData().add(b);
		
		for (int i = 0; i < model.vehicles.size(); i++) {
			Vehicle vehicle = model.vehicles.get(i);
			
			states.add(vehicle.state.get());
			
			a.getData().add(new XYChart.Data<>("V" + i, vehicle.initialBatteryLevel.get()));
			b.getData().add(new XYChart.Data<>("V" + i, vehicle.batteryCapacity.get() - vehicle.initialBatteryLevel.get()));
		}
	}
	
	public void update() {
		for (int i = 0; i < model.vehicles.size(); i++) {
			Vehicle vehicle = model.vehicles.get(i);
			
			Vehicle.State state = states.get(i);
			
			a.getData().get(i).setYValue(state.batteryLevel);
			b.getData().get(i).setYValue(vehicle.batteryCapacity.get() - state.batteryLevel);
		}
	}

}
