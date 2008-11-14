package uk.ac.ucl.mssl.climatephysics.stereomatcher;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.DFTDescriptor;
import javax.media.jai.operator.IDFTDescriptor;

import uk.ac.ucl.mssl.climatephysics.utilities.ArrayArgSort;

public class CorrelationShiftDisparitySetGenerator implements
		DisparitySetGenerator {

	private RenderedImage reference;
	private RenderedImage comparison;

	// disparity set properties
	private int maximumDisparities;
	private float minX;
	private float maxX;
	private float minY;
	private float maxY;
	
	public CorrelationShiftDisparitySetGenerator(RenderedImage reference, 
			RenderedImage comparison, float minX, float maxX, float minY,
			float maxY, int maximumDisparities) {
		validateInputImage(reference);
		validateInputImage(comparison);
		this.reference = reference;
		this.comparison = comparison;
		this.maximumDisparities = maximumDisparities;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}
	
	@Override
	public List<Float> generate() {

		System.out.println("reference " + this.reference.getData().getSampleDouble(255, 255, 0));
		
		System.out.println("Generating disparity set with max " + maximumDisparities);
		ParameterBlock pbDft1 = new ParameterBlock();
		pbDft1.addSource(this.reference);
		pbDft1.set(DFTDescriptor.SCALING_NONE,0);
		RenderedOp dft1 = JAI.create("DFT", pbDft1, null);
	
		ParameterBlock pbDft2 = new ParameterBlock();
		pbDft2.addSource(comparison);
		pbDft2.set(DFTDescriptor.SCALING_NONE,0);
		RenderedOp dft2 = JAI.create("DFT", pbDft2);

		ParameterBlock pbConjugate = new ParameterBlock();
		pbConjugate.addSource(dft2);
		RenderedOp conjugate = JAI.create("conjugate", pbConjugate);
		
		ParameterBlock pbMultiplied = new ParameterBlock();
		pbMultiplied.addSource(dft1);
		pbMultiplied.addSource(conjugate);
		RenderedOp multiply = JAI.create("multiplycomplex", pbMultiplied);
		
		ParameterBlock pbCrossCorrelation = new ParameterBlock();
		pbCrossCorrelation.addSource(multiply);
		pbCrossCorrelation.add(IDFTDescriptor.SCALING_DIMENSIONS);
		pbCrossCorrelation.add(IDFTDescriptor.COMPLEX_TO_COMPLEX);
		RenderedOp crossCorrelation = JAI.create("IDFT", pbCrossCorrelation);
		
		ParameterBlock pbPeriodicShift = new ParameterBlock();
		pbPeriodicShift.addSource(crossCorrelation);
		pbPeriodicShift.add(reference.getHeight() / 2);
		pbPeriodicShift.add(reference.getWidth() /2);
		RenderedOp periodicShift = JAI.create("periodicshift", pbPeriodicShift);
			
		ParameterBlock pbSmooth = new ParameterBlock();
		pbSmooth.addSource(periodicShift);
		pbSmooth.add(3);
		pbSmooth.add(3);
		RenderedOp smoothed = JAI.create("boxfilter", pbSmooth);
		
		Raster smoothedData = smoothed.getData();
		
		double[] buffer = new double[reference.getWidth() * reference.getHeight() * 2];
		// TODO find out real boundaries the -1 is wrong
		buffer = smoothedData.getPixels(reference.getMinX(), reference.getMinY(), reference.getWidth()-1, reference.getHeight()-1, buffer);

		Integer[] sorted = new ArrayArgSort(buffer).indicesReversed();
		
		System.out.println("sorted " + sorted);
		
		List<Point2D.Float> disparities = new ArrayList<Point2D.Float>();
		
		// TODO compute 95 percentile
		//double minCorrelationIndex = sorted[(int)(pixelNumber * 0.95)];

		int i = 0;
		// TODO done as while loop for further conditions later
		// TODO M4 has minCorr as additional cut off condition
		while (i <= maximumDisparities) {
			
			// translate into x and y disparities and shift them so they
			// are centered around 0
			float x = sorted[i] %  reference.getHeight();
			if (x > reference.getWidth() / 2){
				x -= reference.getWidth();
			}
			float y = sorted[i] / reference.getWidth();
			if (y > reference.getHeight() / 2){
				y -= reference.getHeight();
			}
			
			// test if disparity falls within specified bounds, otherwise drop it
			// this will eliminate spurious matches that are too far out to be 
			// considered true
			if (minX <= x && x <= maxX && minY <= y && y <= maxY){
				System.out.println(i + " Adding disparity " + x + " " + y);
				disparities.add(new Point2D.Float(x, y));
			} else {
				System.out.println(i + " Discarding disparity " + x + " " + y);
			}
			++i;
		}	
		return disparities;
	}
	
	private void validateInputImage(RenderedImage image){
		if (image.getHeight() != 512 || image.getWidth() != 512) {
			System.out.println("input image wrong size" + image.getHeight() + image.getWidth());
			throw new IllegalArgumentException("Image not of size 512 * 512");
		}
	}
}
