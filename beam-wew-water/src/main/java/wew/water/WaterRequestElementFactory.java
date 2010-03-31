/*
 * $Id: WaterRequestElementFactory.java, MS0610151415
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

import org.esa.beam.framework.dataio.ProductIOPlugInManager;
import org.esa.beam.framework.param.ParamProperties;
import org.esa.beam.framework.param.ParamValidateException;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.processor.*;
import org.esa.beam.util.StringUtils;

import java.io.File;


/*
 * Responsible for creating the elements for a processing request used by the WATER Processor.
 *
 */
public class WaterRequestElementFactory implements RequestElementFactory {

    // singleton instance
    private static WaterRequestElementFactory _factory;

    private DefaultRequestElementFactory _defaultFactory;

    /*
     * Singleton interface - creates the one and only instance of this factory.
     * @return areference to the factory
     */
    public static WaterRequestElementFactory getInstance() {
        if (_factory == null) {
            _factory = new WaterRequestElementFactory();
        }
        return _factory;
    }



    /*
     * Creates an output format parameter.
     * @return
     */
    public Parameter createOutputFormatParameter() {
        ProductIOPlugInManager instance = ProductIOPlugInManager.getInstance();
        String[] formats = instance.getAllProductWriterFormatStrings();
        ParamProperties paramProperties = _defaultFactory.createStringParamProperties();
        if (formats.length > 0) {
            if (StringUtils.contains(formats, WaterProcessor.DEFAULT_OUTPUT_FORMAT)) {
                paramProperties.setDefaultValue(WaterProcessor.DEFAULT_OUTPUT_FORMAT);
            } else {
                paramProperties.setDefaultValue(formats[0]);
            }
        }
        paramProperties.setValueSet(formats);
        paramProperties.setValueSetBound(true);
        paramProperties.setReadOnly(true);
        paramProperties.setLabel(ProcessorConstants.OUTPUT_FORMAT_LABELTEXT);
        paramProperties.setDescription(ProcessorConstants.OUTPUT_FORMAT_DESCRIPTION);
        return new Parameter(ProcessorConstants.OUTPUT_FORMAT_PARAM_NAME, paramProperties);
    }

    public Parameter createParameter(String name, String value) throws RequestElementFactoryException {
        return _defaultFactory.createParameter(name, value);
    }

    /*
     * Creates a new reference to an input product for the current processing request.
     *
     * @param file the input product's file, must not be <code>null</code>
     * @param fileFormat the file format, can be <code>null</code> if not known
     * @param typeId the product type identifier, can be <code>null</code> if not known
     * @throws IllegalArgumentException if <code>url</code> is <code>null</code>
     * @throws RequestElementFactoryException if the element could not be created
     */
    public ProductRef createInputProductRef(File file, String fileFormat, String typeId) throws RequestElementFactoryException {
        return _defaultFactory.createInputProductRef(file, fileFormat, typeId);
    }

    /*
     * Creates a new reference to an output product for the current processing request.
     *
     * @param file the output product's file, must not be <code>null</code>
     * @param fileFormat the file format, can be <code>null</code> if not known
     * @param typeId the product type identifier, can be <code>null</code> if not known
     * @throws IllegalArgumentException if <code>url</code> is <code>null</code>
     * @throws RequestElementFactoryException if the element could not be created
     */
    public ProductRef createOutputProductRef(File file, String fileFormat, String typeId) throws RequestElementFactoryException {
        return _defaultFactory.createOutputProductRef(file, fileFormat, typeId);
    }

    /*
     * Creates a parameter for the default input product path - which is the
     * current user's home directory.
     */
    public Parameter createDefaultInputProductParameter() {
        return _defaultFactory.createDefaultInputProductParameter();
    }

    /*
     * Creates a parameter for the default checkbox1
     */
    public Parameter createDefaultCheckbox1Parameter() {
	ParamProperties paramProps = new ParamProperties();
	paramProps.setValueType(Boolean.class);
	paramProps.setDefaultValue(new Boolean(WaterProcessor.CHECKBOX1_DEFAULT));
        paramProps.setLabel(WaterProcessor.CHECKBOX1_LABEL_TEXT);
        paramProps.setDescription(WaterProcessor.CHECKBOX1_DESCRIPTION);
	Parameter param = new Parameter(WaterProcessor.CHECKBOX1_PARAM_NAME, paramProps);
//	param.setDefaultValue(); 
	return param;
    }

    /*
     * Creates a parameter for the default checkbox2
     */
    public Parameter createDefaultCheckbox2Parameter() {
	ParamProperties paramProps = new ParamProperties();
	paramProps.setValueType(Boolean.class);
	paramProps.setDefaultValue(new Boolean(WaterProcessor.CHECKBOX2_DEFAULT));
        paramProps.setLabel(WaterProcessor.CHECKBOX2_LABEL_TEXT);
        paramProps.setDescription(WaterProcessor.CHECKBOX2_DESCRIPTION);
	Parameter param = new Parameter(WaterProcessor.CHECKBOX2_PARAM_NAME, paramProps);
//	param.setDefaultValue(); 
	return param;
    }

    /*
     * Creates a parameter for the default checkbox3
     */
    public Parameter createDefaultCheckbox3Parameter() {
	ParamProperties paramProps = new ParamProperties();
	paramProps.setValueType(Boolean.class);
	paramProps.setDefaultValue(new Boolean(WaterProcessor.CHECKBOX3_DEFAULT));
        paramProps.setLabel(WaterProcessor.CHECKBOX3_LABEL_TEXT);
        paramProps.setDescription(WaterProcessor.CHECKBOX3_DESCRIPTION);
	Parameter param = new Parameter(WaterProcessor.CHECKBOX3_PARAM_NAME, paramProps);
//	param.setDefaultValue(); 
	return param;
    }

    /*
     * Creates a parameter for the default checkbox4
     */
    public Parameter createDefaultCheckbox4Parameter() {
	ParamProperties paramProps = new ParamProperties();
	paramProps.setValueType(Boolean.class);
	paramProps.setDefaultValue(new Boolean(WaterProcessor.CHECKBOX4_DEFAULT));
        paramProps.setLabel(WaterProcessor.CHECKBOX4_LABEL_TEXT);
        paramProps.setDescription(WaterProcessor.CHECKBOX4_DESCRIPTION);
	Parameter param = new Parameter(WaterProcessor.CHECKBOX4_PARAM_NAME, paramProps);
//	param.setDefaultValue(); 
	return param;
    }

    /*
     * Creates a parameter for the default checkbox5
     */
    public Parameter createDefaultCheckbox5Parameter() {
	ParamProperties paramProps = new ParamProperties();
	paramProps.setValueType(Boolean.class);
	paramProps.setDefaultValue(new Boolean(WaterProcessor.CHECKBOX5_DEFAULT));
        paramProps.setLabel(WaterProcessor.CHECKBOX5_LABEL_TEXT);
        paramProps.setDescription(WaterProcessor.CHECKBOX5_DESCRIPTION);
	Parameter param = new Parameter(WaterProcessor.CHECKBOX5_PARAM_NAME, paramProps);
//	param.setDefaultValue(); 
	return param;
    }

    /*
     * Creates a parameter for the default checkbox6
     */
    public Parameter createDefaultCheckbox6Parameter() {
	ParamProperties paramProps = new ParamProperties();
	paramProps.setValueType(Boolean.class);
	paramProps.setDefaultValue(new Boolean(WaterProcessor.CHECKBOX6_DEFAULT));
        paramProps.setLabel(WaterProcessor.CHECKBOX6_LABEL_TEXT);
        paramProps.setDescription(WaterProcessor.CHECKBOX6_DESCRIPTION);
	Parameter param = new Parameter(WaterProcessor.CHECKBOX6_PARAM_NAME, paramProps);
//	param.setDefaultValue(); 
	return param;
    }

    /*
     * Creates an output product parameter set to the default path.
     */
    public Parameter createDefaultOutputProductParameter() {
        Parameter defaultOutputProductParameter = _defaultFactory.createDefaultOutputProductParameter();
        ParamProperties properties = defaultOutputProductParameter.getProperties();
        Object defaultValue = properties.getDefaultValue();
        if (defaultValue instanceof File) {
            File file = (File) defaultValue;
            file = new File(file, WaterProcessor.DEFAULT_OUTPUT_DIR_NAME);
            properties.setDefaultValue(new File(file, WaterProcessor.DEFAULT_OUTPUT_PRODUCT_NAME));
        }
        defaultOutputProductParameter.setDefaultValue();
        return defaultOutputProductParameter;
    }

    /*
     * Creates a default logging pattern parameter set to the prefix passed in.
     * @param prefix the default setting for the logging pattern
     * @return a logging pattern Parameter conforming the system settings
     */
    public Parameter createDefaultLogPatternParameter(String prefix) {
        return _defaultFactory.createDefaultLogPatternParameter(prefix);
    }

    /*
     * Creates a logging to output product parameter set to true.
     * @return
     */
    public Parameter createLogToOutputParameter(String value) throws ParamValidateException {
        return _defaultFactory.createLogToOutputParameter(value);
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /*
     * Constructs the object.
     */
    private WaterRequestElementFactory() {
        _defaultFactory = DefaultRequestElementFactory.getInstance();
    }
}
