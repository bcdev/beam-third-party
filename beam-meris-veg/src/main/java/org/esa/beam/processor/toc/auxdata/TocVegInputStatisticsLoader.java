/*
 * $Id: TocVegInputStatisticsLoader.java,v 1.1.1.1 2005/02/15 11:13:39 meris Exp $
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
package org.esa.beam.processor.toc.auxdata;

import java.io.IOException;
import org.esa.beam.processor.toc.TocVegConstants;
import org.esa.beam.processor.common.auxdata.VegInputStatisticsLoader;
import org.esa.beam.processor.common.auxdata.VegInputStatisticsAccess;

public class TocVegInputStatisticsLoader extends VegInputStatisticsLoader implements VegInputStatisticsAccess {

    /**
     * Constructs the obkect with default parameters.
     */
    public TocVegInputStatisticsLoader() {
        _coeffs = new double[8];
        _coeffs[0] = TocVegConstants.THETA_S_MEAN_DEFAULT;
        _coeffs[1] = TocVegConstants.THETA_S_STD_DEFAULT;
        _coeffs[2] = TocVegConstants.THETA_V_MEAN_DEFAULT;
        _coeffs[3] = TocVegConstants.THETA_V_STD_DEFAULT;
        _coeffs[4] = TocVegConstants.COS_PHI_MEAN_DEFAULT;
        _coeffs[5] = TocVegConstants.COS_PHI_STD_DEFAULT;
        _coeffs[6] = TocVegConstants.R_MEAN_DEFAULT;
        _coeffs[7] = TocVegConstants.R_STD_DEFAULT;
        initLogger(TocVegConstants.LOGGER_NAME);
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

        loadDoubleProperty(TocVegConstants.THETA_S_MEAN_AUX_KEY, TocVegConstants.THETA_S_MEAN_DEFAULT, auxPath, 0);
        loadDoubleProperty(TocVegConstants.THETA_S_STD_AUX_KEY, TocVegConstants.THETA_S_STD_DEFAULT, auxPath, 1);
        loadDoubleProperty(TocVegConstants.THETA_V_MEAN_AUX_KEY, TocVegConstants.THETA_V_MEAN_DEFAULT, auxPath, 2);
        loadDoubleProperty(TocVegConstants.THETA_V_STD_AUX_KEY, TocVegConstants.THETA_V_STD_DEFAULT, auxPath, 3);
        loadDoubleProperty(TocVegConstants.COS_PHI_MEAN_AUX_KEY, TocVegConstants.COS_PHI_MEAN_DEFAULT, auxPath, 4);
        loadDoubleProperty(TocVegConstants.COS_PHI_STD_AUX_KEY, TocVegConstants.COS_PHI_STD_DEFAULT, auxPath, 5);
        loadDoubleProperty(TocVegConstants.R_MEAN_AUX_KEY, TocVegConstants.R_MEAN_DEFAULT, auxPath, 6);
        loadDoubleProperty(TocVegConstants.R_STD_AUX_KEY, TocVegConstants.R_STD_DEFAULT, auxPath, 7);

        _logger.info("... success");
    }
}
