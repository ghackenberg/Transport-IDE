package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Warehouse;
import io.github.ghackenberg.mbse.transport.fx.viewers.WarehouseViewer;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public class WarehouseViewerDeep extends WarehouseViewer{
	
	public final PhongMaterial material = new PhongMaterial();
	
	public final Cylinder cylinder = new Cylinder();

	public WarehouseViewerDeep(Model model, Warehouse entity) {
		super(model, entity);
		
		// Material
		
		material.diffuseColorProperty().bind(color);
		
		// Cylinder
		
		cylinder.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		
		cylinder.translateXProperty().bind(entity.location.coordinate.x);
		cylinder.translateYProperty().bind(entity.location.coordinate.y);
		cylinder.translateZProperty().bind(entity.location.coordinate.z.subtract(0.5));
		
		cylinder.setRadius(0.5);
		cylinder.setHeight(1);
		cylinder.setMaterial(material);
		
		// Self
		
		getChildren().add(cylinder);
	}

}
