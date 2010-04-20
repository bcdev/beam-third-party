/*
 * $Id: ToaVegMerisPixel.java,v 1.1.1.1 2005/02/15 11:13:38 meris Exp $
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
package org.esa.beam.processor.toa.utils;

import org.esa.beam.processor.common.utils.MerisGenericPixel;
import org.esa.beam.processor.toa.ToaVegConstants;

public class ToaVegMerisPixel extends MerisGenericPixel {

    private float _pressure;
    private float[] _solarSpecFlux;


    public ToaVegMerisPixel(){
        _solarSpecFlux = new float[ToaVegConstants.NUM_BANDS];
        initPixel(ToaVegConstants.NUM_BANDS);
    }
   /**
     * Retrieves the pression of the pixel.
     * @return
     */
    public float getBand_Pressure() {
        return _pressure;
    }

    /**
     * Sets the pression of the pixel.
     */
    public void setBand_Pressure(float val) {
        _pressure = val;
    }


    /**
     * Retrieves the solar spec flux at the given index.
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
   public float getBand_SolarSpecFlux(int idx) {
        if ((idx < 0) || (idx >= NUM_BANDS)) {
            throw new IllegalArgumentException("Invalid band index");
        }
        return _solarSpecFlux[idx];
    }

    /**
     * Sets the solar spec flux at the given index.
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
    public void setBand_SolarSpecFlux(float fVal, int idx) {
        if ((idx < 0) || (idx >= NUM_BANDS)) {
            throw new IllegalArgumentException("Invalid band index");
        }
        _solarSpecFlux[idx] = fVal;
    }

}
