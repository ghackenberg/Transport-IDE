module mbse.transport.swing{
	
	requires mbse.transport.core;
	
	requires transitive java.desktop;
	
	requires docking.frames.core;
	requires jfreechart;
	
	exports io.github.ghackenberg.mbse.transport.swing.programs;
	
}