/*
 * $Id: AerPhaseLoader.java,v 1.1.1.1 2005/02/15 11:13:35 meris Exp $
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
package org.esa.beam.processor.baer.auxdata;

import org.esa.beam.util.Guardian;
import org.esa.beam.framework.processor.ProcessorException;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.esa.beam.processor.baer.BaerConstants;

public class AerPhaseLoader extends AuxFileLoader implements AerPhaseAccess {

    private String _filePath;
    private HashMap<String,AerLut> _luts;
    private String _version;
    private String _description;
    private Vector<String> _lutNames;
    private AerLut _currentLut;
    private AerLut _selectedLut;
    private AerBandParam _currentBand;

    private boolean _listStarted;
    private boolean _lutStarted;
    private boolean _bandStarted;
    private boolean _a0_Set;
    private boolean _a1_Set;
    private boolean _a2_Set;

    /**
     * Constructs the object with default parameters
     */
    public AerPhaseLoader() {
        _luts = new HashMap<String,AerLut>();
        _lutNames = new Vector<String>();
    }

    /**
     * Retrieves the aerosol phase function coefficients for the band with the given index.
     * Band indexing is 1-based i.e. as in the MERIS case.
     * @param bandIdx
     * @return
     */
    public AerBandParam getAerPhase(int bandIdx) {
        AerBandParam bandRet = null;

        if (_selectedLut != null) {
            try {
                bandRet = _selectedLut.getBand(bandIdx);
            } catch (ProcessorException e) {
                _logger.severe("Unable to access LUT." + e.getMessage());
                bandRet = null;
            }
        }
        return bandRet;
    }

    /**
     * Selects a LUT to be used.
     * @param lutName
     * @return true when successful, false if no LUT with the given name exists
     */
    public boolean selectLut(String lutName) {
        boolean bRet = true;
        _selectedLut = (AerLut) _luts.get(lutName);

        if (_selectedLut == null) {
            bRet = false;
        }

        return bRet;
    }

    /**
     * Retrieves the description of the aux file - or null if no description is present
     * @return
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Retrieves the version string of the aux file - or null if no version is present
     * @return
     */
    public String getVersionString() {
        return _version;
    }

    /**
     * Retrieves the names of all LUT's loaded
     * @return
     */
    public String[] getLUTNames() {
        return (String[]) _lutNames.toArray(new String[_lutNames.size()]);
    }

    /**
     * Retrieves the LUT with the given name. Return null when no LUT with
     * the given name exists
     * @param name
     * @return
     */
    public AerLut getLUT(String name) {
        return (AerLut) _luts.get(name);
    }

    /**
     * Loads the aux data file associated to the aerosol phase function coefficients.
     */
    public void load(String auxPath) throws IOException {
        Guardian.assertNotNull("auxPath", auxPath);

        _logger.info("Reading auxiliary data file: '" + auxPath + "'");

        _filePath = auxPath;

        try {
            parseFile(new URL("file", "", auxPath));
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        }

        _logger.info("... success");
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Parses the aerosol phase function auxiliary file.
     * @param defFile
     */
    private void parseFile(URL defFile) throws ParserConfigurationException, SAXException, IOException {
        // clear everything eventually in vector
        _luts.clear();
        _lutNames.clear();
        initStateVariables();

        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();

        // Parse the input
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(defFile.openStream(), new LUTHandler());
    }

    /**
     * Callback invoked from SAX parser when an object starts.
     * @param nameSpaceURI
     * @param sName simple (local) name
     * @param qName qualified name
     * @param attrs the element attributes
     */
    private void createLUTElement(String nameSpaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        if (qName.equalsIgnoreCase(BaerConstants.AER_PARAMETER_TAG)) {
            parseParameter(attrs);
        } else if (qName.equalsIgnoreCase(BaerConstants.AER_BAND_TAG)) {
            startBand(attrs);
        } else if (qName.equalsIgnoreCase(BaerConstants.AER_LUT_TAG)) {
            startLut(attrs);
        } else if (qName.equalsIgnoreCase(BaerConstants.AER_LUT_LIST_TAG)) {
            startAerLutList(attrs);
        }
    }

    /**
     * Callback invoked from SAX parser when an object is finished.
     * @param nameSpaceURI
     * @param sName simple (local) name
     * @param qName qualified name
     */
    private void finishLUTElement(String nameSpaceURI, String sName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(BaerConstants.AER_BAND_TAG)) {
            endBand();
        } else if (qName.equalsIgnoreCase(BaerConstants.AER_LUT_TAG)) {
            endLut();
        } else if (qName.equalsIgnoreCase(BaerConstants.AER_LUT_LIST_TAG)) {
            endtAerLutList();
        }
    }


    /**
     * Starts a new parser run on a LUT list. Extracts version and description of the list, if possible.
     * @param attrs
     */
    private void startAerLutList(Attributes attrs) throws SAXException {
        if (_listStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }

        // parse the attributes for the data needed
        // ----------------------------------------
        for (int n = 0; n < attrs.getLength(); n++) {
            // get the parameter name - is an attribute named "name"
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.AUX_VERSION_KEY)) {
                _version = attrs.getValue(n);
                continue;
            }

            // get the parameter value - is an attribute named "value"
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.AUX_DESCRIPTION_KEY)) {
                _description = attrs.getValue(n);
                continue;
            }
        }

        if (_version != null) {
            _logger.fine("... version: '" + _version + "'");
        }
        if (_description != null) {
            _logger.fine("... description: '" + _description + "'");
        }

        _listStarted = true;
    }

    /**
     * Ends the parsing of the LUT list
     */
    private void endtAerLutList() throws SAXException {
        if (!_listStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }
        _listStarted = false;
    }

    /**
     * Starts parsing of a LUT
     * @param attrs
     */
    private void startLut(Attributes attrs) throws SAXException {
        if (_lutStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }

        String lutName = null;
        String numBandsString = null;
        // parse the attributes for the data needed
        // ----------------------------------------
        for (int n = 0; n < attrs.getLength(); n++) {
            // get the parameter name - is an attribute named "name"
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.AUX_LUT_NAME_KEY)) {
                lutName = attrs.getValue(n);
                continue;
            }

            // get the parameter value - is an attribute named "value"
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.AUX_LUT_NUM_BANDS_KEY)) {
                numBandsString = attrs.getValue(n);
                continue;
            }
        }
        if ((lutName == null) || (numBandsString == null)) {
            throw new SAXException("Malformatted aux data file - missing LUT parameter. Please check '" + _filePath + "'");
        }

        int numBands;

        try {
            numBands = Integer.parseInt(numBandsString);
        } catch (NumberFormatException e) {
            throw new SAXException("Malformatted aux data file - Invalid number of bands: '" + numBandsString + "'. Please check '" + _filePath + "'");
        }

        _currentLut = new AerLut(numBands);
        _currentLut.setName(lutName);

        _logger.fine("... LUT: '" + lutName + "'");

        _lutStarted = true;
    }

    /**
     * Finishes parsing of a LUT
     */
    private void endLut() throws SAXException {
        if (!_lutStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }
        _luts.put(_currentLut.getName(), _currentLut);
        _lutNames.add(_currentLut.getName());
        _currentLut = null;

        _lutStarted = false;
    }

    /**
     * Starts parsing of a band.
     * @param attrs
     */
    private void startBand(Attributes attrs) throws SAXException {
        if (_bandStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }

        String bandName = null;
        // parse the attributes for the data needed
        // ----------------------------------------
        for (int n = 0; n < attrs.getLength(); n++) {
            // get the parameter name - is an attribute named "name"
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.AUX_LUT_NAME_KEY)) {
                bandName = attrs.getValue(n);
                break;
            }
        }

        if (bandName == null) {
            throw new SAXException("Malformatted aux data file - missing band name. Please check '" + _filePath + "'");
        }

        _currentBand = new AerBandParam();
        _currentBand.setName(bandName);

        _a0_Set = false;
        _a1_Set = false;
        _a2_Set = false;

        _bandStarted = true;
    }

    /**
     * Finishes parsing of a band
     */
    private void endBand() throws SAXException {
        if (!_bandStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }

        if (!(_a0_Set && _a1_Set && _a2_Set)) {
            throw new SAXException("Malformatted aux data file - LUT coefficient missing. Please check '" + _filePath + "'");
        }

        try {
            _currentBand.validate(true);
            _currentLut.addBand(Integer.parseInt(_currentBand.getName()), _currentBand);
        } catch (ProcessorException e) {
            throw new SAXException(e.getMessage());
        } catch (NumberFormatException e) {
            throw new SAXException(e.getMessage());
        }
        _bandStarted = false;
    }

    /**
     * Parses a single parameter
     * @param attrs
     */
    private void parseParameter(Attributes attrs) throws SAXException {
        if (!_bandStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }

        String name = null;
        String value = null;
        // parse the attributes for the data needed
        // ----------------------------------------
        for (int n = 0; n < attrs.getLength(); n++) {
            // get the parameter name - is an attribute named "name"
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.AUX_LUT_NAME_KEY)) {
                name = attrs.getValue(n);
                continue;
            }

            // get the parameter value - is an attribute named "value"
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.AUX_LUT_VALUE_KEY)) {
                value = attrs.getValue(n);
                continue;
            }
        }

        if ((name == null) || (value == null)) {
            throw new SAXException("Malformatted aux data file - parameter name or vakue missing. Please check '" + _filePath + "'");
        }
        double paramVal;

        try {
            paramVal = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new SAXException(e.getMessage());
        }

        if (name.equalsIgnoreCase("a.0")) {
            _currentBand.setA0(paramVal);
            _a0_Set = true;
        } else if (name.equalsIgnoreCase("a.1")) {
            _currentBand.setA1(paramVal);
            _a1_Set = true;
        } else if (name.equalsIgnoreCase("a.2")) {
            _currentBand.setA2(paramVal);
            _a2_Set = true;
        } else {
            throw new SAXException("Malformatted aux data file - parameter missing. Please check '" + _filePath + "'");
        }
    }

    /**
     * Initializes all state variables for a new parser run
     */
    private void initStateVariables() {
        _listStarted = false;
        _lutStarted = false;
        _bandStarted = false;

        _version = BaerConstants.AUX_VAL_UNKNOWN;
        _description = BaerConstants.AUX_VAL_NONE;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// INTERNAL CLASS
    ///////////////////////////////////////////////////////////////////////////

    class LUTHandler extends DefaultHandler {

        /**
         * Callback from parser. Is invoked each time the parser encounters a new element.
         *
         * @param nameSpaceURI
         * @param sName simple (local) name
         * @param qName qualified name
         * @param attrs the element attributes
         */
        public void startElement(String nameSpaceURI, String sName, String qName, Attributes attrs) throws SAXException {
            createLUTElement(nameSpaceURI, sName, qName, attrs);
        }

        /**
         * Callback from parser. Is invoked each time the parser finishes an element.
         *
         * @param nameSpaceURI
         * @param sName simple (local) name
         * @param qName qualified name
         */
        public void endElement(String nameSpaceURI, String sName, String qName) throws SAXException {
            finishLUTElement(nameSpaceURI, sName, qName);
        }
    }
}
