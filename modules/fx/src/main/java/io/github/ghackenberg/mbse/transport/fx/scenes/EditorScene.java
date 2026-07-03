package io.github.ghackenberg.mbse.transport.fx.scenes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.Serializer;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Outcominggoods;
import io.github.ghackenberg.mbse.transport.core.entities.Packaging;
import io.github.ghackenberg.mbse.transport.core.entities.Warehouse;
import io.github.ghackenberg.mbse.transport.core.entities.Batterychargingstation;
import io.github.ghackenberg.mbse.transport.core.entities.Installationsite;
import io.github.ghackenberg.mbse.transport.core.entities.Machinery;
import io.github.ghackenberg.mbse.transport.core.entities.Buffer;
import io.github.ghackenberg.mbse.transport.core.entities.Workshop;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Incominggoods;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.core.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import io.github.ghackenberg.mbse.transport.fx.helpers.GenericListChangeListener;
import io.github.ghackenberg.mbse.transport.fx.helpers.GridHelper;
import io.github.ghackenberg.mbse.transport.fx.helpers.ImageViewHelper;
import io.github.ghackenberg.mbse.transport.fx.helpers.PersistentMemoryHelper;
import io.github.ghackenberg.mbse.transport.fx.viewers.EntityViewer;
import io.github.ghackenberg.mbse.transport.fx.viewers.deep.ModelViewerDeep;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.DemandViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.IntersectionViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.ModelViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.SegmentViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.IncominggoodsViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.OutcominggoodsViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.PackagingViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.WarehouseViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.BatterychargingstationViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.InstallationsiteViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.MachineryViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.BufferViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.WorkshopViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.StationViewerFlat;
import io.github.ghackenberg.mbse.transport.fx.viewers.flat.VehicleViewerFlat;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;





/**
 * Editor scene providing a toolbar on top, a property grid on the right, and the 2D and 3D viewers in the center.
 */
public class EditorScene extends Scene {

	// Fields

	private File modelRunsFolder = new File("runs");
	
	private Model model = new Model();

	private final GridPane commandGrid = new GridPane();

	
	/**
 	* Enum zur Verwaltung des aktuellen Benutzer-Werkzeugs.
 	* Definiert, welches Objekt als nächstes per Mausklick platziert wird.
 	*/
	private enum Tool {
    	SELECT,                         // Standardmodus (nur Auswahl)
    	PLACE_STATION,                  // Station platzieren
    	PLACE_INCOMINGGOODS,            // Incominggoods platzieren
    	PLACE_OUTCOMINGGOODS,           // Outcominggoods platzieren
    	PLACE_PACKAGE,                  // Package platzieren
    	PLACE_WAREHOUSE,                // Warehouse platzieren
    	PLACE_BATTERYCHARGINGSTATION,   // Batterychargingstation platzieren
    	PLACE_INSTALLATIONSITE,         // Installationsite platzieren
    	PLACE_MACHINERY,                // Machinery platzieren
    	PLACE_BUFFER,                   // Buffer platzieren
    	PLACE_WORKSHOP                  // Workshop platzieren
	}

	/**
 	* Speichert das aktuell ausgewählte Werkzeug.
 	* Wird über Tastenkombinationen gesetzt und nach Platzierung zurückgesetzt.
 	*/
	private Tool currentTool = Tool.SELECT;
	
/**
	 * Zeigt an, welches Werkzeug aktuell ausgewählt ist.
	 * Dadurch erkennt der Benutzer, welches Element beim nächsten Klick platziert wird.
	 */
	private final Label currentToolLabel = new Label("Aktuelles Werkzeug: Keines Ausgewählt");


	/**
	 * Container für Werkzeuganzeige und Legende.
	 * Wird im unteren Bereich der rechten Seitenleiste fixiert,
	 * sodass er beim Vergrößern des Fensters immer unten sichtbar bleibt.
	 */
	private final VBox toolInfoBox = new VBox(10,new Label("Bedienung:"),commandGrid);

	private Node dragDemandNode;

	// - Previews

	// --- Segment preview
	
	private final BooleanProperty segmentPreviewVisible = new SimpleBooleanProperty(false);
	private final DoubleProperty segmentPreviewEndX = new SimpleDoubleProperty();
	private final DoubleProperty segmentPreviewEndY = new SimpleDoubleProperty();
	
	private final Line segmentPreviewLine = new Line();
	private final Circle segmentPreviewHead = new Circle();

	// --- Demand preview

	private final BooleanProperty demandPreviewVisible = new SimpleBooleanProperty(false);
	private final Location demandPreviewPickLoc = new Location();
	private final Location demandPreviewDropLoc = new Location();

	private final Circle demandPreviewPick = new Circle();
	private final Line demandPreviewLine = new Line();
	private final Circle demandPreviewDrop = new Circle();

	// - Buttons

	private final Button clear = new Button("Clear", ImageViewHelper.load("clear.png", 16, 16));
	private final Button open = new Button("Open", ImageViewHelper.load("open.png", 16, 16));
	private final Button save = new Button("Save", ImageViewHelper.load("save.png", 16, 16));
	private final Button run = new Button("Run", ImageViewHelper.load("run.png", 16, 16));
	private final Button help = new Button("Help", ImageViewHelper.load("help.png", 16, 16));
	
	private final ToggleButton deep = new ToggleButton("3D", ImageViewHelper.load("deep.png", 16, 16));

	// - Toolbars
	
	private final ToolBar top = new ToolBar(clear, open, save, run, help, deep);
	private final ToolBar bottom = new ToolBar(new Label("© 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, FH Upper Austria"));

	// - Viewers
	
	private ModelViewerFlat modelViewerFlat;
	private ModelViewerDeep modelViewerDeep;
	
	private EntityViewer<?> selected;

	// - Panes
	
	private final GridPane grid = new GridPane();
	private final BorderPane right = new BorderPane();
	private final BorderPane root = new BorderPane(null, top, right, bottom, null);

	// Constructors
	
	public EditorScene() {
		super(new Label("Loading ..."), 800, 600);
		
	/**
	 * Registrierung der Tastenkombinationen.
	 * Jede Tastenkombination aktiviert ein Werkzeug für die nächste Platzierung.
	 */
	getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_STATION));

	getAccelerators().put(new KeyCodeCombination(KeyCode.I, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_INCOMINGGOODS));

	getAccelerators().put(new KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_OUTCOMINGGOODS));

	getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_PACKAGE));

	getAccelerators().put(new KeyCodeCombination(KeyCode.W, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_WAREHOUSE));

	getAccelerators().put(new KeyCodeCombination(KeyCode.B, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_BATTERYCHARGINGSTATION));

	getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_INSTALLATIONSITE));

	getAccelerators().put(new KeyCodeCombination(KeyCode.M, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_MACHINERY));

	getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_BUFFER));

	getAccelerators().put(new KeyCodeCombination(KeyCode.U, KeyCombination.ALT_DOWN),
		() -> setTool(Tool.PLACE_WORKSHOP));

	/**
	 * Mit ESC wird der aktuelle Platziermodus verlassen.
	 */
	getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE),
		() -> setTool(Tool.SELECT));

		if (!modelRunsFolder.exists()) {
			modelRunsFolder.mkdirs();
		}
		
		// Top
		
		clear.setOnAction(event -> {
			modelRunsFolder = new File("runs");
			
			model = new Model();
			
			reload();
		});
		
		open.setOnAction(event -> {			
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(PersistentMemoryHelper.getContextFolder());
			
			File directory = chooser.showDialog(getWindow());
			
			if (directory != null) {
				try {
					PersistentMemoryHelper.setContextFolder(directory.getParentFile());
					
					model = new Parser().parse(directory);
					
					modelRunsFolder = new File(directory, "runs");
					
					if (!modelRunsFolder.exists()) {
						modelRunsFolder.mkdir();
					}
					
					reload();
				} catch (MissingException e) {
					e.printStackTrace();
				} catch (DirectoryException e) {
					e.printStackTrace();
				}	
			}
		});
		
		save.setOnAction(event -> {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(PersistentMemoryHelper.getContextFolder());
			
			File directory = chooser.showDialog(getWindow());
			
			if (directory != null) {
				PersistentMemoryHelper.setContextFolder(directory.getParentFile());
				
				new Serializer().serialize(model, directory);
				
				modelRunsFolder = new File(directory, "runs");
				
				if (!modelRunsFolder.exists()) {
					modelRunsFolder.mkdir();
				}
			}
		});
		
		run.setOnAction(event -> {
			if (modelRunsFolder != null) {
				Stage parentStage = (Stage) getWindow();
				
				SimulatorScene scene = new SimulatorScene(model, modelRunsFolder, 800, 600);
				
				Stage stage = new Stage();
	
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initOwner(parentStage);
				
				stage.getIcons().addAll(parentStage.getIcons());
				
				stage.setTitle("ITS-MSE Simulator");
				stage.setMaximized(parentStage.isMaximized());
				stage.setX(getWindow().getX() + 20);
				stage.setY(getWindow().getY() + 20);
				stage.setScene(scene);
				
				stage.show();
			} else {
				new Alert(AlertType.ERROR, "Please save model first!").showAndWait();
			}
		});
		
		help.setOnAction(event -> {
			Stage parentStage = (Stage) getWindow();
			
			HelpScene scene = new HelpScene(800, 600);
			
			Stage stage = new Stage();

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(parentStage);
			
			stage.getIcons().addAll(parentStage.getIcons());
			
			stage.setTitle("ITS-MSE Help");
			stage.setMaximized(parentStage.isMaximized());
			stage.setX(getWindow().getX() + 20);
			stage.setY(getWindow().getY() + 20);
			stage.setScene(scene);
			
			stage.show();
		});
		
		deep.setOnAction(event -> {
			if (deep.isSelected()) {
				root.setCenter(modelViewerDeep);
			} else {
				root.setCenter(modelViewerFlat);
			}
		});
		
		// Right
		
		grid.setPrefWidth(300);
		
		grid.setPadding(new Insets(10));

		grid.setHgap(10);
		grid.setVgap(10);
		
		grid.getColumnConstraints().add(GridHelper.createColumnConstraints(false, Priority.NEVER));
		grid.getColumnConstraints().add(GridHelper.createColumnConstraints(true, Priority.ALWAYS));
		
		
		right.setCenter(grid);
		right.setBottom(toolInfoBox);	

		commandGrid.setHgap(20);
		commandGrid.setVgap(4);
		commandGrid.setPadding(new Insets(8));

		int row = 0;

		Label befehlHeader = new Label("Befehl");
		befehlHeader.setStyle("-fx-font-weight: bold;");

		Label funktionHeader = new Label("Funktion");
		funktionHeader.setStyle("-fx-font-weight: bold;");

		Label farbeHeader = new Label("Farbe");
		farbeHeader.setStyle("-fx-font-weight: bold;");

		commandGrid.add(befehlHeader, 0, row);
		commandGrid.add(funktionHeader, 1, row);
		commandGrid.add(farbeHeader, 2, row++);

		commandGrid.add(new Label("Linksklick freie Fläche"), 0, row);
		commandGrid.add(new Label("Intersection platzieren"), 1, row++);

		commandGrid.add(new Label("STRG + Ziehen"), 0, row);
		commandGrid.add(new Label("Verbindung erstellen"), 1, row++);

		commandGrid.add(new Label("SHIFT + Klick Segment"), 0, row);
		commandGrid.add(new Label("Vehicle platzieren"), 1, row++);

		commandGrid.add(currentToolLabel, 0, row, 3, 1);
		row++;
		row++;

		row++;

		commandGrid.add(new Label("Alt + S"), 0, row);
		commandGrid.add(new Label("Station"), 1, row);
		commandGrid.add(colorBox(Color.DARKVIOLET), 2, row++);

		commandGrid.add(new Label("Alt + I"), 0, row);
		commandGrid.add(new Label("Incominggoods"), 1, row);
		commandGrid.add(colorBox(Color.BLUE), 2, row++);

		commandGrid.add(new Label("Alt + O"), 0, row);
		commandGrid.add(new Label("Outcominggoods"), 1, row);
		commandGrid.add(colorBox(Color.RED), 2, row++);

		commandGrid.add(new Label("Alt + P"), 0, row);
		commandGrid.add(new Label("Packaging"), 1, row);
		commandGrid.add(colorBox(Color.HOTPINK), 2, row++);

		commandGrid.add(new Label("Alt + W"), 0, row);
		commandGrid.add(new Label("Warehouse"), 1, row);
		commandGrid.add(colorBox(Color.BLACK), 2, row++);

		commandGrid.add(new Label("Alt + B"), 0, row);
		commandGrid.add(new Label("Batterychargingstation"), 1, row);
		commandGrid.add(colorBox(Color.YELLOW), 2, row++);

		commandGrid.add(new Label("Alt + N"), 0, row);
		commandGrid.add(new Label("Installationsite"), 1, row);
		commandGrid.add(colorBox(Color.ORANGE), 2, row++);

		commandGrid.add(new Label("Alt + M"), 0, row);
		commandGrid.add(new Label("Machinery"), 1, row);
		commandGrid.add(colorBox(Color.GREEN), 2, row++);

		commandGrid.add(new Label("Alt + V"), 0, row);
		commandGrid.add(new Label("Buffer"), 1, row);
		commandGrid.add(colorBox(Color.BROWN), 2, row++);

		commandGrid.add(new Label("Alt + U"), 0, row);
		commandGrid.add(new Label("Workshop"), 1, row);
		commandGrid.add(colorBox(Color.TURQUOISE), 2, row++);

		row++;

		commandGrid.add(new Label("ESC"), 0, row);
		commandGrid.add(new Label("Auswahl abbrechen"), 1, row);
		
		/**
		 * Konfiguration der Anzeige für aktuelles Werkzeug und Tastenkombinationen.
		 * Die VBox sorgt dafür, dass beide Elemente untereinander dargestellt werden.
		 */
		toolInfoBox.setStyle("-fx-padding: 8;");
		toolInfoBox.setFillWidth(true);
		
		/**
		 * Gestaltung des Werkzeug-Indikators und der Legende.
		 * Beide Elemente werden in der rechten Seitenleiste angezeigt.
		 */
		currentToolLabel.setStyle(
			"-fx-font-weight: bold;" +
			"-fx-padding: 8;" +
			"-fx-background-color: #eeeeee;"
		);

		// Center

		// - Segment preview
		
		segmentPreviewLine.visibleProperty().bind(segmentPreviewVisible);
		segmentPreviewLine.endXProperty().bind(segmentPreviewEndX);
		segmentPreviewLine.endYProperty().bind(segmentPreviewEndY);

		segmentPreviewLine.setStroke(Color.LIGHTGRAY);
		segmentPreviewLine.setStrokeWidth(1);
		segmentPreviewLine.setStrokeDashOffset(1);
		segmentPreviewLine.setStrokeLineCap(StrokeLineCap.BUTT);
		
		segmentPreviewHead.visibleProperty().bind(segmentPreviewVisible);
		segmentPreviewHead.centerXProperty().bind(segmentPreviewEndX);
		segmentPreviewHead.centerYProperty().bind(segmentPreviewEndY);

		segmentPreviewHead.setRadius(0.5);
		segmentPreviewHead.setFill(Color.GRAY);

		// - Demand preview

		demandPreviewPickLoc.lane.set(-1);
		demandPreviewDropLoc.lane.set(-1);

		demandPreviewPick.visibleProperty().bind(demandPreviewVisible);
		demandPreviewPick.centerXProperty().bind(demandPreviewPickLoc.coordinate.x);
		demandPreviewPick.centerYProperty().bind(demandPreviewPickLoc.coordinate.y);

		demandPreviewPick.setRadius(0.5);
		demandPreviewPick.setFill(Color.GREEN);

		demandPreviewLine.visibleProperty().bind(demandPreviewVisible);
		demandPreviewLine.startXProperty().bind(demandPreviewPickLoc.coordinate.x);
		demandPreviewLine.startYProperty().bind(demandPreviewPickLoc.coordinate.y);
		demandPreviewLine.endXProperty().bind(demandPreviewDropLoc.coordinate.x);
		demandPreviewLine.endYProperty().bind(demandPreviewDropLoc.coordinate.y);

		demandPreviewLine.setStroke(Color.GREEN);
		demandPreviewLine.setStrokeWidth(0.5 / 5);

		demandPreviewDrop.visibleProperty().bind(demandPreviewVisible);
		demandPreviewDrop.centerXProperty().bind(demandPreviewDropLoc.coordinate.x);
		demandPreviewDrop.centerYProperty().bind(demandPreviewDropLoc.coordinate.y);

		demandPreviewDrop.setRadius(0.5);
		demandPreviewDrop.setFill(Color.GREEN);

		// - Reload
		
		reload();
		
		// Root
		
		setRoot(root);
	}

	// Methods

	// - Reload
	
	private void reload() {
		select(null);
		
		modelViewerFlat = new ModelViewerFlat(model);
		modelViewerDeep = new ModelViewerDeep(model);
		
		modelViewerFlat.segmentLayer.getChildren().add(segmentPreviewLine);
		modelViewerFlat.segmentLayer.getChildren().add(segmentPreviewHead);

		modelViewerFlat.demandLayer.getChildren().add(demandPreviewLine);
		modelViewerFlat.demandLayer.getChildren().add(demandPreviewPick);
		modelViewerFlat.demandLayer.getChildren().add(demandPreviewDrop);
		
		setup(modelViewerFlat.intersections, this::detach, this::attach);
		setup(modelViewerFlat.segments, this::detach, this::attach);
		setup(modelViewerFlat.incominggoods, this::detach, this::attach);
		setup(modelViewerFlat.outcominggoods, this::detach, this::attach);
		setup(modelViewerFlat.packaging, this::detach, this::attach);
		setup(modelViewerFlat.warehouses, this::detach, this::attach);
		setup(modelViewerFlat.batterychargingstations, this::detach, this::attach);
		setup(modelViewerFlat.installationsites, this::detach, this::attach);
		setup(modelViewerFlat.machineries, this::detach, this::attach);
		setup(modelViewerFlat.buffers, this::detach, this::attach);
		setup(modelViewerFlat.workshops, this::detach, this::attach);
		setup(modelViewerFlat.stations, this::detach, this::attach);
		setup(modelViewerFlat.vehicles, this::detach, this::attach);
		setup(modelViewerFlat.demands, this::detach, this::attach);
		
		modelViewerFlat.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				Point2D world = modelViewerFlat.unproject(event);

				if (!event.isAltDown()) {
					world = modelViewerFlat.grid.snap(world);
				}
				
				Intersection intersection = new Intersection();
				intersection.name.set("Intersection " + (model.intersections.size() + 1));
				intersection.coordinate.x.set(world.getX());
				intersection.coordinate.y.set(world.getY());
				intersection.coordinate.z.set(0);
				
				model.intersections.add(intersection);
				
				select(modelViewerFlat.intersectionViewers.get(intersection));
				
				event.consume();
			}
		});
		modelViewerFlat.setOnMouseDragEntered(event -> {
			if (event.getGestureSource() instanceof IntersectionViewerFlat) {
				if (event.isControlDown()) {
					segmentPreviewVisible.set(true);
					event.consume();
				}
			} else if (event.getGestureSource() instanceof SegmentViewerFlat) {
				demandPreviewVisible.set(true);
				event.consume();
			}
		});
		modelViewerFlat.setOnMouseDragOver(event -> {
			Point2D world = modelViewerFlat.unproject(event);
			
		if (event.getGestureSource() instanceof IntersectionViewerFlat) {
			IntersectionViewerFlat viewer = (IntersectionViewerFlat) event.getGestureSource();

			if (!event.isAltDown()) {
				world = modelViewerFlat.grid.snap(world);
			}

			if (event.isControlDown()) {
				segmentPreviewEndX.set(world.getX());
				segmentPreviewEndY.set(world.getY());
			} else {
				Map<Station, Double> stationDist = new HashMap<>();
				Map<Incominggoods, Double> incominggoodsDist = new HashMap<>();
				Map<Vehicle, Double> vehicleDist = new HashMap<>();

				for (Station station : model.stations) {
					stationDist.put(
						station,
						station.location.distance.get()
							/ station.location.segment.get().length.get()
					);
				}

				for (Incominggoods incominggoods : model.incominggoods) {
					incominggoodsDist.put(
						incominggoods,
						incominggoods.location.distance.get()
							/ incominggoods.location.segment.get().length.get()
					);
				}

				for (Vehicle vehicle : model.vehicles) {
					vehicleDist.put(
						vehicle,
						vehicle.initialLocation.distance.get()
							/ vehicle.initialLocation.segment.get().length.get()
					);
				}

				Map<Demand, Double> demandPickDist = new HashMap<>();
				Map<Demand, Double> demandDropDist = new HashMap<>();

				for (Demand demand : model.demands) {
					demandPickDist.put(
						demand,
						demand.pick.location.distance.get()
							/ demand.pick.location.segment.get().length.get()
					);

					demandDropDist.put(
						demand,
						demand.drop.location.distance.get()
							/ demand.drop.location.segment.get().length.get()
					);
				}

				viewer.entity.coordinate.x.set(world.getX());
				viewer.entity.coordinate.y.set(world.getY());

				for (Station station : model.stations) {
					station.location.distance.set(
						stationDist.get(station)
							* station.location.segment.get().length.get()
					);
				}

				for (Incominggoods incominggoods : model.incominggoods) {
					incominggoods.location.distance.set(
						incominggoodsDist.get(incominggoods)
							* incominggoods.location.segment.get().length.get()
					);
				}

				for (Vehicle vehicle : model.vehicles) {
					vehicle.initialLocation.distance.set(
						vehicleDist.get(vehicle)
							* vehicle.initialLocation.segment.get().length.get()
					);
				}

				for (Demand demand : model.demands) {
					demand.pick.location.distance.set(
						demandPickDist.get(demand)
							* demand.pick.location.segment.get().length.get()
					);

					demand.drop.location.distance.set(
						demandDropDist.get(demand)
							* demand.drop.location.segment.get().length.get()
					);
				}
			}

			event.consume();
					

			} else if (event.getGestureSource() instanceof SegmentViewerFlat) {
				Model.Projection projection = model.project(world.getX(), world.getY());

				if (projection.seg != null) {
					demandPreviewDropLoc.segment.set(projection.seg);
					demandPreviewDropLoc.distance.set(projection.dot);
				}

				event.consume();
				
			} else if (event.getGestureSource() instanceof IncominggoodsViewerFlat) {
				IncominggoodsViewerFlat viewer = (IncominggoodsViewerFlat) event.getGestureSource();
				
				Model.Projection projection = model.project(world.getX(), world.getY());
				
				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}
				
				event.consume();

			} else if (event.getGestureSource() instanceof OutcominggoodsViewerFlat) {
				OutcominggoodsViewerFlat viewer = (OutcominggoodsViewerFlat) event.getGestureSource();

				Model.Projection projection = model.project(world.getX(), world.getY());

				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}

				event.consume();

			} else if (event.getGestureSource() instanceof PackagingViewerFlat) {
				PackagingViewerFlat viewer = (PackagingViewerFlat) event.getGestureSource();

				Model.Projection projection = model.project(world.getX(), world.getY());

				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}

				event.consume();

			} else if (event.getGestureSource() instanceof WarehouseViewerFlat) {
				WarehouseViewerFlat viewer = (WarehouseViewerFlat) event.getGestureSource();

				Model.Projection projection = model.project(world.getX(), world.getY());

				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}

				event.consume();

			} else if (event.getGestureSource() instanceof BatterychargingstationViewerFlat) {
				BatterychargingstationViewerFlat viewer = (BatterychargingstationViewerFlat) event.getGestureSource();

				Model.Projection projection = model.project(world.getX(), world.getY());

				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}

				event.consume();

			} else if (event.getGestureSource() instanceof InstallationsiteViewerFlat) {
				InstallationsiteViewerFlat viewer = (InstallationsiteViewerFlat) event.getGestureSource();

				Model.Projection projection = model.project(world.getX(), world.getY());

				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}

				event.consume();

			} else if (event.getGestureSource() instanceof MachineryViewerFlat) {
				MachineryViewerFlat viewer = (MachineryViewerFlat) event.getGestureSource();

				Model.Projection projection = model.project(world.getX(), world.getY());

				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}

				event.consume();

			} else if (event.getGestureSource() instanceof BufferViewerFlat) {
				BufferViewerFlat viewer = (BufferViewerFlat) event.getGestureSource();

				Model.Projection projection = model.project(world.getX(), world.getY());

				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}

				event.consume();

			} else if (event.getGestureSource() instanceof WorkshopViewerFlat) {
				WorkshopViewerFlat viewer = (WorkshopViewerFlat) event.getGestureSource();

				Model.Projection projection = model.project(world.getX(), world.getY());

				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}

				event.consume();	
				
			} else if (event.getGestureSource() instanceof StationViewerFlat) {
				StationViewerFlat viewer = (StationViewerFlat) event.getGestureSource();
				
				Model.Projection projection = model.project(world.getX(), world.getY());
				
				if (projection.seg != null) {
					viewer.entity.location.segment.set(projection.seg);
					viewer.entity.location.distance.set(projection.dot);
				}
				
				event.consume();
			} else if (event.getGestureSource() instanceof VehicleViewerFlat) {
				VehicleViewerFlat viewer = (VehicleViewerFlat) event.getGestureSource();
				
				Model.Projection projection = model.project(world.getX(), world.getY());
				
				if (projection.seg != null) {
					viewer.entity.initialLocation.segment.set(projection.seg);
					viewer.entity.initialLocation.distance.set(projection.dot);
				}
				
				event.consume();
			} else if (event.getGestureSource() instanceof DemandViewerFlat) {
				DemandViewerFlat viewer = (DemandViewerFlat) event.getGestureSource();
				
				Model.Projection projection = model.project(world.getX(), world.getY());
				
				if (projection.seg != null) {
					if (dragDemandNode == viewer.source || dragDemandNode == viewer.sourceText) {
						viewer.entity.pick.location.segment.set(projection.seg);
						viewer.entity.pick.location.distance.set(projection.dot);
					} else {
						viewer.entity.drop.location.segment.set(projection.seg);
						viewer.entity.drop.location.distance.set(projection.dot);
					}
				}
				
				event.consume();
			}
		});
		modelViewerFlat.setOnMouseDragExited(event -> {
			segmentPreviewVisible.set(false);
			demandPreviewVisible.set(false);

			event.consume();
		});
		modelViewerFlat.setOnMouseDragReleased(event -> {
			Point2D world = modelViewerFlat.unproject(event);

			if (event.getGestureSource() instanceof IntersectionViewerFlat) {
				if (event.isControlDown()) {
					IntersectionViewerFlat viewer = (IntersectionViewerFlat) event.getGestureSource();

					if (!event.isAltDown()) {
						world = modelViewerFlat.grid.snap(world);
					}
	
					Intersection other = new Intersection();
					
					other.name.set("Intersection " + (model.intersections.size() + 1));
					other.coordinate.x.set(world.getX());
					other.coordinate.y.set(world.getY());
					other.coordinate.z.set(0);
					
					model.intersections.add(other);
					
					Segment segment = new Segment(viewer.entity, other);
					
					viewer.entity.outgoing.add(segment);
					other.incoming.add(segment);
					
					model.segments.add(segment);
					
					select(modelViewerFlat.segmentViewers.get(segment));
	
					event.consume();
				}
			} else if (event.getGestureSource() instanceof SegmentViewerFlat) {
				demandPreviewVisible.set(false);

				Demand demand = new Demand();
				
				demand.size.set(1);

				demand.pick.location.segment.set(demandPreviewPickLoc.segment.get());
				demand.pick.location.distance.set(demandPreviewPickLoc.distance.get());

				demand.drop.location.segment.set(demandPreviewDropLoc.segment.get());
				demand.drop.location.distance.set(demandPreviewDropLoc.distance.get());

				model.demands.add(demand);

				select(modelViewerFlat.demandViewers.get(demand));

				event.consume();
			}
		});
		
		if (deep.isSelected()) {
			root.setCenter(modelViewerDeep);
		} else {
			root.setCenter(modelViewerFlat);
		}
	}

	// - Setup
	
	private <T> void setup(ObservableList<T> list, GenericListChangeListener.Handler<T> remove, GenericListChangeListener.Handler<T> add) {
		list.addListener(new GenericListChangeListener<T>(remove, add));
		for (T item : list) {
			add.handle(item);
		}
	}

	// - Attach
	
	private void attach(IntersectionViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);

				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			select(viewer);

			viewer.startFullDrag();

			if (event.isControlDown()) {
				segmentPreviewVisible.set(true);
				
				segmentPreviewLine.startXProperty().bind(viewer.entity.coordinate.x);
				segmentPreviewLine.startYProperty().bind(viewer.entity.coordinate.y);
				
				segmentPreviewEndX.set(viewer.entity.coordinate.x.get());
				segmentPreviewEndY.set(viewer.entity.coordinate.y.get());
			}

			event.consume();
		});
		viewer.setOnMouseDragEntered(event -> {
			if (event.getGestureSource() instanceof IntersectionViewerFlat && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					viewer.selected.set(true);
					
					segmentPreviewEndX.set(viewer.entity.coordinate.x.get());
					segmentPreviewEndY.set(viewer.entity.coordinate.y.get());
					
					event.consume();
				}
			}
		});
		viewer.setOnMouseDragOver(event -> {
			if (event.getGestureSource() instanceof IntersectionViewerFlat && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					event.consume();
				}
			}
		});
		viewer.setOnMouseDragExited(event -> {
			if (event.getGestureSource() instanceof IntersectionViewerFlat && event.getGestureSource() != viewer) {
				if (event.isControlDown()) {
					viewer.selected.set(false);
					event.consume();
				}
			}
		});
		viewer.setOnMouseDragReleased(event -> {
			if (event.getGestureSource() instanceof IntersectionViewerFlat && event.getGestureSource() != viewer) {
				IntersectionViewerFlat other = (IntersectionViewerFlat) event.getGestureSource();

				if (event.isControlDown()) {
					segmentPreviewVisible.set(false);

					Segment segment = new Segment(other.entity, viewer.entity);
					
					other.entity.outgoing.add(segment);
					viewer.entity.incoming.add(segment);
					
					model.segments.add(segment);
					
					select(modelViewerFlat.segmentViewers.get(segment));
					
					event.consume();
				}
			}
		});
	}

	private void attach(SegmentViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				Point2D world = modelViewerFlat.unproject(event);

				Segment.Projection projection = viewer.entity.project(world.getX(), world.getY());
				
				if (currentTool == Tool.PLACE_STATION) {

            		// Erstellung einer Station an der berechneten Position
            		Station station = new Station();
            		station.speed.set(1);
            		station.location.segment.set(viewer.entity);
            		station.location.distance.set(projection.dot);

            		model.stations.add(station);
            		select(modelViewerFlat.stationViewers.get(station));

            		setTool(Tool.SELECT); // Rücksetzen

        		} else if (currentTool == Tool.PLACE_INCOMINGGOODS) {

            		// Erstellung eines Incominggoods-Objekts an der geklickten Segmentposition
            		Incominggoods incominggoods = new Incominggoods();
            		incominggoods.speed.set(1);
            		incominggoods.location.segment.set(viewer.entity);
            		incominggoods.location.distance.set(projection.dot);

            		model.incominggoods.add(incominggoods);
            		select(modelViewerFlat.incominggoodsViewers.get(incominggoods));

            		setTool(Tool.SELECT);

        		} else if (currentTool == Tool.PLACE_OUTCOMINGGOODS) {

					// Erstellung eines Outcominggoods-Objekts an der geklickten Segmentposition
   					Outcominggoods outcominggoods = new Outcominggoods();
    				outcominggoods.speed.set(1);
    				outcominggoods.location.segment.set(viewer.entity);
    				outcominggoods.location.distance.set(projection.dot);

    				model.outcominggoods.add(outcominggoods);
    				select(modelViewerFlat.outcominggoodsViewers.get(outcominggoods));

    				setTool(Tool.SELECT);

        		} else if (currentTool == Tool.PLACE_PACKAGE) {
					
					// Erstellung eines Packaging-Objekts an der geklickten Segmentposition
					Packaging packaging = new Packaging();
					packaging.speed.set(1);
					packaging.location.segment.set(viewer.entity);
					packaging.location.distance.set(projection.dot);

					model.packaging.add(packaging);
					select(modelViewerFlat.packagingViewers.get(packaging));

					setTool(Tool.SELECT);

				} else if (currentTool == Tool.PLACE_WAREHOUSE) {
					
					// Erstellung eines Warehouse-Objekts an der geklickten Segmentposition
					Warehouse warehouse = new Warehouse();
					warehouse.speed.set(1);
					warehouse.location.segment.set(viewer.entity);
					warehouse.location.distance.set(projection.dot);

					model.warehouse.add(warehouse);
					select(modelViewerFlat.warehouseViewers.get(warehouse));

					setTool(Tool.SELECT);

				} else if (currentTool == Tool.PLACE_BATTERYCHARGINGSTATION) {

					// Erstellung eines Batterychargingstation-Objekts an der geklickten Segmentposition
					Batterychargingstation b = new Batterychargingstation();
					b.speed.set(1);
					b.location.segment.set(viewer.entity);
					b.location.distance.set(projection.dot);

					model.batterychargingstation.add(b);
					select(modelViewerFlat.batterychargingstationViewers.get(b));

					setTool(Tool.SELECT);

				} else if (currentTool == Tool.PLACE_INSTALLATIONSITE) {

					// Erstellung eines Installationsite-Objekts an der geklickten Segmentposition
					Installationsite i = new Installationsite();
					i.speed.set(1);
					i.location.segment.set(viewer.entity);
					i.location.distance.set(projection.dot);

					model.installationsite.add(i);
					select(modelViewerFlat.installationsiteViewers.get(i));

					setTool(Tool.SELECT);

				} else if (currentTool == Tool.PLACE_MACHINERY) {

					// Erstellung eines Machinery-Objekts an der geklickten Segmentposition
					Machinery m = new Machinery();
					m.speed.set(1);
					m.location.segment.set(viewer.entity);
					m.location.distance.set(projection.dot);

					model.machinery.add(m);
					select(modelViewerFlat.machineryViewers.get(m));

					setTool(Tool.SELECT);

				} else if (currentTool == Tool.PLACE_BUFFER) {

					// Erstellung eines Buffer-Objekts an der geklickten Segmentposition
					Buffer b = new Buffer();
					b.speed.set(1);
					b.location.segment.set(viewer.entity);
					b.location.distance.set(projection.dot);

					model.buffer.add(b);
					select(modelViewerFlat.bufferViewers.get(b));

					setTool(Tool.SELECT);

				} else if (currentTool == Tool.PLACE_WORKSHOP) {

					// Erstellung eines Workshop-Objekts an der geklickten Segmentposition
					Workshop w = new Workshop();
					w.speed.set(1);
					w.location.segment.set(viewer.entity);
					w.location.distance.set(projection.dot);

					model.workshop.add(w);
					select(modelViewerFlat.workshopViewers.get(w));

					setTool(Tool.SELECT);

				} else if (event.isShiftDown()) {
					Vehicle vehicle = new Vehicle();
					
					vehicle.name.set("Vehicle " + (model.vehicles.size() + 1));
					vehicle.length.set(1);
					vehicle.loadCapacity.set(1);
					vehicle.batteryCapacity.set(1000);
					vehicle.initialBatteryLevel.set(1000);
					vehicle.initialSpeed.set(1);
					vehicle.initialLocation.segment.set(viewer.entity);
					vehicle.initialLocation.distance.set(projection.dot);
					
					model.vehicles.add(vehicle);
					
					select(modelViewerFlat.vehicleViewers.get(vehicle));
				} else {
					select(viewer);
				}
				event.consume();
			}
		});

		viewer.setOnDragDetected(event -> {
			Point2D world = modelViewerFlat.unproject(event);

			Segment.Projection projection = viewer.entity.project(world.getX(), world.getY());

			demandPreviewPickLoc.segment.set(viewer.entity);
			demandPreviewPickLoc.distance.set(projection.dot);

			demandPreviewDropLoc.segment.set(viewer.entity);
			demandPreviewDropLoc.distance.set(projection.dot);

			demandPreviewVisible.set(true);
			
			viewer.startFullDrag();

			event.consume();
		});
		viewer.setOnMouseDragReleased(event -> {
			if (event.getGestureSource() instanceof SegmentViewerFlat) {
				demandPreviewVisible.set(false);
				
				Demand demand = new Demand();
				
				demand.size.set(1);
				
				demand.pick.location.segment.set(demandPreviewPickLoc.segment.get());
				demand.pick.location.distance.set(demandPreviewPickLoc.distance.get());
				
				demand.drop.location.segment.set(demandPreviewDropLoc.segment.get());
				demand.drop.location.distance.set(demandPreviewDropLoc.distance.get());
				
				model.demands.add(demand);
				
				select(modelViewerFlat.demandViewers.get(demand));
				
				event.consume();
			}
		});
	}

	private void attach(IncominggoodsViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(OutcominggoodsViewerFlat viewer) {
    	viewer.setOnMouseClicked(event -> {
        	if (event.isStillSincePress()) {
            	select(viewer);
            	event.consume();
        	}
    	});
    	viewer.setOnDragDetected(event -> {
        	viewer.startFullDrag();
        	event.consume();
    	});
	}

	private void attach(PackagingViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(WarehouseViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(BatterychargingstationViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(InstallationsiteViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(MachineryViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(BufferViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(WorkshopViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(StationViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(VehicleViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			viewer.startFullDrag();
			event.consume();
		});
	}

	private void attach(DemandViewerFlat viewer) {
		viewer.setOnMouseClicked(event -> {
			if (event.isStillSincePress()) {
				select(viewer);
				event.consume();
			}
		});
		viewer.setOnDragDetected(event -> {
			dragDemandNode = event.getPickResult().getIntersectedNode();
			if (dragDemandNode == viewer.source || dragDemandNode == viewer.sourceText || dragDemandNode == viewer.target || dragDemandNode == viewer.targetText) {
				viewer.startFullDrag();
				event.consume();	
			}
		});
	}

	// - Detach
	
	private void detach(IntersectionViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
		viewer.setOnMouseDragEntered(null);
		viewer.setOnMouseDragExited(null);
		viewer.setOnMouseDragReleased(null);
	}

	private void detach(SegmentViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}

	private void detach(StationViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}

	private void detach(IncominggoodsViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}

	private void detach(OutcominggoodsViewerFlat viewer) {
    	viewer.setOnMouseClicked(null);
   		viewer.setOnDragDetected(null);
	}

	private void detach(PackagingViewerFlat viewer) {
    	viewer.setOnMouseClicked(null);
   		viewer.setOnDragDetected(null);
	}

	private void detach(WarehouseViewerFlat viewer) {
    	viewer.setOnMouseClicked(null);
   		viewer.setOnDragDetected(null);
	}

	private void detach(BatterychargingstationViewerFlat viewer) {
    	viewer.setOnMouseClicked(null);
   		viewer.setOnDragDetected(null);
	}

	private void detach(InstallationsiteViewerFlat viewer) {
    	viewer.setOnMouseClicked(null);
   		viewer.setOnDragDetected(null);
	}

	private void detach(MachineryViewerFlat viewer) {
    	viewer.setOnMouseClicked(null);
   		viewer.setOnDragDetected(null);
	}

	private void detach(BufferViewerFlat viewer) {
    	viewer.setOnMouseClicked(null);
   		viewer.setOnDragDetected(null);
	}

	private void detach(WorkshopViewerFlat viewer) {
    	viewer.setOnMouseClicked(null);
   		viewer.setOnDragDetected(null);
	}

	private void detach(VehicleViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}
	
	private void detach(DemandViewerFlat viewer) {
		viewer.setOnMouseClicked(null);
		viewer.setOnDragDetected(null);
	}

	// - Select
	
	private void select(EntityViewer<?> viewer) {
		if (selected != null) {
			selected.selected.set(false);
		}
		
		grid.getChildren().clear();
		
		if (viewer != null) {
			viewer.selected.set(true);
			
			selected = viewer;
			
			if (viewer instanceof IntersectionViewerFlat) {
				show((IntersectionViewerFlat) viewer);
			} else if (viewer instanceof SegmentViewerFlat) {
				show((SegmentViewerFlat) viewer);
			} else if (viewer instanceof IncominggoodsViewerFlat) {
				show((IncominggoodsViewerFlat) viewer);
			} else if (viewer instanceof OutcominggoodsViewerFlat) {
    			show((OutcominggoodsViewerFlat) viewer);
			} else if (viewer instanceof PackagingViewerFlat) {
				show((PackagingViewerFlat) viewer);
			} else if (viewer instanceof WarehouseViewerFlat) {
				show((WarehouseViewerFlat) viewer);
			} else if (viewer instanceof BatterychargingstationViewerFlat) {
				show((BatterychargingstationViewerFlat) viewer);
			} else if (viewer instanceof InstallationsiteViewerFlat) {
				show((InstallationsiteViewerFlat) viewer);
			} else if (viewer instanceof MachineryViewerFlat) {
				show((MachineryViewerFlat) viewer);
			} else if (viewer instanceof BufferViewerFlat) {
				show((BufferViewerFlat) viewer);
			} else if (viewer instanceof WorkshopViewerFlat) {
				show((WorkshopViewerFlat) viewer);
			} else if (viewer instanceof StationViewerFlat) {
				show((StationViewerFlat) viewer);
			} else if (viewer instanceof VehicleViewerFlat) {
				show((VehicleViewerFlat) viewer);
			} else if (viewer instanceof DemandViewerFlat) {
				show((DemandViewerFlat) viewer);
			}
		}
	}

	// - Show
	
	private void show(IntersectionViewerFlat viewer) {
		TextField type = new TextField("Intersection");
		type.setDisable(true);
		
		TextField name = new TextField(viewer.entity.name.get());
		viewer.entity.name.bind(name.textProperty());
		
		TextField x = new TextField("" + viewer.entity.coordinate.x.get());
		x.setOnAction(event -> {
			viewer.entity.coordinate.x.set(Double.parseDouble(x.getText()));
		});
		x.focusedProperty().addListener(event -> {
			viewer.entity.coordinate.x.set(Double.parseDouble(x.getText()));
		});
		
		TextField y = new TextField("" + viewer.entity.coordinate.y.get());
		y.setOnAction(event -> {
			viewer.entity.coordinate.y.set(Double.parseDouble(y.getText()));
		});
		y.focusedProperty().addListener(event -> {
			viewer.entity.coordinate.y.set(Double.parseDouble(y.getText()));
		});
		
		TextField z = new TextField("" + viewer.entity.coordinate.z.get());
		z.setOnAction(event -> {
			viewer.entity.coordinate.z.set(Double.parseDouble(z.getText()));
		});
		z.focusedProperty().addListener(event -> {
			viewer.entity.coordinate.z.set(Double.parseDouble(z.getText()));
		});
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
		});
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Name"), 0, 1);
		grid.add(name, 1, 1);
		
		grid.add(new Label("X"), 0, 2);
		grid.add(x, 1, 2);
		
		grid.add(new Label("Y"), 0, 3);
		grid.add(y, 1, 3);
		
		grid.add(new Label("Z"), 0, 4);
		grid.add(z, 1, 4);
		
		grid.add(delete, 1, 5);
	}
	
	private void show(SegmentViewerFlat viewer) {
		TextField type = new TextField("Segment");
		type.setDisable(true);
		
		TextField start = new TextField();
		start.textProperty().bind(viewer.entity.start.name);
		start.setDisable(true);
		
		TextField end = new TextField();
		end.textProperty().bind(viewer.entity.end.name);
		end.setDisable(true);
		
		TextField lanes = new TextField("" + viewer.entity.lanes.get());
		lanes.setOnAction(event -> {
			viewer.entity.lanes.set(Double.parseDouble(lanes.getText()));
		});
		lanes.focusedProperty().addListener(event -> {
			viewer.entity.lanes.set(Double.parseDouble(lanes.getText()));
		});
		
		TextField speed = new TextField("" + viewer.entity.speed.get());
		speed.setOnAction(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
		speed.focusedProperty().addListener(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
		});
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Start"), 0, 1);
		grid.add(start, 1, 1);
		
		grid.add(new Label("End"), 0, 2);
		grid.add(end, 1, 2);
		
		grid.add(new Label("Lanes"), 0, 3);
		grid.add(lanes, 1, 3);
		
		grid.add(new Label("Speed"), 0, 4);
		grid.add(speed, 1, 4);
		
		grid.add(delete, 1, 5);
	}
	
	private void show(StationViewerFlat viewer) {
		TextField type = new TextField("Station");
		type.setDisable(true);
		
		TextField speed = new TextField("" + viewer.entity.speed.get());
		speed.setOnAction(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
		speed.focusedProperty().addListener(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
		});
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Speed"), 0, 1);
		grid.add(speed, 1, 1);
		
		grid.add(delete, 1, 2);
	}
	
	private void show(IncominggoodsViewerFlat viewer) {
		TextField type = new TextField("Incominggoods");
		type.setDisable(true);
	
		TextField speed = new TextField("" + viewer.entity.speed.get());
		speed.setOnAction(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
		speed.focusedProperty().addListener(event -> {
			viewer.entity.speed.set(Double.parseDouble(speed.getText()));
		});
	
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
		});
	
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
	
		grid.add(new Label("Speed"), 0, 1);
		grid.add(speed, 1, 1);
	
		grid.add(delete, 1, 2);
	}

	private void show(OutcominggoodsViewerFlat viewer) {
    	TextField type = new TextField("Outcominggoods");
    	type.setDisable(true);

    	TextField speed = new TextField("" + viewer.entity.speed.get());
   		speed.setOnAction(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});
    	speed.focusedProperty().addListener(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});

    	Button delete = new Button("Delete");
    	delete.setOnAction(event -> {
        	model.delete(viewer.entity);
        	select(null);
    	});

    	grid.add(new Label("Type"), 0, 0);
    	grid.add(type, 1, 0);

    	grid.add(new Label("Speed"), 0, 1);
    	grid.add(speed, 1, 1);

    	grid.add(delete, 1, 2);
	}

	private void show(PackagingViewerFlat viewer) {
    	TextField type = new TextField("Packaging");
    	type.setDisable(true);

    	TextField speed = new TextField("" + viewer.entity.speed.get());
   		speed.setOnAction(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});
    	speed.focusedProperty().addListener(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});

    	Button delete = new Button("Delete");
    	delete.setOnAction(event -> {
        	model.delete(viewer.entity);
        	select(null);
    	});

    	grid.add(new Label("Type"), 0, 0);
    	grid.add(type, 1, 0);

    	grid.add(new Label("Speed"), 0, 1);
    	grid.add(speed, 1, 1);

    	grid.add(delete, 1, 2);
	}

	private void show(WarehouseViewerFlat viewer) {
    	TextField type = new TextField("Warehouse");
    	type.setDisable(true);

    	TextField speed = new TextField("" + viewer.entity.speed.get());
   		speed.setOnAction(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});
    	speed.focusedProperty().addListener(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});

    	Button delete = new Button("Delete");
    	delete.setOnAction(event -> {
        	model.delete(viewer.entity);
        	select(null);
    	});

    	grid.add(new Label("Type"), 0, 0);
    	grid.add(type, 1, 0);

    	grid.add(new Label("Speed"), 0, 1);
    	grid.add(speed, 1, 1);

    	grid.add(delete, 1, 2);
	}

	private void show(BatterychargingstationViewerFlat viewer) {
    	TextField type = new TextField("Batterychargingstation");
    	type.setDisable(true);

    	TextField speed = new TextField("" + viewer.entity.speed.get());
   		speed.setOnAction(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});
    	speed.focusedProperty().addListener(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});

    	Button delete = new Button("Delete");
    	delete.setOnAction(event -> {
        	model.delete(viewer.entity);
        	select(null);
    	});

    	grid.add(new Label("Type"), 0, 0);
    	grid.add(type, 1, 0);

    	grid.add(new Label("Speed"), 0, 1);
    	grid.add(speed, 1, 1);

    	grid.add(delete, 1, 2);
	}

	private void show(InstallationsiteViewerFlat viewer) {
    	TextField type = new TextField("Installationsite");
    	type.setDisable(true);

    	TextField speed = new TextField("" + viewer.entity.speed.get());
   		speed.setOnAction(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});
    	speed.focusedProperty().addListener(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});

    	Button delete = new Button("Delete");
    	delete.setOnAction(event -> {
        	model.delete(viewer.entity);
        	select(null);
    	});

    	grid.add(new Label("Type"), 0, 0);
    	grid.add(type, 1, 0);

    	grid.add(new Label("Speed"), 0, 1);
    	grid.add(speed, 1, 1);

    	grid.add(delete, 1, 2);
	}

	private void show(MachineryViewerFlat viewer) {
    	TextField type = new TextField("Machinery");
    	type.setDisable(true);

    	TextField speed = new TextField("" + viewer.entity.speed.get());
   		speed.setOnAction(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});
    	speed.focusedProperty().addListener(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});

    	Button delete = new Button("Delete");
    	delete.setOnAction(event -> {
        	model.delete(viewer.entity);
        	select(null);
    	});

    	grid.add(new Label("Type"), 0, 0);
    	grid.add(type, 1, 0);

    	grid.add(new Label("Speed"), 0, 1);
    	grid.add(speed, 1, 1);

    	grid.add(delete, 1, 2);
	}

	private void show(BufferViewerFlat viewer) {
    	TextField type = new TextField("Buffer");
    	type.setDisable(true);

    	TextField speed = new TextField("" + viewer.entity.speed.get());
   		speed.setOnAction(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});
    	speed.focusedProperty().addListener(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});

    	Button delete = new Button("Delete");
    	delete.setOnAction(event -> {
        	model.delete(viewer.entity);
        	select(null);
    	});

    	grid.add(new Label("Type"), 0, 0);
    	grid.add(type, 1, 0);

    	grid.add(new Label("Speed"), 0, 1);
    	grid.add(speed, 1, 1);

    	grid.add(delete, 1, 2);
	}

	private void show(WorkshopViewerFlat viewer) {
    	TextField type = new TextField("Workshop");
    	type.setDisable(true);

    	TextField speed = new TextField("" + viewer.entity.speed.get());
   		speed.setOnAction(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});
    	speed.focusedProperty().addListener(event -> {
        	viewer.entity.speed.set(Double.parseDouble(speed.getText()));
    	});

    	Button delete = new Button("Delete");
    	delete.setOnAction(event -> {
        	model.delete(viewer.entity);
        	select(null);
    	});

    	grid.add(new Label("Type"), 0, 0);
    	grid.add(type, 1, 0);

    	grid.add(new Label("Speed"), 0, 1);
    	grid.add(speed, 1, 1);

    	grid.add(delete, 1, 2);
	}

	private void show(VehicleViewerFlat viewer) {
		TextField type = new TextField("Vehicle");
		type.setDisable(true);
		
		TextField name = new TextField(viewer.entity.name.get());
		viewer.entity.name.bind(name.textProperty());
		
		TextField length = new TextField("" + viewer.entity.length.get());
		length.setOnAction(event -> {
			viewer.entity.length.set(Double.parseDouble(length.getText()));
		});
		length.focusedProperty().addListener(event -> {
			viewer.entity.length.set(Double.parseDouble(length.getText()));
		});
		
		TextField batteryCapacity = new TextField("" + viewer.entity.batteryCapacity.get());
		batteryCapacity.setOnAction(event -> {
			viewer.entity.batteryCapacity.set(Double.parseDouble(batteryCapacity.getText()));
		});
		batteryCapacity.focusedProperty().addListener(event -> {
			viewer.entity.batteryCapacity.set(Double.parseDouble(batteryCapacity.getText()));
		});
		
		TextField loadCapacity = new TextField("" + viewer.entity.loadCapacity.get());
		loadCapacity.setOnAction(event -> {
			viewer.entity.loadCapacity.set(Double.parseDouble(loadCapacity.getText()));
		});
		loadCapacity.focusedProperty().addListener(event -> {
			viewer.entity.loadCapacity.set(Double.parseDouble(loadCapacity.getText()));
		});
		
		TextField initialBatteryLevel = new TextField("" + viewer.entity.initialBatteryLevel.get());
		initialBatteryLevel.setOnAction(event -> {
			viewer.entity.initialBatteryLevel.set(Double.parseDouble(initialBatteryLevel.getText()));
		});
		initialBatteryLevel.focusedProperty().addListener(event -> {
			viewer.entity.initialBatteryLevel.set(Double.parseDouble(initialBatteryLevel.getText()));
		});
		
		TextField initialSpeed = new TextField("" + viewer.entity.initialSpeed.get());
		initialSpeed.setOnAction(event -> {
			viewer.entity.initialSpeed.set(Double.parseDouble(initialSpeed.getText()));
		});
		initialSpeed.focusedProperty().addListener(event -> {
			viewer.entity.initialSpeed.set(Double.parseDouble(initialSpeed.getText()));
		});
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
		});
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Name"), 0, 1);
		grid.add(name, 1, 1);
		
		grid.add(new Label("Length"), 0, 2);
		grid.add(length, 1, 2);
		
		grid.add(new Label("Battery capacity"), 0, 3);
		grid.add(batteryCapacity, 1, 3);
		
		grid.add(new Label("Load capacity"), 0, 4);
		grid.add(loadCapacity, 1, 4);
		
		grid.add(new Label("Initial battery level"), 0, 5);
		grid.add(initialBatteryLevel, 1, 5);
		
		grid.add(new Label("Initial speed"), 0, 6);
		grid.add(initialSpeed, 1, 6);
		
		grid.add(delete, 1, 7);
	}
	
	private void show(DemandViewerFlat viewer) {
		TextField type = new TextField("Demand");
		type.setDisable(true);
		
		TextField size = new TextField("" + viewer.entity.size.get());
		size.setOnAction(event -> {
			viewer.entity.size.set(Double.parseDouble(size.getText()));
		});
		size.focusedProperty().addListener(event -> {
			viewer.entity.size.set(Double.parseDouble(size.getText()));
		});
		
		TextField pick = new TextField("" + viewer.entity.pick.time.get());
		pick.setOnAction(event -> {
			viewer.entity.pick.time.set(Double.parseDouble(pick.getText()));
		});
		pick.focusedProperty().addListener(event -> {
			viewer.entity.pick.time.set(Double.parseDouble(pick.getText()));
		});
		
		TextField drop = new TextField("" + viewer.entity.drop.time.get());
		drop.setOnAction(event -> {
			viewer.entity.drop.time.set(Double.parseDouble(drop.getText()));
		});
		drop.focusedProperty().addListener(event -> {
			viewer.entity.drop.time.set(Double.parseDouble(drop.getText()));
		});
		
		Button delete = new Button("Delete");
		delete.setOnAction(event -> {
			model.delete(viewer.entity);
			select(null);
		});
		
		grid.add(new Label("Type"), 0, 0);
		grid.add(type, 1, 0);
		
		grid.add(new Label("Size"), 0, 1);
		grid.add(size, 1, 1);
		
		grid.add(new Label("Pick"), 0, 2);
		grid.add(pick, 1, 2);
		
		grid.add(new Label("Drop"), 0, 3);
		grid.add(drop, 1, 3);
		
		grid.add(delete, 1, 4);
	}



		/**
	 * Setzt das aktuelle Werkzeug und aktualisiert die Anzeige in der Oberfläche.
	 *
	 * @param tool Werkzeug, das ausgewählt werden soll
	 */
	private void setTool(Tool tool) {
		currentTool = tool;
		updateToolLabel();
	}
	
		/**
	 * Aktualisiert den Text des Werkzeug-Indikators.
	 * Der Benutzer sieht dadurch jederzeit, welches Element aktuell aktiv ist.
	 */
	private void updateToolLabel() {
		switch (currentTool) {
			case PLACE_STATION:
				currentToolLabel.setText("Aktuelles Werkzeug: Station");
				break;
			case PLACE_INCOMINGGOODS:
				currentToolLabel.setText("Aktuelles Werkzeug: Incominggoods");
				break;
			case PLACE_OUTCOMINGGOODS:
				currentToolLabel.setText("Aktuelles Werkzeug: Outcominggoods");
				break;
			case PLACE_PACKAGE:
				currentToolLabel.setText("Aktuelles Werkzeug: Packaging");
				break;
			case PLACE_WAREHOUSE:
				currentToolLabel.setText("Aktuelles Werkzeug: Warehouse");
				break;
			case PLACE_BATTERYCHARGINGSTATION:
				currentToolLabel.setText("Aktuelles Werkzeug: Batterychargingstation");
				break;
			case PLACE_INSTALLATIONSITE:
				currentToolLabel.setText("Aktuelles Werkzeug: Installationsite");
				break;
			case PLACE_MACHINERY:
				currentToolLabel.setText("Aktuelles Werkzeug: Machinery");
				break;
			case PLACE_BUFFER:
				currentToolLabel.setText("Aktuelles Werkzeug: Buffer");
				break;
			case PLACE_WORKSHOP:
				currentToolLabel.setText("Aktuelles Werkzeug: Workshop");
				break;
			default:
				currentToolLabel.setText("Aktuelles Werkzeug: Keines Ausgewählt");
				break;
		}
	}


	private Rectangle colorBox(Color color) {
		Rectangle rectangle = new Rectangle(14, 14);
		rectangle.setFill(color);
		rectangle.setStroke(Color.BLACK);
		return rectangle;
	}


}






