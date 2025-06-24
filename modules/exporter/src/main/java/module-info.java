module example.exporter {
	
	requires transitive example.model;
	requires transitive example.statistics;
	
	exports io.github.ghackenberg.mbse.transport.exporter;
	exports io.github.ghackenberg.mbse.transport.exporter.implementations;
	
}