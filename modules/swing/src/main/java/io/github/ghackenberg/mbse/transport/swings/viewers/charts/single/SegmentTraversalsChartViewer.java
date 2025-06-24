package io.github.ghackenberg.mbse.transport.swings.viewers.charts.single;

import java.awt.Color;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.SingleChartViewer;

public class SegmentTraversalsChartViewer extends SingleChartViewer {

	public SegmentTraversalsChartViewer(List<Simulator> simulators, int index) {
		super(simulators, index, "Segment traversals", "Segments", "Count");
		
		renderer.setSeriesPaint(0, Color.GREEN);
	}

	@Override
	public void update() {
		// Update values
		
		for (Segment segment : model.segments) {
			int traversals = statistics.segmentTraversals.get(segment);
			dataset.addValue(traversals, "Traversals", segment.toString());
		}
		
		// Update range
		
		double max = 0;
		
		for (Simulator simulator : simulators) {
			for (int traversals : simulator.getStatistics().segmentTraversals.values()) {
				max = Math.max(traversals, max);
			}
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
