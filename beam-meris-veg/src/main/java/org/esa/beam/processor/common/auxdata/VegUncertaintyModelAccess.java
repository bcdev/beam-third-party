/*
 * $Id: VegUncertaintyModelAccess.java,v 1.1.1.1 2005/02/15 11:13:40 meris Exp $
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
package org.esa.beam.processor.common.auxdata;

public interface VegUncertaintyModelAccess  {

    /**
     * Retrieves the coefficients for the uncertainty polynomial of the fAPAR.
     * ret[0] = fAPAR.0 - offset
     * ret[1] = fAPAR.1 - linear
     * ret[2] = fAPAR.2 - quadratic
     * @param recycle an array of doubles to be filled with data (at least 3 elements). Can be null,
     * then the array is allocated in the method
     * @return
     */
    public double[] getfAPARCoefficients(double[] recycle);

    /**
     * Retrieves the coefficients for the uncertainty polynomial of the fCover.
     * ret[0] = fCover.0 - offset
     * ret[1] = fCover.1 - linear
     * ret[2] = fCover.2 - quadratic
     * @param recycle an array of doubles to be filled with data (at least 3 elements). Can be null,
     * then the array is allocated in the method
     * @return
     */
    public double[] getfCoverCoefficients(double[] recycle);

    /**
     * Retrieves the coefficients for the uncertainty polynomial of the LAI.
     * ret[0] = LAI.0 - offset
     * ret[1] = LAI.1 - linear
     * ret[2] = LAI.2 - quadratic
     * @param recycle an array of doubles to be filled with data (at least 3 elements). Can be null,
     * then the array is allocated in the method
     * @return
     */
    public double[] getLAICoefficients(double[] recycle);

    /**
     * Retrieves the coefficients for the uncertainty polynomial of the LAIxCab.
     * ret[0] = LAIxCab.0 - offset
     * ret[1] = LAIxCab.1 - linear
     * ret[2] = LAIxCab.2 - quadratic
     * @param recycle an array of doubles to be filled with data (at least 3 elements). Can be null,
     * then the array is allocated in the method
     * @return
     */
    public double[] getLAIxCabCoefficients(double[] recycle);
}
