package example.viewer.charts.multiple;

import java.awt.Color;
import java.util.List;

import example.model.Demand;
import example.model.Model;
import example.simulator.Simulator;
import example.statistics.Statistics;
import example.viewer.charts.MultipleChartViewer;

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

				if (demand.pickup.time < model.time) {
					
					double pickup = model.time;
					if (stats.demandPickupAcceptTimes.get(demand).size() == 1) {
						pickup = stats.demandPickupAcceptTimes.get(demand).entrySet().iterator().next().getKey();
					}
					
					double dropoff = model.time;
					if (stats.demandDropoffTimes.get(demand).size() == 1) {
						dropoff = stats.demandDropoffTimes.get(demand).entrySet().iterator().next().getKey();
					}
					
					underdueWait += Math.min(demand.dropoff.time, pickup) - demand.pickup.time;
					underdueRide += Math.min(demand.dropoff.time, dropoff) - Math.min(demand.dropoff.time, pickup);
					overdueWait += Math.max(demand.dropoff.time, pickup) - demand.dropoff.time;
					overdueRide += Math.max(demand.dropoff.time, dropoff) - Math.max(demand.dropoff.time, pickup);
					
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
