package example.viewer.charts;

import java.util.List;

import example.model.Model;
import example.simulator.Simulator;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ChartViewer;

public abstract class SingleChartViewer extends ChartViewer {

	protected List<Simulator<ExampleStatistics>> simulators;
	protected int index;
	
	protected Model model;
	protected ExampleStatistics statistics;

	public SingleChartViewer(List<Simulator<ExampleStatistics>> simulators, int index, String title, String categoryAxisLabel, String valueAxisLabel) {
		super(title, categoryAxisLabel, valueAxisLabel);
		
		this.simulators = simulators;
		this.index = index;
		
		this.model = simulators.get(index).getModel();
		this.statistics = simulators.get(index).getStatistics();
	}
	
	public SingleChartViewer(Model model, ExampleStatistics statistics, String title, String categoryAxisLabel, String valueAxisLabel) {
		super(title, categoryAxisLabel, valueAxisLabel);
		
		this.model = model;
		this.statistics = statistics;
	}

}
