package io.github.ghackenberg.mbse.transport.fx.helpers;

import javafx.scene.image.ImageView;

public class ImageViewHelper {

	public static ImageView load(String url, double fitWidth, double fitHeight) {
		ImageView result = new ImageView(url);
		
		result.setFitWidth(fitWidth);
		result.setFitHeight(fitHeight);
		
		return result;
	}
	
}
