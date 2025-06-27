package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.fx.events.SegmentEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class SegmentViewer extends EntityViewer<Segment, SegmentEvent> {
	
	private Line line;
	
	public SegmentViewer(Model model, Segment segment) {
		super(segment);
		
		setManaged(false);
		
		// Rectangle
		
		line = new Line();
		
		line.startXProperty().bind(segment.start.getCoordinate().xProperty());
		line.startYProperty().bind(segment.start.getCoordinate().yProperty());
		
		line.endXProperty().bind(segment.end.getCoordinate().xProperty());
		line.endYProperty().bind(segment.end.getCoordinate().yProperty());
		
		line.strokeWidthProperty().bind(segment.lanesProperty());
		line.setStroke(Color.LIGHTGRAY);
		
		line.setOnMouseClicked(event -> {
			// TODO
		});
		
		getChildren().add(line);
	}
	
	public void update() {
		
	}

}
