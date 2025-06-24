module mbse.transport.core {
	
	requires java.desktop;
	
	requires transitive org.jgrapht.core;
	
	exports io.github.ghackenberg.mbse.transport.core;
	exports io.github.ghackenberg.mbse.transport.core.entities;
	exports io.github.ghackenberg.mbse.transport.core.adapters;
	exports io.github.ghackenberg.mbse.transport.core.controllers;
	exports io.github.ghackenberg.mbse.transport.core.exporters;
	exports io.github.ghackenberg.mbse.transport.core.exceptions;
	
}