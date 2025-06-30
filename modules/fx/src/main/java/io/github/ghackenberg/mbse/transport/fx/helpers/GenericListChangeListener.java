package io.github.ghackenberg.mbse.transport.fx.helpers;

import javafx.collections.ListChangeListener;

public class GenericListChangeListener<T> implements ListChangeListener<T> {
	
	public interface Handler<T> {
		public void handle(T item);
	}
	
	private Handler<T> handleRemoved;
	private Handler<T> handleAdded;
	
	public GenericListChangeListener(Handler<T> handleRemoved, Handler<T> handleAdded) {
		this.handleRemoved = handleRemoved;
		this.handleAdded = handleAdded;
	}

	@Override
	public void onChanged(Change<? extends T> c) {
		while (c.next()) {
			for (T item : c.getRemoved()) {
				handleRemoved.handle(item);
			}
			for (T item : c.getAddedSubList()) {
				handleAdded.handle(item);
			}
		}
	}

}
