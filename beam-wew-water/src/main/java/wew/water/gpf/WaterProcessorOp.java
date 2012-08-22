package wew.water.gpf;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.*;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.util.ProductUtils;
import org.esa.beam.util.StringUtils;
import wew.water.*;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;

@OperatorMetadata(alias = "WaterProcessorOp",
                  version = "1.0",
                  description = "FUB/WeW WATER Processor GPF-Operator")
public class WaterProcessorOp extends PixelOperator {

    private final static String[] output_concentration_band_names = {
            "algal_2",
            "yellow_subs",
            "total_susp"
    };
    private final static String[] output_optical_depth_band_names = {
            "aero_opt_thick_440",
            "aero_opt_thick_550",
            "aero_opt_thick_670",
            "aero_opt_thick_870"
    };
    private final static String[] output_reflectance_band_names = {
            "reflec_1",
            "reflec_2",
            "reflec_3",
            "reflec_4",
            "reflec_5",
            "reflec_6",
            "reflec_7",
            "reflec_9"
    };

    private static String[] output_concentration_band_descriptions = {
            "Chlorophyll 2 content",
            "Yellow substance",
            "Total suspended matter"
    };
    private static String[] output_optical_depth_band_descriptions = {
            "Aerosol optical thickness",
            "Aerosol optical thickness",
            "Aerosol optical thickness",
            "Aerosol optical thickness"
    };

    private static String[] output_reflectance_band_descriptions = {
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance",
            "RS reflectance"
    };

    private static String[] output_concentration_band_units = {
            "log10(mg/m^3)",
            "log10(1/m)",
            "log10(g/m^3)"
    };

    private static String[] output_optical_depth_band_units = {
            "1",
            "1",
            "1",
            "1"
    };

    private static String[] output_reflectance_band_units = {
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

    private static final String l1_flags_name = "l1_flags";

    private static final String result_flags_name = "result_flags";

    // Mask value to be written if inversion fails
    private static final double result_mask_value = +5.0;

    private static final String[] result_error_texts = {
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

    private static final String[] result_error_names = {
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
    private static final int[] result_error_values = {
            0x00000001,
            0x00000002,
            0x00000004,
            0x00000008,
            0x00000010,
            0x00000020,
            0x00000040,
            0x00000080,
            0x00000100
    };

    private static final float[] result_error_transparencies = {
            0.0f,
            0.5f,
            0.5f,
            0.5f,
            0.5f,
            0.5f,
            0.5f,
            0.5f,
            0.5f
    };

    private static final int GLINT_RISK = 0x00000004;
    private static final int SUSPECT = 0x00000008;
    private static final int BRIGHT = 0x00000020;
    private static final int INVALID = 0x00000080;
    private static final int MASK_TO_BE_USED = (GLINT_RISK | BRIGHT | INVALID);

    @SourceProduct(label = "Select source product",
                   description = "The MERIS L1b source product used for the processing.")
    private Product sourceProduct;
    private int maskToBeUsed;
    private float[] solarFlux;
    private double[] exO3;
    private double[] wavelength;

    private Sample getSampleFromSampleArray(Sample[] samples, String name) {
        for (Sample sample : samples) {
            if (sample.getNode().getName().equals(name)) {
                return sample;
            }
        }
        return null;
    }

    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        //todo refactor

        int resultFlag = 0;
        int resultFlagNN = 0;

        final Sample l1FlagsSample = getSampleFromSampleArray(sourceSamples, l1_flags_name);

        // Exclude pixels from processing if the following l1flags mask becomes true
        int k = l1FlagsSample.getInt() & maskToBeUsed;
        if (k != 0) {
            resultFlag = result_error_values[0];
        }

        // *********************
        // * STAGE 0
        // *********************

        // Get the toa reflectances for selected bands
        // and normalize ozone
        //

        int l = 0;
        double degreeToRadian = Math.acos(-1.0) / 180.0;
        int numTopOfAtmosphereBands = 12;
        float[] top = new float[numTopOfAtmosphereBands];
        float[] tops = new float[numTopOfAtmosphereBands];
        double[] o3f = new double[numTopOfAtmosphereBands];
        float[] sof = new float[numTopOfAtmosphereBands];
        float[] aux = new float[2];
        float[] geo = new float[4];
        float[] result = new float[output_concentration_band_names.length +
                output_optical_depth_band_names.length + output_reflectance_band_names.length];
        float[] topOfAtmosphere = new float[EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS];
        double totalOzoneInDU = 344.0;
        final Sample sunZenithSample = getSampleFromSampleArray(sourceSamples, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[6]);
        float sunZenithAngle = sunZenithSample.getFloat();
        final Sample viewZenithAngleSample = getSampleFromSampleArray(sourceSamples, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[8]);
        float viewZenithAngle = viewZenithAngleSample.getFloat();
        final Sample o3Sample = getSampleFromSampleArray(sourceSamples, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[13]);
        float ozone = o3Sample.getFloat();
        for (int n = 0; n <= 6; n++, l++) {
            tops[l] = topOfAtmosphere[n];
            sof[l] = solarFlux[n];
            top[l] = topOfAtmosphere[n] / solarFlux[n];
            o3f[l] = Math.exp(-(totalOzoneInDU - ozone) * exO3[n] / 1000.0 * (1.0 / Math.cos(
                    (double) viewZenithAngle * degreeToRadian) + 1.0 / Math.cos((double) sunZenithAngle * degreeToRadian)));
            top[l] *= o3f[l];
        }
        for (int n = 8; n <= 9; n++, l++) {
            tops[l] = topOfAtmosphere[n];
            sof[l] = solarFlux[n];
            top[l] = topOfAtmosphere[n] / solarFlux[n];
            o3f[l] = Math.exp(-(totalOzoneInDU - ozone) * exO3[n] / 1000.0 * (1.0 / Math.cos(
                    (double) viewZenithAngle * degreeToRadian) + 1.0 / Math.cos((double) sunZenithAngle * degreeToRadian)));
            top[l] *= o3f[l];
        }
        for (int n = 11; n <= 13; n++, l++) {
            tops[l] = topOfAtmosphere[n];
            sof[l] = solarFlux[n];
            top[l] = topOfAtmosphere[n] / solarFlux[n];
            o3f[l] = Math.exp(-(totalOzoneInDU - ozone) * exO3[n] / 1000.0 * (1.0 / Math.cos(
                    (double) viewZenithAngle * degreeToRadian) + 1.0 / Math.cos((double) sunZenithAngle * degreeToRadian)));
            top[l] *= o3f[l];
        }

        //todo continue shifting code from WaterProcessorRefactored


    }

    /*
    * Reads the solar spectral fluxes for all MERIS L1b bands.
    *
    * Sometimes the file do not contain solar fluxes. As they do
    * show heavy variations over the year or for slight wavelength
    * shifts we do use some defaults if necessary.
    */
    private float[] getSolarFlux(Product product, Band[] bands) {
        float[] dsf = getSolarFluxFromMetadata(product);
        if (dsf == null) {
            dsf = new float[bands.length];
            final double[] defsol = new double[]{
                    1670.5964, 1824.1444, 1874.9883,
                    1877.6682, 1754.7749, 1606.6401,
                    1490.0026, 1431.8726, 1369.2035,
                    1231.7164, 1220.0767, 1144.9675,
                    932.3497, 904.8193, 871.0908
            };
            for (int i = 0; i < bands.length; i++) {
                Band band = bands[i];
                dsf[i] = band.getSolarFlux();
                if (dsf[i] <= 0.0) {
                    dsf[i] = (float) defsol[i];
                }
            }
        }
        return dsf;
    }

    private float[] getSolarFluxFromMetadata(Product product) {
        MetadataElement metadataRoot = product.getMetadataRoot();
        MetadataElement gadsElem = metadataRoot.getElement("Scaling_Factor_GADS");
        if (gadsElem != null) {
            MetadataAttribute solarFluxAtt = gadsElem.getAttribute("sun_spec_flux");
            if (solarFluxAtt != null) {
                return (float[]) solarFluxAtt.getDataElems();
            }
        }
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();  //Todo change body of created method. Use File | Settings | File Templates to change
    }

    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();

        Band[] radianceBands = new Band[EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS];
        for (int i = 0; i < radianceBands.length; i++) {
            String radianceBandName = "radiance_" + (i + 1);
            Band radianceBand = sourceProduct.getBand(radianceBandName);
            if (radianceBand == null) {
                throw new OperatorException(String.format("Missing input band '%s'.", radianceBandName));
            }
            if (radianceBand.getSpectralWavelength() <= 0.0) {
                throw new OperatorException(String.format("Input band '%s' does not have wavelength information.", radianceBandName));
            }
            radianceBands[i] = radianceBand;
        }
        solarFlux = getSolarFlux(sourceProduct, radianceBands);

        // Load the wavelengths and ozone spectral extinction coefficients
        //
        exO3 = new double[EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS];
        wavelength = new double[EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS];
        for (int i = 0; i < exO3.length; i++) {
            wavelength[i] = radianceBands[i].getSpectralWavelength();
            exO3[i] = wew.water.WaterProcessorOzone.O3excoeff(wavelength[i]);
        }

        // Some Level 1b scenes mark almost all pixels as 'SUSPECT'. This is obviously nonsense.
        // Because we would like to make use of the SUSPECT flag in mask MASK_TO_BE_USED we do
        // check first if it behaves fine, ie the number of suspect pixels for one line in the
        // middle of the scene should be below 50 % . Else we do not make use of the SUSPECT flag
        // in the mask MASK_TO_BE_USED.

        // Grab a line in the middle of the scene

        final Band l1FlagsBand = sourceProduct.getBand(l1_flags_name);
        final int height = sourceProduct.getSceneRasterHeight();
        final int width = sourceProduct.getSceneRasterWidth();
        final int[] l1Flags = new int[width];
        final String icolPattern = "MER_.*1N";

        try {
            l1FlagsBand.readPixels(0, height / 2, width, 1, l1Flags);
        } catch (IOException e) {
            throw new OperatorException(e.getMessage());
        }

        boolean icolMode = sourceProduct.getProductType().matches(icolPattern);
        if (icolMode) {
            maskToBeUsed = MASK_TO_BE_USED;
            System.out.println("--- Input product is of type icol ---");
            System.out.println("--- Switching to relaxed mask. ---");
        } else {
            maskToBeUsed = SUSPECT;
            int numSuspect = 0;
            // Now sum up the cases which signal a SUSPECT behaviour
            for (int x = 0; x < width; x++) {
                if ((l1Flags[x] & maskToBeUsed) != 0) {
                    numSuspect++;
                }
            }
            // lower than 50 percent ?
            if (numSuspect < width / 2) {
                // Make use of the SUSPECT flag
                maskToBeUsed = MASK_TO_BE_USED | SUSPECT;
            } else {
                // Forget it ....
                maskToBeUsed = MASK_TO_BE_USED;
                final float percent = (float) numSuspect / (float) width * 100.0f;
                System.out.println("--- " + percent + " % of the scan line are marked as SUSPECT ---");
                System.out.println("--- Switching to relaxed mask. ---");
            }
        }
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);
        final Product sourceProduct = productConfigurer.getSourceProduct();
        final Product targetProduct = productConfigurer.getTargetProduct();

        targetProduct.setProductType(getOutputProductTypeSafe());

        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();

        addConcentrationBands(targetProduct, sceneWidth, sceneHeight);
        addOpticalDepthBands(targetProduct, sceneWidth, sceneHeight);
        addReflectanceBands(targetProduct, sceneWidth, sceneHeight);
        ProductUtils.copyFlagBands(sourceProduct, targetProduct, false);
        productConfigurer.copyBands(EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LATITUDE_BAND_NAME);
        productConfigurer.copyBands(EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LONGITUDE_BAND_NAME);
        productConfigurer.copyBands(EnvisatConstants.MERIS_AMORGOS_L1B_ALTIUDE_BAND_NAME);

        FlagCoding resultFlagCoding = createResultFlagCoding();
        targetProduct.getFlagCodingGroup().add(resultFlagCoding);
        final Band resultFlagsOutputBand = targetProduct.addBand(result_flags_name, ProductData.TYPE_UINT16);
        resultFlagsOutputBand.setDescription("FUB/WeW WATER plugin specific flags");
        resultFlagsOutputBand.setSampleCoding(resultFlagCoding);

        productConfigurer.copyMasks();

        String flagNamePrefix = result_flags_name + ".";
        addMasksToTargetProduct(targetProduct, sceneWidth, sceneHeight, flagNamePrefix);
        productConfigurer.copyMetadata();
    }

    private void addMasksToTargetProduct(Product targetProduct, int sceneWidth, int sceneHeight, String flagNamePrefix) {
        ProductNodeGroup<Mask> maskGroup = targetProduct.getMaskGroup();
        Color[] colors = new Color[]{
                Color.cyan, Color.green, Color.green, Color.yellow, Color.yellow,
                Color.orange, Color.orange, Color.blue, Color.blue
        };
        for (int i = 0; i < result_error_names.length; i++) {
            maskGroup.add(Mask.BandMathsType.create(result_error_names[i].toLowerCase(), result_error_texts[i],
                                                    sceneWidth, sceneHeight, flagNamePrefix + result_error_names[i],
                                                    colors[i], result_error_transparencies[i]));

        }
    }

    private void addReflectanceBands(Product targetProduct, int sceneWidth, int sceneHeight) {
        for (int i = 0; i < output_reflectance_band_names.length; i++) {
            final Band band = createBand(output_reflectance_band_names[i], sceneWidth, sceneHeight);
            band.setDescription(output_reflectance_band_descriptions[i]);
            band.setUnit(output_reflectance_band_units[i]);
            band.setSpectralWavelength(rho_w_lambda[i]);
            band.setSpectralBandwidth(rho_w_bandw[i]);
            band.setSpectralBandIndex(i);
            band.setNoDataValue(result_mask_value);
            band.setNoDataValueUsed(true);
            targetProduct.addBand(band);
        }
    }

    private void addOpticalDepthBands(Product targetProduct, int sceneWidth, int sceneHeight) {
        for (int i = 0; i < output_optical_depth_band_names.length; i++) {
            final Band band = createBand(output_optical_depth_band_names[i], sceneWidth, sceneHeight);
            band.setDescription(output_optical_depth_band_descriptions[i]);
            band.setUnit(output_optical_depth_band_units[i]);
            band.setSpectralWavelength(tau_lambda[i]);
            band.setSpectralBandIndex(i);
            band.setNoDataValue(result_mask_value);
            band.setNoDataValueUsed(true);
            targetProduct.addBand(band);
        }
    }

    private void addConcentrationBands(Product targetProduct, int sceneWidth, int sceneHeight) {
        for (int i = 0; i < output_concentration_band_names.length; i++) {
            final Band band = createBand(output_concentration_band_names[i], sceneWidth, sceneHeight);
            band.setDescription(output_concentration_band_descriptions[i]);
            band.setUnit(output_concentration_band_units[i]);
            targetProduct.addBand(band);
        }
    }

    private Band createBand(String bandName, int sceneWidth, int sceneHeight) {
        final Band band = new Band(bandName, ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
        band.setScalingOffset(0.0);
        band.setScalingFactor(1.0);
        band.setSpectralBandIndex(0);
        return band;
    }

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        final String[] bandNames = EnvisatConstants.MERIS_L1B_BAND_NAMES;
        configureBandnames(sampleConfigurer, bandNames);
        int index = bandNames.length;
        sampleConfigurer.defineSample(index++, l1_flags_name);
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
        String[] bandNames = StringUtils.addArrays(output_concentration_band_names, output_optical_depth_band_names);
        bandNames = StringUtils.addArrays(bandNames, output_reflectance_band_names);


        configureBandnames(sampleConfigurer, bandNames);
        int index = bandNames.length;
        sampleConfigurer.defineSample(index++, l1_flags_name);
        sampleConfigurer.defineSample(index++, result_flags_name);
    }

    private void configureBandnames(SampleConfigurer sampleConfigurer, String[] outputBandNames) {
        for (int i = 0; i < outputBandNames.length; i++) {
            sampleConfigurer.defineSample(i, outputBandNames[i]);
        }
    }

    public static FlagCoding createResultFlagCoding() {
        FlagCoding resultFlagCoding = new FlagCoding(result_flags_name);
        resultFlagCoding.setDescription("RESULT Flag Coding");
        for (int i = 0; i < result_error_names.length; i++) {
            MetadataAttribute attribute = new MetadataAttribute(result_error_names[i], ProductData.TYPE_INT32);
            attribute.getData().setElemInt(result_error_values[i]);
            attribute.setDescription(result_error_texts[i]);
            resultFlagCoding.addAttribute(attribute);
        }
        return resultFlagCoding;
    }

    private int appendFlags(int resultFlagNN, float ax, int stage) {
        // Input range failure
        if ((ax > -2.1) && (ax < -1.9)) {
            resultFlagNN |= result_error_values[2 * stage - 1];
        }
        // Output range failure
        if ((ax > -19.1) && (ax < -18.9)) {
            resultFlagNN |= result_error_values[2 * stage];
        }
        // Input AND Output range failure
        if ((ax > -22.1) && (ax < -21.9)) {
            resultFlagNN |= result_error_values[2 * stage - 1];
            resultFlagNN |= result_error_values[2 * stage];
        }
        return resultFlagNN;
    }

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
