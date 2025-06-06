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
import example.parser.exceptions.DirectoryException;
import example.parser.exceptions.MissingException;
import example.program.exceptions.ArgumentsException;
import example.simulator.Simulator;
import example.statistics.implementations.ExampleStatistics;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class StatisticControllerComparisonProgram extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	final int processorCount = Runtime.getRuntime().availableProcessors() - 1;
	
	final double maxModelTimeStep = Double.MAX_VALUE;
	final double ratioModelRealTime = -1;
	
	final int demandCount = 1000;
	
	final double maxDemandSize = 1;
	final double maxDemandTime = 1000000;
	final double maxDemandDuration = 100000;
	
	final int replicationCount = 2000;
	
	final int totalCount = 3 * replicationCount;
	
	int randomReplicationCount = 0;
	int greedyReplicationCount = 0;
	int smartReplicationCount = 0;
	
	int finishedCount = 0;
	
	final List<Double> randomFinishedTimes = new ArrayList<>();
	final List<Double> greedyFinishedTimes = new ArrayList<>();
	final List<Double> smartFinishedTimes = new ArrayList<>();
	
	final List<Double> randomEmptyTimes = new ArrayList<>();
	final List<Double> greedyEmptyTimes = new ArrayList<>();
	final List<Double> smartEmptyTimes = new ArrayList<>();
	
	final List<Double> randomCollisionTimes = new ArrayList<>();
	final List<Double> greedyCollisionTimes = new ArrayList<>();
	final List<Double> smartCollisionTimes = new ArrayList<>();
	
	final List<Double> randomInfiniteTimes = new ArrayList<>();
	final List<Double> greedyInfiniteTimes = new ArrayList<>();
	final List<Double> smartInfiniteTimes = new ArrayList<>();
	
	final int binCount = 30;

	final double[] binData = new double[binCount];
	
	final double[] randomData = new double[binCount];
	final double[] greedyData = new double[binCount];
	final double[] smartData = new double[binCount];

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			// Choose folder
		
			File modelFolder = new DirectoryChooser().showDialog(primaryStage);
			
			if (modelFolder == null) {
				return;
			}
			
			File intersectionFile = new File(modelFolder, "intersections.txt");
			File segmentFile = new File(modelFolder, "segments.txt");
			File stationFile = new File(modelFolder, "stations.txt");
			File vehicleFile = new File(modelFolder, "vehicles.txt");
			File demandFile = new File(modelFolder, "demands.txt");
			
			// Select runs folder
			
			File runsFolder = new File(modelFolder, "runs");
			
			if (!runsFolder.exists())
				runsFolder.mkdir();
			else if (!runsFolder.isDirectory())
				throw new ArgumentsException("Path to model contains a runs file");
			
			// Select random runs folder
			
			File randomRunsFolder = new File(runsFolder, "random");
			
			if (!randomRunsFolder.exists())
				randomRunsFolder.mkdir();
			else if (!randomRunsFolder.isDirectory())
				throw new ArgumentsException("Path to model runs contains a random file");
			
			// Select greedy runs folder
			
			File greedyRunsFolder = new File(runsFolder, "greedy");
			
			if (!greedyRunsFolder.exists())
				greedyRunsFolder.mkdir();
			else if (!greedyRunsFolder.isDirectory())
				throw new ArgumentsException("Path to model runs contains a greedy file");
			
			// Select smart runs folder
			
			File smartRunsFolder = new File(runsFolder, "smart");
			
			if (!smartRunsFolder.exists())
				smartRunsFolder.mkdir();
			else if (!smartRunsFolder.isDirectory())
				throw new ArgumentsException("Path to model runs contains a smart file");
			
			// Create parser
			
			Parser parser = new Parser();
			
			// Prepare models
			
			List<Model> models = new ArrayList<>();
			
			for (int processor = 0; processor < processorCount; processor++) {
				
				// Parse models
				
				Model model = parser.parse(intersectionFile, segmentFile, stationFile, vehicleFile, demandFile);
				
				// Clear demands
				
				model.demands.clear();
				
				// Remember model
				
				models.add(model);
			}
			
			// Generate demands
			
			for (int index = 0; index < demandCount; index++) {
				// Select size
				
				double size = Math.random() * maxDemandSize;
				
				// Select times
				
				double pickupTime = Math.random() * maxDemandTime;
				double dropoffTime = pickupTime + Math.random() * maxDemandDuration;
				
				// Select segments
				
				int pickupSegmentNumber = (int) (Math.random() * models.get(0).segments.size());
				int dropoffSegmentNumber = (int) (Math.random() * models.get(0).segments.size());
				
				// Select distances
				
				double pickupDistance = Math.random();
				double dropoffDistance = Math.random();
				
				// Check segment validity
				
				boolean valid = true;
				
				for (Model model : models) {
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
				if (!valid) {
					index--;
					continue;
				}
				
				// Process models
				
				for (Model model : models) {
					Segment pickupSegment = model.segments.get(pickupSegmentNumber);
					Segment dropoffSegment = model.segments.get(dropoffSegmentNumber);
					
					Demand demand = new Demand(pickupSegment, pickupDistance * pickupSegment.getLength(), pickupTime, dropoffSegment, dropoffDistance * dropoffSegment.getLength(), dropoffTime, size);
	
					model.demands.add(demand);
				}
			}
			
			// Sort demands and reset model
			
			for (Model model : models) {
				model.demands.sort((first, second) -> (int) Math.signum(first.pickup.time - second.pickup.time));
				
				model.reset();
			}
			
			// Create statistics
			
			List<ExampleStatistics> stats = new ArrayList<>();
			
			for (int processor = 0; processor < processorCount; processor++) {
				Model model = models.get(processor);
				
				ExampleStatistics stat = new ExampleStatistics(model);
				
				stat.reset();
				
				stats.add(stat);
			}
			
			// Create series
			
			XYChart.Series<String, Number> randomSeries = new XYChart.Series<>();
			randomSeries.setName("Random control strategy");
			
			XYChart.Series<String, Number> greedySeries = new XYChart.Series<>();
			greedySeries.setName("Greedy control strategy");
			
			XYChart.Series<String, Number> smartSeries = new XYChart.Series<>();
			smartSeries.setName("Smart control strategy");
			
			// Create datasets
			
			ObservableList<PieChart.Data> randomDataset = createPieDataset();
			ObservableList<PieChart.Data> greedyDataset = createPieDataset();
			ObservableList<PieChart.Data> smartDataset = createPieDataset();
			
			// Create axes
			
			CategoryAxis timeAxis = new CategoryAxis();
			timeAxis.setLabel("Time");
			
			NumberAxis frequencyAxis = new NumberAxis();
			frequencyAxis.setLabel("Relative frequency");
			
			// Create charts
			
			PieChart randomChart = new PieChart(randomDataset);
			randomChart.setTitle("Random control strategy");
			randomChart.setAnimated(false);
			
			PieChart greedyChart = new PieChart(greedyDataset);
			greedyChart.setTitle("Greedy control strategy");
			greedyChart.setAnimated(false);
			
			PieChart smartChart = new PieChart(smartDataset);
			smartChart.setTitle("Smart control strategy");
			smartChart.setAnimated(false);
			
			BarChart<String, Number> timeChart = new BarChart<>(timeAxis, frequencyAxis);
			timeChart.setTitle("Distribution of stop time in finished state");
			timeChart.setAnimated(false);
			timeChart.getData().add(randomSeries);
			timeChart.getData().add(greedySeries);
			timeChart.getData().add(smartSeries);
			
			// Create progress
			
			ProgressBar progress = new ProgressBar();
			
			// Start threads
			
			List<Thread> threads = new ArrayList<>();
			
			for (int processor = 0; processor < processorCount; processor++) {
				Thread thread = new Thread(() -> {
					Model model;
					ExampleStatistics stat;
					Controller ctrl;
					String name;
					Simulator<ExampleStatistics> simulator;
					
					while (true) {
						synchronized (models) {
							model = models.removeFirst();
							stat = stats.removeFirst();
							
							if (randomReplicationCount++ < replicationCount) {
								ctrl = new RandomController();
								name = "Random-" + randomReplicationCount;
							} else if (greedyReplicationCount++ < replicationCount) {
								ctrl = new GreedyController(model);
								name = "Greedy-" + greedyReplicationCount;
							} else if (smartReplicationCount++ < replicationCount) {
								ctrl = new SmartController(model);
								name = "Smart-" + smartReplicationCount;
							} else {
								return;
							}
						}
						
						simulator = new Simulator<>(name, model, ctrl, stat, maxModelTimeStep, ratioModelRealTime, randomRunsFolder);
						simulator.loop();
						
						synchronized (models) {
							if (simulator.isFinished()) {
								chooseList(ctrl, randomFinishedTimes, greedyFinishedTimes, smartFinishedTimes).add(model.time);
							} else if (simulator.isEmpty()) {
								chooseList(ctrl, randomEmptyTimes, greedyEmptyTimes, smartEmptyTimes).add(model.time);
							} else if (Double.isInfinite(model.time)) {
								chooseList(ctrl, randomInfiniteTimes, greedyInfiniteTimes, smartInfiniteTimes).add(model.time);
							} else {
								chooseList(ctrl, randomCollisionTimes, greedyCollisionTimes, smartCollisionTimes).add(model.time);
							}
							
							stats.add(stat);
							models.add(model);
							
							updatePieChart(randomDataset, randomFinishedTimes, randomEmptyTimes, randomCollisionTimes, randomInfiniteTimes);
							updatePieChart(greedyDataset, greedyFinishedTimes, greedyEmptyTimes, greedyCollisionTimes, greedyInfiniteTimes);
							updatePieChart(smartDataset, smartFinishedTimes, smartEmptyTimes, smartCollisionTimes, smartInfiniteTimes);
							
							double min = Math.min(Math.min(computeMin(randomFinishedTimes), computeMin(greedyFinishedTimes)), computeMin(smartFinishedTimes)) - 1;
							double max = Math.max(Math.max(computeMax(randomFinishedTimes), computeMax(greedyFinishedTimes)), computeMax(smartFinishedTimes)) + 1;
							
							computeHistogramX(min, max, binData);
							
							computeHistogramY(randomFinishedTimes, min, max, randomData);
							computeHistogramY(greedyFinishedTimes, min, max, greedyData);
							computeHistogramY(smartFinishedTimes, min, max, smartData);
							
							XYChart.Series<String, Number> randomSeriesNew = createHistogramSeries("Random control strategy", binData, randomData);
							XYChart.Series<String, Number> greedySeriesNew = createHistogramSeries("Greedy control strategy", binData, greedyData);
							XYChart.Series<String, Number> smartSeriesNew = createHistogramSeries("Smart control strategy", binData, smartData);
							
							Platform.runLater(() -> {
								timeChart.getData().clear();
								
								timeChart.getData().add(randomSeriesNew);
								timeChart.getData().add(greedySeriesNew);
								timeChart.getData().add(smartSeriesNew);
								
								progress.setProgress(++finishedCount / (double) totalCount);
							});
						}
					}
				});
				thread.start();
						
				threads.add(thread);
			}
			
			// Create window
			
			ToolBar tool = new ToolBar(new Label("Progress:"), progress);
			
			GridPane grid = new GridPane();
			
			grid.getColumnConstraints().add(new ColumnConstraints());
			grid.getColumnConstraints().add(new ColumnConstraints());
			grid.getColumnConstraints().add(new ColumnConstraints());
			
			grid.getColumnConstraints().get(0).setHgrow(Priority.ALWAYS);
			grid.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);
			grid.getColumnConstraints().get(2).setHgrow(Priority.ALWAYS);
			
			grid.getRowConstraints().add(new RowConstraints());
			grid.getRowConstraints().add(new RowConstraints());
			
			grid.getRowConstraints().get(0).setVgrow(Priority.ALWAYS);
			grid.getRowConstraints().get(1).setVgrow(Priority.ALWAYS);
			
			grid.add(progress, 0, 0, 3, 1);
			grid.add(randomChart, 0, 1);
			grid.add(greedyChart, 1, 1);
			grid.add(smartChart, 2, 1);
			grid.add(timeChart, 0, 2, 3, 1);
			
			ToolBar status = new ToolBar(new Label("(c) 2025 Dr. Georg Hackenberg, Professor for Industrial Informatics, FH Upper Austria."));
			
			BorderPane border = new BorderPane();
			border.setTop(tool);
			border.setCenter(grid);
			border.setBottom(status);
			
			Scene scene = new Scene(border, 640, 480);
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("Transport-IDE Monte-Carlo Experiment");
			primaryStage.show();
			
		} catch (MissingException e) {
			e.printStackTrace();
		} catch (DirectoryException e) {
			e.printStackTrace();
		} catch (ArgumentsException e) {
			e.printStackTrace();
		}
	}
	
	private static List<Double> chooseList(Controller ctrl, List<Double> random, List<Double> greedy, List<Double> smart) {				
		if (ctrl instanceof RandomController) {
			return random;
		} else if (ctrl instanceof GreedyController) {
			return greedy;
		} else if (ctrl instanceof SmartController) {
			return smart;
		} else {
			throw new IllegalStateException();
		}
	}
	
	private static ObservableList<PieChart.Data> createPieDataset() {
		return FXCollections.observableArrayList(
			new PieChart.Data("Finished", 0),
			new PieChart.Data("Empty", 0),
			new PieChart.Data("Collision", 0),
			new PieChart.Data("Infinite", 0)
		);
	}
	
	private static void updatePieChart(ObservableList<PieChart.Data> dataset, List<Double> finished, List<Double> empty, List<Double> collision, List<Double> infinite) {
		Platform.runLater(() -> {
			dataset.get(0).setPieValue(finished.size());
			dataset.get(1).setPieValue(empty.size());
			dataset.get(2).setPieValue(collision.size());
			dataset.get(3).setPieValue(infinite.size());
		});
	}
	
	private static double computeMin(List<Double> data) {
		double min = Double.MAX_VALUE;
		
		for (double time : data) {
			min = Math.min(time, min);
		}
		
		return min;
	}
	
	private static double computeMax(List<Double> data) {
		double max = 0;
		
		for (double time : data) {
			max = Math.max(time, max);
		}
		
		return max;
	}
	
	private static void computeHistogramX(double min, double max, double[] bins) {
		double width = (max - min) / bins.length;
		
		for (int bin = 0; bin < bins.length; bin++) {
			double x = min + width / 2 + bin * width;
			
			bins[bin] = x;
		}
	}
	
	private static void computeHistogramY(List<Double> data, double min, double max, double[] bins) {
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
	
	private static XYChart.Series<String, Number> createHistogramSeries(String name, double[] x, double[] y) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		
		series.setName(name);
		
		for (int bin = 0; bin < x.length; bin++) {
			series.getData().add(new XYChart.Data<String, Number>("" + Math.round(x[bin]), y[bin]));
		}
		
		return series;
	}

}
