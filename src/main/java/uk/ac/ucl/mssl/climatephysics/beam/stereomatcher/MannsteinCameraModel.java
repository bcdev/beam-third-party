package uk.ac.ucl.mssl.climatephysics.beam.stereomatcher;

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

import java.awt.Rectangle;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;

@OperatorMetadata(alias="MannsteinCameraModel", 
                  description="Computes geometric height from parallax using Mannstein Camera Model")
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

	private HeightCalculator heightCalculator;

	@Override
	public void initialize() throws OperatorException {
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

		Band heights = targetProduct.addBand("CloudTopHeight", ProductData.TYPE_INT16);
		heights.setUnit("m");
		heights.setDescription("Cloud Top Height");
		heights.setNoDataValue(noDataValue);
		heights.setNoDataValueUsed(true);

		heightCalculator = new HeightCalculator(new MannsteinATSRCameraModel(coneHalfAngle), 
				minimumCloudHeight, maximumCloudHeight, disparityOffset, yDisparityBand.getNoDataValue(), noDataValue);

		setTargetProduct(targetProduct);
	}

	@Override
	public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
	    Rectangle targetRect = targetTile.getRectangle();
        pm.beginTask("Computing filter", targetRect.height+8);
        try {
            Tile yDisparityTile = getSourceTile(yDisparityBand, targetRect, SubProgressMonitor.create(pm, 4));	
            Tile elevationTile = getSourceTile(elevationTiePointGrid, targetRect, SubProgressMonitor.create(pm, 4));		
		
            for (int y = (yDisparityTile.getMinY()); y <= yDisparityTile.getMaxY(); y++) {
                for(int x = yDisparityTile.getMinX(); x <= yDisparityTile.getMaxX(); x++) {					
                    final double height = heightCalculator.getHeight(x, yDisparityTile.getSampleFloat(x, y), elevationTile.getSampleDouble(x,y));
                    targetTile.setSample(x, y, height);
                }
                if (pm.isCanceled()) {
                    return;
                }
                pm.worked(1);
            }
		} finally {
		    pm.done();
		}
	}
	
	
	public static class Spi extends OperatorSpi {
		public Spi() {
			super(MannsteinCameraModel.class);
		}
	}
}
