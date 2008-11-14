package uk.ac.ucl.mssl.climatephysics.stereomatcher;

import uk.ac.ucl.mssl.climatephysics.utilities.Interpolation;

public class MannsteinATSRCameraModel extends CameraModel {

	final private double earthRadius = 6371.0f;
	final private double earthRadiusSq = earthRadius * earthRadius;
	final private double orbitRadius = 7153.0f;
	final private double orbitRadiusSq = orbitRadius * orbitRadius;
	final private int n = 1000;
	final private int size = 512;
	final private double coneHalfAngle;

	private double[] tangentNadir;
	private double[] tangentForward;
	
	/**
	 * angular distances of pixel from nadir point as seen from centre of earth
	 * @return double[] array of angular distances
	 */
	public double[] angularDistances() {
		double[] result = new double[size]; 
		for (int i =0; i < size; ++i){
			result[i] = (i - (((double)size -1) / 2)) / earthRadius;
		}
		return result;
	}
	
	public double[] parameterisedRotationAngle(){
		double result[] = new double[n];
		for (int i = 0; i < n; ++i){
			result[i] = (double)i * 2.0d * Math.PI / (double)n;
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
			c[i] = Math.asin(orbitRadius / earthRadius * Math.sin(alpha[i])) - alpha[i];
			d[i] = Math.sqrt(orbitRadiusSq + earthRadiusSq - (2.0d * orbitRadius * earthRadius * Math.cos(c[i])));
			a[i] = Math.asin(d[i] / earthRadius * Math.sin(gamma[i]));
			b1[i] = Math.acos(Math.cos(c[i]) / Math.cos(a[i]));
			result[i] = delta[i] + b1[i];
		}
		
		return result;
	}
	
	public void computeAngles(){
		
		// TODO this is all fairly ugly and taken straight from the IDL 
		// TODO implementation, it needs some cleanup, clarification
		// TODO and testing
		double[] eps = parameterisedRotationAngle();
		double[] delta = new double[n];
		double[] gamma = new double[n];
		double[] alpha = new double[n];
		double[] c = new double[n];
		double[] d = new double[n];
		double[] a = new double[n];
		                       
		for (int i=0; i < n; i++){
			delta[i] = coneHalfAngle * (1.0d - Math.cos(eps[i]));
			gamma[i] = coneHalfAngle * Math.sin(eps[i]);
			alpha[i] = Math.acos(Math.cos(delta[i]) * Math.cos(gamma[i]));
			c[i] = Math.asin(Math.sin(alpha[i]) * orbitRadius / earthRadius) - alpha[i]; 
			d[i] = Math.sqrt(earthRadiusSq + orbitRadiusSq - 2.0d * earthRadius * orbitRadius * Math.cos(c[i]));
			a[i] = Math.asin(d[i] / earthRadius * Math.sin(gamma[i]));
		}
		
		// find min and max
		int minPos = 0;
		int maxPos = 0;
		Double minVal = Double.POSITIVE_INFINITY;
		Double maxVal = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < n; ++i){
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
		double[] aShifted = new double[n];
		double[] epsShifted  = new double[n];
		for (int i = 0; i < n; ++i){
			int pos = (maxPos + i) % n;
			aShifted[pos] = a[i];
			epsShifted[pos] = eps[i];
		}
		double[] aShiftedSlice = new double[minPos - maxPos];
		double[] epsShiftedSlice = new double[minPos - maxPos];
		System.arraycopy(aShifted, 0, aShiftedSlice, 0, minPos - maxPos);
		System.arraycopy(epsShifted, 0, epsShiftedSlice, 0, minPos - maxPos);
		
		for (int i = 0; i < epsShiftedSlice.length; ++i){
			if (epsShiftedSlice[i] > Math.PI){
				epsShiftedSlice[i] -= 2 * Math.PI;
			}
		}
	
		double[] interpolatedNadir = new double[size];
		Interpolation interpol = new Interpolation(aShiftedSlice, epsShiftedSlice);
		double[] grid = angularDistances();
		for (int i = 0; i < interpolatedNadir.length; ++i){
			interpolatedNadir[i] = interpol.valueAt(grid[i]);
		}
		
		// compute forward angles
		int sizeForward = minPos +1 - maxPos;
		double[] aF = new double[sizeForward];
		double[] espF = new double[sizeForward];
		
		for (int i=0; i < sizeForward; i++){

			aF[sizeForward - i - 1] = a[maxPos + i];
			espF[sizeForward - i -1] = eps[maxPos + i];
		}

		double[] interpolatedForward = new double[size];
		interpol = new Interpolation(aF, espF);
		for (int i =0; i < interpolatedForward.length; i++){
			interpolatedForward[i] = interpol.valueAt(grid[i]);
		}
		tangentForward = translateToAngles(interpolatedForward);
		tangentNadir = translateToAngles(interpolatedNadir);
	}
	
	public MannsteinATSRCameraModel(double coneHalfAngle) {
		System.out.println(coneHalfAngle);
		this.coneHalfAngle = coneHalfAngle;
		computeAngles();
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
}
