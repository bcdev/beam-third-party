/*
 * $Id: RelAerPhaseLoader.java,v 1.2 2006/03/21 17:22:29 meris Exp $
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

public class RelAerPhaseLoader extends AuxFilePropsLoader implements RelAerPhaseAccess {

    /**
     * Constructs the object with default parameters.
     */
    public RelAerPhaseLoader() {
        _coeffs = new double[3];
    }

    /**
     * Loads the auxiliary data file for the relative aerosol phase coefficients
     * @param auxPath
     */
    public void load(String auxPath) throws IOException {
        _logger.info("Reading auxiliary data file: '" + auxPath + "'");

        loadFromPropertiesFile(auxPath);

        // log information
        // ---------------
        logVersionFromProperties();

        String valueString;
        String keyString;
        for (int n = 0; n < BaerConstants.AUX_NUM_REL_PHASE_COEFFS; n++) {
            keyString = BaerConstants.AUX_REL_PHASE_KEY_STUB + n;
            valueString = _props.getProperty(keyString);
            if (valueString == null) {
                throw new IOException("Corrupted auxiliary data file:" + auxPath + "'");
            }

            _coeffs[n] = Double.parseDouble(valueString);
            _logger.fine("... " + keyString + ": '" + valueString + "'");
        }

        _logger.info("... success");
    }

    /**
     * Retrieves the relative aerosol phase function coefficients. The array
     * contains three coefficients:
     *  a[0] - offset
     *  a[1] - linear term
     *  a[2] - quadratic term
     * @return the coefficients array
     */
    public double[] getRelativeAerosolPhaseCoefficients() {
        return _coeffs;
    }
}
