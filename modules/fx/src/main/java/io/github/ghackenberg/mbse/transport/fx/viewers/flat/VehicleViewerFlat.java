package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import io.github.ghackenberg.mbse.transport.fx.viewers.EntityViewer;
import javafx.beans.binding.Bindings;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class VehicleViewerFlat extends EntityViewer<Vehicle> {
	
	public final Vehicle.State entityState;
	
	public final Location location;
	
	public final Rectangle rectangle;
	
	public final Text text;
	
	public VehicleViewerFlat(Model model, Vehicle vehicle) {
		super(model, vehicle);
		
		entityState = vehicle.state.get();
		
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
		
		rectangle.fillProperty().bind(Bindings.when(selected).then(Color.DODGERBLUE).otherwise(Color.DEEPSKYBLUE));
		
		getChildren().add(rectangle);
		
		// Text
		
		text = new Text();
		
		text.textProperty().bind(vehicle.name);
		text.yProperty().bind(location.coordinate.y);
		
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);
		
		text.setFill(Color.BLACK);

		text.setFont(new Font(0.5));
		text.setX(location.coordinate.x.get() - text.getBoundsInLocal().getWidth() / 2);
		
		getChildren().add(text);
		
		// Listeners

		vehicle.name.addListener(event -> {
			text.setX(location.coordinate.x.get() - text.getBoundsInLocal().getWidth() / 2);
		});
		location.coordinate.x.addListener(event -> {
			text.setX(location.coordinate.x.get() - text.getBoundsInLocal().getWidth() / 2);
		});
	}

	@Override
	public void update() {
		location.segment.set(entityState.segment);
		location.distance.set(entityState.distance);
	}
	
}
