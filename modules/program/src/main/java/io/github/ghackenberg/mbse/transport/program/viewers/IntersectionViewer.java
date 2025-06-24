package example.program.viewers;

import example.model.Intersection;
import example.model.Model;
import example.model.Segment;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class IntersectionViewer extends Group {
	
	private Circle circle;
	
	private Text text;
	
	public IntersectionViewer(Model model, Intersection intersection) {
		
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
		
		circle.setCenterX(intersection.coordinate.latitude);
		circle.setCenterY(intersection.coordinate.longitude);
		
		circle.setFill(Color.BLACK);
		
		getChildren().add(circle);
		
		// Text
		
		text = new Text(intersection.name);
		
		text.setFont(new Font(radius));
		
		text.setX(intersection.coordinate.latitude);
		text.setY(intersection.coordinate.longitude);
		
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);
		
		text.setFill(Color.WHITE);
		
		getChildren().add(text);
	}
	
	public void update() {
		
	}
	
}
