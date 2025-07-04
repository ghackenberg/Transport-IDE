package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.animation.AnimationTimer;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ModelViewerDeep extends ModelViewer<IntersectionViewerDeep, SegmentViewerDeep, StationViewerDeep, VehicleViewerDeep, DemandViewerDeep, SubScene> {
	
	public final PerspectiveCamera camera = new PerspectiveCamera(true);
	
	public final Group root = new Group(camera);
	
	public ModelViewerDeep(Model model) {
		super(model, new SubScene(new Group(), 300, 300), true);
		
		// Camera
		
		Rotate rotateX = new Rotate(-45, Rotate.X_AXIS);
		Rotate rotateZ = new Rotate(45, Rotate.Y_AXIS);
		
		Translate translate = new Translate(0, 0, -30);
        
		camera.getTransforms().addAll(rotateX, rotateZ, translate);
		
		// Root
		
		root.getChildren().add(segmentLayer);
		root.getChildren().add(intersectionLayer);
		root.getChildren().add(stationLayer);
		root.getChildren().add(vehicleLayer);
		root.getChildren().add(demandLayer);
		
		// Canvas
		
		canvas.setDepthTest(DepthTest.ENABLE);
		canvas.setCamera(camera);
		canvas.setRoot(root);
		
		canvas.widthProperty().bind(widthProperty());
		canvas.heightProperty().bind(heightProperty());
		
		// Animation
		
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				rotateZ.setAngle(rotateZ.getAngle() + 0.1);
			}
		}.start();
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
