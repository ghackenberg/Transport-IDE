package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import javafx.beans.binding.Bindings;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DemandViewer extends EntityViewer<Demand> {
	
	public final Demand.State entityState;
	
	public final Location location;
	
	public final Line line;
	
	public final Circle source;
	public final Circle target;
	
	public final Text sourceText;
	public final Text targetText;
	
	public DemandViewer(Model model, Demand demand) {
		super(model, demand);
		
		entityState = demand.state.get();
		
		// Location
		
		if (entityState == null) {
			location = demand.pick.location;
		} else {
			location = new Location();
			location.segment.set(entityState.segment);
			location.distance.set(entityState.distance);
		}
		
		final Coordinate start = location.coordinate;
		final Coordinate end = demand.drop.location.coordinate;
		
		// Line
		
		line = new Line();
		
		line.startXProperty().bind(start.x);
		line.startYProperty().bind(start.y);
		
		line.endXProperty().bind(end.x);
		line.endYProperty().bind(end.y);
		
		line.strokeProperty().bind(Bindings.when(selected).then(Color.GREEN).otherwise(Color.LIMEGREEN));
		line.strokeWidthProperty().bind(demand.size.divide(2));
		
		getChildren().add(line);
		
		// Source
		
		source = new Circle();
		
		source.radiusProperty().bind(demand.size.divide(2));
		
		source.centerXProperty().bind(start.x);
		source.centerYProperty().bind(start.y);
		
		source.fillProperty().bind(Bindings.when(selected).then(Color.GREEN).otherwise(Color.LIMEGREEN));
		
		getChildren().add(source);
		
		// Target
		
		target = new Circle();
		
		target.radiusProperty().bind(demand.size.divide(2));
		
		target.centerXProperty().bind(end.x);
		target.centerYProperty().bind(end.y);
		
		target.fillProperty().bind(Bindings.when(selected).then(Color.GREEN).otherwise(Color.LIMEGREEN));
		
		getChildren().add(target);
		
		// Source text
		
		sourceText = new Text("S");

		sourceText.yProperty().bind(demand.pick.location.coordinate.y);

		sourceText.setTextAlignment(TextAlignment.CENTER);
		sourceText.setTextOrigin(VPos.CENTER);
		
		sourceText.setFill(Color.WHITE);

		sourceText.setFont(new Font(0.5));
		sourceText.setX(demand.pick.location.coordinate.x.get() - sourceText.getBoundsInLocal().getWidth() / 2);
		
		getChildren().add(sourceText);
		
		// Target text
		
		targetText = new Text("T");

		targetText.yProperty().bind(demand.drop.location.coordinate.y);

		targetText.setTextAlignment(TextAlignment.CENTER);
		targetText.setTextOrigin(VPos.CENTER);
		
		targetText.setFill(Color.WHITE);

		targetText.setFont(new Font(0.5));
		targetText.setX(demand.drop.location.coordinate.x.get() - targetText.getBoundsInLocal().getWidth() / 2);
		
		getChildren().add(targetText);
		
		// Listeners

		demand.pick.location.coordinate.x.addListener(event -> {
			sourceText.setX(demand.pick.location.coordinate.x.get() - sourceText.getBoundsInLocal().getWidth() / 2);
		});
		demand.drop.location.coordinate.x.addListener(event -> {
			targetText.setX(demand.drop.location.coordinate.x.get() - targetText.getBoundsInLocal().getWidth() / 2);
		});
	}
	
	@Override
	public void update() {
		location.segment.set(entityState.segment);
		location.distance.set(entityState.distance);
		
		if (!entityState.done && modelState.time >= entity.pick.time.get()) {
			setVisible(true);
			
			if (modelState.time > entity.drop.time.get()) {
				line.setStroke(Color.RED);
				
				source.setFill(Color.RED);
				target.setFill(Color.RED);
			} else {
				line.setStroke(Color.GREEN);
				
				source.setFill(Color.GREEN);
				target.setFill(Color.GREEN);
			}
		} else {
			setVisible(false);
		}
	}
	
}
