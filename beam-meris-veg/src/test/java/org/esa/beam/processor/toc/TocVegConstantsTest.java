/*
 * $Id: TocVegConstantsTest.java,v 1.9 2006/04/12 10:14:49 meris Exp $
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
package org.esa.beam.processor.toc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TocVegConstantsTest extends TestCase {

    public TocVegConstantsTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TocVegConstantsTest.class);
    }

    /**
     * Tests the basic processor constants (name, version ...) for correctness
     */
    public void testProcessorBaseConstants() {
        assertEquals("MERIS TOC-VEG", TocVegConstants.PROC_NAME);

        assertEquals("beam.processor.org.esa.beam.processor.toc", TocVegConstants.LOGGER_NAME);
        assertEquals("toc", TocVegConstants.DEFAULT_LOG_PREFIX);

        assertEquals("TOC_VEG_PROCESS", TocVegConstants.REQUEST_TYPE);

        assertEquals("config.xml", TocVegConstants.CONFIG_FILE);
        assertEquals("toc", TocVegConstants.AUXDATA_DIR);
    }

    /**
     * Checks the constants used for the processor configuration for correctness
     */
    public void testConfigurationConstants() {
        assertEquals("TocVegConfig", TocVegConstants.CONFIGURATION_TAG);
        assertEquals("Parameter", TocVegConstants.PARAMETER_TAG);
        assertEquals("name", TocVegConstants.ATTRIB_NAME);
        assertEquals("value", TocVegConstants.ATTRIB_VALUE);

        assertEquals("normalisation_factor", TocVegConstants.NORMALISATION_FACTOR_ATTRIB_NAME);
        assertEquals("input_statistics", TocVegConstants.INPUT_STATISTICS_ATTRIB_NAME);
    }

    /**
     * Tests the constants needed for the auxiliary file access for correctness
     */
    public void testAuxFileConstants() {
        assertEquals("version", TocVegConstants.AUX_VERSION_KEY);
        assertEquals("description", TocVegConstants.AUX_DESCRIPTION_KEY);

        assertEquals(0.66, TocVegConstants.NORMALISATION_FACTOR_DEFAULT, 1e-6);
        assertEquals("norm_factor", TocVegConstants.NORMALISATION_FACTOR_AUX_KEY);

        assertEquals(41.491, TocVegConstants.THETA_S_MEAN_DEFAULT, 1e-6);
        assertEquals("theta_s_mean", TocVegConstants.THETA_S_MEAN_AUX_KEY);
        assertEquals(11.712, TocVegConstants.THETA_S_STD_DEFAULT, 1e-6);
        assertEquals("theta_s_std", TocVegConstants.THETA_S_STD_AUX_KEY);
        assertEquals(22.172, TocVegConstants.THETA_V_MEAN_DEFAULT, 1e-6);
        assertEquals("theta_v_mean", TocVegConstants.THETA_V_MEAN_AUX_KEY);
        assertEquals(10.55, TocVegConstants.THETA_V_STD_DEFAULT, 1e-6);
        assertEquals("theta_v_std", TocVegConstants.THETA_V_STD_AUX_KEY);
        assertEquals(0.022895, TocVegConstants.COS_PHI_MEAN_DEFAULT, 1e-6);
        assertEquals("cos_phi_mean", TocVegConstants.COS_PHI_MEAN_AUX_KEY);
        assertEquals(0.77433, TocVegConstants.COS_PHI_STD_DEFAULT, 1e-6);
        assertEquals("cos_phi_std", TocVegConstants.COS_PHI_STD_AUX_KEY);
        assertEquals(0.19841, TocVegConstants.R_MEAN_DEFAULT, 1e-6);
        assertEquals("r_mean", TocVegConstants.R_MEAN_AUX_KEY);
        assertEquals(0.16899, TocVegConstants.R_STD_DEFAULT, 1e-6);
        assertEquals("r_std", TocVegConstants.R_STD_AUX_KEY);

        assertEquals("nn", TocVegConstants.NN_AUX_KEY);

        assertEquals(0.70321, TocVegConstants.FAPAR_MEAN_DEFAULT, 1e-6);
        assertEquals("fAPAR_mean", TocVegConstants.FAPAR_MEAN_KEY);
        assertEquals(0.22361, TocVegConstants.FAPAR_STD_DEFAULT, 1e-6);
        assertEquals("fAPAR_std", TocVegConstants.FAPAR_STD_KEY);
        assertEquals(3.7167E-5, TocVegConstants.FAPAR_MIN_DEFAULT, 1e-6);
        assertEquals("fAPAR_min", TocVegConstants.FAPAR_MIN_KEY);
        assertEquals(0.98728, TocVegConstants.FAPAR_MAX_DEFAULT, 1e-6);
        assertEquals("fAPAR_max", TocVegConstants.FAPAR_MAX_KEY);
        assertEquals(0.60202, TocVegConstants.FCOVER_MEAN_DEFAULT, 1e-6);
        assertEquals("fCover_mean", TocVegConstants.FCOVER_MEAN_KEY);
        assertEquals(0.26446, TocVegConstants.FCOVER_STD_DEFAULT, 1e-6);
        assertEquals("fCover_std", TocVegConstants.FCOVER_STD_KEY);
        assertEquals(2.9771E-5, TocVegConstants.FCOVER_MIN_DEFAULT, 1e-6);
        assertEquals("fCover_min", TocVegConstants.FCOVER_MIN_KEY);
        assertEquals(0.99556, TocVegConstants.FCOVER_MAX_DEFAULT, 1e-6);
        assertEquals("fCover_max", TocVegConstants.FCOVER_MAX_KEY);
        assertEquals(3.1099, TocVegConstants.LAI_MEAN_DEFAULT, 1e-6);
        assertEquals("LAI_mean", TocVegConstants.LAI_MEAN_KEY);
        assertEquals(1.9554, TocVegConstants.LAI_STD_DEFAULT, 1e-6);
        assertEquals("LAI_std", TocVegConstants.LAI_STD_KEY);
        assertEquals(6.0712E-5, TocVegConstants.LAI_MIN_DEFAULT, 1e-6);
        assertEquals("LAI_min", TocVegConstants.LAI_MIN_KEY);
        assertEquals(7.9669, TocVegConstants.LAI_MAX_DEFAULT, 1e-6);
        assertEquals("LAI_max", TocVegConstants.LAI_MAX_KEY);
        assertEquals(173.07, TocVegConstants.LAIXCAB_MEAN_DEFAULT, 1e-6);
        assertEquals("LAIxCab_mean", TocVegConstants.LAIXCAB_MEAN_KEY);
        assertEquals(137.97, TocVegConstants.LAIXCAB_STD_DEFAULT, 1e-6);
        assertEquals("LAIxCab_std", TocVegConstants.LAIXCAB_STD_KEY);
        assertEquals(0.00091478, TocVegConstants.LAIXCAB_MIN_DEFAULT, 1e-6);
        assertEquals("LAIxCab_min", TocVegConstants.LAIXCAB_MIN_KEY);
        assertEquals(765.6, TocVegConstants.LAIXCAB_MAX_DEFAULT, 1e-6);
        assertEquals("LAIxCab_max", TocVegConstants.LAIXCAB_MAX_KEY);

    }

    /**
     * Tests the input product constants for correctness
     */
    public void testInputProductConstants() {
        assertEquals(11, TocVegConstants.NUM_BANDS);
        assertEquals(TocVegConstants.NUM_BANDS, TocVegConstants.REFLEC_BAND_NAMES.length);
        assertEquals("reflec_3", TocVegConstants.REFLEC_BAND_NAMES[0]);
        assertEquals("reflec_4", TocVegConstants.REFLEC_BAND_NAMES[1]);
        assertEquals("reflec_5", TocVegConstants.REFLEC_BAND_NAMES[2]);
        assertEquals("reflec_6", TocVegConstants.REFLEC_BAND_NAMES[3]);
        assertEquals("reflec_7", TocVegConstants.REFLEC_BAND_NAMES[4]);
        assertEquals("reflec_8", TocVegConstants.REFLEC_BAND_NAMES[5]);
        assertEquals("reflec_9", TocVegConstants.REFLEC_BAND_NAMES[6]);
        assertEquals("reflec_10", TocVegConstants.REFLEC_BAND_NAMES[7]);
        assertEquals("reflec_12", TocVegConstants.REFLEC_BAND_NAMES[8]);
        assertEquals("reflec_13", TocVegConstants.REFLEC_BAND_NAMES[9]);
        assertEquals("reflec_14", TocVegConstants.REFLEC_BAND_NAMES[10]);
        assertEquals("reflec_3", TocVegConstants.REFLEC_3_BAND_NAME);
        assertEquals("reflec_4", TocVegConstants.REFLEC_4_BAND_NAME);
        assertEquals("reflec_5", TocVegConstants.REFLEC_5_BAND_NAME);
        assertEquals("reflec_7", TocVegConstants.REFLEC_7_BAND_NAME);
        assertEquals("l2_flags", TocVegConstants.L2_FLAGS_BAND_NAME);
        assertEquals("toa_veg", TocVegConstants.TOA_VEG_BAND_NAME);
        assertEquals("BAER_FLAGS", TocVegConstants.BAER_FLAGS_BAND_NAME);

        assertEquals("latitude", TocVegConstants.LAT_TIEPOINT_NAME);
        assertEquals("longitude", TocVegConstants.LON_TIEPOINT_NAME);
        assertEquals("sun_zenith", TocVegConstants.SZA_TIEPOINT_NAME);
        assertEquals("sun_azimuth", TocVegConstants.SAA_TIEPOINT_NAME);
        assertEquals("view_zenith", TocVegConstants.VZA_TIEPOINT_NAME);
        assertEquals("view_azimuth", TocVegConstants.VAA_TIEPOINT_NAME);
    }

    /**
     * Tests the output product constants for correctness
     */
    public void testOutputProductConstants() {
        assertEquals("_VEG", TocVegConstants.PRODUCT_TYPE_APPENDIX);

        assertEquals("LAI", TocVegConstants.LAI_BAND_NAME);
        assertEquals("Leaf Area Index", TocVegConstants.LAI_BAND_DESCRIPTION);
        assertEquals("m^2 / m^2", TocVegConstants.LAI_BAND_UNIT);

        assertEquals("fCover", TocVegConstants.FCOVER_BAND_NAME);
        assertEquals("Fraction of vegetation", TocVegConstants.FCOVER_BAND_DESCRIPTION);

        assertEquals("LAIxCab", TocVegConstants.LAIXCAB_BAND_NAME);
        assertEquals("Canopy chlorophyll content", TocVegConstants.LAIXCAB_BAND_DESCRIPTION);
        assertEquals("g / m^2", TocVegConstants.LAIXCAB_BAND_UNIT);

        assertEquals("fAPAR", TocVegConstants.FAPAR_BAND_NAME);
        assertEquals("Fraction of Absorbed Photosynthetically Active Radiation", TocVegConstants.FAPAR_BAND_DESCRIPTION);

        assertEquals("TOC_VEG_FLAGS", TocVegConstants.VEG_FLAGS_BAND_NAME);
        assertEquals("VEG flags dataset", TocVegConstants.VEG_FLAGS_BAND_DESCRIPTION);
    }

    /**
     * Tests the messaging constants
     */
    public void testMessages() {
        assertEquals("Generating output pixels ...", TocVegConstants.LOG_MSG_GENERATE_PIXEL);
    }

    /**
     * Tests the constants needed for the metadata of the target product for correctness
     */
    public void testMetadataConstants() {
        assertEquals("SRC_METADATA", TocVegConstants.SRC_METADATA_NAME);
        assertEquals("MPH", TocVegConstants.MPH_METADATA_NAME);
        assertEquals("PRODUCT", TocVegConstants.PRODUCT_METADATA_NAME);
        assertEquals("SRC_PRODUCT", TocVegConstants.SRC_PRODUCT_METADATA_NAME);
        assertEquals("PROCESSOR", TocVegConstants.PROCESSOR_METADATA_NAME);
        assertEquals("PROCESSOR_VERSION", TocVegConstants.PROCESSOR_VERSION_METADATA_NAME);
        assertEquals("PROC_TIME", TocVegConstants.PROCESSING_TIME_METADATA_NAME);

        assertEquals("AUX_FILE_NAME", TocVegConstants.AUX_FILE_NAME_METADATA_NAME);
        assertEquals("AUX_FILE_VERSION", TocVegConstants.AUX_FILE_VERSION_METADATA_NAME);
        assertEquals("AUX_FILE_DESCRIPTION", TocVegConstants.AUX_FILE_DESCRIPTION_METADATA_NAME);
        assertEquals("unknown", TocVegConstants.AUX_VAL_UNKNOWN);
        assertEquals("none", TocVegConstants.AUX_VAL_NONE);

        assertEquals("NORMALISATION_FACTOR", TocVegConstants.NORMALISATION_AUX_METADATA_NAME);
        assertEquals("INPUT_STATISTICS", TocVegConstants.INPUT_STATISTICS_AUX_METADATA_NAME);
        assertEquals("OUTPUT_STATISTICS", TocVegConstants.OUTPUT_STATISTICS_AUX_METADATA_NAME);
    }

    /**
     * Tests the constants needed for the processing request for correctness
     */
    public void testRequestConstants() {
        assertEquals("bitmask", TocVegConstants.BITMASK_PARAM_NAME);
        assertEquals("Bitmask", TocVegConstants.BITMASK_PARAM_LABEL);
        assertEquals("Please enter a bitmask expression", TocVegConstants.BITMASK_PARAM_DESCRIPTION);
        assertEquals("l2_flags.LAND", TocVegConstants.BITMASK_PARAM_DEFAULT);
     }

}
