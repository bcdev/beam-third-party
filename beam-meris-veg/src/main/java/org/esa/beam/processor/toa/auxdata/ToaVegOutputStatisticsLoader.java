/*
 * $Id: ToaVegOutputStatisticsLoader.java,v 1.5 2006/03/13 08:51:10 meris Exp $
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
package org.esa.beam.processor.toa.auxdata;

import org.esa.beam.processor.toa.ToaVegConstants;
import org.esa.beam.processor.common.auxdata.VegOutputStatisticsLoader;
import org.esa.beam.processor.common.auxdata.VegOutputStatisticsAccess;
import java.io.IOException;

public class ToaVegOutputStatisticsLoader extends VegOutputStatisticsLoader implements VegOutputStatisticsAccess {

    /**
     * Constructs the object with default data
     */
    public ToaVegOutputStatisticsLoader() {
        _coeffs = new double[8];
        _coeffs[0] = ToaVegConstants.FAPAR_MIN_DEFAULT;
        _coeffs[1] = ToaVegConstants.FAPAR_MAX_DEFAULT;
        _coeffs[2] = ToaVegConstants.FCOVER_MIN_DEFAULT;
        _coeffs[3] = ToaVegConstants.FCOVER_MAX_DEFAULT;
        _coeffs[4] = ToaVegConstants.LAI_MIN_DEFAULT;
        _coeffs[5] = ToaVegConstants.LAI_MAX_DEFAULT;
        _coeffs[6] = ToaVegConstants.LAIXCAB_MIN_DEFAULT;
        _coeffs[7] = ToaVegConstants.LAIXCAB_MAX_DEFAULT;
        initLogger(ToaVegConstants.LOGGER_NAME);
        _coeff_length=2;
    }



    /**
     * Loads the auxiliary data file for the diffuse aerosol transmission coefficients
     * @param auxPath
     */
    public void load(String auxPath) throws IOException {
        _logger.info("Reading auxiliary data file: '" + auxPath + "'");

        loadFromPropertiesFile(auxPath);

        // log information
        // ---------------
        logVersionFromProperties(ToaVegConstants.AUX_VERSION_KEY);

        int i = 0;

        loadDoubleProperty(ToaVegConstants.FAPAR_MIN_KEY, ToaVegConstants.FAPAR_MIN_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.FAPAR_MAX_KEY, ToaVegConstants.FAPAR_MAX_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.FCOVER_MIN_KEY, ToaVegConstants.FCOVER_MIN_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.FCOVER_MAX_KEY, ToaVegConstants.FCOVER_MAX_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAI_MIN_KEY, ToaVegConstants.LAI_MIN_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAI_MAX_KEY, ToaVegConstants.LAI_MAX_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAIXCAB_MIN_KEY, ToaVegConstants.LAIXCAB_MIN_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAIXCAB_MAX_KEY, ToaVegConstants.LAIXCAB_MAX_DEFAULT, auxPath, i++);

        _logger.info("... success");
    }


}
