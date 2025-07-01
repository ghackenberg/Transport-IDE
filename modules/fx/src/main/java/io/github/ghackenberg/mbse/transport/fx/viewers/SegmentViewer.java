package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class SegmentViewer extends EntityViewer<Segment> {
	
	public final Line line;
	
	public SegmentViewer(Model model, Segment segment) {
		super(model, segment);
		
		// Rectangle
		
		line = new Line();
		
		line.startXProperty().bind(segment.start.coordinate.x);
		line.startYProperty().bind(segment.start.coordinate.y);
		
		line.endXProperty().bind(segment.end.coordinate.x);
		line.endYProperty().bind(segment.end.coordinate.y);
		
		line.strokeWidthProperty().bind(segment.lanes);
		line.strokeProperty().bind(Bindings.when(selected).then(Color.GRAY).otherwise(Color.LIGHTGRAY));
		
		line.setStrokeLineCap(StrokeLineCap.BUTT);
		
		getChildren().add(line);
	}

	@Override
	public void update() {
		
	}

}
