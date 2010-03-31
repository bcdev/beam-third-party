/*
 * $Id: ToaVegUncertaintyModelLoader.java,v 1.7 2006/03/20 17:28:46 meris Exp $
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
import org.esa.beam.processor.common.auxdata.VegUncertaintyModelAccess;
import org.esa.beam.processor.common.auxdata.VegUncertaintyModelLoader;
import org.esa.beam.processor.toa.ToaVegConstants;

public class ToaVegUncertaintyModelLoader extends  VegUncertaintyModelLoader implements VegUncertaintyModelAccess {

    /**
     * Constructs the object with default parameters
     */
    public ToaVegUncertaintyModelLoader() {
        _coeffs = new double[12];
        _coeffs[0] = ToaVegConstants.FAPAR_UNC0_DEFAULT;
        _coeffs[1] = ToaVegConstants.FAPAR_UNC1_DEFAULT;
        _coeffs[2] = ToaVegConstants.FAPAR_UNC2_DEFAULT;
        _coeffs[3] = ToaVegConstants.FCOVER_UNC0_DEFAULT;
        _coeffs[4] = ToaVegConstants.FCOVER_UNC1_DEFAULT;
        _coeffs[5] = ToaVegConstants.FCOVER_UNC2_DEFAULT;
        _coeffs[6] = ToaVegConstants.LAI_UNC0_DEFAULT;
        _coeffs[7] = ToaVegConstants.LAI_UNC1_DEFAULT;
        _coeffs[8] = ToaVegConstants.LAI_UNC2_DEFAULT;
        _coeffs[9] = ToaVegConstants.LAIXCAB_UNC0_DEFAULT;
        _coeffs[10] = ToaVegConstants.LAIXCAB_UNC1_DEFAULT;
        _coeffs[11] = ToaVegConstants.LAIXCAB_UNC2_DEFAULT;
        initLogger(ToaVegConstants.LOGGER_NAME);
    }

    /**
     * Retrieves the coefficients for the uncertainty polynomial of the fAPAR.
     * ret[0] = fAPAR.0 - offset
     * ret[1] = fAPAR.1 - linear
     * ret[2] = fAPAR.2 - quadratic
     * @param recycle an array of doubles to be filled with data (at least 3 elements). Can be null,
     * then the array is allocated in the method
     * @return
     */
    public double[] getfAPARCoefficients(double[] recycle) {
        if ((recycle == null) || (recycle.length < 3)) {
            recycle = new double[3];
        }

        recycle[0] = _coeffs[0];
        recycle[1] = _coeffs[1];
        recycle[2] = _coeffs[2];

        return recycle;
    }

    /**
     * Retrieves the coefficients for the uncertainty polynomial of the fCover.
     * ret[0] = fCover.0 - offset
     * ret[1] = fCover.1 - linear
     * ret[2] = fCover.2 - quadratic
     * @param recycle an array of doubles to be filled with data (at least 3 elements). Can be null,
     * then the array is allocated in the method
     * @return
     */
    public double[] getfCoverCoefficients(double[] recycle) {
        if ((recycle == null) || (recycle.length < 3)) {
            recycle = new double[3];
        }

        recycle[0] = _coeffs[3];
        recycle[1] = _coeffs[4];
        recycle[2] = _coeffs[5];

        return recycle;
    }

    /**
     * Retrieves the coefficients for the uncertainty polynomial of the LAI.
     * ret[0] = LAI.0 - offset
     * ret[1] = LAI.1 - linear
     * ret[2] = LAI.2 - quadratic
     * @param recycle an array of doubles to be filled with data (at least 3 elements). Can be null,
     * then the array is allocated in the method
     * @return
     */
    public double[] getLAICoefficients(double[] recycle) {
        if ((recycle == null) || (recycle.length < 3)) {
            recycle = new double[3];
        }

        recycle[0] = _coeffs[6];
        recycle[1] = _coeffs[7];
        recycle[2] = _coeffs[8];

        return recycle;
    }


    /**
     * Retrieves the coefficients for the uncertainty polynomial of the LAIxCab.
     * ret[0] = LAIxCab.0 - offset
     * ret[1] = LAIxCab.1 - linear
     * ret[2] = LAIxCab.2 - quadratic
     * @param recycle an array of doubles to be filled with data (at least 3 elements). Can be null,
     * then the array is allocated in the method
     * @return
     */
    public double[] getLAIxCabCoefficients(double[] recycle) {
        if ((recycle == null) || (recycle.length < 3)) {
            recycle = new double[3];
        }

        recycle[0] = _coeffs[9];
        recycle[1] = _coeffs[10];
        recycle[2] = _coeffs[11];

        return recycle;
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
        loadDoubleProperty(ToaVegConstants.FAPAR_UNC0_KEY, ToaVegConstants.FAPAR_UNC0_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.FAPAR_UNC1_KEY, ToaVegConstants.FAPAR_UNC1_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.FAPAR_UNC2_KEY, ToaVegConstants.FAPAR_UNC2_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.FCOVER_UNC0_KEY, ToaVegConstants.FCOVER_UNC0_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.FCOVER_UNC1_KEY, ToaVegConstants.FCOVER_UNC1_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.FCOVER_UNC2_KEY, ToaVegConstants.FCOVER_UNC2_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAI_UNC0_KEY, ToaVegConstants.LAI_UNC0_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAI_UNC1_KEY, ToaVegConstants.LAI_UNC1_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAI_UNC2_KEY, ToaVegConstants.LAI_UNC2_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAIXCAB_UNC0_KEY, ToaVegConstants.LAIXCAB_UNC0_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAIXCAB_UNC1_KEY, ToaVegConstants.LAIXCAB_UNC1_DEFAULT, auxPath, i++);
        loadDoubleProperty(ToaVegConstants.LAIXCAB_UNC2_KEY, ToaVegConstants.LAIXCAB_UNC2_DEFAULT, auxPath, i++);

        _logger.info("... success");
    }
}
