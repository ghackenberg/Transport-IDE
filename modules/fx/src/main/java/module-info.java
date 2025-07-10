/**
 * Contains the new JavaFX-based graphical user interface of Transport-IDE.
 * 
 * @author Georg Hackenberg
 */
module mbse.transport.fx {
	
	requires mbse.transport.core;
	requires javafx.controls;
	requires javafx.web;

	requires transitive javafx.graphics;
	
	exports io.github.ghackenberg.mbse.transport.fx.programs;
	
}