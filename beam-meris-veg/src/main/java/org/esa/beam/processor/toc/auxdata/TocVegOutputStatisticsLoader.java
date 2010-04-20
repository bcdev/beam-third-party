/*
 * $Id: TocVegOutputStatisticsLoader.java,v 1.2 2006/03/24 08:09:03 meris Exp $
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
package org.esa.beam.processor.toc.auxdata;

import org.esa.beam.processor.toc.TocVegConstants;
import org.esa.beam.processor.common.auxdata.VegOutputStatisticsLoader;
import org.esa.beam.processor.common.auxdata.VegOutputStatisticsAccess;

import java.io.IOException;

public class TocVegOutputStatisticsLoader extends VegOutputStatisticsLoader implements VegOutputStatisticsAccess {

    /**
     * Constructs the object with default data
     */
    public TocVegOutputStatisticsLoader() {
        _coeffs = new double[16];
        _coeffs[0] = TocVegConstants.FAPAR_MEAN_DEFAULT;
        _coeffs[1] = TocVegConstants.FAPAR_STD_DEFAULT;
        _coeffs[2] = TocVegConstants.FAPAR_MIN_DEFAULT;
        _coeffs[3] = TocVegConstants.FAPAR_MAX_DEFAULT;
        _coeffs[4] = TocVegConstants.FCOVER_MEAN_DEFAULT;
        _coeffs[5] = TocVegConstants.FCOVER_STD_DEFAULT;
        _coeffs[6] = TocVegConstants.FCOVER_MIN_DEFAULT;
        _coeffs[7] = TocVegConstants.FCOVER_MAX_DEFAULT;
        _coeffs[8] = TocVegConstants.LAI_MEAN_DEFAULT;
        _coeffs[9] = TocVegConstants.LAI_STD_DEFAULT;
        _coeffs[10] = TocVegConstants.LAI_MIN_DEFAULT;
        _coeffs[11] = TocVegConstants.LAI_MAX_DEFAULT;
        _coeffs[12] = TocVegConstants.LAIXCAB_MEAN_DEFAULT;
        _coeffs[13] = TocVegConstants.LAIXCAB_STD_DEFAULT;
        _coeffs[14] = TocVegConstants.LAIXCAB_MIN_DEFAULT;
        _coeffs[15] = TocVegConstants.LAIXCAB_MAX_DEFAULT;
        initLogger(TocVegConstants.LOGGER_NAME);
        _coeff_length = 4;
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
        logVersionFromProperties(TocVegConstants.AUX_VERSION_KEY);

        int i = 0;
        loadDoubleProperty(TocVegConstants.FAPAR_MEAN_KEY, TocVegConstants.FAPAR_MEAN_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.FAPAR_STD_KEY, TocVegConstants.FAPAR_STD_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.FAPAR_MIN_KEY, TocVegConstants.FAPAR_MIN_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.FAPAR_MAX_KEY, TocVegConstants.FAPAR_MAX_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.FCOVER_MEAN_KEY, TocVegConstants.FCOVER_MEAN_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.FCOVER_STD_KEY, TocVegConstants.FCOVER_STD_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.FCOVER_MIN_KEY, TocVegConstants.FCOVER_MIN_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.FCOVER_MAX_KEY, TocVegConstants.FCOVER_MAX_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.LAI_MEAN_KEY, TocVegConstants.LAI_MEAN_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.LAI_STD_KEY, TocVegConstants.LAI_STD_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.LAI_MIN_KEY, TocVegConstants.LAI_MIN_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.LAI_MAX_KEY, TocVegConstants.LAI_MAX_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.LAIXCAB_MEAN_KEY, TocVegConstants.LAIXCAB_MEAN_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.LAIXCAB_STD_KEY, TocVegConstants.LAIXCAB_STD_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.LAIXCAB_MIN_KEY, TocVegConstants.LAIXCAB_MIN_DEFAULT, auxPath, i++);
        loadDoubleProperty(TocVegConstants.LAIXCAB_MAX_KEY, TocVegConstants.LAIXCAB_MAX_DEFAULT, auxPath, i++);

        _logger.info("... success");
    }
}
