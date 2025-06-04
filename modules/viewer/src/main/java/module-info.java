module example.viewer {
	
	requires transitive example.model;
	requires transitive example.controller;
	requires transitive example.simulator;
	requires transitive example.statistics;
	requires transitive java.desktop;
	
	requires docking.frames.core;
	requires jfreechart;
	
	exports example.viewer;
	exports example.viewer.charts;
	exports example.viewer.charts.multiple;
	exports example.viewer.charts.single;
	
}