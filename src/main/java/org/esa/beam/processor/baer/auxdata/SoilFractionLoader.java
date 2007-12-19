/*
 * $Id: SoilFractionLoader.java,v 1.1.1.1 2005/02/15 11:13:36 meris Exp $
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

import java.io.IOException;
import org.esa.beam.processor.baer.BaerConstants;

public class SoilFractionLoader extends AuxFilePropsLoader implements SoilFractionAccess {

    /**
     * Creates the object with default parameters.
     */
    public SoilFractionLoader() {
        _coeffs = new double[1];
        _coeffs[0] = BaerConstants.AUX_SOIL_FRACTION_DEFAULT;
    }

    /**
     * Retrieves the soil fraction factor as loaded from the aux data
     * @return the soil fraction factor
     */
    public double getSoilFraction() {
        return _coeffs[0];
    }

    /**
     * Loads the auxiliary file for soil fraction factor.
     * @param auxPath the path to the aux file
     */
    public void load(String auxPath) throws IOException {
        _logger.info("Reading auxiliary data file: '" + auxPath + "'");

        loadFromPropertiesFile(auxPath);

        // log information
        // ---------------
        logVersionFromProperties();

        String temp = _props.getProperty(BaerConstants.AUX_SOIL_FRACTION_KEY);
        if (temp == null) {
            _logger.warning("No soil fraction factor value found in auxiliary file '" + auxPath + "'");
            _logger.warning("Using default value for soil fraction factor: '" + BaerConstants.AUX_SOIL_FRACTION_DEFAULT);
            _coeffs[0] = BaerConstants.AUX_SOIL_FRACTION_DEFAULT;
        } else {
            _coeffs[0] = Double.parseDouble(temp);
            _logger.fine("... soil fraction factor: '" + _coeffs[0] + "'");
        }

        _logger.info("... success");
    }
}
