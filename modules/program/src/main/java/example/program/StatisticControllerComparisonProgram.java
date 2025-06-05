package example.program;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
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
import example.program.dialogs.ModelOpenDialog;
import example.program.exceptions.ArgumentsException;
import example.simulator.Simulator;
import example.statistics.implementations.ExampleStatistics;
import example.viewer.ModelViewer;
import example.viewer.Viewer;

public class StatisticControllerComparisonProgram {
	
	static final int processorCount = Runtime.getRuntime().availableProcessors() - 1;
	
	static final double maxModelTimeStep = Double.MAX_VALUE;
	static final double ratioModelRealTime = -1;
	
	static final int demandCount = 1000;
	
	static final double maxDemandSize = 1;
	static final double maxDemandTime = 1000000;
	static final double maxDemandDuration = 100000;
	
	static final int replicationCount = 1000;
	
	static final int totalCount = 3 * replicationCount;
	
	static int randomReplicationCount = 0;
	static int greedyReplicationCount = 0;
	static int smartReplicationCount = 0;
	
	static int finishedCount = 0;
	
	static final List<Double> randomFinishedTimes = new ArrayList<>();
	static final List<Double> greedyFinishedTimes = new ArrayList<>();
	static final List<Double> smartFinishedTimes = new ArrayList<>();
	
	static final List<Double> randomEmptyTimes = new ArrayList<>();
	static final List<Double> greedyEmptyTimes = new ArrayList<>();
	static final List<Double> smartEmptyTimes = new ArrayList<>();
	
	static final List<Double> randomCollisionTimes = new ArrayList<>();
	static final List<Double> greedyCollisionTimes = new ArrayList<>();
	static final List<Double> smartCollisionTimes = new ArrayList<>();
	
	static final List<Double> randomInfiniteTimes = new ArrayList<>();
	static final List<Double> greedyInfiniteTimes = new ArrayList<>();
	static final List<Double> smartInfiniteTimes = new ArrayList<>();
	
	static final int binCount = 30;

	static final double[] binData = new double[binCount];
	
	static final int[] randomData = new int[binCount];
	static final int[] greedyData = new int[binCount];
	static final int[] smartData = new int[binCount];

	public static void main(String[] args) {
		try {
			// Choose folder
		
			File modelFolder = ModelOpenDialog.choose();
			
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
			
			// Create viewers
			
			List<Viewer> viewers = new ArrayList<>();
			
			for (int processor = 0; processor < processorCount; processor++) {
				Model model = models.get(processor);
				
				ExampleStatistics stat = stats.get(processor);
				
				viewers.add(new ModelViewer(model, stat));
			}
			
			// Create datasets
			
			DefaultPieDataset randomDataset = new DefaultPieDataset();
			
			randomDataset.setValue("Finished", 0);
			randomDataset.setValue("Empty", 0);
			randomDataset.setValue("Collision", 0);
			randomDataset.setValue("Infinity", 0);
			
			DefaultPieDataset greedyDataset = new DefaultPieDataset();
			
			greedyDataset.setValue("Finished", 0);
			greedyDataset.setValue("Empty", 0);
			greedyDataset.setValue("Collision", 0);
			greedyDataset.setValue("Infinity", 0);
			
			DefaultPieDataset smartDataset = new DefaultPieDataset();
			
			smartDataset.setValue("Finished", 0);
			smartDataset.setValue("Empty", 0);
			smartDataset.setValue("Collision", 0);
			smartDataset.setValue("Infinity", 0);
			
			DefaultCategoryDataset histogramDataset = new DefaultCategoryDataset();
			
			// Create charts

			JFreeChart randomChart = ChartFactory.createPieChart("Random", randomDataset);
			JFreeChart greedyChart = ChartFactory.createPieChart("Greedy", greedyDataset);
			JFreeChart smartChart = ChartFactory.createPieChart("Smart", smartDataset);
			JFreeChart histogramChart = ChartFactory.createBarChart("Histogram", "Time", "Count", histogramDataset);
			
			// Create grid
			
			SplitDockGrid grid = new SplitDockGrid();
			
			for (int processor = 0; processor < processorCount; processor++) {
				Viewer viewer = viewers.get(processor);
				
				Dockable dockable = new DefaultDockable(viewer.getComponent(), viewer.getName(), viewer.getIcon());
				
				grid.addDockable(processor, 0, 1, 1, dockable);
			}
			
			grid.addDockable(0, 1, 2, 2, new DefaultDockable(new ChartPanel(randomChart), "Chart"));
			grid.addDockable(2, 1, 2, 2, new DefaultDockable(new ChartPanel(greedyChart), "Chart"));
			grid.addDockable(4, 1, 2, 2, new DefaultDockable(new ChartPanel(smartChart), "Chart"));
			grid.addDockable(6, 1, processorCount - 6, 2, new DefaultDockable(new ChartPanel(histogramChart), "Histogram"));
			
			// FIXME Remove work around for linux
			
			System.setProperty("java.version", "13.0.0");
			
			// Create station
			
			SplitDockStation station = new SplitDockStation();
			station.dropTree(grid.toTree());
			
			// Create controller
			
			new DockController().add(station);
			
			// Create progress bar
			
			JProgressBar progress = new JProgressBar(0, 3 * replicationCount);
			progress.setString("0 / " + totalCount + " Simulations");
			progress.setStringPainted(true);
			
			// Create panel
			
			JPanel border = new JPanel();
			border.setLayout(new BorderLayout(5, 5));
			border.add(progress, BorderLayout.NORTH);
			border.add(station.getComponent(), BorderLayout.CENTER);
			
			// Create frame
			
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(600, 600);
			frame.setResizable(true);
			frame.setTitle("Multiple Viewer");
			frame.setContentPane(border);
			frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			frame.setVisible(true);
			
			// Start threads
			
			List<Thread> threads = new ArrayList<>();
			
			for (int processor = 0; processor < processorCount; processor++) {
				Thread thread = new Thread(() -> {
					Model model;
					ExampleStatistics stat;
					Viewer viewer;
					Controller ctrl;
					String name;
					Simulator<ExampleStatistics> simulator;
					
					while (true) {
						synchronized (models) {
							model = models.removeFirst();
							stat = stats.removeFirst();
							viewer = viewers.removeFirst();
							
							if (randomReplicationCount++ < replicationCount) {
								System.out.println("Random " + randomReplicationCount);
								ctrl = new RandomController();
								name = "Random-" + randomReplicationCount;
							} else if (greedyReplicationCount++ < replicationCount) {
								System.out.println("Greedy " + greedyReplicationCount);
								ctrl = new GreedyController(model);
								name = "Greedy-" + greedyReplicationCount;
							} else if (smartReplicationCount++ < replicationCount) {
								System.out.println("Smart " + smartReplicationCount);
								ctrl = new SmartController(model);
								name = "Smart-" + smartReplicationCount;
							} else {
								return;
							}
						}
						
						simulator = new Simulator<>(name, model, ctrl, stat, maxModelTimeStep, ratioModelRealTime, randomRunsFolder);
						simulator.setHandleUpdated(viewer::update);
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
							
							viewers.add(viewer);
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
							
							histogramDataset.clear();
							
							updateHistogram(histogramDataset, "Random", binData, randomData);
							updateHistogram(histogramDataset, "Greedy", binData, greedyData);
							updateHistogram(histogramDataset, "Smart", binData, smartData);
							
							progress.setValue(++finishedCount);
							progress.setString(finishedCount + " / " + totalCount + " Simulations");
						}
					}
				});
				thread.start();
						
				threads.add(thread);
			}
			
			// Join threads
			
			for (Thread thread : threads) {
				thread.join();
			}
			
			System.out.println("Finished: " + randomFinishedTimes.size() + " / " + greedyFinishedTimes.size() + " / " + smartFinishedTimes.size());
			System.out.println("Empty: " + randomEmptyTimes.size() + " / " + greedyEmptyTimes.size() + " / " + smartEmptyTimes.size());
			System.out.println("Collision: " + randomCollisionTimes.size() + " / " + greedyCollisionTimes.size() + " / " + smartCollisionTimes.size());
			System.out.println("Infinite: " + randomInfiniteTimes.size() + " / " + greedyInfiniteTimes.size() + " / " + smartInfiniteTimes.size());
			
		} catch (MissingException e) {
			e.printStackTrace();
		} catch (DirectoryException e) {
			e.printStackTrace();
		} catch (ArgumentsException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
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
	
	private static void updatePieChart(DefaultPieDataset dataset, List<Double> finished, List<Double> empty, List<Double> collision, List<Double> infinite) {
		dataset.setValue("Finished", finished.size());
		dataset.setValue("Empty", empty.size());
		dataset.setValue("Collision", collision.size());
		dataset.setValue("Infinity", infinite.size());
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
	
	private static void computeHistogramY(List<Double> data, double min, double max, int[] bins) {
		for (int bin = 0; bin < bins.length; bin++) {
			bins[bin] = 0;
		}
		
		double width = max - min;
		
		for (double item : data) {
			double bin = (item - min) / width * bins.length;
			
			bins[(int) bin]++;
		}
	}
	
	private static void updateHistogram(DefaultCategoryDataset dataset, String name, double[] x, int[] y) {
		for (int bin = 0; bin < x.length; bin++) {
			dataset.addValue(y[bin], name, "" + Math.round(x[bin]));
		}
	}

}
