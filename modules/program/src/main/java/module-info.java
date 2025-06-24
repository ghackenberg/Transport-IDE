module example.program {
	
	requires example.model;
	requires example.parser;
	requires example.controller;
	requires example.statistics;
	requires example.exporter;
	requires example.simulator;
	requires example.viewer;
	
	requires transitive javafx.graphics;
	requires javafx.controls;
	
	exports io.github.ghackenberg.mbse.transport.program;
	
}