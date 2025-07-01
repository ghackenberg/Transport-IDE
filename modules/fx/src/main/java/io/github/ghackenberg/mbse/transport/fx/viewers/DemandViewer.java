package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.structures.Coordinate;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class DemandViewer extends EntityViewer<Demand> {
	
	public final Demand.State entityState;
	
	public final Location location;
	
	public final Line line;
	
	public final Circle source;
	public final Circle target;
	
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
		
		line.setStroke(Color.GREEN);
		
		getChildren().add(line);
		
		// Source
		
		source = new Circle();
		
		source.radiusProperty().bind(demand.size);
		
		source.centerXProperty().bind(start.x);
		source.centerYProperty().bind(start.y);
		
		source.setFill(Color.GREEN);
		
		getChildren().add(source);
		
		// Target
		
		target = new Circle();
		
		target.radiusProperty().bind(demand.size);
		
		target.centerXProperty().bind(end.x);
		target.centerYProperty().bind(end.y);
		
		target.setFill(Color.GREEN);
		
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
