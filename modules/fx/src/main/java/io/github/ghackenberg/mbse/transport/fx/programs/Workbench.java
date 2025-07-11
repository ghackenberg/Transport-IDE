package io.github.ghackenberg.mbse.transport.fx.programs;

import io.github.ghackenberg.mbse.transport.fx.scenes.EditorScene;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point of the workbench application including a graphical model editor and the simulator.
 */
public class Workbench extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.getIcons().add(new Image("app.png"));
		primaryStage.setScene(new EditorScene());
		primaryStage.setTitle("ITS-MSE Editor");
		primaryStage.show();
	}
	
}
