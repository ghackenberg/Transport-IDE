package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.fx.events.IntersectionEvent;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class IntersectionViewer extends EntityViewer<Intersection, IntersectionEvent> {
	
	public final Circle circle;
	
	public final Text text;
	
	public IntersectionViewer(Model model, Intersection intersection) {
		super(model, intersection);
		
		setManaged(false);
		
		double radius = 0.5;
		
		for (Segment segment : intersection.incoming) {
			radius = Math.max(radius, segment.lanes.get() / 2.);
		}
		for (Segment segment : intersection.outgoing) {
			radius = Math.max(radius, segment.lanes.get() / 2.);
		}
		
		// Circle
		
		circle = new Circle();
		
		circle.setRadius(radius);
		
		circle.centerXProperty().bind(intersection.coordinate.x);
		circle.centerYProperty().bind(intersection.coordinate.y);
		
		circle.setFill(Color.BLACK);
		
		getChildren().add(circle);
		
		// Text
		
		text = new Text();
		
		text.textProperty().bind(intersection.name);
		
		text.setFont(new Font(radius));
		
		text.xProperty().bind(intersection.coordinate.x);
		text.yProperty().bind(intersection.coordinate.y);
		
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);
		
		text.setFill(Color.WHITE);
		
		getChildren().add(text);
	}

	@Override
	public void update() {
		
	}
	
}
