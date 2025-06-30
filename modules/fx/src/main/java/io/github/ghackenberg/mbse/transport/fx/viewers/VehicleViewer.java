package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Coordinate;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.events.VehicleEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class VehicleViewer extends EntityViewer<Vehicle, VehicleEvent> {

	private Vehicle vehicle;
	
	private Rectangle rectangle;
	
	public VehicleViewer(Model model, Vehicle vehicle) {
		super(vehicle);
		
		this.vehicle = vehicle;
		
		setManaged(false);
		
		Coordinate location = vehicle.location.coordinate;
		
		double angle = vehicle.location.segment.get().angle.get();
		
		// Rectangle
		
		rectangle = new Rectangle();
		
		rectangle.widthProperty().bind(vehicle.length);
		rectangle.setHeight(1);
		
		rectangle.xProperty().bind(location.x.subtract(vehicle.length.divide(2)));
		rectangle.yProperty().bind(location.y.subtract(1 / 2.));
		
		rectangle.setRotate(angle / Math.PI * 180);
		
		rectangle.setFill(Color.BLUE);
		
		getChildren().add(rectangle);
	}
	
	public void update() {
		double angle = vehicle.location.segment.get().angle.get();
		
		rectangle.setRotate(angle / Math.PI * 180);
	}
	
}
