package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Coordinate;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class VehicleViewer extends Group {

	private Vehicle vehicle;
	
	private Rectangle rectangle;
	
	public VehicleViewer(Model model, Vehicle vehicle) {
		this.vehicle = vehicle;
		
		setManaged(false);
		
		Coordinate location = vehicle.location.toCoordinate();
		
		double angle = vehicle.location.segment.getAngle();
		
		// Rectangle
		
		rectangle = new Rectangle();
		
		rectangle.setWidth(vehicle.length);
		rectangle.setHeight(1);
		
		rectangle.setX(location.latitude - vehicle.length / 2);
		rectangle.setY(location.longitude - 1 / 2.);
		
		rectangle.setRotate(angle / Math.PI * 180);
		
		rectangle.setFill(Color.BLUE);
		
		getChildren().add(rectangle);
	}
	
	public void update() {
		Coordinate location = vehicle.location.toCoordinate();
		
		double angle = vehicle.location.segment.getAngle();
		
		rectangle.setX(location.latitude - vehicle.length / 2);
		rectangle.setY(location.longitude - 1 / 2.);
		
		rectangle.setRotate(angle / Math.PI * 180);
	}
	
}
