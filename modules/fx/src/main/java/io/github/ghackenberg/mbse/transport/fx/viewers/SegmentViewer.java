package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.fx.events.SegmentEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class SegmentViewer extends EntityViewer<Segment, SegmentEvent> {
	
	private final Line line;
	
	public SegmentViewer(Model model, Segment segment) {
		super(model, segment);
		
		setManaged(false);
		
		// Rectangle
		
		line = new Line();
		
		line.startXProperty().bind(segment.start.coordinate.x);
		line.startYProperty().bind(segment.start.coordinate.y);
		
		line.endXProperty().bind(segment.end.coordinate.x);
		line.endYProperty().bind(segment.end.coordinate.y);
		
		line.strokeWidthProperty().bind(segment.lanes);
		line.setStroke(Color.LIGHTGRAY);
		
		line.setOnMouseClicked(event -> {
			event.consume();
			// TODO
		});
		
		getChildren().add(line);
	}

	@Override
	public void update() {
		
	}

}
