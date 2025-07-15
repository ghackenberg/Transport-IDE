package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.structures.Vector;
import io.github.ghackenberg.mbse.transport.fx.viewers.StationViewer;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class StationViewerFlat extends StationViewer {
	
	//public final Circle circle;
	public final Rectangle rectangle;
	public final double width = 1.0;
	public final double height = 1.0;
	private double normalX;
	private double normalY;
	private double angle;

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
		
		// get angle from segment tangent normal
		normalX = station.location.segment.get().tangentNormal.x.get();
		normalY = station.location.segment.get().tangentNormal.y.get();
		angle = Math.atan2(normalY, normalX);

		// rotate rectangle to match segment tangent normal
		rectangle.setRotate(Math.toDegrees(angle));
		
		rectangle.fillProperty().bind(color);
		
		// Circle
		
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
