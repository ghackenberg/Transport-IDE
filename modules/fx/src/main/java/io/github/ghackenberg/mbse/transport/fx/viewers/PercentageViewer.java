package io.github.ghackenberg.mbse.transport.fx.viewers;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane;

public class PercentageViewer extends BorderPane {

	private List<String> names = new ArrayList<>();
	private List<Integer> counts = new ArrayList<>();
	
	private PieChart chart = new PieChart();
	
	public PercentageViewer(String title) {
		chart.setStyle("-fx-background-color: white;");
		chart.setTitle(title);
		chart.setAnimated(false);
		
		setCenter(chart);
	}
	
	public void increment(String name) {
		int i = names.indexOf(name);
		
		if (i == -1) {
			names.add(name);
			counts.add(0);
			
			i = names.size() - 1;
		}
		
		counts.set(i, counts.get(i) + 1);
	}
	
	public void update() {
		chart.getData().clear();
		
		for (int i = 0; i < names.size(); i++) {
			chart.getData().add(new PieChart.Data(names.get(i), counts.get(i)));
		}
	}
	
}
