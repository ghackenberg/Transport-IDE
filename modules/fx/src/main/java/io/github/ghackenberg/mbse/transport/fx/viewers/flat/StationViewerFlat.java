package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.structures.Vector;
import io.github.ghackenberg.mbse.transport.fx.viewers.StationViewer;
import javafx.scene.shape.Rectangle;
import javafx.beans.binding.Bindings;

public class StationViewerFlat extends StationViewer {
	
	//public final Circle circle;
	public final Rectangle rectangle;
	public final double width = 1.0;
	public final double height = 1.0;

	public StationViewerFlat(Model model, Station station) {
		super(model, station);
		
		Vector location = station.location.coordinate;
		
		// Rectangle
		// location defined by left top corner
		
		rectangle = new Rectangle();
		
		rectangle.setWidth(width);
		rectangle.setHeight(height);
		rectangle.xProperty().bind(location.x.subtract(width / 2));
		rectangle.yProperty().bind(location.y.subtract(height / 2));
		//rectangle.rotateProperty().bind(location.x.multiply(10).add(location.y.multiply(0)));
		//rectangle.rotateProperty().bind(new SimpleDoubleProperty(1).multiply(location.x.multiply(10).add(location.y.multiply(0))));
		//rectangle.rotateProperty().bind(new SimpleDoubleProperty(1).multiply(location.x.multiply(10).add(location.y.multiply(0))));
		rectangle.rotateProperty().bind(
			    Bindings.createDoubleBinding(
			        () -> Math.toDegrees(Math.atan2(station.location.segment.get().tangent.y.get(), station.location.segment.get().tangent.x.get())),
			        location.x, location.y
			    )
			);
		
		//rectangle.rotateProperty().bind(new SimpleDoubleProperty( Math.atan2(station.location.segment.get().tangent.y.get(), station.location.segment.get().tangent.x.get())));

		
		rectangle.fillProperty().bind(color);
		
//		// Circle
//		
//		circle = new Circle();
//		
//		circle.setRadius(0.5);
//		
//		circle.centerXProperty().bind(location.x);
//		circle.centerYProperty().bind(location.y);
//		
//		circle.fillProperty().bind(color);
		
		// Self
		
		getChildren().add(rectangle);
	}
	
}
