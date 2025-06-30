package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import io.github.ghackenberg.mbse.transport.fx.events.VehicleEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class VehicleViewer extends EntityViewer<Vehicle, VehicleEvent> {
	
	private final Vehicle.State entityState;
	
	private final Location location;
	
	private final Rectangle rectangle;
	
	public VehicleViewer(Model model, Vehicle vehicle) {
		super(model, vehicle);
		
		entityState = vehicle.state.get();
		
		setManaged(false);
		
		// Location
		
		if (entityState == null) {
			location = vehicle.initialLocation;
		} else {
			location = new Location();
			location.segment.set(entityState.segment);
			location.distance.set(entityState.distance);
		}
		
		// Rectangle
		
		rectangle = new Rectangle();
		
		rectangle.widthProperty().bind(vehicle.length);
		rectangle.setHeight(1);
		
		rectangle.xProperty().bind(location.coordinate.x.subtract(vehicle.length.divide(2)));
		rectangle.yProperty().bind(location.coordinate.y.subtract(1 / 2.));
		
		rectangle.rotateProperty().bind(location.angle.divide(Math.PI).multiply(180));
		
		rectangle.setFill(Color.BLUE);
		
		rectangle.setOnMouseClicked(event -> {
			event.consume();
			// TODO
		});
		
		getChildren().add(rectangle);
	}

	@Override
	public void update() {
		location.segment.set(entityState.segment);
		location.distance.set(entityState.distance);
	}
	
}
