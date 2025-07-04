package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class ModelViewerDeep extends ModelViewer<IntersectionViewerDeep, SegmentViewerDeep, StationViewerDeep, VehicleViewerDeep, DemandViewerDeep, SubScene> {
	
	public final PerspectiveCamera camera = new PerspectiveCamera(true);
	
	public final AmbientLight ambient = new AmbientLight(new Color(0.5, 0.5, 0.5, 1));
	
	public final PointLight point = new PointLight(new Color(0.5, 0.5, 0.5, 1));
	
	public final Group main = new Group();
	
	public final Group root = new Group(camera, ambient, point, main);
	
	public ModelViewerDeep(Model model) {
		super(model, new SubScene(new Group(), 300, 300, true, SceneAntialiasing.BALANCED), true);
		
		// Camera
		
		camera.translateZProperty().bind(model.deltaY.multiply(-3));
		
		// Point
		
		point.translateXProperty().bind(camera.translateXProperty().add(10));
		point.translateYProperty().bind(camera.translateYProperty().add(-10));
		point.translateZProperty().bind(camera.translateZProperty());
		
		// Main
		
		main.setDepthTest(DepthTest.ENABLE);
		
		main.getChildren().add(segmentLayer);
		main.getChildren().add(intersectionLayer);
		main.getChildren().add(stationLayer);
		main.getChildren().add(vehicleLayer);
		main.getChildren().add(demandLayer);
		
		main.getTransforms().add(new Rotate(-70, Rotate.X_AXIS));
		
		main.translateXProperty().bind(model.minX.multiply(-1).subtract(model.deltaX.divide(2)));
		main.translateYProperty().bind(model.minY.multiply(-1).subtract(model.deltaY.divide(2)));
		main.translateZProperty().bind(model.minZ.multiply(-1).subtract(model.deltaZ.divide(2)));
		
		// Canvas
		
		canvas.setDepthTest(DepthTest.ENABLE);
		canvas.setCamera(camera);
		canvas.setRoot(root);
		
		canvas.widthProperty().bind(widthProperty());
		canvas.heightProperty().bind(heightProperty());
	}
	
	// Create

	@Override
	protected IntersectionViewerDeep create(Intersection intersection) {
		return new IntersectionViewerDeep(model, intersection);
	}

	@Override
	protected SegmentViewerDeep create(Segment segment) {
		return new SegmentViewerDeep(model, segment);
	}

	@Override
	protected StationViewerDeep create(Station station) {
		return new StationViewerDeep(model, station);
	}

	@Override
	protected VehicleViewerDeep create(Vehicle vehicle) {
		return new VehicleViewerDeep(model, vehicle);
	}

	@Override
	protected DemandViewerDeep create(Demand demand) {
		return new DemandViewerDeep(model, demand);
	}

}
