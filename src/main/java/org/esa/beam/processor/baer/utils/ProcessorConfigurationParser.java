/*
 * $Id: ProcessorConfigurationParser.java,v 1.5 2006/03/27 15:16:42 meris Exp $
 *
 * Copyright (C) 2002,2003  by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.esa.beam.processor.baer.utils;

import java.net.URL;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.util.Guardian;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.esa.beam.processor.baer.BaerConstants;

public class ProcessorConfigurationParser {

    private ProcessorConfiguration _configToBuild;
    private ProcessorConfiguration _config;
    private boolean _ndviRead;
    private boolean _aerPhaseRead;
    private boolean _relAerPhaseRead;
    private boolean _groundReflecRead;
    private boolean _soilFractionRead;
    private boolean _f_TuningRead;
    private boolean _aerDiffTransmRead;
    private boolean _hemisphReflecRead;

    private File _auxdataPath;
    
    /**
     * Constructs the object with default parameters
     */
    public ProcessorConfigurationParser() {
        _configToBuild = null;
        _ndviRead = false;
        _aerPhaseRead = false;
        _relAerPhaseRead = false;
        _groundReflecRead = false;
        _soilFractionRead = false;
        _f_TuningRead = false;
        _aerDiffTransmRead = false;
        _hemisphReflecRead = false;
    }

    /**
     * Parses the configuration file passed in as path.
     * @param configPath the configuration file absolute path
     * @throws ProcessorException on failures
     */ 
    public void parseConfigurationFile(URL configPath, File auxdataPath) throws ProcessorException {
        Guardian.assertNotNull("configPath", configPath);
        _auxdataPath = auxdataPath;
        
        try {
            parse(configPath);
        } catch (ParserConfigurationException e) {
            throw new ProcessorException(e.getMessage());
        } catch (SAXException e) {
            throw new ProcessorException(e.getMessage());
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage());
        }
    }

    /**
     * Retrieves the last configuration parsed
     * @return
     */
    public ProcessorConfiguration getConfiguration() {
        return _config;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private void parse(URL configPath) throws ParserConfigurationException, SAXException, IOException {
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // Parse the input
        SAXParser saxParser = factory.newSAXParser();
        final InputStream is = configPath.openStream();
        try {
            saxParser.parse(is, new ConfigurationHandler());
        } finally {
            is.close();
        }
    }

    /**
     * Callback invoked by parser when a new element is created.
     * @param nameSpaceURI
     * @param sName
     * @param qName
     * @param attrs
     */
    private void createConfigurationElement(String nameSpaceURI, String sName,
                                            String qName, Attributes attrs) throws SAXException {
        if (qName.equalsIgnoreCase(BaerConstants.PARAMETER_TAG)) {
            parseParameter(attrs);
        } else if (qName.equalsIgnoreCase(BaerConstants.CONFIGURATION_TAG)) {
            startConfiguration();
        }
    }

    /**
     * Callback invoked by parser when an element is finished
     * @param nameSpaceURI
     * @param sName
     * @param qName
     */
    private void finishConfigurationElement(String nameSpaceURI, String sName,
                                            String qName) throws SAXException {
        if (qName.equalsIgnoreCase(BaerConstants.CONFIGURATION_TAG)) {
            endConfiguration();
        }
    }

    /**
     * Starts assembing a configuration object.
     */
    private void startConfiguration() throws SAXException {
        // check, must be null
        if (_configToBuild != null) {
            // this is an error - must be false!!!
            throw new SAXException("Internal error - a configuration parser run was not finished correctly!\n Please restart the processor!");
        }
        _configToBuild = new ProcessorConfiguration();
        _ndviRead = false;
        _aerPhaseRead = false;
        _relAerPhaseRead = false;
        _groundReflecRead = false;
        _soilFractionRead = false;
        _f_TuningRead = false;
        _aerDiffTransmRead = false;
        _hemisphReflecRead = false;
    }

    /**
     * Finishes a configuration
     */
    private void endConfiguration() throws SAXException {
        if (!_ndviRead) {
            throw new SAXException("No ndvi aux data file found in processor configuration");
        }
        if (!_aerPhaseRead) {
            throw new SAXException("No aerosol phase aux data file found in processor configuration");
        }
        if (!_relAerPhaseRead) {
            throw new SAXException("No relative aerosol phase aux data file found in processor configuration");
        }
        if (!_groundReflecRead) {
            throw new SAXException("No ground reflectance aux data file found in processor configuration");
        }
        if (!_soilFractionRead) {
            throw new SAXException("No soil fraction factor aux data file found in processor configuration");
        }
        if (!_f_TuningRead) {
            throw new SAXException("No F tuning factor aux data file found in processor configuration");
        }
        if (!_aerDiffTransmRead) {
            throw new SAXException("No aerosol diffuse transmission aux data file found in processor configuration");
        }
        if (!_hemisphReflecRead) {
            throw new SAXException("No helispherical reflectance aux data file found in processor configuration");
        }
        _config = _configToBuild;
        _configToBuild = null;
    }


    /** Partses a parameter of the processor configuration file
     * @param attrs
     */
    private void parseParameter(Attributes attrs) throws SAXException {
        if (_configToBuild == null) {
            throw new SAXException("Internal error - a configuration parser run was not finished correctly!\n Please restart the processor!");
        }

        String name = null;
        String value = null;

        //File beamAuxdataDir = BaerProcessor.getAuxdataDir();

        // parse the attributes for the data needed
        // ----------------------------------------
        for (int i = 0; i < attrs.getLength(); i++) {
            // get the parameter name - is an attribute named "name"
            if (attrs.getQName(i).equalsIgnoreCase(BaerConstants.ATTRIB_NAME)) {
                name = attrs.getValue(i);
                continue;
            }

            // get the parameter value - is an attribute named "value"
            if (attrs.getQName(i).equalsIgnoreCase(BaerConstants.ATTRIB_VALUE)) {
                value = attrs.getValue(i);
                continue;
            }
        }

        // we must have both values set!
        // -----------------------------
        if ((name == null) || (value == null)) {
            throw new SAXException("Parser error - incomplete parameter definition\n Please check the processor configuration file!");
        }

        value = new File(_auxdataPath, value).getPath();

        // check that we have the correct tags
        // -----------------------------------
        if (name.equalsIgnoreCase(BaerConstants.NDVI_FILE_ATTRIB_NAME)) {
            _configToBuild.setNdviAuxFile(value);
            _ndviRead = true;
        } else if (name.equalsIgnoreCase(BaerConstants.AER_PHASE_FILE_ATTRIB_NAME)) {
            _configToBuild.setAerosolPhaseAuxFile(value);
            _aerPhaseRead = true;
        } else if (name.equalsIgnoreCase(BaerConstants.REL_AER_PHASE_FILE_ATTRIB_NAME)) {
            _configToBuild.setRelativeAerosolPhaseAuxFile(value);
            _relAerPhaseRead = true;
        } else if (name.equalsIgnoreCase(BaerConstants.GROUND_REFLECTANCE_FILE_ATTRIB_NAME)) {
            _configToBuild.setGroundReflectanceAuxFile(value);
            _groundReflecRead = true;
        } else if (name.equalsIgnoreCase(BaerConstants.SOIL_FRACTION_FILE_ATTRIB_NAME)) {
            _configToBuild.setSoilFractionAuxFile(value);
            _soilFractionRead = true;
        } else if (name.equalsIgnoreCase(BaerConstants.F_TUNING_FILE_ATTRIB_NAME)) {
            _configToBuild.setF_TuningAuxFile(value);
            _f_TuningRead = true;
        } else if (name.equalsIgnoreCase(BaerConstants.AER_DIFF_TRANSM_FILE_ATTRIB_NAME)) {
            _configToBuild.setAerDiffTransmAuxFile(value);
            _aerDiffTransmRead = true;
        } else if (name.equalsIgnoreCase(BaerConstants.HEMISPH_REFLEC_FILE_ATTRIB_NAME)) {
            _configToBuild.setHemisphReflecAuxFile(value);
            _hemisphReflecRead = true;
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    /////// INTERNAL CLASS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Callback class for XML parser.
     */
    class ConfigurationHandler extends DefaultHandler {

        /**
         * Callback from parser. Is invoked each time the parser encounters a new element.
         *
         * @param nameSpaceURI
         * @param sName simple (local) name
         * @param qName qualified name
         * @param attrs the element attributes
         */
        public void startElement(String nameSpaceURI, String sName, String qName, Attributes attrs) throws SAXException {
            createConfigurationElement(nameSpaceURI, sName, qName, attrs);
        }

        /**
         * Callback from parser. Is invoked each time the parser finishes an element.
         *
         * @param nameSpaceURI
         * @param sName simple (local) name
         * @param qName qualified name
         */
        public void endElement(String nameSpaceURI, String sName, String qName) throws SAXException {
            finishConfigurationElement(nameSpaceURI, sName, qName);
        }
    }
}
