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

package wew.water;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import org.esa.beam.dataio.dimap.DimapProductConstants;
import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductWriter;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.processor.Processor;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.framework.processor.ProcessorUtils;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.Request;
import org.esa.beam.framework.processor.ui.ProcessorUI;
import org.esa.beam.processor.smac.SmacConstants;
import org.esa.beam.util.ProductUtils;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.util.io.FileUtils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * The <code>WaterProcessor</code> implements all specific functionality to calculate a RESULT
 * product from a given MERIS Level 1b product. This simple processor does not take any flags into
 * account, it just calculates the RESULT over the whole product.
 */

public class WaterProcessor extends Processor {

    // Constants
    public static final String PROCESSOR_NAME = "FUB/WeW Water processor";
    public static final String PROCESSOR_VERSION = "1.2";        // PROCESS
    public static final String PROCESSOR_COPYRIGHT = "Copyright (C) 2005/7 by WeW (michael.schaale@wew.fu-berlin.de)";

    public static final String LOGGER_NAME = "beam.processor.water";
    public static final String DEFAULT_LOG_PREFIX = "water";

    public final static String DEFAULT_OUTPUT_DIR_NAME = "OUTPUT_WATER";
    public final static String DEFAULT_OUTPUT_FORMAT = DimapProductConstants.DIMAP_FORMAT_NAME;
    public static final String DEFAULT_OUTPUT_PRODUCT_NAME = "water";

    public final static boolean CHECKBOX1_DEFAULT = false;
    public final static String CHECKBOX1_LABEL_TEXT = "normal output";
    public final static String CHECKBOX1_DESCRIPTION = "select/unselect";
    public final static String CHECKBOX1_PARAM_NAME = "Normout";
    private boolean normout = CHECKBOX1_DEFAULT;

    public final static boolean CHECKBOX2_DEFAULT = true;
    public final static String CHECKBOX2_LABEL_TEXT = "two-step inversion";
    public final static String CHECKBOX2_DESCRIPTION = "select/unselect";
    public final static String CHECKBOX2_PARAM_NAME = "Extout";
    private boolean extout = CHECKBOX2_DEFAULT;

    public final static boolean CHECKBOX3_DEFAULT = false;
    public final static String CHECKBOX3_LABEL_TEXT = "case I water";
    public final static String CHECKBOX3_DESCRIPTION = "select/unselect";
    public final static String CHECKBOX3_PARAM_NAME = "caseI";
    private boolean caseI = CHECKBOX3_DEFAULT;

    public final static boolean CHECKBOX4_DEFAULT = true;
    public final static String CHECKBOX4_LABEL_TEXT = "case II water";
    public final static String CHECKBOX4_DESCRIPTION = "select/unselect";
    public final static String CHECKBOX4_PARAM_NAME = "caseII";
    private boolean caseII = CHECKBOX4_DEFAULT;

    public final static boolean CHECKBOX5_DEFAULT = true;
    public final static String CHECKBOX5_LABEL_TEXT = "TOA Ozone normalization";
    public final static String CHECKBOX5_DESCRIPTION = "select/unselect";
    public final static String CHECKBOX5_PARAM_NAME = "ozone_norm";
    private boolean ozone_norm = CHECKBOX5_DEFAULT;

    public final static boolean CHECKBOX6_DEFAULT = false;
    public final static String CHECKBOX6_LABEL_TEXT = "Rayleigh pre-processing";
    public final static String CHECKBOX6_DESCRIPTION = "Select/unselect";
    public final static String CHECKBOX6_PARAM_NAME = "ray_corr";
    private boolean ray_corr = CHECKBOX6_DEFAULT;

    public static final String REQUEST_TYPE = "WATER";

    public static final String RESULT_PRODUCT_TYPE = "MER_MLP_WATER2P";

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

    public int COSMETIC = 0x00000001;
    public int DUPLICATED = 0x00000002;
    public int GLINT_RISK = 0x00000004;
    public int SUSPECT = 0x00000008;
    public int LAND_OCEAN = 0x00000010;
    public int BRIGHT = 0x00000020;
    public int COASTLINE = 0x00000040;
    public int INVALID = 0x00000080;

    // Fields
    private ArrayList<Band> _inputBandList;
    private Product _inputProduct;
    private Product _outputProduct;
    private Band _l1FlagsInputBand;
    private Band _l1FlagsOutputBand;
    private Band _resultFlagsOutputBand;
    private Logger _logger;
    private float[] solarFlux;
    private Band[] _inputBand = new Band[EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS];

    // This product string does so far (061103) not exist in 
    // org.esa.beam.dataio.envisat.EnvisatConstants. This is the reason
    // why Thomas Lankester experienced a failure of our plug in
    // when he tried to use a MERIS data set named 
    // MER_FRS_1PNUPA20051005_073634_000000702041_00221_18812_0082.N1.
    //
    // This is now fixed
    String MERIS_FRS_L1B_PRODUCT_TYPE_NAME = "MER_FRS_1P";

    // - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS  

    // the ozone concentration used in the MOMO simulation in Dobson units (1 DU = 1/1000. cm)
    public final static double TOTAL_OZONE_DU_MOMO = 344.0;

    private static int num_toa_caseII = 12;
    private int num_toa;

    private static int num_msl_caseII = 8;
    private int num_msl;

    // # of concentration bands
    private static int num_c_caseII = 3;
    private int num_c;

    // # of spectral aerosol optical depths
    private int num_tau = 4;

    // # of spectral water leaving reflectances
    private static int num_rho_w_normout = 0;
    private static int num_rho_w_extout = 8;
    private int num_rho_w;

    private int MASK_TO_BE_USED = (GLINT_RISK | BRIGHT | INVALID);
//    private int MASK_TO_BE_USED = (0);

    // ID strings for all possible output bands
    private static String _outputBandName[] = {
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
    private static String _outputBandDescription[] = {
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
    private static String _outputBandUnit[] = {
            "log10(mg/m^3)",
            "log10(1/m)",
            "log10(g/m^3)",
            "/",
            "/",
            "/",
            "/",
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
    private static float tau_lambda[] = {
            440.00f, 550.00f, 670.00f, 870.00f
    };

    // Wavelengths for the water leaving reflectances rho_w
    private static float rho_w_lambda[] = {
            412.50f, 442.50f, 490.00f, 510.00f,
            560.00f, 620.00f, 665.00f, 708.75f
    };

    // Bandwidth for the water leaving reflectances rho_w
    private static float rho_w_bandw[] = {
            10.00f, 10.00f, 10.00f, 10.00f,
            10.00f, 10.00f, 10.00f, 10.00f
    };

    // Mask value to be written if inversion fails 
    private float RESULT_MASK_VALUE = +5.0f;    // constant

    // - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS - PROCESS  

    private int output_planes;
    private Band[] _outputBand;
    private static final String ICOL_PATTERN = "MER_.*L1N";

    public WaterProcessor() {
        _logger = Logger.getLogger(LOGGER_NAME);
    }

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
    public void process(ProgressMonitor pm) throws ProcessorException {

        try {
            // Activate the logging process !
            Request request = getRequest();
            ProcessorUtils.setProcessorLoggingHandler(DEFAULT_LOG_PREFIX, request,
                                                      getName(), getVersion(), getCopyrightInformation());

            _logger.info("Started processing ...");

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

            _logger.info(ProcessorConstants.LOG_MSG_SUCCESS);
        }
        catch (IOException e) {
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
    public String getName() {
        return PROCESSOR_NAME;
    }

    /*
     * Retrieves a version string of the processor
     */
    public String getVersion() {
        return PROCESSOR_VERSION;
    }

    /*
     * Retrieves copyright information of the processor
     */
    public String getCopyrightInformation() {
        return PROCESSOR_COPYRIGHT;
    }

    /*
     * Creates the UI for the processor. Override to perform processor specific
     * UI initializations.
     */
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
        _inputBandList = new ArrayList<Band>();

        // clear vector of bands
        // ---------------------
        _inputBandList.clear();

        // only the first product - there might be more but these will be ignored
        // ----------------------------------------------------------------------
        _inputProduct = loadInputProduct(0);

        // Allow MERIS only !!
        //
        if (!EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME.equals(_inputProduct.getProductType())
            && !EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME.equals(_inputProduct.getProductType())
            && !EnvisatConstants.MERIS_FSG_L1B_PRODUCT_TYPE_NAME.equals(_inputProduct.getProductType())
            && !EnvisatConstants.MERIS_FRG_L1B_PRODUCT_TYPE_NAME.equals(_inputProduct.getProductType())
            && !MERIS_FRS_L1B_PRODUCT_TYPE_NAME.equals(_inputProduct.getProductType())) {
            throw new ProcessorException("Invalid product type: MERIS Level 1b required.");
        }


        String[] bandNames = _inputProduct.getBandNames();

        for (String bandName : bandNames) {
            Band band = _inputProduct.getBand(bandName);

            if (band == null) {
                _logger.warning("The requested band '" + bandName + "' is not contained in the input product!");
            } else {
                if (band.getSpectralBandIndex() != -1) {
                    _inputBandList.add(band);
                } else {
                    _logger.warning(
                            "The requested band '" + bandName + "' is not a spectral band! It is excluded from processing");
                }
            }
        }

        for (int i = 0; i < _inputBandList.size(); i++) {
            _inputBand[i] = _inputProduct.getBand(bandNames[i]);
        }

        _l1FlagsInputBand = _inputProduct.getBand(L1FLAGS_INPUT_BAND_NAME);
        if (_l1FlagsInputBand == null) {
            throw new ProcessorException("Can not load band " + L1FLAGS_INPUT_BAND_NAME);
        }
        _logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND + L1FLAGS_INPUT_BAND_NAME);

        /*
       * Finally read solar flux for all MERIS L1b bands.
       */
        solarFlux = getSolarFlux(_inputProduct, _inputBandList);
    }

    /*
     * Reads the solar spectral fluxes for all MERIS L1b bands.
     *
     * Sometimes the file do not contain solar fluxes. As they do
     * show heavy variations over the year or for slight wavelength
     * shifts we do use some defaults if necessary.  
     */
    private float[] getSolarFlux(Product product, ArrayList<Band> numbands) {
        // Try to grab the solar fluxes
        float[] dsf = getSolarFluxFromMetadata(product);

        // todo - try also to get solar flux from bands

        if (dsf == null) {
            _logger.log(Level.WARNING, "No solar flux values found. Using some default values");
            double defsol[] = {
                    1670.5964, 1824.1444, 1874.9883,
                    1877.6682, 1754.7749, 1606.6401,
                    1490.0026, 1431.8726, 1369.2035,
                    1231.7164, 1220.0767, 1144.9675,
                    932.3497, 904.8193, 871.0908
            };

            // Prepare the defaults
            dsf = new float[numbands.size()];
            for (int i = 0; i < numbands.size(); i++) {
                dsf[i] = (float) 1.0;
                if (i < defsol.length) {
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

        if (caseII) {
            num_c = num_c_caseII;
        }
        if (normout) {
            num_rho_w = num_rho_w_normout;
        }
        if (extout) {
            num_rho_w = num_rho_w_extout;
        }
        if (caseII) {
            num_toa = num_toa_caseII;
        }
        if (caseII) {
            num_msl = num_msl_caseII;
        }

        output_planes = num_tau;     // tau
        output_planes += num_rho_w;    // water-leaving reflectances
        output_planes += num_c;        // log(c)

        _outputBand = new Band[output_planes];

        // create and add the output bands
        //
        int j = 0;
        for (int i = 0; i < output_planes; i++) {
            _outputBand[i] = new Band(_outputBandName[j], ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
            _outputBand[i].setScalingOffset(0.0);
            _outputBand[i].setScalingFactor(1.0);
            _outputBand[i].setSpectralBandIndex(0);    // The Spectrum Tool in VISAT needs it !!
            _outputBand[i].setDescription(_outputBandDescription[j]);
            _outputBand[i].setUnit(_outputBandUnit[j]);
            _outputProduct.addBand(_outputBand[i]);
            j++;
        }

        for (int i = 0; i < num_c; i++) {
//    	    _outputBand[i].setSpectralWavelength((float) i);
            _outputBand[i].setSpectralWavelength((float) (i + 1));
        }

        for (int i = 0; i < num_tau; i++) {
            _outputBand[i + num_c].setSpectralWavelength(tau_lambda[i]);
        }

        if (extout) {
            for (int i = 0; i < num_rho_w; i++) {
                _outputBand[num_tau + num_c + i].setSpectralWavelength(rho_w_lambda[i]);
                _outputBand[num_tau + num_c + i].setSpectralBandwidth(rho_w_bandw[i]);
            }
        }

        // copy all tie point grids to output product
        //
        ProductUtils.copyTiePointGrids(_inputProduct, _outputProduct);

        // copy geo-coding and the lat/lon tiepoints to the output product
        //
        ProductUtils.copyGeoCoding(_inputProduct, _outputProduct);

        // copy L1b flag band
        //
        ProductUtils.copyFlagBands(_inputProduct, _outputProduct);
        _l1FlagsOutputBand = _outputProduct.getBand(L1FLAGS_INPUT_BAND_NAME);

        // create and add the RESULT flags coding
        //
        FlagCoding resultFlagCoding = createResultFlagCoding();
        _outputProduct.addFlagCoding(resultFlagCoding);

        // create and add the RESULT flags band
        //
        _resultFlagsOutputBand = new Band(RESULT_FLAGS_NAME, ProductData.TYPE_UINT16, sceneWidth, sceneHeight);
        _resultFlagsOutputBand.setDescription("FUB/WeW WATER plugin specific flags");
        _resultFlagsOutputBand.setFlagCoding(resultFlagCoding);
        _outputProduct.addBand(_resultFlagsOutputBand);

        // Copy predefined bitmask definitions
        ProductUtils.copyBitmaskDefs(_inputProduct, _outputProduct);

        _outputProduct.addBitmaskDef(new BitmaskDef(RESULT_ERROR_NAME[0].toLowerCase(),
                                                    RESULT_ERROR_TEXT[0],
                                                    RESULT_FLAGS_NAME + "." + RESULT_ERROR_NAME[0],
                                                    Color.cyan, 0.0f));

        _outputProduct.addBitmaskDef(new BitmaskDef(RESULT_ERROR_NAME[1].toLowerCase(),
                                                    RESULT_ERROR_TEXT[1],
                                                    RESULT_FLAGS_NAME + "." + RESULT_ERROR_NAME[1],
                                                    Color.green, 0.5f));

        _outputProduct.addBitmaskDef(new BitmaskDef(RESULT_ERROR_NAME[2].toLowerCase(),
                                                    RESULT_ERROR_TEXT[2],
                                                    RESULT_FLAGS_NAME + "." + RESULT_ERROR_NAME[2],
                                                    Color.green, 0.5f));

        _outputProduct.addBitmaskDef(new BitmaskDef(RESULT_ERROR_NAME[3].toLowerCase(),
                                                    RESULT_ERROR_TEXT[3],
                                                    RESULT_FLAGS_NAME + "." + RESULT_ERROR_NAME[3],
                                                    Color.yellow, 0.5f));

        _outputProduct.addBitmaskDef(new BitmaskDef(RESULT_ERROR_NAME[4].toLowerCase(),
                                                    RESULT_ERROR_TEXT[4],
                                                    RESULT_FLAGS_NAME + "." + RESULT_ERROR_NAME[4],
                                                    Color.yellow, 0.5f));

        _outputProduct.addBitmaskDef(new BitmaskDef(RESULT_ERROR_NAME[5].toLowerCase(),
                                                    RESULT_ERROR_TEXT[5],
                                                    RESULT_FLAGS_NAME + "." + RESULT_ERROR_NAME[5],
                                                    Color.orange, 0.5f));

        _outputProduct.addBitmaskDef(new BitmaskDef(RESULT_ERROR_NAME[6].toLowerCase(),
                                                    RESULT_ERROR_TEXT[6],
                                                    RESULT_FLAGS_NAME + "." + RESULT_ERROR_NAME[6],
                                                    Color.orange, 0.5f));

        _outputProduct.addBitmaskDef(new BitmaskDef(RESULT_ERROR_NAME[7].toLowerCase(),
                                                    RESULT_ERROR_TEXT[7],
                                                    RESULT_FLAGS_NAME + "." + RESULT_ERROR_NAME[7],
                                                    Color.blue, 0.5f));

        _outputProduct.addBitmaskDef(new BitmaskDef(RESULT_ERROR_NAME[8].toLowerCase(),
                                                    RESULT_ERROR_TEXT[8],
                                                    RESULT_FLAGS_NAME + "." + RESULT_ERROR_NAME[8],
                                                    Color.blue, 0.5f));

        // Initialize the disk representation
        //
        writer.writeProductNodes(_outputProduct, new File(outputRef.getFilePath()));

        _logger.info("Output product successfully created");
    }

    public static FlagCoding createResultFlagCoding() {

        FlagCoding resultFlagCoding = new FlagCoding("result_flags");
        resultFlagCoding.setDescription("RESULT Flag Coding");

        MetadataAttribute attribute;

        for (int i = 0; i < RESULT_ERROR_NUM; i++) {
            attribute = new MetadataAttribute(RESULT_ERROR_NAME[i], ProductData.TYPE_INT32);
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

        int i;

        int _merisLatIndex = 0;
        TiePointGrid _latBand;

        int _merisLonIndex = 1;
        TiePointGrid _lonBand;

        int _merisElevIndex = 2;
        TiePointGrid _elevBand;

        int _merisLatcorrIndex = 4;
        TiePointGrid _latcorrBand;

        int _merisLoncorrIndex = 5;
        TiePointGrid _loncorrBand;

        int _merisSzaIndex = 6;
        TiePointGrid _szaBand;

        int _merisSaaIndex = 7;
        TiePointGrid _saaBand;

        int _merisVzaIndex = 8;
        TiePointGrid _vzaBand;

        int _merisVaaIndex = 9;
        TiePointGrid _vaaBand;

        int _merisZwIndex = 10;
        TiePointGrid _zwBand;

        int _merisMwIndex = 11;
        TiePointGrid _mwBand;

        int _merisPressIndex = 12;
        TiePointGrid _pressBand;

        int _merisO3Index = 13;
        TiePointGrid _o3Band;

        int _merisWvIndex = 14;
        TiePointGrid _wvBand;

        // If set to -1.0f : NN input and output ranges are checked
        // If set to +1.0f : NN input and output ranges are NOT checked
        float aset = -1.0f;

        int width = _inputProduct.getSceneRasterWidth();
        int height = _inputProduct.getSceneRasterHeight();
        int nbands = _inputBandList.size();

        float[] lat = new float[width];
        float[] lon = new float[width];
        float[] latc = new float[width];
        float[] lonc = new float[width];
        float[] elev = new float[width];
        float[] sza = new float[width];
        float[] saa = new float[width];
        float[] vza = new float[width];
        float[] vaa = new float[width];
        float[] zw = new float[width];
        float[] mw = new float[width];
        float[] press = new float[width];
        float[] wv = new float[width];
        float[] o3 = new float[width];

        // allocate memory for a multispectral scan line
        //
        float[][] toa = new float[nbands][width];
        int[] l1Flags = new int[width];

        // allocate memory for the flags
        //
        int[] resultFlags = new int[width];
        int[] resultFlagsNN = new int[width];
        int mask_to_be_used;

        // Ozone data
        //
        double[] wavelength = new double[nbands];
        double[] exO3 = new double[nbands];

        // local variables
        //
        double d2r;
        float dazi;
        int k, l, ls = 0, n, x;
        float[] a = new float[width];

        int inodes = 1, onodes = 1, inodes_1, inodes_2, onodes_1, onodes_2, stage;

        float[][] ipixel = new float[2][1];
        float[][] ipixels = new float[2][1];
        float[][] opixel = new float[2][1];

        RecallBCK recallBCK = new RecallBCK();
/*
	// ----- ASCII Output ------ 
	FileOutputStream vector=null,    vectoro=null,   toav=null;
    	FileOutputStream outlat=null,    outlon=null;
    	FileOutputStream outc=null,      outy=null,      outs=null;
    	FileOutputStream outt440=null,   outt550=null,   outt670=null,   outt870=null;
    	FileOutputStream outr412=null,   outr442=null,   outr490=null,   outr510=null;
    	FileOutputStream outr560=null,   outr620=null,   outr665=null,   outr708=null;
	FileOutputStream outtoa885=null, outm1=null,     outm2=null;

	PrintStream      vectorps=null,  vectorops=null, toavps=null;
	PrintStream      outlatps=null,  outlonps=null;
	PrintStream      outcps=null,    outyps=null,    outsps=null;
    	PrintStream      outtps440=null, outtps550=null, outtps670=null, outtps870=null;
    	PrintStream      outrps412=null, outrps442=null, outrps490=null, outrps510=null;
    	PrintStream      outrps560=null, outrps620=null, outrps665=null, outrps708=null;
	PrintStream      outtoaps885=null, outmps1=null, outmps2=null;

	DecimalFormat    SciForm  = new DecimalFormat("0.00000000E000#");
	DecimalFormat    SciForm0 = new DecimalFormat("0");
*/
        // Load the wavelengths and ozone spectral extinction coefficients
        //
        for (i = 0; i < nbands; i++) {
            wavelength[i] = _inputBand[i].getSpectralWavelength();
            exO3[i] = WaterProcessorOzone.O3excoeff(wavelength[i]);
        }

        // Load the auxiliary data
        //

        // latitude
        _latBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLatIndex]);
        checkParamNotNull(_latBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLatIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLatIndex]);

        // longitude
        _lonBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLonIndex]);
        checkParamNotNull(_lonBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLonIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLonIndex]);

        // latitude correction factors
        _latcorrBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLatcorrIndex]);
        checkParamNotNull(_latcorrBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLatcorrIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLatcorrIndex]);

        // longitude correction factors
        _loncorrBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLoncorrIndex]);
        checkParamNotNull(_loncorrBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLoncorrIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisLoncorrIndex]);

        // digital elevation
        _elevBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisElevIndex]);
        checkParamNotNull(_elevBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisElevIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisElevIndex]);

        // sun zenith angle
        _szaBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisSzaIndex]);
        checkParamNotNull(_szaBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisSzaIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisSzaIndex]);

        // sun azimuth angle
        _saaBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisSaaIndex]);
        checkParamNotNull(_saaBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisSaaIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisSaaIndex]);

        // view zenith angle
        _vzaBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisVzaIndex]);
        checkParamNotNull(_vzaBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisVzaIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisVzaIndex]);

        // view azimuth angle
        _vaaBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisVaaIndex]);
        checkParamNotNull(_vaaBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisVaaIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisVaaIndex]);

        // zonal wind
        _zwBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisZwIndex]);
        checkParamNotNull(_zwBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisZwIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisZwIndex]);

        // merid wind
        _mwBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisMwIndex]);
        checkParamNotNull(_mwBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisMwIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisMwIndex]);

        // atmospheric pressure
        _pressBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisPressIndex]);
        checkParamNotNull(_pressBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisPressIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisPressIndex]);

        // ozone
        _o3Band = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisO3Index]);
        checkParamNotNull(_o3Band, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisO3Index]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisO3Index]);

        // waterVapour
        _wvBand = _inputProduct.getTiePointGrid(EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisWvIndex]);
        checkParamNotNull(_wvBand, EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisWvIndex]);
        _logger.fine(SmacConstants.LOG_MSG_LOADED + EnvisatConstants.MERIS_TIE_POINT_GRID_NAMES[_merisWvIndex]);

        d2r = Math.acos(-1.0) / 180.0;
        float fd2r = (float) d2r;

        // Get the number of I/O nodes in advance
        // implicit atm.corr.
        inodes_1 = recallBCK.lrecall_run38_C2_040_nn(ipixel, -1, opixel, 1, width, resultFlags, 0, a);
        onodes_1 = recallBCK.lrecall_run38_C2_040_nn(ipixel, 1, opixel, -1, width, resultFlags, 0, a);

        // explicit atm.corr.
        inodes_2 = recallBCK.lrecall_run19_C2_080_nn(ipixel, -1, opixel, 1, width, resultFlags, 0, a);
        onodes_2 = recallBCK.lrecall_run19_C2_080_nn(ipixel, 1, opixel, -1, width, resultFlags, 0, a);

        float[][] top = new float[num_toa][width];
        float[][] tops = new float[num_toa][width];
        double[][] o3f = new double[num_toa][width];
        float[][] sof = new float[num_toa][width];
        float[][] aux = new float[2][width];
        float[][] geo = new float[4][width];
        float[][] result = new float[output_planes][width];

        // Some Level 1b scenes mark almost all pixels as 'SUSPECT'. This is obviously nonsense.
        // Because we would like to make use of the SUSPECT flag in mask MASK_TO_BE_USED we do
        // check first if it behaves fine, ie the number of suspect pixels for one line in the
        // middle of the scene should be below 50 % . Else we do not make use of the SUSPECT flag
        // in the mask MASK_TO_BE_USED.

        // Grab a line in the middle of the scene
        _l1FlagsInputBand.readPixels(0, height / 2, width, 1, l1Flags);
        boolean icolMode = _inputProduct.getProductType().matches(ICOL_PATTERN);
        if(icolMode){
            mask_to_be_used = MASK_TO_BE_USED;
            System.out.println("--- Input product is of type icol ---");
            System.out.println("--- Switching to relaxed mask. ---");
        } else {
            mask_to_be_used = SUSPECT;
            k = 0;
            // Now sum up the cases which signal a SUSPECT behaviour
            for (x = 0; x < width; x++) {
                if ((l1Flags[x] & mask_to_be_used) != 0) {
                    k++;
                }
            }
            // lower than 50 percent ?
            if (k < width / 2)
            // Make use of the SUSPECT flag
            {
                mask_to_be_used = MASK_TO_BE_USED | SUSPECT;
            } else {
                // Forget it ....
                mask_to_be_used = MASK_TO_BE_USED;
                final float percent = (float) k / (float) width * 100.0f;
                System.out.println("--- " + percent + " % of the scan line are marked as SUSPECT ---");
                System.out.println("--- Switching to relaxed mask. ---");
            }
        }

        // Notify process listeners that processing has started
        pm.beginTask("Analyzing water pixels...", height);

        try {
            // for all required bands loop over all scanlines
            //
            for (int y = 0; y < height; y++) {

                // read the input data line by line
                //

                // First the TOA radiances
                for (n = 0; n < _inputBandList.size(); n++) {
                    _inputBand[n].readPixels(0, y, width, 1, toa[n]);
                } // n

                // Second the flags
                _l1FlagsInputBand.readPixels(0, y, width, 1, l1Flags);

                // Third the auxiliary data
                _latBand.readPixels(0, y, width, 1, lat);
                _lonBand.readPixels(0, y, width, 1, lon);
                _latcorrBand.readPixels(0, y, width, 1, latc);
                _loncorrBand.readPixels(0, y, width, 1, lonc);
                _elevBand.readPixels(0, y, width, 1, elev);
                _szaBand.readPixels(0, y, width, 1, sza);
                _saaBand.readPixels(0, y, width, 1, saa);
                _vzaBand.readPixels(0, y, width, 1, vza);
                _vaaBand.readPixels(0, y, width, 1, vaa);
                _zwBand.readPixels(0, y, width, 1, zw);
                _mwBand.readPixels(0, y, width, 1, mw);
                _pressBand.readPixels(0, y, width, 1, press);
                _o3Band.readPixels(0, y, width, 1, o3);
                _wvBand.readPixels(0, y, width, 1, wv);

                // process the complete scanline
                //
                for (x = 0; x < width; x++) {
                    resultFlags[x] = 0;
                    resultFlagsNN[x] = 0;

                    // Exclude pixels from processing if the following l1flags mask becomes true
                    k = l1Flags[x] & mask_to_be_used;
                    if (k != 0) {
                        resultFlags[x] = RESULT_ERROR_VALUE[0];
                    }

                    // *********************
                    // * STAGE 0
                    // *********************

                    // Get the toa reflectances for selected bands
                    // and normalize ozone (ozone_norm is fixed to TRUE)
                    //
                    l = 0;
                    for (n = 0; n <= 6; n++, l++) {
                        tops[l][x] = toa[n][x];
                        sof[l][x] = solarFlux[n];
                        top[l][x] = toa[n][x] / solarFlux[n];
                        if (ozone_norm) {
                            o3f[l][x] = Math.exp(-(TOTAL_OZONE_DU_MOMO - o3[x]) * exO3[n] / 1000.0 * (1.0 / Math.cos(
                                    (double) vza[x] * d2r) + 1.0 / Math.cos((double) sza[x] * d2r)));
                            top[l][x] *= o3f[l][x];
                        }
                    }
                    for (n = 8; n <= 9; n++, l++) {
                        tops[l][x] = toa[n][x];
                        sof[l][x] = solarFlux[n];
                        top[l][x] = toa[n][x] / solarFlux[n];
                        if (ozone_norm) {
                            o3f[l][x] = Math.exp(-(TOTAL_OZONE_DU_MOMO - o3[x]) * exO3[n] / 1000.0 * (1.0 / Math.cos(
                                    (double) vza[x] * d2r) + 1.0 / Math.cos((double) sza[x] * d2r)));
                            top[l][x] *= o3f[l][x];
                        }
                    }
                    for (n = 11; n <= 13; n++, l++) {
                        tops[l][x] = toa[n][x];
                        sof[l][x] = solarFlux[n];
                        top[l][x] = toa[n][x] / solarFlux[n];
                        if (ozone_norm) {
                            o3f[l][x] = Math.exp(-(TOTAL_OZONE_DU_MOMO - o3[x]) * exO3[n] / 1000.0 * (1.0 / Math.cos(
                                    (double) vza[x] * d2r) + 1.0 / Math.cos((double) sza[x] * d2r)));
                            top[l][x] *= o3f[l][x];
                        }
                    }

                    // Prepare some auxiliary vectors
                    //

                    // Get the wind speed
                    aux[0][x] = (float) Math.sqrt((double) (zw[x] * zw[x] + mw[x] * mw[x]));
                    // Get the pressure
                    aux[1][x] = (float) press[x];

                    // Adjust the azimuth difference
                    dazi = vaa[x] - saa[x];

                    while (dazi <= -180.0f) {
                        dazi += 360.0f;
                    }
                    while (dazi > 180.0f) {
                        dazi -= 360.0f;
                    }
                    float tmp = dazi;
                    if (tmp >= 0.0f) {
                        dazi = +180.0f - dazi;
                    }
                    if (tmp < 0.0f) {
                        dazi = -180.0f - dazi;
                    }

                    // Get cos(sunzen)
                    geo[0][x] = (float) Math.cos((double) sza[x] * d2r);

                    // And now transform into cartesian coordinates
                    geo[1][x] = (float) (Math.sin((double) vza[x] * d2r) * Math.cos((double) dazi * d2r)); // obs_x
                    geo[2][x] = (float) (Math.sin((double) vza[x] * d2r) * Math.sin((double) dazi * d2r)); // obs_y
                    geo[3][x] = (float) (Math.cos((double) vza[x] * d2r));                            // obs_z
                } // x

                // *********************
                // * STAGE 1-4
                // *********************

                inodes = inodes_1; // They are all the same !!
                onodes = onodes_1; // They differ !!

                ipixel = new float[inodes][width];
                ipixels = new float[inodes][width];
                opixel = new float[onodes][width];

                for (x = 0; x < width; x++) {
                    // load the TOA reflectances
                    for (l = 0; l < num_toa; l++) {
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

                    // Check against range limits inside the network
                    // recall if the value of a[x] is set to -1.0f.
                    //
                    // This results in the application of the flag
                    // 'RESULT_ERROR_VALUE[]' to the 'resultFlagsNN'
                    a[x] = aset;

                    // Save input pixel
                    ls = l;
                    for (l = 0; l < ls; l++) {
                        ipixels[l][x] = ipixel[l][x];
                    }

                }

                // Run the 1-step chlorophyll network;
                stage = 1;
                recallBCK.lrecall_run46_C2_100_nn(ipixel, inodes, opixel, onodes, width, resultFlags, 0, a);
                for (x = 0; x < width; x++) {
                    // Input range failure
                    if ((a[x] > -2.1) && (a[x] < -1.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage - 1];
                    }
                    // Output range failure
                    if ((a[x] > -19.1) && (a[x] < -18.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage];
                    }
                    // Input AND Output range failure
                    if ((a[x] > -22.1) && (a[x] < -21.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage - 1];
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage];
                    }
                    result[0][x] = opixel[0][x];
                }

                // Run the 1-step yellow substance network;
                stage = 2;
                for (x = 0; x < width; x++) {
                    // reload the pixel
                    for (l = 0; l < ls; l++) {
                        ipixel[l][x] = ipixels[l][x];
                    }
                    a[x] = aset;
                }
                recallBCK.lrecall_run38_C2_040_nn(ipixel, inodes, opixel, onodes, width, resultFlags, 0, a);
                for (x = 0; x < width; x++) {
                    // Input range failure
                    if ((a[x] > -2.1) && (a[x] < -1.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage - 1];
                    }
                    // Output range failure
                    if ((a[x] > -19.1) && (a[x] < -18.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage];
                    }
                    // Input AND Output range failure
                    if ((a[x] > -22.1) && (a[x] < -21.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage - 1];
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage];
                    }
                    result[1][x] = opixel[0][x];
                }
                // Run the 1-step total suspended matter network;
                stage = 3;
                for (x = 0; x < width; x++) {
                    // reload the pixel
                    for (l = 0; l < ls; l++) {
                        ipixel[l][x] = ipixels[l][x];
                    }
                    a[x] = aset;
                }
                recallBCK.lrecall_run39_C2_080_nn(ipixel, inodes, opixel, onodes, width, resultFlags, 0, a);
                for (x = 0; x < width; x++) {
                    // Input range failure
                    if ((a[x] > -2.1) && (a[x] < -1.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage - 1];
                    }
                    // Output range failure
                    if ((a[x] > -19.1) && (a[x] < -18.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage];
                    }
                    // Input AND Output range failure
                    if ((a[x] > -22.1) && (a[x] < -21.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage - 1];
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage];
                    }
                    result[2][x] = opixel[0][x];
                }

                // Run part 1 of the 2-step atm.corr. network;
                stage = 4;
                onodes = onodes_2;
                opixel = new float[onodes][width];
                for (x = 0; x < width; x++) {
                    // reload the pixel
                    for (l = 0; l < ls; l++) {
                        ipixel[l][x] = ipixels[l][x];
                    }
                    a[x] = aset;
                }

                recallBCK.lrecall_run19_C2_080_nn(ipixel, inodes, opixel, onodes, width, resultFlags, 0, a);
                for (x = 0; x < width; x++) {
                    //System.out.print("--" + y + "--" + x + "--> " + a[x] + " : " + resultFlagsNN[x]);
                    // Input range failure
                    if ((a[x] > -2.1) && (a[x] < -1.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage - 1];
                    }
                    // Output range failure
                    if ((a[x] > -19.1) && (a[x] < -18.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage];
                    }
                    // Input AND Output range failure
                    if ((a[x] > -22.1) && (a[x] < -21.9)) {
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage - 1];
                        resultFlagsNN[x] |= RESULT_ERROR_VALUE[2 * stage];
                    }
                    //System.out.println(" --> " + resultFlagsNN[x]);

                    // The aots
                    for (i = num_msl; i < onodes; i++) {
                        result[num_c + i - num_msl][x] = opixel[i][x];
                    }
                    if (extout) {
                        for (i = 0; i < num_msl; i++) {
                            result[num_c + num_tau + i][x] = opixel[i][x];
                        }
                    }
                }

                // OK, the whole scanline had been processed. Now check for error flags !
                // If set, set output vector to mask value !
                for (x = 0; x < width; x++) {
                    if (resultFlags[x] != 0) {
                        for (n = 0; n < output_planes; n++) {
                            result[n][x] = RESULT_MASK_VALUE;
                        }
                    }
                    // Combine result flags
                    resultFlags[x] |= resultFlagsNN[x];
                }

                // Write the result planes
                //
                for (n = 0; n < output_planes; n++) {
                    _outputBand[n].writePixels(0, y, width, 1, result[n]);
                }

                // write the flag planes
                //
                _resultFlagsOutputBand.writePixels(0, y, width, 1, resultFlags);
                _l1FlagsOutputBand.writePixels(0, y, width, 1, l1Flags);

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

} // Processor

