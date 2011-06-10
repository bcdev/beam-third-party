/*
 * $Id: WaterProcessorUI.java, 0610151415
 *
 * Copyright (C) 2005/7 by WeW (michael.schaale@wew.fu-berlin.de)
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
package wew.water;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.param.ParamChangeEvent;
import org.esa.beam.framework.param.ParamChangeListener;
import org.esa.beam.framework.param.ParamGroup;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.framework.processor.ProcessorUtils;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.Request;
import org.esa.beam.framework.processor.ui.ProcessorApp;
import org.esa.beam.framework.processor.ui.ProcessorUI;
import org.esa.beam.framework.ui.GridBagUtils;
import org.esa.beam.util.Guardian;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class WaterProcessorUI implements ProcessorUI {

    private JTabbedPane _tabbedPane;
    private ParamGroup _paramGroup;
    private File _requestFile;
    private WaterRequestElementFactory _factory;

    /*
     * Creates the ui class with default parameters
     */
    public WaterProcessorUI() {
        _factory = WaterRequestElementFactory.getInstance();
    }

    /*
     * Retrieves the base component for the processor specific user interface classes.
     * This can be any Java Swing containertype. This method creates the UI from scratch if
     * not present.
     */
    @Override
    public JComponent getGuiComponent() {
        if (_tabbedPane == null) {
            createUI();
        }
        return _tabbedPane;
    }

    /*
     * Retrieves the requests currently edited.
     */
    @Override
    public Vector getRequests() throws ProcessorException {
        Vector<Request> requests = new Vector<Request>();
        requests.add(createRequest());
        return requests;
    }

    /*
     * Sets a new Request list to be edited.
     *
     * @param requests the request list to be edited must not be <code>null</code>.
     */
    @Override
    public void setRequests(Vector requests) throws ProcessorException {
        Guardian.assertNotNull("requests", requests);
        if (!requests.isEmpty()) {
            Request request = (Request) requests.elementAt(0);
            _requestFile = request.getFile();
            updateParamInputFile(request);
            updateParamOutputFile(request);
            updateParamOutputFormat(request);
            updateParamCheckBoxFormat(request, WaterProcessor.CHECKBOX1_PARAM_NAME);
            updateParamCheckBoxFormat(request, WaterProcessor.CHECKBOX2_PARAM_NAME);
            updateParamCheckBoxFormat(request, WaterProcessor.CHECKBOX3_PARAM_NAME);
            updateParamCheckBoxFormat(request, WaterProcessor.CHECKBOX4_PARAM_NAME);
            updateParamCheckBoxFormat(request, WaterProcessor.CHECKBOX5_PARAM_NAME);
            updateParamCheckBoxFormat(request, WaterProcessor.CHECKBOX6_PARAM_NAME);
        } else {
            setDefaultRequests();
        }
    }

    /*
     * Create a new default request for the sst processor and sets it to the UI
     */
    @Override
    public void setDefaultRequests() throws ProcessorException {
        Vector<Request> requests = new Vector<Request>();
        requests.add(createDefaultRequest());
        setRequests(requests);
    }

    /*
     * Sets the processor app for the UI
     */
    @Override
    public void setApp(ProcessorApp app) {
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /*
     * Creates a request with all parameters set to their respective default values.
     * @return the default request
     */
    private Request createDefaultRequest() {
        _paramGroup.getParameter(ProcessorConstants.INPUT_PRODUCT_PARAM_NAME).setDefaultValue();
        _paramGroup.getParameter(ProcessorConstants.OUTPUT_PRODUCT_PARAM_NAME).setDefaultValue();
        _paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).setDefaultValue();

        _paramGroup.getParameter(WaterProcessor.CHECKBOX1_PARAM_NAME).setDefaultValue();
        _paramGroup.getParameter(WaterProcessor.CHECKBOX2_PARAM_NAME).setDefaultValue();

        _paramGroup.getParameter(WaterProcessor.CHECKBOX3_PARAM_NAME).setDefaultValue();
        _paramGroup.getParameter(WaterProcessor.CHECKBOX4_PARAM_NAME).setDefaultValue();

        _paramGroup.getParameter(WaterProcessor.CHECKBOX5_PARAM_NAME).setDefaultValue();
        _paramGroup.getParameter(WaterProcessor.CHECKBOX6_PARAM_NAME).setDefaultValue();

        return createRequest();
    }

    /*
     * Creates all user interface components of the sst user interface
     */
    private void createUI() {
        initParamGroup();
        _tabbedPane = new JTabbedPane();
        _tabbedPane.add("Process parameters", createPathTab());
    }

    /*
     * Initializes the parameter group to hold all parameter needed for the processor.
     */
    private void initParamGroup() {
        _paramGroup = new ParamGroup();
        final Parameter inputProductParameter = _factory.createDefaultInputProductParameter();
        final Parameter checkbox1Parameter = _factory.createDefaultCheckbox1Parameter();
        final Parameter checkbox2Parameter = _factory.createDefaultCheckbox2Parameter();
        final Parameter checkbox3Parameter = _factory.createDefaultCheckbox3Parameter();
        final Parameter checkbox4Parameter = _factory.createDefaultCheckbox4Parameter();
        final Parameter checkbox5Parameter = _factory.createDefaultCheckbox5Parameter();
        final Parameter checkbox6Parameter = _factory.createDefaultCheckbox6Parameter();

        _paramGroup.addParameter(inputProductParameter);

        _paramGroup.addParameter(_factory.createDefaultOutputProductParameter());
        _paramGroup.addParameter(_factory.createOutputFormatParameter());
        _paramGroup.addParameter(checkbox1Parameter);
        _paramGroup.addParameter(checkbox2Parameter);
        _paramGroup.addParameter(checkbox3Parameter);
        _paramGroup.addParameter(checkbox4Parameter);
        _paramGroup.addParameter(checkbox5Parameter);
        _paramGroup.addParameter(checkbox6Parameter);

        inputProductParameter.addParamChangeListener(new ParamChangeListener() {
            @Override
            public void parameterValueChanged(ParamChangeEvent event) {
                checkForValidInputProduct(inputProductParameter);
            }
        });

        checkbox1Parameter.addParamChangeListener(new ParamChangeListener() {
            @Override
            public void parameterValueChanged(ParamChangeEvent event) {
                checkForValidCheckbox1(checkbox1Parameter);
            }
        });

        checkbox2Parameter.addParamChangeListener(new ParamChangeListener() {
            @Override
            public void parameterValueChanged(ParamChangeEvent event) {
                checkForValidCheckbox2(checkbox2Parameter);
            }
        });

        checkbox3Parameter.addParamChangeListener(new ParamChangeListener() {
            @Override
            public void parameterValueChanged(ParamChangeEvent event) {
                checkForValidCheckbox3(checkbox3Parameter);
            }
        });

        checkbox4Parameter.addParamChangeListener(new ParamChangeListener() {
            @Override
            public void parameterValueChanged(ParamChangeEvent event) {
                checkForValidCheckbox4(checkbox4Parameter);
            }
        });

        checkbox5Parameter.addParamChangeListener(new ParamChangeListener() {
            @Override
            public void parameterValueChanged(ParamChangeEvent event) {
                checkForValidCheckbox5(checkbox5Parameter);
            }
        });

        checkbox6Parameter.addParamChangeListener(new ParamChangeListener() {
            @Override
            public void parameterValueChanged(ParamChangeEvent event) {
                checkForValidCheckbox6(checkbox6Parameter);
            }
        });
    }

    /*
     * Create the ui tab for the path editing.
     *
     * @return the panel containing the paths tab
     */
    private JPanel createPathTab() {
        JPanel panel = GridBagUtils.createDefaultEmptyBorderPanel();
        final GridBagConstraints gbc = GridBagUtils.createConstraints(null);
        Parameter param;

        gbc.gridy = 0;
        gbc.gridx = 0;

        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // input product
        // -------------
        param = _paramGroup.getParameter(ProcessorConstants.INPUT_PRODUCT_PARAM_NAME);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=7, fill=HORIZONTAL, anchor=WEST, weighty=0.5");
        GridBagUtils.addToPanel(panel, param.getEditor().getLabelComponent(), gbc);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=0, weighty=0.0");
        GridBagUtils.addToPanel(panel, param.getEditor().getComponent(), gbc);

        // output product
        // --------------
        param = _paramGroup.getParameter(ProcessorConstants.OUTPUT_PRODUCT_PARAM_NAME);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=7, fill=HORIZONTAL, anchor=WEST, weighty=0.5");
        GridBagUtils.addToPanel(panel, param.getEditor().getLabelComponent(), gbc);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=0, weighty=0.0");
        GridBagUtils.addToPanel(panel, param.getEditor().getComponent(), gbc);

        // output format
        // -------------
        param = _paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=7, fill=HORIZONTAL, anchor=WEST, weighty=0.5");
        GridBagUtils.addToPanel(panel, param.getEditor().getLabelComponent(), gbc);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "insets.top=0, weighty=0.0");
        GridBagUtils.addToPanel(panel, param.getEditor().getComponent(), gbc);
        gbc.gridwidth = 1;

        // checkbox3 (caseI)
        // -----------------
        param = _paramGroup.getParameter(WaterProcessor.CHECKBOX3_PARAM_NAME);
        gbc.gridy++;
        GridBagUtils.setAttributes(gbc, "anchor=WEST, fill=NONE, insets.top=20, weightx=0.25, weighty=0.0");
        JPanel check3Panel = new JPanel();
        check3Panel.add(param.getEditor().getComponent());
        GridBagUtils.addToPanel(panel, check3Panel, gbc);

        // checkbox4 (caseII)
        // ------------------
        param = _paramGroup.getParameter(WaterProcessor.CHECKBOX4_PARAM_NAME);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = GridBagConstraints.RELATIVE;
        JPanel check4Panel = new JPanel();
        check4Panel.add(param.getEditor().getComponent());
        GridBagUtils.addToPanel(panel, check4Panel, gbc);
//	gbc.gridwidth=1;

        return panel;
    }

    private Request createRequest() {
        Request request = new Request();
        request.setType(WaterProcessor.REQUEST_TYPE);
        request.setFile(_requestFile);
        request.addInputProduct(createInputProductRef());
        request.addOutputProduct(createOutputProductRef());
        request.addParameter(createOutputFormatParamForRequest());

        request.addParameter(createCheckbox1FormatParamForRequest());
        request.addParameter(createCheckbox2FormatParamForRequest());

        request.addParameter(createCheckbox3FormatParamForRequest());
        request.addParameter(createCheckbox4FormatParamForRequest());

        request.addParameter(createCheckbox5FormatParamForRequest());
        request.addParameter(createCheckbox6FormatParamForRequest());

        return request;
    }

    /*
     * Creates an input product reference from the request.
     * @return
     */
    private ProductRef createInputProductRef() {
        final String filePath = _paramGroup.getParameter(ProcessorConstants.INPUT_PRODUCT_PARAM_NAME).getValueAsText();
        return new ProductRef(new File(filePath), null, null);
    }

    /*
     * Creates an output product reference from the request.
     * @return
     */
    private ProductRef createOutputProductRef() {
        final String fileName = _paramGroup.getParameter(ProcessorConstants.OUTPUT_PRODUCT_PARAM_NAME).getValueAsText();
        final String fileFormat = _paramGroup.getParameter(
                ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).getValueAsText();

        return ProcessorUtils.createProductRef(fileName, fileFormat);
    }

    private Parameter createOutputFormatParamForRequest() {
        String outputFormat = _paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).getValueAsText();
        return new Parameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME, outputFormat);
    }

    private Parameter createCheckbox1FormatParamForRequest() {
        Boolean checkbox1Format = (Boolean) (_paramGroup.getParameter(WaterProcessor.CHECKBOX1_PARAM_NAME).getValue());
        return new Parameter(WaterProcessor.CHECKBOX1_PARAM_NAME, checkbox1Format);
    }

    private Parameter createCheckbox2FormatParamForRequest() {
        Boolean checkbox2Format = (Boolean) (_paramGroup.getParameter(WaterProcessor.CHECKBOX2_PARAM_NAME).getValue());
        return new Parameter(WaterProcessor.CHECKBOX2_PARAM_NAME, checkbox2Format);
    }

    private Parameter createCheckbox3FormatParamForRequest() {
        Boolean checkbox3Format = (Boolean) (_paramGroup.getParameter(WaterProcessor.CHECKBOX3_PARAM_NAME).getValue());
        return new Parameter(WaterProcessor.CHECKBOX3_PARAM_NAME, checkbox3Format);
    }

    private Parameter createCheckbox4FormatParamForRequest() {
        Boolean checkbox4Format = (Boolean) (_paramGroup.getParameter(WaterProcessor.CHECKBOX4_PARAM_NAME).getValue());
        return new Parameter(WaterProcessor.CHECKBOX4_PARAM_NAME, checkbox4Format);
    }

    private Parameter createCheckbox5FormatParamForRequest() {
        Boolean checkbox5Format = (Boolean) (_paramGroup.getParameter(WaterProcessor.CHECKBOX5_PARAM_NAME).getValue());
        return new Parameter(WaterProcessor.CHECKBOX5_PARAM_NAME, checkbox5Format);
    }

    private Parameter createCheckbox6FormatParamForRequest() {
        Boolean checkbox6Format = (Boolean) (_paramGroup.getParameter(WaterProcessor.CHECKBOX6_PARAM_NAME).getValue());
        return new Parameter(WaterProcessor.CHECKBOX6_PARAM_NAME, checkbox6Format);
    }

    private void updateParamOutputFormat(Request request) {
        String format = request.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).getValueAsText();
        _paramGroup.getParameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME).setValue(format, null);
    }

    private void updateParamCheckBoxFormat(Request request, String paramName) {
        Parameter param = request.getParameter(paramName);
        if (param != null) {
            // param.getValueType sometimes indicate type String (when reading an XML)
            // or type Boolean (when setting the checkmarks and saving the request).
            // The reason for this behaviour could not be figured out. Thus this
            // workaround was created.
            Boolean value;
            if (param.getValueType() == String.class) {
                // Analyse the String ...
                value = "true".equals(param.getValue());
            } else {
                // Directly use the Boolean
                value = Boolean.valueOf(String.valueOf(param.getValue()));
            }
            Parameter toUpdate = _paramGroup.getParameter(paramName);
            toUpdate.setValue(value, null);
        }
    }

    private void updateParamOutputFile(Request request) {
        File file = new File(request.getOutputProductAt(0).getFilePath());
        _paramGroup.getParameter(ProcessorConstants.OUTPUT_PRODUCT_PARAM_NAME).setValue(file, null);
    }

    private void updateParamInputFile(Request request) {
        File file = new File(request.getInputProductAt(0).getFilePath());
        _paramGroup.getParameter(ProcessorConstants.INPUT_PRODUCT_PARAM_NAME).setValue(file, null);
    }

    /*
     * Brings up a message box if the input product is not valid.
     * Valid input products are: products wich contains at least
     * the bands named '{@link WaterProcessor#LOWER_INPUT_BAND_NAME lowBandName}'
     * and '{@link WaterProcessor#UPPER_INPUT_BAND_NAME highBandName}'.
     * The message box only comes up if the parameter contains an existing file.
     * So you can create requests with not existing input products without interfering message Box.
     * @param parameter
     */
    private void checkForValidInputProduct(Parameter parameter) {
        Object value = parameter.getValue();
        File file = null;
//	System.out.println("---EVENT InputProduct---");
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
            Product product = ProductIO.readProduct(file);
            if (product != null) {
                /*
                    final String lowBandName = WaterProcessor.LOWER_INPUT_BAND_NAME;
                    final String highBandName = WaterProcessor.UPPER_INPUT_BAND_NAME;
                    if (product.getBand(highBandName) == null || product.getBand(lowBandName) == null) {
                        msg = "The WATER Processor only works with products which contains\n" +
                                "at least the bands '" + lowBandName + "' and '" + highBandName + "'.";
                    }
            */
            } else {
                msg = "Unknown file format.";
            }
        } catch (IOException e) {
            msg = e.getMessage();
        }
        if (msg != null) {
            JOptionPane.showMessageDialog(getGuiComponent(), "Invalid input file:\n" + msg,
                                          WaterProcessor.PROCESSOR_NAME, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkForValidCheckbox1(Parameter parameter) {
        Object value = parameter.getValue();
        boolean value_box1;
        boolean value_box2;

        // We do only arrive here, if something has changed. Keep this in mind !

        // Get access to CHECKBOX1_PARAM_NAME
        Parameter param1 = _paramGroup.getParameter(WaterProcessor.CHECKBOX1_PARAM_NAME);
        // Get access to CHECKBOX2_PARAM_NAME
        Parameter param2 = _paramGroup.getParameter(WaterProcessor.CHECKBOX2_PARAM_NAME);
        value_box2 = Boolean.valueOf(param2.getValue().toString());

        // Get the current boolean state
        value_box1 = Boolean.valueOf(value.toString());

        // Avoid that both checkboxes have the same state
        if (value_box1 == value_box2) {
            value_box2 = !value_box2;
            param2.setValue(value_box2, null);
        }
    }

    private void checkForValidCheckbox2(Parameter parameter) {
        Object value = parameter.getValue();
        boolean value_box1;
        boolean value_box2;

        // We do only arrive here, if something has changed. Keep this in mind !

        // Get access to CHECKBOX1_PARAM_NAME
        Parameter param1 = _paramGroup.getParameter(WaterProcessor.CHECKBOX1_PARAM_NAME);
        value_box1 = Boolean.valueOf(param1.getValue().toString());
        // Get access to CHECKBOX2_PARAM_NAME
        Parameter param2 = _paramGroup.getParameter(WaterProcessor.CHECKBOX2_PARAM_NAME);

        // Get the current boolean state
        value_box2 = Boolean.valueOf(value.toString());

        // Avoid that both checkboxes have the same state
        if (value_box1 == value_box2) {
            value_box1 = !value_box1;
            param1.setValue(value_box1, null);
        }
    }

    private void checkForValidCheckbox3(Parameter parameter) {
        Object value = parameter.getValue();
        boolean value_box3, value_box4;

        // We do only arrive here, if something has changed. Keep this in mind !

        // Get access to CHECKBOX3_PARAM_NAME
        Parameter param3 = _paramGroup.getParameter(WaterProcessor.CHECKBOX3_PARAM_NAME);
        // Get access to CHECKBOX4_PARAM_NAME
        Parameter param4 = _paramGroup.getParameter(WaterProcessor.CHECKBOX4_PARAM_NAME);
        value_box4 = Boolean.valueOf(param4.getValue().toString());

        // Get the current boolean state
        value_box3 = Boolean.valueOf(value.toString());

//*NO_CHANGE_ALLOWED
        param3.setValue(WaterProcessor.CHECKBOX3_DEFAULT, null);
//*/

        // Avoid that both checkboxes have the same state
/*CHANGE_ALLOWED
	if(value_box3 == value_box4) { 
	    value_box4 = !value_box4;
	    param4.setValue(new Boolean(value_box4), null);
	}
//*/

    }

    private void checkForValidCheckbox4(Parameter parameter) {
        Object value = parameter.getValue();
        boolean value_box3, value_box4;

        // We do only arrive here, if something has changed. Keep this in mind !

        // Get access to CHECKBOX3_PARAM_NAME
        Parameter param3 = _paramGroup.getParameter(WaterProcessor.CHECKBOX3_PARAM_NAME);
        value_box3 = Boolean.valueOf(param3.getValue().toString());
        // Get access to CHECKBOX4_PARAM_NAME
        Parameter param4 = _paramGroup.getParameter(WaterProcessor.CHECKBOX4_PARAM_NAME);

        // Get the current boolean state
        value_box4 = Boolean.valueOf(value.toString());

//*NO_CHANGE_ALLOWED
        param4.setValue(WaterProcessor.CHECKBOX4_DEFAULT, null);

        // Avoid that both checkboxes have the same state
/*CHANGE_ALLOWED
	if(value_box3 == value_box4) { 
	    value_box3 = !value_box3;
	    param3.setValue(new Boolean(value_box3), null);
	}
//*/

    }

    private void checkForValidCheckbox5(Parameter parameter) {
        Object value = parameter.getValue();
        boolean value_box5;

        // We do only arrive here, if something has changed. Keep this in mind !

        // Get access to CHECKBOX5_PARAM_NAME
        Parameter param5 = _paramGroup.getParameter(WaterProcessor.CHECKBOX5_PARAM_NAME);

        // Get the current boolean state
        value_box5 = Boolean.valueOf(value.toString());

        //param5.setValue(new Boolean(WaterProcessor.CHECKBOX5_DEFAULT), null);
    }

    private void checkForValidCheckbox6(Parameter parameter) {
        Object value = parameter.getValue();
        boolean value_box6;

        // We do only arrive here, if something has changed. Keep this in mind !

        // Get access to CHECKBOX6_PARAM_NAME
        Parameter param6 = _paramGroup.getParameter(WaterProcessor.CHECKBOX6_PARAM_NAME);

        // Get the current boolean state
        value_box6 = Boolean.valueOf(value.toString());

//*NO_CHANGE_ALLOWED
        param6.setValue(WaterProcessor.CHECKBOX6_DEFAULT, null);
//*/
        //param6.setValue(new Boolean(WaterProcessor.CHECKBOX6_DEFAULT), null);
    }
}
