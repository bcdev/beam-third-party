/*
 * $Id: FaparRequestElementFactory.java,v 1.4 2007/12/11 15:56:31 andreio Exp $
 * Written by: Ophelie Aussedat, September, 2004
 * 
 * Copyright (C) 2004 by STARS
 * 
 *      Academic users:
 *       Are authorized to use this code for research and teaching,
 *       but must acknowledge the use of these routines explicitly
 *       and refer to the references in any publication or work.
 *       Original, complete, unmodified versions of these codes may be
 *       distributed free of charge to colleagues involved in similar
 *       activities.  Recipients must also agree with and abide by the
 *       same rules. The code may not be sold, nor distributed to
 *       commercial parties, under any circumstances.
 * 
 *      Commercial and other users:
 *       Use of this code in commercial applications is strictly
 *       forbidden without the written approval of the authors.
 *       Even with such authorization the code may not be distributed
 *       or sold to any other commercial or business partners under
 *       any circumstances.
 * 
 *  This software is provided as is without any warranty whatsoever.
 * 
 * REFERENCES:
 *       N. Gobron, M. Taberner, B. Pinty, F. Melin, M. M. Verstraete and J.-L.
 *       Widlowski (2002) 'MERIS Land Algorithm: preliminary results', in
 *       Proceedings of the ENVISAT Validation Workshop, Frascati, Italy, 09-13
 *       December, 2002, European Space Agency, SP 531
 * 
 *       N. Gobron, B. Pinty, M. M. Verstraete and M. Taberner (2002) 'Medium
 *       Resolution Imaging Spectrometer (MERIS) - Level 2 Land Surface Products
 *       - Algorithm Theoretical Basis Document, Institute for Environment and
 *       Sustainability, *EUR Report No. 20143 EN*, 19 pp
 * 
 *       Gobron, N., B. Pinty, M. M. Verstraete and M. Taberner (2002) 'Medium
 *       Resolution Imaging Spectrometer (MERIS) - An optimized FAPAR Algorithm -
 *       Theoretical Basis Document, Institute for Environment and
 *       Sustainability, *EUR Report No. 20149 EN*, 19 pp
*/
package it.jrc.beam.fapar;

import org.esa.beam.framework.dataio.ProductIOPlugInManager;
import org.esa.beam.framework.param.ParamProperties;
import org.esa.beam.framework.param.ParamValidateException;
import org.esa.beam.framework.param.Parameter;
import org.esa.beam.framework.processor.DefaultRequestElementFactory;
import org.esa.beam.framework.processor.ProcessorConstants;
import org.esa.beam.framework.processor.ProductRef;
import org.esa.beam.framework.processor.RequestElementFactory;
import org.esa.beam.framework.processor.RequestElementFactoryException;
import org.esa.beam.util.StringUtils;

import java.io.File;

/**
 * Responsible for creating the elements for a processing request used by the FAPAR Processor.
 *
 */
public class FaparRequestElementFactory implements RequestElementFactory {

    // singleton instance
    private static FaparRequestElementFactory _factory;

    private DefaultRequestElementFactory _defaultFactory;

    /**
     * Singleton interface - creates the one and only instance of this factory.
     *
     * @return a reference to the factory
     */
    public static FaparRequestElementFactory getInstance() {
        if (_factory == null) {
            _factory = new FaparRequestElementFactory();
        }
        return _factory;
    }

    /**
     * Creates an output format parameter.
     */
    public Parameter createOutputFormatParameter() {
        ProductIOPlugInManager instance = ProductIOPlugInManager.getInstance();
        String[] formats = instance.getAllProductWriterFormatStrings();
	// createStringParamProperties default
        ParamProperties paramProperties = _defaultFactory.createStringParamProperties();
	// set the default value output format defined in Fapar Processor
        if (formats.length > 0) {
            if (StringUtils.contains(formats, FaparProcessor.DEFAULT_PRODUCT_OUTPUT_FORMAT)) {
                paramProperties.setDefaultValue(FaparProcessor.DEFAULT_PRODUCT_OUTPUT_FORMAT);
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

	/**
	 * Creates a parameter with the specified name and value by asking the Request Factory.
	 */
    public Parameter createParameter(String name, String value) throws RequestElementFactoryException {
        return _defaultFactory.createParameter(name, value);
    }

    /**
     * Creates a new reference to an input product for the current processing request.
     *
     * @param file       the input product's file, must not be <code>null</code>
     * @param fileFormat the file format, can be <code>null</code> if not known
     * @param typeId     the product type identifier, can be <code>null</code> if not known
     *
     * @throws IllegalArgumentException       if <code>file</code> is <code>null</code>
     * @throws RequestElementFactoryException if the element could not be created
     */
    public ProductRef createInputProductRef(File file, String fileFormat, String typeId) throws RequestElementFactoryException {
        return _defaultFactory.createInputProductRef(file, fileFormat, typeId);
    }

    /**
     * Creates a new reference to an output product for the current processing request.
     *
     * @param file       the output product's file, must not be <code>null</code>
     * @param fileFormat the file format, can be <code>null</code> if not known
     * @param typeId     the product type identifier, can be <code>null</code> if not known
     *
     * @throws IllegalArgumentException       if <code>file</code> is <code>null</code>
     * @throws RequestElementFactoryException if the element could not be created
     */
    public ProductRef createOutputProductRef(File file, String fileFormat, String typeId) throws RequestElementFactoryException {
        return _defaultFactory.createOutputProductRef(file, fileFormat, typeId);
    }

    /**
     * Creates a parameter for the default input product path - which is the current user's home directory.
     */
    public Parameter createDefaultInputProductParameter() {
        return _defaultFactory.createDefaultInputProductParameter();
    }

    /**
     * Creates an output product parameter set to the default path.
     */
    public Parameter createDefaultOutputProductParameter() {
        Parameter defaultOutputProductParameter = _defaultFactory.createDefaultOutputProductParameter();
        ParamProperties properties = defaultOutputProductParameter.getProperties();
        Object defaultValue = properties.getDefaultValue();
        if (defaultValue instanceof File) {
            File file = (File) defaultValue;
            file = new File(file, FaparProcessor.DEFAULT_OUTPUT_DIR_NAME);
            properties.setDefaultValue(new File(file, FaparProcessor.DEFAULT_OUTPUT_PRODUCT_NAME));
        }
        defaultOutputProductParameter.setDefaultValue();
        return defaultOutputProductParameter;
    }

    /**
     * Creates a default logging pattern parameter set to the prefix passed in.
     *
     * @param prefix the default setting for the logging pattern
     *
     * @return a logging pattern Parameter conforming the system settings
     */
    public Parameter createDefaultLogPatternParameter(String prefix) {
        return _defaultFactory.createDefaultLogPatternParameter(prefix);
    }

    /**
     * Creates a logging to output product parameter set to true.
     */
    public Parameter createLogToOutputParameter(String value) throws ParamValidateException {
        return _defaultFactory.createLogToOutputParameter(value);
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructs the object.
     */
    private FaparRequestElementFactory() {
        _defaultFactory = DefaultRequestElementFactory.getInstance();
    }
}
