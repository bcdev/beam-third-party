package uk.ac.ucl.mssl.climatephysics.stereomatcher;

import java.awt.geom.Point2D;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ucl.mssl.climatephysics.stereomatcher.DenseDisparitySetGenerator;
import uk.ac.ucl.mssl.climatephysics.stereomatcher.DisparitySetGenerator;

public class DenseDisparitySetGeneratorTest extends TestCase {
	public void testConstruction() {
		DisparitySetGenerator generator = new DenseDisparitySetGenerator(-5f, 5f, -10f, 10f);
		List<Point2D.Float> disps = generator.generate();
		Assert.assertTrue(disps.size() == 11 * 21);
		
	}
}
