/**
 * Contains the core classes and functionalities of Transport-IDE.
 * 
 * The core classes and functionalities are used by the command line interface (CLI) and the graphical user interfaces (GUIs).
 */
module mbse.transport.core {
	
	// TODO Remove this dependency in future releases.
	requires java.desktop;
	
	requires transitive org.jgrapht.core;
	requires transitive javafx.base;
	
	exports io.github.ghackenberg.mbse.transport.core;
	exports io.github.ghackenberg.mbse.transport.core.structures;
	exports io.github.ghackenberg.mbse.transport.core.entities;
	exports io.github.ghackenberg.mbse.transport.core.adapters;
	exports io.github.ghackenberg.mbse.transport.core.controllers;
	exports io.github.ghackenberg.mbse.transport.core.exporters;
	exports io.github.ghackenberg.mbse.transport.core.exceptions;
	
}