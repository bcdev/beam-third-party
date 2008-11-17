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

@OperatorMetadata(alias="ExpectedDisparities", 
                  description="Computes expected disparities from elevation")
public class ExpectedDisparities extends Operator {

	@SourceProduct(alias="atsrToaL1b")
	private Product sourceProduct;

	@TargetProduct
	private Product targetProduct;

	@Parameter(alias="outputBandName", defaultValue="expectedDisparities", description="Name of output band")
	private String outputBandName;

	private static final double disparityStep = 800.0;
	
	// source bands
	private TiePointGrid elevationTiePointGrid;

	@Override
	public void initialize() throws OperatorException {
		elevationTiePointGrid = sourceProduct.getTiePointGrid("altitude");

		int rasterWidth = sourceProduct.getSceneRasterWidth();
		int rasterHeight = sourceProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_DataFilter", "MSSL_DataFilter",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);

		targetProduct.addBand(outputBandName, ProductData.TYPE_UINT8);
		setTargetProduct(targetProduct);
	}

	@Override
	public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
	    Rectangle targetRect = targetTile.getRectangle();
        pm.beginTask("Computing filter", targetRect.height+4);
        try {
            Tile elevationTile = getSourceTile(elevationTiePointGrid, targetRect, SubProgressMonitor.create(pm, 4));
		
            for (int y = targetRect.y; y < targetRect.y +targetRect.height; y++) {
                for(int x = targetRect.x; x < targetRect.x + targetRect.width; x++) {
                    final double elevation = elevationTile.getSampleDouble(x, y);
                    targetTile.setSample(x, y, Math.max(Math.round(elevation / disparityStep), 0));
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
			super(ExpectedDisparities.class);
		}
	}
}
