package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class StationViewer extends EntityViewer<Station> {
	
	public final Circle circle;

	public StationViewer(Model model, Station station) {
		super(model, station);
		
		Coordinate location = station.location.coordinate;
		
		// Circle
		
		circle = new Circle();
		
		circle.setRadius(0.5);
		
		circle.centerXProperty().bind(location.x);
		circle.centerYProperty().bind(location.y);
		
		circle.setFill(Color.MAGENTA);
		
		getChildren().add(circle);
	}

	@Override
	public void update() {
		
	}
	
}
