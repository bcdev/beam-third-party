package uk.ac.ucl.mssl.climatephysics.beam.atsr;


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

import java.awt.Rectangle;

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

	@Parameter(alias="noDataValue", defaultValue="0", description="No data value to embed in images")
	private double noDataValue;

	private static final String sunElevationNadirName = "sun_elev_nadir";
	
	// source bands
	private Band referenceBand;
	private TiePointGrid sunElevationBand;

    private double sourceNoDataValue;
    private boolean sourceNoDataValueUsed;


	@Override
	public void initialize() throws OperatorException {
		referenceBand = sourceProduct.getBand(inputBandName);
		sunElevationBand = sourceProduct.getTiePointGrid(sunElevationNadirName);

		sourceNoDataValue = referenceBand.getNoDataValue();
		sourceNoDataValueUsed = referenceBand.isNoDataValueUsed();
		
		int rasterWidth = sourceProduct.getSceneRasterWidth();
		int rasterHeight = sourceProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_DataFilter", "MSSL_DataFilter",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);

		Band filterBand = targetProduct.addBand(filterBandName, ProductData.TYPE_UINT16);
		filterBand.setNoDataValue(noDataValue);
		filterBand.setNoDataValueUsed(true);
		
		setTargetProduct(targetProduct);
	}

	@Override
	public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
	    Rectangle targetRect = targetTile.getRectangle();
        pm.beginTask("Computing filter", targetRect.height+8);
        try {
            Tile referenceTile = getSourceTile(referenceBand, targetRect, SubProgressMonitor.create(pm, 4));
            Tile sunElevationTile = getSourceTile(sunElevationBand, targetRect, SubProgressMonitor.create(pm, 4));

            for (int y = targetRect.y; y < targetRect.y +targetRect.height; y++) {
                for(int x = targetRect.x; x < targetRect.x + targetRect.width; x++) {
                    if (sunElevationTile.getSampleDouble(x, y) < sunElevation){
                        targetTile.setSample(x, y, noDataValue);	
                    } else {
                        double sample = referenceTile.getSampleDouble(x, y);
                        if (sourceNoDataValueUsed && sample == sourceNoDataValue){
                        	targetTile.setSample(x, y, noDataValue);
                        } else {
                        	targetTile.setSample(x, y, 1);
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

	public static class Spi extends OperatorSpi {
		public Spi() {
			super(SunElevationDataFilter.class);
		}
	}
}
