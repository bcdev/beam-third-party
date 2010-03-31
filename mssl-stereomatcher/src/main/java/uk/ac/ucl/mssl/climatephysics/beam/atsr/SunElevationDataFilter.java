package uk.ac.ucl.mssl.climatephysics.beam.atsr;


import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.Tile;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.annotations.TargetProduct;
import org.esa.beam.framework.gpf.internal.OperatorImage;
import org.esa.beam.framework.gpf.internal.TileImpl;
import org.esa.beam.util.ProductUtils;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;

@OperatorMetadata(alias="SunElevationDataFilter", 
                  description="Filters input data based on sun elevation")
public class SunElevationDataFilter extends Operator {

	@SourceProduct(alias="atsrToaL1b")
	private Product sourceProduct;

	@TargetProduct
	private Product targetProduct;

	@Parameter(alias="inputBandName", defaultValue="btemp_nadir_1100", description="Input band for filter")
	private String inputBandName;

	@Parameter(alias="filterBandName", defaultValue="filter", description="Name of output filter band")
	private String filterBandName;

	@Parameter(alias="sunElevation", defaultValue="10d", description="Minimum sun elevation for valid data")
	private double sunElevation;

	@Parameter(alias="noDataValue", defaultValue="0", interval = "[0,255]", description="No data value to embed in images")
	private short noDataValue;

	private static final String sunElevationNadirName = "sun_elev_nadir";
	
	// source bands
	private Band referenceBand;
	private TiePointGrid sunElevationBand;


	@Override
	public void initialize() throws OperatorException {
		referenceBand = sourceProduct.getBand(inputBandName);
		if (referenceBand == null) {
            throw new OperatorException("Input band not found: " + inputBandName);
        }
		sunElevationBand = sourceProduct.getTiePointGrid(sunElevationNadirName);
		if (sunElevationBand == null) {
            throw new OperatorException("sun-Elevation Tie-Point-Grid not found: " + sunElevationNadirName);
        }
		
		int rasterWidth = sourceProduct.getSceneRasterWidth();
		int rasterHeight = sourceProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_DataFilter", "MSSL_DataFilter",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);

		Band filterBand = targetProduct.addBand(filterBandName, ProductData.TYPE_UINT8);
		filterBand.setNoDataValue(noDataValue);
		filterBand.setNoDataValueUsed(true);
		
		setTargetProduct(targetProduct);
	}

	@Override
	public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
	    Rectangle targetRect = targetTile.getRectangle();
        pm.beginTask("Computing filter", targetRect.height + 4 + (referenceBand.isValidMaskUsed()?4:0));
        try {
            Tile sunElevationTile = getSourceTile(sunElevationBand, targetRect, SubProgressMonitor.create(pm, 4));
            Tile validTile = null;
            if (referenceBand.isValidMaskUsed()) {
                validTile = getValidTile(referenceBand, targetRect, SubProgressMonitor.create(pm, 4));
            }

            for (int y = targetRect.y; y < targetRect.y +targetRect.height; y++) {
                for(int x = targetRect.x; x < targetRect.x + targetRect.width; x++) {
                    if (sunElevationTile.getSampleDouble(x, y) < sunElevation){
                        targetTile.setSample(x, y, noDataValue);	
                    } else {
                        if (validTile != null && validTile.getSampleBoolean(x, y)){
                        	targetTile.setSample(x, y, 1);
                        } else {
                            targetTile.setSample(x, y, noDataValue);
                        }
                    }
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
	
	private Tile getValidTile(RasterDataNode rasterDataNode, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
	    RenderedImage image = rasterDataNode.getValidMaskImage();
        ProgressMonitor oldPm = OperatorImage.setProgressMonitor(image, pm);
        try {
            /////////////////////////////////////////////////////////////////////
            //
            // Note: GPF pull-processing is triggered here!
            //
            Raster awtRaster = image.getData(rectangle); // Note: copyData is NOT faster!
            //
            /////////////////////////////////////////////////////////////////////
            return new TileImpl(rasterDataNode, awtRaster);
        } finally {
            OperatorImage.setProgressMonitor(image, oldPm);
        }
	}

	public static class Spi extends OperatorSpi {
		public Spi() {
			super(SunElevationDataFilter.class);
		}
	}
}
