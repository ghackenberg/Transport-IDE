package io.github.ghackenberg.mbse.transport.swings.viewers.charts.multiple;

import java.awt.Color;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.swings.viewers.charts.MultipleChartViewer;

public class DemandTimesChartViewer extends MultipleChartViewer {

	public DemandTimesChartViewer(List<Simulator> simulators) {
		super(simulators, "Demand times", "Simulators", "Time (in s)");
		
		renderer.setSeriesPaint(0, Color.GRAY);
		renderer.setSeriesPaint(1, Color.DARK_GRAY);
		renderer.setSeriesPaint(2, Color.ORANGE);
		renderer.setSeriesPaint(3, Color.RED);
	}

	@Override
	public void update() {
		
		for (Simulator simulator : simulators) {
			
			Model model = simulator.getModel();
			
			Statistics stats = simulator.getStatistics();
			
			double underdueWait = 0;
			double underdueRide = 0;
			double overdueWait = 0;
			double overdueRide = 0;
			
			for (Demand demand : model.demands) {

				if (demand.pick.time.get() < model.state.get().time) {
					
					double pickup = model.state.get().time;
					if (stats.demandPickupAcceptTimes.get(demand).size() == 1) {
						pickup = stats.demandPickupAcceptTimes.get(demand).entrySet().iterator().next().getKey();
					}
					
					double dropoff = model.state.get().time;
					if (stats.demandDropoffTimes.get(demand).size() == 1) {
						dropoff = stats.demandDropoffTimes.get(demand).entrySet().iterator().next().getKey();
					}
					
					underdueWait += Math.min(demand.drop.time.get(), pickup) - demand.pick.time.get();
					underdueRide += Math.min(demand.drop.time.get(), dropoff) - Math.min(demand.drop.time.get(), pickup);
					overdueWait += Math.max(demand.drop.time.get(), pickup) - demand.drop.time.get();
					overdueRide += Math.max(demand.drop.time.get(), dropoff) - Math.max(demand.drop.time.get(), pickup);
					
					//System.out.println((underdueWait + underdueRide + overdueWait + overdueRide) / 1000);
					
				}
			}
			
			dataset.addValue(underdueWait / 1000, "Wait (underdue)", simulator.getName());
			dataset.addValue(overdueWait / 1000, "Wait (overdue)", simulator.getName());
			dataset.addValue(underdueRide / 1000, "Ride (underdue)", simulator.getName());
			dataset.addValue(overdueRide / 1000, "Ride (overdue)", simulator.getName());
			
		}
	}

}
