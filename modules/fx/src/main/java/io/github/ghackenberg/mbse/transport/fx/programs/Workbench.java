package io.github.ghackenberg.mbse.transport.fx.programs;

import io.github.ghackenberg.mbse.transport.fx.scenes.EditorScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class Workbench extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new EditorScene());
		primaryStage.setTitle("ITS-MSE Editor");
		primaryStage.show();
	}
	
}
