/*
 * $Id: FlagsManager.java,v 1.3 2006/03/27 15:16:42 meris Exp $
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

import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.BitmaskDef;
import org.esa.beam.processor.baer.BaerConstants;

import java.awt.Color;

public class FlagsManager {

    // the flags - names, hex-values and descriptions
    // ----------------------------------------------
    public static final String INVALID_FLAG_NAME = "INVALID";
    public static final int INVALID_FLAG_MASK = 0x01;
    public static final String INVALID_FLAG_DESCRIPTION = "Pixel contains invalid data and shall not be used for further processing";

    public static final String INVALID_INPUT_FLAG_NAME = "INVALID_INPUT";
    public static final int INVALID_INPUT_FLAG_MASK = 0x02;
    public static final String INVALID_INPUT_FLAG_DESCRIPTION = "Input data for this pixel was invalid or flagged out";

    public static final String CLOUD_INPUT_FLAG_NAME = "CLOUD_INPUT";
    public static final int CLOUD_INPUT_FLAG_MASK = 0x04;
    public static final String CLOUD_INPUT_FLAG_DESCRIPTION = "";

  /*  public static final String CLOUD_SHADOW_FLAG_NAME = "CLOUD_SHADOW_INPUT";
    public static final int CLOUD_SHADOW_FLAG_MASK = 0x08;
    public static final String CLOUD_SHADOW_FLAG_DESCRIPTION = "";        */

    public static final String AOT_OUT_OF_RANGE_FLAG_NAME = "AOT_OUT_OF_RANGE";
    public static final int AOT_OUT_OF_RANGE_FLAG_MASK = 0x10;
    public static final String AOT_OUT_OF_RANGE_FLAG_DESCRIPTION = "The calculated AOT is out of the valid range";

    public static final String ALPHA_OUT_OF_RANGE_FLAG_NAME = "ALPHA_OUT_OF_RANGE";
    public static final int ALPHA_OUT_OF_RANGE_FLAG_MASK = 0x20;
    public static final String ALPHA_OUT_OF_RANGE_FLAG_DESCRIPTION = "The calculated ALPHA coefficient is out of the valid range";

    public static final String INVALID_OUTPUT_FLAG_NAME = "INVALID_OUTPUT";
    public static final int INVALID_OUTPUT_FLAG_MASK = 0x40;
    public static final String INVALID_OUTPUT_FLAG_DESCRIPTION = "";

    public static final String CORRECTION_FLAG_NAME = "SMAC_CORRECTION";
    public static final int CORRECTION_FLAG_MASK = 0x80;
    public static final String CORRECTION_FLAG_DESCRIPTION = "The Atmospheric correction used is SMAC";




    /**
     * Retrieves the flag coding object for the BAER processor.
     *
     * @return the coding
     */
    public static FlagCoding getFlagCoding() {
        FlagCoding coding = new FlagCoding(BaerConstants.OUT_FLAGS_BAND_NAME);

        coding.addFlag(INVALID_FLAG_NAME, INVALID_FLAG_MASK, INVALID_FLAG_DESCRIPTION);
        coding.addFlag(INVALID_INPUT_FLAG_NAME, INVALID_INPUT_FLAG_MASK, INVALID_INPUT_FLAG_DESCRIPTION);
        coding.addFlag(AOT_OUT_OF_RANGE_FLAG_NAME, AOT_OUT_OF_RANGE_FLAG_MASK, AOT_OUT_OF_RANGE_FLAG_DESCRIPTION);
        coding.addFlag(ALPHA_OUT_OF_RANGE_FLAG_NAME, ALPHA_OUT_OF_RANGE_FLAG_MASK, ALPHA_OUT_OF_RANGE_FLAG_DESCRIPTION);
      //  coding.addFlag(CLOUD_SHADOW_FLAG_NAME, CLOUD_SHADOW_FLAG_MASK, CLOUD_SHADOW_FLAG_DESCRIPTION);
        coding.addFlag(CLOUD_INPUT_FLAG_NAME, CLOUD_INPUT_FLAG_MASK, CLOUD_INPUT_FLAG_DESCRIPTION);
        coding.addFlag(CORRECTION_FLAG_NAME, CORRECTION_FLAG_MASK, CORRECTION_FLAG_DESCRIPTION);
        coding.addFlag(INVALID_OUTPUT_FLAG_NAME,INVALID_OUTPUT_FLAG_MASK,INVALID_OUTPUT_FLAG_DESCRIPTION);

        return coding;
    }

    /**
     * Adds the bitmask definitions for this flag coding to the product passed in
     *
     * @param prod
     */
    public static void addBitmaskDefsToProduct(Product prod) {
        prod.addBitmaskDef(new BitmaskDef(BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.INVALID_FLAG_NAME,
                                          FlagsManager.INVALID_FLAG_DESCRIPTION,
                                          BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.INVALID_FLAG_NAME,
                                          Color.red, 0.5F));

        prod.addBitmaskDef(new BitmaskDef(
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.INVALID_INPUT_FLAG_NAME,
                FlagsManager.INVALID_INPUT_FLAG_DESCRIPTION,
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.INVALID_INPUT_FLAG_NAME,
                Color.orange, 0.5F));

        prod.addBitmaskDef(new BitmaskDef(
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.AOT_OUT_OF_RANGE_FLAG_NAME,
                FlagsManager.AOT_OUT_OF_RANGE_FLAG_DESCRIPTION,
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.AOT_OUT_OF_RANGE_FLAG_NAME,
                Color.pink, 0.5F));

        prod.addBitmaskDef(new BitmaskDef(
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.ALPHA_OUT_OF_RANGE_FLAG_NAME,
                FlagsManager.ALPHA_OUT_OF_RANGE_FLAG_DESCRIPTION,
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.ALPHA_OUT_OF_RANGE_FLAG_NAME,
                Color.red, 0.5F));

        prod.addBitmaskDef(new BitmaskDef(
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.CORRECTION_FLAG_NAME,
                FlagsManager.CORRECTION_FLAG_DESCRIPTION,
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.CORRECTION_FLAG_NAME,
                Color.red, 0.5F));

          prod.addBitmaskDef(new BitmaskDef(
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.CLOUD_INPUT_FLAG_NAME,
                FlagsManager.CLOUD_INPUT_FLAG_DESCRIPTION,
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.CLOUD_INPUT_FLAG_NAME,
                Color.yellow, 0.5F));

     /*     prod.addBitmaskDef(new BitmaskDef(
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.CLOUD_SHADOW_FLAG_NAME,
                FlagsManager.CLOUD_SHADOW_FLAG_DESCRIPTION,
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.CLOUD_SHADOW_FLAG_NAME,
                Color.yellow, 0.5F));        */

        prod.addBitmaskDef(new BitmaskDef(
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.INVALID_OUTPUT_FLAG_NAME,
                FlagsManager.INVALID_OUTPUT_FLAG_DESCRIPTION,
                BaerConstants.OUT_FLAGS_BAND_NAME + "." + FlagsManager.INVALID_OUTPUT_FLAG_NAME,
                Color.red, 0.5F));

    }
}
