package org.esa.beam.processor.baer;

import org.esa.beam.framework.processor.RequestElementFactory;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.RequestElementFactoryException;
import org.esa.beam.framework.processor.DefaultRequestElementFactory;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.param.ParamValidateException;
import org.esa.beam.framework.param.ParamProperties;
import org.esa.beam.framework.param.editors.ComboBoxEditor;
import org.esa.beam.util.Guardian;
import org.esa.beam.util.SystemUtils;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Jul 23, 2003
 * Time: 3:27:28 PM
 * To change this template use Options | File Templates.
 */
public class BaerRequestElementFactory implements RequestElementFactory {

    private static BaerRequestElementFactory _instance = null;

    private final DefaultRequestElementFactory _defaultFactory;
    private final Map<String,ParamProperties> _paramInfoMap;

    /**
     * Retrieves a reference to the one and only instance of the factory.
     * @return the reference
     */
    public static BaerRequestElementFactory getInstance() {
        if (_instance == null) {
            _instance = new BaerRequestElementFactory();
        }

        return _instance;
    }

    /**
     * Creates a new reference to an input product for the current processing request.
     *
     * @param url the input product's URL, must not be <code>null</code>
     * @param fileFormat the file format, can be <code>null</code> if not known
     * @param typeId the product type identifier, can be <code>null</code> if not known
     * @throws java.lang.IllegalArgumentException if <code>url</code> is <code>null</code>
     * @throws org.esa.beam.framework.processor.RequestElementFactoryException if the element could not be created
     */
    public ProductRef createInputProductRef(File file, String fileFormat, String typeId) throws RequestElementFactoryException {
        return _defaultFactory.createInputProductRef(file, fileFormat, typeId);
    }

    /**
     * Creates a new reference to an output product for the current processing request.
     *
     * @param url the output product's URL, must not be <code>null</code>
     * @param fileFormat the file format, can be <code>null</code> if not known
     * @param typeId the product type identifier, can be <code>null</code> if not known
     * @throws java.lang.IllegalArgumentException if <code>url</code> is <code>null</code>
     * @throws org.esa.beam.framework.processor.RequestElementFactoryException if the element could not be created
     */
    public ProductRef createOutputProductRef(File file, String fileFormat, String typeId) throws RequestElementFactoryException {
        return _defaultFactory.createOutputProductRef(file, fileFormat, typeId);
    }

    /**
     * Creates a new processing parameter for the current processing request.
     *
     * @param name the parameter name, must not be <code>null</code> or empty
     * @param value the parameter value, can be <code>null</code> if yet not known
     * @throws java.lang.IllegalArgumentException if <code>name</code> is <code>null</code> or empty
     * @throws org.esa.beam.framework.processor.RequestElementFactoryException if the parameter could not be created or is invalid
     */
    public Parameter createParameter(String name, String value) throws RequestElementFactoryException {
        Guardian.assertNotNullOrEmpty("name", name);

        Parameter param = null;

        try {
            param = createParamWithDefaultValueSet(name);
            if (value != null) {
                param.setValueAsText(value, null);
            }
        } catch (IllegalArgumentException e) {
            throw new RequestElementFactoryException(e.getMessage());
        }

        return param;
    }

    /**
     * Creates an input product parameter set to the default path.
     */
    public Parameter createDefaultInputProductParameter() {
        // just dispatch - no special functionality needed here
        return _defaultFactory.createDefaultInputProductParameter();
    }

    /**
     * Creates an output product parameter set to the default path.
     */
    public Parameter createDefaultOutputProductParameter() {
        final Parameter outputProductParameter = _defaultFactory.createDefaultOutputProductParameter();
        outputProductParameter.setValue(new File(SystemUtils.getUserHomeDir(), BaerConstants.DEFAULT_OUTPUT_FILE_NAME), null);
        return outputProductParameter;
    }

    /**
     * Creates a default logging pattern parameter set to the prefix passed in.
     * @param prefix the default setting for the logging pattern
     * @return a logging pattern Parameter conforming the system settings
     */
    public Parameter createDefaultLogPatternParameter(String prefix) {
        // just dispatch - no special functionality needed here
        return _defaultFactory.createDefaultLogPatternParameter(prefix);
    }

    /**
     * Creates a logging to output product parameter set to the value passed in.
     * @return a logging to output product Parameter
     */
    public Parameter createLogToOutputParameter(String value) throws ParamValidateException {
        // just dispatch - no special functionality needed here
        return _defaultFactory.createLogToOutputParameter(value);
    }

    /**
     * Creates a parameter with the name passed in by searching the parameter properties
     * for the given name. Once found, creates the parameter according to the properties and
     * initializes it with the default value.
     *
     * @throws java.lang.IllegalArgumentException when the parameter name is not specified as valid name
     */
    public Parameter createParamWithDefaultValueSet(String paramName) {
        ParamProperties paramProps = getParamProperties(paramName);
        Parameter param = new Parameter(paramName, paramProps);
        param.setDefaultValue();
        return param;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Constructs the object. Private access due to singleton!
     */
    private BaerRequestElementFactory() {
        _defaultFactory = new DefaultRequestElementFactory();
        _paramInfoMap = new HashMap<String,ParamProperties>();
        fillParamInfoMap();
    }

    /**
     * Gets a default <code>ParamProperties</code> for the parameter with given name.
     *
     * @param name the parameter name
     * @throws java.lang.IllegalArgumentException when the parameter name is not specified as valid name
     */
    private ParamProperties getParamProperties(String name) throws IllegalArgumentException {
        ParamProperties paramProps = (ParamProperties) _paramInfoMap.get(name);
        if (paramProps == null) {
            throw new IllegalArgumentException("Invalid parameter name: '" + name + "'");
        }
        return paramProps;
    }

    /**
     * Initializes the parameter information map with all parameter information we
     * have available.
     */
    private void fillParamInfoMap() {
        _paramInfoMap.put(BaerConstants.BITMASK_PARAM_NAME, createBitmaspInfo());
        _paramInfoMap.put(BaerConstants.SMAC_PARAM_NAME, createProcessFormatInfo());
        _paramInfoMap.put(BaerConstants.USE_BAER_PARAM_NAME, createBAERProcessFormatInfo());
        _paramInfoMap.put(BaerConstants.USE_ATM_COR_PARAM_NAME, createATMCORProcessFormatInfo());
        _paramInfoMap.put(BaerConstants.USE_CLOUD_PARAM_NAME, createCLOUDProcessFormatInfo());
        _paramInfoMap.put(BaerConstants.AER_PHASE_PARAM_NAME, createAerPhaseParamInfo());
    }


    /**
     * Creates the parameter properties for the parameter bitmask.
     * @return
     */
    private ParamProperties createBitmaspInfo() {
        ParamProperties props = _defaultFactory.createBitmaskParamProperties();

        props.setLabel(BaerConstants.BITMASK_PARAM_LABEL);
        props.setDescription(BaerConstants.BITMASK_PARAM_DESCRIPTION);
        props.setDefaultValue(BaerConstants.BITMASK_PARAM_DEFAULT);

        return props;
    }

    /**
     * Creates the parameter information for the parameter "aer_phase_lut"
     * @return
     */
    private ParamProperties createAerPhaseParamInfo() {
        ParamProperties props = _defaultFactory.createStringArrayParamProperties();

        props.setLabel(BaerConstants.AER_PHASE_PARAM_LABEL);
        props.setDescription(BaerConstants.AER_PHASE_PARAM_DESCRIPTION);
        props.setEditorClass(ComboBoxEditor.class);
        props.setValueSetBound(false);

        return props;
    }

   /**
     * Creates the parameter properties for the parameter bitmask.
     * @return
     */
    private ParamProperties createProcessFormatInfo() {
        ParamProperties props = _defaultFactory.createStringArrayParamProperties();

        props.setLabel(BaerConstants.SMAC_PARAM_LABEL);
        props.setDescription(BaerConstants.SMAC_PARAM_DESCRIPTION);
       props.setEditorClass(ComboBoxEditor.class);
       props.setValueSet(BaerConstants.SMAC_PARAM_VALUES);
        props.setDefaultValue(BaerConstants.SMAC_PARAM_DEFAULT);

        return props;
    }

     /**
     * Creates the parameter properties for the parameter baer.
     * @return
     */
    private ParamProperties createBAERProcessFormatInfo() {
        ParamProperties props = _defaultFactory.createBooleanParamProperties();

        props.setLabel(BaerConstants.USE_BAER_PARAM_LABEL);
        props.setDescription(BaerConstants.USE_BAER_PARAM_DESCRIPTION);
       props.setDefaultValue(new Boolean(true));

        return props;
    }

    /**
     * Creates the parameter properties for the parameter cloud.
     * @return
     */
    private ParamProperties createCLOUDProcessFormatInfo() {
        ParamProperties props = _defaultFactory.createBooleanParamProperties();

        props.setLabel(BaerConstants.USE_CLOUD_PARAM_LABEL);
        props.setDescription(BaerConstants.USE_CLOUD_PARAM_DESCRIPTION);
        props.setDefaultValue(new Boolean(true));

        return props;
    }
     /**
     * Creates the parameter properties for the parameter atmospheric correction.
     * @return
     */
    private ParamProperties createATMCORProcessFormatInfo() {
        ParamProperties props = _defaultFactory.createBooleanParamProperties();

         props.setLabel(BaerConstants.USE_ATM_COR_PARAM_LABEL);
               props.setDescription(BaerConstants.USE_ATM_COR_PARAM_DESCRIPTION);
              props.setDefaultValue(new Boolean(true));


        return props;
    }
}
