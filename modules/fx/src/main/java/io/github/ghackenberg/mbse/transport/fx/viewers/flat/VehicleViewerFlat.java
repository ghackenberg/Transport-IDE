package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.viewers.VehicleViewer;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class VehicleViewerFlat extends VehicleViewer {
	
	public final Rectangle rectangle;
	
	public final Text text;
	
	public VehicleViewerFlat(Model model, Vehicle vehicle) {
		super(model, vehicle);
		
		// Rectangle
		
		rectangle = new Rectangle();
		
		// rectangle width = vehicle length !!
		// rectangle height = vehicle width !!
		
		rectangle.widthProperty().bind(vehicle.length);
		rectangle.setHeight(1);
		
		rectangle.xProperty().bind(location.coordinate.x.add(laneOffsetX).subtract(vehicle.length.divide(2)));
		rectangle.yProperty().bind(location.coordinate.y.add(laneOffsetY).subtract(1 / 2.));
		
		rectangle.rotateProperty().bind(location.angleZ.divide(Math.PI).multiply(180));
		
		rectangle.fillProperty().bind(color);
		
		// Text
		
		text = new Text();
		
		text.textProperty().bind(vehicle.name);
		text.yProperty().bind(location.coordinate.y);
		
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);
		
		text.setFill(Color.BLACK);

		text.setFont(new Font(0.5));
		text.setX(location.coordinate.x.get() - text.getBoundsInLocal().getWidth() / 2);
		
		// Self
		
		getChildren().add(rectangle);
		getChildren().add(text);
		
		// Listeners

		vehicle.name.addListener(event -> {
			text.setX(location.coordinate.x.get() + laneOffsetX.get() - text.getBoundsInLocal().getWidth() / 2);
		});
		location.coordinate.x.addListener(event -> {
			text.setX(location.coordinate.x.get() + laneOffsetX.get() - text.getBoundsInLocal().getWidth() / 2);
		});
		laneOffsetX.addListener(event -> {
			text.setX(location.coordinate.x.get() + laneOffsetX.get() - text.getBoundsInLocal().getWidth() / 2);
		});
	}
	
}
