package io.github.ghackenberg.mbse.transport.fx.charts;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

public class IntersectionCrossingChart extends StackedBarChart<String, Number> {
	
	private final Model model;
	private final Statistics statistics;
	
	private final XYChart.Series<String, Number> a = new XYChart.Series<>();

	public IntersectionCrossingChart(Model model, Statistics statistics) {
		super(new CategoryAxis(), new NumberAxis());
		
		this.model = model;
		this.statistics = statistics;
		
		setTitle("Intersection crossings");
		setAnimated(false);
		setLegendVisible(false);
		setStyle("-fx-background-color: white;");
		
		getData().add(a);
		
		for (int i = 0; i < model.intersections.size(); i++) {
			a.getData().add(new XYChart.Data<>("I" + i, 0));
		}
	}
	
	public void update() {
		for (int i = 0; i < model.intersections.size(); i++) {
			Intersection intersection = model.intersections.get(i);
			
			a.getData().get(i).setYValue(statistics.intersectionCrossings.get(intersection));
		}
	}

}
