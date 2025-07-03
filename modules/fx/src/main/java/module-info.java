module mbse.transport.fx {
	
	requires mbse.transport.core;
	requires javafx.controls;

	requires transitive javafx.graphics;
	requires javafx.web;
	
	exports io.github.ghackenberg.mbse.transport.fx.programs;
	
}