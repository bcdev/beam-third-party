/*
 * $Id: AerLut.java,v 1.1.1.1 2005/02/15 11:13:35 meris Exp $
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

import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.util.Guardian;
import java.util.HashMap;

public class AerLut {

    private int _numBands;
    private HashMap<String,AerBandParam> _map;
    private String _name;

    /**
     * Constructs an empty LUT
     */
    public AerLut() {
        _numBands = 0;
        _map = new HashMap<String,AerBandParam>();
        _name = "";
    }

    /**
     * Constructs a LUT with a iven number of bands
     * @param numBands
     */
    public AerLut(int numBands) {
        _numBands = numBands;
        _map = new HashMap<String,AerBandParam>(numBands);
    }

    /**
     * Retrieves the number of bands for this LUT
     * @return
     */
    public int getNumBands() {
        return _numBands;
    }

    /**
     * Sets the number of bands for this LUT
     * @param numBands
     */
    public void setNumBands(int numBands) {
        _numBands = numBands;
    }

    /**
     * Retrieves the band at the given index.
     * IMPORTANT:
     * The band numbering follows the MERSI conventions where the index goes from 1 to numBands
     * @param idx
     * @return
     */
    public AerBandParam getBand(int idx) throws ProcessorException {
        if ((idx < 1) || (idx > _numBands)) {
            throw new ProcessorException("Tried to access an invalid band. Please check the Aer LUT auxiliary data file for correctness.");
        }
        AerBandParam pRet = null;

        pRet = (AerBandParam) _map.get(Integer.toString(idx));
        if (pRet == null) {
            // then it is not in the list - return a set that is flagged as invalid
            pRet = new AerBandParam();
        }

        return pRet;
    }

    /**
     * Adds a set of band parameters to the LUT
     * @param idx
     * @param param
     */
    public void addBand(int idx, AerBandParam param) throws ProcessorException {
        Guardian.assertNotNull("param", param);
        if ((idx < 1) || (idx > _numBands)) {
            throw new ProcessorException("Tried to add band parameter at an invalid index. Please check the Aer LUT auxiliary data file for correctness.");
        }

        _map.put(Integer.toString(idx), param);
    }

    public void setName(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }
}
