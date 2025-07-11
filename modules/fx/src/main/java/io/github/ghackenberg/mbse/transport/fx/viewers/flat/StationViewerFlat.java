package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.structures.Vector;
import io.github.ghackenberg.mbse.transport.fx.viewers.StationViewer;
import javafx.scene.shape.Circle;

public class StationViewerFlat extends StationViewer {
	
	public final Circle circle;

	public StationViewerFlat(Model model, Station station) {
		super(model, station);
		
		Vector location = station.location.coordinate;
		
		// Circle
		
		circle = new Circle();
		
		circle.setRadius(0.5);
		
		circle.centerXProperty().bind(location.x);
		circle.centerYProperty().bind(location.y);
		
		circle.fillProperty().bind(color);
		
		// Self
		
		getChildren().add(circle);
	}
	
}
