package io.github.ghackenberg.mbse.transport.viewer.charts;

import java.util.List;

import io.github.ghackenberg.mbse.transport.simulator.Simulator;
import io.github.ghackenberg.mbse.transport.viewer.ChartViewer;

public abstract class MultipleChartViewer extends ChartViewer {
	
	protected List<Simulator> simulators;

	public MultipleChartViewer(List<Simulator> simulators, String title, String categoryAxisLabel, String valueAxisLabel) {
		super(title, categoryAxisLabel, valueAxisLabel);
		
		this.simulators = simulators;
	}

}
