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
	
	private Circle circle;
	
	private Text text;
	
	public IntersectionViewer(Model model, Intersection intersection) {
		super(intersection);
		
		setManaged(false);
		
		double radius = 0;
		
		for (Segment segment : intersection.incoming) {
			radius = Math.max(radius, segment.lanes / 2.);
		}
		for (Segment segment : intersection.outgoing) {
			radius = Math.max(radius, segment.lanes / 2.);
		}
		
		// Circle
		
		circle = new Circle();
		
		circle.setRadius(radius);
		
		circle.centerXProperty().bind(intersection.getCoordinate().xProperty());
		circle.centerYProperty().bind(intersection.getCoordinate().yProperty());
		
		circle.setFill(Color.BLACK);
		
		circle.setOnMouseClicked(event -> {
			circle.setFill(Color.RED);
			if (getOnSelected() != null) {
				getOnSelected().handle(new IntersectionEvent(getEntity()));
			}
		});
		circle.setOnMouseDragged(event -> {
			// TODO start moving intersection
		});
		circle.setOnMouseDragReleased(event -> {
			// TODO stop moving intersection
		});
		
		getChildren().add(circle);
		
		// Text
		
		text = new Text();
		
		text.textProperty().bind(intersection.nameProperty());
		
		text.setFont(new Font(radius));
		
		text.xProperty().bind(intersection.getCoordinate().xProperty());
		text.yProperty().bind(intersection.getCoordinate().yProperty());
		
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);
		
		text.setFill(Color.WHITE);
		
		text.setOnMouseClicked(event -> {
			circle.setFill(Color.RED);
			if (getOnSelected() != null) {
				getOnSelected().handle(new IntersectionEvent(getEntity()));
			}
		});
		
		getChildren().add(text);
	}
	
	public void update() {
		
	}
	
}
