package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.fx.viewers.IntersectionViewer;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class IntersectionViewerFlat extends IntersectionViewer {
	
	public final Circle circle;
	
	public final Text text;
	
	public IntersectionViewerFlat(Model model, Intersection intersection) {
		super(model, intersection);
		
		// Circle
		
		circle = new Circle();
		
		circle.radiusProperty().bind(intersection.lanes.divide(2));
		
		circle.centerXProperty().bind(intersection.coordinate.x);
		circle.centerYProperty().bind(intersection.coordinate.y);
		
		circle.fillProperty().bind(color);
		
		// Text
		
		text = new Text();
		
		text.textProperty().bind(intersection.name);
		
		text.yProperty().bind(intersection.coordinate.y);

		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);
		
		text.setFill(Color.BLACK);

		text.setFont(new Font(0.5));
		text.setX(intersection.coordinate.x.get() - text.getBoundsInLocal().getWidth() / 2);
		
		// Self
		
		getChildren().add(circle);
		getChildren().add(text);
		
		// Listeners

		intersection.name.addListener(event -> {
			text.setX(intersection.coordinate.x.get() - text.getBoundsInLocal().getWidth() / 2);
		});
		intersection.coordinate.x.addListener(event -> {
			text.setX(intersection.coordinate.x.get() - text.getBoundsInLocal().getWidth() / 2);
		});
	}
	
}
