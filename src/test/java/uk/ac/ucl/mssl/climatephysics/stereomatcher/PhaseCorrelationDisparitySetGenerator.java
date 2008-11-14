package uk.ac.ucl.mssl.climatephysics.stereomatcher;

import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.util.List;

import javax.media.jai.JAI;

import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ucl.mssl.climatephysics.stereomatcher.CorrelationShiftDisparitySetGenerator;
import uk.ac.ucl.mssl.climatephysics.stereomatcher.DisparitySetGenerator;

public class PhaseCorrelationDisparitySetGenerator extends TestCase {

	String path;
	RenderedImage reference;
	RenderedImage comparison;
	
	protected void setUp() throws Exception {
		super.setUp();
		path = "C:/Users/Ludwig/Documents/phd/data/test";
		reference = JAI.create("fileload", path + "/nadir.tif");
		comparison = JAI.create("fileload", path + "/forward.tif");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCorrelation(){
//		DisparitySetGenerator generator = new CorrelationShiftDisparitySetGenerator(reference, comparison);
//		List<Point2D.Float> disparitySet = generator.generate();
//		Assert.assertTrue(disparitySet.size() == 11 * 21);
	}
	
}
