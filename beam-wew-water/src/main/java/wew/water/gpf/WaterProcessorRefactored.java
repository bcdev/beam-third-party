/*
 * $Id: WaterProcessor.java, MS0611030755
 *
 * Copyright (C) 2005/7 by WeW (michael.schaale@wew.fu-berlin.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package wew.water.gpf;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductWriter;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.processor.*;
import org.esa.beam.framework.processor.ui.ProcessorUI;
import org.esa.beam.processor.smac.SmacConstants;
import org.esa.beam.util.ProductUtils;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.util.io.FileUtils;
import wew.water.WaterProcessorOzone;
import wew.water.WaterProcessorUI;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * The <code>WaterProcessor</code> implements all specific functionality to calculate a RESULT
 * product from a given MERIS Level 1b product. This simple processor does not take any flags into
 * account, it just calculates the RESULT over the whole product.
 */

public class WaterProcessorRefactored extends Processor {

    // Constants
    public static final String PROCESSOR_NAME = "FUB/WeW Water processor";
    public static final String PROCESSOR_VERSION = "1.2.8";        // PROCESS
    public static final String PROCESSOR_COPYRIGHT = "Copyright (C) 2005/7 by WeW (michael.schaale@wew.fu-berlin.de)";

    public static final String LOGGER_NAME = "beam.processor.water";
    public static final String DEFAULT_LOG_PREFIX = "water";

    public static final String REQUEST_TYPE = "WATER";

    private static final Logger LOGGER = Logger.getLogger(LOGGER_NAME);


    // In createOutputProduct() watch out for
    // _resultFlagsOutputBand = new Band(RESULT_FLAGS_NAME, ProductData.TYPE_UINT16, sceneWidth, sceneHeight);
    // Here : Adapt he ProductData type length. Now : 16 Bit !!

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

    public static final String L1FLAGS_INPUT_BAND_NAME = "l1_flags";

    private static final int GLINT_RISK = 0x00000004;
    private static final int SUSPECT = 0x00000008;
    private static final int BRIGHT = 0x00000020;
    private static final int INVALID = 0x00000080;

    private static final String SZA_GRID_NAME = EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[6];
    private static final String SAA_GRID_NAME = EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[7];
    private static final String VZA_GRID_NAME = EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[8];
    private static final String VAA_GRID_NAME = EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[9];
    private static final String ZW_GRID_NAME = EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[10];
    private static final String MW_GRID_NAME = EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[11];
    private static final String PRESS_GRID_NAME = EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[12];
    private static final String O3_GRID_NAME = EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[13];

    static final String[] REQUIRED_TIE_POINT_GRID_NAMES = new String[]{
            SZA_GRID_NAME, SAA_GRID_NAME, VZA_GRID_NAME,
            VAA_GRID_NAME, ZW_GRID_NAME, MW_GRID_NAME,
            PRESS_GRID_NAME, O3_GRID_NAME
    };

    // Fields
    private Product _inputProduct;
    private Product _outputProduct;
    private Band _l1FlagsInputBand;
    private Band _l1FlagsOutputBand;
    private Band _resultFlagsOutputBand;
    private float[] solarFlux;
    private Band[] _inputSpectralBand = new Band[EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS];

    // The input type pattern for ICOL products
    static final String ICOL_PATTERN = "MER_.*1N";

    // - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS

    // the ozone concentration used in the MOMO simulation in Dobson units (1 DU = 1/1000. cm)
    public static final double TOTAL_OZONE_DU_MOMO = 344.0;

    private int numTopOfAtmosphereBands;
    private int num_msl;
    private int numOfConcentrationBands;
    private int numOfSpectralAerosolOpticalDepths = 4;

    private static final int MASK_TO_BE_USED = (GLINT_RISK | BRIGHT | INVALID);

    // ID strings for all possible output bands
    private static String[] _outputBandName = {
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
    private static String[] _outputBandDescription = {
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
    private static String[] _outputBandUnit = {
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

    private static final double[] DEFSOL = new double[]{
            1670.5964, 1824.1444, 1874.9883,
            1877.6682, 1754.7749, 1606.6401,
            1490.0026, 1431.8726, 1369.2035,
            1231.7164, 1220.0767, 1144.9675,
            932.3497, 904.8193, 871.0908
    };

    // Mask value to be written if inversion fails
    private static final double RESULT_MASK_VALUE = +5.0;

    // - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS

    private int output_planes;
    private Band[] _outputBand;

    @Override
    public void initProcessor() throws ProcessorException {
        // todo - how to install foreign auxdata
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        URL resourceBaseUrl;
        if (codeSource == null) {
            resourceBaseUrl = getClass().getResource("/");
        } else {
            resourceBaseUrl = codeSource.getLocation();
        }
        File targetlocation = new File(SystemUtils.getUserHomeDir(), ".beam/beam-ui/auxdata");
        try {
            installAuxdata(resourceBaseUrl, "auxdata/", targetlocation);
        } catch (IOException e) {
            throw new ProcessorException("Failed to install auxdata", e);
        }
    }

    /*
     * Worker method invoked by framework to process a single request.
     */
    @Override
    public void process(ProgressMonitor pm) throws ProcessorException {

        try {
            // Activate the logging process !
            Request request = getRequest();
            ProcessorUtils.setProcessorLoggingHandler(DEFAULT_LOG_PREFIX, request,
                                                      getName(), getVersion(), getCopyrightInformation());

            LOGGER.info("Started processing ...");

            pm.beginTask("FUB WeW Water processing...", 100);

            try {
                // check the request type
                Request.checkRequestType(getRequest(), REQUEST_TYPE);

                // create progress bar and add it to the processor's listener list
/*
            useProgressBar();
*/
                // load input product
                loadInputProduct();
                pm.worked(5);

                // create the output product
                createOutputProduct();
                pm.worked(5);

                // and process the water
                processWater(SubProgressMonitor.create(pm, 90));
            } finally {
                pm.done();
            }

            // Properly close products
            // Missing this results in a corrupted HDF5 files !!
            closeProducts();

            LOGGER.info(ProcessorConstants.LOG_MSG_SUCCESS);
        } catch (IOException e) {
            // catch all exceptions expect ProcessorException and throw ProcessorException
            throw new ProcessorException(e.getMessage());
        }
    }

    /*
     * Closes any open products.
     *
     * @throws IOException
     */

    public void closeProducts() throws IOException {
        if (_inputProduct != null) {
            _inputProduct.closeProductReader();
        }
        if (_outputProduct != null) {
            _outputProduct.closeProductWriter();
        }
    }

    /*
     * Retrieves the name of the processor
     */
    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }

    /*
     * Retrieves a version string of the processor
     */
    @Override
    public String getVersion() {
        return PROCESSOR_VERSION;
    }

    /*
     * Retrieves copyright information of the processor
     */
    @Override
    public String getCopyrightInformation() {
        return PROCESSOR_COPYRIGHT;
    }

    /*
     * Creates the UI for the processor. Override to perform processor specific
     * UI initializations.
     */
    @Override
    public ProcessorUI createUI() throws ProcessorException {
        return new WaterProcessorUI();
    }
    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /*
     * Loads the input product from the request. Opens all bands needed to
     * process the water.
     */

    private void loadInputProduct() throws ProcessorException, IOException {

        // only the first product - there might be more but these will be ignored
        _inputProduct = loadInputProduct(0);

        // check for required raster data nodes
        validateInputProduct(_inputProduct);

        for (int i = 0; i < 15; i++) {
            String radianceBandName = "radiance_" + (i + 1);
            Band radianceBand = _inputProduct.getBand(radianceBandName);
            if (radianceBand == null) {
                throw new ProcessorException(String.format("Missing input band '%s'.", radianceBandName));
            }
            if (radianceBand.getSpectralWavelength() <= 0.0) {
                throw new ProcessorException(String.format("Input band '%s' does not have wavelength information.", radianceBandName));
            }
            _inputSpectralBand[i] = radianceBand;
        }

        _l1FlagsInputBand = _inputProduct.getBand(L1FLAGS_INPUT_BAND_NAME);
        if (_l1FlagsInputBand == null) {
            throw new ProcessorException(String.format("Missing input band '%s'.", L1FLAGS_INPUT_BAND_NAME));
        }
        LOGGER.info(String.format("%s%s", ProcessorConstants.LOG_MSG_LOADED_BAND, L1FLAGS_INPUT_BAND_NAME));

        /*
       * Finally read solar flux for all MERIS L1b bands.
       */
        solarFlux = getSolarFlux(_inputProduct, _inputSpectralBand);
    }

    static void validateInputProduct(Product inputProduct) throws ProcessorException {
        List<String> requiredRasterNames = new ArrayList<String>(Arrays.asList(REQUIRED_TIE_POINT_GRID_NAMES));
        requiredRasterNames.add(L1FLAGS_INPUT_BAND_NAME);
        for (String requiredRasterName : requiredRasterNames) {
            if (!inputProduct.containsRasterDataNode(requiredRasterName)) {
                String message = String.format("Required raster data node '%s' is missing.", requiredRasterName);
                throw new ProcessorException(message);
            }
        }
    }

    /*
     * Reads the solar spectral fluxes for all MERIS L1b bands.
     *
     * Sometimes the file do not contain solar fluxes. As they do
     * show heavy variations over the year or for slight wavelength
     * shifts we do use some defaults if necessary.
     */
    private float[] getSolarFlux(Product product, Band[] bands) {

        // Try to grab the solar fluxes
        float[] dsf = getSolarFluxFromMetadata(product);
        if (dsf == null) {

            LOGGER.log(Level.WARNING, "No solar flux values found in input. Using default values.");

            dsf = new float[bands.length];
            for (int i = 0; i < bands.length; i++) {
                Band band = bands[i];
                dsf[i] = band.getSolarFlux();
                if (dsf[i] <= 0.0) {
                    dsf[i] = (float) DEFSOL[i];
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

    /*
     * Creates the output product skeleton.
     */
    private void createOutputProduct() throws ProcessorException, IOException {

        //ProductRef prod;
        //prod = getOutputProductSafe();
        String productType = getOutputProductTypeSafe();
        String productName = getOutputProductNameSafe();

        // get the request from the base class
        // -----------------------------------
        Request request = getRequest();

        // get the scene size from the input product
        // -----------------------------------------
        int sceneWidth = _inputProduct.getSceneRasterWidth();
        int sceneHeight = _inputProduct.getSceneRasterHeight();

        // get the output product from the request. The request holds objects of
        // type ProductRef which contain all the information needed here
        // --------------------------------------------------------------------
        ProductRef outputRef;

        // the request can contain any number of output products, we take the first ..
        outputRef = request.getOutputProductAt(0);
        if (outputRef == null) {
            throw new ProcessorException("No output product in request");
        }

        // create the in-memory representation of the output product
        // ---------------------------------------------------------
        // the product itself
        _outputProduct = new Product(productName, productType, sceneWidth, sceneHeight);

        // outputRef.getFileFormat() is either "BEAM-DIMAP" or "HDF5"
        ProductWriter writer = ProductIO.getProductWriter(outputRef.getFileFormat());

        // Attach to the writer to the output product
        _outputProduct.setProductWriter(writer);

        // adapt process parameters according to the request
        // -------------------------------------------------

        numOfConcentrationBands = 3;
        final int numOfSpectralWaterLeavingReflectances = 8;
        numTopOfAtmosphereBands = 12;
        num_msl = 8;

        output_planes = numOfSpectralAerosolOpticalDepths;     // tau
        output_planes += numOfSpectralWaterLeavingReflectances;    // water-leaving reflectances
        output_planes += numOfConcentrationBands;        // log(c)

        _outputBand = new Band[output_planes];

        // create and add the output bands
        //
        for (int i = 0; i < output_planes; i++) {
            _outputBand[i] = new Band(_outputBandName[i], ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
            _outputBand[i].setScalingOffset(0.0);
            _outputBand[i].setScalingFactor(1.0);
            _outputBand[i].setSpectralBandIndex(0);    // The Spectrum Tool in VISAT needs it !!
            _outputBand[i].setDescription(_outputBandDescription[i]);
            _outputBand[i].setUnit(_outputBandUnit[i]);
            _outputProduct.addBand(_outputBand[i]);
        }

        for (int i = 0; i < numOfSpectralAerosolOpticalDepths; i++) {
            _outputBand[numOfConcentrationBands + i].setSpectralWavelength(tau_lambda[i]);
            _outputBand[numOfConcentrationBands + i].setSpectralBandIndex(i);
            _outputBand[numOfConcentrationBands + i].setNoDataValue(RESULT_MASK_VALUE);
            _outputBand[numOfConcentrationBands + i].setNoDataValueUsed(true);
        }

        for (int i = 0; i < numOfSpectralWaterLeavingReflectances; i++) {
            _outputBand[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setSpectralWavelength(rho_w_lambda[i]);
            _outputBand[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setSpectralBandwidth(rho_w_bandw[i]);
            _outputBand[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setSpectralBandIndex(i);
            _outputBand[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setNoDataValue(RESULT_MASK_VALUE);
            _outputBand[numOfSpectralAerosolOpticalDepths + numOfConcentrationBands + i].setNoDataValueUsed(true);
        }

        ProductUtils.copyTiePointGrids(_inputProduct, _outputProduct);
        copyFlagBands(_inputProduct, _outputProduct);
        copyBand(EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LATITUDE_BAND_NAME, _inputProduct, _outputProduct);
        copyBand(EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LONGITUDE_BAND_NAME, _inputProduct, _outputProduct);
        copyBand(EnvisatConstants.MERIS_AMORGOS_L1B_ALTIUDE_BAND_NAME, _inputProduct, _outputProduct);

        copyGeoCoding(_inputProduct, _outputProduct);

        _l1FlagsOutputBand = _outputProduct.getBand(L1FLAGS_INPUT_BAND_NAME);

        // create and add the RESULT flags coding
        //
        FlagCoding resultFlagCoding = createResultFlagCoding();
        _outputProduct.getFlagCodingGroup().add(resultFlagCoding);

        // create and add the RESULT flags band
        //
        _resultFlagsOutputBand = new Band(RESULT_FLAGS_NAME, ProductData.TYPE_UINT16, sceneWidth, sceneHeight);
        _resultFlagsOutputBand.setDescription("FUB/WeW WATER plugin specific flags");
        _resultFlagsOutputBand.setSampleCoding(resultFlagCoding);
        _outputProduct.addBand(_resultFlagsOutputBand);

        // Copy predefined bitmask definitions
        ProductUtils.copyMasks(_inputProduct, _outputProduct);

        String falgNamePrefix = RESULT_FLAGS_NAME + ".";
        ProductNodeGroup<Mask> maskGroup = _outputProduct.getMaskGroup();
        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[0].toLowerCase(), RESULT_ERROR_TEXT[0],
                                                sceneWidth, sceneHeight, falgNamePrefix + RESULT_ERROR_NAME[0],
                                                Color.cyan, 0.0f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[1].toLowerCase(), RESULT_ERROR_TEXT[1],
                                                sceneWidth, sceneHeight, falgNamePrefix + RESULT_ERROR_NAME[1],
                                                Color.green, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[2].toLowerCase(), RESULT_ERROR_TEXT[2],
                                                sceneWidth, sceneHeight, falgNamePrefix + RESULT_ERROR_NAME[2],
                                                Color.green, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[3].toLowerCase(), RESULT_ERROR_TEXT[3],
                                                sceneWidth, sceneHeight, falgNamePrefix + RESULT_ERROR_NAME[3],
                                                Color.yellow, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[4].toLowerCase(), RESULT_ERROR_TEXT[4],
                                                sceneWidth, sceneHeight, falgNamePrefix + RESULT_ERROR_NAME[4],
                                                Color.yellow, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[5].toLowerCase(), RESULT_ERROR_TEXT[5],
                                                sceneWidth, sceneHeight, falgNamePrefix + RESULT_ERROR_NAME[5],
                                                Color.orange, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[6].toLowerCase(), RESULT_ERROR_TEXT[6],
                                                sceneWidth, sceneHeight, falgNamePrefix + RESULT_ERROR_NAME[6],
                                                Color.orange, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[7].toLowerCase(), RESULT_ERROR_TEXT[7],
                                                sceneWidth, sceneHeight, falgNamePrefix + RESULT_ERROR_NAME[7],
                                                Color.blue, 0.5f));

        maskGroup.add(Mask.BandMathsType.create(RESULT_ERROR_NAME[8].toLowerCase(), RESULT_ERROR_TEXT[8],
                                                sceneWidth, sceneHeight, falgNamePrefix + RESULT_ERROR_NAME[8],
                                                Color.blue, 0.5f));

        // Initialize the disk representation
        //
        writer.writeProductNodes(_outputProduct, new File(outputRef.getFilePath()));
        copyBandData(getBandNamesToCopy(), _inputProduct, _outputProduct, ProgressMonitor.NULL);

        LOGGER.info("Output product successfully created");
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

    /*
     * Retrieves the output product name from the input product name by appending "_FLH_MCI" to the name string.
     *
     * @throws org.esa.beam.framework.processor.ProcessorException
     *          when an error occurs
     */
    private String getOutputProductNameSafe() throws ProcessorException {
        Request request = getRequest();
        ProductRef prod = request.getOutputProductAt(0);
        if (prod == null) {
            throw new ProcessorException(ProcessorConstants.LOG_MSG_NO_OUTPUT_IN_REQUEST);
        }
        File prodFile = new File(prod.getFilePath());

        return FileUtils.getFilenameWithoutExtension(prodFile);
    }

    /*
     * Retrieves the output product type from the input product type by appending "_FLH_MCI" to the type string.
     *
     * @throws org.esa.beam.framework.processor.ProcessorException
     *          when an error occurs
     */
    private String getOutputProductTypeSafe() throws ProcessorException {
        String productType = _inputProduct.getProductType();
        if (productType == null) {
            throw new ProcessorException(ProcessorConstants.LOG_MSG_NO_INPUT_TYPE);
        }

        return productType + "_FLH_MCI";
    }

    /* ***********************************************************************
    * ***************************  THE PROCESSOR ****************************
    * ***********************************************************************

    *  ----------------------------------------------------------------------
       Performs the actual processing of the output product. Reads both input
       bands line by line, calculates the result and writes the result
       to the output band

       This subroutine makes use of Thomas Schroeder's validated networks
       as published in his PhD thesis 02.2005

   run19_C2_080_nn.pro  2-step atm. correction  : water leaving refl. (8)
                                                      aerosol opt. depths (4)

   run38_C2_040_nn.pro  1-step direct inversion : log(YEL)
   run39_C2_080_nn.pro  1-step direct inversion : log(SPM)
   run46_C2_100_nn.pro  1-step direct inversion : log(CHL)

    *  **********************************************************************
    */
    private void processWater(ProgressMonitor pm) throws ProcessorException, IOException {

        // If set to -1.0f : NN input and output ranges are checked
        // If set to +1.0f : NN input and output ranges are NOT checked
        float aset = -1.0f;

        int productWidth = _inputProduct.getSceneRasterWidth();
        int productHeight = _inputProduct.getSceneRasterHeight();
        int numInputSpectralBands = _inputSpectralBand.length;

        float[] sunZenithAngles = new float[productWidth];
        float[] sunAzimuthAngles = new float[productWidth];
        float[] viewZenithAngles = new float[productWidth];
        float[] viewAzimuthAngles = new float[productWidth];
        float[] zonalWinds = new float[productWidth];
        float[] meridianWinds = new float[productWidth];
        float[] airPressures = new float[productWidth];
        float[] ozones = new float[productWidth];

        // allocate memory for a multispectral scan line
        //
        float[][] topOfAtmosphere = new float[numInputSpectralBands][productWidth];
        int[] l1Flags = new int[productWidth];

        // allocate memory for the flags
        //
        int[] resultFlags = new int[productWidth];
        int[] resultFlagsNN = new int[productWidth];

        // Ozone data
        //
        double[] wavelength = new double[numInputSpectralBands];
        double[] exO3 = new double[numInputSpectralBands];

        // local variables
        //
        double degreeToRadian;
        float azimuthDiff;
        int l;
        int ls = 0;
        int n;

        int inodes = 1;
        int onodes_1;
        int onodes_2;

        float[][] ipixel;
        float[][] ipixels;

        // Load the wavelengths and ozone spectral extinction coefficients
        //
        for (int i = 0; i < numInputSpectralBands; i++) {
            wavelength[i] = _inputSpectralBand[i].getSpectralWavelength();
            exO3[i] = WaterProcessorOzone.O3excoeff(wavelength[i]);
        }

        // Load the auxiliary data
        //
        RasterDataNode szaGrid = getTiePointGrid(SZA_GRID_NAME);
        RasterDataNode saaGrid = getTiePointGrid(SAA_GRID_NAME);
        RasterDataNode vzaGrid = getTiePointGrid(VZA_GRID_NAME);
        RasterDataNode vaaGrid = getTiePointGrid(VAA_GRID_NAME);
        RasterDataNode zwGrid = getTiePointGrid(ZW_GRID_NAME);
        RasterDataNode mwGrid = getTiePointGrid(MW_GRID_NAME);
        RasterDataNode pressGrid = getTiePointGrid(PRESS_GRID_NAME);
        RasterDataNode o3Grid = getTiePointGrid(O3_GRID_NAME);

        degreeToRadian = Math.acos(-1.0) / 180.0;

        // Get the number of I/O nodes in advance
        // implicit atm.corr.
        inodes = ChlorophyllNetworkOperation.getNumberOfInputNodes();
        onodes_1 = ChlorophyllNetworkOperation.getNumberOfOutputNodes();
        // explicit atm.corr.
        onodes_2 = AtmosphericCorrectionNetworkOperation.getNumberOfOutputNodes();

        float[][] top = new float[numTopOfAtmosphereBands][productWidth];
        float[][] tops = new float[numTopOfAtmosphereBands][productWidth];
        double[][] o3f = new double[numTopOfAtmosphereBands][productWidth];
        float[][] sof = new float[numTopOfAtmosphereBands][productWidth];
        float[][] aux = new float[2][productWidth];
        float[][] geo = new float[4][productWidth];
        float[][] result = new float[output_planes][productWidth];

        // Some Level 1b scenes mark almost all pixels as 'SUSPECT'. This is obviously nonsense.
        // Because we would like to make use of the SUSPECT flag in mask MASK_TO_BE_USED we do
        // check first if it behaves fine, ie the number of suspect pixels for one line in the
        // middle of the scene should be below 50 % . Else we do not make use of the SUSPECT flag
        // in the mask MASK_TO_BE_USED.

        // Grab a line in the middle of the scene
        _l1FlagsInputBand.readPixels(0, productHeight / 2, productWidth, 1, l1Flags);
        boolean icolMode = _inputProduct.getProductType().matches(ICOL_PATTERN);
        int mask_to_be_used;
        if (icolMode) {
            mask_to_be_used = MASK_TO_BE_USED;
            System.out.println("--- Input product is of type icol ---");
            System.out.println("--- Switching to relaxed mask. ---");
        } else {
            mask_to_be_used = SUSPECT;
            int numSuspect = 0;
            // Now sum up the cases which signal a SUSPECT behaviour
            for (int x = 0; x < productWidth; x++) {
                if ((l1Flags[x] & mask_to_be_used) != 0) {
                    numSuspect++;
                }
            }
            // lower than 50 percent ?
            if (numSuspect < productWidth / 2) {
                // Make use of the SUSPECT flag
                mask_to_be_used = MASK_TO_BE_USED | SUSPECT;
            } else {
                // Forget it ....
                mask_to_be_used = MASK_TO_BE_USED;
                final float percent = (float) numSuspect / (float) productWidth * 100.0f;
                System.out.println("--- " + percent + " % of the scan line are marked as SUSPECT ---");
                System.out.println("--- Switching to relaxed mask. ---");
            }
        }

        // Notify process listeners that processing has started
        pm.beginTask("Analyzing water pixels...", productHeight);

        try {
            // for all required bands loop over all scanlines
            //
            for (int y = 0; y < productHeight; y++) {

                // read the input data line by line
                //

                // First the TOA radiances
                for (n = 0; n < _inputSpectralBand.length; n++) {
                    _inputSpectralBand[n].readPixels(0, y, productWidth, 1, topOfAtmosphere[n]);
                } // n

                // Second the flags
                _l1FlagsInputBand.readPixels(0, y, productWidth, 1, l1Flags);

                // Third the auxiliary data
                szaGrid.readPixels(0, y, productWidth, 1, sunZenithAngles);
                saaGrid.readPixels(0, y, productWidth, 1, sunAzimuthAngles);
                vzaGrid.readPixels(0, y, productWidth, 1, viewZenithAngles);
                vaaGrid.readPixels(0, y, productWidth, 1, viewAzimuthAngles);
                zwGrid.readPixels(0, y, productWidth, 1, zonalWinds);
                mwGrid.readPixels(0, y, productWidth, 1, meridianWinds);
                pressGrid.readPixels(0, y, productWidth, 1, airPressures);
                o3Grid.readPixels(0, y, productWidth, 1, ozones);


                // process the complete scanline
                //
                ipixel = new float[inodes][productWidth];
                final float[] nnIpixel = new float[inodes];
                float[] nnOpixel1 = new float[onodes_1];
                float[] nnOpixel2 = new float[onodes_2];
                for (int x = 0; x < productWidth; x++) {
                    resultFlags[x] = 0;
                    resultFlagsNN[x] = 0;

                    // Exclude pixels from processing if the following l1flags mask becomes true
                    int k = l1Flags[x] & mask_to_be_used;
                    if (k != 0) {
                        resultFlags[x] = RESULT_ERROR_VALUE[0];
                    }

                    // *********************
                    // * STAGE 0
                    // *********************

                    // Get the toa reflectances for selected bands
                    // and normalize ozone
                    //
                    l = 0;
                    for (n = 0; n <= 6; n++, l++) {
                        tops[l][x] = topOfAtmosphere[n][x];
                        sof[l][x] = solarFlux[n];
                        top[l][x] = topOfAtmosphere[n][x] / solarFlux[n];
                        o3f[l][x] = Math.exp(-(TOTAL_OZONE_DU_MOMO - ozones[x]) * exO3[n] / 1000.0 * (1.0 / Math.cos(
                                (double) viewZenithAngles[x] * degreeToRadian) + 1.0 / Math.cos((double) sunZenithAngles[x] * degreeToRadian)));
                        top[l][x] *= o3f[l][x];
                    }
                    for (n = 8; n <= 9; n++, l++) {
                        tops[l][x] = topOfAtmosphere[n][x];
                        sof[l][x] = solarFlux[n];
                        top[l][x] = topOfAtmosphere[n][x] / solarFlux[n];
                        o3f[l][x] = Math.exp(-(TOTAL_OZONE_DU_MOMO - ozones[x]) * exO3[n] / 1000.0 * (1.0 / Math.cos(
                                (double) viewZenithAngles[x] * degreeToRadian) + 1.0 / Math.cos((double) sunZenithAngles[x] * degreeToRadian)));
                        top[l][x] *= o3f[l][x];
                    }
                    for (n = 11; n <= 13; n++, l++) {
                        tops[l][x] = topOfAtmosphere[n][x];
                        sof[l][x] = solarFlux[n];
                        top[l][x] = topOfAtmosphere[n][x] / solarFlux[n];
                        o3f[l][x] = Math.exp(-(TOTAL_OZONE_DU_MOMO - ozones[x]) * exO3[n] / 1000.0 * (1.0 / Math.cos(
                                (double) viewZenithAngles[x] * degreeToRadian) + 1.0 / Math.cos((double) sunZenithAngles[x] * degreeToRadian)));
                        top[l][x] *= o3f[l][x];
                    }

                    // Prepare some auxiliary vectors
                    //

                    // Get the wind speed
                    aux[0][x] = (float) Math.sqrt((double) (zonalWinds[x] * zonalWinds[x] + meridianWinds[x] * meridianWinds[x]));
                    // Get the pressure
                    aux[1][x] = airPressures[x];

                    // Adjust the azimuth difference
                    azimuthDiff = viewAzimuthAngles[x] - sunAzimuthAngles[x];

                    while (azimuthDiff <= -180.0f) {
                        azimuthDiff += 360.0f;
                    }
                    while (azimuthDiff > 180.0f) {
                        azimuthDiff -= 360.0f;
                    }
                    float tmp = azimuthDiff;
                    if (tmp >= 0.0f) {
                        azimuthDiff = +180.0f - azimuthDiff;
                    }
                    if (tmp < 0.0f) {
                        azimuthDiff = -180.0f - azimuthDiff;
                    }

                    // Get cos(sunzen)
                    geo[0][x] = (float) Math.cos((double) sunZenithAngles[x] * degreeToRadian);

                    // And now transform into cartesian coordinates
                    geo[1][x] = (float) (Math.sin((double) viewZenithAngles[x] * degreeToRadian) * Math.cos((double) azimuthDiff * degreeToRadian)); // obs_x
                    geo[2][x] = (float) (Math.sin((double) viewZenithAngles[x] * degreeToRadian) * Math.sin((double) azimuthDiff * degreeToRadian)); // obs_y
                    geo[3][x] = (float) (Math.cos((double) viewZenithAngles[x] * degreeToRadian));                            // obs_z

                // *********************
                // * STAGE 1-4
                // *********************



                    // load the TOA reflectances
                    for (l = 0; l < numTopOfAtmosphereBands; l++) {
                        ipixel[l][x] = top[l][x];
                    }

                    // get the wind speed and pressure
                    ipixel[l++][x] = aux[0][x];
                    ipixel[l++][x] = aux[1][x];

                    // get cos(sunzen), x, y, z
                    ipixel[l++][x] = geo[0][x];
                    ipixel[l++][x] = geo[1][x];
                    ipixel[l++][x] = geo[2][x];
                    ipixel[l++][x] = geo[3][x];

                    // Run the 1-step chlorophyll, yellow substance and total suspended matter network;
                    for (int i = 0; i < nnIpixel.length; i++) {
                        nnIpixel[i] = ipixel[i][x];
                    }
                    resultFlagsNN[x] |= ChlorophyllNetworkOperation.compute(nnIpixel, nnOpixel1);
                    result[0][x] = nnOpixel1[0];

                    for (int i = 0; i < nnIpixel.length; i++) {
                        nnIpixel[i] = ipixel[i][x];
                    }
                    resultFlagsNN[x] |= YellowSubstanceNetworkOperation.compute(nnIpixel, nnOpixel1);
                    result[1][x] = nnOpixel1[0];

                    for (int i = 0; i < nnIpixel.length; i++) {
                        nnIpixel[i] = ipixel[i][x];
                    }
                    resultFlagsNN[x] |= TotalSuspendedMatterNetworkOperation.compute(nnIpixel, nnOpixel1);
                    result[2][x] = nnOpixel1[0];

                    // Run part 1 of the 2-step atm.corr. network;
                    for (int i = 0; i < nnIpixel.length; i++) {
                        nnIpixel[i] = ipixel[i][x];
                    }
                    resultFlagsNN[x] |= AtmosphericCorrectionNetworkOperation.compute(nnIpixel, nnOpixel2);
                    // The aots
                    for (int i = num_msl; i < nnOpixel2.length; i++) {
                        result[numOfConcentrationBands + i - num_msl][x] = nnOpixel2[i];
                    }
                    for (int i = 0; i < num_msl; i++) {
                        result[numOfConcentrationBands + numOfSpectralAerosolOpticalDepths + i][x] = nnOpixel2[i];
                    }

                    // OK, the whole scanline had been processed. Now check for error flags !
                    // If set, set output vector to mask value !
                    if (resultFlags[x] != 0) {
                        for (n = 0; n < output_planes; n++) {
                            result[n][x] = (float) RESULT_MASK_VALUE;
                        }
                    }
                    // Combine result flags
                    resultFlags[x] |= resultFlagsNN[x];
                }


                // Write the result planes
                //
                for (n = 0; n < output_planes; n++) {
                    _outputBand[n].writePixels(0, y, productWidth, 1, result[n]);
                }

                // write the flag planes
                //
                _resultFlagsOutputBand.writePixels(0, y, productWidth, 1, resultFlags);
                _l1FlagsOutputBand.writePixels(0, y, productWidth, 1, l1Flags);

                pm.worked(1);
                if (pm.isCanceled()) {
                    // 'Cancel' was pressed, processing will be terminated now !
                    // --> Completely remove output product
                    _outputProduct.getProductWriter().deleteOutput();
                    // Immediately terminate now
                    return;
                } // if

            } // y
        } finally {
            // Notify processing success
            pm.done();
        }

    } // processWater

    private int getErrorFlags(float errorIndicator, int offset) {
        final int errorOffset = 2 * offset;
        // Input range failure
        if (errorIndicator == -2f) {
            return RESULT_ERROR_VALUE[errorOffset - 1];
        }
        // Output range failure
        if (errorIndicator == -19f) {
            return RESULT_ERROR_VALUE[errorOffset];
        }
        // Input AND Output range failure
        if (errorIndicator == -22f) {
            return RESULT_ERROR_VALUE[errorOffset - 1] | RESULT_ERROR_VALUE[errorOffset];
        }
        return 0;
    }

    private RasterDataNode getTiePointGrid(String rasterName) throws ProcessorException {
        RasterDataNode latGrid = _inputProduct.getRasterDataNode(rasterName);
        checkParamNotNull(latGrid, rasterName);
        LOGGER.fine(SmacConstants.LOG_MSG_LOADED + rasterName);
        return latGrid;
    }

} // Processor

