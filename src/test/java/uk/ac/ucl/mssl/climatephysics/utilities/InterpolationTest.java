package uk.ac.ucl.mssl.climatephysics.utilities;

import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ucl.mssl.climatephysics.utilities.Interpolation;

public class InterpolationTest extends TestCase {
	public void test1(){
		double[] x = {0.0, 1.0};
		double[] y = {0.0, 1.0};
		Interpolation interpol = new Interpolation(x, y);
		Assert.assertEquals(0.0, interpol.valueAt(0.0), 0.0000001);
		Assert.assertEquals(1.0, interpol.valueAt(1.0), 0.0000001);
		Assert.assertEquals(0.5, interpol.valueAt(0.5), 0.0000001);
		Assert.assertEquals(0.9999, interpol.valueAt(0.9999), 0.0000001);
	}
	
	public void test2(){
		double[] x = {0.0, 1.0, 2.0};
		double[] y = {0.0, 1.0, 2.0};
		Interpolation interpol = new Interpolation(x, y);
		Assert.assertEquals(0.0, interpol.valueAt(0.0), 0.0000001);
		Assert.assertEquals(1.0, interpol.valueAt(1.0), 0.0000001);
		Assert.assertEquals(1.5, interpol.valueAt(1.5), 0.0000001);
		Assert.assertEquals(0.9999, interpol.valueAt(0.9999), 0.0000001);
	}
	
	public void test3(){
		double[] x = {-2.0, -1.0, 0.0};
		double[] y = {2.0, 1.0, 0.0};
		Interpolation interpol = new Interpolation(x, y);
		Assert.assertEquals(0.0, interpol.valueAt(0.0), 0.0000001);
		Assert.assertEquals(1.0, interpol.valueAt(-1.0), 0.0000001);
		Assert.assertEquals(0.5, interpol.valueAt(-0.5), 0.0000001);
		Assert.assertEquals(0.9999, interpol.valueAt(-0.9999), 0.0000001);
	}

	public void test4(){
		double[] x = {-2.0, -1.0, 0.0};
		double[] y = {0.0, 10.0, 100.0};
		Interpolation interpol = new Interpolation(x, y);
		Assert.assertEquals(100.0, interpol.valueAt(0.0), 0.0000001);
		Assert.assertEquals(10.0, interpol.valueAt(-1.0), 0.0000001);
		Assert.assertEquals(55.0, interpol.valueAt(-0.5), 0.0000001);
		Assert.assertEquals(10.009, interpol.valueAt(-0.9999), 0.0000001);
	}
	
}
