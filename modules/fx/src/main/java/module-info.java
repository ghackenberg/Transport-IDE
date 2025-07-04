module mbse.transport.fx {
	
	requires mbse.transport.core;
	requires javafx.controls;
	requires javafx.web;

	requires transitive javafx.graphics;
	
	exports io.github.ghackenberg.mbse.transport.fx.programs;
	
}