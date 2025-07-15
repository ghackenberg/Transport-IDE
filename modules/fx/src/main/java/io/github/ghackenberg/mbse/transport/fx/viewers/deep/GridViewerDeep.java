package io.github.ghackenberg.mbse.transport.fx.viewers.deep;

import io.github.ghackenberg.mbse.transport.core.Model;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class GridViewerDeep extends Group {

    private final Model model;

    public GridViewerDeep(Model model) {
        this.model = model;

        model.min.x.addListener(event -> updateGrid());
        model.min.y.addListener(event -> updateGrid());

        model.max.x.addListener(event -> updateGrid());
        model.max.y.addListener(event -> updateGrid());
        model.max.z.addListener(event -> updateGrid());

        updateGrid();
    }

    private void updateGrid() {
        getChildren().clear();

        double minX = Math.ceil(model.min.x.get() - 1.5);
        double minY = + Math.ceil(model.min.y.get() - 1.5);

        double maxX = Math.floor(model.max.x.get() + 1.5);
        double maxY = Math.floor(model.max.y.get() + 1.5);

        double dX = maxX - minX;
        double dY = maxY - minY;

        double step = 1;

        while (Math.min(dX / step, dY / step) > 3) {
            step *= 10;
        }
        while (Math.max(dX / step, dY / step) < 3) {
            step /= 10;
        }

        minX = Math.floor(minX / step) * step;
        minY = Math.floor(minY / step) * step;

        maxX = Math.ceil(maxX / step) * step;
        maxY = Math.ceil(maxY / step) * step;

        for (double x = minX; x <= maxX; x += step) {
            Color color = Math.round(x / step) % 100 == 0 ? Color.BLACK : (Math.round(x / step) % 10 == 0 ? Color.GRAY : Color.LIGHTGRAY);
            getChildren().add(createLine3D((float) x, (float) minY, 0.01f, (float) x, (float) maxY, 0.01f, color));
        }

        for (double y = minY; y <= maxY; y += step) {
            Color color = Math.round(y / step) % 100 == 0 ? Color.BLACK : (Math.round(y / step) % 10 == 0 ? Color.GRAY : Color.LIGHTGRAY);
            getChildren().add(createLine3D((float) minX, (float) y, 0.01f, (float) maxX, (float) y, 0.01f, color));
        }
    }
	
	private MeshView createLine3D(float sx, float sy, float sz, float ex, float ey, float ez, Color color) {		
        TriangleMesh mesh = new TriangleMesh();

		mesh.getPoints().addAll(sx, sy, sz, ex, ey, ez);
		mesh.getTexCoords().addAll(0, 0, 1, 1);
		mesh.getFaces().addAll(0, 1, 0, 0, 1, 0);

        MeshView view = new MeshView(mesh);
        
		view.setMaterial(new PhongMaterial(color));
		view.setCullFace(CullFace.NONE);
		view.setDrawMode(DrawMode.LINE);

        return view;
	}

}
