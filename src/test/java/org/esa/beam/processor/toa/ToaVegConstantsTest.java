/*
 * $Id: ToaVegConstantsTest.java,v 1.7 2006/03/15 14:16:11 meris Exp $
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
package org.esa.beam.processor.toa;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ToaVegConstantsTest extends TestCase {

    public ToaVegConstantsTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ToaVegConstantsTest.class);
    }

    /**
     * Tests the basic processor constants (name, version ...) for correctness
     */
    public void testProcessorBaseConstants() {
        assertEquals("beam.processor.toa", ToaVegConstants.LOGGER_NAME);
        assertEquals("toa", ToaVegConstants.DEFAULT_LOG_PREFIX);
        assertEquals("TOA_VEG_PROCESS", ToaVegConstants.REQUEST_TYPE);
    }

    /**
     * Checks the constants used for the processor configuration for correctness
     */
    public void testConfigurationConstants() {
        assertEquals("ToaVegConfig", ToaVegConstants.CONFIGURATION_TAG);
        assertEquals("Parameter", ToaVegConstants.PARAMETER_TAG);
        assertEquals("name", ToaVegConstants.ATTRIB_NAME);
        assertEquals("value", ToaVegConstants.ATTRIB_VALUE);

        assertEquals("input_statistics", ToaVegConstants.INPUT_STATISTICS_ATTRIB_NAME);
        assertEquals("output_statistics", ToaVegConstants.OUTPUT_STATISTICS_ATTRIB_NAME);
        assertEquals("uncertainty", ToaVegConstants.UNCERTAINTY_ATTRIB_NAME);
    }

    /**
     * Tests the constants needed for the auxiliary file access for correctness
     */
    public void testAuxFileConstants() {
        assertEquals("version", ToaVegConstants.AUX_VERSION_KEY);
        assertEquals("description", ToaVegConstants.AUX_DESCRIPTION_KEY);

        assertEquals(0.31792995, ToaVegConstants.THETA_S_MIN_DEFAULT, 1e-6);
        assertEquals("theta_s_min", ToaVegConstants.THETA_S_MIN_AUX_KEY);
        assertEquals(1.04718879, ToaVegConstants.THETA_S_MAX_DEFAULT, 1e-6);
        assertEquals("theta_s_max", ToaVegConstants.THETA_S_MAX_AUX_KEY);
        assertEquals(0.00000099616, ToaVegConstants.THETA_V_MIN_DEFAULT, 1e-6);
        assertEquals("theta_v_min", ToaVegConstants.THETA_V_MIN_AUX_KEY);
        assertEquals(0.68590864, ToaVegConstants.THETA_V_MAX_DEFAULT, 1e-6);
        assertEquals("theta_v_max", ToaVegConstants.THETA_V_MAX_AUX_KEY);
        assertEquals(-0.999999, ToaVegConstants.COS_PHI_MIN_DEFAULT, 1e-6);
        assertEquals("cos_phi_min", ToaVegConstants.COS_PHI_MIN_AUX_KEY);
        assertEquals(1, ToaVegConstants.COS_PHI_MAX_DEFAULT, 1e-6);
        assertEquals("cos_phi_max", ToaVegConstants.COS_PHI_MAX_AUX_KEY);
        assertEquals(0.0891215, ToaVegConstants.R1_MIN_DEFAULT, 1e-6);
        assertEquals("r1_min", ToaVegConstants.R1_MIN_AUX_KEY);
        assertEquals(0.41778551, ToaVegConstants.R1_MAX_DEFAULT, 1e-6);
        assertEquals("r1_max", ToaVegConstants.R1_MAX_AUX_KEY);
        assertEquals(0.07151076, ToaVegConstants.R2_MIN_DEFAULT, 1e-6);
        assertEquals("r2_min", ToaVegConstants.R2_MIN_AUX_KEY);
        assertEquals(0.46906965, ToaVegConstants.R2_MAX_DEFAULT, 1e-6);
        assertEquals("r2_max", ToaVegConstants.R2_MAX_AUX_KEY);
        assertEquals(0.05094766, ToaVegConstants.R3_MIN_DEFAULT, 1e-6);
        assertEquals("r3_min", ToaVegConstants.R3_MIN_AUX_KEY);
        assertEquals(0.52309721, ToaVegConstants.R3_MAX_DEFAULT, 1e-6);
        assertEquals("r3_max", ToaVegConstants.R3_MAX_AUX_KEY);
        assertEquals(0.04281258, ToaVegConstants.R4_MIN_DEFAULT, 1e-6);
        assertEquals("r4_min", ToaVegConstants.R4_MIN_AUX_KEY);
        assertEquals(0.56118259, ToaVegConstants.R4_MAX_DEFAULT, 1e-6);
        assertEquals("r4_max", ToaVegConstants.R4_MAX_AUX_KEY);
        assertEquals(0.02512556, ToaVegConstants.R5_MIN_DEFAULT, 1e-6);
        assertEquals("r5_min", ToaVegConstants.R5_MIN_AUX_KEY);
        assertEquals(0.61873024, ToaVegConstants.R5_MAX_DEFAULT, 1e-6);
        assertEquals("r5_max", ToaVegConstants.R5_MAX_AUX_KEY);
        assertEquals(0.01927991, ToaVegConstants.R6_MIN_DEFAULT, 1e-6);
        assertEquals("r6_min", ToaVegConstants.R6_MIN_AUX_KEY);
        assertEquals(0.63023794, ToaVegConstants.R6_MAX_DEFAULT, 1e-6);
        assertEquals("r6_max", ToaVegConstants.R6_MAX_AUX_KEY);
        assertEquals(0.01944251, ToaVegConstants.R7_MIN_DEFAULT, 1e-6);
        assertEquals("r7_min", ToaVegConstants.R7_MIN_AUX_KEY);
        assertEquals(0.69832564, ToaVegConstants.R7_MAX_DEFAULT, 1e-6);
        assertEquals("r7_max", ToaVegConstants.R7_MAX_AUX_KEY);
        assertEquals(0.01832826, ToaVegConstants.R8_MIN_DEFAULT, 1e-6);
        assertEquals("r8_min", ToaVegConstants.R8_MIN_AUX_KEY);
        assertEquals(0.7548423, ToaVegConstants.R8_MAX_DEFAULT, 1e-6);
        assertEquals("r8_max", ToaVegConstants.R8_MAX_AUX_KEY);
        assertEquals(0.01060944, ToaVegConstants.R9_MIN_DEFAULT, 1e-6);
        assertEquals("r9_min", ToaVegConstants.R9_MIN_AUX_KEY);
        assertEquals(0.76507422, ToaVegConstants.R9_MAX_DEFAULT, 1e-6);
        assertEquals("r9_max", ToaVegConstants.R9_MAX_AUX_KEY);
        assertEquals(0.0595014, ToaVegConstants.R10_MIN_DEFAULT, 1e-6);
        assertEquals("r10_min", ToaVegConstants.R10_MIN_AUX_KEY);
        assertEquals(0.87085622, ToaVegConstants.R10_MAX_DEFAULT, 1e-6);
        assertEquals("r10_max", ToaVegConstants.R10_MAX_AUX_KEY);
        assertEquals(0.07529407, ToaVegConstants.R11_MIN_DEFAULT, 1e-6);
        assertEquals("r11_min", ToaVegConstants.R11_MIN_AUX_KEY);
        assertEquals(0.92774513, ToaVegConstants.R11_MAX_DEFAULT, 1e-6);
        assertEquals("r11_max", ToaVegConstants.R11_MAX_AUX_KEY);
        assertEquals(0.08980312, ToaVegConstants.R12_MIN_DEFAULT, 1e-6);
        assertEquals("r12_min", ToaVegConstants.R12_MIN_AUX_KEY);
        assertEquals(1.10054001, ToaVegConstants.R12_MAX_DEFAULT, 1e-6);
        assertEquals("r12_max", ToaVegConstants.R12_MAX_AUX_KEY);
        assertEquals(0.09010581, ToaVegConstants.R13_MIN_DEFAULT, 1e-6);
        assertEquals("r13_min", ToaVegConstants.R13_MIN_AUX_KEY);
        assertEquals(1.1017671, ToaVegConstants.R13_MAX_DEFAULT, 1e-6);
        assertEquals("r13_max", ToaVegConstants.R13_MAX_AUX_KEY);

        assertEquals("nn_LAI", ToaVegConstants.NN_LAI_AUX_KEY);
        assertEquals("nn_fCover", ToaVegConstants.NN_FCOVER_AUX_KEY);
        assertEquals("nn_fAPAR", ToaVegConstants.NN_FAPAR_AUX_KEY);
        assertEquals("nn_LAIxCab", ToaVegConstants.NN_LAIXCAB_AUX_KEY);

        assertEquals(0.000028034, ToaVegConstants.FAPAR_MIN_DEFAULT, 1e-6);
        assertEquals("fAPAR_min", ToaVegConstants.FAPAR_MIN_KEY);
        assertEquals(0.96796425, ToaVegConstants.FAPAR_MAX_DEFAULT, 1e-6);
        assertEquals("fAPAR_max", ToaVegConstants.FAPAR_MAX_KEY);
        assertEquals(0.000018282, ToaVegConstants.FCOVER_MIN_DEFAULT, 1e-6);
        assertEquals("fCover_min", ToaVegConstants.FCOVER_MIN_KEY);
        assertEquals(0.98850956, ToaVegConstants.FCOVER_MAX_DEFAULT, 1e-6);
        assertEquals("fCover_max", ToaVegConstants.FCOVER_MAX_KEY);
        assertEquals(0.000036178, ToaVegConstants.LAI_MIN_DEFAULT, 1e-6);
        assertEquals("LAI_min", ToaVegConstants.LAI_MIN_KEY);
        assertEquals(5.99982132, ToaVegConstants.LAI_MAX_DEFAULT, 1e-6);
        assertEquals("LAI_max", ToaVegConstants.LAI_MAX_KEY);
        assertEquals(0.00167064, ToaVegConstants.LAIXCAB_MIN_DEFAULT, 1e-6);
        assertEquals("LAIxCab_min", ToaVegConstants.LAIXCAB_MIN_KEY);
        assertEquals(594.623954, ToaVegConstants.LAIXCAB_MAX_DEFAULT, 1e-6);
        assertEquals("LAIxCab_max", ToaVegConstants.LAIXCAB_MAX_KEY);

        assertEquals(0.02205,ToaVegConstants.FAPAR_UNC0_DEFAULT,1e-6);
        assertEquals("fAPAR.0",ToaVegConstants.FAPAR_UNC0_KEY);
        assertEquals(0.2312, ToaVegConstants.FAPAR_UNC1_DEFAULT,1e-6);
        assertEquals("fAPAR.1",ToaVegConstants.FAPAR_UNC1_KEY);
        assertEquals(-0.2422, ToaVegConstants.FAPAR_UNC2_DEFAULT,1e-6);
        assertEquals("fAPAR.2",ToaVegConstants.FAPAR_UNC2_KEY);
        assertEquals(0.0004322, ToaVegConstants.FCOVER_UNC0_DEFAULT,1e-6);
        assertEquals("fCover.0",ToaVegConstants.FCOVER_UNC0_KEY );
        assertEquals(0.3711, ToaVegConstants.FCOVER_UNC1_DEFAULT ,1e-6);
        assertEquals("fCover.1",ToaVegConstants.FCOVER_UNC1_KEY );
        assertEquals(-0.3485, ToaVegConstants.FCOVER_UNC2_DEFAULT,1e-6);
        assertEquals("fCover.2",ToaVegConstants.FCOVER_UNC2_KEY);
        assertEquals(-0.1535, ToaVegConstants.LAI_UNC0_DEFAULT,1e-6);
        assertEquals("LAI.0", ToaVegConstants.LAI_UNC0_KEY);
        assertEquals(0.776, ToaVegConstants.LAI_UNC1_DEFAULT,1e-6);
        assertEquals("LAI.1", ToaVegConstants.LAI_UNC1_KEY);
        assertEquals(-0.1207, ToaVegConstants.LAI_UNC2_DEFAULT,1e-6);
        assertEquals("LAI.2", ToaVegConstants.LAI_UNC2_KEY);
        assertEquals(0.5928, ToaVegConstants.LAIXCAB_UNC0_DEFAULT,1e-6);
        assertEquals("LAIxCab.0", ToaVegConstants.LAIXCAB_UNC0_KEY);
        assertEquals(0.5024, ToaVegConstants.LAIXCAB_UNC1_DEFAULT,1e-6);
        assertEquals("LAIxCab.1", ToaVegConstants.LAIXCAB_UNC1_KEY);
        assertEquals(-0.0007351, ToaVegConstants.LAIXCAB_UNC2_DEFAULT,1e-6);
        assertEquals("LAIxCab.2", ToaVegConstants.LAIXCAB_UNC2_KEY);


        assertEquals("nn_training_db", ToaVegConstants.TRAIN_DB_AUX_KEY);
        assertEquals("version", ToaVegConstants.TRAIN_DB_VERSION_KEY);
        assertEquals("description", ToaVegConstants.TRAIN_DB_DESCRIPTION_KEY);
        assertEquals("num_bands", ToaVegConstants.TRAIN_DB_NUM_BANDS_KEY);
        assertEquals("num_spectra", ToaVegConstants.TRAIN_DB_NUM_SPECTRA_KEY);

   }

    /**
     * Tests the input product constants for correctness
     */
    public void testInputProductConstants() {
        assertEquals(13, ToaVegConstants.NUM_BANDS);
        assertEquals(ToaVegConstants.NUM_BANDS, ToaVegConstants.REFLEC_BAND_NAMES.length);
        assertEquals("radiance_1", ToaVegConstants.REFLEC_BAND_NAMES[0]);
        assertEquals("radiance_2", ToaVegConstants.REFLEC_BAND_NAMES[1]);
        assertEquals("radiance_3", ToaVegConstants.REFLEC_BAND_NAMES[2]);
        assertEquals("radiance_4", ToaVegConstants.REFLEC_BAND_NAMES[3]);
        assertEquals("radiance_5", ToaVegConstants.REFLEC_BAND_NAMES[4]);
        assertEquals("radiance_6", ToaVegConstants.REFLEC_BAND_NAMES[5]);
        assertEquals("radiance_7", ToaVegConstants.REFLEC_BAND_NAMES[6]);
        assertEquals("radiance_8", ToaVegConstants.REFLEC_BAND_NAMES[7]);
        assertEquals("radiance_9", ToaVegConstants.REFLEC_BAND_NAMES[8]);
        assertEquals("radiance_10", ToaVegConstants.REFLEC_BAND_NAMES[9]);
        assertEquals("radiance_12", ToaVegConstants.REFLEC_BAND_NAMES[10]);
        assertEquals("radiance_13", ToaVegConstants.REFLEC_BAND_NAMES[11]);
        assertEquals("radiance_14", ToaVegConstants.REFLEC_BAND_NAMES[12]);

        assertEquals("L1_FLAGS", ToaVegConstants.L1_FLAGS_BAND_NAME);

        assertEquals("latitude", ToaVegConstants.LAT_TIEPOINT_NAME);
        assertEquals("longitude", ToaVegConstants.LON_TIEPOINT_NAME);
        assertEquals("sun_zenith", ToaVegConstants.SZA_TIEPOINT_NAME);
        assertEquals("sun_azimuth", ToaVegConstants.SAA_TIEPOINT_NAME);
        assertEquals("view_zenith", ToaVegConstants.VZA_TIEPOINT_NAME);
        assertEquals("view_azimuth", ToaVegConstants.VAA_TIEPOINT_NAME);
    }

    /**
     * Tests the output product constants for correctness
     */
    public void testOutputProductConstants() {
        assertEquals("_VEG", ToaVegConstants.PRODUCT_TYPE_APPENDIX);

        assertEquals("LAI", ToaVegConstants.LAI_BAND_NAME);
        assertEquals("Leaf Area Index", ToaVegConstants.LAI_BAND_DESCRIPTION);
        assertEquals("m^2 / m^2", ToaVegConstants.LAI_BAND_UNIT);

        assertEquals("fCover", ToaVegConstants.FCOVER_BAND_NAME);
        assertEquals("Fraction of vegetation", ToaVegConstants.FCOVER_BAND_DESCRIPTION);

        assertEquals("LAIxCab", ToaVegConstants.LAIXCAB_BAND_NAME);
        assertEquals("Canopy chlorophyll content", ToaVegConstants.LAIXCAB_BAND_DESCRIPTION);
        assertEquals("g / m^2", ToaVegConstants.LAIXCAB_BAND_UNIT);

        assertEquals("fAPAR", ToaVegConstants.FAPAR_BAND_NAME);
        assertEquals("Fraction of Absorbed Photosynthetically Active Radiation", ToaVegConstants.FAPAR_BAND_DESCRIPTION);

        assertEquals("TOA_VEG_FLAGS", ToaVegConstants.VEG_FLAGS_BAND_NAME);
        assertEquals("TOA_VEG flags dataset", ToaVegConstants.VEG_FLAGS_BAND_DESCRIPTION);
    }

    /**
     * Tests the messaging constants
     */
    public void testMessages() {
        assertEquals("Generating output pixels ...", ToaVegConstants.LOG_MSG_GENERATE_PIXEL);
    }

    /**
     * Tests the constants needed for the metadata of the target product for correctness
     */
    public void testMetadataConstants() {
        assertEquals("SRC_METADATA", ToaVegConstants.SRC_METADATA_NAME);
        assertEquals("MPH", ToaVegConstants.MPH_METADATA_NAME);
        assertEquals("PRODUCT", ToaVegConstants.PRODUCT_METADATA_NAME);
        assertEquals("SRC_PRODUCT", ToaVegConstants.SRC_PRODUCT_METADATA_NAME);
        assertEquals("PROCESSOR", ToaVegConstants.PROCESSOR_METADATA_NAME);
        assertEquals("PROCESSOR_VERSION", ToaVegConstants.PROCESSOR_VERSION_METADATA_NAME);
        assertEquals("PROC_TIME", ToaVegConstants.PROCESSING_TIME_METADATA_NAME);

        assertEquals("AUX_FILE_NAME", ToaVegConstants.AUX_FILE_NAME_METADATA_NAME);
        assertEquals("AUX_FILE_VERSION", ToaVegConstants.AUX_FILE_VERSION_METADATA_NAME);
        assertEquals("AUX_FILE_DESCRIPTION", ToaVegConstants.AUX_FILE_DESCRIPTION_METADATA_NAME);
        assertEquals("unknown", ToaVegConstants.AUX_VAL_UNKNOWN);
        assertEquals("none", ToaVegConstants.AUX_VAL_NONE);

        assertEquals("INPUT_STATISTICS", ToaVegConstants.INPUT_STATISTICS_AUX_METADATA_NAME);
        assertEquals("OUTPUT_STATISTICS", ToaVegConstants.OUTPUT_STATISTICS_AUX_METADATA_NAME);
        assertEquals("UNCERTAINTY", ToaVegConstants.UNCERTAINTY_AUX_METADATA_NAME);
    }

    /**
     * Tests the constants needed for the processing request for correctness
     */
    public void testRequestConstants() {
        assertEquals("bitmask", ToaVegConstants.BITMASK_PARAM_NAME);
        assertEquals("Bitmask", ToaVegConstants.BITMASK_PARAM_LABEL);
        assertEquals("Please enter a bitmask expression", ToaVegConstants.BITMASK_PARAM_DESCRIPTION);
        assertEquals("l1_flags.LAND_OCEAN", ToaVegConstants.BITMASK_PARAM_DEFAULT);

     }

}




