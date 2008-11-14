package uk.ac.ucl.mssl.climatephysics.beam.stereomatcher;

import java.awt.Rectangle;
import java.util.Map;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.Tile;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.annotations.TargetProduct;
import org.esa.beam.util.ProductUtils;

import uk.ac.ucl.mssl.climatephysics.stereomatcher.HeightCalculator;
import uk.ac.ucl.mssl.climatephysics.stereomatcher.MannsteinATSRCameraModel;

import com.bc.ceres.core.ProgressMonitor;

@OperatorMetadata(alias="MannsteinCameraModel", description="Computes geometric height from parallax using Mannstein Camera Model")
public class MannsteinCameraModel extends Operator {


	@SourceProduct(alias="source")
	protected Product sourceProduct;

	@TargetProduct
	protected Product targetProduct;

	@Parameter(alias="yDisparityBandName", defaultValue="YDisparities", description="Name of band containing along-track disparities")
	protected String yDisparityBandName;

	@Parameter(alias="minimumCloudHeight", defaultValue="1000f", description="minimum cloud height to extract")
	protected float minimumCloudHeight;

	@Parameter(alias="maximumCloudHeight", defaultValue="20000f", description="maximum cloud height to extract")
	protected float maximumCloudHeight;

	@Parameter(alias="disparityOffset", defaultValue="-2.0d", description="shift disparities to compensate for image registration errors")
	protected double disparityOffset;

	@Parameter(alias="noDataValue", defaultValue="-999d", description="no data value to embed in images")
	protected double noDataValue;

	@Parameter(alias="coneHalfAngle", defaultValue="0.40913952", description="half angle of the ATSR scanning cone")
	protected double coneHalfAngle;
	
	
	// input bands
	private Band yDisparityBand;
	private TiePointGrid elevationTiePointGrid;

	// output bands
	private Band heights;
	
	private HeightCalculator heightCalculator;

	@Override
	public void initialize() throws OperatorException {

		System.out.println("Initialising Mannstein Camera Model");

		assert(null != sourceProduct);
		int rasterWidth = sourceProduct.getSceneRasterWidth();
		int rasterHeight = sourceProduct.getSceneRasterHeight();

		yDisparityBand = sourceProduct.getBand(yDisparityBandName);
		
		if (null == yDisparityBand){
			throw new IllegalArgumentException("Band " + yDisparityBandName + " missing in source product");
		}
		
		elevationTiePointGrid = sourceProduct.getTiePointGrid("altitude");

		targetProduct = new Product("MSSL_StereoCloudHeights", "MSSL_StereoCloudHeights",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
		ProductUtils.copyMetadata(sourceProduct, targetProduct);

		heights = new Band("CloudTopHeight", ProductData.TYPE_INT16, 
				rasterWidth, rasterHeight);
		heights.setUnit("m");
		heights.setDescription("Cloud Top Height");
		heights.setNoDataValue(noDataValue);
		targetProduct.addBand(heights);

		heightCalculator = new HeightCalculator(new MannsteinATSRCameraModel(coneHalfAngle), 
				minimumCloudHeight, maximumCloudHeight, disparityOffset, yDisparityBand.getNoDataValue(), noDataValue);

		setTargetProduct(targetProduct);
	}


	@Override
	public void computeTileStack(Map<Band, Tile> targetTiles,
			Rectangle targetRectangle,
			ProgressMonitor pm) throws OperatorException {
		System.out.println("Computing tile stack for " + targetRectangle);

		Tile yDisparityTile = getSourceTile(yDisparityBand, targetRectangle, pm);	
		Tile elevationTile = getSourceTile(elevationTiePointGrid, targetRectangle, pm);		
		Tile heightsTile = targetTiles.get(heights);
		
		for (int y = (yDisparityTile.getMinY()); y <= yDisparityTile.getMaxY(); y++) {
			for(int x = yDisparityTile.getMinX(); x <= yDisparityTile.getMaxX(); x++) {					
				heightsTile.setSample(x, y, heightCalculator.getHeight(x, yDisparityTile.getSampleFloat(x, y), elevationTile.getSampleDouble(x,y)));
			}

		}
	}
	public static class Spi extends OperatorSpi {
		public Spi() {
			super(MannsteinCameraModel.class);
		}
	}
}
