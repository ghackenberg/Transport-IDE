package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.fx.viewers.EntityViewer;
import javafx.scene.DepthTest;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;

public class IntersectionViewerDeep extends EntityViewer<Intersection> {
	
	public final Sphere sphere = new Sphere();

	public IntersectionViewerDeep(Model model, Intersection entity) {
		super(model, entity);
		
		// Sphere
		
		sphere.radiusProperty().bind(entity.lanes.divide(2));
		
		sphere.setDepthTest(DepthTest.ENABLE);
		sphere.setMaterial(new PhongMaterial(Color.RED));
		sphere.setDrawMode(DrawMode.FILL);
		
		getChildren().add(sphere);
		
		// Self
		
		translateXProperty().bind(entity.coordinate.x);
		translateYProperty().bind(entity.coordinate.y);
		translateZProperty().bind(entity.coordinate.z);
	}

	@Override
	public void update() {
		
	}

}
