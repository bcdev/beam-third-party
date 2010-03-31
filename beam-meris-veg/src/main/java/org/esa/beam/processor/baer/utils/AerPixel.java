/*
 * $Id: AerPixel.java,v 1.4 2006/03/27 15:16:42 meris Exp $
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

public class AerPixel {

    private static int NUM_BANDS = 13;
    private float[] _reflec;
    private float _aot_412;
    private float _aot_560;
    private float _aot_865;
    private float _aot_665;
    private float _aot_440;
    private float _aot_470;
    private float _aot_550;
    private float _aero_412;
    private float _aero_565;
   private float _aero_865;
    private float _aero_alpha;
    private float _band_lat;
    private float _band_lon;
    private float _band_cloud;
    private float _alpha;
    private int _flags;

    /**
     * Constructs the object with default parameters.
     */
    public AerPixel() {
        _reflec = new float[NUM_BANDS];
        reset();
    }

    /**
     * Resets the pixel to default values
     */
    public void reset() {
        for (int n = 0; n < NUM_BANDS; n++) {
            _reflec[n] = 0.f;
        }
        _aot_412 = 0.f;
        _aot_560 = 0.f;
        _aot_865 = 0.f;
        _aero_412=0.f;
        _aero_565=0.f;
        _aero_865 = 0.f;
        _aero_alpha=0.f;
        _aot_665=0.f;
        _aot_440=0.f;
        _aot_550=0.f;
        _aot_470=0.f;
        _band_lat=0.f;
        _band_lon=0.f;
        _alpha = 0.f;
        _flags = 0;
    }

    /**
     * Retrieves the corrected reflectance at the given (zero based) band index
     * The band indexing follows the MERIS Level 2 specification:
     *  idx 0 ... 9     : reflec_1 ... reflec_10
     *  idx 10 ... 12   : reflec_12 ... reflec_14
     * @param idx
     * @return
     */
    public float getBand(int idx) {
        if ((idx >= 0) && (idx < NUM_BANDS)) {
            return _reflec[idx];
        }

        throw new IllegalArgumentException("Invalid band index");
    }

    /**
     * Sets the corrected reflectance at the given (zero based) band index
     * The band indexing follows the MERIS Level 2 specification:
     *  idx 0 ... 9     : reflec_1 ... reflec_10
     *  idx 10 ... 12   : reflec_12 ... reflec_14
     * @param idx
     */
    public void setBand(float fVal, int idx) {
        if ((idx >= 0) && (idx < NUM_BANDS)) {
            _reflec[idx] = fVal;
        } else {
            throw new IllegalArgumentException("Invalid band index");
        }
    }

    /**
        * Retrieves the value for the Cloud.
        * @return
        */
       public float getBand_Cloud() {
           return _band_cloud;
       }

       /**
        * Sets the value for the aerosol optical thickness at 412 nm.
        * @param fVal
        */
       public void setBand_Cloud(float fVal) {
           _band_cloud = fVal;
       }


    /**
     * Retrieves the value for the aerosol optical thickness at 412 nm.
     * @return
     */
    public float getAero_865() {
        return _aero_865;
    }

    /**
     * Sets the value for the aerosol optical thickness at 412 nm.
     * @param fVal
     */
    public void setAero_865(float fVal) {
        _aero_865 = fVal;
    }



    /**
     * Retrieves the value for the aerosol optical thickness at 412 nm.
     * @return
     */
    public float getAero_alpha() {
        return _aero_alpha;
    }

    /**
     * Sets the value for the aerosol optical thickness at 412 nm.
     * @param fVal
     */
    public void setAero_alpha(float fVal) {
        _aero_alpha = fVal;
    }


        /**
     * Retrieves the value for the aerosol optical thickness at 412 nm.
     * @return
     */
    public float getAero_412() {
        return _aero_412;
    }

    /**
     * Sets the value for the aerosol optical thickness at 412 nm.
     * @param fVal
     */
    public void setAero_412(float fVal) {
        _aero_412 = fVal;
    }

        /**
     * Retrieves the value for the aerosol optical thickness at 412 nm.
     * @return
     */
    public float getAero_565() {
        return _aero_565;
    }

    /**
     * Sets the value for the aerosol optical thickness at 412 nm.
     * @param fVal
     */
    public void setAero_565(float fVal) {
        _aero_565 = fVal;
    }


     /**
     * Retrieves the value for the aerosol optical thickness at 412 nm.
     * @return
     */
    public float getAot_440() {
        return _aot_440;
    }

    /**
     * Sets the value for the aerosol optical thickness at 412 nm.
     * @param fVal
     */
    public void setAot_440(float fVal) {
        _aot_440 = fVal;
    }



    /**
       * Retrieves the value for the aerosol optical thickness at 412 nm.
       * @return
       */
      public float getAot_470() {
          return _aot_470;
      }

      /**
       * Sets the value for the aerosol optical thickness at 412 nm.
       * @param fVal
       */
      public void setAot_470(float fVal) {
          _aot_470 = fVal;
      }
     /**
     * Retrieves the value for the aerosol optical thickness at 412 nm.
     * @return
     */
    public float getAot_550() {
        return _aot_550;
    }

    /**
     * Sets the value for the aerosol optical thickness at 412 nm.
     * @param fVal
     */
    public void setAot_550(float fVal) {
        _aot_550 = fVal;
    }

     /**
     * Retrieves the value for the aerosol optical thickness at 412 nm.
     * @return
     */
    public float getAot_665() {
        return _aot_665;
    }

    /**
     * Sets the value for the aerosol optical thickness at 412 nm.
     * @param fVal
     */
    public void setAot_665(float fVal) {
        _aot_665 = fVal;
    }

    /**
     * Retrieves the value for the aerosol optical thickness at 412 nm.
     * @return
     */
    public float getAot_412() {
        return _aot_412;
    }

    /**
     * Sets the value for the aerosol optical thickness at 412 nm.
     * @param fVal
     */
    public void setAot_412(float fVal) {
        _aot_412 = fVal;
    }

    /**
     * Retrieves the value for the optical thickness at 560 nm.
     * @return
     */
    public float getAot_560() {
        return _aot_560;
    }

    /**
     * Sets the value for the aerosol optical thickness at 560 nm.
     * @param fVal
     */
    public void setAot_560(float fVal) {
        _aot_560 = fVal;
    }


       /**
     * Retrieves the value for the aerosol optical thickness at 412 nm.
     * @return
     */
    public float getAot_865() {
        return _aot_865;
    }

    /**
     * Sets the value for the aerosol optical thickness at 412 nm.
     * @param fVal
     */
    public void setAot_865(float fVal) {
        _aot_865 = fVal;
    }


    /**
     * Retrieves the value for the alpha value
     * @return
     */
    public float getAlpha() {
        return _alpha;
    }

    /**
     * Sets the value for the alpha value
     * @param fVal
     */
    public void setAlpha(float fVal) {
        _alpha = fVal;
    }


    /**
      * Retrieves the value for the alpha value
      * @return
      */
     public float getLat() {
         return _band_lat;
     }

     /**
      * Sets the value for the alpha value
      * @param fVal
      */
     public void setLat(float fVal) {
         _band_lat = fVal;
     }


    /**
      * Retrieves the value for the alpha value
      * @return
      */
     public float getLon() {
         return _band_lon;
     }

     /**
      * Sets the value for the alpha value
      * @param fVal
      */
     public void setLon(float fVal) {
         _band_lon = fVal;
     }

    /**
     * Retrieves the flag mask integer for the pixel.
     * @return
     */
    public int getFlagMask() {
        return _flags;
    }

    /**
     * Clears the flag mask set.
     */
    public void clearFlags() {
        _flags = 0;
    }

    /**
     * Sets the invalid input flag. Also the INVALID flag is set automatically.
     */
    public void setInvalidInputFlag() {
        _flags |= FlagsManager.INVALID_INPUT_FLAG_MASK;
        _flags |= FlagsManager.INVALID_FLAG_MASK;
    }

    /**
     * Sets the AOT out of range flag. Also the INVALID flag is set automatically.
     */
    public void setAotOutOfRangeFlag() {
        _flags |= FlagsManager.AOT_OUT_OF_RANGE_FLAG_MASK;
    }

    /**
     * Sets the ALPHA out of range flag. Also the INVALID flag is set automatically.
     */
    public void setAlphaOutOfRangeFlag() {
        _flags |= FlagsManager.ALPHA_OUT_OF_RANGE_FLAG_MASK;
     }


    /**
     * Sets the invalid output flag. Also the INVALID flag is set automatically.
     */
    public void setInvalidOutputFlag() {
        _flags |= FlagsManager.INVALID_OUTPUT_FLAG_MASK;
     }

/**
       * Sets the atmospheric correction flag.
       */
    public void setAtmosphericCorrectionFlag() {
          _flags |= FlagsManager.CORRECTION_FLAG_MASK;
    }

    /**
        * Sets the cloud input flag. Also the INVALID flag is set automatically.
        */
       public void setCloudInputFlag() {
           _flags |= FlagsManager.CLOUD_INPUT_FLAG_MASK;
           _flags |= FlagsManager.INVALID_FLAG_MASK;
       }

     /**
        * Sets the cloud shaddow flag. Also the INVALID flag is set automatically.
        */
  /*     public void setCloudShadowFlag() {
           _flags |= FlagsManager.CLOUD_SHADOW_FLAG_MASK;
           _flags |= FlagsManager.INVALID_FLAG_MASK;
       }
                    */
    /**
       * Sets the no cloud flag. Also the INVALID flag is set automatically.
       */

  /*   public void setNoL2CloudFlag() {
            _flags |= FlagsManager.NO_L2_CLOUD_FLAG_MASK;
            _flags |= FlagsManager.INVALID_FLAG_MASK;
     }  */

}
