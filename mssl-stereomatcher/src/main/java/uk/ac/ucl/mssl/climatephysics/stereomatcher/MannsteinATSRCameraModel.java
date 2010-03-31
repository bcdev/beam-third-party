package uk.ac.ucl.mssl.climatephysics.stereomatcher;

import uk.ac.ucl.mssl.climatephysics.utilities.Interpolation;

public class MannsteinATSRCameraModel implements CameraModel {

	private static final double EARTH_RADIUS = 6371.0f;
	private static final double EARTH_RADIUS_SQUARE = EARTH_RADIUS * EARTH_RADIUS;
	private static final double ORBIT_RADIUS = 7153.0f;
	private static final double ORBIT_RADIUS_SQUARE = ORBIT_RADIUS * ORBIT_RADIUS;
	private static final int N = 1000;
	private static final int SIZE = 512;
	private final double coneHalfAngle;

	private double[] tangentNadir;
	private double[] tangentForward;
	
	
	public MannsteinATSRCameraModel(double coneHalfAngle) {
    	this.coneHalfAngle = coneHalfAngle;
    	computeAngles();
    }

    /**
	 * angular distances of pixel from nadir point as seen from centre of earth
	 * @return double[] array of angular distances
	 */
	public double[] angularDistances() {
		double[] result = new double[SIZE]; 
		for (int i =0; i < SIZE; ++i){
			result[i] = (i - (((double)SIZE -1) / 2)) / EARTH_RADIUS;
		}
		return result;
	}
	
	public double[] parameterisedRotationAngle(){
		double result[] = new double[N];
		for (int i = 0; i < N; ++i){
			result[i] = (double)i * 2.0d * Math.PI / (double)N;
		}
		return result;
	}
	
	public double[] translateToAngles(double[] eps) {
		
		double[] delta = new double[eps.length];
		double[] gamma = new double[eps.length];
		double[] alpha = new double[eps.length];
		double[] c = new double[eps.length];
		double[] d = new double[eps.length];
		double[] a = new double[eps.length];
		double[] b1 = new double[eps.length];
		double[] result = new double[eps.length];
		for (int i=0; i < eps.length; ++i){
			delta[i] = coneHalfAngle * (1.0d - Math.cos(eps[i]));
			gamma[i] = coneHalfAngle * Math.sin(eps[i]);
			alpha[i] = Math.acos(Math.cos(delta[i])* Math.cos(gamma[i]));
			c[i] = Math.asin(ORBIT_RADIUS / EARTH_RADIUS * Math.sin(alpha[i])) - alpha[i];
			d[i] = Math.sqrt(ORBIT_RADIUS_SQUARE + EARTH_RADIUS_SQUARE - (2.0d * ORBIT_RADIUS * EARTH_RADIUS * Math.cos(c[i])));
			a[i] = Math.asin(d[i] / EARTH_RADIUS * Math.sin(gamma[i]));
			b1[i] = Math.acos(Math.cos(c[i]) / Math.cos(a[i]));
			result[i] = delta[i] + b1[i];
		}
		
		return result;
	}
	
	public double[] getTangentNadirAngles(){
		return tangentNadir;
	}
	public double[] getTangentForwardAngles() {
		return tangentForward;
	}
	
	public double[] getTangentDifferences() {
		double[] differences = new double[512];
		for (int i=0; i< 512; i++) {
				differences[i] = tangentForward[i] - tangentNadir[i];
		}
		return differences;
	}

    private void computeAngles(){
    	
    	// TODO this is all fairly ugly and taken straight from the IDL 
    	// TODO implementation, it needs some cleanup, clarification
    	// TODO and testing
    	double[] eps = parameterisedRotationAngle();
    	double[] delta = new double[N];
    	double[] gamma = new double[N];
    	double[] alpha = new double[N];
    	double[] c = new double[N];
    	double[] d = new double[N];
    	double[] a = new double[N];
    	                       
    	for (int i=0; i < N; i++){
    		delta[i] = coneHalfAngle * (1.0d - Math.cos(eps[i]));
    		gamma[i] = coneHalfAngle * Math.sin(eps[i]);
    		alpha[i] = Math.acos(Math.cos(delta[i]) * Math.cos(gamma[i]));
    		c[i] = Math.asin(Math.sin(alpha[i]) * ORBIT_RADIUS / EARTH_RADIUS) - alpha[i]; 
    		d[i] = Math.sqrt(EARTH_RADIUS_SQUARE + ORBIT_RADIUS_SQUARE - 2.0d * EARTH_RADIUS * ORBIT_RADIUS * Math.cos(c[i]));
    		a[i] = Math.asin(d[i] / EARTH_RADIUS * Math.sin(gamma[i]));
    	}
    	
    	// find min and max
    	int minPos = 0;
    	int maxPos = 0;
    	double minVal = Double.POSITIVE_INFINITY;
    	double maxVal = Double.NEGATIVE_INFINITY;
    	for (int i = 0; i < N; ++i){
    		if (a[i] < minVal) {
    			minPos = i;
    			minVal = a[i];
    		}
    		if (a[i] > maxVal) {
    			maxPos = i;
    			maxVal = a[i];
    		}
    	}
    	
    	// compute nadir angles
    	double[] aShifted = new double[N];
    	double[] epsShifted  = new double[N];
    	for (int i = 0; i < N; ++i){
    		int pos = (maxPos + i) % N;
    		aShifted[pos] = a[i];
    		epsShifted[pos] = eps[i];
    	}
    	int length = Math.abs(maxPos - minPos);
        double[] aShiftedSlice = new double[length];
    	double[] epsShiftedSlice = new double[length];
    	System.arraycopy(aShifted, 0, aShiftedSlice, 0, length);
    	System.arraycopy(epsShifted, 0, epsShiftedSlice, 0, length);
    	
    	for (int i = 0; i < epsShiftedSlice.length; ++i){
    		if (epsShiftedSlice[i] > Math.PI){
    			epsShiftedSlice[i] -= 2 * Math.PI;
    		}
    	}
    
    	double[] interpolatedNadir = new double[SIZE];
    	Interpolation interpol = new Interpolation(aShiftedSlice, epsShiftedSlice);
    	double[] grid = angularDistances();
    	for (int i = 0; i < interpolatedNadir.length; ++i){
    		interpolatedNadir[i] = interpol.valueAt(grid[i]);
    	}
    	
    	// compute forward angles
    	int sizeForward = Math.abs(maxPos+1 - minPos);
    	double[] aF = new double[sizeForward];
    	double[] espF = new double[sizeForward];
    	
    	for (int i=0; i < sizeForward; i++){
    
    		aF[sizeForward - i - 1] = a[maxPos + i];
    		espF[sizeForward - i -1] = eps[maxPos + i];
    	}
    
    	double[] interpolatedForward = new double[SIZE];
    	interpol = new Interpolation(aF, espF);
    	for (int i =0; i < interpolatedForward.length; i++){
    		interpolatedForward[i] = interpol.valueAt(grid[i]);
    	}
    	tangentForward = translateToAngles(interpolatedForward);
    	tangentNadir = translateToAngles(interpolatedNadir);
    }
}
