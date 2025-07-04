package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.fx.viewers.SegmentViewer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class SegmentViewerFlat extends SegmentViewer {
	
	private static final double FACTOR = 3;
	
	public final Line line = new Line();
	
	public final Line arrowTail = new Line();
	public final Line arrowHeadLeft = new Line();
	public final Line arrowHeadRight = new Line();
	
	public SegmentViewerFlat(Model model, Segment segment) {
		super(model, segment);
		
		// Rectangle
		
		line.startXProperty().bind(segment.start.coordinate.x);
		line.startYProperty().bind(segment.start.coordinate.y);
		
		line.endXProperty().bind(segment.end.coordinate.x);
		line.endYProperty().bind(segment.end.coordinate.y);
		
		line.strokeWidthProperty().bind(segment.lanes);
		line.strokeProperty().bind(color);
		
		line.setStrokeLineCap(StrokeLineCap.BUTT);
		
		// Arrow tail
		
		arrowTail.startXProperty().bind(segment.center.x.subtract(segment.tangent.x.multiply(segment.lanes).divide(FACTOR)));
		arrowTail.startYProperty().bind(segment.center.y.subtract(segment.tangent.y.multiply(segment.lanes).divide(FACTOR)));
		
		arrowTail.endXProperty().bind(segment.center.x.add(segment.tangent.x.multiply(segment.lanes).divide(FACTOR)));
		arrowTail.endYProperty().bind(segment.center.y.add(segment.tangent.y.multiply(segment.lanes).divide(FACTOR)));
		
		arrowTail.strokeWidthProperty().bind(segment.lanes.divide(FACTOR * 3));
		arrowTail.setStroke(Color.WHITE);
		arrowTail.setStrokeLineCap(StrokeLineCap.ROUND);
		
		// Arrow head left
		
		arrowHeadLeft.startXProperty().bind(segment.center.x.subtract(segment.tangent.y.multiply(segment.lanes).divide(FACTOR)));
		arrowHeadLeft.startYProperty().bind(segment.center.y.add(segment.tangent.x.multiply(segment.lanes).divide(FACTOR)));
		
		arrowHeadLeft.endXProperty().bind(segment.center.x.add(segment.tangent.x.multiply(segment.lanes).divide(FACTOR)));
		arrowHeadLeft.endYProperty().bind(segment.center.y.add(segment.tangent.y.multiply(segment.lanes).divide(FACTOR)));

		arrowHeadLeft.strokeWidthProperty().bind(segment.lanes.divide(FACTOR * 3));
		arrowHeadLeft.setStroke(Color.WHITE);
		arrowHeadLeft.setStrokeLineCap(StrokeLineCap.ROUND);
		
		// Arrow head right
		
		arrowHeadRight.startXProperty().bind(segment.center.x.add(segment.tangent.y.multiply(segment.lanes).divide(FACTOR)));
		arrowHeadRight.startYProperty().bind(segment.center.y.subtract(segment.tangent.x.multiply(segment.lanes).divide(FACTOR)));
		
		arrowHeadRight.endXProperty().bind(segment.center.x.add(segment.tangent.x.multiply(segment.lanes).divide(FACTOR)));
		arrowHeadRight.endYProperty().bind(segment.center.y.add(segment.tangent.y.multiply(segment.lanes).divide(FACTOR)));

		arrowHeadRight.strokeWidthProperty().bind(segment.lanes.divide(FACTOR * 3));
		arrowHeadRight.setStroke(Color.WHITE);
		arrowHeadRight.setStrokeLineCap(StrokeLineCap.ROUND);
		
		// Self
		
		getChildren().add(line);
		getChildren().add(arrowTail);
		getChildren().add(arrowHeadLeft);
		getChildren().add(arrowHeadRight);
	}

}
