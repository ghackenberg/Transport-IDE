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
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

public class ModelViewerDeep extends ModelViewer<IntersectionViewerDeep, SegmentViewerDeep, StationViewerDeep, VehicleViewerDeep, DemandViewerDeep, SubScene> {
	
	public final PerspectiveCamera camera = new PerspectiveCamera(true);
	
	public final AmbientLight ambient = new AmbientLight(new Color(0.2, 0.2, 0.2, 1));
	
	public final PointLight point = new PointLight(new Color(0.8, 0.8, 0.8, 1));
	
	public final Group main = new Group();
	
	public final Group root = new Group(camera, ambient, point, main);
	
	public ModelViewerDeep(Model model) {
		super(model, new SubScene(new Group(), 300, 300), true);
		
		// Camera
		
		camera.setTranslateZ(-30);
		
		// Point
		
		point.setTranslateY(10);
		
		// Main
		
		main.getChildren().add(segmentLayer);
		main.getChildren().add(intersectionLayer);
		main.getChildren().add(stationLayer);
		main.getChildren().add(vehicleLayer);
		main.getChildren().add(demandLayer);
		
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
