package io.github.ghackenberg.mbse.transport.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.structures.Location;
import io.github.ghackenberg.mbse.transport.core.structures.LocationTime;

public class Serializer {

	public void serialize(Model model, File folder) {
		
		// Create folder (if not exists)
		
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		// Check folder is directory
		
		if (!folder.isDirectory()) {
			throw new IllegalArgumentException("Folder is not a directory!");
		}
		
		// Create file objects
		
		File intersectionFile = new File(folder, "intersections.txt");
		File segmentFile = new File(folder, "segments.txt");
		File stationFile = new File(folder, "stations.txt");
		File vehicleFile = new File(folder, "vehicles.txt");
		File demandFile = new File(folder, "demands.txt");
		
		// Delete files (if exist)
		
		if (intersectionFile.exists()) {
			intersectionFile.delete();
		}
		if (segmentFile.exists()) {
			segmentFile.delete();
		}
		if (stationFile.exists()) {
			stationFile.delete();
		}
		if (vehicleFile.exists()) {
			vehicleFile.delete();
		}
		if (demandFile.exists()) {
			demandFile.delete();
		}
		
		// Write intersections
		
		try (FileWriter out = new FileWriter(intersectionFile)) {
			for (Intersection intersection : model.intersections) {
				double x = intersection.coordinate.x.get();
				double y = intersection.coordinate.y.get();
				double z = intersection.coordinate.z.get();
				
				out.write(serialize(intersection) + " " + x + " " + y + " " + z + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Write segments
		
		try (FileWriter out = new FileWriter(segmentFile)) {
			for (Segment segment : model.segments) {
				double lanes = segment.lanes.get();
				double speed = segment.speed.get();
				
				out.write(serialize(segment) + " " + lanes + " " + speed + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Write stations
		
		try (FileWriter out = new FileWriter(stationFile)) {
			for (Station station : model.stations) {
				double speed = station.speed.get();
				
				out.write(speed + " " + serialize(station.location) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Write vehicles
		
		try (FileWriter out = new FileWriter(vehicleFile)) {
			for (Vehicle vehicle : model.vehicles) {
				double length = vehicle.length.get();
				double loadCapacity = vehicle.loadCapacity.get();
				double batteryCapacity = vehicle.batteryCapacity.get();
				
				double initialBatteryLevel = vehicle.initialBatteryLevel.get();
				double initialSpeed = vehicle.initialSpeed.get();
				
				out.write(serialize(vehicle) + " " + length + " " + loadCapacity + " " + batteryCapacity + " " + initialBatteryLevel + " " + initialSpeed + " " + serialize(vehicle.initialLocation) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Write demands
		
		try (FileWriter out = new FileWriter(demandFile)) {
			for (Demand demand : model.demands) {
				out.write(serialize(demand.pick) + " " + serialize(demand.drop) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private String serialize(LocationTime locationTime) {
		double time = locationTime.time.get();
		
		return serialize(locationTime.location) + "@" + time;
	}
	
	private String serialize(Location location) {
		double distance = location.distance.get();
		
		return serialize(location.segment.get()) + ":" + distance;
	}
	
	private String serialize(Segment segment) {
		return serialize(segment.start) + "->" + serialize(segment.end);
	}
	
	private String serialize(Intersection intersection) {
		return serialize(intersection.name.get());
	}
	
	private String serialize(Vehicle vehicle) {
		return serialize(vehicle.name.get());
	}
	
	private String serialize(String name) {
		return name.replaceAll(" ", "_");
	}
	
}
