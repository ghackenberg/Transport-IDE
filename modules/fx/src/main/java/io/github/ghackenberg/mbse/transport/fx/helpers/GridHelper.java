package io.github.ghackenberg.mbse.transport.fx.helpers;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;

public class GridHelper {

	public static ColumnConstraints createColumnConstraints(boolean fillWidth, Priority hgrow) {
		ColumnConstraints result = new ColumnConstraints();
		
		result.setFillWidth(fillWidth);
		result.setHgrow(hgrow);
		
		return result;
	}
	
}
