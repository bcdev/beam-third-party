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
     * @throws org.esa.beam.framework.processor.RequestElementFactoryException if the element could not be created
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
     * @throws org.esa.beam.framework.processor.RequestElementFactoryException if the element could not be created
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
