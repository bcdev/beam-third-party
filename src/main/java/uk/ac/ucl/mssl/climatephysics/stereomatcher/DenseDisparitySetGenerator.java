package uk.ac.ucl.mssl.climatephysics.stereomatcher;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class DenseDisparitySetGenerator implements DisparitySetGenerator {

    private final List<Point2D.Float> disparitySet;

    public DenseDisparitySetGenerator(float minX, float maxX, float minY, float maxY) {
        disparitySet = new ArrayList<Point2D.Float>();
        for (float x = minX; x <= maxX; ++x) {
            for (float y = minY; y <= maxY; y++) {
                disparitySet.add(new Point2D.Float(x, y));
            }
        }
    }

    public List<Point2D.Float> generate() {
        return disparitySet;
    }
}