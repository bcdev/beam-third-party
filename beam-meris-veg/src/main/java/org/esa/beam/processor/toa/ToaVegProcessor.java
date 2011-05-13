/*
 * $Id: ToaVegProcessor.java,v 1.17 2006/03/27 15:24:52 meris Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.esa.beam.processor.toa;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import com.bc.jexp.Term;
import com.bc.jnn.JnnException;
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
import org.esa.beam.processor.common.VegRequestElementFactory;
import org.esa.beam.processor.common.auxdata.VegUncertaintyModelLoader;
import org.esa.beam.processor.common.utils.VegFlagsManager;
import org.esa.beam.processor.common.utils.VegGenericPixel;
import org.esa.beam.processor.common.utils.VegProcessorConfiguration;
import org.esa.beam.processor.toa.algorithm.ToaVegAlgorithm;
import org.esa.beam.processor.toa.auxdata.ToaVegInputStatisticsLoader;
import org.esa.beam.processor.toa.auxdata.ToaVegOutputStatisticsLoader;
import org.esa.beam.processor.toa.auxdata.ToaVegUncertaintyModelLoader;
import org.esa.beam.processor.toa.ui.ToaVegUi;
import org.esa.beam.processor.toa.utils.ToaVegMerisPixel;
import org.esa.beam.processor.toa.utils.ToaVegProcessorConfigurationParser;
import org.esa.beam.util.ProductUtils;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.io.FileUtils;
import org.esa.beam.dataio.envisat.EnvisatConstants;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class ToaVegProcessor extends Processor {

    private Logger _logger;

    private ToaVegAlgorithm _algo;
    private VegProcessorConfiguration _config;
    private boolean _auxFilesLoaded;

    private Product _inputProduct;
    private Product _outputProduct;

    private ToaVegInputStatisticsLoader _inStatAux;
    private ToaVegOutputStatisticsLoader _outStatAux;
    private VegUncertaintyModelLoader _uncertaintyAux;

    private Band[] _reflec_bands;

    private TiePointGrid _gridLat;
    private TiePointGrid _gridLon;
    private TiePointGrid _gridSza;
    private TiePointGrid _gridSaa;
    private TiePointGrid _gridVza;
    private TiePointGrid _gridVaa;
    private TiePointGrid _gridPress;

    private Band _lai_band;
    private Band _fCover_band;
    private Band _cabxLai_band;
    private Band _fapar_band;
    private Band _veg_flags_band;
    private Band _sigma_lai_band;
    private Band _sigma_fcover_band;
    private Band _sigma_laixcab_band;
    private Band _sigma_fapar_band;

    private String _bitmaskExpression;
    private Term _bitMaskTerm;

    private ToaVegUi _ui;

    /**
     * Constructs the object with default parameters.
     */
    public ToaVegProcessor() {
    }

    /**
     * Retrieves the request element factory of the processor.
     *
     * @return
     */
    public RequestElementFactory getRequestElementFactory() {
        return VegRequestElementFactory.getToaVegInstance();
    }

    /**
     * Initializes the processor. Override to perform processor specific initialization. Called by the framework after
     * the logging is initialized.
     */
    public void initProcessor() throws ProcessorException {
        _logger = Logger.getLogger(ToaVegConstants.LOGGER_NAME);

        _algo = new ToaVegAlgorithm();
        _auxFilesLoaded = false;
        _reflec_bands = new Band[ToaVegConstants.NUM_BANDS];

        installAuxdata();
        loadConfig();
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

            processVegAlgorithm(pm);

            closeProducts();
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage(), e);
        } catch (JnnException e) {
            throw new ProcessorException(e.getMessage(), e);
        }

        _logger.info(ProcessorConstants.LOG_MSG_FINISHED_REQUEST);
    }

    /**
     * Retrieves the name of the processor
     */
    public String getName() {
        return ToaVegConstants.PROC_NAME;
    }

    /**
     * Retrieves a version string of the processor
     */
    public String getVersion() {
        return ToaVegConstants.PROC_VERSION;
    }

    /**
     * Retrieves copyright information of the processor
     */
    public String getCopyrightInformation() {
        return ToaVegConstants.PROC_COPYRIGHT;
    }

    /**
     * Creates the GUI for the processor.
     */
    public ProcessorUI createUI() throws ProcessorException {
        if (_ui == null) {
            _ui = new ToaVegUi();
        }

        return _ui;
    }

    /**
     * Retrieves the title string for the user interface
     *
     * @return
     */
    public String getUITitle() {
        return ToaVegConstants.PROC_NAME + " " + ToaVegConstants.PROC_VERSION;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Loads the processor configuration.
     */
    private void loadConfig() throws ProcessorException {
        URL configPath = getConfigPath();
        File auxdataPath = null;
		
        auxdataPath = getAuxdataInstallDir();	
        _logger.info("... Loading processor configuration '" + configPath + "'");

        ToaVegProcessorConfigurationParser parser = new ToaVegProcessorConfigurationParser();
        parser.parseConfigurationFile(configPath, auxdataPath);
        _config = parser.getConfiguration();

        _logger.info("...... success");
    }

    /**
     * Retrieves the full path to the processor configuration file.
     *
     * @return the config path
     */
    private URL getConfigPath() throws ProcessorException {
        File configFile = new File(getAuxdataInstallDir(), ToaVegConstants.CONFIG_FILE);
        URL configPath = null;
        try {
            configPath = configFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new ProcessorException("Failed to create configuration URL for " + configFile.getPath(), e);
        }
        return configPath;
    }

    /*
    public static File getAuxdataDir() {
        String auxdataPath = System.getProperty(ToaVegConstants.AUXDATA_DIR_PROPERTY);
        
        if (auxdataPath != null && auxdataPath.length() > 0) {
            return new File(auxdataPath);
        }
        return new File(getBeamAuxdataDir(), ToaVegConstants.AUXDATA_DIR);
    }
    */

    /**
     * Loads all auxiliary data needed for the processor.
     */
    private void loadAuxiliaryData() throws IOException, JnnException {
        if (_auxFilesLoaded) {
            return;
        }

        _inStatAux = new ToaVegInputStatisticsLoader();
        _inStatAux.load(_config.getInputStatisticsAuxFile());
        _algo.setInputStatisticsAccess(_inStatAux);

        _outStatAux = new ToaVegOutputStatisticsLoader();
        _outStatAux.load(_config.getOutputStatisticsAuxFile());
        _algo.setOutputStatisticsAccess(_outStatAux);

        _uncertaintyAux = new ToaVegUncertaintyModelLoader();
        _uncertaintyAux.load(_config.getUncertaintyAuxFile());
        _algo.setUncertaintyModelAccess(_uncertaintyAux);
        _algo.InitAlgo();
        _algo.setNn_LaiAuxPath(_config.getNN_LaiAuxFile());
        _algo.setNn_fCoverAuxPath(_config.getNN_fCoverAuxFile());
        _algo.setNn_fAPARAuxPath(_config.getNN_fAPARAuxFile());
        _algo.setNn_LAIxCabAuxPath(_config.getNN_LAIxCabAuxFile());

        _auxFilesLoaded = true;
    }

    /**
     * Loads all necessary parameters from the request except in- and out products. Checks the request type for
     * correctness.
     *
     * @throws ProcessorException on failures
     */
    private void loadRequestParameter() throws ProcessorException {
        checkRequestType();

        Request request = getRequest();

        // initialize logging for the request
        ProcessorUtils.setProcessorLoggingHandler(ToaVegConstants.DEFAULT_LOG_PREFIX, request,
                                                  getName(), getVersion(), getCopyrightInformation());

        // bitmask expression
        // ------------------
        Parameter param = request.getParameter(ToaVegConstants.BITMASK_PARAM_NAME);
        if (param != null) {
            _bitmaskExpression = param.getValueAsText();

        } else {
            _bitmaskExpression = "";
            _bitMaskTerm = null;
            _logger.warning("Parameter '" + ToaVegConstants.BITMASK_PARAM_NAME + "' not set. Processing all pixel!");
        }


    }

    /**
     * Checks the request for correct type.
     */
    private void checkRequestType() throws ProcessorException {
        Request request = getRequest();

        if (!request.isRequestType(ToaVegConstants.REQUEST_TYPE)) {
            throw new ProcessorException("Illegal processing request of type: '" + request.getType() + "'");
        }
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
    }

    /**
     * Loads the input product from the processing request and checks for valid product type.
     *
     * @throws ProcessorException when the product is of an unsupported type
     */
    private void loadInputProduct() throws ProcessorException,
                                           IOException {
        _inputProduct = loadInputProduct(0);

        loadBands();
        loadTiePointGrids();
    }

    /**
     * Loads all input bands needed by the processor.
     */
    private void loadBands() throws ProcessorException {

        for (int n = 0; n < ToaVegConstants.NUM_BANDS; n++) {
            _reflec_bands[n] = loadBand(ToaVegConstants.REFLEC_BAND_NAMES[n]);

        }
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
     * Loads all tie point grids needed by the processor.
     */
    private void loadTiePointGrids() throws ProcessorException {
        _gridLat = loadTiePointGrid(ToaVegConstants.LAT_TIEPOINT_NAME);
        _gridLon = loadTiePointGrid(ToaVegConstants.LON_TIEPOINT_NAME);
        _gridSza = loadTiePointGrid(ToaVegConstants.SZA_TIEPOINT_NAME);
        _gridSaa = loadTiePointGrid(ToaVegConstants.SAA_TIEPOINT_NAME);
        _gridVza = loadTiePointGrid(ToaVegConstants.VZA_TIEPOINT_NAME);
        _gridVaa = loadTiePointGrid(ToaVegConstants.VAA_TIEPOINT_NAME);
        _gridPress =  loadTiePointGrid(ToaVegConstants.PRESS_TIEPOINT_NAME);
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
     * Creates the output product.
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

        // copy stuff from input to output
        // -------------------------
        ProductUtils.copyTiePointGrids(_inputProduct, _outputProduct);
        copyRequestMetaData(_outputProduct);
        copyFlagBands(_inputProduct, _outputProduct);

        // for MERIS FSG / FRG products
        copyBand(EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LATITUDE_BAND_NAME, _inputProduct, _outputProduct);
        copyBand(EnvisatConstants.MERIS_AMORGOS_L1B_CORR_LONGITUDE_BAND_NAME, _inputProduct, _outputProduct);
        copyBand(EnvisatConstants.MERIS_AMORGOS_L1B_ALTIUDE_BAND_NAME, _inputProduct, _outputProduct);

        copyGeoCoding(_inputProduct, _outputProduct);

        // add the target bands
        // --------------------
        addBandsToOutputProduct();

        // flags stuff

        _outputProduct.getFlagCodingGroup().add(VegFlagsManager.getCoding(ToaVegConstants.VEG_FLAGS_BAND_NAME));
        VegFlagsManager.addBitmaskDefsToProduct(_outputProduct,ToaVegConstants.VEG_FLAGS_BAND_NAME);

        // add the metadata
        addMetadataToOutput();

        // initialize the disk represenation
        // ---------------------------------
        writer.writeProductNodes(_outputProduct, new File(prod.getFilePath()));
        copyBandData(getBandNamesToCopy(), _inputProduct, _outputProduct, ProgressMonitor.NULL);
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
     * Retrieves the output product type from the input product type by appending "_VEG" to the type string.
     *
     * @throws org.esa.beam.framework.processor.ProcessorException
     *          when an error occurs
     */
    private String getOutputProductTypeSafe() throws ProcessorException {
        String productType = _inputProduct.getProductType();
        if (productType == null) {
            throw new ProcessorException(ProcessorConstants.LOG_MSG_NO_INPUT_TYPE);
        }

        return productType + ToaVegConstants.PRODUCT_TYPE_APPENDIX;
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
     * Adds all bands of the output product.
     */
    private void addBandsToOutputProduct() {
        int width = _inputProduct.getSceneRasterWidth();
        int height = _inputProduct.getSceneRasterHeight();

        _lai_band = new Band(ToaVegConstants.LAI_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _lai_band.setDescription(ToaVegConstants.LAI_BAND_DESCRIPTION);
        _lai_band.setUnit(ToaVegConstants.LAI_BAND_UNIT);
        _lai_band.setValidPixelExpression(getValidPixelExpression(VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_NAME));
        _outputProduct.addBand(_lai_band);

        _fCover_band = new Band(ToaVegConstants.FCOVER_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _fCover_band.setDescription(ToaVegConstants.FCOVER_BAND_DESCRIPTION);
        _fCover_band.setValidPixelExpression(getValidPixelExpression(VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_NAME));
        _outputProduct.addBand(_fCover_band);

        _cabxLai_band = new Band(ToaVegConstants.LAIXCAB_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _cabxLai_band.setDescription(ToaVegConstants.LAIXCAB_BAND_DESCRIPTION);
        _cabxLai_band.setUnit(ToaVegConstants.LAIXCAB_BAND_UNIT);
        _cabxLai_band.setValidPixelExpression(getValidPixelExpression(VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_NAME));
        _outputProduct.addBand(_cabxLai_band);

        _fapar_band = new Band(ToaVegConstants.FAPAR_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _fapar_band.setDescription(ToaVegConstants.FAPAR_BAND_DESCRIPTION);
        _fapar_band.setValidPixelExpression(getValidPixelExpression(VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_NAME));
        _outputProduct.addBand(_fapar_band);

        _sigma_lai_band =  new Band(ToaVegConstants.SIGMA_LAI_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _sigma_lai_band.setDescription(ToaVegConstants.SIGMA_LAI_BAND_DESCRIPTION);
        _outputProduct.addBand(_sigma_lai_band);

        _sigma_fcover_band =  new Band(ToaVegConstants.SIGMA_FCOVER_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _sigma_fcover_band.setDescription(ToaVegConstants.SIGMA_FCOVER_BAND_DESCRIPTION);
        _outputProduct.addBand(_sigma_fcover_band);

        _sigma_fapar_band =  new Band(ToaVegConstants.SIGMA_FAPAR_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _sigma_fapar_band.setDescription(ToaVegConstants.SIGMA_FAPAR_BAND_DESCRIPTION);
        _outputProduct.addBand(_sigma_fapar_band);

        _sigma_laixcab_band =  new Band(ToaVegConstants.SIGMA_LAIXCAB_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _sigma_laixcab_band.setDescription(ToaVegConstants.SIGMA_LAIXCAB_BAND_DESCRIPTION);
        _outputProduct.addBand(_sigma_laixcab_band);

        _veg_flags_band = new Band(ToaVegConstants.VEG_FLAGS_BAND_NAME, ProductData.TYPE_UINT16, width, height);
        _veg_flags_band.setDescription(ToaVegConstants.VEG_FLAGS_BAND_DESCRIPTION);
        _veg_flags_band.setSampleCoding(VegFlagsManager.getCoding(ToaVegConstants.VEG_FLAGS_BAND_NAME));
        _outputProduct.addBand(_veg_flags_band);
    }

    /**
     * Gets a valid-pixel expression for the given of the out-of-range flag name.
     * @param outOfRangeFlagName the name of the out-of-range flag
     * @return  a valid-pixel expression
     */
    private String getValidPixelExpression(final String outOfRangeFlagName) {
        return "NOT " + getFlagReference(VegFlagsManager.INVALID_FLAG_NAME) + " AND NOT " + getFlagReference(outOfRangeFlagName);
    }

    /**
     * Gets a reference to a flag in the TOA-VEG flags dataset.
     * @param flagName the name of the flag
     * @return  the flag reference
     */
    private String getFlagReference(String flagName) {
        return ToaVegConstants.VEG_FLAGS_BAND_NAME + "." + flagName;
    }

    /**
     * Copies all band data from the input product to the output
     *
     * @throws ProcessorException
     * @throws IOException
     */
    private void copyDuplicatedBands(ProgressMonitor pm) throws ProcessorException,
                                              IOException {
        _logger.fine("Copying flag band data ...");
        copyFlagBandData(_inputProduct, _outputProduct, pm);
        _logger.fine("... success");

    }

    /**
     * Processes the input product line by line
     */
    private void processVegAlgorithm(ProgressMonitor pm) throws ProcessorException, IOException {
        int width = _inputProduct.getSceneRasterWidth();
        int height = _inputProduct.getSceneRasterHeight();

        ToaVegMerisPixel inPixel = new ToaVegMerisPixel();
        inPixel.initPixel(ToaVegConstants.NUM_BANDS);
        VegGenericPixel outPixel = new VegGenericPixel();

        // input arrays
        float[][] reflecBands = new float[ToaVegConstants.NUM_BANDS][width];
        float[] lat = new float[width];
        float[] lon = new float[width];
        float[] sza = new float[width];
        float[] saa = new float[width];
        float[] vza = new float[width];
        float[] vaa = new float[width];
        float[] pression = new float[width];
        float[] solarSpecFlux = new float[width];


        boolean process[] = new boolean[width];
        for (int n = 0; n < width; n++) {
            process[n] = true;
        }

        // output arrays
        float[] lai = new float[width];
        float[] fCover = new float[width];
        float[] cabxLai = new float[width];
        float[] fAPAR = new float[width];
        float[] sigma_LAI = new float[width];
        float[] sigma_fCover = new float[width];
        float[] sigma_fApar = new float[width];
        float[] sigma_LAIxCab = new float[width];
        int[] out_flags = new int[width];


        // progress bar init
        // -----------------
        pm.beginTask(ToaVegConstants.LOG_MSG_GENERATE_PIXEL, height*(ToaVegConstants.NUM_BANDS+16)+1); /*I18N*/

        copyDuplicatedBands(SubProgressMonitor.create(pm, 1));
        
        for (int n=0; n < ToaVegConstants.NUM_BANDS; n++) {
            solarSpecFlux[n] = _reflec_bands[n].getSolarFlux();
        }

        for (int line = 0; line < height; line++) {

            // read input data
            // ---------------
            for (int n = 0; n < ToaVegConstants.NUM_BANDS; n++) {
                _reflec_bands[n].readPixels(0, line, width, 1, reflecBands[n], SubProgressMonitor.create(pm, 1));
            }

            _gridLat.readPixels(0, line, width, 1, lat, SubProgressMonitor.create(pm, 1));
            _gridLon.readPixels(0, line, width, 1, lon, SubProgressMonitor.create(pm, 1));
            _gridSza.readPixels(0, line, width, 1, sza, SubProgressMonitor.create(pm, 1));
            _gridSaa.readPixels(0, line, width, 1, saa, SubProgressMonitor.create(pm, 1));
            _gridVza.readPixels(0, line, width, 1, vza, SubProgressMonitor.create(pm, 1));
            _gridVaa.readPixels(0, line, width, 1, vaa, SubProgressMonitor.create(pm, 1));
            _gridPress.readPixels(0, line, width, 1, pression, SubProgressMonitor.create(pm, 1));

            // evaluate bitmask
            // ----------------
            if (_bitMaskTerm != null) {
                _inputProduct.readBitmask(0, line, width, 1, _bitMaskTerm, process);
            }

            for (int x = 0; x < width; x++) {
                // reset to be sure - risk of pending flags ...
                outPixel.reset();


               if (!process[x]) {
                    outPixel.setInvalidInputFlag();
                } else {



                    for (int n = 0; n < ToaVegConstants.NUM_BANDS; n++) {
                        inPixel.setBand(reflecBands[n][x], n);
                        inPixel.setBand_SolarSpecFlux(solarSpecFlux[n],n);
                    }


                    inPixel.setBand_Pressure(pression[x]);
                    inPixel.setBand_Sza(sza[x]);
                    inPixel.setBand_Vza(vza[x]);
                    inPixel.setBand_Saa(saa[x]);
                    inPixel.setBand_Vaa(vaa[x]);
                    inPixel.setBand_Lat(lat[x]);
                    inPixel.setBand_Lon(lon[x]);


                    _algo.processPixel(inPixel, outPixel);

                }

                lai[x] = outPixel.getBand_LAI();
                cabxLai[x] = outPixel.getBand_CabxLAI();
                fAPAR[x] = outPixel.getBand_fAPAR();
                fCover[x] = outPixel.getBand_fCover();
                out_flags[x] = outPixel.getFlagMask();
                sigma_LAI[x] = outPixel.getBand_sigma_LAI();
                sigma_fCover[x] = outPixel.getBand_sigma_fCover();
                sigma_fApar[x] = outPixel.getBand_sigma_fApar();
                sigma_LAIxCab[x] = outPixel.getBand_sigma_LAIxCab();

            }

            _lai_band.writePixels(0, line, width, 1, lai, SubProgressMonitor.create(pm, 1));
            _fCover_band.writePixels(0, line, width, 1, fCover, SubProgressMonitor.create(pm, 1));
            _cabxLai_band.writePixels(0, line, width, 1, cabxLai, SubProgressMonitor.create(pm, 1));
            _fapar_band.writePixels(0, line, width, 1, fAPAR, SubProgressMonitor.create(pm, 1));
            _sigma_lai_band.writePixels(0,line,width,1,sigma_LAI, SubProgressMonitor.create(pm, 1));
            _sigma_fcover_band.writePixels(0,line, width,1,sigma_fCover, SubProgressMonitor.create(pm, 1));
            _sigma_fapar_band.writePixels(0,line,width,1,sigma_fApar, SubProgressMonitor.create(pm, 1));
            _sigma_laixcab_band.writePixels(0,line,width,1,sigma_LAIxCab, SubProgressMonitor.create(pm, 1));
             _veg_flags_band.writePixels(0, line, width, 1, out_flags, SubProgressMonitor.create(pm, 1));

            // update progressbar
            // ------------------
            pm.worked(1);
            if( pm.isCanceled() ) {
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
        }

        pm.done();
    }

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
     * Adds the metadata to the output product
     */
    private void addMetadataToOutput() {
        // first copy MPH from input product
        // ---------------------------------
        copySrcMetadataToOutput();

        // create metadata root
        MetadataElement destRoot = _outputProduct.getMetadataRoot();
        MetadataElement mph = new MetadataElement(ToaVegConstants.MPH_METADATA_NAME);

        mph.addAttribute(new MetadataAttribute(ToaVegConstants.PRODUCT_METADATA_NAME,
                                               ProductData.createInstance(_outputProduct.getName()),
                                               true));
        mph.addAttribute(new MetadataAttribute(ToaVegConstants.SRC_PRODUCT_METADATA_NAME,
                                               ProductData.createInstance(_inputProduct.getName()),
                                               true));
        mph.addAttribute(new MetadataAttribute(ToaVegConstants.PROCESSOR_METADATA_NAME,
                                               ProductData.createInstance(getName()),
                                               true));
        mph.addAttribute(new MetadataAttribute(ToaVegConstants.PROCESSOR_VERSION_METADATA_NAME,
                                               ProductData.createInstance(getVersion()),
                                               true));

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();

        mph.addAttribute(new MetadataAttribute(ToaVegConstants.PROCESSING_TIME_METADATA_NAME,
                                               ProductData.createInstance(date.toString()),
                                               true));

        destRoot.addElement(mph);
    }

    /**
     * Copies MPH and SPH from the input product to the output product
     */
    private void copySrcMetadataToOutput() {
        MetadataElement srcRoot = _inputProduct.getMetadataRoot();
        MetadataElement destRoot = _outputProduct.getMetadataRoot();
        MetadataElement srcFolder = new MetadataElement(ToaVegConstants.SRC_METADATA_NAME);

        srcFolder.addElement(srcRoot.getElement("MPH"));
        destRoot.addElement(srcFolder);
    }

    @Override
    public void installAuxdata() throws ProcessorException {
        setAuxdataInstallDir(ToaVegConstants.AUXDATA_DIR_PROPERTY, getDefaultAuxdataInstallDir());

        try {
            super.installAuxdata(ResourceInstaller.getSourceUrl(getClass()), "auxdata/"+ ToaVegConstants.AUXDATA_DIR, getAuxdataInstallDir());
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage(), e);
        }
    }
}
