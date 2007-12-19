/*
 * $Id: FaparProcessor.java,v 1.13 2007/12/11 15:58:49 andreio Exp $
 * Written by: Ophelie Aussedat, September, 2004
 *
 * Copyright (C) 2004 by STARS
 * Modified 2007 - MC + NG to copy corrected latitude and
 *                                 longitude from Amorgos Output 
 * 	Academic users:
 * 	Are authorized to use this code for research and teaching,
 * 	but must acknowledge the use of these routines explicitly
 * 	and refer to the references in any publication or work.
 * 	Original, complete, unmodified versions of these codes may be
 * 	distributed free of charge to colleagues involved in similar
 * 	activities.  Recipients must also agree with and abide by the
 * 	same rules. The code may not be sold, nor distributed to
 * 	commercial parties, under any circumstances.
 *
 * 	Commercial and other users:
 * 	Use of this code in commercial applications is strictly
 * 	forbidden without the written approval of the authors.
 * 	Even with such authorization the code may not be distributed
 * 	or sold to any other commercial or business partners under
 * 	any circumstances.
 * 
 * This software is provided as is without any warranty whatsoever.
 * 
 * REFERENCES:
 *      N. Gobron, M. Taberner, B. Pinty, F. Melin, M. M. Verstraete and J.-L. 
 *      Widlowski (2002) 'MERIS Land Algorithm: preliminary results', in 
 *	Proceedings of the ENVISAT Validation Workshop, Frascati, Italy, 09-13 
 *	December, 2002, European Space Agency, SP 531
 *
 * 	N. Gobron, B. Pinty, M. M. Verstraete and M. Taberner (2002) 'Medium 
 *	Resolution Imaging Spectrometer (MERIS) - Level 2 Land Surface Products 
 *	- Algorithm Theoretical Basis Document, Institute for Environment and 
 *	Sustainability, *EUR Report No. 20143 EN*, 19 pp
 *
 *	Gobron, N., B. Pinty, M. M. Verstraete and M. Taberner (2002) 'Medium 
 *	Resolution Imaging Spectrometer (MERIS) - An optimized FAPAR Algorithm - 
 *	Theoretical Basis Document, Institute for Environment and 
 *	Sustainability, *EUR Report No. 20149 EN*, 19 pp
 */
package it.jrc.beam.fapar;

import com.bc.ceres.core.ProgressMonitor;

import org.esa.beam.framework.processor.RequestElementFactory;
import org.esa.beam.dataio.dimap.DimapProductConstants;
import org.esa.beam.framework.dataio.ProductWriter;
import org.esa.beam.util.ProductUtils;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.TiePointGeoCoding;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.framework.processor.Processor;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.Request;
import org.esa.beam.framework.processor.ui.ProcessorUI;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.util.math.RsMathUtils;
import org.esa.beam.framework.processor.ProcessorUtils;
import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.BitmaskDef;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.awt.Color;

/**
 * The <code>FaparProcessor</code> implements all specific functionality to calculate a FAPAR product from a given MERIS
 * product.
 * <p>
 * Bands created in the FAPAR product:
 * 	<li>Red and Nir rectified reflectance Bands</li>
 * 	<li>Blue Green Red and Near reflectance Bands</li>
 * 	<li>flags Band</li>
 * ng ++
 * 	<li>copy the corrected latitude and longitude values</li>
 * ng --
 * </p>
 * <p>
 * The flags are composed of the level 1 MERIS flags and 5 flags computed by the MGVI algorithm:
 * <li>mgvi_bad : bad values found from the reflectances values at the beginning of the algorithm</li>
 * <li>mgvi_csi : Cloud, snow or ice pixel found from the reflectances values at the beginning of the algorithm</li>
 * <li>mgvi_ws : Water or deep shadow pixel found from the reflectances values at the  beginning of the algorithm</li>
 * <li>mgvi_bright : Bright pixel found from the reflectances values at the beginning of the algorithm</li>
 * <li>mgvi_inval_rec : Invalid pixel found by the MGVI algorithm (invalid value of one of the rectified or fapar invalid value computed)</li>
 * </p>
 */
public class FaparProcessor extends Processor {

    // Constants
    public final static String DEFAULT_OUTPUT_PRODUCT_NAME = "fapar.dim";
    public final static String DEFAULT_OUTPUT_DIR_NAME = "OUTPUT_FAPAR";
    public final static String DEFAULT_PRODUCT_OUTPUT_FORMAT = DimapProductConstants.DIMAP_FORMAT_NAME;
    public static final String LOGGER_NAME = "fapar_processor";          // MC ++
    // the required request type
    public static final String REQUEST_TYPE = "FAPAR";

    // some string constant definitions
    private static final String _productName = "FAPAR_MERIS";
    private static final String _productType = "FAPAR_MERIS";
    private static final String _outputBandName = "FAPAR";

    // Name of the Bands needed
    /**blue radiance band name in MERIS product*/
    public static final String INPUT_BAND_NAME_BLUE = "radiance_2";	
    /**red radiance band name in MERIS product*/
    public static final String INPUT_BAND_NAME_RED = "radiance_8";
    /**nir radiance band name in MERIS product*/
    public static final String INPUT_BAND_NAME_NIR = "radiance_13";
    /**sun zenith tie point grid name in MERIS product*/
    public static final String INPUT_TPG_NAME_SZA = "sun_zenith";
    /**view zenith tie point grid name in MERIS product*/
    public static final String INPUT_TPG_NAME_VZA = "view_zenith";
    /**sun azimuth tie point grid name in MERIS product*/
    public static final String INPUT_TPG_NAME_SAA = "sun_azimuth";
    /**view azimuth tie point grid name in MERIS product*/
    public static final String INPUT_TPG_NAME_VAA = "view_azimuth";
    
    // MC ++
    /**Corrected latitude in MERIS product from Amorgos*/
    public static final String INPUT_BAND_NAME_CORR_LATITUDE = "corr_latitude";	
    /**Corrected longitude in MERIS product from Amorgos*/
    public static final String INPUT_BAND_NAME_CORR_LONGITUDE = "corr_longitude";	
    // MC --

    private static final String _flagName = "l1_flags";
    private static final String _latTiePointName = "latitude";
    private static final String _lonTiePointName = "longitude";

    // Processor description
    /**Processor Name*/
    public static final String PROCESSOR_NAME = "Fapar Processor";
    private static final String _processorVersion = "2.1";
    //
    private static final String _processorCopyrightInfo = "Copyright (C) 2007 SOLO";
    
    private static final String _processorLoggerName = "beam.processor.fapar";

    // index for _sun_spec array
    private static final int _blue = 0;	
    private static final int _red = 1;
    private static final int _nir = 2;

    
    // Members attributs definition
    // the Products needed
    private Product _inputProduct;
    private Product _outputProduct;
   
    // The bands needed
    private Band _redInputBand;
    private Band _blueInputBand;
    private Band _nirInputBand;
    private Band _greenInputBand;
    // MC ++
    // input 
    private Band _corrlatitudeInputBand;
    private Band _corrlongitudeInputBand;
    // output
    private Band _corrlatitude;
    private Band _corrlongitude;
    // MC ++
    
    private Band _faparBand;
    private Band _redRecBand;
    private Band _nirRecBand;
    
    private Band _reflectanceBlueBand;
    private Band _reflectanceRedBand;
    private Band _reflectanceNirBand;
    private Band _reflectanceGreenBand;
    private Band _flag;
    private Band _inFlag;
    
    // The tie point grids needed
    private TiePointGrid _szaBand;
    private TiePointGrid _saaBand;
    private TiePointGrid _vzaBand;
    private TiePointGrid _vaaBand;
    private float[] _sun_spec;	// Sun Flux	
    
    // Loggers
    private Logger _logger;

    // Algo
    private FaparAlgorithm _algorithm;

    //Flag to set if input data are AMORGOS output, i.e. there are corrected lat/long fields
    private boolean _AmorgosData;
    
    /**
     * Constructs the object with default parameters.
     * <p/>
     * Creates the logger and the associated algorithm.
     */
    public FaparProcessor() {
        _logger = Logger.getLogger(_processorLoggerName);
	
	_algorithm=new FaparAlgorithm();

	_sun_spec=new float[3];
    }

    /**
     * Worker method invoked by framework to process a single request.
     * <p/>
     * Calls the required methods in order to:
     * <li>Get the required data from the input product</li> 
     * <li>Construct the output product</li>
     * <li>Compute and write the fapar and associated bands</li>
     *
     * @throws ProcessorException if the process did not succed
     */
    public void process(ProgressMonitor pm) throws ProcessorException {

        try {
            _logger.info("Started processing ...");

	    // initialize logging for the request
	    ProcessorUtils.setProcessorLoggingHandler("fapar", getRequest(), getName(), getVersion(), getCopyrightInformation());
	    
            // check the request type (throws :  ProcessorException)
            Request.checkRequestType(getRequest(), REQUEST_TYPE);

            // load input product (throws : ProcessorException, IOException)
            loadInputProduct();

            // create the output product (throws : ProcessorException, IOException)
            createOutputProduct();

            // and process the fapar
            processFapar(pm);

            _logger.info(ProcessorConstants.LOG_MSG_SUCCESS);
	    
	_outputProduct.closeProductWriter();
	_outputProduct.dispose();
	_outputProduct = null;
        } catch (IOException e) {
            // catch all exceptions expect ProcessorException and throw ProcessorException
            throw new ProcessorException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves the request element factory for the FAPAR processor
     */
    public RequestElementFactory getRequestElementFactory() {
	    return FaparRequestElementFactory.getInstance();
    }
    
    /**
     * Retrieves the name of the processor
     */
    public String getName() {
        return PROCESSOR_NAME;
    }

    /**
     * Retrieves a version string of the processor
     */
    public String getVersion() {
        return _processorVersion;
    }

    /**
     * Retrieves copyright information of the processor
     */
    public String getCopyrightInformation() {
        return _processorCopyrightInfo;
    }

    /**
     * Creates the UI for the processor. Override to perform processor specific UI initializations.
     */
    public ProcessorUI createUI() throws ProcessorException {
        return new FaparProcessorUI();
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Loads the input product from the request. Opens the product and opens all bands and tie points grid needed to process the fapar.
     * @throws ProcessorException if a band or tie point grid is not found
     * @throws IOException
     */
    private void loadInputProduct() throws ProcessorException,
                                           IOException {
        _inputProduct = loadInputProduct(0);

        // try to retrieve the bands needed
        // --------------------------------
        _blueInputBand = _inputProduct.getBand(INPUT_BAND_NAME_BLUE);
        if (_blueInputBand == null) {
            throw new ProcessorException("Cannot load band " + INPUT_BAND_NAME_BLUE);
        }
        _logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND + INPUT_BAND_NAME_BLUE);

        _redInputBand = _inputProduct.getBand(INPUT_BAND_NAME_RED);
        if (_redInputBand == null) {
            throw new ProcessorException("Cannot load band" + INPUT_BAND_NAME_RED);
        }
        _logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND + INPUT_BAND_NAME_RED);
        
	     _nirInputBand = _inputProduct.getBand(INPUT_BAND_NAME_NIR);
        if (_nirInputBand == null) {
            throw new ProcessorException("Cannot load band" + INPUT_BAND_NAME_NIR);
        }
        _logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND + INPUT_BAND_NAME_NIR);

	// Green is not mandatory
	_greenInputBand = _inputProduct.getBand("radiance_5");
	if (_greenInputBand != null)
		_logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND + "radiance_5");
	
	_inFlag = _inputProduct.getBand(_flagName);
        if (_inFlag == null) {
		_logger.info("No Band "  + _flagName);
	}else
		_logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND  + _flagName);
	
	// Try to retrieve the TiePointGrid needed
	_szaBand = _inputProduct.getTiePointGrid(INPUT_TPG_NAME_SZA);
        if (_szaBand == null) {
            throw new ProcessorException("Cannot load TiePointGrid" + INPUT_TPG_NAME_SZA);
        }
	_logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND  + INPUT_TPG_NAME_SZA);

	_saaBand = _inputProduct.getTiePointGrid(INPUT_TPG_NAME_SAA);
        if (_saaBand == null) {
            throw new ProcessorException("Cannot load TiePointGrid" + INPUT_TPG_NAME_SAA);
        }
	_logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND  + INPUT_TPG_NAME_SAA);
	
	_vzaBand = _inputProduct.getTiePointGrid(INPUT_TPG_NAME_VZA);
        if (_vzaBand == null) {
            throw new ProcessorException("Cannot load TiePointGrid" + INPUT_TPG_NAME_VZA);
        }
	_logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND  + INPUT_TPG_NAME_VZA);
	
	_vaaBand = _inputProduct.getTiePointGrid(INPUT_TPG_NAME_VAA);
        if (_vaaBand == null) {
            throw new ProcessorException("Cannot load TiePointGrid" + INPUT_TPG_NAME_VAA);
        }
	_logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND  + INPUT_TPG_NAME_VAA);
     
         // MC ++ 
          _corrlongitudeInputBand = _inputProduct.getBand(INPUT_BAND_NAME_CORR_LONGITUDE);
        if (_corrlongitudeInputBand == null) {
            //throw new ProcessorException("Cannot load band " + INPUT_BAND_NAME_CORR_LONGITUDE);
            _AmorgosData = false;
        }
        else {
            _AmorgosData = true;
            _logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND + INPUT_BAND_NAME_CORR_LONGITUDE);
        }
       
          _corrlatitudeInputBand = _inputProduct.getBand(INPUT_BAND_NAME_CORR_LATITUDE);
        if (_corrlatitudeInputBand == null) {
            //throw new ProcessorException("Cannot load band " + INPUT_BAND_NAME_CORR_LATITUDE);
            _AmorgosData = false;
        }
        else {
            _AmorgosData = true;
            _logger.info(ProcessorConstants.LOG_MSG_LOADED_BAND + INPUT_BAND_NAME_CORR_LATITUDE);
        }



        }
        // MC --


    /**
     * Creates the output product skeleton.
     * </p>
     * Copies the <code>l1_flags</code> from the input product and change the name in <code>l2_flags</code>. If they do not exist create a new flag <code>l2_flags</code>. Then add the new MGVI flags.
     */
    private void createOutputProduct() throws ProcessorException,
                                              IOException {
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

        // create the in memory represenation of the output product
        // ---------------------------------------------------------
        // the product itself
        _outputProduct = new Product(_productName, _productType, sceneWidth, sceneHeight);
	_outputProduct.setDescription("Fraction of Photosyntheticaly Absorbed radiation computed by the MGVI algorithm");

	// Copy the Flags and the mask
	ProductUtils.copyFlagCodings(_inputProduct, _outputProduct);

	// Get the flags and add some
	FlagCoding outputFlags=_outputProduct.getFlagCoding(_flagName);
	if (outputFlags==null) {
		_logger.info("Flag Coding not found");
		outputFlags=new FlagCoding("l2_flags");
		_outputProduct.addFlagCoding(outputFlags);
	}
	outputFlags.setName("l2_flags");

	// Add MGVI flags
	outputFlags.addFlag("MGVI_BAD_DATA",0x100,"Bad pixel flagged by MGVI processing");
	outputFlags.addFlag("MGVI_CSI",0x200,"Cloud, snow or ice pixel flagged by MGVI processing");
	outputFlags.addFlag("MGVI_WS",0x400,"Water or deep shadow pixel flagged by MGVI processing");
	outputFlags.addFlag("MGVI_BRIGHT",0x800,"Bright pixel flagged by MGVI processing");
	outputFlags.addFlag("MGVI_INVAL_FAPAR",0x1000,"Invalid rectification flagged by MGVI processing");

	BitmaskDef[] bmdefs=_inputProduct.getBitmaskDefs();
	BitmaskDef b;
	for (int i=0;i<_inputProduct.getNumBitmaskDefs();i++)
	{
		b=new BitmaskDef(bmdefs[i].getName(), bmdefs[i].getDescription(), (bmdefs[i].getExpr()).replaceAll("l1", "l2"), bmdefs[i].getColor(), bmdefs[i].getTransparency());
		_outputProduct.addBitmaskDef(b);
	}
	b=new BitmaskDef("mgvi_bad", "Bad pixel flagged by MGVI processing", "l2_flags.MGVI_BAD_DATA", new Color(51,255,204)/*(255, 153, 102)*/, (float)0.5);
	_outputProduct.addBitmaskDef(b);
	b=new BitmaskDef("mgvi_csi", "Cloud, snow or ice pixel flagged by MGVI processing", "l2_flags.MGVI_CSI", new Color(51,153,255)/*(255, 153, 0)*/, (float)0.5);
	_outputProduct.addBitmaskDef(b);	
	b=new BitmaskDef("mgvi_ws", "Water or deep shadow pixel flagged by MGVI processing", "l2_flags.MGVI_WS", new Color(51,204,255)/*(204, 102,0)*/, (float)0.5);
	_outputProduct.addBitmaskDef(b);	
	b=new BitmaskDef("mgvi_bright", "Bright pixel flagged by MGVI processing", "l2_flags.MGVI_BRIGHT", new Color(51,217,217)/*(255, 204, 204)*/, (float)0.5);
	_outputProduct.addBitmaskDef(b);
	b=new BitmaskDef("mgvi_inval_rec", "Invalid rectification flagged by MGVI processing", "l2_flags.MGVI_INVAL_FAPAR", new Color(255,102,255)/*(153, 51, 0)*/, (float)0.5);
	_outputProduct.addBitmaskDef(b);
	
        // create and add the fapar band
        _faparBand = new Band(_outputBandName, ProductData.TYPE_UINT16, sceneWidth, sceneHeight);
        //_faparBand = new Band(_outputBandName, ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
        _outputProduct.addBand(_faparBand);
	   _faparBand.setScalingFactor(1.0/250.0);
	   _faparBand.setDescription("Fraction of Photosyntheticaly Absorbed radiation computed by the MGVI algorithm");
        	
        // create and add the blue reflectance band
      _reflectanceBlueBand = new Band("reflectance_TOA_2", ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
      _outputProduct.addBand(_reflectanceBlueBand);
	   _reflectanceBlueBand.setScalingFactor(1.0);
      _reflectanceBlueBand.setDescription("Top of atmosphere blue reflectance used in the MGVI algorithm");
	   _reflectanceBlueBand.setUnit("W/(m^2 sr^2)");
	   _reflectanceBlueBand.setSolarFlux(_blueInputBand.getSolarFlux());
	   _reflectanceBlueBand.setSpectralBandwidth(_blueInputBand.getSpectralBandwidth());
	   _reflectanceBlueBand.setSpectralWavelength(_blueInputBand.getSpectralWavelength());
	   _reflectanceBlueBand.setSpectralBandIndex(_blueInputBand.getSpectralBandIndex());
	
        // create and add the green reflectance band
	if (_greenInputBand != null)
	{   
            	// MC 22.11.07 - debug - System.out.println (sceneWidth);
            	// MC 22.11.07 - debug - System.out.println (sceneHeight);
		_reflectanceGreenBand = new Band("reflectance_TOA_5", ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
		_outputProduct.addBand(_reflectanceGreenBand);
		_reflectanceGreenBand.setScalingFactor(1.0);
		_reflectanceGreenBand.setDescription("Top of atmosphere green reflectance");
		_reflectanceGreenBand.setUnit("W/(m^2 sr^2)");
		_reflectanceGreenBand.setSolarFlux(_greenInputBand.getSolarFlux());
		_reflectanceGreenBand.setSpectralBandwidth(_greenInputBand.getSpectralBandwidth());
		_reflectanceGreenBand.setSpectralWavelength(_greenInputBand.getSpectralWavelength());
		_reflectanceGreenBand.setSpectralBandIndex(_greenInputBand.getSpectralBandIndex());
	}

        // create and add the red reflectance band
        _reflectanceRedBand = new Band("reflectance_TOA_8", ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
        _outputProduct.addBand(_reflectanceRedBand);
	_reflectanceRedBand.setScalingFactor(1.0);
	_reflectanceRedBand.setDescription("Top of atmosphere red reflectance used in the MGVI algorithm");
	_reflectanceRedBand.setUnit("W/(m^2 sr^2)");
	_reflectanceRedBand.setSolarFlux(_redInputBand.getSolarFlux());
	_reflectanceRedBand.setSpectralBandwidth(_redInputBand.getSpectralBandwidth());
	_reflectanceRedBand.setSpectralWavelength(_redInputBand.getSpectralWavelength());
	_reflectanceRedBand.setSpectralBandIndex(_redInputBand.getSpectralBandIndex());
	
        // create and add the nir reflectance band
        _reflectanceNirBand = new Band("reflectance_TOA_13", ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
        _outputProduct.addBand(_reflectanceNirBand);
	_reflectanceNirBand.setScalingFactor(1.0);
	_reflectanceNirBand.setDescription("Top of atmosphere near infrared reflectance used in the MGVI algorithm");
	_reflectanceNirBand.setUnit("W/(m^2 sr^2)");
	_reflectanceNirBand.setSolarFlux(_nirInputBand.getSolarFlux());
	_reflectanceNirBand.setSpectralBandwidth(_nirInputBand.getSpectralBandwidth());
	_reflectanceNirBand.setSpectralWavelength(_nirInputBand.getSpectralWavelength());
	_reflectanceNirBand.setSpectralBandIndex(_nirInputBand.getSpectralBandIndex());
	
	// create and add the nir rec band
        _nirRecBand = new Band("rectified_reflectance_13", ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
        _outputProduct.addBand(_nirRecBand);
	_nirRecBand.setScalingFactor(1.0);
	_nirRecBand.setDescription("Angular and atmospheric corrected near infrared reflectance");
	_nirRecBand.setUnit("unitless");
	_nirRecBand.setSolarFlux(_nirInputBand.getSolarFlux());
	_nirRecBand.setSpectralBandwidth(_nirInputBand.getSpectralBandwidth());
	_nirRecBand.setSpectralWavelength(_nirInputBand.getSpectralWavelength());
	_nirRecBand.setSpectralBandIndex(_nirInputBand.getSpectralBandIndex());

        // create and add the red rec band
        _redRecBand = new Band("rectified_reflectance_8", ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
        _outputProduct.addBand(_redRecBand);
	_redRecBand.setScalingFactor(1.0);
	_redRecBand.setDescription("Angular and atmospheric corrected red reflectance");
	_redRecBand.setUnit("unitless");
	_redRecBand.setSolarFlux(_redInputBand.getSolarFlux());
	_redRecBand.setSpectralBandwidth(_redInputBand.getSpectralBandwidth());
	_redRecBand.setSpectralWavelength(_redInputBand.getSpectralWavelength());
	_redRecBand.setSpectralBandIndex(_redInputBand.getSpectralBandIndex());
	
	// create and add the flags band
        _flag = new Band("l2_flags", ProductData.TYPE_UINT32, sceneWidth, sceneHeight);
        _outputProduct.addBand(_flag);
	_flag.setFlagCoding(_outputProduct.getFlagCoding("l2_flags"));
	_flag.setScalingFactor(1.0);
	_flag.setDescription("Classification and quality flags");
	 
         // MC ++
        // create and add the corrected latitude
        if (_AmorgosData == true) {
            _corrlongitude = new Band("corr_longitude", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
            _outputProduct.addBand(_corrlongitude);
    	     _corrlongitude.setScalingFactor(9.999999974752427e-7);
            _corrlongitude.setDescription("Orthocorrected Longitude");
    	     _corrlongitude.setUnit("deg");
       
             _corrlatitude = new Band("corr_latitude", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
             _outputProduct.addBand(_corrlatitude);
    	      _corrlatitude.setScalingFactor(9.999999974752427e-7);
             _corrlatitude.setDescription("Orthocorrected Latitude");
    	      _corrlatitude.setUnit("deg");
         }

          // MC --

   
	// copy the lat/lon tiepoints to the output product
        // ------------------------------------------------
        copyGeolocationToOutput();
	
        // copy the angles tiepoints to the output product
        // ------------------------------------------------
        copyAnglesToOutput();
        
	// copy the metadata to the output product
        // ------------------------------------------------
        copyMetadataToOutput();
        
	// retrieve the default disk writer from the ProductIO package
        // this is the BEAM_DIMAP format, the toolbox native file format
        // and attach to the writer to the output product
        // --------------------------------------------------------------
        ProductWriter writer;

	writer=ProcessorUtils.createProductWriter(outputRef);
        //writer = ProductIO.getProductWriter(ProductIO.DEFAULT_FORMAT_NAME);
        _outputProduct.setProductWriter(writer);

        // and initialize the disk representation
        // -------------------------------------
        writer.writeProductNodes(_outputProduct, new File(outputRef.getFilePath()));

        _logger.info("Created output product");

    }

    /**
     * Copies the tiepoint grids for latitude and longitude and the geocoding information from the input product to the
     * output product.
     */
    private void copyGeolocationToOutput() throws ProcessorException{

	TiePointGrid latTiePoint=null;
	TiePointGrid lonTiePoint=null;
	    
        // get the "latitude" tie point grid from the input product,
        // create a new tiepoint grid by duplicating the one received
        // and attach the new one to the output product
        // --------------------------------------------------------
        TiePointGrid srcTiePoint = _inputProduct.getTiePointGrid(_latTiePointName);
	//if (srcTiePoint==null) throw new ProcessorException("Cannot load Tie Point grid "+_latTiePointName);
        if (srcTiePoint!=null)
	{
		latTiePoint = new TiePointGrid(srcTiePoint.getName(),
				srcTiePoint.getRasterWidth(),
				srcTiePoint.getRasterHeight(),
				srcTiePoint.getOffsetX(),
				srcTiePoint.getOffsetY(),
				srcTiePoint.getSubSamplingX(),
				srcTiePoint.getSubSamplingY(),
				srcTiePoint.getTiePoints());
		latTiePoint.setDescription(srcTiePoint.getDescription());
		_outputProduct.addTiePointGrid(latTiePoint);
	}
	
        // do the same for the longitude tiepoints
        // ---------------------------------------
        srcTiePoint = _inputProduct.getTiePointGrid(_lonTiePointName);
	//if (srcTiePoint==null) throw new ProcessorException("Cannot load Tie Point grid "+_lonTiePointName);
	if (srcTiePoint!=null)
	{
		lonTiePoint = new TiePointGrid(srcTiePoint.getName(),
				srcTiePoint.getRasterWidth(),
				srcTiePoint.getRasterHeight(),
				srcTiePoint.getOffsetX(),
				srcTiePoint.getOffsetY(),
				srcTiePoint.getSubSamplingX(),
				srcTiePoint.getSubSamplingY(),
				srcTiePoint.getTiePoints());
		lonTiePoint.setDescription(srcTiePoint.getDescription());
		_outputProduct.addTiePointGrid(lonTiePoint);
	}
	
        // copy the geocoding from input to output. The geocoding
        // tells the product which tiepoint grids define the geolocation
        // -------------------------------------------------------------
        if (latTiePoint != null && lonTiePoint != null) {
            _outputProduct.setGeoCoding(new TiePointGeoCoding(latTiePoint, lonTiePoint));
        }
    }
   
    /**
     * Copies the tiepoint grids for the angles information from the input product to the
     * output product.
     * The Angle attibute Bands must be inizialized.
     */
    private void copyAnglesToOutput() throws ProcessorException{

	if (_szaBand==null || _saaBand==null || _vzaBand==null || _vaaBand==null )
		throw new ProcessorException("Can not copy Angles from input product to output product: attributes not initialized");
        // get the "sun_zenith_angle" tie point grid from the input product,
        // create a new tiepoint grid by duplicating the one received
        // and attach the new one to the output product
        // --------------------------------------------------------
        TiePointGrid sunZenTiePoint = new TiePointGrid(_szaBand.getName(),
                                                    _szaBand.getRasterWidth(),
                                                    _szaBand.getRasterHeight(),
                                                    _szaBand.getOffsetX(),
                                                    _szaBand.getOffsetY(),
                                                    _szaBand.getSubSamplingX(),
                                                    _szaBand.getSubSamplingY(),
                                                    _szaBand.getTiePoints());
	sunZenTiePoint.setDescription(_szaBand.getDescription());
        _outputProduct.addTiePointGrid(sunZenTiePoint);
        
	// do the same for the sun azimuth tiepoints
        // ---------------------------------------
        TiePointGrid sunAziTiePoint = new TiePointGrid(_saaBand.getName(),
                                                    _saaBand.getRasterWidth(),
                                                    _saaBand.getRasterHeight(),
                                                    _saaBand.getOffsetX(),
                                                    _saaBand.getOffsetY(),
                                                    _saaBand.getSubSamplingX(),
                                                    _saaBand.getSubSamplingY(),
                                                    _saaBand.getTiePoints());
	sunAziTiePoint.setDescription(_saaBand.getDescription());
        _outputProduct.addTiePointGrid(sunAziTiePoint);
	
        // do the same for the view zenith tiepoints
        // ---------------------------------------
        TiePointGrid vwZenTiePoint = new TiePointGrid(_vzaBand.getName(),
                                                    _vzaBand.getRasterWidth(),
                                                    _vzaBand.getRasterHeight(),
                                                    _vzaBand.getOffsetX(),
                                                    _vzaBand.getOffsetY(),
                                                    _vzaBand.getSubSamplingX(),
                                                    _vzaBand.getSubSamplingY(),
                                                    _vzaBand.getTiePoints());
	vwZenTiePoint.setDescription(_vzaBand.getDescription());
        _outputProduct.addTiePointGrid(vwZenTiePoint);

        // do the same for the view azimuth tiepoints
        // ---------------------------------------
        TiePointGrid vwAziTiePoint = new TiePointGrid(_vaaBand.getName(),
                                                    _vaaBand.getRasterWidth(),
                                                    _vaaBand.getRasterHeight(),
                                                    _vaaBand.getOffsetX(),
                                                    _vaaBand.getOffsetY(),
                                                    _vaaBand.getSubSamplingX(),
                                                    _vaaBand.getSubSamplingY(),
                                                    _vaaBand.getTiePoints());
	vwAziTiePoint.setDescription(_vaaBand.getDescription());
        _outputProduct.addTiePointGrid(vwAziTiePoint);

    }
    
    /**
     * Copies the metadata from the input product to the
     * output product.
     */
    private void copyMetadataToOutput() {
	    MetadataElement md_input_root=_inputProduct.getMetadataRoot();
	    MetadataElement md_scaling_factor=md_input_root.getElement("Scaling_Factor_GADS");
	    if (md_scaling_factor!=null)
	    {
		    MetadataElement md_output_root=_outputProduct.getMetadataRoot();
		    md_output_root.addElement(md_scaling_factor.createDeepClone());
	    }
    }
    
    /**
     * Performs the actual processing of the output product. Reads input bands line by line, calculates the fapar
     * and writes the result to the output bands
     */
    private void processFapar(ProgressMonitor pm) throws IOException {
	    
	    // first of all - allocate memory for a single scan line
	    // -----------------------------------------------------
	    int width = _inputProduct.getSceneRasterWidth();
	    int height = _inputProduct.getSceneRasterHeight();
	    
	    // Reflectances
	    float[] blue_reflectance=new float[width];
	    float[] red_reflectance=new float[width];
	    float[] nir_reflectance=new float[width];
	    float[] green_reflectance=new float[width*height];

       // MC ++
	    float[] corrlatitude=new float[width];
	    float[] corrlongitude=new float[width];
       // MC --

	    //float green_sun_flux;

	    // Flags
	    int[]   process = new int[width];
	    //int[][] flg=new int[height][width];	// flg is initialize only once (not line by line)
	    int[] flg=new int[height*width];
	    if (_inFlag!=null)			// only if there was flags in the input product
		    //for (int h=0;h<height;h++)
			//    _inFlag.readPixels(0,h,width, 1, flg[h]);
		_inFlag.readPixels(0,0,width, height, flg, ProgressMonitor.NULL);
	
	    
	    // variable used to read the radiance
	    float[] radiance=new float[width];
	    float[] green_radiance=new float[width*height];
	    float[] green_sza=new float[width*height];
	    
	    // Angles
	    float[] sza=new float[width];
	    float[] saa=new float[width];
	    float[] vza=new float[width];
	    float[] vaa=new float[width];  
	    
	    // progress bar init
	    // -----------------
//	    fireProcessStarted("Processing FAPAR.", 0, height);
	   
	    // Begin the computation
	    // ---------------------
	    
	    // Get the solar fux for each Band
	    _sun_spec[_blue] = _blueInputBand.getSolarFlux();
	    _sun_spec[_red] = _redInputBand.getSolarFlux();
	    _sun_spec[_nir] = _nirInputBand.getSolarFlux();
	    
	    // Get the green radiance
	    if (_greenInputBand != null)
	    {
		    //green_sun_flux = _greenInputBand.getSolarFlux();
		    _szaBand.readPixels(0, 0, width, height, green_sza, ProgressMonitor.NULL);
		    green_radiance = _greenInputBand.readPixels(0,0,width,height,green_radiance, ProgressMonitor.NULL);
		    green_reflectance=RsMathUtils.radianceToReflectance(green_radiance, green_sza, _greenInputBand.getSolarFlux(), null);
            	    // MC 22.11.07 - debug - System.out.println (width);
            	    // MC 22.11.07 - debug - System.out.println (height);
		    _reflectanceGreenBand.writePixels(0, 0, width, height, green_reflectance, ProgressMonitor.NULL);
            	    // MC 22.11.07 - debug - _logger.info("coucou 2");
            	    green_radiance=null;
		    green_reflectance=null;
		    
	    }
	    
	    // Loop over every line
	    // --------------------
pm.beginTask("Processing FAPAR...", height-1);
try {
	    for (int y=0; y<height; y++)
	    {	    
		    // Read the angles values for the line
		    _szaBand.readPixels(0, y, width, 1, sza, ProgressMonitor.NULL);
		    _saaBand.readPixels(0, y, width, 1, saa, ProgressMonitor.NULL);
		    _vzaBand.readPixels(0, y, width, 1, vza, ProgressMonitor.NULL);
		    _vaaBand.readPixels(0, y, width, 1, vaa, ProgressMonitor.NULL);
		    
		    // Get the blue radiance
		    // if exception is thrown, it is transfered
		    // all reflectances may contain valules <0 or >1
		    radiance = _blueInputBand.readPixels(0,y,width,1,radiance, ProgressMonitor.NULL);	
		    blue_reflectance=RsMathUtils.radianceToReflectance(radiance, sza, _blueInputBand.getSolarFlux(), null);
		    
		    // Get the red radiance
		    radiance = _redInputBand.readPixels(0,y,width,1,radiance, ProgressMonitor.NULL);
		    red_reflectance=RsMathUtils.radianceToReflectance(radiance, sza, _redInputBand.getSolarFlux(), null);
		    
		    // Get the nir radiance
		    radiance = _nirInputBand.readPixels(0,y,width,1,radiance, ProgressMonitor.NULL);
		    nir_reflectance=RsMathUtils.radianceToReflectance(radiance, sza, _nirInputBand.getSolarFlux(), null);

          // MC ++
                    if (_AmorgosData == true) {
		        // Get the corr Latitude
		        corrlatitude = _corrlatitudeInputBand.readPixels(0,y,width,1,corrlatitude, ProgressMonitor.NULL);
		        // Get the corr Longitude
		        corrlongitude = _corrlongitudeInputBand.readPixels(0,y,width,1,corrlongitude, ProgressMonitor.NULL);
                    }
          // MC --

		    // Initialize the flag values for each pixel of the line
		    // 1=Bad, 2=Cloud,snow,ice, 3=water, deep shadow, 4=bright surface
		    for (int i=0;i<width;i++)
		    {
			    if (blue_reflectance[i]<=0 || red_reflectance[i]<=0 ||  nir_reflectance[i]<=0)
				    process[i]=1;
			    else if (blue_reflectance[i]>=0.3 || red_reflectance[i]>=0.5 || nir_reflectance[i]>=0.7)
				    process[i]=2;
			    else if (blue_reflectance[i]>nir_reflectance[i])
				    process[i]=3;
			    else if (nir_reflectance[i]<=1.25*red_reflectance[i])
				    process[i]=4;
			    else process[i]=0;
		    }
		    
		    // Compute the fapar calling the algorithm
		    // ---------------------------------------
		    float[] fapar=_algorithm.run(sza, saa, vza, vaa, blue_reflectance, red_reflectance, nir_reflectance, process);
		    
		    // Variable used to write the data in type INT
		    int[] fapar_i=new int[width];
		                         
		    
		    // Check the flags and modify the wrong values of Fapar
		    // Mapp the values between 0 and 250 + 251-255 for the flags
		    // ---------------------------------------------------------
		    for (int i=0;i<width;i++)
		    {
			    if (process[i]==0)	
			    {
				    fapar_i[i]=Math.round(fapar[i]*250);
				    //fapar[i]*=250;
			    }	
			    else
			    {
				    fapar_i[i]=process[i]+250;
				    //fapar[i]=process[i]+250;
				    flg[i+y*width]+=Math.pow(2,process[i]-1)*256;
			    }
		    }
		   
		    // Write data
		    // ----------
		    
		    // Write the fapar values to the output fapar band
		    // The values have already been mapped between 0 and 250 so the scaling factor must be 1 for the writting
		    _faparBand.setScalingFactor(1.0);
		    _faparBand.writePixels(0, y, width, 1, fapar_i, ProgressMonitor.NULL);
		    //_faparBand.writePixels(0, y, width, 1, fapar);
		    _faparBand.setScalingFactor(1.0/250.0);
		    
		    // Write the rectified values to the output bands
		    _redRecBand.writePixels(0, y, width, 1, _algorithm.getRedRec(), ProgressMonitor.NULL);
		    _nirRecBand.writePixels(0, y, width, 1, _algorithm.getNirRec(), ProgressMonitor.NULL);
		    
		    //Write all the other bands
		    _reflectanceBlueBand.writePixels(0, y, width, 1, blue_reflectance, ProgressMonitor.NULL);
		    _reflectanceRedBand.writePixels(0, y, width, 1, red_reflectance, ProgressMonitor.NULL);
		    _reflectanceNirBand.writePixels(0, y, width, 1, nir_reflectance, ProgressMonitor.NULL);

		    // MC ++
                    if (_AmorgosData == true) {
		        _corrlatitude.writePixels(0, y, width, 1, corrlatitude, ProgressMonitor.NULL);
		        _corrlongitude.writePixels(0, y, width, 1, corrlongitude, ProgressMonitor.NULL);
                    }
		    // MC --

		    // Notify process listeners about processing progress and
		    // check whether or not processing shall be terminated
		    pm.worked(1);
		    if (pm.isCanceled()) {
			    // Processing terminated!
			    // --> Completely remove output product
			    _outputProduct.getProductWriter().deleteOutput();
			    // Immediately terminate now
			    _logger.info(ProcessorConstants.LOG_MSG_PROC_CANCELED);
			    _logger.info("The output product is completely removed.");
			    setCurrentStatus(ProcessorConstants.STATUS_ABORTED);
			    return;
		    }
		    
		    // update progressbar
		    // ------------------
//		    if (!fireProcessInProgress(y)) {
//			    _logger.warning(ProcessorConstants.LOG_MSG_PROC_CANCELED);
//			    setCurrentStatus(ProcessorConstants.STATUS_ABORTED);
//			    return;
//		    }
//
//		    // check aborted
//		    // -------------
//		    if (isAborted()){
//			    _logger.warning(ProcessorConstants.LOG_MSG_PROC_ABORTED);
//			    fireProcessEnded(false);
//			    return;
//		    }
	    }
	} finally {
		pm.done();
	}
	    
	    _flag.writePixels(0, 0, width, height, flg, ProgressMonitor.NULL);
	    _logger.info(ProcessorConstants.LOG_MSG_PROC_SUCCESS);
	    
	    // finish
	    // ------
//	    fireProcessEnded(true);
	    
    }
}
