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
		
		Coordinate location = station.location.getCoordinate();
		
		// Circle
		
		circle = new Circle();
		
		circle.setRadius(0.5);
		
		circle.centerXProperty().bind(location.xProperty());
		circle.centerYProperty().bind(location.yProperty());
		
		circle.setFill(Color.MAGENTA);
		
		getChildren().add(circle);
	}
	
	public void update() {
		
	}
	
}
