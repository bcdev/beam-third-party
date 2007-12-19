/*
 * $Id: Spectrum.java,v 1.1.1.1 2005/02/15 11:13:36 meris Exp $
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

import org.esa.beam.util.Guardian;

public class Spectrum {

    public static final int MIN_BAND_IDX = 0;
    public static final int MAX_BAND_IDX = 14;

    private String _shortName;
    private String _description;
    private String _groundType;
    private double[] _values;

    /**
     * COnstructs the object with default parameters
     */
    public Spectrum() {
        _shortName = "";
        _description = "";
        _groundType = "";
        _values = new double[MAX_BAND_IDX + 1];
    }

    /**
     * Retrieves the short name of the spectrum.
     * @return
     */
    public String getShortName() {
        return _shortName;
    }

    /**
     * Sets the shortName for this spectrum
     * @param shortName
     */
    public void setShortName(String shortName) {
        Guardian.assertNotNull("shortName", shortName);
        _shortName = shortName;
    }

    /**
     * Retrieves the description of the spectrum.
     * @return
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Sets the description of the spectrum.
     * @param description
     */
    public void setDescription(String description) {
        Guardian.assertNotNull("description", description);
        _description = description;
    }

    /**
     * Retrievs the ground type of the spectrum
     * @return
     */
    public String getGroundType() {
        return _groundType;
    }

    /**
     * Sets the ground type of the spectrum.
     * @param type
     */
    public void setGroundType(String type) {
        Guardian.assertNotNull("type", type);
        _groundType = type;
    }

    /**
     * Retrieves the spectral band value at the given position.
     * @param n
     * @return
     * @throws IllegalArgumentException when the spectral band index is out of range
     */
    public double getValueAt(int n) {
        if ((n < MIN_BAND_IDX) || (n > MAX_BAND_IDX)) {
            throw new IllegalArgumentException("Spectral band index out of range");
        }

        return _values[n];
    }

    /**
     * Sets the spectral band value at the given position.
     * @param n
     * @param value
     * @throws IllegalArgumentException when the spectral band index is out of range
     */
    public void setValueAt(int n, double value) {
        if ((n < MIN_BAND_IDX) || (n > MAX_BAND_IDX)) {
            throw new IllegalArgumentException("Spectral band index out of range");
        }

        _values[n] = value;
    }
}
