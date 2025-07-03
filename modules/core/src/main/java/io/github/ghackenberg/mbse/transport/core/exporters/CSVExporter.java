package io.github.ghackenberg.mbse.transport.core.exporters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import io.github.ghackenberg.mbse.transport.core.Exporter;
import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class CSVExporter implements Exporter {

	private File demands;
	private File vehicles;
	private File segments;
	private File intersections;
	
	public CSVExporter(String path) {
		File folder = new File(path);
		
		if (!folder.exists()) {
			throw new IllegalStateException("Path does not exist");
		}
		if (!folder.isDirectory()) {
			throw new IllegalStateException("Path is not a directory");
		}
		
		demands = new File(folder, "demands.csv");		
		vehicles = new File(folder, "vehicles.csv");
		segments = new File(folder, "segments.csv");
		intersections = new File(folder, "intersections.csv");
		
		if (demands.isDirectory()) {
			throw new IllegalStateException("Demands path is a directory");
		}
		if (vehicles.isDirectory()) {
			throw new IllegalStateException("Vehicles path is a directory");
		}
		if (segments.isDirectory()) {
			throw new IllegalStateException("Segments path is a directory");
		}
		if (intersections.isDirectory()) {
			throw new IllegalStateException("Intersections path is a directory");
		}
	}
	
	@Override
	public void export(Model model, Statistics statistics) {
		exportDemands(model, statistics);
		exportVehicles(model, statistics);
		exportSegments(model, statistics);
		exportIntersections(model, statistics);
	}
	
	private void exportDemands(Model model, Statistics statistics) {
		try (Writer writer = new FileWriter(demands)) {
			// Write header
			writer.write("Size;Earliest Pickup;Latest Dropoff;Actual Pickup;Actual Dropoff;Distance");
			// Process demands
			for (Demand demand : model.demands) {
				// Write row
				writer.write("\n" + demand.size.get() + ";" + demand.pick.time.get() + ";" + demand.drop.time.get() + ";" + statistics.demandPickAcceptTimes.get(demand) + ";" + statistics.demandDropTimes.get(demand) + ";" + statistics.demandDistances.get(demand));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void exportVehicles(Model model, Statistics statistics) {
		try (Writer writer = new FileWriter(vehicles)) {
			// Write header
			writer.write("Name;Capacity;Distance");
			// Process vehicles
			for (Vehicle vehicle : model.vehicles) {
				// Write row
				writer.write("\n" + vehicle.name + ";" + vehicle.loadCapacity + ";" + statistics.vehicleDistances.get(vehicle));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exportSegments(Model model, Statistics statistics) {
		try (Writer writer = new FileWriter(segments)) {
			// Write header
			writer.write("Identifier;Lanes;Speed;Traversals");
			// Process segments
			for (Segment segment : model.segments) {
				// Write row
				writer.write("\n" + segment + ";" + segment.lanes.get() + ";" + segment.speed.get() + ";" + statistics.segmentTraversals.get(segment));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exportIntersections(Model model, Statistics statistics) {
		try (Writer writer = new FileWriter(intersections)) {
			// Write header
			writer.write("Name;Crossings");
			// Process intersections
			for (Intersection intersection : model.intersections) {
				// Write row
				writer.write("\n" + intersection.name.get() + ";" + statistics.intersectionCrossings.get(intersection));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
