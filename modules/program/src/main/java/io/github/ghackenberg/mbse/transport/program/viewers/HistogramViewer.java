package io.github.ghackenberg.mbse.transport.program.viewers;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

public class HistogramViewer extends BorderPane {
	
	private List<String> names = new ArrayList<>();
	private List<List<Double>> values = new ArrayList<>();
	
	private final int binCount = 20;
	
	private final double[] binX = new double[binCount];
	private final double[] binY = new double[binCount];
	
	private CategoryAxis xAxis = new CategoryAxis();
	private NumberAxis yAxis = new NumberAxis();
	
	private AreaChart<String, Number> chart = new AreaChart<String, Number>(xAxis, yAxis);

	public HistogramViewer(String title, String label) {
		xAxis.setLabel(label);
		
		yAxis.setLabel("Probability (in %)");
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(0);
		yAxis.setUpperBound(1);
		
		chart.setStyle("-fx-background-color: white;");
		chart.setTitle(title);
		chart.setAnimated(false);
		chart.setCreateSymbols(false);
		
		setCenter(chart);
	}
	
	public void add(String name, double value) {
		int i = names.indexOf(name);
		
		if (i == -1) {
			names.add(name);
			values.add(new ArrayList<>());
			i = names.size() - 1;
		}
		
		values.get(i).add(value);
	}
	
	public double getMin() {
		double min = +Double.MAX_VALUE;
		
		for (int i = 0; i < names.size(); i++) {
			for (double value : values.get(i)) {
				min = Math.min(value - 1, min);
			}
		}
		
		return min;
	}
	
	public double getMax() {
		double max = -Double.MAX_VALUE;
		
		for (int i = 0; i < names.size(); i++) {
			for (double value : values.get(i)) {
				max = Math.max(value + 1, max);
			}
		}
		
		return max;
	}
	
	public void update() {
		update(getMin(), getMax());
	}
	
	public void update(double min, double max) {
		chart.getData().clear();
		
		computeBinX(min, max);
		
		for (int i = 0; i < names.size(); i++) {
			computeBinY(i, min, max);
			
			chart.getData().add(createSeries(i));
		}
	}
	
	private void computeBinX(double min, double max) {
		double width = (max - min) / binCount;
		
		for (int bin = 0; bin < binCount; bin++) {
			double x = min + width / 2 + bin * width;
			
			binX[bin] = x;
		}
	}
	
	private void computeBinY(int i, double min, double max) {
		List<Double> data = values.get(i);
		
		for (int bin = 0; bin < binCount; bin++) {
			binY[bin] = 0;
		}
		
		double width = max - min;
		
		for (double item : data) {
			double bin = (item - min) / width * binCount;
			
			binY[Math.min((int) bin, binCount - 1)]++;
		}
		
		if (data.size() > 0) {
			for (int bin = 0; bin < binCount; bin++) {
				binY[bin] /= data.size();
			}	
		}
	}
	
	private XYChart.Series<String, Number> createSeries(int i) {
		String name = names.get(i);
		
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		
		series.setName(name);
		
		for (int bin = 0; bin < binCount; bin++) {
			series.getData().add(new XYChart.Data<String, Number>("" + Math.round(binX[bin]), binY[bin]));
		}
		
		return series;
	}

}
