package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.transform.NonInvertibleTransformException;

public class ModelViewerFlat extends ModelViewer<IntersectionViewerFlat, SegmentViewerFlat, StationViewerFlat, VehicleViewerFlat, DemandViewerFlat, Pane> {

	public final GridViewerFlat grid = new GridViewerFlat();
	
	public ModelViewerFlat(Model model) {
		this(model, true);
	}
	
	public ModelViewerFlat(Model model, boolean showDemands) {
		super(model, new Pane(), showDemands);

		// Grid

		grid.prefWidthProperty().bind(widthProperty());
		grid.prefHeightProperty().bind(heightProperty());

		grid.x.bind(canvas.translateXProperty());
		grid.y.bind(canvas.translateYProperty());
		grid.s.bind(canvas.scaleXProperty());

		// Canvas
		
		canvas.setPrefWidth(0);
		canvas.setPrefHeight(0);
		
		canvas.getChildren().add(segmentLayer);
		canvas.getChildren().add(intersectionLayer);
		canvas.getChildren().add(stationLayer);
		canvas.getChildren().add(vehicleLayer);
		canvas.getChildren().add(demandLayer);

		// Self

		getChildren().add(0, grid);
		
		widthProperty().addListener(event -> updateTransform());
		heightProperty().addListener(event -> updateTransform());
		
		setOnScroll(event -> {
			try {
				double scrollX = -event.getDeltaX() * Math.max(deltaX, deltaY) * 0.005;
				double scrollY = -event.getDeltaY() * Math.max(deltaX, deltaY) * 0.005;
				
				if (event.isShiftDown()) {
					// Horizontal scroll
					
					minX += scrollX;
					
				} else if (event.isControlDown()) {
					// Vertical scroll
					
					minY += scrollY;
					
				} else if (deltaX + scrollY > 0 && deltaY + scrollY > 0) {
					// Pan and zoom
					
					deltaX += scrollY;
					deltaY += scrollY;
					
					Point2D point = canvas.getLocalToSceneTransform().createInverse().transform(event.getSceneX(), event.getSceneY());
					
					minX -= scrollY * (point.getX() - minX) / deltaX;
					minY -= scrollY * (point.getY() - minY) / deltaY;
				}
				
				updateTransform();
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
	}

	// Create

	@Override
	protected IntersectionViewerFlat create(Intersection intersection) {
		return new IntersectionViewerFlat(model, intersection);
	}

	@Override
	protected SegmentViewerFlat create(Segment segment) {
		return new SegmentViewerFlat(model, segment); 
	}

	@Override
	protected StationViewerFlat create(Station station) {
		return new StationViewerFlat(model, station);
	}

	@Override
	protected VehicleViewerFlat create(Vehicle vehicle) {
		return new VehicleViewerFlat(model, vehicle);
	}

	@Override
	protected DemandViewerFlat create(Demand demand) {
		return new DemandViewerFlat(model, demand);
	}
	
	// Compute
	
	private void updateTransform() {
		double width = getWidth();
		double height = getHeight();
		
		double factor = Math.min(width / deltaX, height / deltaY);
		
		canvas.setTranslateX(- minX * factor + (width - deltaX * factor) / 2);
		canvas.setTranslateY(- minY * factor + (height - deltaY * factor) / 2);
		
		canvas.setScaleX(factor);
		canvas.setScaleY(factor);
	}
	
}
