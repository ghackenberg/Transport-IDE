package io.github.ghackenberg.mbse.transport.fx.helpers;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class GridHelper {

	public static ColumnConstraints createColumnConstraints(boolean fillWidth, Priority hgrow) {
		ColumnConstraints result = new ColumnConstraints();
		
		result.setFillWidth(fillWidth);
		result.setHgrow(hgrow);
		
		return result;
	}

	public static RowConstraints createRowConstraints(boolean fillHeight, Priority vgrow) {
		RowConstraints result = new RowConstraints();
		
		result.setFillHeight(fillHeight);
		result.setVgrow(vgrow);
		
		return result;
	}

	public static RowConstraints createRowConstraints(double height) {
		RowConstraints result = new RowConstraints();
		
		result.setPercentHeight(height);
		result.setFillHeight(true);
		result.setVgrow(Priority.ALWAYS);
		
		return result;
	}
	
}
