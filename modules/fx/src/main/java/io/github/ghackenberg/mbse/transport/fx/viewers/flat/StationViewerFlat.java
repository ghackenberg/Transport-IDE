package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import io.github.ghackenberg.mbse.transport.fx.viewers.StationViewer;
import javafx.scene.shape.Circle;

public class StationViewerFlat extends StationViewer {
	
	public final Circle circle;

	public StationViewerFlat(Model model, Station station) {
		super(model, station);
		
		Coordinate location = station.location.coordinate;
		
		// Circle
		
		circle = new Circle();
		
		circle.setRadius(0.5);
		
		// TODO (issue #16) bind land offset to tangent and number of lanes somehow!
		
		circle.centerXProperty().bind(location.x.add(laneOffsetX));
		circle.centerYProperty().bind(location.y.add(laneOffsetY));
		
		circle.fillProperty().bind(color);
		
		// Self
		
		getChildren().add(circle);
	}
	
}
