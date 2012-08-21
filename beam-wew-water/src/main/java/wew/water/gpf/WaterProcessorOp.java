package wew.water.gpf;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.dataio.ProductIOPlugInManager;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.PixelOperator;
import org.esa.beam.framework.gpf.pointop.ProductConfigurer;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.util.ProductUtils;

@OperatorMetadata(alias = "WaterProcessorOp",
                  version = "1.0",
                  description = "FUB/WeW WATER Processor GPF-Operator")
public class WaterProcessorOp extends PixelOperator {

//    private final static String[] OUTPUT_FORMAT_NAMES = ProductIOPlugInManager.getInstance().getAllProductWriterFormatStrings();

    @SourceProduct(label = "Select source product",
                   description = "The MERIS L1b source product used for the processing.")
    private Product sourceProduct;

    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        // todo
    }

    @Override
    public void dispose() {
        super.dispose();  //Todo change body of created method. Use File | Settings | File Templates to change
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);

        final String[] bandNames = {
                    EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LATITUDE_BAND_NAME,
                    EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LONGITUDE_BAND_NAME,
                    EnvisatConstants.MERIS_AMORGOS_L1B_ALTIUDE_BAND_NAME
        };
        productConfigurer.copyBands(bandNames);
    }

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        final String[] bandNames = EnvisatConstants.MERIS_L1B_BAND_NAMES;
        configureBandnames(sampleConfigurer, bandNames);
        int index = bandNames.length;
        sampleConfigurer.defineSample(index++, "l1_flags");
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[6]);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[7]);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[8]);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[9]);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[10]);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[11]);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[12]);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[13]);
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        String[] outputBandNames = {
                    "algal_2",
                    "yellow_subs",
                    "total_susp",
                    "aero_opt_thick_440",
                    "aero_opt_thick_550",
                    "aero_opt_thick_670",
                    "aero_opt_thick_870",
                    "reflec_1",
                    "reflec_2",
                    "reflec_3",
                    "reflec_4",
                    "reflec_5",
                    "reflec_6",
                    "reflec_7",
                    "reflec_9",
                    "l1_flags",
                    "result_flags"
        };
        configureBandnames(sampleConfigurer, outputBandNames);
    }

    private void configureBandnames(SampleConfigurer sampleConfigurer, String[] outputBandNames) {
        for (int i = 0; i < outputBandNames.length; i++) {
            sampleConfigurer.defineSample(i, outputBandNames[i]);
        }
    }

//    public static String[] getOutputFormats() {
//        ProductIOPlugInManager instance = ProductIOPlugInManager.getInstance();
//        return instance.getAllProductWriterFormatStrings();
//    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(WaterProcessorOp.class);
        }
    }

    /*
    * Retrieves the output product type from the input product type by appending "_FLH_MCI" to the type string.
    *
    * @throws org.esa.beam.framework.processor.ProcessorException
    *          when an error occurs
    */
    private String getOutputProductTypeSafe() throws OperatorException {
        String productType = sourceProduct.getProductType();
        if (productType == null) {
//            @todo retrieve message not from ProcessorConstants
            throw new OperatorException(ProcessorConstants.LOG_MSG_NO_INPUT_TYPE);
        }

        return productType + "_FLH_MCI";
    }

}
