/*
 * $Id: VegAuxFilePropsLoader.java,v 1.2 2005/04/26 12:10:30 meris Exp $
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
package org.esa.beam.processor.common.auxdata;

import java.util.Properties;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.esa.beam.util.Guardian;

public class VegAuxFilePropsLoader extends VegAuxFileLoader {

    protected Properties _props;
    protected double[] _coeffs;
    protected int _coeff_length;

    /**
     * Retrieves the version string of the aux file - or null if no version is present
     * @return
     */
    public String getVersionString(String aux_version_key) {
        return _props.getProperty(aux_version_key);
    }

    /**
     * Retrieves the description of the aux file - or null if no description is present
     * @return
     */
    public String getDescription(String aux_description_key) {
        return _props.getProperty(aux_description_key);
    }


    /**
     * inits the logger
     * @param logger_name
     */
     public void initLogger(String logger_name){
        _logger = Logger.getLogger(logger_name);
     }


    /**
     * Retrieves the coefficients of the loader
     * @return
     */
    public double[] getCoeffs() {
        return _coeffs;
    }

    /**
     * Loads the given properties file from the path string passed in
     * @param auxPath
     * @throws java.io.IOException if an error occurs
     */
    public void load(String auxPath) throws IOException {
        loadFromPropertiesFile(auxPath);
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructs the object with default parameters
     */
    protected VegAuxFilePropsLoader() {
        _props = new Properties();
    }

    /**
     * Loads the properties file
     * @param auxPath
     * @throws java.io.IOException
     */
    protected void loadFromPropertiesFile(String auxPath) throws IOException {
        Guardian.assertNotNull("auxPath", auxPath);

        InputStream inStream = new FileInputStream(new File(auxPath));
        try {
            _props.clear();
            _props.load(inStream);
        } finally {
            inStream.close();
        }
    }

    /**
     * Logs the version and desccription from the properties
     */
    protected void logVersionFromProperties(String aux_version_key) {
        String temp = _props.getProperty(aux_version_key);
        if (temp != null) {
            _logger.fine("... version: '" + temp + "'");
        }

        temp = _props.getProperty(aux_version_key);
        if (temp != null) {
            _logger.fine("... description: '" + temp + "'");
        }
    }

    /**
     * Loads the double value property from the parameter file.
     * @param keyName name of the aux property
     * @param defVal default value
     * @param filePath path of the aux file
     * @param coeffIdx coefficient index where to store
     */
    protected void loadDoubleProperty(String keyName, double defVal, String filePath, int coeffIdx) {
        String temp = _props.getProperty(keyName);
        if (temp == null) {
            _logger.warning("'" + keyName + "' not found in auxiliary file '" + filePath + "'");
            _logger.warning("Using default value: '" + defVal);
            _coeffs[coeffIdx] = defVal;
        } else {
            _coeffs[coeffIdx] = Double.parseDouble(temp);
            _logger.fine("... '" + keyName + "' : '" + _coeffs[coeffIdx] + "'");
        }
    }
}
