/*
 * $Id: MerisPixel.java,v 1.2 2005/10/17 14:30:58 meris Exp $
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
package org.esa.beam.processor.baer.utils;

public class MerisPixel {

    private float[] _reflec;
    private float _band_Lat;
    private float _band_Lon;
    private float _band_Sza;
    private float _band_Saa;
    private float _band_Vza;
    private float _band_Vaa;
    private float _band_Pressure;
    private float _band_aero_alpha;
    private float _band_aero_opt;

    private static final int NUM_BANDS = 13;

    /**
     * Constructs the object with default parameters
     */
    public MerisPixel() {
        _reflec = new float[NUM_BANDS];
        reset();
    }

    /**
     * Initializes all member fields to 0.0f.
     */
    public void reset() {
        for (int n = 0; n < _reflec.length; n++) {
            _reflec[n] = 0.f;
        }
        _band_Lat = 0.f;
        _band_Lon = 0.f;
        _band_Sza = 0.f;
        _band_Saa = 0.f;
        _band_Vza = 0.f;
        _band_Vaa = 0.f;
        _band_aero_alpha = 0.f;
        _band_aero_opt = 0.f;
    }

    /**
     * Retrieves the value for MERIS band num_band (zero based index)
     * The band indexing follows the MERIS Level 2 specification:
     *  idx 0 ... 9     : reflec_1 ... reflec_10
     *  idx 10 ... 12   : reflec_12 ... reflec_14
     * @param idx  MERIS band index
     * @return the value
     */
    public float getBand(int idx) throws IllegalArgumentException {
        if ((idx >= 0) && (idx < NUM_BANDS)) {
            return _reflec[idx];
        }

        throw new IllegalArgumentException("Invalid index for band");
    }

    /**
     * Sets the value for the MERIS reflectance at the given band index (zero based)
     * The band indexing follows the MERIS Level 2 specification:
     *  idx 0 ... 9     : reflec_1 ... reflec_10
     *  idx 10 ... 12   : reflec_12 ... reflec_14
     * @param fVal
     * @param idx
     * @throws IllegalArgumentException
     */
    public void setBand(float fVal, int idx) throws IllegalArgumentException {
        if ((idx >= 0) && (idx < NUM_BANDS)) {
            _reflec[idx] = fVal;
        } else {
            throw new IllegalArgumentException("Invalid index for band");
        }
    }

    /**
     * Calculates the scattering angle
     * @return the scattering angle
     */
    public double getScatteringAngle() {
        double theta;
        theta = Math.acos(-(Math.cos(Math.toRadians(_band_Sza)) *
                          Math.cos(Math.toRadians(_band_Vza)) +
                          Math.sin(Math.toRadians(_band_Sza)) *
                          Math.sin(Math.toRadians(_band_Vza)) *
                          Math.cos(Math.toRadians(Math.abs(_band_Vaa - _band_Saa)))));
      //  System.out.println("theta en degres = " + Math.toDegrees(theta));
        return theta;
    }


    /**
     * Retrieves the value for MERIS latitudes band
     * @return the value
     */
    public float getBand_Aero_alpha() {
        return _band_aero_alpha;
    }

    /**
     * Sets the value for MERIS latitudes band.
     * @param fVal
     */
    public void setBand_Aero_alpha(float fVal) {
        _band_aero_alpha = fVal;
    }

    /**
     * Retrieves the value for MERIS latitudes band
     * @return the value
     */
    public float getBand_Aero_opt() {
        return _band_aero_opt;
    }

    /**
     * Sets the value for MERIS latitudes band.
     * @param fVal
     */
    public void setBand_Aero_opt(float fVal) {
        _band_aero_opt = fVal;
    }

    /**
     * Retrieves the value for MERIS latitudes band
     * @return the value
     */
    public float getBand_Lat() {
        return _band_Lat;
    }

    /**
     * Sets the value for MERIS latitudes band.
     * @param fVal
     */
    public void setBand_Lat(float fVal) {
        _band_Lat = fVal;
    }

    /**
     * Retrieves the value for MERIS longitudes band
     * @return the value
     */
    public float getBand_Lon() {
        return _band_Lon;
    }

    /**
     * Sets the value for MERIS longitudes band.
     * @param fVal
     */
    public void setBand_Lon(float fVal) {
        _band_Lon = fVal;
    }

    /**
     * Retrieves the value for MERIS sun zenith angles band
     * @return the value
     */
    public float getBand_Sza() {
        return _band_Sza;
    }

    /**
     * Sets the value for MERIS sun zenith angles band.
     * @param fVal
     */
    public void setBand_Sza(float fVal) {
        _band_Sza = fVal;
    }

    /**
     * Retrieves the value for MERIS sun azimuth angles band
     * @return the value
     */
    public float getBand_Saa() {
        return _band_Saa;
    }

    /**
     * Sets the value for MERIS sun azimuth angles band.
     * @param fVal
     */
    public void setBand_Saa(float fVal) {
        _band_Saa = fVal;
    }

    /**
     * Retrieves the value for MERIS view zenith angles band
     * @return the value
     */
    public float getBand_Vza() {
        return _band_Vza;
    }

    /**
     * Sets the value for MERIS view zenith angles band.
     * @param fVal
     */
    public void setBand_Vza(float fVal) {
        _band_Vza = fVal;
    }

    /**
     * Retrieves the value for MERIS view azimuth angles band
     * @return the value
     */
    public float getBand_Vaa() {
        return _band_Vaa;
    }

    /**
     * Sets the value for MERIS view azimuth angles band.
     * @param fVal
     */
    public void setBand_Vaa(float fVal) {
        _band_Vaa = fVal;
    }

    /**
     * Retrieves the surface pressure in hPa as stored in the MERIS tie point data.
     * @return
     */
    public float getPressure() {
        return _band_Pressure;
    }

    /**
     * Sets the surface pressure in hPa as stored in the MERIS tie point data.
     */
    public void setPressure(float val) {
        _band_Pressure = val;
    }

    /**
     * Assign the content of another pixel to this one
     * @param other the source pixel
     */
    public void assign(MerisPixel other) {
        for (int n = 0; n < NUM_BANDS; n++) {
            _reflec[n] = other.getBand(n);
        }
        _band_Lat = other.getBand_Lat();
        _band_Lon = other.getBand_Lon();
        _band_Sza = other.getBand_Sza();
        _band_Saa = other.getBand_Saa();
        _band_Vza = other.getBand_Vza();
        _band_Vaa = other.getBand_Vaa();
        _band_Pressure = other.getPressure();
    }
}
