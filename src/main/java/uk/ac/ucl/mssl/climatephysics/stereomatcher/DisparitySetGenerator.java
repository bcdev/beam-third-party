package uk.ac.ucl.mssl.climatephysics.stereomatcher;

import java.awt.geom.Point2D;
import java.util.List;

public interface DisparitySetGenerator {
    List<Point2D.Float> generate();
}