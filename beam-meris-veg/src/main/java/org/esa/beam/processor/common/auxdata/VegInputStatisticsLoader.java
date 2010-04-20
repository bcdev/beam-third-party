/*
 * $Id: VegInputStatisticsLoader.java,v 1.2 2006/03/20 14:52:58 meris Exp $
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

import org.esa.beam.processor.common.auxdata.VegInputStatisticsAccess;
import org.esa.beam.processor.common.auxdata.VegAuxFilePropsLoader;

public class VegInputStatisticsLoader extends VegAuxFilePropsLoader implements VegInputStatisticsAccess {

    /**
     * Constructs the obkect with default parameters.
     */
    public VegInputStatisticsLoader() {
    }

    /**
     * Retrieves the min value of the sun zenith angle.
     * @return
     */
    public double getTheta_S_Min() {
        return _coeffs[0];
    }

    /**
     * Retrieves the max value of the sun zenith angle.
     * @return
     */
    public double getTheta_S_Max() {
        return _coeffs[1];
    }

    /**
      * Retrieves the mean value of the sun zenith angle.
      * @return
      */
     public double getTheta_S_Mean() {
         return _coeffs[0];
     }

     /**
      * Retrieves the standard deviation value of the sun zenith angle.
      * @return
      */
     public double getTheta_S_StdDev() {
         return _coeffs[1];
     }


    /**
     * Retrieves the mean value of the view zenith angle.
     * @return
     */
    public double getTheta_V_Mean() {
        return _coeffs[2];
    }

    /**
     * Retrieves the statdard deviation value of the view zenith angle.
     * @return
     */
    public double getTheta_V_StdDev() {
        return _coeffs[3];
    }

    /**
       * Retrieves the min value of the view zenith angle.
       * @return
       */
      public double getTheta_V_Min() {
          return _coeffs[2];
      }

      /**
       * Retrieves the max value of the view zenith angle.
       * @return
       */
      public double getTheta_V_Max() {
          return _coeffs[3];
      }

    /**
     * Retrieves the mean value of the relative azimuth angle.
     * @return
     */
    public double getCos_Phi_Mean() {
        return _coeffs[4];
    }

    /**
     * Retrieves the statndard deviation value of the relative azimuth angle.
     * @return
     */
    public double getCos_Phi_StdDev() {
        return _coeffs[5];
    }

     /**
     * Retrieves the min value of the relative azimuth angle.
     * @return
     */
    public double getCos_Phi_Min() {
        return _coeffs[4];
    }

    /**
     * Retrieves the max value of the relative azimuth angle.
     * @return
     */
    public double getCos_Phi_Max() {
        return _coeffs[5];
    }
    /**
     * Retrieves the mean value of the reflectances.
     * @return
     */
    public double getR_Mean() {
        return _coeffs[6];
    }

    /**
     * Retrieves the statndard deviation value of the reflectances.
     * @return
     */
    public double getR_StdDev() {
        return _coeffs[7];
    }
       /**
     * Retrieves the min value of the reflectances.
     * @return
     */
    public double getR_Min(int band) {
        return _coeffs[6+2*band];
    }

    /**
     * Retrieves the max value of the reflectances.
     * @return
     */
    public double getR_Max(int band) {
        return _coeffs[7+2*band];
    }
}
