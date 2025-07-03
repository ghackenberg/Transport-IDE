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
	
	private static final boolean DEBUG = false;
	
	private static int COUNT = 0;
	
	private final int number = COUNT++;
	
	private boolean stop;
	private boolean pause;
	private boolean step;
	
	private final String name;
	private final Model model;
	private final Controller controller;
	private final Statistics statistics;
	
	private double maxModelTimeStep;
	private double ratioModelRealTime;
	
	private final File runsFolder;
	
	private File runFolder;
	
	private final Synchronizer synchronizer;
	
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
	
	public Simulator(String name, Model model, Controller controller, double maxModelTimeStep, double ratioModelRealTime, File runsFolder) {
		this(name, model, controller, maxModelTimeStep, ratioModelRealTime, runsFolder, new Statistics(model));
	}
	
	public Simulator(String name, Model model, Controller controller, double maxModelTimeStep, double ratioModelRealTime, File runsFolder, Statistics statistics) {
		this(name, model, controller, maxModelTimeStep, ratioModelRealTime, runsFolder, statistics, new Synchronizer(1));
	}
	
	public Simulator(String name, Model model, Controller controller, double maxModelTimeStep, double ratioModelRealTime, File runsFolder, Synchronizer synchronizer) {
		this(name, model, controller, maxModelTimeStep, ratioModelRealTime, runsFolder, new Statistics(model), synchronizer);
	}
	
	public Simulator(String name, Model model, Controller controller, double maxModelTimeStep, double ratioModelRealTime, File runsFolder, Statistics statistics, Synchronizer synchronizer) {
		this.name = name;
		this.model = model;
		this.controller = controller;
		this.maxModelTimeStep = maxModelTimeStep;
		this.ratioModelRealTime = ratioModelRealTime;
		this.runsFolder = runsFolder;
		this.synchronizer = synchronizer;
		this.statistics = statistics;
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
		final double modelTimeBefore = model.state.get().time;
		
		// Perform calculation (and advance simulation time)
		calculate();
		
		// Remember real time after calculation
		final double realTimeAfter = System.currentTimeMillis();
		// Remember model time after calculation
		final double modelTimeAfter = model.state.get().time;
		
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
			if (equalWithPrecision(vehicle.state.get().distance, vehicle.state.get().segment.length.get())) {
				// Remember segment
				Segment previous = vehicle.state.get().segment;
				// Select segment
				Segment next = controller.selectSegment(vehicle);
				// Check segment
				if (!previous.end.outgoing.contains(next)) {
					throw new InvalidRouteException(vehicle, previous, next);
				}
				// Update segment
				vehicle.state.get().segment = next;
				// Reset segment distance
				vehicle.state.get().distance = 0;
				// Update speed
				vehicle.state.get().speed = controller.selectSpeed(vehicle);
				// Update demand distances
				for (Demand demand : vehicle.state.get().demands) {
					demand.state.get().segment = next;
					demand.state.get().distance = 0;
				}
				// Update statistics
				statistics.recordCrossing(vehicle, previous, next, model.state.get().time);
				statistics.recordSpeed(vehicle, vehicle.state.get().speed, model.state.get().time);
				// Update model time step
				modelTimeStep = 0;
			}
		}
		
		// Unassign station
		for (Vehicle vehicle : model.vehicles) {
			// Check vehicle station
			if (vehicle.state.get().station != null) {
				// Check battery level
				if (equalWithPrecision(vehicle.state.get().batteryLevel, vehicle.batteryCapacity.get()) || controller.unselectStation(vehicle)) {
					// Remember station
					Station station = vehicle.state.get().station;
					// Unassign vehicle
					station.state.get().vehicle = null;
					// Unassign station
					vehicle.state.get().station = null;
					// Update statistics
					statistics.recordChargeEnd(vehicle, station, modelTimeStep);
				}
			}
		}
		
		// Assign station
		for (Vehicle vehicle : model.vehicles) {
			// Check vehicle station
			if (vehicle.state.get().station == null && smallerWithPrecision(vehicle.state.get().batteryLevel, vehicle.batteryCapacity.get())) {
				// Process stations
				for (Station station : model.stations) {
					// Check station
					if (station.state.get().vehicle == null) {
						// Check segment
						if (vehicle.state.get().segment == station.location.segment.get()) {
							// Check distance
							if (equalWithPrecision(vehicle.state.get().distance, station.location.distance.get())) {
								// Ask controller
								if (controller.selectStation(vehicle, station)) {
									// Assign station
									vehicle.state.get().station = station;
									// Assign vehicle
									station.state.get().vehicle = vehicle;
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
			if (demand.state.get().done == false && demand.state.get().vehicle == null && !smallerWithPrecision(model.state.get().time, demand.pick.time.get())) {
				// Process vehicles
				for (Vehicle vehicle : model.vehicles) {
					// Check vehicle
					if (greaterWithPrecision(vehicle.state.get().batteryLevel, 0) && vehicle.state.get().station == null) {
						// Compare segment
						if (demand.state.get().segment == vehicle.state.get().segment) {
							// Compare distance on segment
							if (equalWithPrecision(demand.state.get().distance, vehicle.state.get().distance)) {
								// Compare load vs. capacity
								if (!greaterWithPrecision(vehicle.state.get().loadLevel + demand.size.get(), vehicle.loadCapacity.get())) {
									// Declined before?
									if (!declines.get(demand).containsKey(model.state.get().time) || !declines.get(demand).get(model.state.get().time).contains(vehicle)) {
										// Ask controller for pickup decision
										if (controller.selectDemand(vehicle, demand)) {
											// Update demand
											demand.state.get().vehicle = vehicle;
											// Update vehicle
											vehicle.state.get().loadLevel += demand.size.get();
											vehicle.state.get().demands.add(demand);
											// Update statistics
											statistics.recordPickupAccept(vehicle, demand, model.state.get().time);
											// Update model time step
											modelTimeStep = 0;
										} else {
											// Check time
											if (!declines.get(demand).containsKey(model.state.get().time)) {
												// Add set
												declines.get(demand).put(model.state.get().time, new HashSet<>());
											}
											// Update declines
											declines.get(demand).get(model.state.get().time).add(vehicle);
											// Update statistics
											statistics.recordPickupDecline(vehicle, demand, model.state.get().time);
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
			for (int index = 0; index < vehicle.state.get().demands.size(); index++) {
				// Obtain demand
				Demand demand = vehicle.state.get().demands.get(index);
				// Compare segment
				if (demand.drop.location.segment.get() == vehicle.state.get().segment) {
					// Compare distance on segment
					if (equalWithPrecision(demand.drop.location.distance.get(), vehicle.state.get().distance)) {
						// Update demand
						demand.state.get().done = true;
						demand.state.get().vehicle = null;
						// Update vehicle
						vehicle.state.get().loadLevel -= demand.size.get();
						vehicle.state.get().demands.remove(index--);
						// Update statistics
						statistics.recordDropoff(vehicle, demand, model.state.get().time);
						// Update model time step
						modelTimeStep = 0;
					}
				}
			}
		}
		
		// Dropoff demands (empty battery vehicles)
		for (Vehicle vehicle : model.vehicles) {
			if (equalWithPrecision(vehicle.state.get().batteryLevel, 0)) {
				while (vehicle.state.get().demands.size() > 0) {
					Demand demand = vehicle.state.get().demands.remove(0);
					
					demand.state.get().vehicle = null;
					
					demand.state.get().segment = vehicle.state.get().segment;
					demand.state.get().distance = vehicle.state.get().distance;
					
					vehicle.state.get().loadLevel -= demand.size.get();
					
					modelTimeStep = 0;
				}
			}
		}
		
		// Update vehicle speed
		for (Vehicle vehicle : model.vehicles) {
			// Initialize speed
			double speed = 0;
			// Select speed
			if (greaterWithPrecision(vehicle.state.get().batteryLevel, 0) && vehicle.state.get().station == null) {
				speed = controller.selectSpeed(vehicle);
			}
			// Check speed
			if (greaterWithPrecision(speed, vehicle.state.get().segment.speed.get())) {
				throw new InvalidSpeedException(vehicle, speed);
			}
			// Update speed
			vehicle.state.get().speed = speed;
			// Update statistics
			statistics.recordSpeed(vehicle, speed, model.state.get().time);
		}
		
		// Duration until speed selection
		for (Vehicle vehicle : model.vehicles) {
			// Check battery level
			if (greaterWithPrecision(vehicle.state.get().batteryLevel, 0) && vehicle.state.get().station == null) {
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
			if (vehicle.state.get().station != null) {
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
			if (greaterWithPrecision(vehicle.state.get().speed, 0)) {
				// Speed in meter per millisecond
				double speed = vehicle.state.get().speed * 1000.0 / 60.0 / 60.0 / 1000.0;
				// Duration in milliseconds
				double duration = vehicle.state.get().batteryLevel / speed;
				// Update model time step
				modelTimeStep = Math.min(modelTimeStep, duration);
			}
		}
		
		// Duration until segment end
		for (Vehicle vehicle : model.vehicles) {
			// Check speed
			if (greaterWithPrecision(vehicle.state.get().speed, 0)) {
				// Speed in meter per millisecond
				double speed = vehicle.state.get().speed * 1000.0 / 60.0 / 60.0 / 1000.0;
				// Delta in meter
				double delta = vehicle.state.get().segment.length.get() - vehicle.state.get().distance;
				// Duration in milliseconds
				double duration = delta / speed;
				// Update model time step
				modelTimeStep = Math.min(modelTimeStep, duration);
			}
		}
		
		// Duration until demand appearance
		for (Demand demand : model.demands) {
			if (greaterWithPrecision(demand.pick.time.get(), model.state.get().time)) {
				modelTimeStep = Math.min(modelTimeStep, demand.pick.time.get() - model.state.get().time);
			}
		}
		
		// Duration until demand overdue
		for (Demand demand : model.demands) {
			if (demand.state.get().done == false && greaterWithPrecision(demand.drop.time.get(), model.state.get().time)) {
				modelTimeStep = Math.min(modelTimeStep, demand.drop.time.get() - model.state.get().time);
			}
		}
		
		// Duration until station
		for (Vehicle vehicle : model.vehicles) {
			// Check speed
			if (greaterWithPrecision(vehicle.state.get().speed, 0)) {
				// Process stations
				for (Station station : model.stations) {
					// Compare segments
					if (vehicle.state.get().segment == station.location.segment.get()) {
						// Compare distances
						if (smallerWithPrecision(vehicle.state.get().distance, station.location.distance.get())) {
							// Speed in meter per millisecond
							double speed = vehicle.state.get().speed * 1000.0 / 60.0 / 60.0 / 1000.0;
							// Delta in meter
							double delta = station.location.distance.get() - vehicle.state.get().distance;
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
			if (vehicle.state.get().station != null) {
				// Speed in meter per millisecond
				double speed = vehicle.state.get().station.speed.get() * 1000.0 / 60.0 / 60.0 / 1000.0;
				// Delta in meter
				double delta = vehicle.batteryCapacity.get() - vehicle.state.get().batteryLevel;
				// Duration in milliseconds
				double duration = delta / speed;
				// Update model time step
				modelTimeStep = Math.min(modelTimeStep, duration);
			}
		}
		
		// Duration until demand pickup
		for (Demand demand : model.demands) {
			// Check demand relevance
			if (demand.state.get().done == false && demand.state.get().vehicle == null && !greaterWithPrecision(demand.pick.time.get(), model.state.get().time)) {
				// Process vehicles
				for (Vehicle vehicle : model.vehicles) {
					// Check speed
					if (greaterWithPrecision(vehicle.state.get().speed, 0)) {
						// Pickup on same segment
						if (demand.state.get().segment == vehicle.state.get().segment) {
							// Pickup ahead
							if (greaterWithPrecision(demand.state.get().distance, vehicle.state.get().distance)) {
								// Enough capactiy?
								if (!greaterWithPrecision(demand.size.get(), vehicle.loadCapacity.get() - vehicle.state.get().loadLevel)) {
									// Speed in meter per millisecond
									double speed = vehicle.state.get().speed * 1000.0 / 60.0 / 60.0 / 1000.0;
									// Delta in meter
									double delta = demand.state.get().distance - vehicle.state.get().distance;
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
			if (greaterWithPrecision(vehicle.state.get().speed, 0)) {
				// Process demands
				for (Demand demand : vehicle.state.get().demands) {
					// Dropoff on same segment
					if (vehicle.state.get().segment == demand.drop.location.segment.get()) {
						// Dropoff ahead
						if (smallerWithPrecision(vehicle.state.get().distance, demand.drop.location.distance.get())) {
							// Speed in meter per millisecond
							double speed = vehicle.state.get().speed * 1000.0 / 60.0 / 60.0 / 1000.0;
							// Delta in meter
							double delta = demand.drop.location.distance.get() - vehicle.state.get().distance;
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
			
			if (greaterWithPrecision(outer.state.get().speed, 0)) {
			
				double outerBack = outer.state.get().distance - outer.length.get() / 2;
				double outerFront = outer.state.get().distance + outer.length.get() / 2;
				
				for (int j = i + 1; j < model.vehicles.size(); j++) {
					Vehicle inner = model.vehicles.get(j);
					
					if (greaterWithPrecision(inner.state.get().speed, 0)) {
					
						double innerBack = inner.state.get().distance - inner.length.get() / 2;
						double innerFront = inner.state.get().distance + inner.length.get() / 2;
						
						// On same segment?
						if (outer.state.get().segment == inner.state.get().segment) {	
							if (smallerWithPrecision(inner.state.get().speed, outer.state.get().speed)) {
								double speed = (outer.state.get().speed - inner.state.get().speed) * 1000.0 / 60.0 / 60.0 / 1000.0;
								
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
							} else if (smallerWithPrecision(outer.state.get().speed, inner.state.get().speed)) {
								double speed = (inner.state.get().speed - outer.state.get().speed) * 1000.0 / 60.0 / 60.0 / 1000.0;
								
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
		statistics.recordStep(modelTimeStep, model.state.get().time);
		
		// Update vehicle state
		for (Vehicle vehicle : model.vehicles) {
			// Speed in meter per millisecond
			double speed = vehicle.state.get().speed * 1000.0 / 60.0 / 60.0 / 1000.0;
			// Delta in meter
			double delta = speed * modelTimeStep;
			// Decrease battery level
			vehicle.state.get().batteryLevel -= delta;
			// Check vehicle station
			if (vehicle.state.get().station != null) {
				// Increase battery level
				vehicle.state.get().batteryLevel += vehicle.state.get().station.speed.get() * 1000.0 / 60.0 / 60.0 / 1000.0 * modelTimeStep;
			}
			// Update distance
			vehicle.state.get().distance += delta;
			// Update demand distances
			for (Demand demand : vehicle.state.get().demands) {
				demand.state.get().distance += delta;
			}
			// Update statistics
			statistics.recordDistance(vehicle, delta, model.state.get().time);
		}
		
		// Update model time
		model.state.get().time += modelTimeStep;
		
		// Update collisions
		updateCollisions();
		
		// Throw infinity exception
		if (Double.isInfinite(model.state.get().time)) {
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
			map.get(vehicle.state.get().segment).add(vehicle);
		});
		model.segments.forEach(segment -> {
			map.get(segment).sort((first, second) -> {
				if (!equalWithPrecision(first.state.get().speed, second.state.get().speed)) {
					return (int) Math.signum(first.state.get().speed - second.state.get().speed);
				} else {
					if (!equalWithPrecision(first.state.get().distance, second.state.get().distance)) {
						return (int) Math.signum(first.state.get().distance - second.state.get().distance);
					} else {
						return first.name.get().compareTo(second.name.get());
					}
				}
			});
		});
		// Clear vehicles
		model.vehicles.forEach(vehicle -> {
			// Clear collisions
			vehicle.state.get().collisions.clear();
			vehicle.state.get().collisions.add(vehicle);
			// Clear lane
			vehicle.state.get().lane = -1;
		});
		// Clear segments
		model.segments.forEach(segment -> {
			// Clear load
			segment.state.get().load = 0;
			// Clear collisions
			segment.state.get().collisions = null;
		});
		// Collect collisions
		for (int i = 0; i < model.vehicles.size(); i++) {
			Vehicle outer = model.vehicles.get(i);
			// Calculate front/back
			double outerBack = outer.state.get().distance - outer.length.get() / 2;
			double outerFront = outer.state.get().distance + outer.length.get() / 2;
			// Compare each other vehicle
			for (int j = i + 1; j < model.vehicles.size(); j++) {
				Vehicle inner = model.vehicles.get(j);
				// Calculate front/back
				double innerBack = inner.state.get().distance - inner.length.get() / 2;
				double innerFront = inner.state.get().distance + inner.length.get() / 2;
				// On same segment?
				if (outer.state.get().segment == inner.state.get().segment) {
					// Outer faster
					if (smallerWithPrecision(inner.state.get().speed, outer.state.get().speed)) {
						if (smallerWithPrecision(outerBack, innerFront) && !smallerWithPrecision(outerFront, innerBack)) {
							outer.state.get().collisions.add(inner);
							inner.state.get().collisions.add(outer);
						}
					}
					// Inner faster
					if (smallerWithPrecision(outer.state.get().speed, inner.state.get().speed)) {
						if (smallerWithPrecision(innerBack, outerFront) && !smallerWithPrecision(innerFront, outerBack)) {
							outer.state.get().collisions.add(inner);
							inner.state.get().collisions.add(outer);
						}
					}
					// Same speed
					if (equalWithPrecision(outer.state.get().speed, inner.state.get().speed)) {
						if (smallerWithPrecision(outerBack, innerFront) && greaterWithPrecision(outerFront, innerBack)) {
							outer.state.get().collisions.add(inner);
							inner.state.get().collisions.add(outer);
						}
						if (smallerWithPrecision(innerBack, outerFront) && greaterWithPrecision(innerFront, outerBack)) {
							outer.state.get().collisions.add(inner);
							inner.state.get().collisions.add(outer);
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
					for (Vehicle inner : outer.state.get().collisions) {
						if (inner.state.get().lane == lane) {
							free = false;
							break;
						}
					}
					if (free) {
						outer.state.get().lane = lane;
						break;
					}
				}
			});
		});
		// Update segments
		model.vehicles.forEach(vehicle -> {
			if (vehicle.state.get().segment.state.get().load < vehicle.state.get().lane + 1) {
				vehicle.state.get().segment.state.get().load = vehicle.state.get().lane + 1;
				vehicle.state.get().segment.state.get().collisions = vehicle.state.get().collisions;
			}
		});
		// Throw exceptions
		for (Segment segment : model.segments) {
			if (segment.state.get().load > segment.lanes.get()) {
				throw new CollisionException(segment, segment.state.get().collisions);
			}
		}
	}
	
	public boolean isFinished() {
		for (Demand demand : model.demands) {
			if (!demand.state.get().done) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmpty() {
		for (Vehicle vehicle : model.vehicles) {
			if (greaterWithPrecision(vehicle.state.get().batteryLevel, 0)) {
				return false;
			} else if (vehicle.state.get().station != null) {
				return false;
			} else {
				for (Station station : model.stations) {
					if (vehicle.state.get().segment == station.location.segment.get()) {
						if (equalWithPrecision(vehicle.state.get().distance, station.location.distance.get())) {
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
