package uk.ac.ucl.mssl.climatephysics.beam.atsr;


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

@OperatorMetadata(alias="ClearLandFilter", description="Filters input to be both clear and over land")
public class ClearLandFilter extends Operator {

	@SourceProduct(alias="atsrToaL1b")
	private Product sourceProduct;

	@TargetProduct
	private Product targetProduct;

	@Parameter(alias="filterBandName", defaultValue="filter", description="name of output filter band")
	private String filterBandName;

	@Parameter(alias="noDataValue", defaultValue="-999", description="no data value to embed in images")
	protected double noDataValue;

	@Parameter(alias="cloudyRadius", defaultValue="5", description="radius to be clear of clouds around pixel")
	protected int cloudRadius;

	// TODO at the edges of orbits the elevation is computed incorrectly
	// this masks those areas out, but it should be fixed in BEAM/Envisat Data
	protected int elevationErrorBorder = 6;

	// source bands
	private Band nadirBand;
	private Band forwardBand;

	// target bands
	private Band filterBand;

	private String nadirCloudFlagName = "cloud_flags_nadir";
	private String forwardCloudFlagName = "cloud_flags_fward";

	private Logger logger;

	@Override
	public void initialize() throws OperatorException {
		logger = Logger.getLogger("MSSL ClearLandFilter");
		logger.info("Starting initialisation");
		nadirBand = sourceProduct.getBand(nadirCloudFlagName);
		forwardBand = sourceProduct.getBand(forwardCloudFlagName);
		System.out.println("got bands -- " + nadirBand + forwardBand);
		int rasterWidth = sourceProduct.getSceneRasterWidth();
		int rasterHeight = sourceProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_DataFilter", "MSSL_DataFilter",
				rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
		ProductUtils.copyMetadata(sourceProduct, targetProduct);

		filterBand = new Band(filterBandName,
				ProductData.TYPE_UINT16,
				rasterWidth, rasterHeight);
		filterBand.setNoDataValue(noDataValue);

		targetProduct.addBand(filterBand);
		setTargetProduct(targetProduct);
	}


	@Override
	public void computeTileStack(Map<Band, Tile> targetTiles,
			Rectangle targetRectangle,
			ProgressMonitor pm) throws OperatorException {

		logger.info("Computing tile stack for " + targetRectangle); 		
		Tile nadirTile = getSourceTile(nadirBand,
				targetRectangle, pm);
		Tile forwardTile = getSourceTile(forwardBand,
				targetRectangle, pm);

		Tile targetFilterTile = targetTiles.get(filterBand);

		for (int y = (targetFilterTile.getMinY()); y <= targetFilterTile.getMaxY(); y++) {
			for(int x = targetFilterTile.getMinX(); x <= targetFilterTile.getMaxX(); x++) {	
//				for (Tile.Pos pos : targetFilterTile) {
				boolean valid = true;
				boolean cloudy = true;
				boolean land = false;
				// TODO this is to mask out areas of incorrect altitude from the Envisat files
				if (x < elevationErrorBorder || x >= targetRectangle.getMaxX() - elevationErrorBorder){
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
				targetFilterTile.setSample(x, y, value);
			}
		}
	}

	public static class Spi extends OperatorSpi {
		public Spi() {
			super(ClearLandFilter.class);
		}
	}
}
