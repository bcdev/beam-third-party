/*
 * $Id: BaerUi.java,v 1.9 2006/03/28 15:10:46 meris Exp $
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
 *//*
 * $Id: BaerUi.java,v 1.9 2006/03/28 15:10:46 meris Exp $
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
package org.esa.beam.processor.baer.ui;

import org.esa.beam.dataio.dimap.DimapProductConstants;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductIOPlugInManager;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.param.ParamChangeEvent;
import org.esa.beam.framework.param.ParamChangeListener;
import org.esa.beam.framework.param.ParamGroup;
import org.esa.beam.framework.param.ParamValidateException;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.param.editors.BooleanExpressionEditor;
import org.esa.beam.framework.processor.DefaultRequestElementFactory;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.Request;
import org.esa.beam.framework.processor.ui.AbstractProcessorUI;
import org.esa.beam.framework.processor.ui.ProcessorApp;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.processor.baer.BaerConstants;
import org.esa.beam.processor.baer.BaerRequestElementFactory;
import org.esa.beam.util.Debug;
import org.esa.beam.util.Guardian;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

public class BaerUi extends AbstractProcessorUI implements ParamChangeListener {

    private Logger _logger;

    private JComponent _uiComponent;
    private ParamGroup _paramGroup;

    private JComboBox _fileFormatCombo;
    private String[] _fileFormatNames;
    private String _outFileFormat;
    private static final String OUT_FORMAT_LABEL = "Output product format:";
     private File _requestFile;

    /**
     * Constructs the object with default parameters
     */
    public BaerUi() {
        _logger = Logger.getLogger(BaerConstants.LOGGER_NAME);

        createParameterGroup();
    }

    /**
     * Retrieves the base component for the processor specific user interface classes. This can be any Java Swing
     * containertype. This method creates the UI from scratch if not present
     */
    public JComponent getGuiComponent() {
        if (_uiComponent == null) {
            scanWriterFormatStrings();
            createUI();
            try {
                setDefaultRequests();
            } catch (ProcessorException e) {
                _logger.severe(e.getMessage());
            }
        }

        return _uiComponent;
    }

    /**
     * Retrieves the requests currently edited.
     */
    public Vector<Request> getRequests() throws ProcessorException {
        Request request = new Request();

        request.setType(BaerConstants.REQUEST_TYPE);
        request.setFile(_requestFile);
        getInputProduct(request);
        getOutputProduct(request);
        getParameter(request);

        Vector<Request> vRet = new Vector<Request>();
        vRet.add(request);
        return vRet;
    }

    /**
     * Sets a new Request list to be edited.
     *
     * @param requests the request list to be edited
     */
    public void setRequests(Vector requests) throws ProcessorException {
        Guardian.assertNotNull("requests", requests);
        if (requests.size() > 0) {
            Request request = (Request) requests.elementAt(0);
            _requestFile = request.getFile();

            setInputProduct(request);
            scanForBitmasksAndAssign();
            setOutputProduct(request);
            setParameter(request);
        }
    }

    /**
     * Create a new default request for the sets the values in the UI.
     */
    public void setDefaultRequests() throws ProcessorException {
        BaerRequestElementFactory factory = BaerRequestElementFactory.getInstance();
        Request request = new Request();

        request.setType(BaerConstants.REQUEST_TYPE);
        request.addInputProduct(createDefaultInputProduct());
        request.addOutputProduct(createDefaultOutputProduct());
        request.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.BITMASK_PARAM_NAME));
        request.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.SMAC_PARAM_NAME));
        request.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.USE_BAER_PARAM_NAME));
        request.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.USE_ATM_COR_PARAM_NAME));
        request.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.USE_CLOUD_PARAM_NAME));
        request.addParameter(factory.createDefaultLogPatternParameter(BaerConstants.DEFAULT_LOG_PREFIX));
        try {
            Parameter aerParam = factory.createParamWithDefaultValueSet(BaerConstants.AER_PHASE_PARAM_NAME);
            aerParam.setValue(aerParam.getProperties().getValueSet()[0]);
            request.addParameter(aerParam);

            request.addParameter(factory.createLogToOutputParameter("false"));
        } catch (ParamValidateException e) {
            _logger.severe(e.getMessage());
        }

        Vector<Request> vSet = new Vector<Request>();
        vSet.add(request);
        setRequests(vSet);
    }

    public void setApp(ProcessorApp processorApp) {
        // not used here
    }

    /**
     * Callback from the parameter group.
     *
     * @param event
     */
    public void parameterValueChanged(ParamChangeEvent event) {
        if (event.getParameter().getName().equalsIgnoreCase(DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME)) {
            scanForBitmasksAndAssign();
        }
    }

    /**
     * Sets the names of the aer_phase LUT's that are read from the aux file
     *
     * @param aerPhaseNames
     */
    public void setAerPhaseLUTNames(String[] aerPhaseNames) {
        Parameter param = _paramGroup.getParameter(BaerConstants.AER_PHASE_PARAM_NAME);

        param.getProperties().setValueSet(aerPhaseNames);

        final boolean valueIsSet = param.getValue() == null || param.getValueAsText().equals("");
        if (!valueIsSet && aerPhaseNames.length > 0) {
            param.setValue(aerPhaseNames[0], null);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates all user interface components and sets them to their appropriate default values
     */
    private void createUI() {
        int line = 0;
                
        JPanel ioParamPanel = GridBagUtils.createDefaultEmptyBorderPanel();
        GridBagConstraints gbc = GridBagUtils.createConstraints(null);

        // input product
        addParameterToPanel(ioParamPanel, DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME, line, 12, gbc);
        line += 2;

        // output product
        addParameterToPanel(ioParamPanel, DefaultRequestElementFactory.OUTPUT_PRODUCT_PARAM_NAME, line, 12, gbc);
        line += 10;

        final JLabel outFormatLabel = new JLabel(OUT_FORMAT_LABEL);
        _fileFormatCombo = new JComboBox(_fileFormatNames);
        _fileFormatCombo.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                updateOutFileType();
            }
        });
        GridBagUtils.setAttributes(gbc,
                                   "anchor=SOUTHWEST, fill=NONE, insets.top=12, weightx = 0, weighty=0.5, gridy=" + String.valueOf(line++));
        GridBagUtils.addToPanel(ioParamPanel, outFormatLabel, gbc);
        GridBagUtils.setAttributes(gbc, "anchor=NORTHWEST, weighty=0.5, insets.top=0 ,gridy=" + String.valueOf(line++));
        GridBagUtils.addToPanel(ioParamPanel, _fileFormatCombo, gbc);

        JPanel procParamPanel = GridBagUtils.createDefaultEmptyBorderPanel();
        gbc = GridBagUtils.createConstraints(null);

        addParameterToPanel2(procParamPanel, BaerConstants.USE_CLOUD_PARAM_NAME, line++, 12, false, gbc);
        addParameterToPanel2(procParamPanel, BaerConstants.USE_BAER_PARAM_NAME, line++, 2, false, gbc);
        addParameterToPanel2(procParamPanel, BaerConstants.USE_ATM_COR_PARAM_NAME, line++, 2, false, gbc);
        addParameterToPanel2(procParamPanel, BaerConstants.SMAC_PARAM_NAME, line++, 12, true, gbc);
        addParameterToPanel2(procParamPanel, BaerConstants.AER_PHASE_PARAM_NAME, line++, 2, true, gbc);
        addParameterToPanel2(procParamPanel, BaerConstants.BITMASK_PARAM_NAME, line++, 12, false, gbc);

        getParameter(BaerConstants.USE_CLOUD_PARAM_NAME).getEditor().setEnabled(false);
    
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("I/O Parameters", ioParamPanel);
        tabbedPane.addTab("Processing Parameters", procParamPanel);
        _uiComponent = tabbedPane; 
}

    /**
     * Scans the ProductIO for all product format strings of the registered writer plugins
     */
    private void scanWriterFormatStrings() {
        ProductIOPlugInManager manager = ProductIOPlugInManager.getInstance();
        _fileFormatNames = manager.getAllProductWriterFormatStrings();
    }

    /**
     * Creates the parameter group containing all parameters editd in this class.
     */
    private void createParameterGroup() {
        BaerRequestElementFactory factory = BaerRequestElementFactory.getInstance();

        try {
            _paramGroup = new ParamGroup();
            final Parameter inputProductParameter = factory.createDefaultInputProductParameter();
            inputProductParameter.addParamChangeListener(new ParamChangeListener() {
                public void parameterValueChanged(final ParamChangeEvent event) {
                    handleInputProductChanged();
                }
            });
            _paramGroup.addParameter(inputProductParameter);
            _paramGroup.addParameter(factory.createDefaultOutputProductParameter());
            _paramGroup.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.BITMASK_PARAM_NAME));
            _paramGroup.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.AER_PHASE_PARAM_NAME));
            _paramGroup.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.USE_BAER_PARAM_NAME));
            _paramGroup.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.USE_ATM_COR_PARAM_NAME));
            _paramGroup.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.USE_CLOUD_PARAM_NAME));
            _paramGroup.addParameter(factory.createParamWithDefaultValueSet(BaerConstants.SMAC_PARAM_NAME));
            _paramGroup.addParameter(factory.createDefaultLogPatternParameter(BaerConstants.DEFAULT_LOG_PREFIX));
            _paramGroup.addParameter(factory.createLogToOutputParameter("false"));
            _paramGroup.addParamChangeListener(createParamChangeListener());
        } catch (ParamValidateException e) {
            _logger.severe(e.getMessage());
        }
    }

    /**
     * Adds a parameter at the given position to tha panel supplied.
     *
     * @param panel  the panel
     * @param name   the parameter name
     * @param line   the line
     * @param insets the top insets
     * @param gbc    the constraints
     */
    private void addParameterToPanel(JPanel panel, String name, int line, int insets, GridBagConstraints gbc) {
        Parameter param = _paramGroup.getParameter(name);

        GridBagUtils.setAttributes(gbc,
                                   "anchor=SOUTHWEST, fill=HORIZONTAL, weightx=1, gridy=" + String.valueOf(line++));
        if (insets > 0) {
            GridBagUtils.setAttributes(gbc, "insets.top=" + String.valueOf(insets));
        }

        JComponent label = param.getEditor().getLabelComponent();
        if (label != null) {
            GridBagUtils.addToPanel(panel, label, gbc);
        }

        GridBagUtils.setAttributes(gbc, "anchor=NORTHWEST, insets.top=0, gridy=" + String.valueOf(line++));
        GridBagUtils.addToPanel(panel, param.getEditor().getEditorComponent(), gbc);
    }

    /**
     * Adds a parameter at the given position to tha panel supplied.
     *
     * @param panel  the panel
     * @param name   the parameter name
     * @param line   the line
     * @param insets the top insets
     * @param gbc    the constraints
     */
    private void addParameterToPanel2(JPanel panel, String name, int line, int insets, boolean nofill, GridBagConstraints gbc) {
        Parameter param = _paramGroup.getParameter(name);

        GridBagUtils.setAttributes(gbc,
                                   "anchor=WEST, fill=HORIZONTAL, gridy=" + line + ", insets.top=" + insets);

        JComponent label = param.getEditor().getLabelComponent();
        if (label != null) {
            gbc.gridwidth = nofill ? 2 : 1;
            gbc.weightx = nofill ? 1 : 0;
            gbc.gridx = 0;
            GridBagUtils.addToPanel(panel, label, gbc);
            gbc.gridwidth = nofill ? 1 : 2;
            gbc.weightx = nofill ? 0 : 1;
            gbc.gridx = nofill ? 2 : 1;
            GridBagUtils.addToPanel(panel, param.getEditor().getEditorComponent(), gbc);
        } else {
            gbc.gridwidth = 3;
            gbc.gridx = 0;
            gbc.weightx = 1;
            GridBagUtils.addToPanel(panel, param.getEditor().getEditorComponent(), gbc);
        }
    }

    /**
     * Callback for output file format combo box.
     */
    private void updateOutFileType() {
        _outFileFormat = (String) _fileFormatCombo.getSelectedItem();
    }

    /**
     * Scans the input product for flags and assigns these to the bitmask parameter
     */
    private void scanForBitmasksAndAssign() {
        Parameter param = _paramGroup.getParameter(DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME);
        File inputFile = (File) param.getValue();

        // check if file exists and is file
        if ((inputFile != null) && inputFile.exists() && inputFile.isFile()) {
            try {
                Product inProduct = ProductIO.readProduct(inputFile.toURI().toURL(), null);
                if (inProduct != null) {
                    String[] bitmaskFlags = inProduct.getAllFlagNames();

                    Parameter bitmask = _paramGroup.getParameter(BaerConstants.BITMASK_PARAM_NAME);
                    bitmask.getProperties().setValueSet(bitmaskFlags);
                }
            } catch (IOException e) {
                Debug.trace(e);
            }
        }
    }

    /**
     * Creates a <code>ProductRef</code> pointing to the BAER default input product.
     */
    private ProductRef createDefaultInputProduct() {
        Parameter inProdParam = BaerRequestElementFactory.getInstance().createDefaultInputProductParameter();
        ProductRef ref = null;
        File inProd = (File) inProdParam.getValue();

        ref = new ProductRef(inProd);
        
        return ref;
    }

    /**
     * Creates a <code>ProductRef</code> pointing to the BAER default output product.
     */
    private ProductRef createDefaultOutputProduct() {
        Parameter outProdParam = BaerRequestElementFactory.getInstance().createDefaultOutputProductParameter();
        File outProd = (File) outProdParam.getValue();
        ProductRef ref = null;

        ref = new ProductRef(outProd);
        
        return ref;
    }

    /**
     * Updates the input product parameter due to an update in the request.
     *
     * @param request the <code>Request</code> to be read
     */
    private void setInputProduct(Request request) {
        if (request.getNumInputProducts() > 0) {
            ProductRef prodRef = request.getInputProductAt(0);
            Parameter param = _paramGroup.getParameter(DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME);
            File file = new File(prodRef.getFilePath());
            param.setValue(file, null);
        }
    }

    private void handleInputProductChanged() {

        final File file = (File) _paramGroup.getParameter(DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME).getValue();
        Product inProduct = null;
        try {
            inProduct = ProductIO.readProduct(file, null);
        } catch (IOException e) {
            Debug.trace(e);
        }
        final Parameter parameter = _paramGroup.getParameter(BaerConstants.BITMASK_PARAM_NAME);
        parameter.getProperties().setPropertyValue(BooleanExpressionEditor.PROPERTY_KEY_SELECTED_PRODUCT,
                                                   inProduct);
    }


    /**
     * Sets the output file parameter to the value stored in the request. Updates the file format combo box with the
     * correct value.
     *
     * @param request the <code>Request</code> to be read
     */
    private void setOutputProduct(Request request) {
        if (request.getNumOutputProducts() > 0) {
            ProductRef outputProduct = request.getOutputProductAt(0);
            Parameter param = _paramGroup.getParameter(DefaultRequestElementFactory.OUTPUT_PRODUCT_PARAM_NAME);
            File file = new File(outputProduct.getFilePath());
            param.setValue(file, null);

            _outFileFormat = outputProduct.getFileFormat();
            if (_outFileFormat != null) {
                _fileFormatCombo.setSelectedItem(_outFileFormat);
            } else {
                // set default format - matbx-dimap
                _outFileFormat = DimapProductConstants.DIMAP_FORMAT_NAME;
            }
        }
    }

    private ParamChangeListener createParamChangeListener() {
        return new ParamChangeListener() {

            public void parameterValueChanged(ParamChangeEvent e) {
                parameterChanged(e);
            }
        };
    }


          /**
     * Callback invoked on changes in a parameter
     *
     * @param e the event that triggered the callback
     */
    private void parameterChanged(ParamChangeEvent e) {
        Parameter param = e.getParameter();
        String paramName = param.getName();

        if (BaerConstants.USE_BAER_PARAM_NAME.equals(paramName)) {
            handleParameterChangedEventUseBaer(param);
        } else if (BaerConstants.USE_ATM_COR_PARAM_NAME.equals(paramName)) {
            handleParameterChangedEventUseAtmCorr(param);
        } else {
            final File file = (File) _paramGroup.getParameter(DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME).getValue();
            if (file != null && file.exists()) {
                Product inProduct = null;
                try {
                    inProduct = ProductIO.readProduct(file, null);
                } catch (IOException i) {
                    Debug.trace(i);
                }
                final Parameter parameter = _paramGroup.getParameter(BaerConstants.BITMASK_PARAM_NAME);
                parameter.getProperties().setPropertyValue(BooleanExpressionEditor.PROPERTY_KEY_SELECTED_PRODUCT,
                                                   inProduct);
            }
        }
    }

     private void handleParameterChangedEventUseBaer(Parameter param) {
        boolean enable = ((Boolean)param.getValue()).booleanValue();
         boolean enableatm;
         Parameter toUpdate;
         // process format
         Parameter newParam = getParameter(BaerConstants.USE_BAER_PARAM_NAME);
           if (newParam != null) {
              toUpdate = getParameter(BaerConstants.USE_ATM_COR_PARAM_NAME);

              toUpdate.setValue(newParam.getValue(), null);
           }
         toUpdate = getParameter(BaerConstants.USE_ATM_COR_PARAM_NAME);
         enableatm =  ((Boolean)toUpdate.getValue()).booleanValue();
         getParameter(BaerConstants.USE_ATM_COR_PARAM_NAME).getEditor().setEnabled(enable);
         getParameter(BaerConstants.SMAC_PARAM_NAME).getEditor().setEnabled(enable);
         getParameter(BaerConstants.BITMASK_PARAM_NAME).getEditor().setEnabled(enable);
         getParameter(BaerConstants.AER_PHASE_PARAM_NAME).getEditor().setEnabled(enable);
    }

    private void handleParameterChangedEventUseAtmCorr(Parameter param) {
        boolean enable = ((Boolean)param.getValue()).booleanValue();
        getParameter(BaerConstants.SMAC_PARAM_NAME).getEditor().setEnabled(enable);
    }

    /**
     * Updates all other parameter with the value set in the request.
     *
     * @param request the <code>Request</code> to be read
     */
    private void setParameter(Request request) throws ProcessorException {
        Parameter toUpdate = null;
        Parameter newParam = null;

        try {
            // bitmask
            newParam = request.getParameter(BaerConstants.BITMASK_PARAM_NAME);
            if (newParam != null) {
                toUpdate = _paramGroup.getParameter(BaerConstants.BITMASK_PARAM_NAME);
                toUpdate.setValue(newParam.getValue());
            }

            // aerosol phase function
            newParam = request.getParameter(BaerConstants.AER_PHASE_PARAM_NAME);
            if (newParam != null) {
                toUpdate = _paramGroup.getParameter(BaerConstants.AER_PHASE_PARAM_NAME);
                toUpdate.setValue(newParam.getValue());
            }

            // process format
              newParam = request.getParameter(BaerConstants.USE_BAER_PARAM_NAME);
              if (newParam != null) {
                  toUpdate = _paramGroup.getParameter(BaerConstants.USE_BAER_PARAM_NAME);
                  toUpdate.setValue(newParam.getValue());
              }

            // process format
              newParam = request.getParameter(BaerConstants.USE_ATM_COR_PARAM_NAME);
              if (newParam != null) {
                  toUpdate = _paramGroup.getParameter(BaerConstants.USE_ATM_COR_PARAM_NAME);
                  toUpdate.setValue(newParam.getValue());
              }

            // process format
               newParam = request.getParameter(BaerConstants.USE_CLOUD_PARAM_NAME);
               if (newParam != null) {
                   toUpdate = _paramGroup.getParameter(BaerConstants.USE_CLOUD_PARAM_NAME);
                   toUpdate.setValue(newParam.getValue());
               }

            // process format
            newParam = request.getParameter(BaerConstants.SMAC_PARAM_NAME);
            if (newParam != null) {
                toUpdate = _paramGroup.getParameter(BaerConstants.SMAC_PARAM_NAME);
                toUpdate.setValue(newParam.getValue());
            }

            // logging
            newParam = request.getParameter(ProcessorConstants.LOG_PREFIX_PARAM_NAME);
            if (newParam != null) {
                toUpdate = _paramGroup.getParameter(ProcessorConstants.LOG_PREFIX_PARAM_NAME);
                toUpdate.setValue(newParam.getValue());
            }

            newParam = request.getParameter(ProcessorConstants.LOG_TO_OUTPUT_PARAM_NAME);
            if (newParam != null) {
                toUpdate = _paramGroup.getParameter(ProcessorConstants.LOG_TO_OUTPUT_PARAM_NAME);
                toUpdate.setValue(newParam.getValue());
            }
        } catch (ParamValidateException e) {
            throw new ProcessorException(e.getMessage());
        }
    }

    /**
     * Copies the value of the input file parameter to the request passed in.
     *
     * @param request the <code>Request</code> to be filled with data
     */
    private void getInputProduct(Request request) {
        File inputFile = ((File) _paramGroup.getParameter(DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME).getValue());
        request.addInputProduct(new ProductRef(inputFile, null, null));
    }

    /**
     * Copies the value of the output file parameter to the request passed in.
     *
     * @param request the <code>Request</code> to be filled with data
     */
    private void getOutputProduct(Request request) {
        File outputFile = ((File) _paramGroup.getParameter(DefaultRequestElementFactory.OUTPUT_PRODUCT_PARAM_NAME).getValue());
        request.addOutputProduct(new ProductRef(outputFile, _outFileFormat, null));
    }

    /**
     * Copies the values of all other parameter needed to process a MERIS product to the request passed in.
     *
     * @param request the <code>Request</code> to be filled with data
     */
    private void getParameter(Request request) {
        request.addParameter(_paramGroup.getParameter(BaerConstants.BITMASK_PARAM_NAME));
        request.addParameter(_paramGroup.getParameter(BaerConstants.AER_PHASE_PARAM_NAME));
        request.addParameter(_paramGroup.getParameter(DefaultRequestElementFactory.LOG_PREFIX_PARAM_NAME));
        request.addParameter(_paramGroup.getParameter(DefaultRequestElementFactory.LOG_TO_OUTPUT_PARAM_NAME));
        request.addParameter(_paramGroup.getParameter(BaerConstants.USE_BAER_PARAM_NAME));
        request.addParameter(_paramGroup.getParameter(BaerConstants.USE_ATM_COR_PARAM_NAME));
        request.addParameter(_paramGroup.getParameter(BaerConstants.USE_CLOUD_PARAM_NAME));
        request.addParameter(_paramGroup.getParameter(BaerConstants.SMAC_PARAM_NAME));
    }

    /**
     * Gets the named Parameter or null if it doesn't exitst in this DataModule.
     *
     * @param name the name of the Parameter to get
     *
     * @return the named Parameter or null if it doesn't exist.
     */
    public Parameter getParameter(String name) {
        return _paramGroup.getParameter(name);
    }

}
