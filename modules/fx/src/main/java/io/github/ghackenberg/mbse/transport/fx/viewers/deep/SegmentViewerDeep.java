package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.fx.viewers.EntityViewer;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;

public class SegmentViewerDeep extends EntityViewer<Segment> {
	
	public final List<Sphere> spheres = new ArrayList<>();

	public SegmentViewerDeep(Model model, Segment entity) {
		super(model, entity);
		
		// Cylinders
		
		for (int i = 1; i < 10; i++) {
			Sphere sphere = new Sphere();
			
			sphere.radiusProperty().bind(entity.lanes.divide(4));
			
			sphere.translateXProperty().bind(entity.start.coordinate.x.add(entity.tangent.x.multiply(entity.length).multiply(i / 10.)));
			sphere.translateYProperty().bind(entity.start.coordinate.y.add(entity.tangent.y.multiply(entity.length).multiply(i / 10.)));
			sphere.translateZProperty().bind(entity.start.coordinate.z.add(entity.tangent.z.multiply(entity.length).multiply(i / 10.)));
			
			sphere.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
			sphere.setDrawMode(DrawMode.FILL);
			
			getChildren().add(sphere);
		}
	}

	@Override
	public void update() {
		
	}

}
