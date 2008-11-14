package uk.ac.ucl.mssl.climatephysics.imaging.test;

import javax.media.jai.KernelJAI;

import junit.framework.TestCase;
import uk.ac.ucl.mssl.climatephysics.imaging.GaussianKernelFixedSize;

public class KernelTest extends TestCase {
	
	public void printGaussianKernel(KernelJAI gaussianKernel) {
		float[] data = gaussianKernel.getKernelData();
		for (int y = 0; y < gaussianKernel.getHeight(); y++) {
			for (int x = 0; x < gaussianKernel.getWidth(); x++) {
				int pos = y * gaussianKernel.getWidth()  + x;
				System.out.format("%d %d %f ", x, y, data[pos]);
			}
			System.out.format("\n");
		}
	}
	
	public void testSimpleKernel() {
		KernelJAI gaussianKernel = new GaussianKernelFixedSize(21, 5.25f);
		printGaussianKernel(gaussianKernel);
	}
	
}
