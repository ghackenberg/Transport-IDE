package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Camera;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class ModelViewerDeep extends ModelViewer<IntersectionViewerDeep, SegmentViewerDeep, StationViewerDeep, VehicleViewerDeep, DemandViewerDeep, SubScene> {
	
	public final PerspectiveCamera camera = new PerspectiveCamera(true);
	
	public final AmbientLight ambient = new AmbientLight(new Color(0.5, 0.5, 0.5, 1));
	public final PointLight point = new PointLight(new Color(0.5, 0.5, 0.5, 1));

	public final Sphere center = new Sphere(0, 10);

	public final Cylinder yAxis = new Cylinder(0, 0);
	public final Cylinder xAxis = new Cylinder(0, 0);
	public final Cylinder zAxis = new Cylinder(0, 0);
	
	public final Group base = new Group(center, yAxis, xAxis, zAxis);
	public final Group main = new Group();
	public final Group root = new Group(camera, ambient, point, base, main);

	private double previousX;
	private double previousY;
	
	public ModelViewerDeep(Model model) {
		super(model, new SubScene(new Group(), 300, 300, true, SceneAntialiasing.BALANCED), true);

		// Control

		Camera control = new Camera();

		control.base.x.set(model.center.x.get());
		control.base.y.set(model.center.y.get());
		control.base.z.set(model.center.z.get());

		model.cameras.add(control);
		
		// Camera
		
		control.apply(camera);
		
		// Point
		
		control.apply(point);

		// Sphere

		center.radiusProperty().bind(control.distance.divide(200));
		center.setMaterial(new PhongMaterial(Color.BLACK));

		// Axes

		yAxis.radiusProperty().bind(center.radiusProperty().divide(3));
		yAxis.heightProperty().bind(center.radiusProperty().multiply(10));
		yAxis.translateYProperty().bind(yAxis.heightProperty().divide(2));
		yAxis.setMaterial(new PhongMaterial(Color.RED));

		xAxis.radiusProperty().bind(yAxis.radiusProperty());
		xAxis.heightProperty().bind(yAxis.heightProperty());
		xAxis.translateXProperty().bind(xAxis.heightProperty().divide(2));
		xAxis.setRotationAxis(Rotate.Z_AXIS);
		xAxis.setRotate(90);
		xAxis.setMaterial(new PhongMaterial(Color.GREEN));

		zAxis.radiusProperty().bind(yAxis.radiusProperty());
		zAxis.heightProperty().bind(yAxis.heightProperty());
		zAxis.translateZProperty().bind(zAxis.heightProperty().divide(-2));
		zAxis.setRotationAxis(Rotate.X_AXIS);
		zAxis.setRotate(90);
		zAxis.setMaterial(new PhongMaterial(Color.BLUE));

		// Base

		base.translateXProperty().bind(control.base.x);
		base.translateYProperty().bind(control.base.y);
		base.translateZProperty().bind(control.base.z);

		base.setVisible(false);
		
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

		// Self

		setOnScroll(event -> {
			control.distance.set(control.distance.get() * (event.getDeltaY() > 0 ? 0.95 : 1.05));
			event.consume();
		});
		setOnDragDetected(event -> {
			startFullDrag();

			previousX = event.getX();
			previousY = event.getY();

			if (event.isPrimaryButtonDown()) {
				base.setVisible(true);
			}

			event.consume();
		});
		setOnMouseDragOver(event -> {
			double dx = event.getX() - previousX;
			double dy = event.getY() - previousY;
			
			if (event.isPrimaryButtonDown()) {
				control.azimuth.set(control.azimuth.get() + dx / 10);
				control.zenith.set(control.zenith.get() - dy / 10);
			} else if (event.isSecondaryButtonDown()) {
				Point3D up = control.up.toPoint3D().multiply(-dy / 20);
				Point3D left = control.left.toPoint3D().multiply(dx / 20);
				Point3D base = control.base.toPoint3D().add(up).add(left);
				control.base.assign(base);
			}

			previousX = event.getX();
			previousY = event.getY();

			event.consume();
		});
		setOnMouseDragExited(event -> {
			if (event.isPrimaryButtonDown()) {
				base.setVisible(false);
			}
		});
		setOnMouseDragEntered(event -> {
			if (event.isPrimaryButtonDown()) {
				base.setVisible(true);
			}
		});
		setOnMouseDragReleased(event -> {
			base.setVisible(false);

			event.consume();
		});
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
