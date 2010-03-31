package uk.ac.ucl.mssl.climatephysics.utilities;

import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ucl.mssl.climatephysics.utilities.ArrayArgSort;

public class ArrayArgSortTest extends TestCase {
	
	public void testSimple(){
		double[] data = {0d, 1d, 2d, 3d, 4d};
		Integer[] indices = new ArrayArgSort(data).indices();
		Assert.assertTrue(data.length == indices.length);
		for (int i = 0; i < indices.length; ++i) {
			Assert.assertTrue(indices[i] == data[i]);
		}
	}
	public void testSimple2(){
		double[] data = {5d, 4d, 3d, 2d, 1d, 0d};
		Integer[] indices = new ArrayArgSort(data).indices();
		Assert.assertTrue(data.length == indices.length);
		
		Assert.assertTrue(data[indices[0]] == 0d);
		Assert.assertTrue(data[indices[5]] == 5d);
	}
	
	public void testDuplicates() {
		double[] data = {5d, 5d, 4d, 3d, 2d, 1d, 0d};
		Integer[] indices = new ArrayArgSort(data).indices();
		Assert.assertTrue(data.length == indices.length);
		Assert.assertTrue(data[indices[0]] == 0.0);
		Assert.assertTrue(data[indices[5]] == 5.0);
		Assert.assertTrue(data[indices[6]] == 5.0);
	}
	
	public void testRandom() {
		double[] data = {5d, 2d, -1d, -22.3d, -9101d, 1000101d, 5d, 4d, 3d, 2d, 1d, 0d};
		Integer[] indices = new ArrayArgSort(data).indices();
		Assert.assertTrue(data.length == indices.length);
		for (int i = 0; i < indices.length; ++i) {
			System.out.print(indices[i] +  " " + data[i] + "\n");
		}	
		Assert.assertTrue(data[indices[0]] == -9101d);
		Assert.assertTrue(data[indices[1]] == -22.3d);
	}
	
	public void testSimpleReversed(){
		double[] data = {0d, 1d, 2d, 3d, 4d};
		Integer[] indices = new ArrayArgSort(data).indicesReversed();
		Assert.assertTrue(data.length == indices.length);
		for (int i = 0; i < indices.length; ++i) {
			Assert.assertTrue(indices[i] == data[data.length - i -1]);
		}
	}
	public void testSimple2Reversed(){
		double[] data = {5d, 4d, 3d, 2d, 1d, 0d};
		Integer[] indices = new ArrayArgSort(data).indicesReversed();
		Assert.assertTrue(data.length == indices.length);
		
		Assert.assertTrue(data[indices[0]] == 5d);
		Assert.assertTrue(data[indices[5]] == 0d);
	}
	
	
}
