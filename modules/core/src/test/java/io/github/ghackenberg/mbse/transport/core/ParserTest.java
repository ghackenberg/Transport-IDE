package io.github.ghackenberg.mbse.transport.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.structures.Vector;

public class ParserTest {

	private Model model;
	private Parser parser;
	
	@Before public void startup() {
		model = new Model();
		parser = new Parser();
	}
	
	@After public void cleanup() {
		model = null;
		parser = null;
	}
	
	@Test public void testfallA() {
		assertEquals(model.intersections.size(), 0);
		
		parser.parseIntersection(model, "A 200 100 0");
		
		assertEquals(model.intersections.size(), 1);
		
		Intersection intersection = model.intersections.get(0); 
		
		assertNotNull(intersection);
		assertEquals(intersection.name.get(), "A");
		
		Vector coordinate = intersection.coordinate; 
		
		assertNotNull(coordinate);
		assertEquals(coordinate.x.get(), 200, 0);
		assertEquals(coordinate.y.get(), 100, 0);
		assertEquals(coordinate.z.get(), 0, 0);
	}

	@Test public void testfallB() {
		assertEquals(model.intersections.size(), 0);
		
		parser.parseIntersection(model, "A 200 100 0");
		parser.parseIntersection(model, "B 0 100 200");
		
		assertEquals(model.intersections.size(), 2);
		
		assertEquals(model.segments.size(), 0);
		
		parser.parseSegment(model, "A->B 1 50");
		
		assertEquals(model.segments.size(), 1);
		
		Segment segment = model.segments.get(0);
		
		assertNotNull(segment);
		assertEquals(segment.start, model.intersections.get(0));
		assertEquals(segment.end, model.intersections.get(1));
		assertEquals(segment.lanes.get(), 1, 0);
		assertEquals(segment.speed.get(), 50, 0);
	}
	
}
