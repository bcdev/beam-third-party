/*
 * $Id: FaparProcessorUI.java,v 1.4 2007/12/11 15:56:31 andreio Exp $
 * Written by: Ophelie Aussedat, September, 2004
 * 
 * Copyright (C) 2004 by STARS
 * 
 *       Academic users:
 *       Are authorized to use this code for research and teaching,
 *       but must acknowledge the use of these routines explicitly
 *       and refer to the references in any publication or work.
 *       Original, complete, unmodified versions of these codes may be
 *       distributed free of charge to colleagues involved in similar
 *       activities.  Recipients must also agree with and abide by the
 *       same rules. The code may not be sold, nor distributed to
 *       commercial parties, under any circumstances.
 * 
 *       Commercial and other users:
 *       Use of this code in commercial applications is strictly
 *       forbidden without the written approval of the authors.
 *       Even with such authorization the code may not be distributed
 *       or sold to any other commercial or business partners under
 *       any circumstances.
 * 
 *  This software is provided as is without any warranty whatsoever.
 * 
 *  REFERENCES:
 *  [1] Gobron, N., Pinty, B., Aussedat, O., Taberner, M., Faber, O., Mélin, F., 
 *  Lavergne, T., Robustelli, M., Snoeij, P. (2008)
 *  Uncertainty Estimates for the FAPAR Operational Products Derived from MERIS - 
 *  Impact of Top-of-Atmosphere Radiance Uncertainties and Validation with Field Data.
 *  Remote Sensing of Environment, 112(4):1871–1883.
 *  Special issue: Remote Sensing Data Assimilation. Edited by Loew, A.
 *  DOI: 10.1016/j.rse.2007.09.011
 *
 *  [2] Gobron, N., Mélin, F., Pinty, B., Taberner, M., Verstraete, M. M. (2004)
 *  MERIS Global Vegetation Index: Evaluation and Performance.
 *  In: Proceedings of the MERIS User Workshop. 10-14 November 2003, Frascati, Italy, 
 *  volume 549 of ESA Special Publication, European Space Agency.
 *  Online: http://envisat.esa.int/workshops/meris03/participants/48/paper_23_gobron.pdf
 *
 *  [3] Gobron, N., Aussedat, O., Pinty, B., Taberner, M., Verstraete, M. M. (2004)
 *  Medium Resolution Imaging Spectrometer (MERIS) - Level 2 Land Surface Products - 
 *  Algorithm Theoretical Basis Document.
 *  EUR Report 21387 EN, European Commission - DG Joint Research Centre, Institute for 
 *  Environment and Sustainability, 20 pages.
 *  Available at: http://fapar.jrc.ec.europa.eu/pubs/?pubid=2004.eur-report.21387&format=html
 *
 *  [4] Gobron, N., Taberner, M., Pinty, B., Mélin, F., Verstraete, M. M., Widlowski, J.-L. (2003)
 *  Evaluation of the MERIS Global Vegetation Index: Methodology and Initial Results.
 *  In: Proceedings of the Working Meeting on the MERIS and ATSR Calibration and Geophysical 
 *  Validation. 20-23 October 2003, Frascati, Italy, volume 541 of ESA Special Publication, 
 *  European Space Agency.
 *  Online: http://envisat.esa.int/workshops/mavt_2003/MAVT-2003_504-paper_NGobron.pdf
 */
package it.jrc.beam.fapar;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.param.ParamChangeEvent;
import org.esa.beam.framework.param.ParamChangeListener;
import org.esa.beam.framework.param.ParamGroup;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.Request;
import org.esa.beam.framework.processor.ui.ProcessorApp;
import org.esa.beam.framework.processor.ui.ProcessorUI;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.util.Debug;
import org.esa.beam.util.Guardian;
import org.esa.beam.framework.processor.ProcessorUtils;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * The <code>FaparProcessorUI</code> implements the graphical user interface for the FAPAR processor.
 */
public class FaparProcessorUI implements ProcessorUI {

    private JTabbedPane _tabbedPane;
    private ParamGroup _paramGroup;
    private File _requestFile;
    private FaparRequestElementFactory _factory;
    private ProcessorApp _app;

    /**
     * Creates the ui class with default parameters
     */
    public FaparProcessorUI() {
        _factory = FaparRequestElementFactory.getInstance();
    }

    /**
     * Retrieves the base component for the processor specific user interface classes. This can be any Java Swing
     * containertype. This method creates the UI from scratch if not present.
     */
    public JComponent getGuiComponent() {
        if (_tabbedPane == null) {
            createUI();
        }
        return _tabbedPane;
    }

    /**
     * Gets the request containing the user's modifications.
     *
     * @return a <code>Request</code> object with all elements required by the FAPAR processor
     */
    public Vector getRequests() throws ProcessorException {
        Vector requests = new Vector();
        requests.add(createRequest());
        return requests;
    }

    /**
     * Sets a new Request list to be edited.
     *
     * @param requests the request list to be edited must not be <code>null</code>.
     */
    public void setRequests(Vector requests) throws ProcessorException {
        Guardian.assertNotNull("requests", requests);
        if (requests.size() > 0) {
            Request request = (Request) requests.elementAt(0);
            _requestFile = request.getFile();
            updateParamInputFile(request);
            updateParamOutputFile(request);
            updateParamOutputFormat(request);
        } else {
            setDefaultRequests();
        }
    }

    /**
     * Create a new default request for the FAPAR processor and sets it to the UI
     */
    public void setDefaultRequests() throws ProcessorException {
        Vector requests = new Vector();
        requests.add(createDefaultRequest());
        setRequests(requests);
    }

    /**
     * Sets the processor app for the UI
     */
    public void setApp(ProcessorApp app) {
        _app = app;
    }

    /**
     * Gets the processor app for the UI
     */
    private ProcessorApp getApp() {
        return _app;
    }


    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a request with all paramneters set to their respective default values.
     *
     * @return the default request
     */
    private Request createDefaultRequest() {
        _paramGroup.getParameter(ProcessorConstants.INPUT_PRODUCT_PARAM_NAME).setDefaultValue();
        _paramGroup.getParameter(ProcessorConstants.OUTPUT_PRODUCT_PARAM_NAME).setDefaultValue();
        _paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).setDefaultValue();
        return createRequest();
    }

    /**
     * Creates all user interface components of the FAPAR user interface
     */
    private void createUI() {
        initParamGroup();
        _tabbedPane = new JTabbedPane();
        _tabbedPane.add("I/O Parameters", createPathTab());
    }

    /**
     * Initializes the parameter group to hold all parameters needed for the processor.
     */
    private void initParamGroup() {
        _paramGroup = new ParamGroup();
	// Value when the processor is open
        final Parameter inputProductParameter = _factory.createDefaultInputProductParameter();
        _paramGroup.addParameter(inputProductParameter);
        _paramGroup.addParameter(_factory.createDefaultOutputProductParameter());
        _paramGroup.addParameter(_factory.createOutputFormatParameter());

	// Add a listener and an action to perform if any change is made to the dialog box
        inputProductParameter.addParamChangeListener(new ParamChangeListener() {
            public void parameterValueChanged(ParamChangeEvent event) {
                checkForValidInputProduct(inputProductParameter);
            }
        });
        if (_paramGroup != null) {
            getApp().markIODirChanges(_paramGroup);
        }

    }

    /**
     * Create the ui tab for the path editing.
     *
     * @return the panel containing the paths tab
     */
    private JPanel createPathTab() {
        JPanel panel = GridBagUtils.createDefaultEmptyBorderPanel();
        final GridBagConstraints gbc = GridBagUtils.createConstraints(null);
        gbc.gridy = 0;
        Parameter param;

        // input product
        // -------------
        param = _paramGroup.getParameter(ProcessorConstants.INPUT_PRODUCT_PARAM_NAME);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "anchor=SOUTHWEST, weighty=1");
        GridBagUtils.addToPanel(panel, param.getEditor().getLabelComponent(), gbc);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "anchor=NORTHWEST, fill=HORIZONTAL, weightx=1, weighty=1");
        GridBagUtils.addToPanel(panel, param.getEditor().getComponent(), gbc);

        // output product
        // --------------
        param = _paramGroup.getParameter(ProcessorConstants.OUTPUT_PRODUCT_PARAM_NAME);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=7,anchor=SOUTHWEST, weighty=0.5");
        GridBagUtils.addToPanel(panel, param.getEditor().getLabelComponent(), gbc);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=0,anchor=NORTHWEST, weighty=1");
        GridBagUtils.addToPanel(panel, param.getEditor().getComponent(), gbc);

        // output format
        // -------------
        param = _paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=7, anchor=SOUTHWEST, fill=NONE, weightx = 0, weighty=0.5");
        GridBagUtils.addToPanel(panel, param.getEditor().getLabelComponent(), gbc);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=0, anchor=NORTHWEST, weighty=0.5");
        GridBagUtils.addToPanel(panel, param.getEditor().getComponent(), gbc);

        return panel;
    }

    private Request createRequest() {
        Request request = new Request();
        request.setType(FaparProcessor.REQUEST_TYPE);
        request.setFile(_requestFile);
        request.addInputProduct(createInputProductRef());
        request.addOutputProduct(createOutputProductRef());
        request.addParameter(createOutputFormatParamForRequest());
        return request;
    }

    /**
     * Creates an input product reference from the request.
     */
    private ProductRef createInputProductRef() {
        try {
            final String filePath = _paramGroup.getParameter(ProcessorConstants.INPUT_PRODUCT_PARAM_NAME).getValueAsText();
            File ProductFile = new File(filePath);
            return new ProductRef(ProductFile, null, null);
        } catch (NullPointerException e) {
            Debug.trace(e);
        }
        return null;
    }

    /**
     * Creates an output product reference from the request.
     */
    private ProductRef createOutputProductRef() {
        final String fileName = _paramGroup.getParameter(ProcessorConstants.OUTPUT_PRODUCT_PARAM_NAME).getValueAsText();
        final String fileFormat = _paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).getValueAsText();

        return ProcessorUtils.createProductRef(fileName, fileFormat);
    }

    private Parameter createOutputFormatParamForRequest() {
        String outputFormat = _paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).getValueAsText();
        return new Parameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME, outputFormat);
    }

    private void updateParamOutputFormat(Request request) {
        String format = request.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).getValueAsText();
        _paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).setValue(format, null);
    }

    private void updateParamOutputFile(Request request) {
        File file = new File(request.getOutputProductAt(0).getFilePath());
        _paramGroup.getParameter(ProcessorConstants.OUTPUT_PRODUCT_PARAM_NAME).setValue(file, null);
    }

    private void updateParamInputFile(Request request) {
        File file = new File(request.getInputProductAt(0).getFilePath());
        _paramGroup.getParameter(ProcessorConstants.INPUT_PRODUCT_PARAM_NAME).setValue(file, null);
    }

    /**
     * Brings up a message box if the input product is not valid. Valid input products are: products wich contains at
     * least the bands named '{@link it.jrc.beam.fapar.FaparProcessor#INPUT_BAND_NAME_RED redBandName}' and '{@link
     * it.jrc.beam.fapar.FaparProcessor#INPUT_BAND_NAME_BLUE blueBandName}' and '{@link it.jrc.beam.fapar.FaparProcessor#INPUT_BAND_NAME_NIR nirBandName}' and tie point grids named '{@link it.jrc.beam.fapar.FaparProcessor#.INPUT_TPG_NAME_SZA}' and '{@link it.jrc.beam.fapar.FaparProcessor#.INPUT_TPG_NAME_SAA}' and '{@link it.jrc.beam.fapar.FaparProcessor#.INPUT_TPG_NAME_VZA}' and '{@link it.jrc.beam.fapar.FaparProcessor#.INPUT_TPG_NAME_VAA}'.
     * The message box only comes up if the parameter contains an
     * existing file. So you can create requests with not existing input products without interfering message Box.
     *
     * @param parameter
     */
    private void checkForValidInputProduct(Parameter parameter) {
        Object value = parameter.getValue();
        File file = null;
        if (value instanceof File) {
            file = (File) value;
        }
        if (value instanceof String) {
            file = new File((String) value);
        }
        if (file == null || !file.exists()) {
            return;
        }
        String msg = null;
        try {
            Product product = ProductIO.readProduct(file, null);
            if (product != null) {
                final String blueBandName = FaparProcessor.INPUT_BAND_NAME_BLUE;
                final String redBandName = FaparProcessor.INPUT_BAND_NAME_RED;
                final String nirBandName = FaparProcessor.INPUT_BAND_NAME_NIR;
                if (product.getBand(blueBandName) == null || product.getBand(redBandName) == null || product.getBand(nirBandName) == null) {
                    msg = "The FAPAR Processor only works with products which contains\n" +
                          "at least the bands '" + blueBandName + "' and '" + redBandName + "' and '" + nirBandName + "'.";
                }
		final String szaTPGName = FaparProcessor.INPUT_TPG_NAME_SZA;
		final String vzaTPGName = FaparProcessor.INPUT_TPG_NAME_VZA;
		final String saaTPGName = FaparProcessor.INPUT_TPG_NAME_SAA;
		final String vaaTPGName = FaparProcessor.INPUT_TPG_NAME_VAA;
		if (product.getTiePointGrid(szaTPGName) == null || product.getTiePointGrid(vzaTPGName) == null || product.getTiePointGrid(saaTPGName) == null || product.getTiePointGrid(vaaTPGName) == null) 
		{
			msg = "The FAPAR Processor only works with products which contains\n" +
				"at least the tie point grids '" + szaTPGName + "' and '" + vzaTPGName + "' and '" + saaTPGName + "' and '" + vaaTPGName + "'.";
		} 
	    }else 
	    {
		    msg = "Unknown file format.";
	    }
	} catch (IOException e) {
		msg = e.getMessage();
	}
	if (msg != null) {
		JOptionPane.showMessageDialog(getGuiComponent(), "Invalid input file:\n" + msg,
				FaparProcessor.PROCESSOR_NAME, JOptionPane.ERROR_MESSAGE);
	}
    }
}
