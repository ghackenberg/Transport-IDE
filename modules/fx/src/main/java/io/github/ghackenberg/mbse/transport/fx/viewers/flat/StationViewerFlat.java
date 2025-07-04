package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import io.github.ghackenberg.mbse.transport.fx.viewers.EntityViewer;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class StationViewerFlat extends EntityViewer<Station> {
	
	public final Circle circle;

	public StationViewerFlat(Model model, Station station) {
		super(model, station);
		
		Coordinate location = station.location.coordinate;
		
		// Circle
		
		circle = new Circle();
		
		circle.setRadius(0.5);
		
		circle.centerXProperty().bind(location.x);
		circle.centerYProperty().bind(location.y);
		
		circle.fillProperty().bind(Bindings.when(selected).then(Color.DARKVIOLET).otherwise(Color.VIOLET));
		
		getChildren().add(circle);
	}

	@Override
	public void update() {
		
	}
	
}
