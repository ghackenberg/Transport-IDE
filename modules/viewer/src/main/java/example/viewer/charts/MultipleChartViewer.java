package example.viewer.charts;

import java.util.List;

import example.simulator.Simulator;
import example.viewer.ChartViewer;

public abstract class MultipleChartViewer extends ChartViewer {
	
	protected List<Simulator> simulators;

	public MultipleChartViewer(List<Simulator> simulators, String title, String categoryAxisLabel, String valueAxisLabel) {
		super(title, categoryAxisLabel, valueAxisLabel);
		
		this.simulators = simulators;
	}

}
