/*
 * $Id: VegFlagsManager.java,v 1.2 2005/11/03 10:32:35 meris Exp $
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
package org.esa.beam.processor.common.utils;

import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.BitmaskDef;
import java.awt.Color;

public class VegFlagsManager {

    // the flags - names, hex-values and descriptions
    // ----------------------------------------------
    public static final String INVALID_FLAG_NAME = "INVALID";
    public static final String INVALID_FLAG_DESCRIPTION = "The pixel is invalid. This flag is a combination of INVALID_INPUT and ALGORITHM_FAILURE.";
    public static final int INVALID_FLAG_MASK = 0x01;

    public static final String INVALID_INPUT_FLAG_NAME = "INVALID_INPUT";
    public static final String INVALID_INPUT_FLAG_DESCRIPTION = "Set 'true' if any one of the input measurement data has a value <= 0.0 or if the input pixel was skipped due to input flagging.";
    public static final int INVALID_INPUT_FLAG_MASK = 0x02;

    public static final String ALGORITHM_FAILURE_FLAG_NAME = "ALGORITHM_FAILURE";
    public static final String ALGORITHM_FAILURE_FLAG_DESCRIPTION = "Set 'true' on any numerical failure during the processing (division by zero ?).";
    public static final int ALGORITHM_FAILURE_FLAG_MASK = 0x04;

    public static final String LAI_OUT_OF_RANGE_FLAG_NAME = "LAI_OUT_OF_RANGE";
    public static final String LAI_OUT_OF_RANGE_FLAG_DESCRIPTION = "Set 'true' if the calculated leaf area index for this pixel is out of the valid range.";
    public static final int LAI_OUT_OF_RANGE_FLAG_MASK = 0x08;

    public static final String FCOVER_OUT_OF_RANGE_FLAG_NAME = "FCOVER_OUT_OF_RANGE";
    public static final String FCOVER_OUT_OF_RANGE_FLAG_DESCRIPTION = "Set 'true' if the calculated fraction of vegetation is out of the valid range.";
    public static final int FCOVER_OUT_OF_RANGE_FLAG_MASK = 0x10;

    public static final String LAIXCAB_OUT_OF_RANGE_FLAG_NAME = "LAIXCAB_OUT_OF_RANGE";
    public static final String LAIXCAB_OUT_OF_RANGE_FLAG_DESCRIPTION = "Set 'true' if the calculated canopy chlorophyll content is out of the valid range.";
    public static final int LAIXCAB_OUT_OF_RANGE_FLAG_MASK = 0x20;

    public static final String FAPAR_OUT_OF_RANGE_FLAG_NAME = "FAPAR_OUT_OF_RANGE";
    public static final String FAPAR_OUT_OF_RANGE_FLAG_DESCRIPTION = "Set 'true' if the calculated Fraction of Absorbed Photosynthetically Active Radiation is out of the valid range.";
    public static final int FAPAR_OUT_OF_RANGE_FLAG_MASK = 0x40;

    /**
     * Sets the invalid flag on the flag passed in.
     * @param currentFlag
     * @return currentFlag | INVALID_MASK
     */
    public static int setInvalidFlag(int currentFlag) {
        return currentFlag | INVALID_FLAG_MASK;
    }

    /**
     * Sets the invalidInput flag and the invalid flag on the flag passed in
     * @param currentFlag
     * @return
     */
    public static int setInvalidInputFlag(int currentFlag) {
        currentFlag |= INVALID_INPUT_FLAG_MASK;
        return currentFlag | INVALID_FLAG_MASK;
    }

    /**
     * Sets the algorithm_failure flag and the invalid flag on the flag passed in
     * @param currentFlag
     * @return
     */
    public static int setAlgorithmFailureFlag(int currentFlag) {
        currentFlag |= ALGORITHM_FAILURE_FLAG_MASK;
        return currentFlag | INVALID_FLAG_MASK;
    }

    /**
     * Sets the LAI_OUT_OF_RANGE flag on the flag passed in.
     * @param currentFlag
     * @return currentFlag | LAI_OUT_OF_RANGE_FLAG_MASK
     */
    public static int setLaiOutOfRangeFlag(int currentFlag) {
        return currentFlag | LAI_OUT_OF_RANGE_FLAG_MASK;
    }

    /**
     * Sets the FCOVER_OUT_OF_RANGE flag on the flag passed in.
     * @param currentFlag
     * @return currentFlag | FCOVER_OUT_OF_RANGE_FLAG_MASK
     */
    public static int setFCoverOutOfRangeFlag(int currentFlag) {
        return currentFlag | FCOVER_OUT_OF_RANGE_FLAG_MASK;
    }

    /**
     * Sets the LAIXCAB_OUT_OF_RANGE flag on the flag passed in.
     * @param currentFlag
     * @return currentFlag | LAIXCAB_OUT_OF_RANGE_FLAG_MASK
     */
    public static int setLaixCabOutOfRangeFlag(int currentFlag) {
        return currentFlag | LAIXCAB_OUT_OF_RANGE_FLAG_MASK;
    }

    /**
     * Sets the FAPAR_OUT_OF_RANGE flag on the flag passed in.
     * @param currentFlag
     * @return currentFlag | FAPAR_OUT_OF_RANGE_FLAG_MASK
     */
    public static int setFaparOutOfRangeFlag(int currentFlag) {
        return currentFlag | FAPAR_OUT_OF_RANGE_FLAG_MASK;
    }



    /**
     * Retrieves the flags coding for the VEG processor.
     */
    public static FlagCoding getCoding(String flags_band_name) {
        FlagCoding coding = new FlagCoding(flags_band_name);

        coding.addFlag(INVALID_FLAG_NAME, INVALID_FLAG_MASK, INVALID_FLAG_DESCRIPTION);
        coding.addFlag(INVALID_INPUT_FLAG_NAME, INVALID_INPUT_FLAG_MASK, INVALID_INPUT_FLAG_DESCRIPTION);
        coding.addFlag(ALGORITHM_FAILURE_FLAG_NAME, ALGORITHM_FAILURE_FLAG_MASK, ALGORITHM_FAILURE_FLAG_DESCRIPTION);
        coding.addFlag(LAI_OUT_OF_RANGE_FLAG_NAME, LAI_OUT_OF_RANGE_FLAG_MASK, LAI_OUT_OF_RANGE_FLAG_DESCRIPTION);
        coding.addFlag(FCOVER_OUT_OF_RANGE_FLAG_NAME, FCOVER_OUT_OF_RANGE_FLAG_MASK, FCOVER_OUT_OF_RANGE_FLAG_DESCRIPTION);
        coding.addFlag(LAIXCAB_OUT_OF_RANGE_FLAG_NAME, LAIXCAB_OUT_OF_RANGE_FLAG_MASK, LAIXCAB_OUT_OF_RANGE_FLAG_DESCRIPTION);
        coding.addFlag(FAPAR_OUT_OF_RANGE_FLAG_NAME, FAPAR_OUT_OF_RANGE_FLAG_MASK, FAPAR_OUT_OF_RANGE_FLAG_DESCRIPTION);

        return coding;
    }

    /**
     * Adds the bitmask definitions for this flag coding to the product passed in
     * @param prod
     */
    public static void addBitmaskDefsToProduct(Product prod, String flags_band_name) {
        prod.addBitmaskDef(new BitmaskDef(flags_band_name + "." + INVALID_FLAG_NAME, INVALID_FLAG_DESCRIPTION,
                                          flags_band_name + "." + INVALID_FLAG_NAME,
                                          Color.red, 0.5F));

        prod.addBitmaskDef(new BitmaskDef(flags_band_name + "." + INVALID_INPUT_FLAG_NAME, INVALID_INPUT_FLAG_DESCRIPTION,
                                          flags_band_name + "." + INVALID_INPUT_FLAG_NAME,
                                          Color.orange, 0.5F));

        prod.addBitmaskDef(new BitmaskDef(flags_band_name + "." + ALGORITHM_FAILURE_FLAG_NAME, ALGORITHM_FAILURE_FLAG_DESCRIPTION,
                                          flags_band_name + "." + ALGORITHM_FAILURE_FLAG_NAME,
                                          Color.magenta, 0.5F));

        prod.addBitmaskDef(new BitmaskDef(flags_band_name + "." + LAI_OUT_OF_RANGE_FLAG_NAME, LAI_OUT_OF_RANGE_FLAG_DESCRIPTION,
                                          flags_band_name + "." + LAI_OUT_OF_RANGE_FLAG_NAME,
                                          Color.red.brighter(), 0.5F));

        prod.addBitmaskDef(new BitmaskDef(flags_band_name + "." + FCOVER_OUT_OF_RANGE_FLAG_NAME, FCOVER_OUT_OF_RANGE_FLAG_DESCRIPTION,
                                          flags_band_name + "." + FCOVER_OUT_OF_RANGE_FLAG_NAME,
                                          Color.yellow, 0.5F));

        prod.addBitmaskDef(new BitmaskDef(flags_band_name + "." + LAIXCAB_OUT_OF_RANGE_FLAG_NAME, LAIXCAB_OUT_OF_RANGE_FLAG_DESCRIPTION,
                                          flags_band_name + "." + LAIXCAB_OUT_OF_RANGE_FLAG_NAME,
                                          Color.orange.darker(), 0.5F));

        prod.addBitmaskDef(new BitmaskDef(flags_band_name + "." + FAPAR_OUT_OF_RANGE_FLAG_NAME, FAPAR_OUT_OF_RANGE_FLAG_DESCRIPTION,
                                          flags_band_name + "." + FAPAR_OUT_OF_RANGE_FLAG_NAME,
                                          Color.magenta.brighter(), 0.5F));
    }
}


