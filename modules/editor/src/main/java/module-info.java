module example.editor {
	
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires example.parser;
	
	exports io.github.ghackenberg.mbse.transport.editor;
	
}