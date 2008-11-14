package uk.ac.ucl.mssl.climatephysics.beam.stereomatcher;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.SourceProduct;

import uk.ac.ucl.mssl.climatephysics.stereomatcher.CorrelationShiftDisparitySetGenerator;
import uk.ac.ucl.mssl.climatephysics.stereomatcher.DisparitySetGenerator;

@OperatorMetadata(alias="M4StereoMatcher", description="Stereo matches using M4 Algorithm")
public class M4StereoMatcher extends M5StereoMatcher
{

	@SourceProduct(alias="source")
	private Product sourceProductM4;

	@SourceProduct(alias="filter")
	private Product filterProductM4;

	public M4StereoMatcher(){
		super();
	}

	@Override
	public void initialize() throws OperatorException {
		//TODO temp hack to get it working while operator inheritance does not
		// work
		sourceProduct = sourceProductM4;
		filterProduct = filterProductM4;
		super.initialize();
	}
	
	
	protected synchronized List<Point2D.Float> generateDisparitySet(Rectangle targetRectangle){

		System.out.println("Generating disp set for " + targetRectangle);
		RenderedImage referenceImage = referenceBand.getSourceImage();
		RenderedImage comparisonImage = comparisonBand.getSourceImage();

		// TODO remove hardcoding of 512
		float tiledimension = 512.0f;

		if (referenceImage.getHeight() < tiledimension){
			throw new IllegalArgumentException("The image must have a minimum height of " + tiledimension);
		}

		// TODO allow for tiling across track
		float minX = 0.0f;
		// usually we want the tile to start at the beginning of the rectangle, 
		// but if we are at the end of an orbit, we shift the tile up so that
		// we still get full tile at the end. 
		// Another solution would be to change to a dense disparity set instead
		float minY = (float)targetRectangle.getMinY();
		if ((minY + tiledimension) > referenceImage.getHeight()){
			minY = referenceImage.getHeight() - tiledimension;
		}

		System.out.println("Cropping for disp " + minX + " " + minY);

		// crop to rectangle for the scene investigated
		ParameterBlock pbCropReference = new ParameterBlock();
		pbCropReference.addSource(referenceImage);
		pbCropReference.add(minX);
		pbCropReference.add(minY);
		pbCropReference.add(tiledimension);
		pbCropReference.add(tiledimension);
		RenderedOp reference = JAI.create("crop", pbCropReference);		

		ParameterBlock pbCropComparison = new ParameterBlock();
		pbCropComparison.addSource(comparisonImage);
		pbCropComparison.add(minX);
		pbCropComparison.add(minY);
		pbCropComparison.add(tiledimension);
		pbCropComparison.add(tiledimension);
		RenderedOp comparison = JAI.create("crop", pbCropReference);		

		DisparitySetGenerator generator = new CorrelationShiftDisparitySetGenerator(reference, comparison, 
				searchWindowMinX, searchWindowMaxX, searchWindowMinX, searchWindowMaxY, 500);
		return generator.generate();

	}

	
	public static class Spi extends OperatorSpi {
		public Spi() {
			super(M4StereoMatcher.class);
		}
	}

}
