package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.entities.Coordinate;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class StationViewer extends Group {
	
	private Circle circle;

	public StationViewer(Station station) {
		
		Coordinate location = station.location.toCoordinate();
		
		// Circle
		
		circle = new Circle();
		
		circle.setRadius(0.5);
		
		circle.setCenterX(location.latitude);
		circle.setCenterY(location.longitude);
		
		circle.setFill(Color.MAGENTA);
		
		getChildren().add(circle);
	}
	
	public void update() {
		
	}
	
}
