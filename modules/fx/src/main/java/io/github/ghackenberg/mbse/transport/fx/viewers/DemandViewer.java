package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Coordinate;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.fx.events.DemandEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class DemandViewer extends EntityViewer<Demand, DemandEvent> {

	private Model model;
	
	private Line line;
	
	private Circle source;
	private Circle target;
	
	public DemandViewer(Model model, Demand demand) {
		super(demand);
		
		this.model = model;
		
		setManaged(false);
		
		Coordinate start = demand.pickup.location.getCoordinate();
		Coordinate end = demand.dropoff.location.getCoordinate();
		
		// Line
		
		line = new Line();
		
		line.startXProperty().bind(start.xProperty());
		line.startYProperty().bind(start.yProperty());
		
		line.endXProperty().bind(end.xProperty());
		line.endYProperty().bind(end.yProperty());
		
		line.setStroke(Color.GREEN);
		
		getChildren().add(line);
		
		// Source
		
		source = new Circle();
		
		source.setRadius(demand.size);
		
		source.centerXProperty().bind(start.xProperty());
		source.centerYProperty().bind(start.yProperty());
		
		source.setFill(Color.GREEN);
		
		getChildren().add(source);
		
		// Target
		
		target = new Circle();
		
		target.setRadius(demand.size);
		
		target.centerXProperty().bind(end.xProperty());
		target.centerYProperty().bind(end.yProperty());
		
		target.setFill(Color.GREEN);
		
		getChildren().add(target);
	}
	
	public void update() {
		if (!getEntity().done && model.time >= getEntity().pickup.time) {
			setVisible(true);
			
			if (model.time > getEntity().dropoff.time) {
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
