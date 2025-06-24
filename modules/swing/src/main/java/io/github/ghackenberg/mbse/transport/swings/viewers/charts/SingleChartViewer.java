package io.github.ghackenberg.mbse.transport.swings.viewers.charts;

import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.swings.viewers.ChartViewer;

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
