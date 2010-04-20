/*
 * $Id: NdviLoader.java,v 1.1.1.1 2005/02/15 11:13:36 meris Exp $
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

import java.io.IOException;
import org.esa.beam.processor.baer.BaerConstants;

public class NdviLoader extends AuxFilePropsLoader implements NdviAccess {

    /**
     * Creates the class with default parameters
     */
    public NdviLoader() {
        _coeffs = new double[1];
        _coeffs[0] = BaerConstants.AUX_NDVI_DEFAULT;
    }

    /**
     * Loads the auxiliary file for ndvi tuning.
     * @param auxPath the path to the aux file
     */
    public void load(String auxPath) throws IOException {
        _logger.info("Reading auxiliary data file: '" + auxPath + "'");

        loadFromPropertiesFile(auxPath);

        // log information
        // ---------------
        logVersionFromProperties();

        String temp = _props.getProperty(BaerConstants.AUX_NDVI_KEY);
        if (temp == null) {
            _logger.warning("No ndvi value found in auxiliary file '" + auxPath + "'");
            _logger.warning("Using default value for ndvi: '" + BaerConstants.AUX_NDVI_DEFAULT);
            _coeffs[0] = BaerConstants.AUX_NDVI_DEFAULT;
        } else {
            _coeffs[0] = Double.parseDouble(temp);
            _logger.fine("... ndvi: '" + _coeffs[0] + "'");
        }

        _logger.info("... success");
    }

    /**
     * Retrieves the ndvi tuning factor aux data.
     * @return the tuning factor
     */
    public double getNdviTuningFactor() {
        return _coeffs[0];
    }
}
