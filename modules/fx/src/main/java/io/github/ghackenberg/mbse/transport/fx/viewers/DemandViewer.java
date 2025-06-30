package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import io.github.ghackenberg.mbse.transport.fx.events.DemandEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class DemandViewer extends EntityViewer<Demand, DemandEvent> {
	
	private final Demand.State entityState;
	
	private final Location location;
	
	private final Line line;
	
	private final Circle source;
	private final Circle target;
	
	public DemandViewer(Model model, Demand demand) {
		super(model, demand);
		
		entityState = demand.state.get();
		
		setManaged(false);
		
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
		
		line.setStroke(Color.GREEN);
		
		line.setOnMouseClicked(event -> {
			event.consume();
			// TODO
		});
		
		getChildren().add(line);
		
		// Source
		
		source = new Circle();
		
		source.radiusProperty().bind(demand.size);
		
		source.centerXProperty().bind(start.x);
		source.centerYProperty().bind(start.y);
		
		source.setFill(Color.GREEN);
		
		source.setOnMouseClicked(event -> {
			event.consume();
			// TODO
		});
		
		getChildren().add(source);
		
		// Target
		
		target = new Circle();
		
		target.radiusProperty().bind(demand.size);
		
		target.centerXProperty().bind(end.x);
		target.centerYProperty().bind(end.y);
		
		target.setFill(Color.GREEN);
		
		target.setOnMouseClicked(event -> {
			event.consume();
			// TODO
		});
		
		getChildren().add(target);
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
