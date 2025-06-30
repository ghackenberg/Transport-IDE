package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.entities.Coordinate;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.fx.events.StationEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class StationViewer extends EntityViewer<Station, StationEvent> {
	
	private Circle circle;

	public StationViewer(Station station) {
		super(station);
		
		Coordinate location = station.location.coordinate;
		
		// Circle
		
		circle = new Circle();
		
		circle.setRadius(0.5);
		
		circle.centerXProperty().bind(location.x);
		circle.centerYProperty().bind(location.y);
		
		circle.setFill(Color.MAGENTA);
		
		getChildren().add(circle);
	}
	
	public void update() {
		
	}
	
}
