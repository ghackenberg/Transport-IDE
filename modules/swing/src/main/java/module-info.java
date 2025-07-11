/**
 * Contains the old Swing-based graphical user interface of Transport-IDE.
 */
module mbse.transport.swing {
	
	requires mbse.transport.core;
	requires org.jfree.jfreechart;
	requires docking.frames.core;
	
	requires transitive java.desktop;
	
	exports io.github.ghackenberg.mbse.transport.swing.programs;
	
}