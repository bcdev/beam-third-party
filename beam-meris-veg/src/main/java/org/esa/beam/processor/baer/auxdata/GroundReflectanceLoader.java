/*
 * $Id: GroundReflectanceLoader.java,v 1.1.1.1 2005/02/15 11:13:36 meris Exp $
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
import java.util.HashMap;
import java.net.URL;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.esa.beam.processor.baer.BaerConstants;

public class GroundReflectanceLoader extends AuxFileLoader implements GroundReflectanceAccess {

    private HashMap<String,Spectrum> _spectra;
    private String _filePath;
    private String _version;
    private String _description;
    private Spectrum _currentSpectrum;

    private boolean _listStarted;
    private boolean _spectrumStarted;

    /**
     * Constructs the object with default parameters
     */
    public GroundReflectanceLoader() {
        _spectra = new HashMap<String,Spectrum>();
    }

    /**
     * Retrieves the spectrum with the given name
     * @param name
     * @return the spectrum as array of 15 doubles (for bands 1 - 15)
     * @throws ProcessorException if the spectrum does not exist
     */
    public Spectrum getSpectrum(String name) throws ProcessorException {
        Spectrum spRet = null;

        spRet = (Spectrum) _spectra.get(name);
        if (spRet == null) {
            throw new ProcessorException("Invalid ground reflectance spectrum '" + name + "'");
        }

        return spRet;
    }

    /**
     * Retrieves the number of spectra loaded.
     */
    public int getNumSpectra() {
        return _spectra.size();
    }

    /**
     * Retrieves the version string of the aux file - or null if no version is present
     * @return
     */
    public String getVersionString() {
        return _version;
    }

    /**
     * Retrieves the description of the aux file - or null if no description is present
     * @return
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Loads the aux data file associated to the ground reflectances.
     */
    public void load(String auxFilePath) throws IOException {
        Guardian.assertNotNull("auxPath", auxFilePath);

        _logger.info("Reading auxiliary data file: '" + auxFilePath + "'");

        _filePath = auxFilePath;

        try {
            parseFile(new URL("file", "", auxFilePath));
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        }

        String[] names = (String[]) _spectra.keySet().toArray(new String[_spectra.size()]);
        for (int n = 0; n < names.length; n++) {
            _logger.fine("... read spectrum '" + names[n] + "'");
        }

        _logger.info("... success");
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private void parseFile(URL defFile) throws ParserConfigurationException, SAXException, IOException {
        // clear everything eventually in vector
        _spectra.clear();
        resetStateVariables();

        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();

        // Parse the input
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(defFile.openStream(), new SpectrumHandler());
    }

    /**
     * Sets the parser state variables to their initial state.
     */
    private void resetStateVariables() {
        _listStarted = false;
        _spectrumStarted = false;
    }

    /**
     * Callback invoked from SAX parser when an object starts.
     * @param nameSpaceURI
     * @param sName simple (local) name
     * @param qName qualified name
     * @param attrs the element attributes
     */
    private void createElement(String nameSpaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        if (qName.equalsIgnoreCase(BaerConstants.GND_REFL_BAND_TAG)) {
            addBand(attrs);
        } else if (qName.equalsIgnoreCase(BaerConstants.GND_REFL_TAG)) {
            startSpectrum(attrs);
        } else if (qName.equalsIgnoreCase(BaerConstants.GND_REFL_LIST_TAG)) {
            startReflList(attrs);
        }
    }

    /**
     * Callback invoked from SAX parser when an object is finished.
     * @param nameSpaceURI
     * @param sName simple (local) name
     * @param qName qualified name
     */
    private void finishElement(String nameSpaceURI, String sName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(BaerConstants.GND_REFL_TAG)) {
            endSpectrum();
        } else if (qName.equalsIgnoreCase(BaerConstants.GND_REFL_LIST_TAG)) {
            endReflList();
        }
    }

    /**
     * Starts a new ground reflectance list.
     * @param attrs the list attributes
     */
    private void startReflList(Attributes attrs) throws SAXException {
        if (_listStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }

        _version = null;
        _description = null;

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
     * Finishes a ground reflectance list.
     */
    private void endReflList() throws SAXException {
        if (!_listStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }
        _listStarted = false;
    }

    /**
     * Starts a new spectrum.
     * @param attrs the attributes of the spectrum
     */
    private void startSpectrum(Attributes attrs) throws SAXException {
        if (_spectrumStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }

        _currentSpectrum = new Spectrum();

        String shortName = null;
        String description = null;
        String ground = null;

        // parse the attributes for the data needed
        // ----------------------------------------
        for (int n = 0; n < attrs.getLength(); n++) {
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.GND_REFL_NAME_KEY)) {
                shortName = attrs.getValue(n);
                continue;
            }
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.GND_REFL_DESCRIPTION_KEY)) {
                description = attrs.getValue(n);
                continue;
            }
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.GND_REFL_GROUND_KEY)) {
                ground = attrs.getValue(n);
                continue;
            }
        }

        // check parameter values
        // ----------------------
        if (shortName == null) {
            throw new SAXException("Malformatted aux data file. Missing spectrum name.");
        }
        if (description == null) {
            throw new SAXException("Malformatted aux data file. Missing spectrum description.");
        }
        if (ground == null) {
            throw new SAXException("Malformatted aux data file. Missing spectrum ground type.");
        }
        if (!ground.equalsIgnoreCase(BaerConstants.GND_REFL_VEG_TYPE) &&
                !ground.equalsIgnoreCase(BaerConstants.GND_REFL_SOIL_TYPE)) {
            throw new SAXException("Malformatted aux data file. Invalid spectrum ground type '" + ground + "'");
        }

        // check if duplicate spectrum name
        if (null != _spectra.get(shortName)) {
            throw new SAXException("Malformatted aux data file. Duplicate spectrum name '" + shortName + "'");
        }

        _currentSpectrum.setShortName(shortName);
        _currentSpectrum.setDescription(description);
        _currentSpectrum.setGroundType(ground);

        _spectrumStarted = true;
    }

    /**
     * Finishes a spectrum.
     */
    private void endSpectrum() throws SAXException {
        if (!_spectrumStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }
        _spectra.put(_currentSpectrum.getShortName(), _currentSpectrum);
        _spectrumStarted = false;
    }

    /**
     * Adds a band to the spectrum.
     * @param attrs
     */
    private void addBand(Attributes attrs) throws SAXException {
        if (!_spectrumStarted) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }

        String index = null;
        String value = null;

        // parse the attributes for the data needed
        // ----------------------------------------
        for (int n = 0; n < attrs.getLength(); n++) {
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.GND_REFL_INDEX_KEY)) {
                index = attrs.getValue(n);
                continue;
            }
            if (attrs.getQName(n).equalsIgnoreCase(BaerConstants.GND_REFL_REFLEC_KEY)) {
                value = attrs.getValue(n);
                continue;
            }
        }

        // check attribute values
        if ((index == null) || (value == null)) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }

        int idx;
        double reflec;

        try {
            idx = Integer.parseInt(index);
            reflec = Double.parseDouble(value);
            _currentSpectrum.setValueAt(idx - 1, reflec);
        } catch (NumberFormatException e) {
            throw new SAXException("Malformatted aux data file. Please check '" + _filePath + "'");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// INTERNAL CLASS
    ///////////////////////////////////////////////////////////////////////////

    class SpectrumHandler extends DefaultHandler {

        /**
         * Callback from parser. Is invoked each time the parser encounters a new element.
         *
         * @param nameSpaceURI
         * @param sName simple (local) name
         * @param qName qualified name
         * @param attrs the element attributes
         */
        public void startElement(String nameSpaceURI, String sName, String qName, Attributes attrs) throws SAXException {
            createElement(nameSpaceURI, sName, qName, attrs);
        }

        /**
         * Callback from parser. Is invoked each time the parser finishes an element.
         *
         * @param nameSpaceURI
         * @param sName simple (local) name
         * @param qName qualified name
         */
        public void endElement(String nameSpaceURI, String sName, String qName) throws SAXException {
            finishElement(nameSpaceURI, sName, qName);
        }
    }
}
