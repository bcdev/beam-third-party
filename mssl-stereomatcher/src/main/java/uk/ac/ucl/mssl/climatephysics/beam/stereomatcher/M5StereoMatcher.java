package uk.ac.ucl.mssl.climatephysics.beam.stereomatcher;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.List;
import java.util.Map;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.RenderedOp;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.Tile;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.annotations.TargetProduct;
import org.esa.beam.util.ProductUtils;

import uk.ac.ucl.mssl.climatephysics.imaging.GaussianKernelFixedSize;
import uk.ac.ucl.mssl.climatephysics.stereomatcher.DenseDisparitySetGenerator;
import uk.ac.ucl.mssl.climatephysics.stereomatcher.DisparitySetGenerator;

import com.bc.ceres.core.ProgressMonitor;

@OperatorMetadata(alias="M5StereoMatcher", 
                  description="Stereo matches using M5 Algorithm")
public class M5StereoMatcher extends Operator {


	@SourceProduct(alias="source")
	protected Product sourceProduct;

	@SourceProduct(alias="filter")
	protected Product filterProduct;

	@TargetProduct
	protected Product targetProduct;

	@Parameter(alias="applyFilter", defaultValue="false", description="Apply filter")
	protected boolean applyFilter;
	
	@Parameter(alias="filterBandName", defaultValue="filter", description="Name of filter band")
	protected String filterBandName;

	@Parameter(alias="referenceBandName", defaultValue="referenceNormalised", description="Name of reference band")
	protected String referenceBandName;

	@Parameter(alias="comparisonBandName", defaultValue="comparisonNormalised", description="Name of comparison band")
	protected String comparisonBandName;

	@Parameter(alias="searchWindowMinX", defaultValue="-15", description="Minimum value for search window horizontally")
	protected int searchWindowMinX;

	@Parameter(alias="searchWindowMaxX", defaultValue="15", description="Maximum value for search window horizontally")
	protected int searchWindowMaxX;

	@Parameter(alias="searchWindowMinY", defaultValue="-30", description="Minimum value for search window vertically")
	protected int searchWindowMinY;

	@Parameter(alias="searchWindowMaxY", defaultValue="30", description="Maximum value for search window vertically")
	protected int searchWindowMaxY;

	@Parameter(alias="noDataValue", defaultValue="-999d", description="No data value to embed in images")
	protected double noDataValue;

	// TODO consider whether there is a point to use
	// TODO the window size across track to account for edge effects.

	@SuppressWarnings("unused")
	private int searchWindowXSize;
	private int searchWindowYSize;

	// input bands
	protected Band referenceBand;
	protected Band comparisonBand;
	private Band filterBand;

	// output bands
	private Band yDisparities;
	private Band xDisparities;
	private Band quality;

	private static final int BORDER_WIDTH = 11;
	// TODO compute value
	private static final int EDGE_AFFECTED = 31;

	public M5StereoMatcher(){
		super();
	}

	@Override
	public void initialize() throws OperatorException {

		searchWindowXSize = Math.abs(searchWindowMinX) + Math.abs(searchWindowMaxX);
		searchWindowYSize = Math.abs(searchWindowMinY) + Math.abs(searchWindowMaxY);
		
		assert null != sourceProduct: "Source Product Missing";
	
		int rasterWidth = sourceProduct.getSceneRasterWidth();
		int rasterHeight = sourceProduct.getSceneRasterHeight();

		// load the filter band if filtering is set to true
		// the input always asks for a filter and there is no way to make it 
		// optional at the moment, so the filtering is turned on or off with
		// the separate applyFilter variable. If filtering is turned off, 
		// any name for the filterProduct is ignored
		if (applyFilter == true && null != filterProduct) {
			filterBand = filterProduct.getBand(filterBandName);
			if (null == filterBand){
				throw new OperatorException("Filter band " + filterBandName + " missing in filter source product");
			}
		} else {
			filterBand = null;
		}
		
		referenceBand = sourceProduct.getBand(referenceBandName);
		if (null == referenceBand){
			throw new OperatorException("Reference band " + referenceBandName + " missing in source product");
		}
		comparisonBand = sourceProduct.getBand(comparisonBandName);
		if (null == comparisonBand){
			throw new OperatorException("Comparison band " + comparisonBandName + " missing in source product");
		}
		targetProduct = new Product("MSSL_StereoMatched", "MSSL_StereoMatched",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
		ProductUtils.copyMetadata(sourceProduct, targetProduct);

		yDisparities = targetProduct.addBand("YDisparities", ProductData.TYPE_FLOAT32);
		yDisparities.setUnit("px");
		yDisparities.setNoDataValue(noDataValue);
		yDisparities.setNoDataValueUsed(true);
		yDisparities.setDescription("Along-track Disparities");

		xDisparities = targetProduct.addBand("XDisparities", ProductData.TYPE_FLOAT32); 
		xDisparities.setUnit("px");
		xDisparities.setNoDataValue(noDataValue);
		xDisparities.setNoDataValueUsed(true);
		xDisparities.setDescription("Across-track Disparities");

		quality = targetProduct.addBand("Quality", ProductData.TYPE_FLOAT64); 
		quality.setNoDataValue(noDataValue);
		quality.setNoDataValueUsed(true);
		quality.setDescription("Match Quality Indicator");

		// no point trying to do the sides, but we need overlapping
		// on the tiles when doing an orbit
		//targetProduct.setPreferredTileSize(512,512 - 2 * borderWidth);
		targetProduct.setPreferredTileSize(512,512);
		setTargetProduct(targetProduct);
	}

	

	protected synchronized List<Point2D.Float> generateDisparitySet(Rectangle targetRectangle){
		DisparitySetGenerator generator = new DenseDisparitySetGenerator(
				(float) searchWindowMinX, (float)searchWindowMaxX, 
				(float)searchWindowMinY, (float)searchWindowMaxY);
		return generator.generate();
	}


	protected boolean filterTileAllNull(Tile filter) {
		for (int x = filter.getMinX(); x <= filter.getMaxX(); x++){
			for (int y = filter.getMinY(); y <= filter.getMaxY(); y++) {
				if (filter.getSampleInt(x, y) != filterBand.getNoDataValue()) {
					return false;
				}
			}
		}
		return true;
	}

	protected void setTileToNull(Tile target){
		for (int x = target.getMinX(); x <= target.getMaxX(); x++){
			for (int y = target.getMinY(); y <= target.getMaxY(); y++) {
				target.setSample(x, y, noDataValue);
			}
		}
	}
	
	@Override
	public void computeTileStack(Map<Band, Tile> targetTiles,
			Rectangle targetRectangle,
			ProgressMonitor pm) throws OperatorException {

		//TODO leave these in, somehow it all fails when they are taken out.
		//TODO must be some side effect -- maybe pulling on JAI images
		@SuppressWarnings("unused")
		Tile referenceTile = getSourceTile(referenceBand, targetRectangle);
		@SuppressWarnings("unused")
		Tile comparisonTile = getSourceTile(comparisonBand, targetRectangle);

		Tile qualityTile = targetTiles.get(quality);
		Tile xDisparityTile = targetTiles.get(xDisparities);
		Tile yDisparityTile = targetTiles.get(yDisparities);

		Tile filterTile = null;
		if (null != filterBand){
			filterTile = getSourceTile(filterBand, targetRectangle);
			if (filterTileAllNull(filterTile)){
				setTileToNull(qualityTile);
				setTileToNull(xDisparityTile);
				setTileToNull(yDisparityTile);
				return;
			}
		}

		List<Point2D.Float> disparitySet = generateDisparitySet(targetRectangle);

		// getting to the image tile in JAI accessible format means getting
		// the image and then cropping to the desired location

		RenderedImage referenceImage = referenceBand.getSourceImage();
		RenderedImage comparisonImage = comparisonBand.getSourceImage();

		ParameterBlock pbCropReference = new ParameterBlock();
		pbCropReference.addSource(referenceImage);
		float minX = (float)Math.max(targetRectangle.getMinX() - BORDER_WIDTH, referenceImage.getMinX());
		float minY = (float)Math.max(targetRectangle.getMinY() - BORDER_WIDTH - Math.abs(searchWindowMinY), referenceImage.getMinY());
		float width = (float)Math.min(targetRectangle.getWidth() + BORDER_WIDTH * 2,
				referenceImage.getMinX() + referenceImage.getWidth() - minX);
		float height = (float)Math.min(targetRectangle.getHeight() + BORDER_WIDTH*2 + searchWindowYSize, 
				referenceImage.getMinY() + referenceImage.getHeight() - minY);

		pbCropReference.add(minX);
		pbCropReference.add(minY);
		pbCropReference.add(width);
		pbCropReference.add(height);
		RenderedOp croppedReferenceImage = JAI.create("Crop", pbCropReference, null);
		ParameterBlock pbCropComparison = new ParameterBlock();
		pbCropComparison.addSource(comparisonImage);
		pbCropComparison.add(minX);
		pbCropComparison.add(minY);
		pbCropComparison.add(width);
		pbCropComparison.add(height);
		RenderedOp croppedComparisonImage = JAI.create("Crop", pbCropComparison, null);

		// TODO factor out stereo matching to separate it from BEAM
		// TODO it should just be a stand-alone operation using JAI
		// TODO solve the problem with the tiling: this will just repeat 
		// TODO operations for the whole image.	

		pm.setTaskName("Stereo matching tile " + targetRectangle);
		for (Point2D.Float disparity : disparitySet) {
			pm.setSubTaskName("Computing disparity for tile " + targetRectangle + " at x/y" + disparity.x + " " + disparity.y);
            if (pm.isCanceled()) {
                return;
            }
            pm.worked(1);

            if (Math.abs(disparity.y) >= targetRectangle.getHeight()){
				//System.out.println("Not enough data for disparity x/y" + disparity.x + " " + disparity.y);
				continue;
			}
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(croppedComparisonImage);
			pb.add(disparity.x);
			pb.add(disparity.y);
			RenderedOp comparisonShifted = JAI.create("translate", pb, null);

			ParameterBlock pb2 = new ParameterBlock();
			pb2.addSource(croppedReferenceImage);
			pb2.addSource(comparisonShifted);
			RenderedOp difference = JAI.create("subtract", pb2, null);

			ParameterBlock pb3 = new ParameterBlock();
			pb3.addSource(difference);
			RenderedOp absolute = JAI.create("absolute", pb3);

			ParameterBlock pb4 = new ParameterBlock();
			pb4.addSource(absolute);
			KernelJAI kernel = new GaussianKernelFixedSize(11, 5.25f);
			pb4.add(kernel);
			RenderedOp sigma = JAI.create("convolve", pb4, null);

			// TODO this implements a crude loop to do an overlay. Better implement
			// TODO this as a JAI operator or use the composite operator with alpha 
			// TODO channels.
			Raster sigmaTile = sigma.getData(targetRectangle);

			for (int y = (qualityTile.getMinY()); y <= qualityTile.getMaxY(); y++) {
				for(int x = qualityTile.getMinX(); x <= qualityTile.getMaxX(); x++) {				
					if (x < EDGE_AFFECTED || (qualityTile.getMaxX() - x) < EDGE_AFFECTED || 
						(filterTile != null && filterTile.getSampleInt(x, y) == filterBand.getNoDataValue())){
						// areas affected by edge effects or filtered out => set to null
						qualityTile.setSample(x, y, noDataValue);
						xDisparityTile.setSample(x, y, noDataValue);
						yDisparityTile.setSample(x, y, noDataValue);
					} 
					else {
						// valid data: now check if we have a better match
						double qualitySoFar = qualityTile.getSampleDouble(x, y);
						if  (qualitySoFar == 0.0d || sigmaTile.getSampleDouble(x, y, 0) <= qualitySoFar) {
							// better match => save data
							qualityTile.setSample(x, y, sigmaTile.getSampleDouble(x, y, 0));
							xDisparityTile.setSample(x, y, disparity.x);
							yDisparityTile.setSample(x, y, disparity.y);
						}
					}
				}
			}	
		}
	}
	public static class Spi extends OperatorSpi {
		public Spi() {
			super(M5StereoMatcher.class);
		}
	}
}
