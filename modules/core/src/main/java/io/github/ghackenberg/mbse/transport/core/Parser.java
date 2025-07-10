package io.github.ghackenberg.mbse.transport.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.exceptions.DirectoryException;
import io.github.ghackenberg.mbse.transport.core.exceptions.MissingException;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import io.github.ghackenberg.mbse.transport.core.structures.LocationTime;

public class Parser {
	
	public Model parse(File folder) throws MissingException, DirectoryException {
		
		System.out.println("Parser.parse");
		
		File intersections = new File(folder, "intersections.txt");
		File segments = new File(folder, "segments.txt");
		File stations = new File(folder, "stations.txt");
		File vehicles = new File(folder, "vehicles.txt");
		File demands = new File(folder, "demands.txt");
		
		Model model = parse(intersections, segments, stations, vehicles, demands);
		
		model.name.set(folder.getName());
		
		return model;
		
	}
	
	private Model parse(File intersections, File segments, File stations, File vehicles, File demands) throws MissingException, DirectoryException {
		
		System.out.println("Parser.parse");
		
		if (!intersections.exists())
			throw new MissingException("Intersections file does not exit");
		if (!segments.exists())
			throw new MissingException("Segments file does not exit");
		if (!stations.exists())
			throw new MissingException("Stations file does not exit");
		if (!vehicles.exists())
			throw new MissingException("Vehicles file does not exit");
		if (!demands.exists())
			throw new MissingException("Demands file does not exit");
		
		if (intersections.isDirectory())
			throw new DirectoryException("Intersections file is directory");
		if (segments.isDirectory())
			throw new DirectoryException("Segments file is directory");
		if (stations.isDirectory())
			throw new DirectoryException("Stations file is directory");
		if (vehicles.isDirectory())
			throw new DirectoryException("Vehicles file is directory");
		if (demands.isDirectory())
			throw new DirectoryException("Demands file is directory");
		
		final Model model = new Model();
		
		try {
			
			BufferedReader reader;
			
			System.out.println("Parsing intersections");
			
			reader = new BufferedReader(new FileReader(intersections));
			reader.lines().forEach(line -> {
				this.parseIntersection(model, line);
			});
			reader.close();
			
			System.out.println("Parsing segments");
			
			reader = new BufferedReader(new FileReader(segments));
			reader.lines().forEach(line -> {
				this.parseSegment(model, line);
			});
			reader.close();
			
			System.out.println("Parsing stations");
			
			reader = new BufferedReader(new FileReader(stations));
			reader.lines().forEach(line -> {
				this.parseStation(model, line);
			});
			reader.close();
			
			System.out.println("Parsing vehicles");

			reader = new BufferedReader(new FileReader(vehicles));
			reader.lines().forEach(line -> {
				this.parseVehicle(model, line);
			});
			reader.close();
			
			System.out.println("Parsing demands");

			reader = new BufferedReader(new FileReader(demands));
			reader.lines().forEach(line -> {
				this.parseDemand(model, line);
			});
			reader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return model;
		
	}
	
	public void parseIntersection(Model model, String line) {
		
		System.out.println(line);
		
		String[] parts = line.split(" ");
		
		if (parts.length != 4)
			throw new IllegalArgumentException(line);
		
		Intersection intersection = new Intersection();
		
		intersection.name.set(parts[0]);
		intersection.coordinate.x.set(Double.parseDouble(parts[1]));
		intersection.coordinate.y.set(Double.parseDouble(parts[2]));
		intersection.coordinate.z.set(Double.parseDouble(parts[3]));
		
		model.intersections.add(intersection);
		
	}
	
	public void parseSegment(Model model, String line) {
		
		System.out.println(line);
		
		String[] parts = line.split(" ");
		
		if (parts.length != 3)
			throw new IllegalArgumentException(line);
		
		String[] intersections = parts[0].split("->");
		
		if (intersections.length != 2)
			throw new IllegalArgumentException(parts[0]);
		
		Intersection start = model.getIntersection(intersections[0]);
		Intersection end = model.getIntersection(intersections[1]);
		
		if (start == null)
			throw new IllegalArgumentException(intersections[0]);
		if (end == null)
			throw new IllegalArgumentException(intersections[1]);
		
		Segment segment = new Segment(start, end);
		
		segment.lanes.set(Double.parseDouble(parts[1]));
		segment.speed.set(Double.parseDouble(parts[2]));

		// Remember outgoing segment
		start.outgoing.add(segment);
		// Remember incoming segment
		end.incoming.add(segment);
		// Remember segment
		model.segments.add(segment);
		
	}
	
	public void parseStation(Model model, String line) {
		
		System.out.println(line);
		
		String[] parts = line.split(" ");
		
		if (parts.length != 2)
			throw new IllegalArgumentException(line);
		
		Location location = resolveLocation(model, parts[1]);
		
		Station station = new Station();
		
		station.speed.set(Double.parseDouble(parts[0]));
		station.location.segment.set(location.segment.get());
		station.location.distance.set(location.distance.get());
		
		model.stations.add(station);
		
	}
	
	public void parseVehicle(Model model, String line) {
		
		System.out.println(line);
		
		String[] parts = line.split(" ");
		
		if (parts.length != 7)
			throw new IllegalArgumentException(line);
		
		Location initialLocation = resolveLocation(model, parts[6]);
		
		Vehicle vehicle = new Vehicle();
		
		vehicle.name.set(parts[0]);
		vehicle.length.set(Double.parseDouble(parts[1]));
		vehicle.loadCapacity.set(Double.parseDouble(parts[2]));
		vehicle.batteryCapacity.set(Double.parseDouble(parts[3]));
		vehicle.initialBatteryLevel.set(Double.parseDouble(parts[4]));
		vehicle.initialSpeed.set(Double.parseDouble(parts[5]));
		vehicle.initialLocation.segment.set(initialLocation.segment.get());
		vehicle.initialLocation.distance.set(initialLocation.distance.get());
		
		model.vehicles.add(vehicle);
		
	}
	
	public void parseDemand(Model model, String line) {
		
		System.out.println(line);
		
		String[] parts = line.split(" ");
		
		if (parts.length != 3)
			throw new IllegalArgumentException(line);
		
		LocationTime pickup = resolveLocationTime(model, parts[0]);
		LocationTime dropoff = resolveLocationTime(model, parts[1]); 
		
		Demand demand = new Demand(pickup, dropoff, Double.parseDouble(parts[2]));
		
		model.demands.add(demand);
		
	}
	
	public LocationTime resolveLocationTime(Model model, String line) {
		
		String[] parts = line.split("@");
		
		if (parts.length != 2)
			throw new IllegalArgumentException(line);
		
		Location location = resolveLocation(model, parts[0]);
		
		LocationTime locTime = new LocationTime();
		
		locTime.location.segment.set(location.segment.get());
		locTime.location.distance.set(location.distance.get());
		locTime.time.set(Double.parseDouble(parts[1]));
		
		return locTime;
		
	}
	
	public Location resolveLocation(Model model, String line) {
		
		String[] parts = line.split(":");
		
		if (parts.length != 2)
			throw new IllegalArgumentException(line);
		
		Location location = new Location();
		
		location.segment.set(resolveSegment(model, parts[0]));
		location.distance.set(location.segment.get().length.get() * Double.parseDouble(parts[1]) / 100.0);
		
		return location;
		
	}
	
	public Segment resolveSegment(Model model, String line) {
		
		String[] parts = line.split("->");
		
		if (parts.length != 2)
			throw new IllegalArgumentException(line);
		
		Intersection start = model.getIntersection(parts[0]);
		Intersection end = model.getIntersection(parts[1]);
		
		if (start == null)
			throw new IllegalArgumentException(parts[0]);
		if (end == null)
			throw new IllegalArgumentException(parts[1]);
		
		return model.getSegment(start, end);
		
	}

}
