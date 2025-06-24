package example.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.github.ghackenberg.mbse.transport.model.Coordinate;
import io.github.ghackenberg.mbse.transport.model.Intersection;
import io.github.ghackenberg.mbse.transport.model.Model;
import io.github.ghackenberg.mbse.transport.model.Segment;
import io.github.ghackenberg.mbse.transport.parser.Parser;

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
		assertEquals(intersection.name, "A");
		
		Coordinate coordinate = intersection.coordinate; 
		
		assertNotNull(coordinate);
		assertEquals(coordinate.latitude, 200, 0);
		assertEquals(coordinate.longitude, 100, 0);
		assertEquals(coordinate.elevation, 0, 0);
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
		assertEquals(segment.lanes, 1, 0);
		assertEquals(segment.speed, 50, 0);
	}
	
}
