/*
 * $Id: VegOutputStatisticsAccess.java,v 1.2 2005/07/22 15:37:57 meris Exp $
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
package org.esa.beam.processor.common.auxdata;

public interface VegOutputStatisticsAccess {

    /**
     * Retrieves the fAPAR constants from the aux file.
     *
     * @param recycle an array of doubles to be filled with data (at least 4 elements). Can be null,
     * then the array is allocated in the method
     * @return
     */
    public double[] getFAPARConstants(double[] recycle);

    /**
     * Retrieves the fCover constants from the aux file.
     *
     * @param recycle an array of doubles to be filled with data (at least 4 elements). Can be null,
     * then the array is allocated in the emthod
     * @return
     */
    public double[] getFCoverConstants(double[] recycle);

    /**
     * Retrieves the LAI constants from the aux file.
     *
     * @param recycle an array of doubles to be filled with data (at least 4 elements). Can be null,
     * then the array is allocated in the emthod
     * @return
     */
    public double[] getLAIConstants(double[] recycle);

    /**
     * Retrieves the LAIxCab constants from the aux file.
     *
     * @param recycle an array of doubles to be filled with data (at least 4 elements). Can be null,
     * then the array is allocated in the emthod
     * @return
     */
    public double[] getLAIxCabConstants(double[] recycle);

 /**
     * Retrieves the LAIxCab constants from the aux file.
     *
     * @param recycle an array of doubles to be filled with data (at least 4 elements). Can be null,
     * then the array is allocated in the emthod
     * @return
     */
    public double[] getLAIxCabConstantsToa(double[] recycle);

}
