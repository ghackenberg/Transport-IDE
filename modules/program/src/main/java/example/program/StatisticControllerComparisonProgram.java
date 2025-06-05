package example.program;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

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
import example.viewer.charts.single.VehicleBatteriesChartViewer;

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
			
			List<Viewer> modelViewers = new ArrayList<>();
			List<Viewer> chartViewers = new ArrayList<>();
			
			for (int processor = 0; processor < processorCount; processor++) {
				Model model = models.get(processor);
				
				ExampleStatistics stat = stats.get(processor);
				
				modelViewers.add(new ModelViewer(model, stat));
				
				chartViewers.add(new VehicleBatteriesChartViewer(model, stat));
			}
			
			// Create grid
			
			SplitDockGrid grid = new SplitDockGrid();
			
			for (int processor = 0; processor < processorCount; processor++) {
				Viewer modelViewer = modelViewers.get(processor);
				Viewer chartViewer = chartViewers.get(processor);
				
				Dockable modelDockable = new DefaultDockable(modelViewer.getComponent(), modelViewer.getName(), modelViewer.getIcon());
				Dockable chartDockable = new DefaultDockable(chartViewer.getComponent(), chartViewer.getName(), chartViewer.getIcon());
				
				grid.addDockable(processor, 0, 1, 1, modelDockable);
				grid.addDockable(processor, 1, 1, 1, chartDockable);
			}
			
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
					while (true) {	
						Model model;
						
						ExampleStatistics stat;
						
						Viewer modelViewer;
						Viewer chartViewer;
						
						Controller ctrl;
						
						String name;
						
						synchronized (models) {
							model = models.removeFirst();
							
							stat = stats.removeFirst();
							
							modelViewer = modelViewers.removeFirst();
							chartViewer = chartViewers.removeFirst();
							
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
						
						Simulator<ExampleStatistics> simulator = new Simulator<>(name, model, ctrl, stat, maxModelTimeStep, ratioModelRealTime, randomRunsFolder);
						
						simulator.setHandleUpdated(() -> {
							modelViewer.update();
							chartViewer.update();
						});
						
						simulator.loop();
						
						synchronized (models) {
							if (simulator.isFinished()) {
								if (ctrl instanceof RandomController) {
									randomFinishedTimes.add(model.time);
								} else if (ctrl instanceof GreedyController) {
									greedyFinishedTimes.add(model.time);
								} else if (ctrl instanceof SmartController) {
									smartFinishedTimes.add(model.time);
								}
							} else if (simulator.isEmpty()) {
								if (ctrl instanceof RandomController) {
									randomEmptyTimes.add(model.time);
								} else if (ctrl instanceof GreedyController) {
									greedyEmptyTimes.add(model.time);
								} else if (ctrl instanceof SmartController) {
									smartEmptyTimes.add(model.time);
								}
							} else if (Double.isInfinite(model.time)) {								
								if (ctrl instanceof RandomController) {
									randomInfiniteTimes.add(model.time);
								} else if (ctrl instanceof GreedyController) {
									greedyInfiniteTimes.add(model.time);
								} else if (ctrl instanceof SmartController) {
									smartInfiniteTimes.add(model.time);
								}
							} else {
								if (ctrl instanceof RandomController) {
									randomCollisionTimes.add(model.time);
								} else if (ctrl instanceof GreedyController) {
									greedyCollisionTimes.add(model.time);
								} else if (ctrl instanceof SmartController) {
									smartCollisionTimes.add(model.time);
								}
							}
							
							chartViewers.add(chartViewer);
							modelViewers.add(modelViewer);
							
							stats.add(stat);
							
							models.add(model);
							
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

}
