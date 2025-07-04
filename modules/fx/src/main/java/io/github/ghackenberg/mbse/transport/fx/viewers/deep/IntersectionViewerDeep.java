package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.fx.viewers.IntersectionViewer;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public class IntersectionViewerDeep extends IntersectionViewer {
	
	public final PhongMaterial material = new PhongMaterial();
	
	public final Cylinder cylinder = new Cylinder();

	public IntersectionViewerDeep(Model model, Intersection entity) {
		super(model, entity);
		
		// Material
		
		material.diffuseColorProperty().bind(Bindings.when(selected).then(Color.RED).otherwise(Color.ORANGE));
		
		// Sphere
		
		cylinder.radiusProperty().bind(entity.lanes.divide(2));
		
		cylinder.setHeight(0.1);
		cylinder.setMaterial(material);
		
		cylinder.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		
		getChildren().add(cylinder);
		
		// Self
		
		translateXProperty().bind(entity.coordinate.x);
		translateYProperty().bind(entity.coordinate.y);
		translateZProperty().bind(entity.coordinate.z);
	}

}
