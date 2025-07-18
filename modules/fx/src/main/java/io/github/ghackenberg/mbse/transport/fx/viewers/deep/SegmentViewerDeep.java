package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.fx.viewers.SegmentViewer;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class SegmentViewerDeep extends SegmentViewer {
	
	private final TriangleMesh mesh = new TriangleMesh();
	
	private final PhongMaterial material = new PhongMaterial();
	
	public final MeshView view = new MeshView(mesh);

	public SegmentViewerDeep(Model model, Segment entity) {
		super(model, entity);
		
		// Mesh
		
		mesh.getPoints().addAll(recomputePoints());
		mesh.getTexCoords().addAll(0, 0, 0, 1, 1, 1, 1, 0);
		mesh.getFaces().addAll(0, 1, 2, 0, 1, 2, 0, 2, 3, 0, 2, 3);
		
		entity.start.coordinate.x.addListener(event -> recompute());
		entity.start.coordinate.y.addListener(event -> recompute());
		entity.start.coordinate.z.addListener(event -> recompute());
		
		entity.end.coordinate.x.addListener(event -> recompute());
		entity.end.coordinate.y.addListener(event -> recompute());
		entity.end.coordinate.z.addListener(event -> recompute());

		entity.lanes.addListener(event -> recompute());
		
		// Material
		
		material.diffuseColorProperty().bind(Bindings.when(selected).then(Color.GRAY).otherwise(Color.LIGHTGRAY));
		
		// View
		
		view.setCullFace(CullFace.NONE);
		view.setMaterial(material);
		
		// Self
		
		getChildren().add(view);
	}
	
	private void recompute() {
		mesh.getPoints().setAll(recomputePoints());
	}
	
	private float[] recomputePoints() {
		float lanes = (float) entity.lanes.get();
		
		float sx = (float) entity.start.coordinate.x.get();
		float sy = (float) entity.start.coordinate.y.get();
		float sz = (float) entity.start.coordinate.z.get();
		
		float ex = (float) entity.end.coordinate.x.get();
		float ey = (float) entity.end.coordinate.y.get();
		float ez = (float) entity.end.coordinate.z.get();
		
		float tx = (float) entity.tangent.x.get() * lanes / 2;
		float ty = (float) entity.tangent.y.get() * lanes / 2;
		
		return new float[] {
			sx - ty, sy + tx, sz,
			sx + ty, sy - tx, sz,
			ex + ty, ey - tx, ez,
			ex - ty, ey + tx, ez
		};
	}

}
