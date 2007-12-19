/*
 * $Id: ToaVegProcessorConfigurationParser.java,v 1.10 2006/03/21 17:33:45 meris Exp $
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
package org.esa.beam.processor.toa.utils;

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
import org.esa.beam.processor.toa.ToaVegConstants;
import org.esa.beam.processor.common.utils.VegProcessorConfiguration;

public class ToaVegProcessorConfigurationParser {

    private VegProcessorConfiguration _config;
    private VegProcessorConfiguration _configToBuild;

    private boolean _inputStatisticsRead;
    private boolean _outputStatisticsRead;
    private boolean _uncertaintyRead;
    private boolean _nnLaiRead;
    private boolean _nnfCoverRead;
    private boolean _nnfAPARRead;
    private boolean _nnLAIxCabRead;

    private File _auxdataPath;

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
            throw new ProcessorException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new ProcessorException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ProcessorException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves the processor configuration currently hold.
     * @return
     */
    public VegProcessorConfiguration getConfiguration() {
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
        if (qName.equalsIgnoreCase(ToaVegConstants.PARAMETER_TAG)) {
            parseParameter(attrs);
        } else if (qName.equalsIgnoreCase(ToaVegConstants.CONFIGURATION_TAG)) {
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
        if (qName.equalsIgnoreCase(ToaVegConstants.CONFIGURATION_TAG)) {
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
        _inputStatisticsRead = false;
        _outputStatisticsRead = false;
        _uncertaintyRead = false;
        _nnLaiRead = false;
        _nnfCoverRead = false;
        _nnfAPARRead = false;
        _nnLAIxCabRead = false;

        _configToBuild = new VegProcessorConfiguration();
    }

    /**
     * Finishes assembling a configuration object.
     */
    private void endConfiguration() throws SAXException {
        if (!_inputStatisticsRead) {
            throw new SAXException("No input statistics aux data file found in processor configuration");
        }
        if (!_outputStatisticsRead) {
            throw new SAXException("No output statistics aux data file found in processor configuration");
        }
        if (!_uncertaintyRead) {
            throw new SAXException("No uncertainty aux data file found in processor configuration");
        }
        if (!_nnLaiRead) {
            throw new SAXException("No LAI neural net aux data file found in processor configuration");
        }
        if (!_nnfCoverRead) {
            throw new SAXException("No fCover neural net aux data file found in processor configuration");
        }
        if (!_nnfAPARRead) {
            throw new SAXException("No fAPAR neural net aux data file found in processor configuration");
        }
        if (!_nnLAIxCabRead) {
            throw new SAXException("No LAIxCab neural net aux data file found in processor configuration");
        }

        _config = _configToBuild;
        _configToBuild = null;
    }

    /**
     * Parses a configuration parameter
     * @param attrs
     */
    private void parseParameter(Attributes attrs) throws SAXException {
        if (_configToBuild == null) {
            throw new SAXException("Internal error - a configuration parser run was not finished correctly!\n Please restart the processor!");
        }

        String name = null;
        String value = null;
        
        // parse the attributes for the data needed
        // ----------------------------------------
        for (int n = 0; n < attrs.getLength(); n++) {
            // get the parameter name - is an attribute named "name"
            if (attrs.getQName(n).equalsIgnoreCase(ToaVegConstants.ATTRIB_NAME)) {
                name = attrs.getValue(n);
                continue;
            }

            // get the parameter value - is an attribute named "value"
            if (attrs.getQName(n).equalsIgnoreCase(ToaVegConstants.ATTRIB_VALUE)) {
                value = attrs.getValue(n);
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
        if (name.equalsIgnoreCase(ToaVegConstants.INPUT_STATISTICS_ATTRIB_NAME)) {
            _configToBuild.setInputStatisticsAuxFile(value);
            _inputStatisticsRead = true;
        } else if (name.equalsIgnoreCase(ToaVegConstants.OUTPUT_STATISTICS_ATTRIB_NAME)) {
            _configToBuild.setOutputStatisticsAuxFile(value);
            _outputStatisticsRead = true;
        } else if (name.equalsIgnoreCase(ToaVegConstants.UNCERTAINTY_ATTRIB_NAME)) {
            _configToBuild.setUncertaintyAuxFile(value);
            _uncertaintyRead = true;
        } else if (name.equalsIgnoreCase(ToaVegConstants.NN_LAI_AUX_KEY)) {
            _configToBuild.setNN_LaiAuxFile(value);
            _nnLaiRead = true;
        } else if (name.equalsIgnoreCase(ToaVegConstants.NN_FCOVER_AUX_KEY)) {
            _configToBuild.setNN_fCoverAuxFile(value);
            _nnfCoverRead = true;
        } else if (name.equalsIgnoreCase(ToaVegConstants.NN_FAPAR_AUX_KEY)) {
            _configToBuild.setNN_fAPARAuxFile(value);
            _nnfAPARRead = true;
        } else if (name.equalsIgnoreCase(ToaVegConstants.NN_LAIXCAB_AUX_KEY)) {
            _configToBuild.setNN_LAIxCabAuxFile(value);
            _nnLAIxCabRead = true;
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
