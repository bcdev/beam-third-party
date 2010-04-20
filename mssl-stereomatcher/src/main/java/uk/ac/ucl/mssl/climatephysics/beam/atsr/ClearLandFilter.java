package uk.ac.ucl.mssl.climatephysics.beam.atsr;


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

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;

@OperatorMetadata(alias="ClearLandFilter", 
                  description="Filters input to be both clear and over land")
public class ClearLandFilter extends Operator {

	@SourceProduct(alias="atsrToaL1b", bands= {NADIR_CLOUD_FLAG_NAME, FORWARD_CLOUD_FLAG_NAME})
	private Product sourceProduct;

	@TargetProduct
	private Product targetProduct;

	@Parameter(alias="filterBandName", defaultValue="filter", description="Name of output filter band")
	private String filterBandName;

	@Parameter(alias="cloudyRadius", defaultValue="5", description="Radius to be clear of clouds around pixel")
	private int cloudRadius;

	private static final String NADIR_CLOUD_FLAG_NAME = "cloud_flags_nadir";
	private static final String FORWARD_CLOUD_FLAG_NAME = "cloud_flags_fward";
	// TODO at the edges of orbits the elevation is computed incorrectly
	// this masks those areas out, but it should be fixed in BEAM/Envisat Data
	private static final int ELEVATION_ERROR_BORDER = 6;

	// source bands
	private Band nadirBand;
	private Band forwardBand;


	@Override
	public void initialize() throws OperatorException {
		nadirBand = sourceProduct.getBand(NADIR_CLOUD_FLAG_NAME);
		forwardBand = sourceProduct.getBand(FORWARD_CLOUD_FLAG_NAME);
		int rasterWidth = sourceProduct.getSceneRasterWidth();
		int rasterHeight = sourceProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_DataFilter", "MSSL_DataFilter",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
		ProductUtils.copyMetadata(sourceProduct, targetProduct);

		targetProduct.addBand(filterBandName, ProductData.TYPE_UINT8);
		setTargetProduct(targetProduct);
	}


	@Override
	public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
	    Rectangle targetRect = targetTile.getRectangle();
		pm.beginTask("Computing filter", targetRect.height+8);
		try {
		    Tile nadirTile = getSourceTile(nadirBand, targetRect, SubProgressMonitor.create(pm, 4));
		    Tile forwardTile = getSourceTile(forwardBand, targetRect, SubProgressMonitor.create(pm, 4));

		    for (int y = targetRect.y; y < targetRect.y +targetRect.height; y++) {
		        for(int x = targetRect.x; x < targetRect.x + targetRect.width; x++) {	
		            boolean valid = true;
		            boolean cloudy = true;
		            boolean land = false;
		            // TODO this is to mask out areas of incorrect altitude from the Envisat files
		            if (x < ELEVATION_ERROR_BORDER || x >= targetRect.getMaxX() - ELEVATION_ERROR_BORDER){
		                valid = false;
		            }
		            if (valid){
		                land = nadirTile.getSampleBit(x, y, 0) || forwardTile.getSampleBit(x, y, 0);
		            }
		            if (valid && land){
		                for (int i = cloudRadius *(-1); i <= cloudRadius; ++i){
		                    if ((x + i < nadirTile.getMinX()) || x +i > nadirTile.getMaxX()) {
		                        continue;
		                    }
		                    for (int j = cloudRadius * (-1); j <= cloudRadius; ++j){
		                        if (y +j < nadirTile.getMinY() || y +j > nadirTile.getMaxY()) {
		                            continue;
		                        }
		                        cloudy = nadirTile.getSampleBit(x + i, y + j, 1) || forwardTile.getSampleBit(x + i, y + j, 1);
		                        if (cloudy)
		                            break;
		                    }
		                    if (cloudy)
		                        break;
		                }
		            }
		            int value = (!valid) || (!land) || cloudy ? 0 : 1;
		            targetTile.setSample(x, y, value);
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
			super(ClearLandFilter.class);
		}
	}
}
