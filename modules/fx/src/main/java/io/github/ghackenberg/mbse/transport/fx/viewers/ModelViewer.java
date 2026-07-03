package io.github.ghackenberg.mbse.transport.fx.viewers;

import java.util.HashMap;
import java.util.Map;

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
import io.github.ghackenberg.mbse.transport.fx.helpers.GenericListChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public abstract class ModelViewer<I extends IntersectionViewer, Se extends SegmentViewer, St extends StationViewer, In extends IncominggoodsViewer, Out extends OutcominggoodsViewer , P extends PackagingViewer, W extends WarehouseViewer, Bcs extends BatterychargingstationViewer, Is extends InstallationsiteViewer, M extends MachineryViewer, Bu extends BufferViewer, Wo extends WorkshopViewer,V extends VehicleViewer, D extends DemandViewer, C extends Node> extends Pane {
	
	protected double minX;
	protected double minY;
	protected double minZ;
	
	protected double sizeX;
	protected double sizeY;
	protected double sizeZ;

	public final Model model;
	public final Model.State modelState;
	
	public final Group intersectionLayer = new Group();
	public final Group segmentLayer = new Group();
	public final Group stationLayer = new Group();
	public final Group incominggoodsLayer = new Group();
	public final Group outcominggoodsLayer = new Group();
	public final Group packagingLayer = new Group();
	public final Group warehouseLayer = new Group();
	public final Group batterychargingstationLayer = new Group();
	public final Group installationsiteLayer = new Group();
	public final Group machineryLayer = new Group();
	public final Group bufferLayer = new Group();
	public final Group workshopLayer = new Group();
	public final Group vehicleLayer = new Group();
	public final Group demandLayer = new Group();
	
	public final C canvas;
	
	private final Text time = new Text();
	
	private final Rectangle clip = new Rectangle();
	
	public final ObservableList<I> intersections = FXCollections.observableArrayList();
	public final ObservableList<Se> segments = FXCollections.observableArrayList();
	public final ObservableList<St> stations = FXCollections.observableArrayList();
	public final ObservableList<In> incominggoods = FXCollections.observableArrayList();
	public final ObservableList<Out> outcominggoods = FXCollections.observableArrayList();
	public final ObservableList<P> packaging = FXCollections.observableArrayList();
	public final ObservableList<W> warehouses = FXCollections.observableArrayList();
	public final ObservableList<Bcs> batterychargingstations = FXCollections.observableArrayList();
	public final ObservableList<Is> installationsites = FXCollections.observableArrayList();
	public final ObservableList<M> machineries = FXCollections.observableArrayList();
	public final ObservableList<Bu> buffers = FXCollections.observableArrayList();
	public final ObservableList<Wo> workshops = FXCollections.observableArrayList();
	public final ObservableList<V> vehicles = FXCollections.observableArrayList();
	public final ObservableList<D> demands = FXCollections.observableArrayList();

	public final Map<Intersection, I> intersectionViewers = new HashMap<>();
	public final Map<Segment, Se> segmentViewers = new HashMap<>();
	public final Map<Station, St> stationViewers = new HashMap<>();
	public final Map<Incominggoods, In> incominggoodsViewers = new HashMap<>();
	public final Map<Outcominggoods, Out> outcominggoodsViewers = new HashMap<>();
	public final Map<Packaging, P> packagingViewers = new HashMap<>();
	public final Map<Warehouse, W> warehouseViewers = new HashMap<>();
	public final Map<Batterychargingstation, Bcs> batterychargingstationViewers = new HashMap<>();
	public final Map<Installationsite, Is> installationsiteViewers = new HashMap<>();
	public final Map<Machinery, M> machineryViewers = new HashMap<>();
	public final Map<Buffer, Bu> bufferViewers = new HashMap<>();
	public final Map<Workshop, Wo> workshopViewers = new HashMap<>();
	public final Map<Vehicle, V> vehicleViewers = new HashMap<>();
	public final Map<Demand, D> demandViewers = new HashMap<>();

	public ModelViewer(Model model, C canvas, boolean showDemands) {
		this.model = model;
		this.modelState = model.state.get();
		
		this.canvas = canvas;
		
		minX = model.intersections.size() > 0 ? model.min.x.get() - 3 : -15;
		minY = model.intersections.size() > 0 ? model.min.y.get() - 3 : -15;
		minZ = model.intersections.size() > 0 ? model.min.z.get() - 3 : -15;
		
		sizeX = model.intersections.size() > 0 ? model.size.x.get() + 6 : 30;
		sizeY = model.intersections.size() > 0 ? model.size.y.get() + 6 : 30;
		sizeZ = model.intersections.size() > 0 ? model.size.z.get() + 6 : 30;
		
		setStyle("-fx-background-color: white;");

		clip.widthProperty().bind(widthProperty());
		clip.heightProperty().bind(heightProperty());
		
		getChildren().add(canvas);
		getChildren().add(time);
		
		setClip(clip);
		
		// Intersections
		
		model.intersections.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
		for (Intersection intersection : model.intersections) {
			add(intersection);
		}
		
		// Segments
		
		model.segments.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
		for (Segment segment : model.segments) {
			add(segment);
		}
		
		// Stations
		
		model.stations.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
		for (Station station : model.stations) {
			add(station);
		}

		// Incominggoods

		model.incominggoods.addListener(new GenericListChangeListener<>(this::remove, this::add));

		for (Incominggoods incominggoods : model.incominggoods) {
    		add(incominggoods);
		}
		
		// Outcominggoods

		model.outcominggoods.addListener(new GenericListChangeListener<>(this::remove, this::add));

		for (Outcominggoods outcominggoods : model.outcominggoods) {
    		add(outcominggoods);
		}

		// Packaging

		model.packaging.addListener(new GenericListChangeListener<>(this::remove, this::add));

		for (Packaging packaging : model.packaging) {
    		add(packaging);
		}

		// Warehouses

		model.warehouse.addListener(new GenericListChangeListener<>(this::remove, this::add));

		for (Warehouse warehouse : model.warehouse) {
    		add(warehouse);
		}

		// Batterychargingstations

		model.batterychargingstation.addListener(new GenericListChangeListener<>(this::remove, this::add));

		for (Batterychargingstation batterychargingstation : model.batterychargingstation) {
    		add(batterychargingstation);
		}

		// Installationsites

		model.installationsite.addListener(new GenericListChangeListener<>(this::remove, this::add));

		for (Installationsite installationsite : model.installationsite) {
    		add(installationsite);
		}

		// Machineries

		model.machinery.addListener(new GenericListChangeListener<>(this::remove, this::add));

		for (Machinery machinery : model.machinery) {
    		add(machinery);
		}

		// Buffers

		model.buffer.addListener(new GenericListChangeListener<>(this::remove, this::add));

		for (Buffer buffer : model.buffer) {
    		add(buffer);
		}

		// Workshops

		model.workshop.addListener(new GenericListChangeListener<>(this::remove, this::add));

		for (Workshop workshop : model.workshop) {
    		add(workshop);
		}

		// Vehicles
		
		model.vehicles.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
		for (Vehicle vehicle : model.vehicles) {
			add(vehicle);
		}
		
		// Demands

		if (showDemands) {
			model.demands.addListener(new GenericListChangeListener<>(this::remove, this::add));
		
			for (Demand demand : model.demands) {
				add(demand);
			}
		}
	}
	
	// Create
	
	protected abstract I create(Intersection intersection);
	protected abstract Se create(Segment segment);
	protected abstract St create(Station station);
	protected abstract In create(Incominggoods incominggoods);
	protected abstract Out create(Outcominggoods outcominggoods);
	protected abstract P create(Packaging packaging);
	protected abstract W create(Warehouse warehouse);
	protected abstract Bcs create(Batterychargingstation batterychargingstation);
	protected abstract Is create(Installationsite installationsite);
	protected abstract M create(Machinery machinery);
	protected abstract Bu create(Buffer buffer);
	protected abstract Wo create(Workshop workshop);
	protected abstract V create(Vehicle vehicle);
	protected abstract D create(Demand demand);
	
	// Add
	
	private void add(Intersection intersection) {
		add(intersection, create(intersection), intersections, intersectionLayer, intersectionViewers);
	}
	
	private void add(Segment segment) {
		add(segment, create(segment), segments, segmentLayer, segmentViewers);
	}
	
	private void add(Station station) {
		add(station, create(station), stations, stationLayer, stationViewers);
	}
	
	private void add(Incominggoods incominggoods) {
    	add(incominggoods, create(incominggoods), this.incominggoods, incominggoodsLayer, incominggoodsViewers);
	}

	private void add(Outcominggoods outcominggoods) {
    	add(outcominggoods, create(outcominggoods), this.outcominggoods, outcominggoodsLayer, outcominggoodsViewers);
	}

	private void add(Packaging packaging) {
    add(packaging, create(packaging), this.packaging, packagingLayer, packagingViewers);
	}

	private void add(Warehouse warehouse) {
    	add(warehouse, create(warehouse), warehouses, warehouseLayer, warehouseViewers);
	}

	private void add(Batterychargingstation batterychargingstation) {
    	add(batterychargingstation, create(batterychargingstation), batterychargingstations, batterychargingstationLayer, batterychargingstationViewers);
	}

	private void add(Installationsite installationsite) {
    	add(installationsite, create(installationsite), installationsites, installationsiteLayer, installationsiteViewers);
	}

	private void add(Machinery machinery) {
    	add(machinery, create(machinery), machineries, machineryLayer, machineryViewers);
	}

	private void add(Buffer buffer) {
    	add(buffer, create(buffer), buffers, bufferLayer, bufferViewers);
	}

	private void add(Workshop workshop) {
    	add(workshop, create(workshop), workshops, workshopLayer, workshopViewers);
	}

	private void add(Vehicle vehicle) {
		add(vehicle, create(vehicle), vehicles, vehicleLayer, vehicleViewers);
	}
	
	private void add(Demand demand) {
		add(demand, create(demand), demands, demandLayer, demandViewers);
	}
	
	private <T, S extends Node> void add(T entity, S viewer, ObservableList<S> viewers, Group layer, Map<T, S> viewerMap) {
		viewers.add(viewer);
		
		layer.getChildren().add(viewer);
		
		viewerMap.put(entity, viewer);
	}
	
	// Remove
	
	private void remove(Intersection intersection) {
		remove(intersection, intersections, intersectionLayer, intersectionViewers);
	}
	
	private void remove(Segment segment) {
		remove(segment, segments, segmentLayer, segmentViewers);
	}
	
	private void remove(Station station) {
		remove(station, stations, stationLayer, stationViewers);
	}
	
	private void remove(Incominggoods incominggoods) {
    	remove(incominggoods, this.incominggoods, incominggoodsLayer, incominggoodsViewers);
	}

	private void remove(Outcominggoods outcominggoods) {
    	remove(outcominggoods, this.outcominggoods, outcominggoodsLayer, outcominggoodsViewers);
	}

	private void remove(Packaging packaging) {
    	remove(packaging, this.packaging, packagingLayer, packagingViewers);
	}

	private void remove(Warehouse warehouse) {
    	remove(warehouse, warehouses, warehouseLayer, warehouseViewers);
	}

	private void remove(Batterychargingstation batterychargingstation) {
    	remove(batterychargingstation, batterychargingstations, batterychargingstationLayer, batterychargingstationViewers);
	}

	private void remove(Installationsite installationsite) {
    	remove(installationsite, installationsites, installationsiteLayer, installationsiteViewers);
	}

	private void remove(Machinery machinery) {
    	remove(machinery, machineries, machineryLayer, machineryViewers);
	}

	private void remove(Buffer buffer) {
    	remove(buffer, buffers, bufferLayer, bufferViewers);
	}

	private void remove(Workshop workshop) {
    	remove(workshop, workshops, workshopLayer, workshopViewers);
	}

	private void remove(Vehicle vehicle) {
		remove(vehicle, vehicles, vehicleLayer, vehicleViewers);
	}
	
	private void remove(Demand demand) {
		remove(demand, demands, demandLayer, demandViewers);
	}
	
	private <T, S extends Node> void remove(T entity, ObservableList<S> viewers, Group layer, Map<T, S> viewerMap) {
		S viewer = viewerMap.remove(entity);
		
		layer.getChildren().remove(viewer);
		
		viewers.remove(viewer);
	}
	
	// Update
	
	public void update() {
		for (I viewer : intersectionViewers.values()) {
			viewer.update();
		}
		for (Se viewer : segmentViewers.values()) {
			viewer.update();
		}
		for (St viewer : stationViewers.values()) {
			viewer.update();
		}
        for (In viewer : incominggoodsViewers.values()) {
            viewer.update();
        }
		for (Out viewer : outcominggoodsViewers.values()) {
    		viewer.update();
		}
		for (P viewer : packagingViewers.values()) {
    		viewer.update();
		}
		for (W viewer : warehouseViewers.values()) {
    		viewer.update();
		}
		for (Bcs viewer : batterychargingstationViewers.values()) {
    		viewer.update();
		}
		for (Is viewer : installationsiteViewers.values()) {
    		viewer.update();
		}
		for (M viewer : machineryViewers.values()) {
    		viewer.update();
		}
		for (Bu viewer : bufferViewers.values()) {
    		viewer.update();
		}
		for (Wo viewer : workshopViewers.values()) {
    		viewer.update();
		}
		for (D viewer : demandViewers.values()) {
			viewer.update();
		}
		for (V viewer : vehicleViewers.values()) {
			viewer.update();
		}
		if (modelState != null) {
			time.setText("" + Math.round(modelState.time) + " ms");
		}
	}
	
}
