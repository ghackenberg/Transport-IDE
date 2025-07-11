package io.github.ghackenberg.mbse.transport.fx.scenes;


import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

/**
 * Help scene providing access to the Transport-IDE online documentation via an integrated Web view.
 */
public class HelpScene extends Scene {
	
	private final ToolBar bottom = new ToolBar(new Label("© 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, FH Upper Austria"));
	
	private final WebView center = new WebView();
	
	private final BorderPane root = new BorderPane(center, null, null, bottom, null);

	public HelpScene(double width, double height) {
		super(new Label("Loading ..."), width, height);
		
		center.getEngine().load("https://github.com/ghackenberg/Transport-IDE");
		
		setRoot(root);
	}

}
