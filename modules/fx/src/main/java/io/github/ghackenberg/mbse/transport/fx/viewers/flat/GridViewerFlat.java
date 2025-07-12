package io.github.ghackenberg.mbse.transport.fx.viewers.flat;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class GridViewerFlat extends Pane {

    // Fields

    public final DoubleProperty x = new SimpleDoubleProperty();
    public final DoubleProperty y = new SimpleDoubleProperty();
    public final DoubleProperty s = new SimpleDoubleProperty();

    public final DoubleProperty step = new SimpleDoubleProperty();

    public final Group layer1 = new Group();
    public final Group layer2 = new Group();
    public final Group layer3 = new Group();

    // Constructors

    public GridViewerFlat() {
        widthProperty().addListener(event -> updateGrid());
        heightProperty().addListener(event -> updateGrid());

        x.addListener(event -> updateGrid());
        y.addListener(event -> updateGrid());
        s.addListener(event -> updateGrid());

        getChildren().add(layer1);
        getChildren().add(layer2);
        getChildren().add(layer3);
    }

    // Methods

    public Point2D snap(Point2D point) {
        return snap(point.getX(), point.getY());
    }

    public Point2D snap(double x, double y) {
        double sx = Math.round(x / step.get()) * step.get();
        double sy = Math.round(y / step.get()) * step.get();

        return new Point2D(sx, sy);
    }

    private void updateGrid() {
        layer1.getChildren().clear();
        layer2.getChildren().clear();
        layer3.getChildren().clear();

        // Get screen coordinates

        double screenWidth = getWidth();
        double screenHeight = getHeight();

        double screenZeroX = this.x.get();
        double screenZeroY = this.y.get();

        double screenUnit = this.s.get();

        // Compute model coordinates

        double modelWidth = screenWidth / screenUnit;
        double modelHeight = screenHeight / screenUnit;

        double modelLeft = -screenZeroX / screenWidth * modelWidth;
        double modelTop = -screenZeroY / screenHeight * modelHeight;

        // Compute step

        double step = 1;

        while (step * screenUnit > 15) {
            step /= 10;
        }
        while (step * screenUnit < 5) {
            step *= 10;
        }

        // Add horizontal lines

        for (double modelX = Math.ceil(modelLeft / step) * step; modelX < modelLeft + modelWidth; modelX += step) {
            double screenX = modelX * screenUnit + screenZeroX;

            Line line = new Line(screenX, 0, screenX, screenHeight);

            if (Math.round(modelX / step) % 100 == 0) {
                line.setStroke(Color.BLACK);
                layer3.getChildren().add(line);
            } else if (Math.round(modelX / step) % 10 == 0) {
                line.setStroke(Color.GRAY);
                layer2.getChildren().add(line);
            } else {
                line.setStroke(Color.LIGHTGRAY);
                layer1.getChildren().add(line);
            }
        }

        // Add vertical lines

        for (double modelY = Math.ceil(modelTop / step) * step; modelY < modelTop + modelHeight; modelY += step) {
            double screenY = modelY * screenUnit + screenZeroY;

            Line line = new Line(0, screenY, screenWidth, screenY);

            if (Math.round(modelY / step) % 100 == 0) {
                line.setStroke(Color.BLACK);
                layer3.getChildren().add(line);
            } else if (Math.round(modelY / step) % 10 == 0) {
                line.setStroke(Color.GRAY);
                layer2.getChildren().add(line);
            } else {
                line.setStroke(Color.LIGHTGRAY);
                layer1.getChildren().add(line);
            }
        }

        // Propagate step

        this.step.set(step);
    }

}
