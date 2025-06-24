package io.github.ghackenberg.mbse.transport.exporter;

import io.github.ghackenberg.mbse.transport.model.Model;
import io.github.ghackenberg.mbse.transport.statistics.Statistics;

public interface Exporter {

	public void export(Model model, Statistics statistics);
	
}
