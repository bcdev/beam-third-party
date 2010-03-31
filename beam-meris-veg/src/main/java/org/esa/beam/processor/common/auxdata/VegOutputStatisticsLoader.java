/*
 * $Id: VegOutputStatisticsLoader.java,v 1.4 2006/03/20 14:53:40 meris Exp $
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
package org.esa.beam.processor.common.auxdata;

import org.esa.beam.processor.common.auxdata.VegAuxFilePropsLoader;

public class VegOutputStatisticsLoader extends VegAuxFilePropsLoader implements VegOutputStatisticsAccess {

    /**
     * Constructs the object with default data
     */
    public VegOutputStatisticsLoader() {

    }

    /**
     * Retrieves the fAPAR constants from the aux file.
     *
     * @param recycle an array of doubles to be filled woth data (at least 4 elements). Can be null,
     * then the array is allocated in the ethod
     * @return
     */
    public double[] getFAPARConstants(double[] recycle) {
        if ((recycle == null) || (recycle.length < _coeff_length)) {
            recycle = new double[_coeff_length];
        }
        for (int n = 0; n < _coeff_length; n++) {
            recycle[n] = _coeffs[n];
        }
        return recycle;
    }

    /**
     * Retrieves the fCover constants from the aux file.
     *
     * @param recycle an array of doubles to be filled with data (at least 4 elements). Can be null,
     * then the array is allocated in the emthod
     * @return
     */
    public double[] getFCoverConstants(double[] recycle) {
        if ((recycle == null) || (recycle.length < _coeff_length)) {
            recycle = new double[_coeff_length];
        }
        for (int n = 0; n < _coeff_length; n++) {
            recycle[n] = _coeffs[n + _coeff_length];
        }
        return recycle;
    }

    /**
     * Retrieves the LAI constants from the aux file.
     *
     * @param recycle an array of doubles to be filled with data (at least 4 elements). Can be null,
     * then the array is allocated in the emthod
     * @return
     */
    public double[] getLAIConstants(double[] recycle) {
        if ((recycle == null) || (recycle.length < _coeff_length)) {
            recycle = new double[_coeff_length];
        }
        for (int n = 0; n < _coeff_length; n++) {
            recycle[n] = _coeffs[n + 2*_coeff_length];
        }
        return recycle;
    }

    /**
     * Retrieves the LAIxCab constants from the aux file.
     *
     * @param recycle an array of doubles to be filled with data (at least 4 elements). Can be null,
     * then the array is allocated in the emthod
     * @return
     */
    public double[] getLAIxCabConstants(double[] recycle) {
        if ((recycle == null) || (recycle.length < _coeff_length)) {
            recycle = new double[_coeff_length];
        }
        for (int n = 0; n < _coeff_length; n++) {
            recycle[n] = _coeffs[n + 3*_coeff_length];
        }
        return recycle;
    }

     /**
     * Retrieves the LAIxCab constants from the aux file.
     *
     * @param recycle an array of doubles to be filled with data (at least 4 elements). Can be null,
     * then the array is allocated in the emthod
     * @return
     */
    public double[] getLAIxCabConstantsToa(double[] recycle) {
         int length_tmp = _coeff_length;
        if ((recycle == null) || (recycle.length < length_tmp)) {
            recycle = new double[length_tmp];
        }
        for (int n = 0; n < length_tmp; n++) {
            recycle[n] = _coeffs[n + 3*_coeff_length];
        }
        return recycle;
    }
}
