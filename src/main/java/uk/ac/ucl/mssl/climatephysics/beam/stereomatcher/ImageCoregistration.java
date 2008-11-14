package uk.ac.ucl.mssl.climatephysics.beam.stereomatcher;


import java.awt.Rectangle;
import java.util.Map;
import java.util.logging.Logger;

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

import com.bc.ceres.core.ProgressMonitor;

@OperatorMetadata(alias="ImageCoregistration", description="Determines nadir/forward coregistration by determining shift for clear views of land")
public class ImageCoregistration extends Operator {

	@SourceProduct(alias="disparities")
	private Product disparitiesProduct;

	@SourceProduct(alias="expectedDisparities")
	private Product expectedDisparitiesProduct;

	@SourceProduct(alias="viewFilter")
	private Product filterProduct;

	@TargetProduct
	private Product targetProduct;

	@Parameter(alias="viewFilterBandName", defaultValue="filter", description="filter band containing mask")
	private String filterBandName;

	@Parameter(alias="xDispBandName", defaultValue="XDisparities", description="band containing x disparities")
	private String xDispBandName;

	@Parameter(alias="yDispBandName", defaultValue="YDisparities", description="band containing y disparities")
	private String yDispBandName;

	@Parameter(alias="expectedDispBandName", defaultValue="expectedDisparities", description="band containing expected disparities caused by elevation")
	private String expectedDispBandName;

	@Parameter(alias="yShiftBandName", defaultValue="yShift", description="output band containing y Shift")
	private String yShiftBandName;

	@Parameter(alias="xShiftBandName", defaultValue="xShift", description="output band containing x Shift")
	private String xShiftBandName;


	@Parameter(alias="noDataValue", defaultValue="-999", description="no data value to embed in images")
	protected int noDataValue;


	// source bands
	private Band xDispBand;
	private Band yDispBand;
	private Band expectedDispBand;
	private Band filterBand;

	// target bands
	private Band xShiftBand;
	private Band yShiftBand;

	private Logger logger;

	@Override
	public void initialize() throws OperatorException {
		logger = Logger.getLogger("MSSL ImageCoregistration");
		logger.info("Starting initialisation");

		xDispBand = disparitiesProduct.getBand(xDispBandName);
		if (null == xDispBand){
			throw new IllegalArgumentException("Band " + xDispBandName + " missing in source product");
		}

		yDispBand = disparitiesProduct.getBand(yDispBandName);
		if (null == yDispBand){
			throw new IllegalArgumentException("Band " + yDispBandName + " missing in source product");
		}

		expectedDispBand = expectedDisparitiesProduct.getBand(expectedDispBandName);
		if (null == expectedDispBand){
			throw new IllegalArgumentException("Band " + expectedDispBandName + " missing in source product");
		}

		filterBand = filterProduct.getBand(filterBandName);
		if (null == filterBand){
			throw new IllegalArgumentException("Band " + filterBandName + " missing in source product");
		}


		int rasterWidth = disparitiesProduct.getSceneRasterWidth();
		int rasterHeight = disparitiesProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_Coregistration", "MSSL_Coregistration",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(disparitiesProduct, targetProduct);
		ProductUtils.copyGeoCoding(disparitiesProduct, targetProduct);
		ProductUtils.copyMetadata(disparitiesProduct, targetProduct);

		xShiftBand = new Band(xShiftBandName,
				ProductData.TYPE_INT16,
				rasterWidth, rasterHeight);
		xShiftBand.setNoDataValue(noDataValue);
		targetProduct.addBand(xShiftBand);

		yShiftBand = new Band(yShiftBandName,
				ProductData.TYPE_INT16,
				rasterWidth, rasterHeight);
		yShiftBand.setNoDataValue(noDataValue);
		targetProduct.addBand(yShiftBand);

		setTargetProduct(targetProduct);
	}


	@Override
	public void computeTileStack(Map<Band, Tile> targetTiles,
			Rectangle targetRectangle,
			ProgressMonitor pm) throws OperatorException {

		logger.info("Computing tile stack for " + targetRectangle); 		
		Tile xDispTile = getSourceTile(xDispBand,
				targetRectangle, pm);
		Tile yDispTile = getSourceTile(yDispBand,
				targetRectangle, pm);
		Tile expectedDisparityTile = getSourceTile(expectedDispBand, targetRectangle, pm);
		Tile filterTile = getSourceTile(filterBand, targetRectangle, pm);

		Tile xShiftTile = targetTiles.get(xShiftBand);
		Tile yShiftTile = targetTiles.get(yShiftBand);


		for (int y = (xShiftTile.getMinY()); y <= xShiftTile.getMaxY(); y++) {
			for(int x = xShiftTile.getMinX(); x <= xShiftTile.getMaxX(); x++) {	

				//for (Tile.Pos pos : xShiftTile) {
				int xShift = noDataValue;
				int yShift = noDataValue;
				// 0 is everything cloudy or sea => no data value
				if (1 == filterTile.getSampleInt(x, y)){
					yShift = (int)(yDispTile.getSampleDouble(x, y) - expectedDisparityTile.getSampleDouble(x, y));
					xShift = (int)xDispTile.getSampleDouble(x, y);
				}
				xShiftTile.setSample(x, y, xShift);
				yShiftTile.setSample(x, y, yShift);
			}
		}
	}

	public static class Spi extends OperatorSpi {
		public Spi() {
			super(ImageCoregistration.class);
		}
	}
}
