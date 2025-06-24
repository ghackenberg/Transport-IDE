package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Coordinate;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.LocationTime;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class DemandViewer extends Group {

	private Model model;
	private Demand demand;
	
	private Line line;
	
	private Circle source;
	private Circle target;
	
	public DemandViewer(Model model, Demand demand) {
		this.model = model;
		this.demand = demand;
		
		setManaged(false);
		
		Coordinate start = demand.pickup.location.toCoordinate();
		Coordinate end = demand.dropoff.location.toCoordinate();
		
		// Line
		
		line = new Line();
		
		line.setStartX(start.latitude);
		line.setStartY(start.longitude);
		
		line.setEndX(end.latitude);
		line.setEndY(end.longitude);
		
		line.setStroke(Color.GREEN);
		
		getChildren().add(line);
		
		// Source
		
		source = new Circle();
		
		source.setRadius(demand.size);
		
		source.setCenterX(start.latitude);
		source.setCenterY(start.longitude);
		
		source.setFill(Color.GREEN);
		
		getChildren().add(source);
		
		// Target
		
		target = new Circle();
		
		target.setRadius(demand.size);
		
		target.setCenterX(end.latitude);
		target.setCenterY(end.longitude);
		
		target.setFill(Color.GREEN);
		
		getChildren().add(target);
	}
	
	public void update() {
		Vehicle vehicle = demand.vehicle;
		
		LocationTime pickup = demand.pickup;
		
		Coordinate start = vehicle != null ? vehicle.location.toCoordinate() : pickup.location.toCoordinate();
		
		if (!demand.done && model.time >= demand.pickup.time) {
			setVisible(true);
			
			line.setStartX(start.latitude);
			line.setStartY(start.longitude);
			
			source.setCenterX(start.latitude);
			source.setCenterY(start.longitude);
			
			if (model.time > demand.dropoff.time) {
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
