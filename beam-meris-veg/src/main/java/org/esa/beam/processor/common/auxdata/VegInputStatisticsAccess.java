/*
 * $Id: VegInputStatisticsAccess.java,v 1.2 2006/03/20 14:51:55 meris Exp $
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

public interface VegInputStatisticsAccess {

    /**
     * Retrieves the mean value of the sun zenith angle.
     * @return
     */
    public double getTheta_S_Mean();

    /**
     * Retrieves the standard deviation value of the sun zenith angle.
     * @return
     */
    public double getTheta_S_StdDev();


    /**
       * Retrieves the min value of the sun zenith angle.
       * @return
       */
      public double getTheta_S_Min();

      /**
       * Retrieves the max value of the sun zenith angle.
       * @return
       */
      public double getTheta_S_Max();

    /**
     * Retrieves the mean value of the view zenith angle.
     * @return
     */
    public double getTheta_V_Mean();

    /**
     * Retrieves the statdard deviation value of the view zenith angle.
     * @return
     */
    public double getTheta_V_StdDev();

    /**
      * Retrieves the min value of the view zenith angle.
      * @return
      */
     public double getTheta_V_Min();

     /**
      * Retrieves the max value of the view zenith angle.
      * @return
      */
     public double getTheta_V_Max();


    /**
     * Retrieves the mean value of the relative azimuth angle.
     * @return
     */
    public double getCos_Phi_Mean();

    /**
     * Retrieves the statndard deviation value of the relative azimuth angle.
     * @return
     */
    public double getCos_Phi_StdDev();

    /**
      * Retrieves the min value of the relative azimuth angle.
      * @return
      */
     public double getCos_Phi_Min();

     /**
      * Retrieves the max value of the relative azimuth angle.
      * @return
      */
     public double getCos_Phi_Max();

    /**
     * Retrieves the mean value of the reflectances.
     * @return
     */
    public double getR_Mean();

    /**
     * Retrieves the statndard deviation value of the reflectances.
     * @return
     */
    public double getR_StdDev();

      /**
     * Retrieves the min value of the reflectances.
     * @return
     */
    public double getR_Min(int band);

    /**
     * Retrieves the max value of the reflectances.
     * @return
     */
    public double getR_Max(int band);
}
