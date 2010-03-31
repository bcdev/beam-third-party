/*
 * $Id: TocVegBaerPixel.java,v 1.2 2006/03/27 15:29:11 meris Exp $
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
package org.esa.beam.processor.toc.utils;

import org.esa.beam.processor.common.utils.MerisGenericPixel;
import org.esa.beam.processor.toc.TocVegConstants;


public class TocVegBaerPixel extends MerisGenericPixel {


    private float _aot_412;
    private float _aot_560;
    private float _alpha;
    private float _toa_veg;


    public TocVegBaerPixel(){
        initPixel(TocVegConstants.NUM_BANDS);
    }

    /**
     * Retrieves the aerosol optical thickness at 412 nm
     * @return
     */
    public float getBand_AOT_412() {
        return _aot_412;
    }

    /**
     * Sets the aerosol optical thickness at 412 nm
     */
    public void setBand_AOT_412(float val) {
        _aot_412 = val;
    }

    /**
     * Retrieves the aerosol optical thickness at 560 nm
     * @return
     */
    public float getBand_AOT_560() {
        return _aot_560;
    }

    /**
     * Sets the aerosol optical thickness at 560 nm
     */
    public void setBand_AOT_560(float val) {
        _aot_560 = val;
    }

    /**
     * Retrieves the angstr?m alpha coefficient
     * @return
     */
    public float getBand_ALPHA() {
        return _alpha;
    }

    /**
     * Sets the angstr?m alpha coefficient
     */
    public void setBand_ALPHA(float val) {
        _alpha = val;
    }

    /**
     * Retrieves the MERIS global vegetation index
     * @return
     */
    public float getBand_TOAVEG() {
        return _toa_veg;
    }

    /**
     * Sets the MERIS global vegetation index
     */
    public void setBand_TOAVEG(float val) {
        _toa_veg = val;
    }
}
