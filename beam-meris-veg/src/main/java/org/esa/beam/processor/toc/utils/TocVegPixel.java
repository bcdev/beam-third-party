/*
 * $Id: TocVegPixel.java,v 1.1.1.1 2005/02/15 11:13:40 meris Exp $
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

import org.esa.beam.processor.common.utils.VegGenericPixel;


public class TocVegPixel extends VegGenericPixel{


    private float _delta_fAPAR;


    public void resetPixel() {
        reset();
        _delta_fAPAR = 0.f;
    }


    /**
     * Retrieves the fAPAR mis-match of the pixel.
     * @return
     */
    public float getBand_delta_fAPAR() {
        return _delta_fAPAR;
    }

    /**
     * Sets the fAPAR mis-match of the pixel.
     */
    public void setBand_delta_fAPAR(float val) {
        _delta_fAPAR = val;
    }
}
