/*
 * $Id: MerisGenericPixel.java,v 1.1.1.1 2005/02/15 11:13:40 meris Exp $
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
package org.esa.beam.processor.common.utils;

//import org.esa.beam.processor.toa.ToaVegConstants;

public class MerisGenericPixel {

    protected int NUM_BANDS;
    private float[] _bands;

    private float _lat;
    private float _lon;
    private float _sza;
    private float _saa;
    private float _vza;
    private float _vaa;

    public MerisGenericPixel() {

    }
    /**
     * Constructs the object with default parameters.
     */
    public void initPixel(int num_band) {
        NUM_BANDS = num_band;
        _bands = new float[NUM_BANDS];
    }

    /**
     * Retrieves the band at the given index.
     * Band indices are as follows:
     *  idx 0 - reflec_3
     *  idx 1 - reflec_4
     *  ...
     *  idx 7 - reflec_10
     *  idx 8 - reflec_12
     *  ...
     *  idx 10 - reflec_14
     * @param idx
     * @return
     */
    public float getBand(int idx) {
        if ((idx < 0) || (idx >= NUM_BANDS)) {
            throw new IllegalArgumentException("Invalid band index");
        }
        return _bands[idx];
    }

    /**
     * Sets the band at the given index.
     * Band indices are as follows:
     *  idx 0 - reflec_3
     *  idx 1 - reflec_4
     *  ...
     *  idx 7 - reflec_10
     *  idx 8 - reflec_12
     *  ...
     *  idx 10 - reflec_14
     * @param fVal
     * @param idx
     */
    public void setBand(float fVal, int idx) {
        if ((idx < 0) || (idx >= NUM_BANDS)) {
            throw new IllegalArgumentException("Invalid band index");
        }
        _bands[idx] = fVal;
    }


   /**
     * Retrieves the latitude of the pixel.
     * @return
     */
    public float getBand_Lat() {
        return _lat;
    }

    /**
     * Sets the latitude of the pixel.
     */
    public void setBand_Lat(float val) {
        _lat = val;
    }

    /**
     * Retrieves the longitude of the pixel.
     * @return
     */
    public float getBand_Lon() {
        return _lon;
    }

    /**
     * Sets the longitude of the pixel.
     */
    public void setBand_Lon(float val) {
        _lon = val;
    }

    /**
     * Retrieves the sun zenith angle of the pixel.
     * @return
     */
    public float getBand_Sza() {
        return _sza;
    }

    /**
     * Sets the sun zenith angle of the pixel.
     */
    public void setBand_Sza(float val) {
        _sza = val;
    }

    /**
     * Retrieves the sun azimuth angle of the pixel.
     * @return
     */
    public float getBand_Saa() {
        return _saa;
    }

    /**
     * Sets the sun azimuth angle of the pixel.
     */
    public void setBand_Saa(float val) {
        _saa = val;
    }


    /**
     * Retrieves the view zenith angle of the pixel.
     * @return
     */
    public float getBand_Vza() {
        return _vza;
    }

    /**
     * Sets the sun view angle of the pixel.
     */
    public void setBand_Vza(float val) {
        _vza = val;
    }

    /**
     * Retrieves the view azimuth angle of the pixel.
     * @return
     */
    public float getBand_Vaa() {
        return _vaa;
    }

    /**
     * Sets the view azimuth angle of the pixel.
     */
    public void setBand_Vaa(float val) {
        _vaa = val;
    }




}
