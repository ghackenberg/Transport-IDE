package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.viewers.VehicleViewer;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

public class VehicleViewerDeep extends VehicleViewer {
	
	private final PhongMaterial material = new PhongMaterial();
	
	public final Box box = new Box();

	public VehicleViewerDeep(Model model, Vehicle entity) {
		super(model, entity);
		
		// Material
		
		material.diffuseColorProperty().bind(Bindings.when(selected).then(Color.DODGERBLUE).otherwise(Color.DEEPSKYBLUE));
		
		// Box
		
		box.widthProperty().bind(entity.length);
		
		box.translateXProperty().bind(location.coordinate.x);
		box.translateYProperty().bind(location.coordinate.y);
		box.translateZProperty().bind(location.coordinate.z.subtract(0.5));
		
		box.rotateProperty().bind(location.angleZ.multiply(180 / Math.PI));
		
		box.setHeight(1);
		box.setDepth(1);
		
		box.setRotationAxis(Rotate.Z_AXIS);
		
		box.setMaterial(material);
		
		// Self
		
		getChildren().add(box);
	}

}
