package io.github.ghackenberg.mbse.transport.model;

public class Location {

	// Statische Eigenschaften (geparst) oder dynamische Eigenschaften (simuliert)
	public Segment segment;
	public double distance;
	
	public Location() {
		
	}
	
	public Location(Segment segment, double distance) {
		this.segment = segment;
		this.distance = distance;
	}
	
	public Coordinate toCoordinate() {
		
		Coordinate start = segment.start.coordinate;
		Coordinate end = segment.end.coordinate;
		
		Coordinate coordinate = new Coordinate();
		
		double len = segment.getLength();
		double prg = distance / len; 
		
		coordinate.latitude = start.latitude + (end.latitude - start.latitude) * prg;
		coordinate.longitude = start.longitude + (end.longitude - start.longitude) * prg;
		coordinate.elevation = start.elevation + (end.elevation - start.elevation) * prg;
		
		return coordinate;
		
	}
	
	@Override
	public String toString() {
		return segment + ":" + Math.round(distance / segment.getLength() * 100);
	}
	
}
