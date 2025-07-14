package io.github.ghackenberg.mbse.transport.core.entities;

import io.github.ghackenberg.mbse.transport.core.structures.Vector;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Camera {

    // Structures

    // - Controlled

    public final Vector base = new Vector(0, 0, 0);

    public final DoubleProperty distance = new SimpleDoubleProperty(30);
    public final DoubleProperty azimuth = new SimpleDoubleProperty(0);
    public final DoubleProperty zenith = new SimpleDoubleProperty(60);

    public final DoubleProperty near = new SimpleDoubleProperty(0.01);
    public final DoubleProperty far = new SimpleDoubleProperty(100);

    // - Computed

    private final Translate innerTranslate = new Translate();
    private final Translate outerTranslate = new Translate();

    private final Rotate azimuthRotate = new Rotate();
    private final Rotate zenithRotate = new Rotate();

    public final Vector up = new Vector();
    public final Vector eye = new Vector();
    public final Vector direction = new Vector();
    public final Vector left = new Vector();

    // Constructors

    public Camera() {
        try {
            innerTranslate.zProperty().bind(distance.negate());
            innerTranslate.zProperty().addListener(event -> recompute());

            outerTranslate.xProperty().bind(base.x);
            outerTranslate.xProperty().addListener(event -> recompute());
            outerTranslate.yProperty().bind(base.y);
            outerTranslate.yProperty().addListener(event -> recompute());
            outerTranslate.zProperty().bind(base.z);
            outerTranslate.zProperty().addListener(event -> recompute());

            azimuthRotate.axisProperty().set(Rotate.Z_AXIS);
            azimuthRotate.angleProperty().bind(azimuth);
            azimuthRotate.angleProperty().addListener(event -> {
                try {
                    zenithRotate.setAxis(azimuthRotate.inverseTransform(Rotate.X_AXIS));
                } catch (NonInvertibleTransformException e) {
                    throw new IllegalStateException(e);
                }
            });

            zenithRotate.axisProperty().set(azimuthRotate.inverseTransform(Rotate.X_AXIS));
            zenithRotate.axisProperty().addListener(event -> recompute());
            zenithRotate.angleProperty().bind(zenith);
            zenithRotate.angleProperty().addListener(event -> recompute());

            recompute();
        } catch (NonInvertibleTransformException e) {
            throw new IllegalStateException(e);
        }
    }

    // Methods

    public void apply(Node node) {
        node.getTransforms().add(outerTranslate);
        node.getTransforms().add(azimuthRotate);
        node.getTransforms().add(zenithRotate);
        node.getTransforms().add(innerTranslate);
    }

    private void recompute() {
        // Compute up
        Point3D up = new Point3D(0, 0, 1);
        zenithRotate.transform(up);
        azimuthRotate.transform(up);

        this.up.assign(up);

        // Compute eye
        Point3D eye = new Point3D(0, 0, 0);
        
        eye = innerTranslate.transform(eye);
        eye = zenithRotate.transform(eye);
        eye = azimuthRotate.transform(eye);
        eye = outerTranslate.transform(eye);

        this.eye.assign(eye);

        // Compute direction
        direction.assign(base.toPoint3D().subtract(eye).normalize());

        // Compute left
        left.assign(direction.toPoint3D().crossProduct(up));
    }

}
