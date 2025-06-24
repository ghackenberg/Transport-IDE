package example.exporter;

import example.model.Model;
import example.statistics.Statistics;

public interface Exporter {

	public void export(Model model, Statistics statistics);
	
}
