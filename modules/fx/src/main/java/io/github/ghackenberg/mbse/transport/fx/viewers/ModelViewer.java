package io.github.ghackenberg.mbse.transport.fx.viewers;

import java.util.HashMap;
import java.util.Map;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public abstract class ModelViewer<I extends EntityViewer<Intersection>, Se extends EntityViewer<Segment>, St extends EntityViewer<Station>, V extends EntityViewer<Vehicle>, D extends EntityViewer<Demand>, C extends Node> extends Pane {

	public final Model model;
	public final Model.State modelState;
	
	public final C canvas;
	
	private final Text time = new Text();
	
	private final Rectangle clip = new Rectangle();
	
	public final ObservableList<I> intersections = FXCollections.observableArrayList();
	public final ObservableList<Se> segments = FXCollections.observableArrayList();
	public final ObservableList<St> stations = FXCollections.observableArrayList();
	public final ObservableList<V> vehicles = FXCollections.observableArrayList();
	public final ObservableList<D> demands = FXCollections.observableArrayList();

	public final Map<Intersection, I> intersectionViewers = new HashMap<>();
	public final Map<Segment, Se> segmentViewers = new HashMap<>();
	public final Map<Station, St> stationViewers = new HashMap<>();
	public final Map<Vehicle, V> vehicleViewers = new HashMap<>();
	public final Map<Demand, D> demandViewers = new HashMap<>();

	public ModelViewer(Model model, C canvas) {
		this.model = model;
		this.modelState = model.state.get();
		
		this.canvas = canvas;
		
		setStyle("-fx-background-color: white;");

		clip.widthProperty().bind(widthProperty());
		clip.heightProperty().bind(heightProperty());
		
		getChildren().add(canvas);
		getChildren().add(time);
		
		setClip(clip);
	}
	
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
