package io.github.ghackenberg.mbse.transport.fx.programs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.ghackenberg.mbse.transport.core.Controller;
import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Parser;
import io.github.ghackenberg.mbse.transport.core.Simulator;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.core.controllers.GreedyController;
import io.github.ghackenberg.mbse.transport.core.controllers.RandomController;
import io.github.ghackenberg.mbse.transport.core.controllers.SmartController;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.exceptions.ArgumentsException;
import io.github.ghackenberg.mbse.transport.fx.charts.HistogramChart;
import io.github.ghackenberg.mbse.transport.fx.charts.PercentageChart;
import io.github.ghackenberg.mbse.transport.fx.viewers.ModelViewer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Comparison extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	final Parser parser = new Parser();
	
	final int processorCount = Runtime.getRuntime().availableProcessors() - 1;
	
	final double maxModelTimeStep = Double.MAX_VALUE;
	final double ratioModelRealTime = -1;
	
	final int demandCount = 1000;
	
	final double maxDemandSize = 1;
	final double maxDemandTime = 1000000;
	final double maxDemandDuration = 100000;
	
	final int replicationCount = 1000;
	
	final List<Class<? extends Controller>> controllerClasses = new ArrayList<>();
	
	int totalCount;
	int finishedCount = 0;
	
	final List<String> names = new ArrayList<>();
	final List<File> folders = new ArrayList<>();
	final List<List<Model>> models = new ArrayList<>();
	final List<List<Statistics>> statistics = new ArrayList<>();
	
	final List<List<Integer>> replicationCounts = new ArrayList<>(); 
	
	final List<List<ModelViewer>> modelViewers = new ArrayList<>();
	final List<List<PercentageChart>> percentageViewers = new ArrayList<>();
	final List<List<HistogramChart>> histogramViewers = new ArrayList<>();
	
	AnimationTimer animation;
	
	final ObservableList<String> modelPaneListItems = FXCollections.observableArrayList();
	final ListView<String> modelPaneList = new ListView<>(modelPaneListItems);
	final BorderPane modelPane = new BorderPane();
	
	final ProgressBar progress = new ProgressBar();

	final GridPane chartPane = new GridPane();
	
	final Tab chartsTab = new Tab("Charts", chartPane);
	final Tab simulatorsTab = new Tab("Simulators", modelPane);
	
	final ToolBar top = new ToolBar(new Label("Progress:"), progress);
	final ToolBar bottom = new ToolBar(new Label("(c) 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, FH Upper Austria."));
	
	final TabPane mainPane = new TabPane();
	final BorderPane rootPane = new BorderPane();
	
	Scene scene;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Register controller classes
		
		controllerClasses.add(RandomController.class);
		controllerClasses.add(GreedyController.class);
		controllerClasses.add(SmartController.class);
		
		// Parse models
		
		File modelFolder = null;
		
		do {
			
			// Choose folder

			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(modelFolder != null ? modelFolder.getParentFile() : null);
			
			modelFolder = chooser.showDialog(primaryStage);
			
			if (modelFolder == null) {
				break;
			}
			
			names.add(modelFolder.getName());
			
			// Check runs folder

			File runsFolder = new File(modelFolder, "runs");
			
			if (!runsFolder.exists())
				runsFolder.mkdir();
			else if (!runsFolder.isDirectory())
				throw new ArgumentsException("Path to model contains a runs file");

			File indexRunsFolder = new File(runsFolder, "run-" + folders.size());
			
			if (!indexRunsFolder.exists())
				indexRunsFolder.mkdir();
			else if (!indexRunsFolder.isDirectory())
				throw new ArgumentsException("Path to model runs contains a run-" + folders.size() + " file");
			
			folders.add(indexRunsFolder);
			
			// Files
			
			File intersections = new File(modelFolder, "intersections.txt");
			File segments = new File(modelFolder, "segments.txt");
			File stations = new File(modelFolder, "stations.txt");
			File vehicles = new File(modelFolder, "vehicles.txt");
			File demands = new File(modelFolder, "demands.txt");
			
			// Parse
			
			List<Model> procModels = new ArrayList<>();
			
			for (int processor = 0; processor < processorCount; processor++) {
				Model model = parser.parse(intersections, segments, stations, vehicles, demands);
				model.name = modelFolder.getName();
				model.demands.clear();
				
				procModels.add(model);
			}
			
			// Add
			
			models.add(procModels);
			
		} while (true);
		
		// Generate demands
		
		for (int index = 0; index < demandCount; index++) {
			// Select size
			
			double size = Math.random() * maxDemandSize;
			
			// Select times
			
			double pickupTime = Math.random() * maxDemandTime;
			double dropoffTime = pickupTime + Math.random() * maxDemandDuration;
			
			// Select segments
			
			int pickupSegmentNumber = (int) (Math.random() * models.get(0).get(0).segments.size());
			int dropoffSegmentNumber = (int) (Math.random() * models.get(0).get(0).segments.size());
			
			// Select distance (in percent)
			
			double pickupDistance = Math.random();
			double dropoffDistance = Math.random();
			
			// Check segment validity
			
			boolean valid = true;
			
			for (List<Model> procModels : models) {
				for (Model model : procModels) {
					for (Station station : model.stations) {
						if (station.location.getSegment() == model.segments.get(pickupSegmentNumber)) {
							valid = false;
						} else if (station.location.getSegment() == model.segments.get(dropoffSegmentNumber)) {
							valid = false;
						}
						if (!valid) {
							break;
						}
					}
					if (!valid) {
						break;
					}
				}
			}
			
			if (!valid) {
				index--;
				continue;
			}
			
			// Process models

			for (List<Model> procModels : models) {
				for (Model model : procModels) {
					Segment pickupSegment = model.segments.get(pickupSegmentNumber);
					Segment dropoffSegment = model.segments.get(dropoffSegmentNumber);
					
					Demand demand = new Demand(pickupSegment, pickupDistance * pickupSegment.getLength(), pickupTime, dropoffSegment, dropoffDistance * dropoffSegment.getLength(), dropoffTime, size);
					
					model.demands.add(demand);
				}
			}
		}
		
		// Sort demands and reset models

		for (List<Model> procModels : models) {
			for (Model model : procModels) {
				model.demands.sort((first, second) -> (int) Math.signum(first.pickup.time - second.pickup.time));
				model.reset();
			}
		}
		
		// Generate statistics
		
		for (List<Model> procModels : models) {
			
			List<Statistics> procStatistics = new ArrayList<>();
			
			for (Model model : procModels) {
				Statistics statistic = new Statistics(model);
				statistic.reset();
				
				procStatistics.add(statistic);
			}
			
			statistics.add(procStatistics);
		}
		
		// Model viewers
		
		for (List<Model> procModels : models) {
			List<ModelViewer> procViewers = new ArrayList<>();
			
			for (Model model : procModels) {
				procViewers.add(new ModelViewer(model));
			}
			
			modelViewers.add(procViewers);
		}
		
		// Model pane
		
		for (int processor = 0; processor < processorCount; processor++) {
			modelPaneListItems.add("Processor " + processor);
		}
		
		modelPaneList.getSelectionModel().selectedIndexProperty().addListener(event -> {
			//int index = modelPaneList.getSelectionModel().getSelectedIndex();
			
			//modelPane.setCenter(viewers.get(index));
		});
		
		modelPane.setLeft(modelPaneList);
		
		// Update viewers
		
		animation = new AnimationTimer() {
			@Override
			public void handle(long now) {
				for (List<ModelViewer> procViewers : modelViewers) {
					for (ModelViewer viewer : procViewers) {
						viewer.update();
					}
				}
			}
		};
		animation.start();
		
		// Replication counts
		
		totalCount = replicationCount * models.size() * controllerClasses.size();
		
		for (int i = 0; i < models.size(); i++) {
			List<Integer> controllerCounts = new ArrayList<>();
			
			for (int j = 0; j < controllerClasses.size(); j++) {
				controllerCounts.add(0);
			}
			
			replicationCounts.add(controllerCounts);
		}
		
		// Create pie charts
		
		for (String name : names) {
			List<PercentageChart> controllerCharts = new ArrayList<>();
			
			for (Class<? extends Controller> controllerClass : controllerClasses) {
				controllerCharts.add(new PercentageChart(name + " + " + controllerClass.getSimpleName()));
			}
			
			percentageViewers.add(controllerCharts);
		}
		
		// Create histogram chart
		
		for (String name : names) {
			List<HistogramChart> controllerCharts = new ArrayList<>();
			
			for (Class<? extends Controller> controllerClass : controllerClasses) {
				controllerCharts.add(new HistogramChart(name + " + " + controllerClass.getSimpleName(), "Finished time (in min)"));
			}
			
			histogramViewers.add(controllerCharts);
		}
		
		// Chart pane
		
		chartPane.setHgap(10);
		chartPane.setVgap(10);
		chartPane.setPadding(new Insets(10));
		
		for (int i = 0; i < models.size() * controllerClasses.size(); i++) {
			chartPane.getColumnConstraints().add(createColumnConstraints(100. / models.size() / controllerClasses.size()));	
		}
		chartPane.getRowConstraints().add(createRowConstraints(100 / 2.));
		chartPane.getRowConstraints().add(createRowConstraints(100 / 2.));
		chartPane.getRowConstraints().add(createRowConstraints(100 / 2.));
		
		for (int i = 0; i < models.size(); i++) {
			chartPane.add(new ModelViewer(models.get(i).get(0), false), i * controllerClasses.size(), 0, controllerClasses.size(), 1);	
			for (int j = 0; j < controllerClasses.size(); j++) {
				chartPane.add(percentageViewers.get(i).get(j), i * controllerClasses.size() + j, 1);
				chartPane.add(histogramViewers.get(i).get(j), i * controllerClasses.size() + j, 2);	
			}
		}
		
		// Tabs
		
		chartsTab.setClosable(false);
		simulatorsTab.setClosable(false);
		
		mainPane.getTabs().add(chartsTab);
		mainPane.getTabs().add(simulatorsTab);
		
		// Border
		
		rootPane.setTop(top);
		rootPane.setCenter(mainPane);
		rootPane.setBottom(bottom);
		
		// Scene
		
		scene = new Scene(rootPane, 640, 480);
		
		// Threads
		
		for (int processor = 0; processor < processorCount; processor++) {
			final int processorIndex = processor;
			
			Thread thread = new Thread(() -> {
				while (true) {
					int modelIndex = -1;
					int controllerIndex = -1;
					
					int repCount = -1;

					String name = null;
					File folder = null;
					
					Model model = null;
					Statistics statistic = null;

					Class<? extends Controller> controllerClass = null;
					Controller controller = null;
					
					Simulator simulator = null;
					
					synchronized (replicationCounts) {
						for (int i = 0; i < models.size(); i++) {
							for (int j = 0; j < controllerClasses.size(); j++) {
								repCount = replicationCounts.get(i).get(j);
								
								if (repCount < replicationCount) {
									modelIndex = i;
									controllerIndex = j; 
									
									name = names.get(i);
									folder = folders.get(i);
									
									model = models.get(i).removeFirst();
									statistic = statistics.get(i).removeFirst();
									
									controllerClass = controllerClasses.get(j);
									if (controllerClass == RandomController.class) {
										controller = new RandomController();
									} else if (controllerClass == GreedyController.class) {
										controller = new GreedyController(model);
									} else if (controllerClass == SmartController.class) {
										controller = new SmartController(model);
									} else {
										throw new IllegalStateException("Controller class not supported!");
									}
									
									replicationCounts.get(modelIndex).set(controllerIndex, ++repCount);
									
									System.out.println(modelIndex + "-" + controllerIndex + "-" + repCount);
									
									break;
								}
							}
							
							if (modelIndex != -1 && controllerIndex != -1) {
								break;
							}
						}
						
						if (modelIndex == -1 || controllerIndex == -1) {
							return;
						}
					}
					
					simulator = new Simulator(name + "-" + controllerClass.getSimpleName() + "-" + repCount, model, controller, statistic, maxModelTimeStep, ratioModelRealTime, folder);
					simulator.loop();
					
					synchronized (replicationCounts) {
						if (simulator.isFinished()) {
							percentageViewers.get(modelIndex).get(controllerIndex).increment("Finished");
							histogramViewers.get(modelIndex).get(controllerIndex).add(controllerClass.getSimpleName(), model.time / 1000 / 60);
						} else if (simulator.isEmpty()) {
							percentageViewers.get(modelIndex).get(controllerIndex).increment("Empty");
						} else if (Double.isInfinite(model.time)) {
							percentageViewers.get(modelIndex).get(controllerIndex).increment("Infinite");
						} else {
							percentageViewers.get(modelIndex).get(controllerIndex).increment("Collision");
						}
						
						models.get(modelIndex).add(model);
						statistics.get(modelIndex).add(statistic);
						
						final int myModelIndex = modelIndex;
						final int myControllerIndex = controllerIndex;
						
						Platform.runLater(() -> {
							update(processorIndex, myModelIndex, myControllerIndex);
						});
					}
				}
			});
			thread.start();
		}
		
		// Stage
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Transport-IDE Monte-Carlo Experiment");
		primaryStage.show();
	}
	
	private void update(int processorIndex, int modelIndex, int controllerIndex) {
		synchronized (replicationCounts) {
			// Pie chart
			
			percentageViewers.get(modelIndex).get(controllerIndex).update();
				
			// Histogram chart
			
			double min = +Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;
			
			for (List<HistogramChart> ctrlViewers : histogramViewers) {
				for (HistogramChart ctrlViewer : ctrlViewers) {
					min = Math.min(min, ctrlViewer.getMin());
					max = Math.max(max, ctrlViewer.getMax());
				}
			}

			for (List<HistogramChart> ctrlViewers : histogramViewers) {
				for (HistogramChart ctrlViewer : ctrlViewers) {
					ctrlViewer.update(min, max);
				}
			}
			
			// Progress bar
				
			progress.setProgress(++finishedCount / (double) totalCount);
		}
	}
	
	private ColumnConstraints createColumnConstraints(double percentage) {
		ColumnConstraints constraints = new ColumnConstraints();
		
		constraints.setPercentWidth(percentage);
		//constraints.setHgrow(Priority.ALWAYS);
		
		return constraints;
	}
	
	private RowConstraints createRowConstraints(double percentage) {
		RowConstraints constraints = new RowConstraints();
		
		constraints.setPercentHeight(percentage);
		//constraints.setVgrow(Priority.ALWAYS);
		
		return constraints;
	}

}
