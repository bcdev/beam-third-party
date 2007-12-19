package org.esa.beam.processor.baer;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import com.bc.jexp.Term;
import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.dataio.ProductWriter;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.processor.Processor;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.framework.processor.ProcessorUtils;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.Request;
import org.esa.beam.framework.processor.RequestElementFactory;
import org.esa.beam.framework.processor.ui.ProcessorUI;
import org.esa.beam.processor.baer.algorithm.BaerAlgorithm;
import org.esa.beam.processor.baer.auxdata.*;
import org.esa.beam.processor.baer.ui.BaerUi;
import org.esa.beam.processor.baer.utils.AerPixel;
import org.esa.beam.processor.baer.utils.FlagsManager;
import org.esa.beam.processor.baer.utils.MerisPixel;
import org.esa.beam.processor.baer.utils.ProcessorConfiguration;
import org.esa.beam.processor.baer.utils.ProcessorConfigurationParser;
import org.esa.beam.util.ProductUtils;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA. User: tom Date: Jul 22, 2003 Time: 8:30:27 AM To change this template use Options | File
 * Templates.
 */
public class BaerProcessor extends Processor {

    private Logger _logger;
    //private boolean _processL1b;
    private boolean _auxFilesLoaded;
    private String _atm_corr_method;
    private boolean _baer_process;
    private boolean _atm_cor_process;
    private boolean _cloud_process;
    private ProcessorConfiguration _config;

    private Product _inputProduct;
    private Band[] _merisReflecBands;
    private Band _toaVegBand;
    private Band _surfPressBand;
    private Band _aero_opt;
    private TiePointGrid _gridLat;
    private TiePointGrid _gridLon;
    private TiePointGrid _gridSza;
    private TiePointGrid _gridSaa;
    private TiePointGrid _gridVza;
    private TiePointGrid _gridVaa;


    private Product _outputProduct;
    private Band[] _aerReflecBands;
    private Band _aot_412_band;
    private Band _aot_560_band;
    private Band _aot_865_band;
    private Band _aot_440_band;
    private Band _aot_470_band;
    private Band _aot_550_band;
    private Band _aot_665_band;
    private Band _aero_412_band;
    private Band _aero_565_band;
    private Band _aero_865_band;
    private Band _band_lat;
    private Band _band_lon;
    private Band _alpha_band;
    private Band _toa_veg_band;
    private Band _out_flags_band;
    private Band _out_cloud_band;

    private BaerAlgorithm _baerAlgo;


    private NdviLoader _ndviAux;
    private RelAerPhaseLoader _relAerAux;
    private AerPhaseLoader _aerPhaseAux;
    private SoilFractionLoader _soilFractionAux;
    private F_TuningLoader _f_TuningAux;
    private GroundReflectanceLoader _groundReflecAux;
    private AerDiffTransmLoader _aerDiffTransmAux;
    private HemisphReflecLoader _hemisphReflecAux;
    private SmacCoefficientsManager _coeffMgr;

    private String _bitmaskExpression;
    private Term _bitMaskTerm;
    private String _bitmaskL2CloudExpr;
    private Term _bitMaskL2Cloud;

    private BaerUi _ui;

    /**
     * Constructs the object with default parameters.
     */
    public BaerProcessor() {
    }

    /**
     * Initializes the processor. Override to perform processor specific initialization. Called by the framework after
     * the loging is initialized.
     */
    public void initProcessor() throws ProcessorException {
        _ui = null;
        _atm_corr_method = "SMAC";
        _baer_process = true;
        _cloud_process = true;
        _atm_cor_process = false;
        _logger = Logger.getLogger(BaerConstants.LOGGER_NAME);
        _auxFilesLoaded = false;
        _baerAlgo = new BaerAlgorithm();
        _merisReflecBands = new Band[BaerConstants.NUM_IN_REFLEC_BANDS];
        _aerReflecBands = new Band[BaerConstants.NUM_OUT_REFLEC_BANDS];

        installAuxdata();
        loadConfig();

        final File auxdataDir = getAuxdataInstallDir();
        try {
            _coeffMgr = new SmacCoefficientsManager(auxdataDir);
            _baerAlgo.setSmacCoeffManager(_coeffMgr);
        } catch (IOException e) {
            String msg = "Unable to load SMAC auxdata from " + auxdataDir;
            _logger.log(Level.SEVERE, msg, e);
            throw new ProcessorException(msg, e);
        }
    }

    /**
     * Retrieves the title string for the user interface
     *
     * @return
     */
    public String getUITitle() {
        return BaerConstants.PROC_NAME + " " + BaerConstants.PROC_VERSION;
    }

    /**
     * Worker method invoked by framework to process a single request.
     */
    public void process(ProgressMonitor pm) throws ProcessorException {
        _logger.info(ProcessorConstants.LOG_MSG_START_REQUEST);

        try {
            loadAuxiliaryData();

            loadRequestParameter();

            loadInputProduct();

            createOutputProduct();

            createBitmaskTerm();

            /*  if (_processL1b) {
              processL1bAerCorrection();
          } else {*/
            processAerCorrection(pm);
            //}

            closeProducts();
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage());
        }

        _logger.info(ProcessorConstants.LOG_MSG_FINISHED_REQUEST);
    }

    /**
     * Retrieves the name of the processor
     */
    public String getName() {
        return BaerConstants.PROC_NAME;
    }

    /**
     * Retrieves a version string of the processor
     */
    public String getVersion() {
        return BaerConstants.PROC_VERSION;
    }

    /**
     * Retrieves copyright information of the processor
     */
    public String getCopyrightInformation() {
        return BaerConstants.PROC_COPYRIGHT;
    }

    /**
     * Retrieves the request element facory for this processor.
     */
    public RequestElementFactory getRequestElementFactory() {
        return BaerRequestElementFactory.getInstance();
    }

    /**
     * Creates the GUI for the processor.
     */
    public ProcessorUI createUI() throws ProcessorException {
        if (_ui == null) {
            try {
                loadAuxiliaryData();
            } catch (IOException e) {
                throw new ProcessorException(e.getMessage());
            }
            _ui = new BaerUi();
            _ui.setAerPhaseLUTNames(_aerPhaseAux.getLUTNames());
        }

        return _ui;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Writes the processor specific logging file header to the logstream currently set.
     * <p/>
     * <p>This method is called by the processor runner initially after a logging sink has been created.
     */
    protected void logHeader() {
        if (_ui == null) {
            _logger.info(
                    "Logfile generated by '" + BaerConstants.PROC_NAME + "' version '" + BaerConstants.PROC_VERSION + "'");
            _logger.info(BaerConstants.PROC_COPYRIGHT);
            _logger.info("");
        }
    }

    /**
     * Loads all auxiliary data - if they're not already loaded.
     */
    private void loadAuxiliaryData() throws IOException,
                                            ProcessorException {
        if (_auxFilesLoaded) {
            return;
        }

        if (_config == null) {
            throw new ProcessorException("No valid processor configuration available.");
        }

        // load relative aerosol phase coefficients
        // ----------------------------------------
        _relAerAux = new RelAerPhaseLoader();

        _relAerAux.load(_config.getRelativeAerosolPhaseAuxFile());
        _baerAlgo.setRelAerPhaseAccess(_relAerAux);

        // load aersosl phase function coefficients
        // ----------------------------------------
        _aerPhaseAux = new AerPhaseLoader();

        _aerPhaseAux.load(_config.getAerosolPhaseAuxFile());
        _baerAlgo.setAerPhaseAccess(_aerPhaseAux);

        // load ndvi tuning factor
        // -----------------------
        _ndviAux = new NdviLoader();

        _ndviAux.load(_config.getNdviAuxFile());
        _baerAlgo.setNdviAccess(_ndviAux);

        // load ground reflectance data
        // ----------------------------
        _groundReflecAux = new GroundReflectanceLoader();

        _groundReflecAux.load(_config.getGroundReflectanceAuxFile());
        _baerAlgo.setGroundReflectanceAccess(_groundReflecAux);

        // load soil fration factor data
        // -----------------------------
        _soilFractionAux = new SoilFractionLoader();
        _soilFractionAux.load(_config.getSoilFractionAuxFile());
        _baerAlgo.setSoilFractionAccess(_soilFractionAux);

        // load f tuning factor data
        // -------------------------
        _f_TuningAux = new F_TuningLoader();
        _f_TuningAux.load(_config.getF_TuningAuxFile());
        _baerAlgo.setF_TuningAccess(_f_TuningAux);

        // load aerosol diffuse transmission file
        // --------------------------------------
        _aerDiffTransmAux = new AerDiffTransmLoader();
        _aerDiffTransmAux.load(_config.getAerDiffTransmAuxFile());
        _baerAlgo.setAerDiffTransmAccess(_aerDiffTransmAux);

        // load hemispherical reflectance file
        // ---------------------------------------
        _hemisphReflecAux = new HemisphReflecLoader();
        _hemisphReflecAux.load(_config.getHemisphReflecAuxFile());
        _baerAlgo.setHemisphReflecAccess(_hemisphReflecAux);


        _auxFilesLoaded = true;
    }

    /**
     * Checks the request for correct type.
     */
    private void checkRequestType() throws ProcessorException {
        Request request = getRequest();

        if (!request.isRequestType(BaerConstants.REQUEST_TYPE)) {
            throw new ProcessorException("Illegal processing request of type: '" + request.getType() + "'");
        }
    }

    /**
     * Loads all necessary parameters from the request except in- and out products. Checks the request type for
     * correctness.
     *
     * @throws ProcessorException on failures
     */
    private void loadRequestParameter() throws ProcessorException {
        checkRequestType();

        Parameter param;
        Request request = getRequest();

        // initialize logging for the request
        ProcessorUtils.setProcessorLoggingHandler(BaerConstants.DEFAULT_LOG_PREFIX, request,
                                                  getName(), getVersion(), getCopyrightInformation());

        // process Format


        param = request.getParameter(BaerConstants.USE_CLOUD_PARAM_NAME);
        checkParamNotNull(param, BaerConstants.USE_CLOUD_PARAM_NAME);
        _cloud_process = ((Boolean) (param.getValue())).booleanValue();


        param = request.getParameter(BaerConstants.USE_BAER_PARAM_NAME);
        checkParamNotNull(param, BaerConstants.USE_BAER_PARAM_NAME);
        _baer_process = ((Boolean) (param.getValue())).booleanValue();

        if (_baer_process) {
            param = request.getParameter(BaerConstants.USE_ATM_COR_PARAM_NAME);
            checkParamNotNull(param, BaerConstants.USE_ATM_COR_PARAM_NAME);
            _atm_cor_process = ((Boolean) (param.getValue())).booleanValue();
            _baerAlgo.setAtmCorProcessFormat(_atm_cor_process);

            if (_atm_cor_process) {
                param = request.getParameter(BaerConstants.SMAC_PARAM_NAME);
                if (param != null) {
                    if (param.getValueAsText().equalsIgnoreCase("SMAC")) {
                        _atm_corr_method = "SMAC";
                    } else {
                        _atm_corr_method = "UBAC";
                    }
                } else {
                    _atm_corr_method = "SMAC";
                }
            } else {
                _atm_corr_method = "";
            }
            _baerAlgo.setProcessFormat(_atm_corr_method);

        }
        _baerAlgo.setAerosolType(SmacCoefficientsManager.AER_CONT_NAME);

        // bitmask expression
        // ------------------
        param = request.getParameter(BaerConstants.BITMASK_PARAM_NAME);
        if (param != null) {
            _bitmaskExpression = param.getValueAsText();

        } else {
            _bitmaskExpression = "";
            _bitMaskTerm = null;
            _logger.warning("Parameter '" + BaerConstants.BITMASK_PARAM_NAME + "' not set. Processing all pixel!");
        }

        // aerosol phase LUT
        // -----------------
        param = request.getParameter(BaerConstants.AER_PHASE_PARAM_NAME);

        if (param == null) {
            throw new ProcessorException(
                    "Missing processing request parameter: '" + BaerConstants.AER_PHASE_PARAM_NAME + "'");
        }

        String value = param.getValueAsText();
        if (!_aerPhaseAux.selectLut(value)) {
            throw new ProcessorException(
                    "Invalid processing request parameter: '" + BaerConstants.AER_PHASE_PARAM_NAME + "'");
        }
        _logger.fine("... selected aerosol phase LUT: '" + value + "'");
    }


    /**
     * Loads the input product from the processing request and checks for valid product type.
     *
     * @throws ProcessorException when the product is of an unsupported type
     */
    private void loadInputProduct() throws ProcessorException,
                                           IOException {
        Product inProd = loadInputProduct(0);
        String prodType = inProd.getProductType();

        _baerAlgo.setProductType(prodType);

        // check the product type and set the processing switch
        /* if (prodType.equalsIgnoreCase(EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME)
               || prodType.equalsIgnoreCase(EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME)) {
           _processL1b = true;
       } else*/
        if ((prodType.equalsIgnoreCase(EnvisatConstants.MERIS_FR_L2_PRODUCT_TYPE_NAME)
             || prodType.equalsIgnoreCase(EnvisatConstants.MERIS_RR_L2_PRODUCT_TYPE_NAME))) {
            //    _processL1b = false;
        } else {
            throw new ProcessorException("Invalid product of type '" + prodType + "'.");
        }

        _inputProduct = inProd;

        loadBands();
        loadTiePointGrids();
    }

    /**
     * Loads all bands needed from input product.
     */
    private void loadBands() throws ProcessorException {
        for (int n = 0; n < BaerConstants.NUM_IN_REFLEC_BANDS; n++) {
            _merisReflecBands[n] = loadBand(EnvisatConstants.MERIS_L2_BAND_NAMES[n]);
        }

        if (_inputProduct.containsBand("aero_opt_thick")) {
            _aero_opt = loadBand("aero_opt_thick");
        } else {
            _aero_opt = loadBand("aero_opt_thick_865");
        }

        _toaVegBand = loadBand(EnvisatConstants.MERIS_L2_BAND_NAMES[15]);
        _surfPressBand = loadBand(EnvisatConstants.MERIS_L2_BAND_NAMES[21]);
    }

    /**
     * Loads a specific band from the input product.
     *
     * @param bandName bands name
     *
     * @return the band loaded
     *
     * @throws ProcessorException on failure
     */
    private Band loadBand(String bandName) throws ProcessorException {
        Band band;

        band = _inputProduct.getBand(bandName);
        if (band == null) {
            String message = "The requested band \"" + bandName + "\" was not found in product!";
            _logger.severe(message);
            throw new ProcessorException(message);
        } else {
            _logger.fine(ProcessorConstants.LOG_MSG_LOADED_BAND + bandName);
        }

        return band;
    }

    /**
     * Loads all tie point grids needed for the processing.
     */
    private void loadTiePointGrids() throws ProcessorException {
        _gridLat = loadTiePointGrid(EnvisatConstants.LAT_DS_NAME);
        _gridLon = loadTiePointGrid(EnvisatConstants.LON_DS_NAME);
        _gridSza = loadTiePointGrid(EnvisatConstants.MERIS_SUN_ZENITH_DS_NAME);
        _gridSaa = loadTiePointGrid(EnvisatConstants.MERIS_SUN_AZIMUTH_DS_NAME);
        _gridVza = loadTiePointGrid(EnvisatConstants.MERIS_VIEW_ZENITH_DS_NAME);
        _gridVaa = loadTiePointGrid(EnvisatConstants.MERIS_VIEW_AZIMUTH_DS_NAME);
    }

    /**
     * Loads the tie point grid with the given name from the input product.
     *
     * @param name the grid name
     *
     * @return the tie point grid
     *
     * @throws ProcessorException on failure
     */
    private TiePointGrid loadTiePointGrid(String name) throws ProcessorException {
        TiePointGrid grid;

        grid = _inputProduct.getTiePointGrid(name);
        if (grid == null) {
            String message = "The requested tie point grid \"" + name + "\" is not found in product!";
            _logger.severe(message);
            throw new ProcessorException(message);
        } else {
            _logger.fine("... loaded tie point grid: " + name);
        }

        return grid;
    }

    /**
     * Creates the output product as specified in the processing request.
     */
    private void createOutputProduct() throws ProcessorException,
                                              IOException {
        ProductRef prod;
        ProductWriter writer;

        // take only the first output product. There might be more but we will ignore
        // these in this processor.
        prod = getOutputProductSafe();

        // retrieve product specific inpormation
        // -------------------------------------
        String productType = getOutputProductTypeSafe();
        String productName = getOutputProductNameSafe();
        int sceneWidth = _inputProduct.getSceneRasterWidth();
        int sceneHeight = _inputProduct.getSceneRasterHeight();

        // create in memory representation of output product and
        // connect with appropriate writer
        // -----------------------------------------------------
        _outputProduct = new Product(productName, productType, sceneWidth, sceneHeight);
        writer = ProcessorUtils.createProductWriter(prod);
        _outputProduct.setProductWriter(writer);

        // flags stuff
        _outputProduct.addFlagCoding(FlagsManager.getFlagCoding());
        FlagsManager.addBitmaskDefsToProduct(_outputProduct);

        // copy the tie point raster
        // -------------------------
        ProductUtils.copyTiePointGrids(_inputProduct, _outputProduct);

        // copy geocoding and flags
        // ------------------------
        ProductUtils.copyGeoCoding(_inputProduct, _outputProduct);
        ProductUtils.copyFlagBands(_inputProduct, _outputProduct);

        // write the processing request as metadata
        copyRequestMetaData(_outputProduct);

        // add the target bands
        // --------------------
        addBandsToOutputProduct();

        // add the metadata
        addMetadataToOutput();

        // initialize the disk represenation
        // ---------------------------------
        writer.writeProductNodes(_outputProduct, new File(prod.getFilePath()));
    }

    /**
     * Retrieves the first output product reference from the request.
     *
     * @throws org.esa.beam.framework.processor.ProcessorException
     *          when no output product is set in the request
     */
    private ProductRef getOutputProductSafe() throws ProcessorException {
        ProductRef prod;
        Request request = getRequest();

        if (request.getNumOutputProducts() > 0) {
            prod = getRequest().getOutputProductAt(0);
            if (prod == null) {
                throw new ProcessorException(ProcessorConstants.LOG_MSG_NO_OUTPUT_IN_REQUEST);
            }
        } else {
            throw new ProcessorException(ProcessorConstants.LOG_MSG_NO_OUTPUT_IN_REQUEST);
        }
        return prod;
    }

    /**
     * Retrieves the output product type from the input product type by appending "_BAER" to the type string.
     *
     * @throws org.esa.beam.framework.processor.ProcessorException
     *          when an error occurs
     */
    private String getOutputProductTypeSafe() throws ProcessorException {
        String productType = _inputProduct.getProductType();
        if (productType == null) {
            throw new ProcessorException(ProcessorConstants.LOG_MSG_NO_INPUT_TYPE);
        }

        return productType + BaerConstants.PRODUCT_TYPE_APPENDIX;
    }


    /**
     * Retrieves the output product name from the request.
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
        String productName = FileUtils.getFilenameWithoutExtension(prodFile);

        return productName;
    }

    /**
     * Evaluates the bitmask expression from the request to an term that can be evaluated during runtime.
     */
    private void createBitmaskTerm() throws ProcessorException {
        if (_bitmaskExpression.equalsIgnoreCase("")) {
            _bitMaskTerm = null;
        } else {
            _bitMaskTerm = ProcessorUtils.createTerm(_bitmaskExpression, _inputProduct);
        }
        _bitmaskL2CloudExpr = "l2_flags.CLOUD";
        _bitMaskL2Cloud = ProcessorUtils.createTerm(_bitmaskL2CloudExpr, _inputProduct);
    }

    /**
     * Adds all required bands to the output product.
     */
    private void addBandsToOutputProduct() {
        int width = _inputProduct.getSceneRasterWidth();
        int height = _inputProduct.getSceneRasterHeight();

        for (int n = 0; n < BaerConstants.NUM_OUT_REFLEC_BANDS; n++) {
            _aerReflecBands[n] = new Band(_merisReflecBands[n].getName(), ProductData.TYPE_FLOAT32, width, height);
            _aerReflecBands[n].setUnit(_merisReflecBands[n].getUnit());
            _aerReflecBands[n].setSpectralBandIndex(_merisReflecBands[n].getSpectralBandIndex());
            _aerReflecBands[n].setSpectralWavelength(_merisReflecBands[n].getSpectralWavelength());
            _aerReflecBands[n].setSpectralBandwidth(_merisReflecBands[n].getSpectralBandwidth());
            _aerReflecBands[n].setDescription(BaerConstants.OUT_REFLEC_BAND_DESCRIPTION);
            _aerReflecBands[n].setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
            _outputProduct.addBand(_aerReflecBands[n]);

        }

        _aot_412_band = new Band(BaerConstants.AOT_412_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aot_412_band.setDescription(BaerConstants.AOT_412_BAND_DESCRIPTION);
        _aot_412_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aot_412_band);

        _aot_560_band = new Band(BaerConstants.AOT_560_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aot_560_band.setDescription(BaerConstants.AOT_560_BAND_DESCRIPTION);
        _aot_560_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aot_560_band);

        _aot_865_band = new Band(BaerConstants.AOT_865_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aot_865_band.setDescription(BaerConstants.AOT_865_BAND_DESCRIPTION);
        _aot_865_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aot_865_band);

        _aot_440_band = new Band(BaerConstants.AOT_440_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aot_440_band.setDescription(BaerConstants.AOT_440_BAND_DESCRIPTION);
        _aot_440_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aot_440_band);

        _aot_470_band = new Band(BaerConstants.AOT_470_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aot_470_band.setDescription(BaerConstants.AOT_470_BAND_DESCRIPTION);
        _aot_470_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aot_470_band);

        _aot_550_band = new Band(BaerConstants.AOT_550_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aot_550_band.setDescription(BaerConstants.AOT_550_BAND_DESCRIPTION);
        _aot_550_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aot_550_band);

        _aot_665_band = new Band(BaerConstants.AOT_665_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aot_665_band.setDescription(BaerConstants.AOT_665_BAND_DESCRIPTION);
        _aot_665_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aot_665_band);


        _aero_412_band = new Band(BaerConstants.AERO_412_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aero_412_band.setDescription(BaerConstants.AERO_412_BAND_DESCRIPTION);
        _aero_412_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aero_412_band);

        _aero_565_band = new Band(BaerConstants.AERO_565_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aero_565_band.setDescription(BaerConstants.AERO_565_BAND_DESCRIPTION);
        _aero_565_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aero_565_band);


        _aero_865_band = new Band(BaerConstants.AERO_865_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _aero_865_band.setDescription(BaerConstants.AERO_865_BAND_DESCRIPTION);
        _aero_865_band.setValidPixelExpression(BaerConstants.VALID_PIXEL_EXPRESSION);
        _outputProduct.addBand(_aero_865_band);

        _alpha_band = new Band(BaerConstants.ALPHA_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _alpha_band.setDescription(BaerConstants.ALPHA_BAND_DESCRIPTION);
        _outputProduct.addBand(_alpha_band);

        _band_lat = new Band(BaerConstants.BAND_LAT_NAME, ProductData.TYPE_FLOAT32, width, height);
        _band_lat.setDescription(BaerConstants.BAND_LAT_DESCRIPTION);
        _outputProduct.addBand(_band_lat);

        _band_lon = new Band(BaerConstants.BAND_LON_NAME, ProductData.TYPE_FLOAT32, width, height);
        _band_lon.setDescription(BaerConstants.BAND_LON_DESCRIPTION);
        _outputProduct.addBand(_band_lon);


        _toa_veg_band = new Band(BaerConstants.TOA_VEG_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _toa_veg_band.setDescription(BaerConstants.TOA_VEG_BAND_DESCRIPTION);
        _outputProduct.addBand(_toa_veg_band);

        _out_cloud_band = new Band(BaerConstants.CLOUD_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _out_cloud_band.setDescription(BaerConstants.CLOUD_BAND_DESCRIPTION);
        _outputProduct.addBand(_out_cloud_band);

        _out_flags_band = new Band(BaerConstants.OUT_FLAGS_BAND_NAME, ProductData.TYPE_UINT16, width, height);
        _out_flags_band.setDescription(BaerConstants.OUT_FLAGS_BAND_DESCRIPTION);
        _out_flags_band.setFlagCoding(FlagsManager.getFlagCoding());
        _outputProduct.addBand(_out_flags_band);
    }


    /**
     * Adds the metadata to the output product
     */
    private void addMetadataToOutput() {
        // first copy MPH and SPH from input product
        // -----------------------------------------
        copySrcMetadataToOutput();

        // create metadata root
        MetadataElement destRoot = _outputProduct.getMetadataRoot();
        MetadataElement mph = new MetadataElement(BaerConstants.MPH_METADATA_NAME);

        mph.addAttribute(new MetadataAttribute(BaerConstants.PRODUCT_METADATA_NAME,
                                               ProductData.createInstance(_outputProduct.getName()),
                                               true));
        mph.addAttribute(new MetadataAttribute(BaerConstants.SRC_PRODUCT_METADATA_NAME,
                                               ProductData.createInstance(_inputProduct.getName()),
                                               true));
        mph.addAttribute(new MetadataAttribute(BaerConstants.PROCESSOR_METADATA_NAME,
                                               ProductData.createInstance(getName()),
                                               true));
        mph.addAttribute(new MetadataAttribute(BaerConstants.PROCESSOR_VERSION_METADATA_NAME,
                                               ProductData.createInstance(getVersion()),
                                               true));

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();

        mph.addAttribute(new MetadataAttribute(BaerConstants.PROCESSING_TIME_METADATA_NAME,
                                               ProductData.createInstance(date.toString()),
                                               true));
        addAuxFilesToMetadata(mph);

        destRoot.addElement(mph);
    }

    /**
     * Copies MPH and SPH from the input product to the output product
     */
    private void copySrcMetadataToOutput() {
        MetadataElement srcRoot = _inputProduct.getMetadataRoot();
        MetadataElement destRoot = _outputProduct.getMetadataRoot();
        MetadataElement srcFolder = new MetadataElement(BaerConstants.SRC_METADATA_NAME);

        srcFolder.addElement(srcRoot.getElement("MPH"));
        srcFolder.addElement(srcRoot.getElement("SPH"));
        destRoot.addElement(srcFolder);
    }

    /**
     * Adds the metadata felds of the auxiliary files to the output product
     *
     * @param mph
     */
    private void addAuxFilesToMetadata(MetadataElement mph) {
        addGroundReflectanceMetadata(mph);
        addAerosolPhaseMetadata(mph);

        addStubbedMetadataElement(mph, BaerConstants.NDVI_AUX_METADATA_NAME,
                                  _config.getNdviAuxFile(),
                                  _ndviAux,
                                  BaerConstants.NDVI_AUX_METADATA_NAME);

        addStubbedMetadataElement(mph, BaerConstants.SOIL_FRACTION_AUX_METADATA_NAME,
                                  _config.getSoilFractionAuxFile(),
                                  _soilFractionAux,
                                  BaerConstants.SOIL_FRACTION_AUX_METADATA_NAME);

        addStubbedMetadataElement(mph, BaerConstants.F_TUNING_AUX_METADATA_NAME,
                                  _config.getF_TuningAuxFile(),
                                  _f_TuningAux,
                                  BaerConstants.F_TUNING_AUX_METADATA_NAME);

        addStubbedMetadataElement(mph, BaerConstants.REL_AER_PHASE_AUX_METADATA_NAME,
                                  _config.getRelativeAerosolPhaseAuxFile(),
                                  _relAerAux,
                                  BaerConstants.REL_AER_PHASE_A_METADATA_STUB);

        addStubbedMetadataElement(mph, BaerConstants.AER_DIFF_TRANSM_AUX_METADATA_NAME,
                                  _config.getAerDiffTransmAuxFile(),
                                  _aerDiffTransmAux,
                                  BaerConstants.AER_DIFF_TRANSM_METADATA_STUB);


        addStubbedMetadataElement(mph, BaerConstants.HEMISPH_REFLEC_AUX_METADATA_NAME,
                                  _config.getHemisphReflecAuxFile(),
                                  _hemisphReflecAux,
                                  BaerConstants.HEMISPH_REFLEC_METADATA_STUB);
    }

    /**
     * Adds aux file version and description strings to the metadata.
     *
     * @param loader
     * @param auxElem
     */
    private void addAuxFileDescAndVersionToMetadata(AuxFileLoader loader, MetadataElement auxElem) {
        String temp;

        temp = loader.getVersionString();
        if (temp == null) {
            temp = BaerConstants.AUX_VAL_UNKNOWN;
        }
        auxElem.addAttribute(new MetadataAttribute(BaerConstants.AUX_FILE_VERSION_METADATA_NAME,
                                                   ProductData.createInstance(temp),
                                                   true));

        temp = loader.getDescription();
        if (temp == null) {
            temp = BaerConstants.AUX_VAL_NONE;
        }
        auxElem.addAttribute(new MetadataAttribute(BaerConstants.AUX_FILE_DESCRIPTION_METADATA_NAME,
                                                   ProductData.createInstance(temp),
                                                   true));
    }

    /**
     * Adds the description and version of the aerosol phase aux data file to the metadata element passed in.
     *
     * @param mph the metadata element where the information is added to
     */
    private void addAerosolPhaseMetadata(MetadataElement mph) {
        MetadataElement aerLutAuxMeta = new MetadataElement(BaerConstants.AER_PHASE_LUT_AUX_METADATA_NAME);

        aerLutAuxMeta.addAttribute(new MetadataAttribute(BaerConstants.AUX_FILE_NAME_METADATA_NAME,
                                                         ProductData.createInstance(_config.getAerosolPhaseAuxFile()),
                                                         true));

        addAuxFileDescAndVersionToMetadata(_aerPhaseAux, aerLutAuxMeta);

        mph.addElement(aerLutAuxMeta);
    }


    /**
     * Adds the description and version of the ground reflectances aux data file to the metadata element passed in.
     *
     * @param mph the metadata element where the information is added to
     */
    private void addGroundReflectanceMetadata(MetadataElement mph) {
        MetadataElement gndReflecAuxMeta = new MetadataElement(BaerConstants.GND_REFLEC_AUX_METADATA_NAME);

        gndReflecAuxMeta.addAttribute(new MetadataAttribute(BaerConstants.AUX_FILE_NAME_METADATA_NAME,
                                                            ProductData.createInstance(
                                                                    _config.getGroundReflectanceAuxFile()),
                                                            true));

        addAuxFileDescAndVersionToMetadata(_groundReflecAux, gndReflecAuxMeta);

        mph.addElement(gndReflecAuxMeta);
    }

    /**
     * Generic function. Adds stubbed auxiliary data elements to the MetaDataElement passed in.
     *
     * @param mph
     */
    private void addStubbedMetadataElement(MetadataElement mph, String elemName, String auxFilePath,
                                           AuxFilePropsLoader loader, String stub) {
        MetadataElement auxMeta = new MetadataElement(elemName);

        auxMeta.addAttribute(new MetadataAttribute(BaerConstants.AUX_FILE_NAME_METADATA_NAME,
                                                   ProductData.createInstance(auxFilePath),
                                                   true));

        addAuxFileDescAndVersionToMetadata(loader, auxMeta);

        String temp;
        double[] values = loader.getCoeffs();
        if (values.length == 1) {
            auxMeta.addAttribute(new MetadataAttribute(stub,
                                                       ProductData.createInstance(values),
                                                       true));
        } else {
            for (int n = 0; n < values.length; n++) {
                temp = stub + n;
                auxMeta.addAttribute(new MetadataAttribute(temp,
                                                           ProductData.createInstance(new double[]{values[n]}),
                                                           true));
            }
        }

        mph.addElement(auxMeta);
    }

    /**
     * Performs the actual aerosol correction for MERIS L2 input products.
     */
    private void processAerCorrection(ProgressMonitor pm) throws ProcessorException, IOException {
        boolean processPixel = true;
        final int width = _inputProduct.getSceneRasterWidth();
        final int height = _inputProduct.getSceneRasterHeight();


        final MerisPixel inPixel = new MerisPixel();
        AerPixel result = new AerPixel();
        double ndvix;
        int desert_pixel;

        // input
        final float[][] input_reflec = new float[BaerConstants.NUM_IN_REFLEC_BANDS][width];

        float[] band_boa_veg = new float[width];
        float[] band_surfPress = new float[width];
        float[] band_Lat = new float[width];
        float[] band_Lon = new float[width];
        float[] band_Sza = new float[width];
        float[] band_Saa = new float[width];
        float[] band_Vza = new float[width];
        float[] band_Vaa = new float[width];
        float[] band_aero_opt = new float[width];
        //       int cloud_pixel;
        int error_pixel;
        //   double rhoclib = 0.3;
        //      double rhoclibx;

        boolean process_cloud[] = new boolean[width];

        boolean process[] = new boolean[width];
        for (int n = 0; n < width; n++) {
            process[n] = true;
            process_cloud[n] = false;
        }

        //Baer Algo init
        _baerAlgo.initAlgo();

        // output
        final float[][] output_reflec = new float[BaerConstants.NUM_OUT_REFLEC_BANDS][width];
        float[] band_aot_412 = new float[width];
        float[] band_aot_440 = new float[width];
        float[] band_aot_550 = new float[width];
        float[] band_alpha = new float[width];
        int[] out_flags = new int[width];
        float[] out_cloud = new float[width];

        // progress bar init
        // -----------------
        int work = BaerConstants.NUM_IN_REFLEC_BANDS + 9;
        if (_baer_process) {
            if (_atm_cor_process) {
                work += BaerConstants.NUM_OUT_REFLEC_BANDS;
            }
            work += 6;
        }
        if (_cloud_process) {
            work += 1;
        }
        work = work * height + 1;

        pm.beginTask(BaerConstants.LOG_MSG_GENERATE_PIXEL, work); /*I18N*/

        copyFlagBandData(_inputProduct, _outputProduct, SubProgressMonitor.create(pm, 1));

        for (int line = 0; line < height; line++) {

            // read input data
            // ---------------
            for (int n = 0; n < BaerConstants.NUM_IN_REFLEC_BANDS; n++) {
                _merisReflecBands[n].readPixels(0, line, width, 1, input_reflec[n], SubProgressMonitor.create(pm, 1));

            }

            _aero_opt.readPixels(0, line, width, 1, band_aero_opt, SubProgressMonitor.create(pm, 1));
            _toaVegBand.readPixels(0, line, width, 1, band_boa_veg, SubProgressMonitor.create(pm, 1));
            _gridLat.readPixels(0, line, width, 1, band_Lat, SubProgressMonitor.create(pm, 1));
            _gridLon.readPixels(0, line, width, 1, band_Lon, SubProgressMonitor.create(pm, 1));
            _gridSza.readPixels(0, line, width, 1, band_Sza, SubProgressMonitor.create(pm, 1));
            _gridSaa.readPixels(0, line, width, 1, band_Saa, SubProgressMonitor.create(pm, 1));
            _gridVza.readPixels(0, line, width, 1, band_Vza, SubProgressMonitor.create(pm, 1));
            _gridVaa.readPixels(0, line, width, 1, band_Vaa, SubProgressMonitor.create(pm, 1));
            _surfPressBand.readPixels(0, line, width, 1, band_surfPress, SubProgressMonitor.create(pm, 1));

            // evaluate bitmask
            // ----------------
            if (_bitMaskTerm != null) {
                _inputProduct.readBitmask(0, line, width, 1, _bitMaskTerm, process);
                _inputProduct.readBitmask(0, line, width, 1, _bitMaskL2Cloud, process_cloud);
            }

            // loop over line, assemble pixel and dispatch to processing classes
            // -----------------------------------------------------------------
            for (int x = 0; x < width; x++) {
                // reset to be sure - risk of pending flags ...
                result.reset();

                // check bitmask
                if (!process[x]) {
                    result.setInvalidInputFlag();
                    processPixel = false;
                }

                if (_atm_corr_method.equals("SMAC")) {
                    result.setAtmosphericCorrectionFlag();
                }
                // check for negative reflectance
                for (int n = 0; n < BaerConstants.NUM_IN_REFLEC_BANDS; n++) {
                    if (input_reflec[n][x] < 0.f) {
                        result.setInvalidInputFlag();
                        processPixel = false;
                        break;
                    }
                }

                /* Discrimination of wrong L2 data
                      *
                      */
                error_pixel = 0;
                if ((input_reflec[0][x] <= 0.01f)) {
                    error_pixel++;
                }
                if ((input_reflec[1][x] <= 0.01f)) {
                    error_pixel++;
                }
                if ((input_reflec[2][x] <= 0.009f)) {
                    error_pixel++;
                }
                if ((input_reflec[3][x] <= 0.009f)) {
                    error_pixel++;
                }
                if (error_pixel >= 2) {
                    result.setInvalidInputFlag();
                    processPixel = false;
                }

                // Discrimination of desert ground
                ndvix = (input_reflec[12][x] - input_reflec[6][x]) /
                        (input_reflec[12][x] + input_reflec[6][x]);

                desert_pixel = 0;

                if (ndvix < 0.1) {
                    if (input_reflec[12][x] > 0.28 && input_reflec[12][x] <= 0.53) {
                        desert_pixel = 1;
                    }
                    if (input_reflec[6][x] > 0.22 && input_reflec[6][x] <= 0.32) {
                        desert_pixel += 1;
                    }
                    if (input_reflec[2][x] > 0.13 && input_reflec[2][x] < 0.3) {
                        desert_pixel += 1;
                    }
                }

                if (_cloud_process) {
                    processPixel = cloud_process(input_reflec, result, x, ndvix, process_cloud[x], processPixel);
                }


                if (processPixel) {

                    // only process after check was successful
                    // init input and result with the meris data
                    for (int n = 0; n < BaerConstants.NUM_IN_REFLEC_BANDS; n++) {
                        inPixel.setBand(input_reflec[n][x], n);
                        result.setBand(input_reflec[n][x], n);
                    }

                    inPixel.setBand_Lat(band_Lat[x]);
                    inPixel.setBand_Lon(band_Lon[x]);
                    inPixel.setBand_Sza(band_Sza[x]);
                    inPixel.setBand_Saa(band_Saa[x]);
                    inPixel.setBand_Vza(band_Vza[x]);
                    inPixel.setBand_Vaa(band_Vaa[x]);
                    inPixel.setBand_Aero_opt(band_aero_opt[x]);
                    inPixel.setPressure(band_surfPress[x]);


                    if (desert_pixel >= 2) {
                        _baerAlgo.setSoilSpectraNumber(3);
                    } else {
                        _baerAlgo.setSoilSpectraNumber(1);
                    }

                    if (_baer_process) {
                        result = _baerAlgo.processPixel(inPixel, result);
                    }

                }

                if (_baer_process) {
                    if (_atm_cor_process) {
                        for (int n = 0; n < BaerConstants.NUM_OUT_REFLEC_BANDS; n++) {
                            output_reflec[n][x] = result.getBand(n);
                        }
                    }

                    band_aot_412[x] = result.getAot_412();
                    band_aot_550[x] = result.getAot_550();
                    band_aot_440[x] = result.getAot_440();
                    band_alpha[x] = result.getAlpha();
                    out_flags[x] = result.getFlagMask();
                }
                out_cloud[x] = result.getBand_Cloud();


                processPixel = true;
            } // end of pixel loop

            if (_baer_process) {
                if (_atm_cor_process) {
                    for (int n = 0; n < BaerConstants.NUM_OUT_REFLEC_BANDS; n++) {
                        _aerReflecBands[n].writePixels(0, line, width, 1, output_reflec[n],
                                                       SubProgressMonitor.create(pm, 1));
                    }
                }
                _aot_412_band.writePixels(0, line, width, 1, band_aot_412, SubProgressMonitor.create(pm, 1));
                _aot_440_band.writePixels(0, line, width, 1, band_aot_440, SubProgressMonitor.create(pm, 1));
                _aot_550_band.writePixels(0, line, width, 1, band_aot_550, SubProgressMonitor.create(pm, 1));
                _toa_veg_band.writePixels(0, line, width, 1, band_boa_veg, SubProgressMonitor.create(pm, 1));
                _alpha_band.writePixels(0, line, width, 1, band_alpha, SubProgressMonitor.create(pm, 1));
                _out_flags_band.writePixels(0, line, width, 1, out_flags, SubProgressMonitor.create(pm, 1));
            }
            if (_cloud_process) {
                _out_cloud_band.writePixels(0, line, width, 1, out_cloud, SubProgressMonitor.create(pm, 1));
            }

            // update progressbar
            // ------------------
            pm.worked(1);
            if (pm.isCanceled()) {
                _logger.warning(ProcessorConstants.LOG_MSG_PROC_CANCELED);
                setCurrentStatus(ProcessorConstants.STATUS_ABORTED);
                return;
            }

            // check aborted
            // -------------
            if (isAborted()) {
                _logger.warning(ProcessorConstants.LOG_MSG_PROC_ABORTED);
                pm.done();
                return;
            }
        } // end of line loop
        pm.done();

    }

    private boolean cloud_process(float[][] input_reflec, AerPixel result, int x, double ndvix, boolean process_cloud,
                                  boolean processPixel) {

        int cloud_pixel;
        double rhoclibx;
        result.setBand_Cloud(0);
        if (process_cloud) {
            result.setBand_Cloud(2);
            processPixel = false;

        } else {    // Attempt to recognize cloud Shaddows
            /* if ((input_reflec[0][x] <= 0.005f)) {
               result.setCloudShadowFlag();
               result.setBand_Cloud(2);
               processPixel = false;
           }
           if ((input_reflec[1][x] <= 0.006f)) {
               result.setCloudShadowFlag();
               result.setBand_Cloud(2);
               processPixel = false;
           } */
            // Cloud pixel
            cloud_pixel = 0;
            // rhoclibx = rhoclib * 0.785;
            rhoclibx = 0.2;
            if (input_reflec[1][x] >= rhoclibx) {
                cloud_pixel = 1;
            }
            if (input_reflec[2][x] >= rhoclibx) {
                cloud_pixel++;
            }
            if (input_reflec[3][x] >= rhoclibx) {
                cloud_pixel++;
            }
            if (ndvix < 0.1) {
                if (input_reflec[12][x] > 0.53) {
                    cloud_pixel++;
                }
                if (input_reflec[6][x] > 0.32) {
                    cloud_pixel++;
                }
                if (input_reflec[2][x] > 0.30) {
                    cloud_pixel++;
                }
            }
            if (cloud_pixel >= 3) {
                result.setCloudInputFlag();
                result.setBand_Cloud(1);
                processPixel = false;
                // out_flags[x] = result.getFlagMask();
            }
        }
        return processPixel;
    }

    /**
     * Performs the actual aerosol correction for MERIS L1b input products.
     */
    /*    private void processL1bAerCorrection() throws IOException {
         boolean processPixel = true;
         final int width = _inputProduct.getSceneRasterWidth();
         final int height = _inputProduct.getSceneRasterHeight();

         final MerisPixel inPixel = new MerisPixel();
         MerisPixel outPixel = new MerisPixel();
         AerPixel result = new AerPixel();


         // input
         final float[][] input_reflec = new float[BaerConstants.NUM_IN_REFLEC_BANDS][width];

         float[] band_boa_veg = new float[width];
         float[] band_surfPress = new float[width];
         float[] band_Lat = new float[width];
         float[] band_Lon = new float[width];
         float[] band_Sza = new float[width];
         float[] band_Saa = new float[width];
         float[] band_Vza = new float[width];
         float[] band_Vaa = new float[width];
         boolean process[] = new boolean[width];
         for (int n = 0; n < width; n++) {
             process[n] = true;
         }

         // output
         final float[][] output_reflec = new float[BaerConstants.NUM_OUT_REFLEC_BANDS][width];
         float[] band_aot_412 = new float[width];
         float[] band_aot_560 = new float[width];
         float[] band_aot_550 = new float[width];
         float[] band_alpha = new float[width];
         int[] out_flags = new int[width];

         // progress bar init
         // -----------------
         fireProcessStarted(BaerConstants.LOG_MSG_GENERATE_PIXEL, 0, height);

         for (int line = 0; line < height; line++) {
             // read input data
             // ---------------
             for (int n = 0; n < BaerConstants.NUM_IN_REFLEC_BANDS; n++) {
                 _merisReflecBands[n].readPixels(0, line, width, 1, input_reflec[n]);
             }

             _toaVegBand.readPixels(0, line, width, 1, band_boa_veg);
             _gridLat.readPixels(0, line, width, 1, band_Lat);
             _gridLon.readPixels(0, line, width, 1, band_Lon);
             _gridSza.readPixels(0, line, width, 1, band_Sza);
             _gridSaa.readPixels(0, line, width, 1, band_Saa);
             _gridVza.readPixels(0, line, width, 1, band_Vza);
             _gridVaa.readPixels(0, line, width, 1, band_Vaa);
             _surfPressBand.readPixels(0, line, width, 1, band_surfPress);

             // evaluate bitmask
             // ----------------
             if (_bitMaskTerm != null) {
                 _inputProduct.readBitmask(0, line, width, 1, _bitMaskTerm, process);
             }

             // loop over line, assemble pixel and dispatch to processing classes
             // -----------------------------------------------------------------
             for (int x = 0; x < width; x++) {
                 // reset to be sure - risk of pending flags ...
                 result.reset();
                 // check bitmask
                 if (!process[x]) {
                     result.setInvalidInputFlag();
                     processPixel = false;
                 }

                 // check for negative reflectance
                 for (int n = 0; n < BaerConstants.NUM_IN_REFLEC_BANDS; n++) {
                     if (input_reflec[n][x] < 0.f) {
                        result.setInvalidInputFlag();
                         processPixel = false;
                         break;
                     }
                 }

                 if (processPixel) {
                     // only process after check was successful
                     // init input and result with the meris data
                     for (int n = 0; n < BaerConstants.NUM_IN_REFLEC_BANDS; n++) {
                         inPixel.setBand(input_reflec[n][x], n);
                         result.setBand(input_reflec[n][x], n);
                     }
                     inPixel.setBand_Lat(band_Lat[x]);
                     inPixel.setBand_Lon(band_Lon[x]);
                     inPixel.setBand_Sza(band_Sza[x]);
                     inPixel.setBand_Saa(band_Saa[x]);
                     inPixel.setBand_Vza(band_Vza[x]);
                     inPixel.setBand_Vaa(band_Vaa[x]);
                     inPixel.setPressure(band_surfPress[x]);

 //		    outPixel = _rayleighAlgo.processPixel(inPixel, outPixel);
                 //    result = _baerAlgo.processPixel(outPixel, result);
                 }

                 for (int n = 0; n < BaerConstants.NUM_OUT_REFLEC_BANDS; n++) {
                     output_reflec[n][x] = result.getBand(n);
                 }
                 band_aot_412[x] = result.getAot_412();
                 band_aot_560[x] = result.getAot_560();
                 band_alpha[x] = result.getAlpha();
                 out_flags[x] = result.getFlagMask();

                 processPixel = true;
             } // end of pixel loop

             for (int n = 0; n < BaerConstants.NUM_OUT_REFLEC_BANDS; n++) {
                 _aerReflecBands[n].writePixels(0, line, width, 1, output_reflec[n]);
             }
             _aot_412_band.writePixels(0, line, width, 1, band_aot_412);
             _aot_560_band.writePixels(0, line, width, 1, band_aot_560);
       //      _aot_550_band.writePixels(0, line, width, 1, band_aot_550);
            _toa_veg_band.writePixels(0, line, width, 1, band_boa_veg);
             _alpha_band.writePixels(0, line, width, 1, band_alpha);
             _out_flags_band.writePixels(0, line, width, 1, out_flags);

             // update progressbar
             // ------------------
             if (!fireProcessInProgress(line)) {
                 _logger.warning(ProcessorConstants.LOG_MSG_PROC_CANCELED);
                 setCurrentStatus(ProcessorConstants.STATUS_ABORTED);
                 return;
             }

             // check aborted
             // -------------
             if (isAborted()) {
                 _logger.warning(ProcessorConstants.LOG_MSG_PROC_ABORTED);
                 fireProcessEnded(false);
                 return;
             }
        } // end of line loop

         fireProcessEnded(true);
     }
       */
    /**
     * Closes all open products
     */
    protected void closeProducts() throws IOException {
        if (_inputProduct != null) {
            _inputProduct.closeProductReader();
        }
        if (_outputProduct != null) {
            _outputProduct.closeProductWriter();
        }
    }

    /**
     * Loads the processor configuration.
     */
    private void loadConfig() throws ProcessorException {
        URL configPath = getConfigPath();
        File auxdataPath = null;

        auxdataPath = getAuxdataInstallDir();
        _logger.info("... Loading processor configuration '" + configPath.toString() + "'");

        ProcessorConfigurationParser parser = new ProcessorConfigurationParser();
        parser.parseConfigurationFile(configPath, auxdataPath);
        _config = parser.getConfiguration();

        _logger.info("...... success");
    }

    /**
     * Retrieves the full path to the processor configuration file.
     *
     * @return the config path
     * @throws org.esa.beam.framework.processor.ProcessorException
     */
    private URL getConfigPath() throws ProcessorException {
        File configFile = new File(getAuxdataInstallDir(), BaerConstants.CONFIG_FILE);
        URL configPath = null;
        try {
            configPath = configFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new ProcessorException("Failed to create configuration URL for " + configFile.getPath(), e);
        }
        return configPath;
    }

    @Override
    public void installAuxdata() throws ProcessorException {
        setAuxdataInstallDir(BaerConstants.AUXDATA_DIR_PROPERTY, getDefaultAuxdataInstallDir());
        try {
            super.installAuxdata(ResourceInstaller.getSourceUrl(getClass()), "auxdata/" + BaerConstants.AUXDATA_DIR,
                                 getAuxdataInstallDir());
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage(), e);
             }

	 }
}
