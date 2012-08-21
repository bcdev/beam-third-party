package wew.water.gpf;

import com.bc.jexp.ParseException;
import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.dataop.barithm.BandArithmetic;
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

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

@OperatorMetadata(alias = "WaterProcessorOp",
                  version = "1.0",
                  description = "FUB/WeW WATER Processor GPF-Operator")
public class WaterProcessorOp extends PixelOperator {

    private final static String[] output_Band_Names = {
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
            "reflec_9"
    };

    // Descriptive strings for all possible output bands
    private static String[] output_Band_Descriptions = {
            "Chlorophyll 2 content",
            "Yellow substance",
            "Total suspended matter",
            "Aerosol optical thickness",
            "Aerosol optical thickness",
            "Aerosol optical thickness",
            "Aerosol optical thickness",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance"
    };

    // Unit strings for all possible output bands
    private static String[] output_Band_Units = {
            "log10(mg/m^3)",
            "log10(1/m)",
            "log10(g/m^3)",
            "1",
            "1",
            "1",
            "1",
            "1/sr",
            "1/sr",
            "1/sr",
            "1/sr",
            "1/sr",
            "1/sr",
            "1/sr",
            "1/sr"
    };

    // Wavelengths for the water leaving reflectances rho_w
    private static float[] tau_lambda = {
            440.00f, 550.00f, 670.00f, 870.00f
    };

    // Wavelengths for the water leaving reflectances rho_w
    private static float[] rho_w_lambda = {
            412.50f, 442.50f, 490.00f, 510.00f,
            560.00f, 620.00f, 665.00f, 708.75f
    };

    // Bandwidth for the water leaving reflectances rho_w
    private static float[] rho_w_bandw = {
            10.00f, 10.00f, 10.00f, 10.00f,
            10.00f, 10.00f, 10.00f, 10.00f
    };

    // Mask value to be written if inversion fails
    private static final double RESULT_MASK_VALUE = +5.0;

    public static final String RESULT_FLAGS_NAME = "result_flags";

    public static final int RESULT_ERROR_NUM = 9;

    public static final String[] RESULT_ERROR_TEXT = {
            "Pixel was a priori masked out",
            "CHL retrieval failure (input)",
            "CHL retrieval failure (output)",
            "YEL retrieval failure (input)",
            "YEL retrieval failure (output)",
            "TSM retrieval failure (input)",
            "TSM retrieval failure (output)",
            "Atmospheric correction failure (input)",
            "Atmospheric correction failure (output)"
    };

    public static final String[] RESULT_ERROR_NAME = {
            "LEVEL1b_MASKED",
            "CHL_IN",
            "CHL_OUT",
            "YEL_IN",
            "YEL_OUT",
            "TSM_IN",
            "TSM_OUT",
            "ATM_IN",
            "ATM_OUT"
    };

    public static final int[] RESULT_ERROR_VALUE = {
            0x00000001,
            0x00000002,
            0x00000004,
            0x00000008,
            0x00000010,
            0x00000020,
            0x00000040,
            0x00000080,
            0x00000100,
    };

    private Set<String> bandNamesToCopy = new LinkedHashSet<String>();

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
        final Product sourceProduct = productConfigurer.getSourceProduct();
        final Product targetProduct = productConfigurer.getTargetProduct();

        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();

        final int numOfConcentrationBands = 3;
        final int numOfSpectralWaterLeavingReflectances = 8;
        final int numOfSpectralAerosolOpticalDepths = 4;

        int output_planes = numOfSpectralAerosolOpticalDepths;
        output_planes += numOfSpectralWaterLeavingReflectances;
        output_planes += numOfConcentrationBands;

        Band[] targetBands = new Band[output_planes];
        for (int i = 0; i < output_planes; i++) {
            targetBands[i] = new Band(output_Band_Names[i], ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
            targetBands[i].setScalingOffset(0.0);
            targetBands[i].setScalingFactor(1.0);
            targetBands[i].setSpectralBandIndex(0);
            targetBands[i].setDescription(output_Band_Descriptions[i]);
            targetBands[i].setUnit(output_Band_Units[i]);
            targetProduct.addBand(targetBands[i]);
        }

        for (int i = 0; i < numOfSpectralAerosolOpticalDepths; i++) {
            targetBands[numOfConcentrationBands + i].setSpectralWavelength(tau_lambda[i]);
            targetBands[numOfConcentrationBands + i].setSpectralBandIndex(i);
            targetBands[numOfConcentrationBands + i].setNoDataValue(RESULT_MASK_VALUE);
            targetBands[numOfConcentrationBands + i].setNoDataValueUsed(true);
        }

        for (int i = 0; i < numOfSpectralWaterLeavingReflectances; i++) {
            targetBands[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setSpectralWavelength(rho_w_lambda[i]);
            targetBands[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setSpectralBandwidth(rho_w_bandw[i]);
            targetBands[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setSpectralBandIndex(i);
            targetBands[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setNoDataValue(RESULT_MASK_VALUE);
            targetBands[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setNoDataValueUsed(true);
        }

        copyFlagBands(sourceProduct, targetProduct, productConfigurer);
        productConfigurer.copyBands(EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LATITUDE_BAND_NAME);
        productConfigurer.copyBands(EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LONGITUDE_BAND_NAME);
        productConfigurer.copyBands(EnvisatConstants.MERIS_AMORGOS_L1B_ALTIUDE_BAND_NAME);

        copyGeoCoding(sourceProduct, targetProduct, productConfigurer);

        FlagCoding resultFlagCoding = createResultFlagCoding();
        targetProduct.getFlagCodingGroup().add(resultFlagCoding);
        final Band resultFlagsOutputBand = targetProduct.addBand(RESULT_FLAGS_NAME, ProductData.TYPE_UINT16);
        resultFlagsOutputBand.setDescription("FUB/WeW WATER plugin specific flags");
        resultFlagsOutputBand.setSampleCoding(resultFlagCoding);

        productConfigurer.copyMasks();

        String flagNamePrefix = RESULT_FLAGS_NAME + ".";
        ProductNodeGroup<Mask> maskGroup = targetProduct.getMaskGroup();
        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[0].toLowerCase(), RESULT_ERROR_TEXT[0],
                                                sceneWidth, sceneHeight, flagNamePrefix + RESULT_ERROR_NAME[0],
                                                Color.cyan, 0.0f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[1].toLowerCase(), RESULT_ERROR_TEXT[1],
                                                sceneWidth, sceneHeight, flagNamePrefix + RESULT_ERROR_NAME[1],
                                                Color.green, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[2].toLowerCase(), RESULT_ERROR_TEXT[2],
                                                sceneWidth, sceneHeight, flagNamePrefix + RESULT_ERROR_NAME[2],
                                                Color.green, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[3].toLowerCase(), RESULT_ERROR_TEXT[3],
                                                sceneWidth, sceneHeight, flagNamePrefix + RESULT_ERROR_NAME[3],
                                                Color.yellow, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[4].toLowerCase(), RESULT_ERROR_TEXT[4],
                                                sceneWidth, sceneHeight, flagNamePrefix + RESULT_ERROR_NAME[4],
                                                Color.yellow, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[5].toLowerCase(), RESULT_ERROR_TEXT[5],
                                                sceneWidth, sceneHeight, flagNamePrefix + RESULT_ERROR_NAME[5],
                                                Color.orange, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[6].toLowerCase(), RESULT_ERROR_TEXT[6],
                                                sceneWidth, sceneHeight, flagNamePrefix + RESULT_ERROR_NAME[6],
                                                Color.orange, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[7].toLowerCase(), RESULT_ERROR_TEXT[7],
                                                sceneWidth, sceneHeight, flagNamePrefix + RESULT_ERROR_NAME[7],
                                                Color.blue, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[8].toLowerCase(), RESULT_ERROR_TEXT[8],
                                                sceneWidth, sceneHeight, flagNamePrefix + RESULT_ERROR_NAME[8],
                                                Color.blue, 0.5f));
        productConfigurer.copyTiePointGrids();
        productConfigurer.copyMetadata();
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
        sampleConfigurer.defineSample(index, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[13]);
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        configureBandnames(sampleConfigurer, output_Band_Names);
        int index = output_Band_Names.length;
        sampleConfigurer.defineSample(index++, "l1_flags");
        sampleConfigurer.defineSample(index, "result_flags");
    }

    private void configureBandnames(SampleConfigurer sampleConfigurer, String[] outputBandNames) {
        for (int i = 0; i < outputBandNames.length; i++) {
            sampleConfigurer.defineSample(i, outputBandNames[i]);
        }
    }

    /**
     * Copies all flag bands together with their flagcoding from the input product
     * to the outout product.
     *
     * @param inputProduct  The input product.
     * @param outputProduct The output product.
     * @param productConfigurer
     */
    private void copyFlagBands(Product inputProduct, Product outputProduct, ProductConfigurer productConfigurer) {
        ProductUtils.copyFlagBands(inputProduct, outputProduct, false);
        if (inputProduct.getFlagCodingGroup().getNodeCount() > 0) {
            // loop over bands and check if they have a flags coding attached
            for (int n = 0; n < inputProduct.getNumBands(); n++) {
                final Band band = inputProduct.getBandAt(n);
                if (band.getFlagCoding() != null) {
                    productConfigurer.copyBands(band.getName());
                }
            }
        }
    }

    /**
     * Adds the band name to the internal list of band which shall be copied.
     *
     * @param bandName The name of the band.
     *
     */
    private void addToBandNamesToCopy(String bandName) {
        bandNamesToCopy.add(bandName);
    }

    /**
     * Copies the band with the given {@code bandName} from the {@code inputProduct}
     * to the {@code outputProduct}, if the band exists in the {@code inputProduct}.
     * <p/>
     * The band is added to the copy list by calling {@link #addToBandNamesToCopy(String) addToBandNamesToCopy(bandName)}.
     *
     * @param bandName      The name of the band to be copied.
     * @param inputProduct  The input product.
     * @param outputProduct The output product.
     *
     */
    protected void copyBand(String bandName, Product inputProduct, Product outputProduct) {
        if (!outputProduct.containsBand(bandName)) {
            final Band band = ProductUtils.copyBand(bandName, inputProduct, outputProduct, false);
            if (band != null) {
                addToBandNamesToCopy(bandName);
            }
        }
    }

    /**
     * Copies the {@link GeoCoding geo-coding} from the input to the output product.
     *
     * @param inputProduct  The input product.
     * @param outputProduct The output product.
     * @param productConfigurer
     */
    protected void copyGeoCoding(Product inputProduct, Product outputProduct, ProductConfigurer productConfigurer) {
        Set<String> bandsToCopy = getBandNamesForGeoCoding(inputProduct, productConfigurer);
        for (String bandName : bandsToCopy) {
            productConfigurer.copyBands(bandName);
            final Band srcBand = inputProduct.getBand(bandName);
            final Band destBand = outputProduct.getBand(bandName);
            destBand.setSourceImage(srcBand.getSourceImage());
        }
        ProductUtils.copyGeoCoding(inputProduct, outputProduct);
    }

    private Set<String> getBandNamesForGeoCoding(Product inputProduct, ProductConfigurer productConfigurer) {
        Set<String> bandsToCopy = new LinkedHashSet<String>();
        GeoCoding geoCoding = inputProduct.getGeoCoding();
        if (geoCoding != null && geoCoding instanceof PixelGeoCoding) {
            PixelGeoCoding pixelGeoCoding = (PixelGeoCoding) geoCoding;
            final String[] bandNames = {pixelGeoCoding.getLonBand().getName(), pixelGeoCoding.getLatBand().getName()};
            productConfigurer.copyBands(bandNames);
            String validMask = pixelGeoCoding.getValidMask();
            if (validMask != null) {
                try {
                    RasterDataNode[] refRasters = BandArithmetic.getRefRasters(validMask, new Product[]{inputProduct});
                    for (RasterDataNode rasterDataNode : refRasters) {
                        productConfigurer.copyBands(rasterDataNode.getName());
                    }
                } catch (ParseException ignore) {
                }
            }
        }
        return bandsToCopy;
    }

    public static FlagCoding createResultFlagCoding() {
        FlagCoding resultFlagCoding = new FlagCoding("result_flags");
        resultFlagCoding.setDescription("RESULT Flag Coding");
        for (int i = 0; i < RESULT_ERROR_NUM; i++) {
            MetadataAttribute attribute = new MetadataAttribute(RESULT_ERROR_NAME[i], ProductData.TYPE_INT32);
            attribute.getData().setElemInt(RESULT_ERROR_VALUE[i]);
            attribute.setDescription(RESULT_ERROR_TEXT[i]);
            resultFlagCoding.addAttribute(attribute);
        }
        return resultFlagCoding;
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
