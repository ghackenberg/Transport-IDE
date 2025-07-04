package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import javafx.scene.paint.Color;

public abstract class SegmentViewer extends EntityViewer<Segment> {
	
	public final Segment.State entityState = entity.state.get();

	public SegmentViewer(Model model, Segment entity) {
		super(model, entity, Color.LIGHTGRAY, Color.GRAY);
	}

	@Override
	public void update() {
		
	}

}
