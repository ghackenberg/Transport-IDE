package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import javafx.scene.paint.Color;

public abstract class IntersectionViewer extends EntityViewer<Intersection> {

	public IntersectionViewer(Model model, Intersection entity) {
		super(model, entity, Color.ORANGE, Color.RED);
	}

	@Override
	public void update() {
		
	}

}
