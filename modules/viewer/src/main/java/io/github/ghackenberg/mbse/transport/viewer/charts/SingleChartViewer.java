package example.viewer.charts;

import java.util.List;

import example.model.Model;
import example.simulator.Simulator;
import example.statistics.Statistics;
import example.viewer.ChartViewer;

public abstract class SingleChartViewer extends ChartViewer {

	protected List<Simulator> simulators;
	protected int index;
	
	protected Model model;
	protected Statistics statistics;

	public SingleChartViewer(List<Simulator> simulators, int index, String title, String categoryAxisLabel, String valueAxisLabel) {
		super(title, categoryAxisLabel, valueAxisLabel);
		
		this.simulators = simulators;
		this.index = index;
		
		this.model = simulators.get(index).getModel();
		this.statistics = simulators.get(index).getStatistics();
	}
	
	public SingleChartViewer(Model model, Statistics statistics, String title, String categoryAxisLabel, String valueAxisLabel) {
		super(title, categoryAxisLabel, valueAxisLabel);
		
		this.model = model;
		this.statistics = statistics;
	}

}
