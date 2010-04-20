package uk.ac.ucl.mssl.climatephysics.beam.stereomatcher;


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

import java.awt.Rectangle;
import java.util.Map;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;

@OperatorMetadata(alias="ImageCoregistration", 
                  description="Determines nadir/forward coregistration by determining shift for clear views of land")
public class ImageCoregistration extends Operator {

	@SourceProduct(alias="disparities")
	private Product disparitiesProduct;

	@SourceProduct(alias="expectedDisparities")
	private Product expectedDisparitiesProduct;

	@SourceProduct(alias="viewFilter")
	private Product filterProduct;

	@TargetProduct
	private Product targetProduct;

	@Parameter(alias="viewFilterBandName", defaultValue="filter", description="Filter band containing mask")
	private String filterBandName;

	@Parameter(alias="xDispBandName", defaultValue="XDisparities", description="Band containing x disparities")
	private String xDispBandName;

	@Parameter(alias="yDispBandName", defaultValue="YDisparities", description="Band containing y disparities")
	private String yDispBandName;

	@Parameter(alias="expectedDispBandName", defaultValue="expectedDisparities", description="Band containing expected disparities caused by elevation")
	private String expectedDispBandName;

	@Parameter(alias="yShiftBandName", defaultValue="yShift", description="Output band containing y Shift")
	private String yShiftBandName;

	@Parameter(alias="xShiftBandName", defaultValue="xShift", description="Output band containing x Shift")
	private String xShiftBandName;


	@Parameter(alias="noDataValue", defaultValue="-999", interval = "[-32768,32767]", description="No data value to embed in images")
	private int noDataValue;


	// source bands
	private Band xDispBand;
	private Band yDispBand;
	private Band expectedDispBand;
	private Band filterBand;

	// target bands
	private Band xShiftBand;
	private Band yShiftBand;

	@Override
	public void initialize() throws OperatorException {
		xDispBand = disparitiesProduct.getBand(xDispBandName);
		if (null == xDispBand){
			throw new OperatorException("Band " + xDispBandName + " missing in source product");
		}

		yDispBand = disparitiesProduct.getBand(yDispBandName);
		if (null == yDispBand){
			throw new OperatorException("Band " + yDispBandName + " missing in source product");
		}

		expectedDispBand = expectedDisparitiesProduct.getBand(expectedDispBandName);
		if (null == expectedDispBand){
			throw new OperatorException("Band " + expectedDispBandName + " missing in source product");
		}

		filterBand = filterProduct.getBand(filterBandName);
		if (null == filterBand){
			throw new OperatorException("Band " + filterBandName + " missing in source product");
		}

		int rasterWidth = disparitiesProduct.getSceneRasterWidth();
		int rasterHeight = disparitiesProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_Coregistration", "MSSL_Coregistration",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(disparitiesProduct, targetProduct);
		ProductUtils.copyGeoCoding(disparitiesProduct, targetProduct);
		ProductUtils.copyMetadata(disparitiesProduct, targetProduct);

		xShiftBand =targetProduct.addBand(xShiftBandName,
		                                   ProductData.TYPE_INT16);
		xShiftBand.setNoDataValue(noDataValue);
		xShiftBand.setNoDataValueUsed(true);

		yShiftBand = targetProduct.addBand(yShiftBandName,
		                                   ProductData.TYPE_INT16);
		yShiftBand.setNoDataValue(noDataValue);
		yShiftBand.setNoDataValueUsed(true);

		setTargetProduct(targetProduct);
	}


	@Override
	public void computeTileStack(Map<Band, Tile> targetTiles,
			Rectangle targetRect,
			ProgressMonitor pm) throws OperatorException {

	    pm.beginTask("Computing filter", targetRect.height+16);
        try {
            Tile xDispTile = getSourceTile(xDispBand, targetRect, SubProgressMonitor.create(pm, 4));
            Tile yDispTile = getSourceTile(yDispBand, targetRect, SubProgressMonitor.create(pm, 4));
            Tile expectedDisparityTile = getSourceTile(expectedDispBand, targetRect, SubProgressMonitor.create(pm, 4));
            Tile filterTile = getSourceTile(filterBand, targetRect, SubProgressMonitor.create(pm, 4));

            Tile xShiftTile = targetTiles.get(xShiftBand);
            Tile yShiftTile = targetTiles.get(yShiftBand);


            for (int y = targetRect.y; y < targetRect.y +targetRect.height; y++) {
                for(int x = targetRect.x; x < targetRect.x + targetRect.width; x++) {

                    int xShift = noDataValue;
                    int yShift = noDataValue;
                    // 0 is everything cloudy or sea => no data value
                    if (filterTile.getSampleInt(x, y) == 1){
                        yShift = (int)(yDispTile.getSampleDouble(x, y) - expectedDisparityTile.getSampleDouble(x, y));
                        xShift = (int)xDispTile.getSampleDouble(x, y);
                    }
                    xShiftTile.setSample(x, y, xShift);
                    yShiftTile.setSample(x, y, yShift);
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
			super(ImageCoregistration.class);
		}
	}
}
