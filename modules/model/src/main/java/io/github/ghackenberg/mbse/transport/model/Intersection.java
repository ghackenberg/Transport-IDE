package io.github.ghackenberg.mbse.transport.model;

import java.util.ArrayList;
import java.util.List;

public class Intersection {
	
	// Statische Eigenschaften (geparst)
	public String name;
	public Coordinate coordinate;
	public List<Segment> incoming = new ArrayList<>();
	public List<Segment> outgoing = new ArrayList<>();
	
}
