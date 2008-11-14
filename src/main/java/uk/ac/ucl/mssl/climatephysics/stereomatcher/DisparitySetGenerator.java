package uk.ac.ucl.mssl.climatephysics.stereomatcher;

import java.awt.geom.Point2D;
import java.util.List;

abstract public class DisparitySetGenerator {

	abstract public List<Point2D.Float> generate();

	public DisparitySetGenerator() {
		super();
	}

}