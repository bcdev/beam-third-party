package uk.ac.ucl.mssl.climatephysics.beam.imaging;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Map;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.RenderedOp;

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

import uk.ac.ucl.mssl.climatephysics.imaging.GaussianKernelFixedSize;

import com.bc.ceres.core.ProgressMonitor;

@OperatorMetadata(alias="Normaliser", description="Normalises an input image")
public class Normaliser extends Operator {

	@SourceProduct(alias="atsrToaL1b")
	private Product sourceProduct;
	
	@TargetProduct
	private Product targetProduct;
	

	@Parameter(alias="referenceBandName", defaultValue="btemp_nadir_1100", description="Reference band")
	private String referenceBandName;
	@Parameter(alias="comparisonBandName", defaultValue="btemp_fward_1100", description="Comparison band")
	private String comparisonBandName;

	// source bands
	private Band referenceBand;
	private Band comparisonBand;

	// target bands
	private Band targetReferenceBand;
	private Band targetComparisonBand;
	private Band normalisedReferenceBand;
	private Band normalisedComparisonBand;
	private Band stddevReferenceBand;
	private Band meanReferenceBand;
	private Band stddevComparisonBand;
	private Band meanComparisonBand;
	
	private float borderWidth = 21f;
	
	@Override
	public void initialize() throws OperatorException {
		
		//TODO remove this hack to work around module activation
		//TODO from GPF
		try {
			ModuleActivator activator = new ModuleActivator();
			activator.start(null);
		} catch (Throwable e) {
			System.out.println("Module already activated.");
		}
		
		referenceBand = sourceProduct.getBand(referenceBandName);
		comparisonBand = sourceProduct.getBand(comparisonBandName);

		int rasterWidth = sourceProduct.getSceneRasterWidth();
        int rasterHeight = sourceProduct.getSceneRasterHeight();
		targetProduct = new Product("MSSL_Normalised", "MSSL_Normalised",
                rasterWidth, rasterHeight);	
		ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
		ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
		ProductUtils.copyMetadata(sourceProduct, targetProduct);
		
		targetReferenceBand = ProductUtils.copyBand(referenceBandName, sourceProduct, targetProduct);
		targetComparisonBand = ProductUtils.copyBand(comparisonBandName, sourceProduct, targetProduct);
		
		normalisedReferenceBand = new Band("referenceNormalised",
                     					ProductData.TYPE_FLOAT64,
                     					rasterWidth, rasterHeight);
		meanReferenceBand = new Band("referenceRegionalMean",
					ProductData.TYPE_FLOAT64,
					rasterWidth, rasterHeight);
		stddevReferenceBand = new Band("referenceRegionalStdDev",
				ProductData.TYPE_FLOAT64,
				rasterWidth, rasterHeight);		
		
		normalisedComparisonBand = new Band("comparisonNormalised",
					ProductData.TYPE_FLOAT64,
					rasterWidth, rasterHeight);
		meanComparisonBand = new Band("comparisonRegionalMean",
				ProductData.TYPE_FLOAT64,
				rasterWidth, rasterHeight);
		stddevComparisonBand = new Band("comparisonRegionalStdDev",
			ProductData.TYPE_FLOAT64,
			rasterWidth, rasterHeight);

		targetProduct.addBand(normalisedReferenceBand);
		targetProduct.addBand(normalisedComparisonBand);
		targetProduct.addBand(meanReferenceBand);
		targetProduct.addBand(stddevReferenceBand);
		targetProduct.addBand(meanComparisonBand);
		targetProduct.addBand(stddevComparisonBand);
		
		setTargetProduct(targetProduct);
	}

	protected void normaliseBand(Band inputBand, Band copyInputBand, Band normalisedBand, Band stddevBand, Band meanBand, 
			Map<Band, Tile> targetTiles,
			Rectangle targetRectangle, ProgressMonitor pm){
		boolean test = false;
		Tile inputTile = getSourceTile(inputBand, targetRectangle, pm);		

        RenderedImage inputImage = inputBand.getImage();
                   
        ParameterBlock pbCrop = new ParameterBlock();
        pbCrop.addSource(inputImage);
        float minX = (float)Math.max(targetRectangle.getMinX() - borderWidth, inputImage.getMinX());
        float minY = (float)Math.max(targetRectangle.getMinY() - borderWidth, inputImage.getMinY());
        float width = (float)Math.min(targetRectangle.getWidth() + borderWidth * 2.0d,
        							  inputImage.getMinX() + inputImage.getWidth() - minX);
        float height = (float)Math.min(targetRectangle.getHeight() + borderWidth*2, 
        							   inputImage.getMinY() + inputImage.getHeight() - minY);
        pbCrop.add(minX);
        pbCrop.add(minY);
        pbCrop.add(width);
        pbCrop.add(height);
        RenderedOp cropped = JAI.create("Crop", pbCrop, null);
        
        ParameterBlock pbformat = new ParameterBlock();
        pbformat.addSource(cropped);
        pbformat.add(DataBuffer.TYPE_DOUBLE);
        RenderedOp doubleFormat = JAI.create("Format", pbformat, null);
		
        if (test){
        	Raster data = doubleFormat.getData();
        	System.out.println("double " + data.getSampleDouble(100, 100, 0));
        }
        
		KernelJAI kernel = new GaussianKernelFixedSize(21, 5.25f);	
		ParameterBlock pbx = new ParameterBlock();
		pbx.addSource(doubleFormat);
		pbx.add(kernel);
		RenderedOp mean = JAI.create("convolve", pbx, null);

		if (test){
        	Raster data = mean.getData();
        	System.out.println("mean data " + data.getSampleDouble(100, 100, 0));
        }
			
		ParameterBlock pb2 = new ParameterBlock();
		pb2.addSource(doubleFormat);
		pb2.addSource(mean);
		RenderedOp imageMinusMean = JAI.create("subtract", pb2, null);
		
        if (test){
        	Raster data = imageMinusMean.getData();
        	System.out.println("image minus mean data " + data.getSampleDouble(100, 100, 0));
        }

		
		ParameterBlock pb3 = new ParameterBlock();
		pb3.addSource(imageMinusMean);
		pb3.addSource(imageMinusMean);
		RenderedOp multiplied = JAI.create("multiply", pb3, null);
		
        if (test){
        	Raster data = multiplied.getData();
        	System.out.println("multiply " + data.getSampleDouble(100, 100, 0));
        }

		
		KernelJAI k2 = new GaussianKernelFixedSize(21, 5.25f);
		ParameterBlock pb4 = new ParameterBlock();
		pb4.addSource(multiplied);
		pb4.add(k2);
		RenderedOp x2 = JAI.create("convolve", pb4, null);
		
        if (test){
        	Raster data  = x2.getData();
        	System.out.println("cv again data " + data.getSampleDouble(100, 100, 0));
        }

		ParameterBlock pb5 = new ParameterBlock();
		pb5.addSource(x2);
		RenderedOp stddev = JAI.create("uk.ac.ucl.mssl.climatephysics.imaging.sqrt", pb5, null);

        if (test){
        	Raster data = stddev.getData();
        	System.out.println("sqrt data " + data.getSampleDouble(100, 100, 0));
        }

		
		double epsilon = 0.001;
		ParameterBlock pb7 = new ParameterBlock();
		pb7.addSource(stddev);
		pb2.add(epsilon);
		RenderedOp stddevPlusEpsilon = JAI.create("addconst", pb7, null);

        if (test){
        	Raster data = stddevPlusEpsilon.getData();
        	System.out.println("epsilon data " + data.getSampleDouble(100, 100, 0));
        }

		ParameterBlock pb8 = new ParameterBlock();
		pb8.addSource(imageMinusMean);
		pb8.addSource(stddevPlusEpsilon);
		RenderedOp normalised = JAI.create("divide", pb8, null);
		
        if (test){
        	Raster data = normalised.getData();
        	System.out.println("normalised " + data.getSampleDouble(100, 100, 0));
        }

		
		ParameterBlock pb9 = new ParameterBlock();
		pb9.addSource(normalised);
		double[] low = {-2.0d};
		pb9.add(low);
		double[] high = {2.0d};
		pb9.add(high);
		RenderedOp clamped = JAI.create("clamp", pb9, null);
        if (test){
        	Raster data = clamped.getData();
        	System.out.println("clamped data " + data.getSampleDouble(100, 100, 0));
        }

		
        Raster stddevImageTile = stddev.getData();
		Raster meanImageTile = mean.getData();
		Raster normalisedImageTile = clamped.getData();
		
		Tile targetInputTile = targetTiles.get(copyInputBand);
		Tile targetNormalisedTile = targetTiles.get(normalisedBand);
		Tile targetStddevTile = targetTiles.get(stddevBand);
		Tile targetMeanTile = targetTiles.get(meanBand);
		
		for (int y = inputTile.getMinY(); y <= inputTile.getMaxY(); y++) {
			for (int x = inputTile.getMinX(); x <= inputTile.getMaxX(); x++) {
				targetInputTile.setSample(x, y, inputTile.getSampleDouble(x, y));
				targetNormalisedTile.setSample(x, y, normalisedImageTile.getSampleDouble(x, y, 0));
				targetStddevTile.setSample(x, y, stddevImageTile.getSampleDouble(x, y, 0));
				targetMeanTile.setSample(x, y, meanImageTile.getSampleDouble(x, y, 0));
			}
        }
		
	}
	
	@Override
	public void computeTileStack(Map<Band, Tile> targetTiles,
			Rectangle targetRectangle,
			ProgressMonitor pm) throws OperatorException {
		normaliseBand(referenceBand, targetReferenceBand, normalisedReferenceBand, stddevReferenceBand, meanReferenceBand, targetTiles,
				targetRectangle, pm);
		normaliseBand(comparisonBand, targetComparisonBand, normalisedComparisonBand, stddevReferenceBand, meanReferenceBand, targetTiles,
				targetRectangle, pm);	
	}

	
	public static class Spi extends OperatorSpi {
        public Spi() {
            super(Normaliser.class);
        }
    }
}
