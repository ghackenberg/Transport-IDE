package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.viewers.VehicleViewer;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;

public class VehicleViewerDeep extends VehicleViewer {

	private final PhongMaterial wheelMaterial = new PhongMaterial();
	private final PhongMaterial bodyMaterial = new PhongMaterial();
	
	public final Cylinder wheel1 = new Cylinder();
	public final Cylinder wheel2 = new Cylinder();
	public final Cylinder wheel3 = new Cylinder();
	public final Cylinder wheel4 = new Cylinder();
	
	public final Box body = new Box();
	public final Box frame = new Box();

	public VehicleViewerDeep(Model model, Vehicle entity) {
		super(model, entity);
		
		// Material
		
		wheelMaterial.setDiffuseColor(Color.BLACK);
		
		bodyMaterial.diffuseColorProperty().bind(color);
		
		// Wheels
		
		wheel1.setRadius(0.1);
		wheel1.setHeight(0.1);
		
		wheel2.setRadius(0.1);
		wheel2.setHeight(0.1);
		
		wheel3.setRadius(0.1);
		wheel3.setHeight(0.1);
		
		wheel4.setRadius(0.1);
		wheel4.setHeight(0.1);
		
		wheel1.setMaterial(wheelMaterial);
		wheel2.setMaterial(wheelMaterial);
		wheel3.setMaterial(wheelMaterial);
		wheel4.setMaterial(wheelMaterial);
		
		wheel1.translateXProperty().bind(entity.length.divide(-2).add(wheel1.radiusProperty()));
		wheel2.translateXProperty().bind(entity.length.divide(-2).add(wheel1.radiusProperty()));
		wheel3.translateXProperty().bind(entity.length.divide(+2).subtract(wheel1.radiusProperty()));
		wheel4.translateXProperty().bind(entity.length.divide(+2).subtract(wheel1.radiusProperty()));
		
		wheel1.setTranslateY(-0.5 + wheel1.getHeight() / 2);
		wheel2.setTranslateY(+0.5 - wheel2.getHeight() / 2);
		wheel3.setTranslateY(-0.5 + wheel3.getHeight() / 2);
		wheel4.setTranslateY(+0.5 - wheel4.getHeight() / 2);
		
		wheel1.setTranslateZ(-0.1);
		wheel2.setTranslateZ(-0.1);
		wheel3.setTranslateZ(-0.1);
		wheel4.setTranslateZ(-0.1);
		
		// Body
		
		body.widthProperty().bind(entity.length);		
		body.setHeight(1);
		body.setDepth(0.1);
		
		body.translateZProperty().bind(body.depthProperty().divide(-2).subtract(wheel1.radiusProperty().multiply(2)));

		body.setMaterial(bodyMaterial);
		
		// Frame
		
		frame.widthProperty().bind(entity.length);
		frame.setHeight(1);
		frame.depthProperty().bind(entity.loadCapacity.divide(entity.length));
		
		frame.translateZProperty().bind(frame.depthProperty().divide(-2).subtract(body.depthProperty()).subtract(wheel1.radiusProperty().multiply(2)));
		
		frame.setMaterial(bodyMaterial);
		frame.setDrawMode(DrawMode.LINE);
		frame.setCullFace(CullFace.NONE);
		
		// Self
		
		getChildren().add(wheel1);
		getChildren().add(wheel2);
		getChildren().add(wheel3);
		getChildren().add(wheel4);
		
		getChildren().add(body);
		getChildren().add(frame);
		
		translateXProperty().bind(location.coordinate.x);
		translateYProperty().bind(location.coordinate.y);
		translateZProperty().bind(location.coordinate.z);
		
		rotateProperty().bind(location.angleZ.multiply(180 / Math.PI));
		
		setRotationAxis(Rotate.Z_AXIS);
	}

}
