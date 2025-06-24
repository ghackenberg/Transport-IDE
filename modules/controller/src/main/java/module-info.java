module example.controller {
	
	requires transitive example.model;
	requires transitive java.desktop;
	requires org.jgrapht.core;
	requires org.jheaps;
	
	exports io.github.ghackenberg.mbse.transport.controller;	
	exports io.github.ghackenberg.mbse.transport.controller.implementations;
	
}