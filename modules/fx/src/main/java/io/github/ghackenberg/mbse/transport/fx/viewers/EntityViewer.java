package io.github.ghackenberg.mbse.transport.fx.viewers;

import io.github.ghackenberg.mbse.transport.core.Model;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;

public abstract class EntityViewer<T> extends Group {

	public final Model model;
	public final Model.State modelState;
	
	public final T entity;
	
	public final BooleanProperty selected = new SimpleBooleanProperty(false);
	
	public ObjectBinding<Color> color;
	
	public EntityViewer(Model model, T entity) {
		this.model = model;
		this.modelState = model.state.get();
		
		this.entity = entity;
		
		setManaged(false);
		setDepthTest(DepthTest.ENABLE);
	}
	
	public EntityViewer(Model model, T entity, Color colorDefault, Color colorSelected) {
		this(model, entity);
		
		color = Bindings.when(selected).then(colorSelected).otherwise(colorDefault);
	}
	
	public abstract void update();
	
}
