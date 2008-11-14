package uk.ac.ucl.mssl.climatephysics.beam.atsr;


import java.awt.Rectangle;
import java.util.Map;
import java.util.logging.Logger;

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

import com.bc.ceres.core.ProgressMonitor;

@OperatorMetadata(alias="ExpectedDisparities", description="Computes expected disparities from elevation")
public class ExpectedDisparities extends Operator {

	@SourceProduct(alias="atsrToaL1b")
	private Product sourceProduct;

	@TargetProduct
	private Product targetProduct;

	@Parameter(alias="outputBandName", defaultValue="expectedDisparities", description="name of output band")
	private String outputBandName;

	@Parameter(alias="noDataValue", defaultValue="-999.0", description="no data value to embed in images")
	protected double noDataValue;

	protected double disparityStep = 800.0;
	// source bands
	private TiePointGrid elevationTiePointGrid;

	// target bands
	private Band outputBand;

	private Logger logger;
	
	@Override
	public void initialize() throws OperatorException {
		logger = Logger.getLogger("MSSL ExpectedDisparities");
		logger.info("Starting initialisation");
				
		elevationTiePointGrid = sourceProduct.getTiePointGrid("altitude");

		int rasterWidth = sourceProduct.getSceneRasterWidth();
		int rasterHeight = sourceProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_DataFilter", "MSSL_DataFilter",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);

		outputBand = new Band(outputBandName,
				ProductData.TYPE_UINT16,
				rasterWidth, rasterHeight);
		outputBand.setNoDataValue(noDataValue);
		
		targetProduct.addBand(outputBand);
		setTargetProduct(targetProduct);
	}

	
	@Override
	public void computeTileStack(Map<Band, Tile> targetTiles,
			Rectangle targetRectangle,
			ProgressMonitor pm) throws OperatorException {
		
		logger.info("Computing tile stack for " + targetRectangle); 		
		Tile elevationTile = getSourceTile(elevationTiePointGrid,
				targetRectangle, pm);
		Tile targetTile = targetTiles.get(outputBand);
		
		//for (Tile.Pos pos : targetTile) {
		for (int y = (targetTile.getMinY()); y <= targetTile.getMaxY(); y++) {
			for(int x = targetTile.getMinX(); x <= targetTile.getMaxX(); x++) {	
				double elevation = elevationTile.getSampleDouble(x, y);
				targetTile.setSample(x, y, Math.max(Math.round(elevation / disparityStep), 0));
			}
		}
	}

	public static class Spi extends OperatorSpi {
		public Spi() {
			super(ExpectedDisparities.class);
		}
	}
}
