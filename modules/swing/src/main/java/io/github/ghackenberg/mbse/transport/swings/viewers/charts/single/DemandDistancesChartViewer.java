package io.github.ghackenberg.mbse.transport.swings.viewers.charts.single;

import java.awt.Color;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.SingleChartViewer;

public class DemandDistancesChartViewer extends SingleChartViewer {

	public DemandDistancesChartViewer(List<Simulator> simulators, int index) {
		super(simulators, index, "Demand distances", "Demands", "Distance");
		
		renderer.setSeriesPaint(0, Color.BLUE);
	}

	@Override
	public void update() {
		// Update values
		
		for (Demand demand : model.demands) {
			double distance = statistics.demandDistances.get(demand);
			dataset.addValue(distance, "Distances", demand.toString());
		}
		
		// Update range
		
		double max = 0;
		
		for (Simulator simulator : simulators) {
			for (double distance : simulator.getStatistics().demandDistances.values()) {
				max = Math.max(distance, max);
			}
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
