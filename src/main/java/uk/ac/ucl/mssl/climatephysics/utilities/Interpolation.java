package uk.ac.ucl.mssl.climatephysics.utilities;

/**
 * 
 * @author Ludwig Brinckmann
 * Crappy and slow interpolation routine, that requires x arrays to 
 * be monotonally increasing. If someone can point me to a better 
 * standard routine, please do so.
 * 
 */
public class Interpolation {
	private double[] x;
	private double[] y;

	public Interpolation(final double[] x,final double[] y) {
		this.x = x;
		this.y = y;
	}

	private double valueAt(int x0, int x1, double u){
		return y[x0] + ((u - x[x0]) * (y[x1] - y[x0]) / (x1 -x0));
	}
	
	public double valueAt(double u) {
		int pos = 0;
		// TODO binary search
		for (int i=0; i < x.length; i++){
			if (x[i] < u) {
				pos = i;
			} else {
				break;
			}
		}
		return valueAt(pos, pos +1, u);
	}
}

