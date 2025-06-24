module example.simulator {
	
	requires transitive example.model;
	requires transitive example.controller;
	requires transitive example.statistics;
	
	exports io.github.ghackenberg.mbse.transport.simulator;
	exports io.github.ghackenberg.mbse.transport.simulator.exceptions;
	
}