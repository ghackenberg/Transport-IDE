package io.github.ghackenberg.mbse.transport.swings.viewers.charts.single;

import java.awt.Color;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.SingleChartViewer;

public class DemandTimesChartViewer extends SingleChartViewer {

	public DemandTimesChartViewer(List<Simulator> simulators, int index) {
		super(simulators, index, "Demand times", "Demands", "Time (in s)");
		
		renderer.setSeriesPaint(0, Color.GRAY);
		renderer.setSeriesPaint(1, Color.ORANGE);
		renderer.setSeriesPaint(2, Color.DARK_GRAY);
		renderer.setSeriesPaint(3, Color.RED);
		
		domain.setTickMarksVisible(false);
		domain.setTickLabelsVisible(false);
	}

	@Override
	public void update() {
		// Update values
		
		for (Demand demand : model.demands) {
			if (demand.pick.time.get() > model.time) {
				
				dataset.addValue(0, "Underdue (wait)", demand.toString());
				dataset.addValue(0, "Underdue (ride)", demand.toString());
				dataset.addValue(0, "Overdue (wait)", demand.toString());
				dataset.addValue(0, "Overdue (ride)", demand.toString());
				
			} else {
				
				double pickup = model.time;
				if (statistics.demandPickupAcceptTimes.get(demand).size() == 1) {
					pickup = statistics.demandPickupAcceptTimes.get(demand).entrySet().iterator().next().getKey();
				}
				
				double dropoff = model.time;
				if (statistics.demandDropoffTimes.get(demand).size() == 1) {
					dropoff = statistics.demandDropoffTimes.get(demand).entrySet().iterator().next().getKey();
				}
				
				double underdueWait = Math.min(demand.drop.time.get(), pickup) - demand.pick.time.get();
				double underdueRide = Math.min(demand.drop.time.get(), dropoff) - Math.min(demand.drop.time.get(), pickup);
				double overdueWait = Math.max(demand.drop.time.get(), pickup) - demand.drop.time.get();
				double overdueRide = Math.max(demand.drop.time.get(), dropoff) - Math.max(demand.drop.time.get(), pickup);
				
				dataset.addValue(underdueWait / 1000, "Underdue (wait)", demand.toString());
				dataset.addValue(underdueRide / 1000, "Underdue (ride)", demand.toString());
				dataset.addValue(overdueWait / 1000, "Overdue (wait)", demand.toString());
				dataset.addValue(overdueRide / 1000, "Overdue (ride)", demand.toString());
				
				//System.out.println((underdueWait + underdueRide + overdueWait + overdueRide) / 1000);
				
			}
		}
		
		// Update range
		
		double max = 0;
		
		for (Simulator simulator : simulators) {
			Model model = simulator.getModel();
			Statistics statistics = simulator.getStatistics();
			for (Demand demand : model.demands) {
				if (demand.pick.time.get() < model.time) {
					double dropoff = model.time;
					if (statistics.demandDropoffTimes.get(demand).size() == 1) {
						dropoff = statistics.demandDropoffTimes.get(demand).entrySet().iterator().next().getKey();
					}
					max = Math.max((dropoff - demand.pick.time.get()) / 1000, max);
				}
			}
		}
		
		range.setRange(0, max > 0 ? max : 1);
	}

}
