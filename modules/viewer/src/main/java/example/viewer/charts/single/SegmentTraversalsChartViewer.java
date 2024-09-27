package example.viewer.charts.single;

import java.awt.Color;
import java.util.List;

import example.model.Segment;
import example.simulator.Simulator;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.charts.SingleChartViewer;

public class SegmentTraversalsChartViewer extends SingleChartViewer {

	public SegmentTraversalsChartViewer(List<Simulator<ExampleStatistics>> simulators, int index) {
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
		
		for (Simulator<ExampleStatistics> simulator : simulators) {
			for (int traversals : simulator.getStatistics().segmentTraversals.values()) {
				max = Math.max(traversals, max);
			}
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
