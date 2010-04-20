package uk.ac.ucl.mssl.climatephysics.imaging;

import java.awt.geom.Point2D;

import javax.media.jai.KernelJAI;

public class GaussianKernelFixedSize extends KernelJAI {
	static final long serialVersionUID = 2204440659701736426L;
	
	protected float[] computeData(int size, float sigma){
		final double eds2p = 1.0 / Math.sqrt(2.0 * Math.PI);
		float[] data = new float[size * size];
		Point2D centre = new Point2D.Float(size /2, size /2);
		// for normalisation
		double total = 0.0d; 
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				double euclidianDistance =  centre.distance(new Point2D.Float(x, y));
				double temp = -1.0 * (euclidianDistance * euclidianDistance) / (2.0 * sigma * sigma);
				double value = (eds2p / sigma * Math.exp(temp));
				total += value;
				data[x * size + y] = (float)value;
			}
		}
		// normalise
		for (int x = 0; x < data.length; x++){
			data[x] /= total;
		}
		return data;
	}
	
	public GaussianKernelFixedSize(int size, float sigma) {
		super(size, size, new float[size * size]);
		super.data = computeData(size, sigma);
	}
}
