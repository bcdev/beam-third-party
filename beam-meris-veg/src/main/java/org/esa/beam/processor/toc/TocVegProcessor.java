/*
 * $Id: TocVegProcessor.java,v 1.11 2006/04/12 10:10:00 meris Exp $
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
package org.esa.beam.processor.toc;

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
import org.esa.beam.processor.common.auxdata.VegAuxFileLoader;
import org.esa.beam.processor.common.auxdata.VegAuxFilePropsLoader;
import org.esa.beam.processor.common.auxdata.VegNormFactorLoader;
import org.esa.beam.processor.common.utils.VegFlagsManager;
import org.esa.beam.processor.toc.algorithm.TocVegAlgorithm;
import org.esa.beam.processor.toc.auxdata.TocVegInputStatisticsLoader;
import org.esa.beam.processor.toc.auxdata.TocVegOutputStatisticsLoader;
import org.esa.beam.processor.toc.ui.TocVegUi;
import org.esa.beam.processor.toc.utils.TocVegBaerPixel;
import org.esa.beam.processor.toc.utils.TocVegPixel;
import org.esa.beam.processor.toc.utils.TocVegProcessorConfiguration;
import org.esa.beam.processor.toc.utils.TocVegProcessorConfigurationParser;
import org.esa.beam.util.ProductUtils;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class TocVegProcessor extends Processor {

    private Logger _logger;

    private TocVegAlgorithm _algo;
    private TocVegProcessorConfiguration _config;
    private boolean _auxFilesLoaded;

    private Product _inputProduct;
    private Product _outputProduct;

    private VegNormFactorLoader _normFactorAux;
    private TocVegInputStatisticsLoader _inStatAux;
    private TocVegOutputStatisticsLoader _outStatAux;

    private Band[] _reflec_bands;
    private Band _toa_veg_band;

    private TiePointGrid _gridLat;
    private TiePointGrid _gridLon;
    private TiePointGrid _gridSza;
    private TiePointGrid _gridSaa;
    private TiePointGrid _gridVza;
    private TiePointGrid _gridVaa;

    private Band _lai_band;
    private Band _fCover_band;
    private Band _cabxLai_band;
    private Band _fapar_band;
    private Band _delta_fapar_band;
    private Band _veg_flags_band;

    private String _bitmaskExpression;
    private Term _bitMaskTerm;

    private TocVegUi _ui;

    /**
     * Constructs the object with default parameters.
     */
    public TocVegProcessor() {
    }

    public void initProcessor() throws ProcessorException {
        _logger = Logger.getLogger(TocVegConstants.LOGGER_NAME);

        _algo = new TocVegAlgorithm();
        _auxFilesLoaded = false;
        _reflec_bands = new Band[TocVegConstants.NUM_BANDS];

        installAuxdata();
        loadConfig();
    }

    /**
     * Retrieves the request element factory of the processor.
     *
     * @return
     */
    public RequestElementFactory getRequestElementFactory() {
        return VegRequestElementFactory.getTocVegInstance();
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
        return TocVegConstants.PROC_NAME;
    }

    /**
     * Retrieves a version string of the processor
     */
    public String getVersion() {
        return TocVegConstants.PROC_VERSION;
    }

    /**
     * Retrieves copyright information of the processor
     */
    public String getCopyrightInformation() {
        return TocVegConstants.PROC_COPYRIGHT;
    }

    /**
     * Creates the GUI for the processor.
     */
    public ProcessorUI createUI() throws ProcessorException {
        if (_ui == null) {
            _ui = new TocVegUi();
        }

        return _ui;
    }

    /**
     * Retrieves the title string for the user interface
     *
     * @return
     */
    public String getUITitle() {
        return TocVegConstants.PROC_NAME + " " + TocVegConstants.PROC_VERSION;
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
        _logger.info("... Loading processor configuration '" + configPath.toString() + "'");

        TocVegProcessorConfigurationParser parser = new TocVegProcessorConfigurationParser();
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
        File configFile = new File(getAuxdataInstallDir(), TocVegConstants.CONFIG_FILE);
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
        String auxdataPath = System.getProperty(TocVegConstants.AUXDATA_DIR_PROPERTY);
        if (auxdataPath != null && auxdataPath.length() > 0) {
            return new File(auxdataPath);
        }
        return new File(SystemUtils.getBeamAuxdataDir(), TocVegConstants.AUXDATA_DIR);
    }
    */

    /**
     * Loads all auxiliary data needed for the processor.
     */
    private void loadAuxiliaryData() throws IOException, JnnException {
        if (_auxFilesLoaded) {
            return;
        }
        _normFactorAux = new VegNormFactorLoader(TocVegConstants.NORMALISATION_FACTOR_DEFAULT,
                                                 TocVegConstants.LOGGER_NAME);
        _normFactorAux.load(_config.getNormalisationFactorAuxFile(), TocVegConstants.AUX_VERSION_KEY);
        _algo.setNormFactorAccess(_normFactorAux);

        _inStatAux = new TocVegInputStatisticsLoader();
        _inStatAux.load(_config.getInputStatisticsAuxFile());
        _algo.setInputStatisticsAccess(_inStatAux);

        _outStatAux = new TocVegOutputStatisticsLoader();
        _outStatAux.load(_config.getOutputStatisticsAuxFile());
        _algo.setOutputStatisticsAccess(_outStatAux);

        _algo.InitAlgo();

        _algo.setNnAuxPath(_config.getNN_AuxFile());

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
        ProcessorUtils.setProcessorLoggingHandler(TocVegConstants.DEFAULT_LOG_PREFIX, request,
                                                  getName(), getVersion(), getCopyrightInformation());

        // bitmask expression
        // ------------------
        Parameter param = request.getParameter(TocVegConstants.BITMASK_PARAM_NAME);
        if (param != null) {
            _bitmaskExpression = param.getValueAsText();

        } else {
            _bitmaskExpression = "";
            _bitMaskTerm = null;
            _logger.warning("Parameter '" + TocVegConstants.BITMASK_PARAM_NAME + "' not set. Processing all pixel!");
        }

    }

    /**
     * Checks the request for correct type.
     */
    private void checkRequestType() throws ProcessorException {
        Request request = getRequest();

        if (!request.isRequestType(TocVegConstants.REQUEST_TYPE)) {
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
        for (int n = 0; n < TocVegConstants.NUM_BANDS; n++) {
            _reflec_bands[n] = loadBand(TocVegConstants.REFLEC_BAND_NAMES[n]);
        }
        _toa_veg_band = loadBand(TocVegConstants.TOA_VEG_BAND_NAME);
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
        _gridLat = loadTiePointGrid(TocVegConstants.LAT_TIEPOINT_NAME);
        _gridLon = loadTiePointGrid(TocVegConstants.LON_TIEPOINT_NAME);
        _gridSza = loadTiePointGrid(TocVegConstants.SZA_TIEPOINT_NAME);
        _gridSaa = loadTiePointGrid(TocVegConstants.SAA_TIEPOINT_NAME);
        _gridVza = loadTiePointGrid(TocVegConstants.VZA_TIEPOINT_NAME);
        _gridVaa = loadTiePointGrid(TocVegConstants.VAA_TIEPOINT_NAME);
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

        // copy the tie point raster
        // -------------------------
        ProductUtils.copyTiePointGrids(_inputProduct, _outputProduct);

        // copy geocoding and flags
        // ------------------------
        copyFlagBands(_inputProduct, _outputProduct);
        copyGeoCoding(_inputProduct, _outputProduct);

        // write the processing request as metadata
        copyRequestMetaData(_outputProduct);

        // add the target bands
        // --------------------
        addBandsToOutputProduct();

        // flags stuff
        _outputProduct.getFlagCodingGroup().add(VegFlagsManager.getCoding(TocVegConstants.VEG_FLAGS_BAND_NAME));
        VegFlagsManager.addBitmaskDefsToProduct(_outputProduct, TocVegConstants.VEG_FLAGS_BAND_NAME);

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

        return productType + TocVegConstants.PRODUCT_TYPE_APPENDIX;
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

        _lai_band = new Band(TocVegConstants.LAI_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _lai_band.setDescription(TocVegConstants.LAI_BAND_DESCRIPTION);
        _lai_band.setUnit(TocVegConstants.LAI_BAND_UNIT);
        _outputProduct.addBand(_lai_band);

        _fCover_band = new Band(TocVegConstants.FCOVER_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _fCover_band.setDescription(TocVegConstants.FCOVER_BAND_DESCRIPTION);
        _outputProduct.addBand(_fCover_band);

        _cabxLai_band = new Band(TocVegConstants.LAIXCAB_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _cabxLai_band.setDescription(TocVegConstants.LAIXCAB_BAND_DESCRIPTION);
        _cabxLai_band.setUnit(TocVegConstants.LAIXCAB_BAND_UNIT);
        _outputProduct.addBand(_cabxLai_band);

        _fapar_band = new Band(TocVegConstants.FAPAR_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _fapar_band.setDescription(TocVegConstants.FAPAR_BAND_DESCRIPTION);
        _outputProduct.addBand(_fapar_band);

        _delta_fapar_band = new Band(TocVegConstants.DELTA_FAPAR_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        _delta_fapar_band.setDescription(TocVegConstants.DELTA_FAPAR_BAND_DESCRIPTION);
        _outputProduct.addBand(_delta_fapar_band);

        _veg_flags_band = new Band(TocVegConstants.VEG_FLAGS_BAND_NAME, ProductData.TYPE_UINT16, width, height);
        _veg_flags_band.setDescription(TocVegConstants.VEG_FLAGS_BAND_DESCRIPTION);
        _veg_flags_band.setSampleCoding(VegFlagsManager.getCoding(TocVegConstants.VEG_FLAGS_BAND_NAME));
        _outputProduct.addBand(_veg_flags_band);
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

        TocVegBaerPixel inPixel = new TocVegBaerPixel();
        inPixel.initPixel(TocVegConstants.NUM_BANDS);
        TocVegPixel outPixel = new TocVegPixel();

        // input arrays
        float[][] reflecBands = new float[TocVegConstants.NUM_BANDS][width];
        float[] toa_veg = new float[width];
        float[] lat = new float[width];
        float[] lon = new float[width];
        float[] sza = new float[width];
        float[] saa = new float[width];
        float[] vza = new float[width];
        float[] vaa = new float[width];

        boolean process[] = new boolean[width];
        for (int n = 0; n < width; n++) {
            process[n] = true;
        }

        // output arrays
        float[] lai = new float[width];
        float[] fCover = new float[width];
        float[] cabxLai = new float[width];
        float[] fAPAR = new float[width];
        float[] delta_fAPAR = new float[width];
        int[] out_flags = new int[width];

        // progress bar init
        // -----------------
        pm.beginTask(TocVegConstants.LOG_MSG_GENERATE_PIXEL, height * (TocVegConstants.NUM_BANDS + 13) + 1); /*I18N*/

        copyDuplicatedBands(SubProgressMonitor.create(pm, 1));

        for (int line = 0; line < height; line++) {

            // read input data
            // ---------------
            for (int n = 0; n < TocVegConstants.NUM_BANDS; n++) {
                _reflec_bands[n].readPixels(0, line, width, 1, reflecBands[n], SubProgressMonitor.create(pm, 1));
            }

            _toa_veg_band.readPixels(0, line, width, 1, toa_veg, SubProgressMonitor.create(pm, 1));
            _gridLat.readPixels(0, line, width, 1, lat, SubProgressMonitor.create(pm, 1));
            _gridLon.readPixels(0, line, width, 1, lon, SubProgressMonitor.create(pm, 1));
            _gridSza.readPixels(0, line, width, 1, sza, SubProgressMonitor.create(pm, 1));
            _gridSaa.readPixels(0, line, width, 1, saa, SubProgressMonitor.create(pm, 1));
            _gridVza.readPixels(0, line, width, 1, vza, SubProgressMonitor.create(pm, 1));
            _gridVaa.readPixels(0, line, width, 1, vaa, SubProgressMonitor.create(pm, 1));

            // evaluate bitmask
            // ----------------
            if (_bitMaskTerm != null) {
                _inputProduct.readBitmask(0, line, width, 1, _bitMaskTerm, process);
            }

            for (int x = 0; x < width; x++) {

                // reset to be sure - risk of pending flags ...
                outPixel.resetPixel();

                if (!process[x]) {
                    outPixel.setInvalidInputFlag();
                } else {

                    for (int n = 0; n < TocVegConstants.NUM_BANDS; n++) {
                        inPixel.setBand(reflecBands[n][x], n);
                    }

                    inPixel.setBand_TOAVEG(toa_veg[x]);
                    inPixel.setBand_Sza(sza[x]);
                    inPixel.setBand_Vza(vza[x]);
                    inPixel.setBand_Saa(saa[x]);
                    inPixel.setBand_Vaa(vaa[x]);
                    inPixel.setBand_Lat(lat[x]);
                    inPixel.setBand_Lon(lon[x]);

                    _algo.processPixel(inPixel, outPixel);
                }

                lai[x] = outPixel.getBand_LAI();
                fCover[x] = outPixel.getBand_fCover();
                cabxLai[x] = outPixel.getBand_CabxLAI();
                fAPAR[x] = outPixel.getBand_fAPAR();
                delta_fAPAR[x] = outPixel.getBand_delta_fAPAR();
                out_flags[x] = outPixel.getFlagMask();
            }

            _lai_band.writePixels(0, line, width, 1, lai, SubProgressMonitor.create(pm, 1));
            _fCover_band.writePixels(0, line, width, 1, fCover, SubProgressMonitor.create(pm, 1));
            _cabxLai_band.writePixels(0, line, width, 1, cabxLai, SubProgressMonitor.create(pm, 1));
            _fapar_band.writePixels(0, line, width, 1, fAPAR, SubProgressMonitor.create(pm, 1));
            _delta_fapar_band.writePixels(0, line, width, 1, delta_fAPAR, SubProgressMonitor.create(pm, 1));
            _veg_flags_band.writePixels(0, line, width, 1, out_flags, SubProgressMonitor.create(pm, 1));

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
        MetadataElement mph = new MetadataElement(TocVegConstants.MPH_METADATA_NAME);

        mph.addAttribute(new MetadataAttribute(TocVegConstants.PRODUCT_METADATA_NAME,
                                               ProductData.createInstance(_outputProduct.getName()),
                                               true));
        mph.addAttribute(new MetadataAttribute(TocVegConstants.SRC_PRODUCT_METADATA_NAME,
                                               ProductData.createInstance(_inputProduct.getName()),
                                               true));
        mph.addAttribute(new MetadataAttribute(TocVegConstants.PROCESSOR_METADATA_NAME,
                                               ProductData.createInstance(getName()),
                                               true));
        mph.addAttribute(new MetadataAttribute(TocVegConstants.PROCESSOR_VERSION_METADATA_NAME,
                                               ProductData.createInstance(getVersion()),
                                               true));

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();

        mph.addAttribute(new MetadataAttribute(TocVegConstants.PROCESSING_TIME_METADATA_NAME,
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
        MetadataElement srcFolder = new MetadataElement(TocVegConstants.SRC_METADATA_NAME);

        srcFolder.addElement(srcRoot.getElement("MPH"));
        destRoot.addElement(srcFolder);
    }

    /**
     * Adds the metadata felds of the auxiliary files to the output product
     *
     * @param mph
     */
    private void addAuxFilesToMetadata(MetadataElement mph) {

        addStubbedMetadataElement(mph, TocVegConstants.NORMALISATION_AUX_METADATA_NAME,
                                  _config.getNormalisationFactorAuxFile(),
                                  _normFactorAux,
                                  TocVegConstants.NORMALISATION_AUX_METADATA_NAME);

        writeInputStatisticsMetadata(mph);
        writeOutputStatisticsMetadata(mph);
    }

    /**
     * Writes the input statistics meta information to the metadataelement passed in
     *
     * @param mph
     */
    private void writeInputStatisticsMetadata(MetadataElement mph) {
        MetadataElement auxMeta = new MetadataElement(TocVegConstants.INPUT_STATISTICS_AUX_METADATA_NAME);

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.AUX_FILE_NAME_METADATA_NAME,
                                                   ProductData.createInstance(_config.getInputStatisticsAuxFile()),
                                                   true));
        addAuxFileDescAndVersionToMetadata(_inStatAux, auxMeta);

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.THETA_S_MEAN_AUX_KEY,
                                                   ProductData.createInstance(
                                                           new double[]{_inStatAux.getTheta_S_Mean()}),
                                                   true));

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.THETA_S_STD_AUX_KEY,
                                                   ProductData.createInstance(
                                                           new double[]{_inStatAux.getTheta_S_StdDev()}),
                                                   true));

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.THETA_V_MEAN_AUX_KEY,
                                                   ProductData.createInstance(
                                                           new double[]{_inStatAux.getTheta_V_Mean()}),
                                                   true));

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.THETA_V_STD_AUX_KEY,
                                                   ProductData.createInstance(
                                                           new double[]{_inStatAux.getTheta_V_StdDev()}),
                                                   true));

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.COS_PHI_MEAN_AUX_KEY,
                                                   ProductData.createInstance(
                                                           new double[]{_inStatAux.getCos_Phi_Mean()}),
                                                   true));

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.COS_PHI_STD_AUX_KEY,
                                                   ProductData.createInstance(
                                                           new double[]{_inStatAux.getCos_Phi_StdDev()}),
                                                   true));

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.R_MEAN_AUX_KEY,
                                                   ProductData.createInstance(new double[]{_inStatAux.getR_Mean()}),
                                                   true));

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.R_STD_AUX_KEY,
                                                   ProductData.createInstance(new double[]{_inStatAux.getR_StdDev()}),
                                                   true));
        mph.addElement(auxMeta);
    }

    /**
     * Writes the input statistics meta information to the metadataelement passed in
     *
     * @param mph
     */
    private void writeOutputStatisticsMetadata(MetadataElement mph) {
        MetadataElement auxMeta = new MetadataElement(TocVegConstants.OUTPUT_STATISTICS_AUX_METADATA_NAME);

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.AUX_FILE_NAME_METADATA_NAME,
                                                   ProductData.createInstance(_config.getOutputStatisticsAuxFile()),
                                                   true));
        addAuxFileDescAndVersionToMetadata(_outStatAux, auxMeta);

        double[] vec = _outStatAux.getFAPARConstants(null);
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.FAPAR_MEAN_KEY,
                                                   ProductData.createInstance(new double[]{vec[0]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.FAPAR_STD_KEY,
                                                   ProductData.createInstance(new double[]{vec[1]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.FAPAR_MIN_KEY,
                                                   ProductData.createInstance(new double[]{vec[2]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.FAPAR_MAX_KEY,
                                                   ProductData.createInstance(new double[]{vec[3]}),
                                                   true));

        vec = _outStatAux.getFCoverConstants(vec);
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.FCOVER_MEAN_KEY,
                                                   ProductData.createInstance(new double[]{vec[0]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.FCOVER_STD_KEY,
                                                   ProductData.createInstance(new double[]{vec[1]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.FCOVER_MIN_KEY,
                                                   ProductData.createInstance(new double[]{vec[2]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.FCOVER_MAX_KEY,
                                                   ProductData.createInstance(new double[]{vec[3]}),
                                                   true));

        vec = _outStatAux.getLAIConstants(vec);
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.LAI_MEAN_KEY,
                                                   ProductData.createInstance(new double[]{vec[0]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.LAI_STD_KEY,
                                                   ProductData.createInstance(new double[]{vec[1]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.LAI_MIN_KEY,
                                                   ProductData.createInstance(new double[]{vec[2]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.LAI_MAX_KEY,
                                                   ProductData.createInstance(new double[]{vec[3]}),
                                                   true));

        vec = _outStatAux.getLAIxCabConstants(vec);
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.LAIXCAB_MEAN_KEY,
                                                   ProductData.createInstance(new double[]{vec[0]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.LAIXCAB_STD_KEY,
                                                   ProductData.createInstance(new double[]{vec[1]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.LAIXCAB_MIN_KEY,
                                                   ProductData.createInstance(new double[]{vec[2]}),
                                                   true));
        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.LAIXCAB_MAX_KEY,
                                                   ProductData.createInstance(new double[]{vec[3]}),
                                                   true));
        mph.addElement(auxMeta);
    }

    /**
     * Generic function. Adds stubbed auxiliary data elements to the MetaDataElement passed in.
     *
     * @param mph
     */
    private void addStubbedMetadataElement(MetadataElement mph, String elemName, String auxFilePath,
                                           VegAuxFilePropsLoader loader, String stub) {
        MetadataElement auxMeta = new MetadataElement(elemName);

        auxMeta.addAttribute(new MetadataAttribute(TocVegConstants.AUX_FILE_NAME_METADATA_NAME,
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
     * Adds aux file version and description strings to the metadata.
     *
     * @param loader
     * @param auxElem
     */
    private void addAuxFileDescAndVersionToMetadata(VegAuxFileLoader loader, MetadataElement auxElem) {
        String temp;

        temp = loader.getVersionString(TocVegConstants.AUX_VERSION_KEY);
        if (temp == null) {
            temp = TocVegConstants.AUX_VAL_UNKNOWN;
        }
        auxElem.addAttribute(new MetadataAttribute(TocVegConstants.AUX_FILE_VERSION_METADATA_NAME,
                                                   ProductData.createInstance(temp),
                                                   true));

        temp = loader.getDescription(TocVegConstants.AUX_DESCRIPTION_KEY);
        if (temp == null) {
            temp = TocVegConstants.AUX_VAL_NONE;
        }
        auxElem.addAttribute(new MetadataAttribute(TocVegConstants.AUX_FILE_DESCRIPTION_METADATA_NAME,
                                                   ProductData.createInstance(temp),
                                                   true));
    }

    @Override
    public void installAuxdata() throws ProcessorException {
        setAuxdataInstallDir(TocVegConstants.AUXDATA_DIR_PROPERTY, getDefaultAuxdataInstallDir());
        try {
            super.installAuxdata(ResourceInstaller.getSourceUrl(getClass()), "auxdata/" + TocVegConstants.AUXDATA_DIR,
                                 getAuxdataInstallDir());
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage(), e);
        }

    }
}
