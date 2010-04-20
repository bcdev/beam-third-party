/*
 * $Id: VegRequestElementFactory.java,v 1.6 2006/03/27 15:23:20 meris Exp $
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
package org.esa.beam.processor.common;

import org.esa.beam.framework.param.ParamProperties;
import org.esa.beam.framework.param.ParamValidateException;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.processor.DefaultRequestElementFactory;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.RequestElementFactory;
import org.esa.beam.framework.processor.RequestElementFactoryException;
import org.esa.beam.processor.toa.ToaVegConstants;
import org.esa.beam.processor.toc.TocVegConstants;
import org.esa.beam.util.Guardian;
import org.esa.beam.util.SystemUtils;

import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class VegRequestElementFactory implements RequestElementFactory {

    private static VegRequestElementFactory _tocVegInstance = null;
    private static VegRequestElementFactory _toaVegInstance = null;

    private final DefaultRequestElementFactory _defaultFactory;
    private final Map<String,ParamProperties> _paramInfoMap;
    private final String _defaultOutputFileName;

    /**
     * Retrieves the one and only instance of this class
     *
     * @return
     */
    public static VegRequestElementFactory getTocVegInstance() {
        if (_tocVegInstance == null) {
            _tocVegInstance = new VegRequestElementFactory(TocVegConstants.DEFAULT_OUTPUT_FILE_NAME,
                                                           TocVegConstants.BITMASK_PARAM_NAME,
                                                           TocVegConstants.BITMASK_PARAM_LABEL,
                                                           TocVegConstants.BITMASK_PARAM_DESCRIPTION,
                                                           TocVegConstants.BITMASK_PARAM_DEFAULT);
        }
        return _tocVegInstance;
    }

    public static VegRequestElementFactory getToaVegInstance() {
        if (_toaVegInstance == null) {
            _toaVegInstance = new VegRequestElementFactory(ToaVegConstants.DEFAULT_OUTPUT_FILE_NAME,
                                                           ToaVegConstants.BITMASK_PARAM_NAME,
                                                           ToaVegConstants.BITMASK_PARAM_LABEL,
                                                           ToaVegConstants.BITMASK_PARAM_DESCRIPTION,
                                                           ToaVegConstants.BITMASK_PARAM_DEFAULT);
        }
        return _toaVegInstance;
    }

    /**
     * Creates a new reference to an input product for the current processing request.
     *
     * @param url        the input product's URL, must not be <code>null</code>
     * @param fileFormat the file format, can be <code>null</code> if not known
     * @param typeId     the product type identifier, can be <code>null</code> if not known
     * @throws java.lang.IllegalArgumentException
     *          if <code>url</code> is <code>null</code>
     * @throws org.esa.beam.framework.processor.RequestElementFactoryException
     *          if the element could not be created
     */
    public ProductRef createInputProductRef(File file, String fileFormat, String typeId) throws RequestElementFactoryException {
        return _defaultFactory.createInputProductRef(file, fileFormat, typeId);
    }

    /**
     * Creates a new reference to an output product for the current processing request.
     *
     * @param url        the output product's URL, must not be <code>null</code>
     * @param fileFormat the file format, can be <code>null</code> if not known
     * @param typeId     the product type identifier, can be <code>null</code> if not known
     * @throws java.lang.IllegalArgumentException
     *          if <code>url</code> is <code>null</code>
     * @throws org.esa.beam.framework.processor.RequestElementFactoryException
     *          if the element could not be created
     */
    public ProductRef createOutputProductRef(File file, String fileFormat, String typeId) throws RequestElementFactoryException {
        return _defaultFactory.createOutputProductRef(file, fileFormat, typeId);
    }

    /**
     * Creates a new processing parameter for the current processing request.
     *
     * @param name  the parameter name, must not be <code>null</code> or empty
     * @param value the parameter value, can be <code>null</code> if yet not known
     * @throws java.lang.IllegalArgumentException
     *          if <code>name</code> is <code>null</code> or empty
     * @throws org.esa.beam.framework.processor.RequestElementFactoryException
     *          if the parameter could not be created or is invalid
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

    public Parameter createDefaultInputProductParameter() {
        return _defaultFactory.createDefaultInputProductParameter();
    }

    public Parameter createDefaultOutputProductParameter() {
        final Parameter outputProductParameter = _defaultFactory.createDefaultOutputProductParameter();
        outputProductParameter.setValue(new File(SystemUtils.getUserHomeDir(), _defaultOutputFileName), null);
        return outputProductParameter;
    }

    public Parameter createDefaultLogPatternParameter(String s) {
        return _defaultFactory.createDefaultLogPatternParameter(s);
    }

    public Parameter createLogToOutputParameter(String s) throws ParamValidateException {
        return _defaultFactory.createLogToOutputParameter(s);
    }

    /**
     * Creates a parameter with the name passed in by searching the parameter properties for the given name. Once found,
     * creates the parameter according to the properties and initializes it with the default value.
     *
     * @throws java.lang.IllegalArgumentException
     *          when the parameter name is not specified as valid name
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


    private VegRequestElementFactory(String defaultOutputFileName,
                                     String bitmask_name,
                                     String bitmask_label,
                                     String bitmask_descr,
                                     String bitmask_default) {
        _defaultFactory = DefaultRequestElementFactory.getInstance();
        _paramInfoMap = new HashMap<String,ParamProperties>();
        _defaultOutputFileName = defaultOutputFileName;
        fillParamInfoMapBitmask(bitmask_name,
                                bitmask_label,
                                bitmask_descr,
                                bitmask_default);
    }

    /*
     * Initializes the parameter information map with all parameter information we have available.
     */
//  private void fillParamInfoMapUncertainty(String uncert_name, String uncert_label, String uncert_descr, Boolean uncert_default) {
//      _paramInfoMap.put(uncert_name, createUncertaintyInfo(uncert_label, uncert_descr, uncert_default));
//  }

    /**
     * Initializes the parameter information map with all parameter information we have available.
     */
    private void fillParamInfoMapBitmask(String bitmask_name, String bitmask_label, String bitmask_descr, String bitmask_default) {
        _paramInfoMap.put(bitmask_name, createBitmaskInfo(bitmask_label, bitmask_descr, bitmask_default));
    }

    /**
     * Gets a default <code>ParamProperties</code> for the parameter with given name.
     *
     * @param name the parameter name
     * @throws java.lang.IllegalArgumentException
     *          when the parameter name is not specified as valid name
     */
    private ParamProperties getParamProperties(String name) throws IllegalArgumentException {
        ParamProperties paramProps = (ParamProperties) _paramInfoMap.get(name);
        if (paramProps == null) {
            throw new IllegalArgumentException("Invalid parameter name: '" + name + "'");
        }
        return paramProps;
    }

    /**
     * Creates the parameter properties for the parameter bitmask.
     *
     * @return
     */
    private ParamProperties createBitmaskInfo(String bitmask_label, String bitmask_descr, String bitmask_default) {
        ParamProperties props = _defaultFactory.createBitmaskParamProperties();

        props.setLabel(bitmask_label);
        props.setDescription(bitmask_descr);
        props.setDefaultValue(bitmask_default);

        return props;
    }


}
