/*
 * $Id: AuxFilePropsLoader.java,v 1.1.1.1 2005/02/15 11:13:35 meris Exp $
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
package org.esa.beam.processor.baer.auxdata;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.esa.beam.processor.baer.BaerConstants;
import org.esa.beam.util.Guardian;

public class AuxFilePropsLoader extends AuxFileLoader {

    protected Properties _props;
    protected double[] _coeffs;

    /**
     * Retrieves the version string of the aux file - or null if no version is present
     * @return
     */
    public String getVersionString() {
        return _props.getProperty(BaerConstants.AUX_VERSION_KEY);
    }

    /**
     * Retrieves the description of the aux file - or null if no description is present
     * @return
     */
    public String getDescription() {
        return _props.getProperty(BaerConstants.AUX_DESCRIPTION_KEY);
    }

    /**
     * Retrieves the coefficients of the loader
     * @return
     */
    public double[] getCoeffs() {
        return _coeffs;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructs the object with default parameters
     */
    protected AuxFilePropsLoader() {
        _props = new Properties();
    }

    /**
     * Loads the properties file
     * @param auxPath
     * @throws IOException
     */
    protected void loadFromPropertiesFile(String auxPath) throws IOException {
        Guardian.assertNotNull("auxPath", auxPath);

        InputStream inStream = new FileInputStream(new File(auxPath));
        _props.clear();
        _props.load(inStream);
        inStream.close();
    }

    /**
     * Logs the version and desccription from the properties
     */
    protected void logVersionFromProperties() {
        String temp = _props.getProperty(BaerConstants.AUX_VERSION_KEY);
        if (temp != null) {
            _logger.fine("... version: '" + temp + "'");
        }

        temp = _props.getProperty(BaerConstants.AUX_DESCRIPTION_KEY);
        if (temp != null) {
            _logger.fine("... description: '" + temp + "'");
        }
    }
}
