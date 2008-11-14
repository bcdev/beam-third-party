package uk.ac.ucl.mssl.climatephysics.beam.atsr;


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

import com.bc.ceres.core.ProgressMonitor;

@OperatorMetadata(alias="SunElevationDataFilter", description="Filters input data based on sun elevation")
public class SunElevationDataFilter extends Operator {

	@SourceProduct(alias="atsrToaL1b")
	private Product sourceProduct;

	@TargetProduct
	private Product targetProduct;

	@Parameter(alias="inputBandName", defaultValue="btemp_nadir_1100", description="input band for filter")
	private String inputBandName;

	@Parameter(alias="filterBandName", defaultValue="filter", description="name of output filter band")
	private String filterBandName;

	@Parameter(alias="sunElevation", defaultValue="10d", description="minimum sun elevation for valid data")
	protected double sunElevation;

	@Parameter(alias="noDataValue", defaultValue="0", description="no data value to embed in images")
	protected double noDataValue;

	// source bands
	private Band referenceBand;
	private TiePointGrid sunElevationBand;

	// target bands
	private Band filterBand;


	private String sunElevationNadirName = "sun_elev_nadir";

	@Override
	public void initialize() throws OperatorException {

		referenceBand = sourceProduct.getBand(inputBandName);
		sunElevationBand = sourceProduct.getTiePointGrid(sunElevationNadirName);

		int rasterWidth = sourceProduct.getSceneRasterWidth();
		int rasterHeight = sourceProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_DataFilter", "MSSL_DataFilter",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);

		filterBand = new Band(filterBandName,
				ProductData.TYPE_UINT16,
				rasterWidth, rasterHeight);
		filterBand.setNoDataValue(noDataValue);
		
		targetProduct.addBand(filterBand);
		setTargetProduct(targetProduct);
	}

	final private void copySample(Tile source, Tile target, int x, int y, double sourceNoDataValue, double targetNoDataValue){
		double sample = source.getSampleDouble(x, y);
		if (sample == sourceNoDataValue){
			target.setSample(x, y, targetNoDataValue);
		} else {
			target.setSample(x, y, 1);
		}
		
	}
	
	@Override
	public void computeTileStack(Map<Band, Tile> targetTiles,
			Rectangle targetRectangle,
			ProgressMonitor pm) throws OperatorException {
		
		System.out.println("processing tile " + targetRectangle);
		Tile referenceTile = getSourceTile(referenceBand,
				targetRectangle, pm);
		Tile sunElevationTile = getSourceTile(sunElevationBand, targetRectangle, pm);

		Tile targetFilterTile = targetTiles.get(filterBand);


		int minX = targetRectangle.x;
		int maxX = minX + targetRectangle.width;
		int minY = targetRectangle.y;
		int maxY = minY + targetRectangle.height;
		
		for (int y = minY; y < maxY; y++) {
			for (int x = minX; x < maxX; x++) {
				if (sunElevationTile.getSampleDouble(x, y) < sunElevation){
					targetFilterTile.setSample(x, y, noDataValue);	
				} else {
					copySample(referenceTile, targetFilterTile, x, y, referenceBand.getNoDataValue(), noDataValue);
				}
			}
		}
	}

	public static class Spi extends OperatorSpi {
		public Spi() {
			super(SunElevationDataFilter.class);
		}
	}
}
