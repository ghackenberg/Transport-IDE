package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.fx.viewers.DemandViewer;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;

public class DemandViewerDeep extends DemandViewer {
	
	private final PhongMaterial material = new PhongMaterial();
	
	public final Sphere pick = new Sphere();
	public final Sphere drop = new Sphere();
	
	public final TriangleMesh mesh = new TriangleMesh();
	
	public final MeshView view = new MeshView(mesh);

	public DemandViewerDeep(Model model, Demand entity) {
		super(model, entity);
		
		// Material
		
		material.diffuseColorProperty().bind(color);
		
		// Pick
		
		pick.radiusProperty().bind(entity.size.divide(2));
		
		pick.translateXProperty().bind(location.coordinate.x);
		pick.translateYProperty().bind(location.coordinate.y);
		pick.translateZProperty().bind(location.coordinate.z.subtract(entity.size.divide(2)));
		
		pick.setMaterial(material);
		
		// Drop

		drop.radiusProperty().bind(entity.size.divide(2));
		
		drop.translateXProperty().bind(entity.drop.location.coordinate.x);
		drop.translateYProperty().bind(entity.drop.location.coordinate.y);
		drop.translateZProperty().bind(entity.drop.location.coordinate.z.subtract(entity.size.divide(2)));
		
		drop.setMaterial(material);
		
		// Mesh
		
		mesh.getPoints().addAll(recomputePoints());
		mesh.getTexCoords().addAll(0, 0, 1, 1);
		mesh.getFaces().addAll(0, 1, 0, 0, 1, 0);
		
		start.x.addListener(event -> recompute());
		start.y.addListener(event -> recompute());
		start.z.addListener(event -> recompute());
		
		end.x.addListener(event -> recompute());
		end.y.addListener(event -> recompute());
		end.z.addListener(event -> recompute());
		
		entity.size.addListener(event -> recompute());
		
		// View
		
		view.setMaterial(material);
		view.setCullFace(CullFace.NONE);
		view.setDrawMode(DrawMode.LINE);
		
		// Self
		
		getChildren().add(pick);
		getChildren().add(drop);
		getChildren().add(view);
	}
	
	private void recompute() {
		mesh.getPoints().setAll(recomputePoints());
	}
	
	private float[] recomputePoints() {
		float sx = (float) start.x.get();
		float sy = (float) start.y.get();
		float sz = (float) start.z.get() - (float) entity.size.get() / 2;
		
		float ex = (float) end.x.get();
		float ey = (float) end.y.get();
		float ez = (float) end.z.get() - (float) entity.size.get() / 2;
		
		return new float[] {
			sx, sy, sz,
			ex, ey, ez
		};
	}

}
