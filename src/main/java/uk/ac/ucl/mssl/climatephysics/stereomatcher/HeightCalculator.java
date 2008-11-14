package uk.ac.ucl.mssl.climatephysics.stereomatcher;

public class HeightCalculator {
	
	double tangentDifferences[];
	double minimumHeight;
	double maximumHeight;
	/**
	 * shift retrieved disparities by value of disparityOffset -- used to compensate for
	 * errors in image registration
	 */
	double disparityOffset;
	double nullValueInInput;
	double nullValueInOutput;
	
	
	public HeightCalculator(CameraModel cameraModel, double minHeight, double maxHeight, double disparityOffset, double nullValueInInput, double nullValueInOutput) {
		this.tangentDifferences = cameraModel.getTangentDifferences();
		this.minimumHeight = minHeight;
		this.maximumHeight = maxHeight;
		this.nullValueInInput = nullValueInInput;
		this.nullValueInOutput = nullValueInOutput;
	}
	
	public double[] disparityToHeight(double[] disparityLine, 
			float disparityOffset, CameraModel cameraModel){
		double[] result = new double[disparityLine.length]; 
		double[] tangentDifference = cameraModel.getTangentDifferences();
		for (int i = 0; i < disparityLine.length; i++){
			result[i] = disparityLine[i] * (-1000.0d) / tangentDifference[i];
		}
		return result;
	}
	public double getHeight(int position, float disparity, double elevation){
		if (disparity == nullValueInInput){
			return nullValueInOutput;
		}
		double height = ((disparity + disparityOffset) * (-1000.0)) / tangentDifferences[position];
		if (height <= 0){
			return nullValueInOutput;
		}
		if (height - elevation < minimumHeight){
			return nullValueInOutput;
		} 
		if (height > maximumHeight){
			return nullValueInOutput;
		}
		return height;
	}
}
