/*
 * $Id: VegGenericPixel.java,v 1.1.1.1 2005/02/15 11:13:40 meris Exp $
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

import org.esa.beam.processor.common.utils.VegFlagsManager;

public class VegGenericPixel {

    private float _lai;
    private float _fCover;
    private float _cabxLAI;
    private float _fAPAR;
    private float _delta_R;
    private float _sigma_LAI;
    private float _sigma_fCover;
    private float _sigma_LAIxCab;
    private float _sigma_fApar;
    private int _flags;

    /**
     * Constructs the object with default parameters.
     */
    public VegGenericPixel() {
        _flags = 0;
        _flags = VegFlagsManager.setInvalidFlag(_flags);
    }

    /**
     * Resets the pixel to it's default state.
     */
    public void reset() {
        _lai = 0.f;
        _fCover = 0.f;
        _cabxLAI = 0.f;
        _fAPAR = 0.f;
        _delta_R = 0.f;
        _sigma_LAI = 0.f;
        _sigma_fCover = 0.f;
        _sigma_LAIxCab = 0.f;
        _sigma_fApar = 0.f;
         clearFlags();
    }

    /**
     * Retrieves the leaf area index of the pixel.
     * @return
     */
    public float getBand_LAI() {
        return _lai;
    }

    /**
     * Sets the leaf area index of the pixel.
     */
    public void setBand_LAI(float val) {
        _lai = val;
    }

    /**
     * Retrieves the fraction of vegetation of the pixel.
     * @return
     */
    public float getBand_fCover() {
        return _fCover;
    }

    /**
     * Sets the fraction of vegetation of the pixel.
     */
    public void setBand_fCover(float val) {
        _fCover = val;
    }

    /**
     * Retrieves the Canopy chlorophyll content of the pixel.
     * @return
     */
    public float getBand_CabxLAI() {
        return _cabxLAI;
    }

    /**
     * Sets the canopy chlorophyll content of the pixel.
     */
    public void setBand_CabxLAI(float val) {
        _cabxLAI = val;
    }

    /**
     * Retrieves the Fraction of Absorbed Photosynthetically Active Radiation of the pixel.
     * @return
     */
    public float getBand_fAPAR() {
        return _fAPAR;
    }

    /**
     * Sets the Fraction of Absorbed Photosynthetically Active Radiation of the pixel.
     */
    public void setBand_fAPAR(float val) {
        _fAPAR = val;
    }

    /**
     * Retrieves the Reflectance mis-match of the pixel.
     * @return
     */
    public float getBand_delta_R() {
        return _delta_R;
    }

    /**
     * Sets the Reflectance mis-match of the pixel.
     */
    public void setBand_delta_R(float val) {
        _delta_R = val;
    }

    /**
     * Retrieves the LAI uncertainty of the pixel.
     * @return
     */
    public float getBand_sigma_LAI() {
        return _sigma_LAI;
    }

    /**
     * Sets the LAI uncertainty of the pixel.
     */
    public void setBand_sigma_LAI(float val) {
        _sigma_LAI = val;
    }

    /**
     * Retrieves the fCover uncertainty of the pixel.
     * @return
     */
    public float getBand_sigma_fApar() {
        return _sigma_fApar;
    }

    /**
     * Sets the fCover uncertainty of the pixel.
     */
    public void setBand_sigma_fApar(float val) {
        _sigma_fApar = val;
    }

    /**
     * Retrieves the fCover uncertainty of the pixel.
     * @return
     */
    public float getBand_sigma_fCover() {
        return _sigma_fCover;
    }

    /**
     * Sets the fCover uncertainty of the pixel.
     */
    public void setBand_sigma_fCover(float val) {
        _sigma_fCover = val;
    }

    /**
     * Retrieves the LAIxCab uncertainty of the pixel.
     * @return
     */
    public float getBand_sigma_LAIxCab() {
        return _sigma_LAIxCab;
    }

    /**
     * Sets the LAIxCab uncertainty of the pixel.
     */
    public void setBand_sigma_LAIxCab(float val) {
        _sigma_LAIxCab = val;
    }



    /**
     * Retrieves the flag mask of the pixel.
     * @return
     */
    public int getFlagMask() {
        return _flags;
    }

    /**
     * Clears all flags set.
     */
    public void clearFlags() {
        _flags = 0;
    }

    /**
     * Sets the invalid input flag on the pixel. Also the INVALID flag is set automatically.
     */
    public void setInvalidInputFlag() {
        _flags = VegFlagsManager.setInvalidInputFlag(_flags);
    }

    /**
     * Sets the algorithm failure flag on the pixel. Also the INVALID flag is set automatically.
     */
    public void setAlgorithmFailureFlag() {
        _flags = VegFlagsManager.setAlgorithmFailureFlag(_flags);
    }

    /**
     * Sets the LAI out of range flag on the pixel.
     */
    public void setLAIOutOfRangeFlag() {
        _flags = VegFlagsManager.setLaiOutOfRangeFlag(_flags);
    }

    /**
     * Sets the fCover out of range flag on the pixel.
     */
    public void setFCoverOutOfRangeFlag() {
        _flags = VegFlagsManager.setFCoverOutOfRangeFlag(_flags);
    }

    /**
     * Sets the LAIxCab out of range flag on the pixel.
     */
    public void setLAIxCabOutOfRangeFlag() {
        _flags = VegFlagsManager.setLaixCabOutOfRangeFlag(_flags);
    }

    /**
     * Sets the fAPAR out of range flag on the pixel.
     */
    public void setFaparOutOfRangeFlag() {
        _flags = VegFlagsManager.setFaparOutOfRangeFlag(_flags);
    }
}
