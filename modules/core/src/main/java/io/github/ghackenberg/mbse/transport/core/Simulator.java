package io.github.ghackenberg.mbse.transport.core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.exceptions.CollisionException;
import io.github.ghackenberg.mbse.transport.core.exceptions.EmptyException;
import io.github.ghackenberg.mbse.transport.core.exceptions.InfinityException;
import io.github.ghackenberg.mbse.transport.core.exceptions.InvalidException;
import io.github.ghackenberg.mbse.transport.core.exceptions.InvalidRouteException;
import io.github.ghackenberg.mbse.transport.core.exceptions.InvalidSpeedException;
import io.github.ghackenberg.mbse.transport.core.exceptions.InvalidTimeoutException;

public class Simulator {
	
	private static boolean DEBUG = false;
	
	private static int COUNT = 0;
	
	private final int number = COUNT++;
	
	private boolean stop;
	private boolean pause;
	private boolean step;
	
	private String name;
	private Model model;
	private Controller controller;
	private Statistics statistics;
	private double maxModelTimeStep;
	private double ratioModelRealTime;
	
	private File runsFolder;
	private File runFolder;
	
	private Synchronizer synchronizer;
	
	private Map<Demand, Map<Double, Set<Vehicle>>> declines = new HashMap<>();
	
	public interface Handler {
		void handle(Exception exception);
	}
	public interface Runner {
		void run() throws InterruptedException;
	}
	
	private Runner handleUpdated;
	private Runner handleFinished;
	private Runner handleStopped;
	private Handler handleException;
	
	private Thread thread;
	
	public Simulator(String name, Model model, Controller controller, Statistics statistics, double maxModelTimeStep, double ratioModelRealTime, File runsFolder) {
		this(name, model, controller, statistics, maxModelTimeStep, ratioModelRealTime, runsFolder, new Synchronizer(1));
	}
	
	public Simulator(String name, Model model, Controller controller, Statistics statistics, double maxModelTimeStep, double ratioModelRealTime, File runsFolder, Synchronizer synchronizer) {
		this.name = name;
		this.model = model;
		this.controller = controller;
		this.statistics = statistics;
		this.maxModelTimeStep = maxModelTimeStep;
		this.ratioModelRealTime = ratioModelRealTime;
		this.runsFolder = runsFolder;
		this.synchronizer = synchronizer;
	}
	
	public String getName() {
		return name;
	}
	
	public Model getModel() {
		return model;
	}
	
	public Statistics getStatistics() {
		return statistics;
	}
	
	public double getMaxModelTimeStep() {
		return maxModelTimeStep;
	}
	
	public double getRatioModelRealTime() {
		return ratioModelRealTime;
	}
	
	public synchronized void setMaxModelTimeStep(double value) {
		maxModelTimeStep = value;
	}
	
	public synchronized void setRatioModelRealTime(double value) {
		ratioModelRealTime = value;
	}
	
	public void setHandleUpdated(Runner value) {
		handleUpdated = value;
	}
	
	public void setHandleFinished(Runner value) {
		handleFinished = value;
	}
	
	public void setHandleStopped(Runner value) {
		handleStopped = value;
	}
	
	public void setHandleException(Handler value) {
		handleException = value;
	}
	
	public File getRunsFolder() {
		return runsFolder;
	}
	
	public File getRunFolder() {
		return runFolder;
	}
	
	public void start() {
		if (DEBUG) {
			System.out.println("Simulator[" + name + "].start");
		}
		
		thread = new Thread(this::loop);
		thread.setName("Simulator " + number);
		thread.start();
	}
	
	public void stop() {
		try {
			if (DEBUG) {
				System.out.println("Simulator[" + name + "].stop");
			}
			
			stop = true;
			
			synchronized (this) {
				notify();
			}
			
			thread.join();
			thread = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void pause() {
		if (DEBUG) {
			System.out.println("Simulator[" + name + "].pause");
		}
		
		pause = true;
	}
	
	public void resume() {
		if (DEBUG) {
			System.out.println("Simulator[" + name + "].resume");
		}
		
		pause = false;
		
		synchronized (this) {
			notify();
		}
	}
	
	public void join() {
		try {
			if (DEBUG) {
				System.out.println("Simulator[" + name + "].join");
			}
			
			thread.join();
			thread = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void step() {
		if (DEBUG) {
			System.out.println("Simulator[" + name + "].step");
		}
		
		step = true;
		
		synchronized (this) {
			notify();
		}
	}
	
	public void loop() {
		if (DEBUG) {
			System.out.println("Simulator[" + name + "].loop");
		}
		
		model.reset();
		controller.reset();
		statistics.reset();
		
		declines.clear();
		model.demands.forEach(demand -> {
			declines.put(demand, new HashMap<>());
		});
		
		step = false;
		stop = false;
		pause = false;
		
		Date date = Calendar.getInstance().getTime();
		String name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
		
		runFolder = new File(runsFolder, name);
		runFolder.mkdir();
		
		synchronizer.start();
		
		try {
			updateCollisions();
			if (handleUpdated != null) {
				handleUpdated.run();
			}
			while (!stop && !isFinished()) {
				update();
				if (handleUpdated != null) {
					handleUpdated.run();
				}
			}
			if (isFinished()) {
				if (handleFinished != null) {
					handleFinished.run();
				}
			} else {
				if (handleStopped != null) {
					handleStopped.run();
				}
			}
		} catch (Exception e) {
			if (handleException != null) {
				handleException.handle(e);
			}
		}
		
		synchronizer.finish();
	}
	
	private synchronized void update() throws InterruptedException, InvalidException {
		
		//System.out.println("Simulator.step");
		
		while (!stop && pause && !step) {
			wait();
		}
		
		step = false;
		
		if (stop) {
			return;
		}
		
		// Remember real time before calculation
		final double realTimeBefore = System.currentTimeMillis();
		// Remember model time before calculation
		final double modelTimeBefore = model.time;
		
		// Perform calculation (and advance simulation time)
		calculate();
		
		// Remember real time after calculation
		final double realTimeAfter = System.currentTimeMillis();
		// Remember model time after calculation
		final double modelTimeAfter = model.time;
		
		// Calculate actual time advance
		final double realTimeDelta = realTimeAfter - realTimeBefore;
		// Calculate simulation time advance
		final double modelTimeDelta = modelTimeAfter - modelTimeBefore;
		
		// Calculate difference between simulation time advance and actual time advance 
		final double difference = modelTimeDelta - realTimeDelta;
		
		// Debug
		/*
		if (modelTimeDelta == 0) {
			System.out.println("Model time delta is null");
		}
		*/
		
		// Sleep for the difference in time advances
		if (!pause && ratioModelRealTime != -1) {
			Thread.sleep(Math.max((long) (difference / ratioModelRealTime), 0));
		}
		
	}
	
	private void calculate() throws InvalidException, InterruptedException {
		
		//System.out.println("Simulator.calculate");
		
		// Initialize model time step
		double modelTimeStep = maxModelTimeStep;
		
		// Update vehicle segment
		for (Vehicle vehicle : model.vehicles) {
			// Check location distance
			if (equalWithPrecision(vehicle.location.distance, vehicle.location.segment.getLength())) {
				// Remember segment
				Segment previous = vehicle.location.segment;
				// Select segment
				Segment next = controller.selectSegment(vehicle);
				// Check segment
				if (!previous.end.outgoing.contains(next)) {
					throw new InvalidRouteException(vehicle, previous, next);
				}
				// Update segment
				vehicle.location.segment = next;
				// Reset segment distance
				vehicle.location.distance = 0;
				// Update speed
				vehicle.speed = controller.selectSpeed(vehicle);
				// Update statistics
				statistics.recordCrossing(vehicle, previous, next, model.time);
				statistics.recordSpeed(vehicle, vehicle.speed, model.time);
				// Update model time step
				modelTimeStep = 0;
			}
		}
		
		// Unassign station
		for (Vehicle vehicle : model.vehicles) {
			// Check vehicle station
			if (vehicle.station != null) {
				// Check battery level
				if (equalWithPrecision(vehicle.batteryLevel, vehicle.batteryCapacity) || controller.unselectStation(vehicle)) {
					// Remember station
					Station station = vehicle.station;
					// Unassign vehicle
					station.vehicle = null;
					// Unassign station
					vehicle.station = null;
					// Update statistics
					statistics.recordChargeEnd(vehicle, station, modelTimeStep);
				}
			}
		}
		
		// Assign station
		for (Vehicle vehicle : model.vehicles) {
			// Check vehicle station
			if (vehicle.station == null && smallerWithPrecision(vehicle.batteryLevel, vehicle.batteryCapacity)) {
				// Process stations
				for (Station station : model.stations) {
					// Check station
					if (station.vehicle == null) {
						// Check segment
						if (vehicle.location.segment == station.location.segment) {
							// Check distance
							if (equalWithPrecision(vehicle.location.distance, station.location.distance)) {
								// Ask controller
								if (controller.selectStation(vehicle, station)) {
									// Assign station
									vehicle.station = station;
									// Assign vehicle
									station.vehicle = vehicle;
									// Update statistics
									statistics.recordChargeStart(vehicle, station, modelTimeStep);
								}
							}
						}
					}
				}
			}
		}
		
		// Pickup demands
		for (Demand demand : model.demands) {
			// Check demand relevance
			if (demand.done == false && demand.vehicle == null && !smallerWithPrecision(model.time, demand.pickup.time)) {
				// Process vehicles
				for (Vehicle vehicle : model.vehicles) {
					// Check vehicle
					if (greaterWithPrecision(vehicle.batteryLevel, 0) && vehicle.station == null) {
						// Compare segment
						if (demand.pickup.location.segment == vehicle.location.segment) {
							// Compare distance on segment
							if (equalWithPrecision(demand.pickup.location.distance, vehicle.location.distance)) {
								// Compare load vs. capacity
								if (!greaterWithPrecision(vehicle.loadLevel + demand.size, vehicle.loadCapacity)) {
									// Declined before?
									if (!declines.get(demand).containsKey(model.time) || !declines.get(demand).get(model.time).contains(vehicle)) {
										// Ask controller for pickup decision
										if (controller.selectDemand(vehicle, demand)) {
											// Update demand
											demand.vehicle = vehicle;
											// Update vehicle
											vehicle.loadLevel += demand.size;
											vehicle.demands.add(demand);
											// Update statistics
											statistics.recordPickupAccept(vehicle, demand, model.time);
											// Update model time step
											modelTimeStep = 0;
										} else {
											// Check time
											if (!declines.get(demand).containsKey(model.time)) {
												// Add set
												declines.get(demand).put(model.time, new HashSet<>());
											}
											// Update declines
											declines.get(demand).get(model.time).add(vehicle);
											// Update statistics
											statistics.recordPickupDecline(vehicle, demand, model.time);
											// Update model time step
											modelTimeStep = 0;
										}
									} else {
										if (DEBUG) {
											System.out.println("Simulator[" + name + "].calculate - Already declined!");
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		// Dropoff demands
		for (Vehicle vehicle : model.vehicles) {
			// Process demands
			for (int index = 0; index < vehicle.demands.size(); index++) {
				// Obtain demand
				Demand demand = vehicle.demands.get(index);
				// Compare segment
				if (demand.dropoff.location.segment == vehicle.location.segment) {
					// Compare distance on segment
					if (equalWithPrecision(demand.dropoff.location.distance, vehicle.location.distance)) {
						// Update demand
						demand.done = true;
						demand.vehicle = null;
						// Update vehicle
						vehicle.loadLevel -= demand.size;
						vehicle.demands.remove(index--);
						// Update statistics
						statistics.recordDropoff(vehicle, demand, model.time);
						// Update model time step
						modelTimeStep = 0;
					}
				}
			}
		}
		
		// Dropoff demands (empty battery vehicles)
		for (Vehicle vehicle : model.vehicles) {
			if (equalWithPrecision(vehicle.batteryLevel, 0)) {
				while (vehicle.demands.size() > 0) {
					Demand demand = vehicle.demands.remove(0);
					
					demand.vehicle = null;
					
					demand.pickup.location.segment = vehicle.location.segment;
					demand.pickup.location.distance = vehicle.location.distance;
					
					vehicle.loadLevel -= demand.size;
					
					modelTimeStep = 0;
				}
			}
		}
		
		// Update vehicle speed
		for (Vehicle vehicle : model.vehicles) {
			// Initialize speed
			double speed = 0;
			// Select speed
			if (greaterWithPrecision(vehicle.batteryLevel, 0) && vehicle.station == null) {
				speed = controller.selectSpeed(vehicle);
			}
			// Check speed
			if (greaterWithPrecision(speed, vehicle.location.segment.speed)) {
				throw new InvalidSpeedException(vehicle, speed);
			}
			// Update speed
			vehicle.speed = speed;
			// Update statistics
			statistics.recordSpeed(vehicle, speed, model.time);
		}
		
		// Duration until speed selection
		for (Vehicle vehicle : model.vehicles) {
			// Check battery level
			if (greaterWithPrecision(vehicle.batteryLevel, 0) && vehicle.station == null) {
				// Select timeout
				double timeout = controller.selectMaximumSpeedSelectionTimeout(vehicle);
				// Check timeout
				if (timeout < 0) {
					throw new InvalidTimeoutException(vehicle, timeout);
				}
				// Calculate duration
				modelTimeStep = Math.min(modelTimeStep, timeout);
			}
		}
		
		// Duration until station selection
		for (Vehicle vehicle : model.vehicles) {
			// Check vehicle station
			if (vehicle.station != null) {
				// Select timeout
				double timeout = controller.selectMaximumStationSelectionTimeout(vehicle);
				// Check timeout
				if (timeout < 0) {
					throw new InvalidTimeoutException(vehicle, timeout);
				}
				// Calculate duration
				modelTimeStep = Math.min(modelTimeStep, timeout);
			}
		}
		
		// Duration until battery level exhaustion
		for (Vehicle vehicle : model.vehicles) {
			// Check speed
			if (greaterWithPrecision(vehicle.speed, 0)) {
				// Speed in meter per millisecond
				double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
				// Duration in milliseconds
				double duration = vehicle.batteryLevel / speed;
				// Update model time step
				modelTimeStep = Math.min(modelTimeStep, duration);
			}
		}
		
		// Duration until segment end
		for (Vehicle vehicle : model.vehicles) {
			// Check speed
			if (greaterWithPrecision(vehicle.speed, 0)) {
				// Speed in meter per millisecond
				double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
				// Delta in meter
				double delta = vehicle.location.segment.getLength() - vehicle.location.distance;
				// Duration in milliseconds
				double duration = delta / speed;
				// Update model time step
				modelTimeStep = Math.min(modelTimeStep, duration);
			}
		}
		
		// Duration until demand appearance
		for (Demand demand : model.demands) {
			if (greaterWithPrecision(demand.pickup.time, model.time)) {
				modelTimeStep = Math.min(modelTimeStep, demand.pickup.time - model.time);
			}
		}
		
		// Duration until demand overdue
		for (Demand demand : model.demands) {
			if (demand.done == false && greaterWithPrecision(demand.dropoff.time, model.time)) {
				modelTimeStep = Math.min(modelTimeStep, demand.dropoff.time - model.time);
			}
		}
		
		// Duration until station
		for (Vehicle vehicle : model.vehicles) {
			// Check speed
			if (greaterWithPrecision(vehicle.speed, 0)) {
				// Process stations
				for (Station station : model.stations) {
					// Compare segments
					if (vehicle.location.segment == station.location.segment) {
						// Compare distances
						if (smallerWithPrecision(vehicle.location.distance, station.location.distance)) {
							// Speed in meter per millisecond
							double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
							// Delta in meter
							double delta = station.location.distance - vehicle.location.distance;
							// Duration in milliseconds
							double duration = delta / speed;
							// Update model time step;
							modelTimeStep = Math.min(modelTimeStep, duration);
						}
					}
				}
			}
		}
		
		// Duration until battery full
		for (Vehicle vehicle : model.vehicles) {
			// Check vehicle station
			if (vehicle.station != null) {
				// Speed in meter per millisecond
				double speed = vehicle.station.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
				// Delta in meter
				double delta = vehicle.batteryCapacity - vehicle.batteryLevel;
				// Duration in milliseconds
				double duration = delta / speed;
				// Update model time step
				modelTimeStep = Math.min(modelTimeStep, duration);
			}
		}
		
		// Duration until demand pickup
		for (Demand demand : model.demands) {
			// Check demand relevance
			if (demand.done == false && demand.vehicle == null && !greaterWithPrecision(demand.pickup.time, model.time)) {
				// Process vehicles
				for (Vehicle vehicle : model.vehicles) {
					// Check speed
					if (greaterWithPrecision(vehicle.speed, 0)) {
						// Pickup on same segment
						if (demand.pickup.location.segment == vehicle.location.segment) {
							// Pickup ahead
							if (greaterWithPrecision(demand.pickup.location.distance, vehicle.location.distance)) {
								// Enough capactiy?
								if (!greaterWithPrecision(demand.size, vehicle.loadCapacity - vehicle.loadLevel)) {
									// Speed in meter per millisecond
									double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
									// Delta in meter
									double delta = demand.pickup.location.distance - vehicle.location.distance;
									// Duration in milliseconds
									double duration = delta / speed;
									// Update model time step;
									modelTimeStep = Math.min(modelTimeStep, duration);
								}
							}
						}
					}
				}
			}
		}
		
		// Duration until demand dropoff
		for (Vehicle vehicle : model.vehicles) {
			// Check speed
			if (greaterWithPrecision(vehicle.speed, 0)) {
				// Process demands
				for (Demand demand : vehicle.demands) {
					// Dropoff on same segment
					if (vehicle.location.segment == demand.dropoff.location.segment) {
						// Dropoff ahead
						if (smallerWithPrecision(vehicle.location.distance, demand.dropoff.location.distance)) {
							// Speed in meter per millisecond
							double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
							// Delta in meter
							double delta = demand.dropoff.location.distance - vehicle.location.distance;
							// Duration in milliseconds
							double duration = delta / speed;
							// Update model time step;
							modelTimeStep = Math.min(modelTimeStep, duration);
						}
					}
				}
			}
		}
		
		// Duration until vehicle attach/detach
		for (int i = 0; i < model.vehicles.size(); i++) {
			Vehicle outer = model.vehicles.get(i);
			
			if (greaterWithPrecision(outer.speed, 0)) {
			
				double outerBack = outer.location.distance - outer.length / 2;
				double outerFront = outer.location.distance + outer.length / 2;
				
				for (int j = i + 1; j < model.vehicles.size(); j++) {
					Vehicle inner = model.vehicles.get(j);
					
					if (greaterWithPrecision(inner.speed, 0)) {
					
						double innerBack = inner.location.distance - inner.length / 2;
						double innerFront = inner.location.distance + inner.length / 2;
						
						// On same segment?
						if (outer.location.segment == inner.location.segment) {	
							if (smallerWithPrecision(inner.speed, outer.speed)) {
								double speed = (outer.speed - inner.speed) * 1000.0 / 60.0 / 60.0 / 1000.0;
								
								// Attach
								if (smallerWithPrecision(outerFront, innerBack)) {
									double distance = innerBack - outerFront;
									double duration = distance / speed;
									modelTimeStep = Math.min(modelTimeStep, duration);
								}
								
								// Detach
								if (smallerWithPrecision(outerBack, innerFront)) {
									double distance = innerFront - outerBack;
									double duration = distance / speed;
									modelTimeStep = Math.min(modelTimeStep, duration);
								}
							} else if (smallerWithPrecision(outer.speed, inner.speed)) {
								double speed = (inner.speed - outer.speed) * 1000.0 / 60.0 / 60.0 / 1000.0;
								
								// Attach
								if (smallerWithPrecision(innerFront, outerBack)) {
									double distance = outerBack - innerFront;
									double duration = distance / speed;
									modelTimeStep = Math.min(modelTimeStep, duration);
								}
								
								// Detach
								if (smallerWithPrecision(innerBack, outerFront)) {
									double distance = outerFront - innerBack;
									double duration = distance / speed;
									modelTimeStep = Math.min(modelTimeStep, duration);
								}
							}
						}
					}
				}
			}
		}
		
		// Synchronize simulators
		modelTimeStep = synchronizer.vote(modelTimeStep);
		
		// Update statistics
		statistics.recordStep(modelTimeStep, model.time);
		
		// Update vehicle state
		for (Vehicle vehicle : model.vehicles) {
			// Speed in meter per millisecond
			double speed = vehicle.speed * 1000.0 / 60.0 / 60.0 / 1000.0;
			// Delta in meter
			double delta = speed * modelTimeStep;
			// Decrease battery level
			vehicle.batteryLevel -= delta;
			// Check vehicle station
			if (vehicle.station != null) {
				// Increase battery level
				vehicle.batteryLevel += vehicle.station.speed * 1000.0 / 60.0 / 60.0 / 1000.0 * modelTimeStep;
			}
			// Update distance
			vehicle.location.distance += delta;
			// Update statistics
			statistics.recordDistance(vehicle, delta, model.time);
		}
		
		// Update model time
		model.time += modelTimeStep;
		
		// Update collisions
		updateCollisions();
		
		// Throw infinity exception
		if (Double.isInfinite(model.time)) {
			throw new InfinityException();
		}
		
		// Throw empty exception
		if (isEmpty()) {
			throw new EmptyException();
		}
	}
	
	private void updateCollisions() throws CollisionException {
		Map<Segment, List<Vehicle>> map = new HashMap<>();
		// Initialize map
		model.segments.forEach(segment -> {
			map.put(segment, new ArrayList<>());
		});
		model.vehicles.forEach(vehicle -> {
			map.get(vehicle.location.segment).add(vehicle);
		});
		model.segments.forEach(segment -> {
			map.get(segment).sort((first, second) -> {
				if (!equalWithPrecision(first.speed, second.speed)) {
					return (int) Math.signum(first.speed - second.speed);
				} else {
					if (!equalWithPrecision(first.location.distance, second.location.distance)) {
						return (int) Math.signum(first.location.distance - second.location.distance);
					} else {
						return first.name.compareTo(second.name);
					}
				}
			});
		});
		// Clear vehicles
		model.vehicles.forEach(vehicle -> {
			// Clear collisions
			vehicle.collisions.clear();
			vehicle.collisions.add(vehicle);
			// Clear lane
			vehicle.lane = -1;
		});
		// Clear segments
		model.segments.forEach(segment -> {
			// Clear load
			segment.load = 0;
			// Clear collisions
			segment.collisions = null;
		});
		// Collect collisions
		for (int i = 0; i < model.vehicles.size(); i++) {
			Vehicle outer = model.vehicles.get(i);
			// Calculate front/back
			double outerBack = outer.location.distance - outer.length / 2;
			double outerFront = outer.location.distance + outer.length / 2;
			// Compare each other vehicle
			for (int j = i + 1; j < model.vehicles.size(); j++) {
				Vehicle inner = model.vehicles.get(j);
				// Calculate front/back
				double innerBack = inner.location.distance - inner.length / 2;
				double innerFront = inner.location.distance + inner.length / 2;
				// On same segment?
				if (outer.location.segment == inner.location.segment) {
					// Outer faster
					if (smallerWithPrecision(inner.speed, outer.speed)) {
						if (smallerWithPrecision(outerBack, innerFront) && !smallerWithPrecision(outerFront, innerBack)) {
							outer.collisions.add(inner);
							inner.collisions.add(outer);
						}
					}
					// Inner faster
					if (smallerWithPrecision(outer.speed, inner.speed)) {
						if (smallerWithPrecision(innerBack, outerFront) && !smallerWithPrecision(innerFront, outerBack)) {
							outer.collisions.add(inner);
							inner.collisions.add(outer);
						}
					}
					// Same speed
					if (equalWithPrecision(outer.speed, inner.speed)) {
						if (smallerWithPrecision(outerBack, innerFront) && greaterWithPrecision(outerFront, innerBack)) {
							outer.collisions.add(inner);
							inner.collisions.add(outer);
						}
						if (smallerWithPrecision(innerBack, outerFront) && greaterWithPrecision(innerFront, outerBack)) {
							outer.collisions.add(inner);
							inner.collisions.add(outer);
						}
					}
				}
			}
		}
		// Update vehicles
		map.entrySet().forEach(entry -> {
			entry.getValue().forEach(outer-> {
				for (int lane = 0; lane < entry.getValue().size(); lane++) {
					boolean free = true;
					for (Vehicle inner : outer.collisions) {
						if (inner.lane == lane) {
							free = false;
							break;
						}
					}
					if (free) {
						outer.lane = lane;
						break;
					}
				}
			});
		});
		// Update segments
		model.vehicles.forEach(vehicle -> {
			if (vehicle.location.segment.load < vehicle.lane + 1) {
				vehicle.location.segment.load = vehicle.lane + 1;
				vehicle.location.segment.collisions = vehicle.collisions;
			}
		});
		// Throw exceptions
		for (Segment segment : model.segments) {
			if (segment.load > segment.lanes) {
				throw new CollisionException(segment, segment.collisions);
			}
		}
	}
	
	public boolean isFinished() {
		for (Demand demand : model.demands) {
			if (!demand.done) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmpty() {
		for (Vehicle vehicle : model.vehicles) {
			if (greaterWithPrecision(vehicle.batteryLevel, 0)) {
				return false;
			} else if (vehicle.station != null) {
				return false;
			} else {
				for (Station station : model.stations) {
					if (vehicle.location.segment == station.location.segment) {
						if (equalWithPrecision(vehicle.location.distance, station.location.distance)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	static private double EPSILON = Math.pow(10, -6); 
	
	static private boolean equalWithPrecision(double a, double b) {
		return Math.abs(a - b) < EPSILON;
	}
	
	static private boolean smallerWithPrecision(double a, double b) {
		return a < b - EPSILON;
	}
	
	static private boolean greaterWithPrecision(double a, double b) {
		return a > b + EPSILON;
	}

}
