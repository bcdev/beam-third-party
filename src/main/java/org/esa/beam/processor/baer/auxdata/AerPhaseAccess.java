/*
 * $Id: AerPhaseAccess.java,v 1.1.1.1 2005/02/15 11:13:35 meris Exp $
 *
 * Copyright (C) 2002,2003 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation. This program is distributed in the hope it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 */
package org.esa.beam.processor.baer.auxdata;

public interface AerPhaseAccess {

   /**
     * Retrieves the aerosol phase coefficients for given geometry.
     * @param sza sun zenith angle (decimal degrees)
     * @param saa sun azimuth angle (decimal degrees)
     * @param vza view zenith angle (decimal degrees)
     * @param vaa view azimuth angle (decimal degrees)
     * @param retArray array to be filled with data (can be null)
     * @return true if retrieval was successful, false if geometry was out of the LUT
     */
    // Don't know if this access method will ever be used
    //public boolean getPhase(float sza, float saa, float vza, float vaa, double[] retArray);

    /**
     * Retrieves the aerosol phase function coefficients for the band with the given index.
     * Band indexing is 1-based i.e. as in the MERIS case.
     * @param bandIdx
     * @return
     */
    public AerBandParam getAerPhase(int bandIdx);
}
