package example.program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import example.controller.Controller;
import example.controller.implementations.GreedyController;
import example.controller.implementations.RandomController;
import example.controller.implementations.SmartController;
import example.model.Demand;
import example.model.Model;
import example.model.Segment;
import example.model.Station;
import example.parser.Parser;
import example.program.exceptions.ArgumentsException;
import example.program.viewers.ModelViewer;
import example.simulator.Simulator;
import example.statistics.implementations.ExampleStatistics;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
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

public class StatisticComparisonProgram extends Application {

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
	
	final int replicationCount = 200;
	
	final List<Class<? extends Controller>> controllerClasses = new ArrayList<>();
	
	int totalCount;
	int finishedCount = 0;
	
	final List<String> names = new ArrayList<>();
	final List<File> folders = new ArrayList<>();
	final List<List<Model>> models = new ArrayList<>();
	final List<List<ExampleStatistics>> statistics = new ArrayList<>();
	
	final List<List<Integer>> replicationCounts = new ArrayList<>(); 
	
	final List<List<ModelViewer>> viewers = new ArrayList<>();
	final ObservableList<String> modelPaneListItems = FXCollections.observableArrayList();
	final ListView<String> modelPaneList = new ListView<>(modelPaneListItems);
	final BorderPane modelPane = new BorderPane();
	
	AnimationTimer animation;
	
	final List<List<List<Double>>> finishedTimes = new ArrayList<>();
	final List<List<List<Double>>> emptyTimes = new ArrayList<>();
	final List<List<List<Double>>> collisionTimes = new ArrayList<>();
	final List<List<List<Double>>> infiniteTimes = new ArrayList<>();
	
	final List<List<ObservableList<PieChart.Data>>> pieData = new ArrayList<>();
	final List<List<PieChart>> pieCharts = new ArrayList<>();
	
	final int histBinCount = 30;
	final double[] histBinX = new double[histBinCount];
	final List<List<double[]>> histBinY = new ArrayList<>();
	final List<List<XYChart.Series<String, Number>>> histSeries = new ArrayList<>();
	final CategoryAxis histAxisX = new CategoryAxis();
	final NumberAxis histAxisY = new NumberAxis();
	final BarChart<String, Number> histChart = new BarChart<>(histAxisX, histAxisY);
	
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
		
		do {
			
			// Choose folder
			
			File modelFolder = new DirectoryChooser().showDialog(primaryStage);
			
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
						if (station.location.segment == model.segments.get(pickupSegmentNumber)) {
							valid = false;
						} else if (station.location.segment == model.segments.get(dropoffSegmentNumber)) {
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
			
			List<ExampleStatistics> procStatistics = new ArrayList<>();
			
			for (Model model : procModels) {
				ExampleStatistics statistic = new ExampleStatistics(model);
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
			
			viewers.add(procViewers);
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
				for (List<ModelViewer> procViewers : viewers) {
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
		
		// Initialize times
		
		for (int i = 0; i < models.size(); i++) {
			List<List<Double>> ctrlFinishedTimes = new ArrayList<>();
			List<List<Double>> ctrlEmptyTimes = new ArrayList<>();
			List<List<Double>> ctrlCollisionTimes = new ArrayList<>();
			List<List<Double>> ctrlInfiniteTimes = new ArrayList<>();
			
			for (int j = 0; j < controllerClasses.size(); j++) {
				ctrlFinishedTimes.add(new ArrayList<>());
				ctrlEmptyTimes.add(new ArrayList<>());
				ctrlCollisionTimes.add(new ArrayList<>());
				ctrlInfiniteTimes.add(new ArrayList<>());
			}
			
			finishedTimes.add(ctrlFinishedTimes);
			emptyTimes.add(ctrlEmptyTimes);
			collisionTimes.add(ctrlCollisionTimes);
			infiniteTimes.add(ctrlInfiniteTimes);
		}
		
		// Create pie charts
		
		for (String name : names) {
			List<ObservableList<PieChart.Data>> controllerData = new ArrayList<>();
			List<PieChart> controllerCharts = new ArrayList<>();
			
			for (Class<? extends Controller> controllerClass : controllerClasses) {
				ObservableList<PieChart.Data> data = createPieDataset();
				
				controllerData.add(data);
				
				PieChart chart = new PieChart(data);
				chart.setStyle("-fx-background-color: white;");
				chart.setTitle(name + "-" + controllerClass.getSimpleName());
				chart.setAnimated(false);
				
				controllerCharts.add(chart);
			}
			
			pieData.add(controllerData);
			pieCharts.add(controllerCharts);
		}
		
		// Create histogram chart
		
		histAxisX.setLabel("Time");
		histAxisY.setLabel("Relative frequency");
		
		for (String name : names) {
			List<double[]> ctrlBinY = new ArrayList<>();
			List<XYChart.Series<String, Number>> ctrlSeries = new ArrayList<>();
			
			for (Class<? extends Controller> controllerClass : controllerClasses) {
				ctrlBinY.add(new double[histBinCount]);
				
				XYChart.Series<String, Number> series = new XYChart.Series<>();
				series.setName(name + "-" + controllerClass.getSimpleName());
				
				ctrlSeries.add(series);
				
				histChart.getData().add(series);
			}
			
			histBinY.add(ctrlBinY);
			histSeries.add(ctrlSeries);
		}
		
		histChart.setStyle("-fx-background-color: white;");
		histChart.setTitle("Distribution of stop time in finished state");
		histChart.setAnimated(false);
		
		// Chart pane
		
		chartPane.setHgap(10);
		chartPane.setVgap(10);
		chartPane.setPadding(new Insets(10));
		
		for (int i = 0; i < models.size() * controllerClasses.size(); i++) {
			chartPane.getColumnConstraints().add(createColumnConstraints(100. / models.size() / controllerClasses.size()));	
		}
		chartPane.getRowConstraints().add(createRowConstraints(100 / 2.));
		chartPane.getRowConstraints().add(createRowConstraints(100 / 2.));
		
		for (int i = 0; i < models.size(); i++) {
			for (int j = 0; j < controllerClasses.size(); j++) {
				chartPane.add(pieCharts.get(i).get(j), i * controllerClasses.size() + j, 0);	
			}
		}
		chartPane.add(histChart, 0, 1, models.size() * controllerClasses.size(), 1);
		
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
					ExampleStatistics statistic = null;

					Class<? extends Controller> controllerClass = null;
					Controller controller = null;
					
					Simulator<ExampleStatistics> simulator = null;
					
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
					
					simulator = new Simulator<>(name + "-" + controllerClass.getSimpleName() + "-" + repCount, model, controller, statistic, maxModelTimeStep, ratioModelRealTime, folder);
					simulator.loop();
					
					synchronized (replicationCounts) {
						if (simulator.isFinished()) {
							finishedTimes.get(modelIndex).get(controllerIndex).add(model.time);
						} else if (simulator.isEmpty()) {
							emptyTimes.get(modelIndex).get(controllerIndex).add(model.time);
						} else if (Double.isInfinite(model.time)) {
							infiniteTimes.get(modelIndex).get(controllerIndex).add(model.time);
						} else {
							collisionTimes.get(modelIndex).get(controllerIndex).add(model.time);
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
			
			updatePieChart(modelIndex, controllerIndex);
				
			// Histogram chart
			
			histChart.getData().clear();
			
			double min = computeMin(finishedTimes.get(0).get(0)) - 1;
			double max = computeMax(finishedTimes.get(0).get(0)) + 1;
			
			for (int i = 0; i < models.size(); i++) {
				for (int j = 0; j < controllerClasses.size(); j++ ) {
					min = Math.min(min, computeMin(finishedTimes.get(i).get(j)) - 1);
					max = Math.max(max, computeMax(finishedTimes.get(i).get(j)) + 1);
				}
			}
			
			computeHistogramX(min, max, histBinX);
			
			for (int i = 0; i < models.size(); i++) {
				for (int j = 0; j < controllerClasses.size(); j++) {
					computeHistogramY(finishedTimes.get(i).get(j), min, max, histBinY.get(i).get(j));
					
					XYChart.Series<String, Number> seriesNew = createHistogramSeries(names.get(i) + "-" + controllerClasses.get(j).getSimpleName(), histBinX, histBinY.get(i).get(j));
					
					histChart.getData().add(seriesNew);
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
	
	private ObservableList<PieChart.Data> createPieDataset() {
		return FXCollections.observableArrayList(
			new PieChart.Data("Finished", 0),
			new PieChart.Data("Empty", 0),
			new PieChart.Data("Collision", 0),
			new PieChart.Data("Infinite", 0)
		);
	}
	
	private void updatePieChart(int modelIndex, int controllerIndex) {
		pieData.get(modelIndex).get(controllerIndex).get(0).setPieValue(finishedTimes.get(modelIndex).get(controllerIndex).size());
		pieData.get(modelIndex).get(controllerIndex).get(1).setPieValue(emptyTimes.get(modelIndex).get(controllerIndex).size());
		pieData.get(modelIndex).get(controllerIndex).get(2).setPieValue(collisionTimes.get(modelIndex).get(controllerIndex).size());
		pieData.get(modelIndex).get(controllerIndex).get(3).setPieValue(infiniteTimes.get(modelIndex).get(controllerIndex).size());
	}
	
	private double computeMin(List<Double> data) {
		double min = Double.MAX_VALUE;
		
		for (double time : data) {
			min = Math.min(time, min);
		}
		
		return min;
	}
	
	private double computeMax(List<Double> data) {
		double max = 0;
		
		for (double time : data) {
			max = Math.max(time, max);
		}
		
		return max;
	}
	
	private void computeHistogramX(double min, double max, double[] bins) {
		double width = (max - min) / bins.length;
		
		for (int bin = 0; bin < bins.length; bin++) {
			double x = min + width / 2 + bin * width;
			
			bins[bin] = x;
		}
	}
	
	private void computeHistogramY(List<Double> data, double min, double max, double[] bins) {
		for (int bin = 0; bin < bins.length; bin++) {
			bins[bin] = 0;
		}
		
		double width = max - min;
		
		for (double item : data) {
			double bin = (item - min) / width * bins.length;
			
			bins[(int) bin]++;
		}
		
		if (data.size() > 0) {
			for (int bin = 0; bin < bins.length; bin++) {
				bins[bin] /= data.size();
			}	
		}
	}
	
	private XYChart.Series<String, Number> createHistogramSeries(String name, double[] x, double[] y) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		
		series.setName(name);
		
		for (int bin = 0; bin < x.length; bin++) {
			series.getData().add(new XYChart.Data<String, Number>("" + Math.round(x[bin]), y[bin]));
		}
		
		return series;
	}

}
