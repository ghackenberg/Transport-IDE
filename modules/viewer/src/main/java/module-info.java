module example.viewer {
	
	requires transitive example.model;
	requires transitive example.controller;
	requires transitive example.simulator;
	requires transitive example.statistics;
	requires transitive java.desktop;
	
	requires docking.frames.core;
	requires jfreechart;
	
	exports io.github.ghackenberg.mbse.transport.viewer;
	exports io.github.ghackenberg.mbse.transport.viewer.charts;
	exports io.github.ghackenberg.mbse.transport.viewer.charts.multiple;
	exports io.github.ghackenberg.mbse.transport.viewer.charts.single;
	
}