package org.esa.beam.processor.toa.ui;

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
import org.esa.beam.framework.processor.ui.ProcessorApp;
import org.esa.beam.framework.processor.ui.AbstractProcessorUI;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.util.Debug;
import org.esa.beam.util.Guardian;
import org.esa.beam.processor.toa.ToaVegConstants;
import org.esa.beam.processor.common.VegRequestElementFactory;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: Tom Date: 20.04.2004 Time: 10:28:28 To change this template use File | Settings |
 * File Templates.
 */
public class ToaVegUi extends AbstractProcessorUI implements ParamChangeListener {

    private Logger _logger;
    private ParamGroup _paramGroup;
    private JPanel _panel;
    private JComboBox _fileFormatCombo;
    private String[] _fileFormatNames;
    private String _outFileFormat;
    private File _requestFile;
    private static final String OUT_FORMAT_LABEL = "Output product format:";

    /**
     * Constructs the object with default parameters
     */
    public ToaVegUi() {
        _logger = Logger.getLogger(ToaVegConstants.LOGGER_NAME);

        createParameterGroup();
    }

    /**
     * Retrieves the base compon ent for the processor specific user interface classes. This can be any Java Swing
     * containertype. This method creates the UI from scratch if not present
     */
    public JComponent getGuiComponent() {
        if (_panel == null) {
            scanWriterFormatStrings();
            createUI();
        }

        return _panel;
    }

    /**
     * Retrieves the requests currently edited.
     */
    public Vector<Request> getRequests() throws ProcessorException {
        Request request = new Request();

        request.setType(ToaVegConstants.REQUEST_TYPE);
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
        VegRequestElementFactory factory = VegRequestElementFactory.getToaVegInstance();
        Request request = new Request();

        request.setType(ToaVegConstants.REQUEST_TYPE);
        request.addInputProduct(createDefaultInputProduct());
        request.addOutputProduct(createDefaultOutputProduct());
        request.addParameter(factory.createParamWithDefaultValueSet(ToaVegConstants.BITMASK_PARAM_NAME));
        request.addParameter(factory.createDefaultLogPatternParameter(ToaVegConstants.DEFAULT_LOG_PREFIX));

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

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private void createParameterGroup() {
        VegRequestElementFactory factory = VegRequestElementFactory.getToaVegInstance();

        try {
            _paramGroup = new ParamGroup();
            final Parameter inputParameter = factory.createDefaultInputProductParameter();
            inputParameter.addParamChangeListener(new ParamChangeListener() {
                public void parameterValueChanged(final ParamChangeEvent event) {
                    handleInputProductChanged();
                }
            });
            _paramGroup.addParameter(inputParameter);
            _paramGroup.addParameter(factory.createDefaultOutputProductParameter());
            _paramGroup.addParameter(factory.createParamWithDefaultValueSet(ToaVegConstants.BITMASK_PARAM_NAME));
            _paramGroup.addParameter(factory.createDefaultLogPatternParameter(ToaVegConstants.DEFAULT_LOG_PREFIX));
            _paramGroup.addParameter(factory.createLogToOutputParameter("false"));
            _paramGroup.addParamChangeListener(this);
        } catch (ParamValidateException e) {
            _logger.severe(e.getMessage());
        }
    }

    private void handleInputProductChanged() {
        final File file = (File) _paramGroup.getParameter(DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME).getValue();
        Product inProduct = null;
        try {
            inProduct = ProductIO.readProduct(file);
        } catch (IOException e) {
            Debug.trace(e);
        }
        final Parameter parameter = _paramGroup.getParameter(ToaVegConstants.BITMASK_PARAM_NAME);
        parameter.getProperties().setPropertyValue(BooleanExpressionEditor.PROPERTY_KEY_SELECTED_PRODUCT,
                                                   inProduct);
    }

    /**
     * Creates all user interface components and sets them to their appropriate default values
     */
    private void createUI() {
        int line = 0;
        _panel = GridBagUtils.createDefaultEmptyBorderPanel();
        final GridBagConstraints gbc = GridBagUtils.createConstraints(null);

        // input product
        // -------------
        addParameterToPanel(_panel, DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME, line, 12, gbc);
        line += 2;

        // output product
        // --------------
        addParameterToPanel(_panel, DefaultRequestElementFactory.OUTPUT_PRODUCT_PARAM_NAME, line, 12, gbc);
        line += 2;

        final JLabel outFormatLabel = new JLabel(OUT_FORMAT_LABEL);
        _fileFormatCombo = new JComboBox(_fileFormatNames);
        _fileFormatCombo.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                updateOutFileType();
            }
        });
        GridBagUtils.setAttributes(gbc,
                                   "anchor=SOUTHWEST, fill=NONE, insets.top=12, weightx = 0, weighty=0.5, gridy=" + String.valueOf(
                                           line++));
        GridBagUtils.addToPanel(_panel, outFormatLabel, gbc);
        GridBagUtils.setAttributes(gbc, "anchor=NORTHWEST, weighty=0.5, insets.top=0 ,gridy=" + String.valueOf(line++));
        GridBagUtils.addToPanel(_panel, _fileFormatCombo, gbc);


        addParameterToPanel(_panel, ToaVegConstants.BITMASK_PARAM_NAME, line, 12, gbc);
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
     * Callback for output file format combo box.
     */
    private void updateOutFileType() {
        _outFileFormat = (String) _fileFormatCombo.getSelectedItem();
    }

    /**
     * Scans the ProductIO for all product format strings of the registered writer plugins
     */
    private void scanWriterFormatStrings() {
        ProductIOPlugInManager manager = ProductIOPlugInManager.getInstance();
        _fileFormatNames = manager.getAllProductWriterFormatStrings();
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
        request.addParameter(_paramGroup.getParameter(ToaVegConstants.BITMASK_PARAM_NAME));
        request.addParameter(_paramGroup.getParameter(DefaultRequestElementFactory.LOG_PREFIX_PARAM_NAME));
        request.addParameter(_paramGroup.getParameter(DefaultRequestElementFactory.LOG_TO_OUTPUT_PARAM_NAME));
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

    /**
     * Scans the input product for flags and assigns these to the bitmask parameter
     */
    private void scanForBitmasksAndAssign() {
        Parameter param = _paramGroup.getParameter(DefaultRequestElementFactory.INPUT_PRODUCT_PARAM_NAME);
        File inputFile = (File) param.getValue();

        // check if file exists and is file
        if ((inputFile != null) && inputFile.exists() && inputFile.isFile()) {
            try {
                Product inProduct = ProductIO.readProduct(inputFile);
                if (inProduct != null) {
                    String[] bitmaskFlags = inProduct.getAllFlagNames();

                    Parameter bitmask = _paramGroup.getParameter(ToaVegConstants.BITMASK_PARAM_NAME);
                    bitmask.getProperties().setValueSet(bitmaskFlags);
                }
            } catch (IOException e) {
                Debug.trace(e);
            }
        }
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
            newParam = request.getParameter(ToaVegConstants.BITMASK_PARAM_NAME);
            if (newParam != null) {
                toUpdate = _paramGroup.getParameter(ToaVegConstants.BITMASK_PARAM_NAME);
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
     * Creates a <code>ProductRef</code> pointing to the VEG default input product.
     */
    private ProductRef createDefaultInputProduct() {
        Parameter inProdParam = VegRequestElementFactory.getToaVegInstance().createDefaultInputProductParameter();
        ProductRef ref = null;
        File inProd = (File) inProdParam.getValue();

        ref = new ProductRef(inProd);

        return ref;
    }

    /**
     * Creates a <code>ProductRef</code> pointing to the MERIS default output product.
     */
    private ProductRef createDefaultOutputProduct() {
        Parameter outProdParam = VegRequestElementFactory.getToaVegInstance().createDefaultOutputProductParameter();
        File outProd = (File) outProdParam.getValue();
        ProductRef ref = null;

        ref = new ProductRef(outProd);
        
        return ref;
    }
}
