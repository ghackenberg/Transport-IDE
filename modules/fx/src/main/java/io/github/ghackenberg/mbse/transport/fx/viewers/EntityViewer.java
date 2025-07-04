package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.DepthTest;
import javafx.scene.Group;

public abstract class EntityViewer<T> extends Group {

	public final Model model;
	public final Model.State modelState;
	
	public final T entity;
	
	public final BooleanProperty selected = new SimpleBooleanProperty(false);
	
	public EntityViewer(Model model, T entity) {
		this.model = model;
		this.modelState = model.state.get();
		
		this.entity = entity;
		
		setManaged(false);
		setDepthTest(DepthTest.ENABLE);
	}
	
	public abstract void update();
	
}
