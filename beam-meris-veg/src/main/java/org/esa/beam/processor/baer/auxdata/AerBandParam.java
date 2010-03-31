/*
 * $Id: AerBandParam.java,v 1.1.1.1 2005/02/15 11:13:35 meris Exp $
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

public class AerBandParam {

    private double[] _a;
    private boolean _isValid;
    private String _name;

    /**
     * Consttructs the object as invalid.
     */
    public AerBandParam() {
        _a = new double[]{-1.0, -1.0, -1.0};
        _isValid = false;
        _name = new String();
    }

    /**
     * Sets the a0 coefficient
     * @param val
     */
    public void setA0(double val) {
        _a[0] = val;
    }

    /**
     * Sets the a1 coefficient
     * @param val
     */
    public void setA1(double val) {
        _a[1] = val;
    }

    /**
     * Sets the a2 coefficient
     * @param val
     */
    public void setA2(double val) {
        _a[2] = val;
    }

    /**
     * Validates or invalidates the coefficient set
     * @param bValid
     */
    public void validate(boolean bValid) {
        _isValid = bValid;
    }

    /**
     * Retrieves the coefficients array as vector (a_0, a_1, a_2)
     * @return
     */
    public double[] getA() {
        return _a;
    }

    /**
     * Retrieves whether tis dataset is valid - or not
     * @return
     */
    public boolean isValid() {
        return _isValid;
    }

    /**
     * Sets a name for the band
     * @param name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Retrieves the name of the band.
     * @return
     */
    public String getName() {
        return _name;
    }
}
