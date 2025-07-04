package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import io.github.ghackenberg.mbse.transport.fx.viewers.EntityViewer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class StationViewerFlat extends EntityViewer<Station> {

	private final DoubleProperty laneOffsetX = new SimpleDoubleProperty(0);
	private final DoubleProperty laneOffsetY = new SimpleDoubleProperty(0);
	
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
		
		circle.fillProperty().bind(Bindings.when(selected).then(Color.DARKVIOLET).otherwise(Color.VIOLET));
		
		getChildren().add(circle);
	}

	@Override
	public void update() {
		
	}
	
}
