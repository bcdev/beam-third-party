/*
 * $Id: ToaVegInputStatisticsLoader.java,v 1.2 2006/03/06 13:26:18 meris Exp $
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
package org.esa.beam.processor.toa.auxdata;

import java.io.IOException;
import org.esa.beam.processor.toa.ToaVegConstants;
import org.esa.beam.processor.common.auxdata.VegInputStatisticsLoader;

public class ToaVegInputStatisticsLoader extends VegInputStatisticsLoader implements ToaVegInputStatisticsAccess {

    /**
     * Constructs the obkect with default parameters.
     */
    public ToaVegInputStatisticsLoader() {
        _coeffs = new double[32];
        _coeffs[0] = ToaVegConstants.THETA_S_MIN_DEFAULT;
        _coeffs[1] = ToaVegConstants.THETA_S_MAX_DEFAULT;
        _coeffs[2] = ToaVegConstants.THETA_V_MIN_DEFAULT;
        _coeffs[3] = ToaVegConstants.THETA_V_MAX_DEFAULT;
        _coeffs[4] = ToaVegConstants.COS_PHI_MIN_DEFAULT;
        _coeffs[5] = ToaVegConstants.COS_PHI_MAX_DEFAULT;
        _coeffs[6] = ToaVegConstants.R1_MIN_DEFAULT;
        _coeffs[7] = ToaVegConstants.R1_MAX_DEFAULT;
        _coeffs[8] = ToaVegConstants.R2_MIN_DEFAULT;
        _coeffs[9] = ToaVegConstants.R2_MAX_DEFAULT;
        _coeffs[10] = ToaVegConstants.R3_MIN_DEFAULT;
        _coeffs[11] = ToaVegConstants.R3_MAX_DEFAULT;
        _coeffs[12] = ToaVegConstants.R4_MIN_DEFAULT;
        _coeffs[13] = ToaVegConstants.R4_MAX_DEFAULT;
        _coeffs[14] = ToaVegConstants.R5_MIN_DEFAULT;
        _coeffs[15] = ToaVegConstants.R5_MAX_DEFAULT;
        _coeffs[16] = ToaVegConstants.R6_MIN_DEFAULT;
        _coeffs[17] = ToaVegConstants.R6_MAX_DEFAULT;
        _coeffs[18] = ToaVegConstants.R7_MIN_DEFAULT;
        _coeffs[19] = ToaVegConstants.R7_MAX_DEFAULT;
        _coeffs[20] = ToaVegConstants.R8_MIN_DEFAULT;
        _coeffs[21] = ToaVegConstants.R8_MAX_DEFAULT;
        _coeffs[22] = ToaVegConstants.R9_MIN_DEFAULT;
        _coeffs[23] = ToaVegConstants.R9_MAX_DEFAULT;
        _coeffs[24] = ToaVegConstants.R10_MIN_DEFAULT;
        _coeffs[25] = ToaVegConstants.R10_MAX_DEFAULT;
        _coeffs[26] = ToaVegConstants.R11_MIN_DEFAULT;
        _coeffs[27] = ToaVegConstants.R11_MAX_DEFAULT;
        _coeffs[28] = ToaVegConstants.R12_MIN_DEFAULT;
        _coeffs[29] = ToaVegConstants.R12_MAX_DEFAULT;
        _coeffs[30] = ToaVegConstants.R13_MIN_DEFAULT;
        _coeffs[31] = ToaVegConstants.R13_MAX_DEFAULT;
         initLogger(ToaVegConstants.LOGGER_NAME);
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

        loadDoubleProperty(ToaVegConstants.THETA_S_MIN_AUX_KEY, ToaVegConstants.THETA_S_MIN_DEFAULT, auxPath, 0);
        loadDoubleProperty(ToaVegConstants.THETA_S_MAX_AUX_KEY, ToaVegConstants.THETA_S_MAX_DEFAULT, auxPath, 1);
        loadDoubleProperty(ToaVegConstants.THETA_V_MIN_AUX_KEY, ToaVegConstants.THETA_V_MIN_DEFAULT, auxPath, 2);
        loadDoubleProperty(ToaVegConstants.THETA_V_MAX_AUX_KEY, ToaVegConstants.THETA_V_MAX_DEFAULT, auxPath, 3);
        loadDoubleProperty(ToaVegConstants.COS_PHI_MIN_AUX_KEY, ToaVegConstants.COS_PHI_MIN_DEFAULT, auxPath, 4);
        loadDoubleProperty(ToaVegConstants.COS_PHI_MAX_AUX_KEY, ToaVegConstants.COS_PHI_MAX_DEFAULT, auxPath, 5);
        loadDoubleProperty(ToaVegConstants.R1_MIN_AUX_KEY, ToaVegConstants.R1_MIN_DEFAULT, auxPath, 6);
        loadDoubleProperty(ToaVegConstants.R1_MAX_AUX_KEY, ToaVegConstants.R1_MAX_DEFAULT, auxPath, 7);
        loadDoubleProperty(ToaVegConstants.R2_MIN_AUX_KEY, ToaVegConstants.R2_MIN_DEFAULT, auxPath, 8);
        loadDoubleProperty(ToaVegConstants.R2_MAX_AUX_KEY, ToaVegConstants.R2_MAX_DEFAULT, auxPath, 9);
        loadDoubleProperty(ToaVegConstants.R3_MIN_AUX_KEY, ToaVegConstants.R3_MIN_DEFAULT, auxPath, 10);
        loadDoubleProperty(ToaVegConstants.R3_MAX_AUX_KEY, ToaVegConstants.R3_MAX_DEFAULT, auxPath, 11);
        loadDoubleProperty(ToaVegConstants.R4_MIN_AUX_KEY, ToaVegConstants.R4_MIN_DEFAULT, auxPath, 12);
        loadDoubleProperty(ToaVegConstants.R4_MAX_AUX_KEY, ToaVegConstants.R4_MAX_DEFAULT, auxPath, 13);
        loadDoubleProperty(ToaVegConstants.R5_MIN_AUX_KEY, ToaVegConstants.R5_MIN_DEFAULT, auxPath, 14);
        loadDoubleProperty(ToaVegConstants.R5_MAX_AUX_KEY, ToaVegConstants.R5_MAX_DEFAULT, auxPath, 15);
        loadDoubleProperty(ToaVegConstants.R6_MIN_AUX_KEY, ToaVegConstants.R6_MIN_DEFAULT, auxPath, 16);
        loadDoubleProperty(ToaVegConstants.R6_MAX_AUX_KEY, ToaVegConstants.R6_MAX_DEFAULT, auxPath, 17);
        loadDoubleProperty(ToaVegConstants.R7_MIN_AUX_KEY, ToaVegConstants.R7_MIN_DEFAULT, auxPath, 18);
        loadDoubleProperty(ToaVegConstants.R7_MAX_AUX_KEY, ToaVegConstants.R7_MAX_DEFAULT, auxPath, 19);
        loadDoubleProperty(ToaVegConstants.R8_MIN_AUX_KEY, ToaVegConstants.R8_MIN_DEFAULT, auxPath, 20);
        loadDoubleProperty(ToaVegConstants.R8_MAX_AUX_KEY, ToaVegConstants.R8_MAX_DEFAULT, auxPath, 21);
        loadDoubleProperty(ToaVegConstants.R9_MIN_AUX_KEY, ToaVegConstants.R9_MIN_DEFAULT, auxPath, 22);
        loadDoubleProperty(ToaVegConstants.R9_MAX_AUX_KEY, ToaVegConstants.R9_MAX_DEFAULT, auxPath, 23);
        loadDoubleProperty(ToaVegConstants.R10_MIN_AUX_KEY, ToaVegConstants.R10_MIN_DEFAULT, auxPath, 24);
        loadDoubleProperty(ToaVegConstants.R10_MAX_AUX_KEY, ToaVegConstants.R10_MAX_DEFAULT, auxPath, 25);
        loadDoubleProperty(ToaVegConstants.R11_MIN_AUX_KEY, ToaVegConstants.R11_MIN_DEFAULT, auxPath, 26);
        loadDoubleProperty(ToaVegConstants.R11_MAX_AUX_KEY, ToaVegConstants.R11_MAX_DEFAULT, auxPath, 27);
        loadDoubleProperty(ToaVegConstants.R12_MIN_AUX_KEY, ToaVegConstants.R12_MIN_DEFAULT, auxPath, 28);
        loadDoubleProperty(ToaVegConstants.R12_MAX_AUX_KEY, ToaVegConstants.R12_MAX_DEFAULT, auxPath, 29);
        loadDoubleProperty(ToaVegConstants.R13_MIN_AUX_KEY, ToaVegConstants.R13_MIN_DEFAULT, auxPath, 30);
        loadDoubleProperty(ToaVegConstants.R13_MAX_AUX_KEY, ToaVegConstants.R13_MAX_DEFAULT, auxPath, 31);

        _logger.info("... success");
    }
}
