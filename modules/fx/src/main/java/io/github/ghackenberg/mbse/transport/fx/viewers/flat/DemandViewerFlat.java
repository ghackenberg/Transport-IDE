package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.fx.viewers.DemandViewer;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DemandViewerFlat extends DemandViewer {
	
	public final Line line;
	
	public final Circle source;
	public final Circle target;
	
	public final Text sourceText;
	public final Text targetText;
	
	public DemandViewerFlat(Model model, Demand demand) {
		super(model, demand);
		
		// Line
		
		line = new Line();
		
		line.startXProperty().bind(start.x);
		line.startYProperty().bind(start.y);
		
		line.endXProperty().bind(end.x);
		line.endYProperty().bind(end.y);
		
		line.strokeProperty().bind(color);
		line.strokeWidthProperty().bind(demand.size.divide(10));
		
		// Source
		
		source = new Circle();
		
		source.radiusProperty().bind(demand.size.divide(2));
		
		source.centerXProperty().bind(start.x);
		source.centerYProperty().bind(start.y);
		
		source.fillProperty().bind(color);
		
		// Target
		
		target = new Circle();
		
		target.radiusProperty().bind(demand.size.divide(2));
		
		target.centerXProperty().bind(end.x);
		target.centerYProperty().bind(end.y);
		
		target.fillProperty().bind(color);
		
		// Source text
		
		sourceText = new Text("S");

		sourceText.yProperty().bind(location.coordinate.y);

		sourceText.setTextAlignment(TextAlignment.CENTER);
		sourceText.setTextOrigin(VPos.CENTER);
		
		sourceText.setFill(Color.WHITE);

		sourceText.setFont(new Font(0.5));
		sourceText.setX(location.coordinate.x.get() - sourceText.getBoundsInLocal().getWidth() / 2);
		
		// Target text
		
		targetText = new Text("T");

		targetText.yProperty().bind(demand.drop.location.coordinate.y);

		targetText.setTextAlignment(TextAlignment.CENTER);
		targetText.setTextOrigin(VPos.CENTER);
		
		targetText.setFill(Color.WHITE);

		targetText.setFont(new Font(0.5));
		targetText.setX(demand.drop.location.coordinate.x.get() - targetText.getBoundsInLocal().getWidth() / 2);
		
		// Self
		
		getChildren().add(line);
		
		getChildren().add(source);
		getChildren().add(target);
		
		getChildren().add(sourceText);
		getChildren().add(targetText);
		
		// Listeners

		location.coordinate.x.addListener(event -> {
			sourceText.setX(location.coordinate.x.get() - sourceText.getBoundsInLocal().getWidth() / 2);
		});
		demand.drop.location.coordinate.x.addListener(event -> {
			targetText.setX(demand.drop.location.coordinate.x.get() - targetText.getBoundsInLocal().getWidth() / 2);
		});
	}
	
}
