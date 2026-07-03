package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Incominggoods;
import io.github.ghackenberg.mbse.transport.core.entities.Outcominggoods;
import io.github.ghackenberg.mbse.transport.core.entities.Packaging;
import io.github.ghackenberg.mbse.transport.core.entities.Warehouse;
import io.github.ghackenberg.mbse.transport.core.entities.Batterychargingstation;
import io.github.ghackenberg.mbse.transport.core.entities.Installationsite;
import io.github.ghackenberg.mbse.transport.core.entities.Machinery;
import io.github.ghackenberg.mbse.transport.core.entities.Buffer;
import io.github.ghackenberg.mbse.transport.core.entities.Workshop;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.NonInvertibleTransformException;

public class ModelViewerFlat extends ModelViewer<IntersectionViewerFlat, SegmentViewerFlat, StationViewerFlat, IncominggoodsViewerFlat, OutcominggoodsViewerFlat, PackagingViewerFlat, WarehouseViewerFlat, BatterychargingstationViewerFlat, InstallationsiteViewerFlat, MachineryViewerFlat, BufferViewerFlat, WorkshopViewerFlat,VehicleViewerFlat, DemandViewerFlat, Pane> {

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
		canvas.getChildren().add(incominggoodsLayer);
		canvas.getChildren().add(outcominggoodsLayer);
		canvas.getChildren().add(packagingLayer);
		canvas.getChildren().add(warehouseLayer);
		canvas.getChildren().add(batterychargingstationLayer);
		canvas.getChildren().add(installationsiteLayer);
		canvas.getChildren().add(machineryLayer);
		canvas.getChildren().add(bufferLayer);
		canvas.getChildren().add(workshopLayer);
		canvas.getChildren().add(vehicleLayer);
		canvas.getChildren().add(demandLayer);

		// Self

		getChildren().add(0, grid);
		
		widthProperty().addListener(event -> updateTransform());
		heightProperty().addListener(event -> updateTransform());
		
		setOnScroll(event -> {
			try {
				double scrollX = -event.getDeltaX() * Math.max(sizeX, sizeY) * 0.005;
				double scrollY = -event.getDeltaY() * Math.max(sizeX, sizeY) * 0.005;
				
				if (event.isShiftDown()) {
					// Horizontal scroll
					
					minX += scrollX;
					
				} else if (event.isControlDown()) {
					// Vertical scroll
					
					minY += scrollY;
					
				} else if (sizeX + scrollY > 0 && sizeY + scrollY > 0) {
					// Pan and zoom
					
					sizeX += scrollY;
					sizeY += scrollY;
					
					Point2D point = canvas.getLocalToSceneTransform().createInverse().transform(event.getSceneX(), event.getSceneY());
					
					minX -= scrollY * (point.getX() - minX) / sizeX;
					minY -= scrollY * (point.getY() - minY) / sizeY;
				}
				
				updateTransform();
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
		});
	}

	// Unproject

	public Point2D unproject(MouseEvent event) {
		double x = event.getSceneX();
		double y = event.getSceneY();

		return unproject(x, y);
	}

	public Point2D unproject(double x, double y) {
		try {
			return canvas.getLocalToSceneTransform().createInverse().transform(x, y);
		} catch (NonInvertibleTransformException e) {
			return new Point2D(0, 0);
		}
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
	protected IncominggoodsViewerFlat create(Incominggoods incominggoods) {
    	return new IncominggoodsViewerFlat(model, incominggoods);
	}

	@Override
	protected OutcominggoodsViewerFlat create(Outcominggoods outcominggoods) {
    	return new OutcominggoodsViewerFlat(model, outcominggoods);
	}

	@Override
	protected PackagingViewerFlat create(Packaging packaging) {
    	return new PackagingViewerFlat(model, packaging);
	}

	@Override
	protected WarehouseViewerFlat create(Warehouse warehouse) {
    	return new WarehouseViewerFlat(model, warehouse);
	}

	@Override
	protected BatterychargingstationViewerFlat create(Batterychargingstation batterychargingstation) {
    	return new BatterychargingstationViewerFlat(model, batterychargingstation);
	}

	@Override
	protected InstallationsiteViewerFlat create(Installationsite installationsite) {
    	return new InstallationsiteViewerFlat(model, installationsite);
	}

	@Override
	protected MachineryViewerFlat create(Machinery machinery) {
    	return new MachineryViewerFlat(model, machinery);
	}

	@Override
	protected BufferViewerFlat create(Buffer buffer) {
    	return new BufferViewerFlat(model, buffer);
	}

	@Override
	protected WorkshopViewerFlat create(Workshop workshop) {
    	return new WorkshopViewerFlat(model, workshop);
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
		
		double factor = Math.min(width / sizeX, height / sizeY);
		
		canvas.setTranslateX(- minX * factor + (width - sizeX * factor) / 2);
		canvas.setTranslateY(- minY * factor + (height - sizeY * factor) / 2);
		
		canvas.setScaleX(factor);
		canvas.setScaleY(factor);
	}
	
}
