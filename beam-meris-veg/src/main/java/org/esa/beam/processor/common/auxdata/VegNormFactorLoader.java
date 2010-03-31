/*
 * $Id: VegNormFactorLoader.java,v 1.1.1.1 2005/02/15 11:13:40 meris Exp $
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


import org.esa.beam.processor.common.auxdata.VegAuxFilePropsLoader;
import org.esa.beam.processor.common.auxdata.VegNormFactorAccess;

import java.io.IOException;

public class VegNormFactorLoader extends VegAuxFilePropsLoader implements VegNormFactorAccess {

    public static final String NORMALISATION_FACTOR_AUX_KEY = "norm_factor";
    public static double _default_value;

    /**
     * Constructs the object with default parameters.
     */
    public VegNormFactorLoader(double value, String logger_name) {
        _coeffs = new double[1];
        _coeffs[0] = value; //NORMALISATION_FACTOR_DEFAULT;
        setNormalisationFactorDefault(value);
        initLogger(logger_name);
    }


    /**
     * Set default value of normalisation factor
     * @param value
     */
    public void setNormalisationFactorDefault(double value){
        _default_value = value;
    }

    /**
     * Retrieves the normalisation factor read from the auxiliary data file.
     * @return
     */
    public double getNormalisationFactor() {
        return _coeffs[0];
    }

    /**
     * Loads the auxiliary data file for the diffuse aerosol transmission coefficients
     * @param auxPath
     */
    public void load(String auxPath, String aux_version_key) throws IOException {
        _logger.info("Reading auxiliary data file: '" + auxPath + "'");

        loadFromPropertiesFile(auxPath);

        // log information
        // ---------------
        logVersionFromProperties(aux_version_key);

        String temp = _props.getProperty(NORMALISATION_FACTOR_AUX_KEY);
        if (temp == null) {
            _logger.warning("No normalisation factor found in auxiliary file '" + auxPath + "'");
            _logger.warning("Using default value for normalisation: '" + _default_value); //NORMALISATION_FACTOR_DEFAULT);
            _coeffs[0] = _default_value; //NORMALISATION_FACTOR_DEFAULT;
        } else {
            _coeffs[0] = Double.parseDouble(temp);
            _logger.fine("... normalisation factor: '" + _coeffs[0] + "'");
        }

        _logger.info("... success");
    }
}
