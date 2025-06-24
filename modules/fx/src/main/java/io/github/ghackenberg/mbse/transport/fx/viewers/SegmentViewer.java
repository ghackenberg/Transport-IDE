package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class SegmentViewer extends Group {
	
	private Line line;
	
	public SegmentViewer(Model model, Segment segment) {
		
		setManaged(false);
		
		// Rectangle
		
		line = new Line();
		
		line.setStartX(segment.start.coordinate.latitude);
		line.setStartY(segment.start.coordinate.longitude);
		
		line.setEndX(segment.end.coordinate.latitude);
		line.setEndY(segment.end.coordinate.longitude);
		
		line.setStrokeWidth(segment.lanes);
		line.setStroke(Color.LIGHTGRAY);
		
		getChildren().add(line);
	}
	
	public void update() {
		
	}

}
