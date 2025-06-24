package io.github.ghackenberg.mbse.transport.swings.viewers.charts;

import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.swings.viewers.ChartViewer;

public abstract class MultipleChartViewer extends ChartViewer {
	
	protected List<Simulator> simulators;

	public MultipleChartViewer(List<Simulator> simulators, String title, String categoryAxisLabel, String valueAxisLabel) {
		super(title, categoryAxisLabel, valueAxisLabel);
		
		this.simulators = simulators;
	}

}
