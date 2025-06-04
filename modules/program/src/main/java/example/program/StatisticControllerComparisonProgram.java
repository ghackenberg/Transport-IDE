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
import example.program.dialogs.ModelOpenDialog;
import example.program.exceptions.ArgumentsException;
import example.simulator.Simulator;
import example.statistics.implementations.ExampleStatistics;

public class StatisticControllerComparisonProgram {
	
	static final int demandCount = 10;
	
	static final double maxDemandSize = 1;
	static final double maxDemandTime = 1000000;
	static final double maxDemandDuration = 100000;
	
	static final int replicationCount = 4;
	
	static int randomReplicationCount = 0;
	static int greedyReplicationCount = 0;
	static int smartReplicationCount = 0;
	
	static List<Double> randomFinishedTimes = new ArrayList<>();
	static List<Double> greedyFinishedTimes = new ArrayList<>();
	static List<Double> smartFinishedTimes = new ArrayList<>();
	
	static List<Double> randomEmptyTimes = new ArrayList<>();
	static List<Double> greedyEmptyTimes = new ArrayList<>();
	static List<Double> smartEmptyTimes = new ArrayList<>();
	
	static List<Double> randomCollisionTimes = new ArrayList<>();
	static List<Double> greedyCollisionTimes = new ArrayList<>();
	static List<Double> smartCollisionTimes = new ArrayList<>();

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
			
			for (int processor = 0; processor < Runtime.getRuntime().availableProcessors(); processor++) {
				
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
			
			// Sort demands
			
			for (Model model : models) {
				model.demands.sort((first, second) -> (int) Math.signum(first.pickup.time - second.pickup.time));
			}
			
			// Define settings
			
			double maxModelTimeStep = Double.MAX_VALUE;
			double ratioModelRealTime = -1;
			
			// Start threads
			
			List<Thread> threads = new ArrayList<>();
			
			for (int processor = 0; processor < Runtime.getRuntime().availableProcessors(); processor++) {
				Thread thread = new Thread(() -> {
					while (true) {	
						Model model;
						
						Controller controller;
						
						String name;
						
						synchronized (models) {
							model = models.removeFirst();
							
							if (randomReplicationCount++ < replicationCount) {
								System.out.println("Random " + randomReplicationCount);
								controller = new RandomController();
								name = "Random-" + randomReplicationCount;
							} else if (greedyReplicationCount++ < replicationCount) {
								System.out.println("Greedy " + greedyReplicationCount);
								controller = new GreedyController(model);
								name = "Greedy-" + randomReplicationCount;
							} else if (smartReplicationCount++ < replicationCount) {
								System.out.println("Smart " + smartReplicationCount);
								controller = new SmartController(model);
								name = "Smart-" + randomReplicationCount;
							} else {
								return;
							}
						}
						
						ExampleStatistics statistics = new ExampleStatistics(model);
						
						Simulator<ExampleStatistics> simulator = new Simulator<>(name, model, controller, statistics, maxModelTimeStep, ratioModelRealTime, randomRunsFolder);
						
						simulator.loop();
						
						synchronized (models) {
							if (model.isFinished()) {
								if (controller instanceof RandomController) {
									randomFinishedTimes.add(model.time);
								} else if (controller instanceof GreedyController) {
									greedyFinishedTimes.add(model.time);
								} else if (controller instanceof SmartController) {
									smartFinishedTimes.add(model.time);
								}
							} else if (model.isEmpty()) {
								if (controller instanceof RandomController) {
									randomEmptyTimes.add(model.time);
								} else if (controller instanceof GreedyController) {
									greedyEmptyTimes.add(model.time);
								} else if (controller instanceof SmartController) {
									smartEmptyTimes.add(model.time);
								}
							} else {
								if (controller instanceof RandomController) {
									randomCollisionTimes.add(model.time);
								} else if (controller instanceof GreedyController) {
									greedyCollisionTimes.add(model.time);
								} else if (controller instanceof SmartController) {
									smartCollisionTimes.add(model.time);
								}
							}
							models.add(model);
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
