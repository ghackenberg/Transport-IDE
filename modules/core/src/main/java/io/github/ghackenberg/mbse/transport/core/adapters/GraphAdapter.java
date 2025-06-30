package io.github.ghackenberg.mbse.transport.core.adapters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;

public class GraphAdapter implements Graph<Intersection, Segment> {
	
	private Model model;
	
	public GraphAdapter(Model model) {
		this.model = model;
	}
	
	@Override
	public Intersection getEdgeSource(Segment segment) {
		return segment.start;
	}
	@Override
	public Intersection getEdgeTarget(Segment segment) {
		return segment.end;
	}
	@Override
	public double getEdgeWeight(Segment segment) {
		return segment.length.get();
	}
	@Override
	public Set<Segment> getAllEdges(Intersection sourceVertex, Intersection targetVertex) {
		Set<Segment> result = new HashSet<>();
		result.add(model.getSegment(sourceVertex, targetVertex));
		return result;
	}
	@Override
	public Segment getEdge(Intersection sourceVertex, Intersection targetVertex) {
		return model.getSegment(sourceVertex, targetVertex);
	}
	@Override
	public Supplier<Intersection> getVertexSupplier() {
		throw new IllegalStateException();
	}
	@Override
	public Supplier<Segment> getEdgeSupplier() {
		throw new IllegalStateException();
	}
	@Override
	public Segment addEdge(Intersection sourceVertex, Intersection targetVertex) {
		throw new IllegalStateException();
	}
	@Override
	public boolean addEdge(Intersection sourceVertex, Intersection targetVertex, Segment e) {
		throw new IllegalStateException();
	}
	@Override
	public Intersection addVertex() {
		throw new IllegalStateException();
	}
	@Override
	public boolean addVertex(Intersection v) {
		throw new IllegalStateException();
	}
	@Override
	public boolean containsEdge(Intersection sourceVertex, Intersection targetVertex) {
		return model.getSegment(sourceVertex, targetVertex) != null;
	}
	@Override
	public boolean containsEdge(Segment e) {
		return model.segments.contains(e);
	}
	@Override
	public boolean containsVertex(Intersection v) {
		return model.intersections.contains(v);
	}
	@Override
	public Set<Segment> edgeSet() {
		return new HashSet<>(model.segments);
	}
	@Override
	public int degreeOf(Intersection vertex) {
		return inDegreeOf(vertex) + outDegreeOf(vertex);
	}
	@Override
	public Set<Segment> edgesOf(Intersection vertex) {
		Set<Segment> result = new HashSet<>();
		result.addAll(vertex.incoming);
		result.addAll(vertex.outgoing);
		return result;
	}
	@Override
	public int inDegreeOf(Intersection vertex) {
		return vertex.incoming.size();
	}
	@Override
	public Set<Segment> incomingEdgesOf(Intersection vertex) {
		return new HashSet<>(vertex.incoming);
	}
	@Override
	public int outDegreeOf(Intersection vertex) {
		return vertex.outgoing.size();
	}
	@Override
	public Set<Segment> outgoingEdgesOf(Intersection vertex) {
		return new HashSet<>(vertex.outgoing);
	}
	@Override
	public boolean removeAllEdges(Collection<? extends Segment> edges) {
		throw new IllegalStateException();
	}
	@Override
	public Set<Segment> removeAllEdges(Intersection sourceVertex, Intersection targetVertex) {
		throw new IllegalStateException();
	}
	@Override
	public boolean removeAllVertices(Collection<? extends Intersection> vertices) {
		throw new IllegalStateException();
	}
	@Override
	public Segment removeEdge(Intersection sourceVertex, Intersection targetVertex) {
		throw new IllegalStateException();
	}
	@Override
	public boolean removeEdge(Segment e) {
		throw new IllegalStateException();
	}
	@Override
	public boolean removeVertex(Intersection v) {
		throw new IllegalStateException();
	}
	@Override
	public Set<Intersection> vertexSet() {
		return new HashSet<>(model.intersections);
	}
	@Override
	public GraphType getType() {
		return GraphTypeBuilder.<Intersection, Segment>directed().weighted(true).allowingMultipleEdges(false).allowingSelfLoops(true).buildType();
	}
	@Override
	public void setEdgeWeight(Segment e, double weight) {
		throw new IllegalStateException();
	}
	
}
