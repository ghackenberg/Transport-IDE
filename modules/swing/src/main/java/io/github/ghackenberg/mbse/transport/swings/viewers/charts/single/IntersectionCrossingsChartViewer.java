package io.github.ghackenberg.mbse.transport.swings.viewers.charts.single;

import java.awt.Color;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.SingleChartViewer;

public class IntersectionCrossingsChartViewer extends SingleChartViewer {

	public IntersectionCrossingsChartViewer(List<Simulator> simulators, int index) {
		super(simulators, index, "Intersection crossings", "Intersections", "Count");
		
		renderer.setSeriesPaint(0, Color.GREEN);
	}

	@Override
	public void update() {
		// Update values
		
		for (Intersection intersection : model.intersections) {
			int traversals = statistics.intersectionCrossings.get(intersection);
			dataset.addValue(traversals, "Crossings", intersection.name.get());
		}
		
		// Update range
		
		double max = 1;
		
		for (Simulator simulator : simulators) {
			for (int traversals : simulator.getStatistics().intersectionCrossings.values()) {
				max = Math.max(traversals, max);
			}
		}
		
		range.setRange(0, max);
	}

}
