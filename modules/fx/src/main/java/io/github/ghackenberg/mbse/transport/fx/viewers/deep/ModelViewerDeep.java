package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ModelViewerDeep extends ModelViewer<IntersectionViewerDeep, SegmentViewerDeep, StationViewerDeep, VehicleViewerDeep, DemandViewerDeep, SubScene> {
	
	public final PerspectiveCamera camera = new PerspectiveCamera(true);
	
	public final Box box = new Box(5, 5, 5);
	
	public final Group root = new Group(camera, box);
	
	public ModelViewerDeep(Model model) {
		super(model, new SubScene(new Group(), 300, 300));
        
		camera.getTransforms().addAll(new Rotate(-20, Rotate.Y_AXIS), new Rotate(-20, Rotate.X_AXIS), new Translate(0, 0, -15));
		
		box.setMaterial(new PhongMaterial(Color.RED));
		box.setDrawMode(DrawMode.LINE);
		
		canvas.setCamera(camera);
		canvas.setRoot(root);
		
		canvas.widthProperty().bind(widthProperty());
		canvas.heightProperty().bind(heightProperty());
	}

}
