package org.esa.beam.processor.baer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Jul 22, 2003
 * Time: 9:23:53 AM
 * To change this template use Options | File Templates.
 */
public class BaerConstantsTest extends TestCase {

    public BaerConstantsTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(BaerConstantsTest.class);
    }

    /**
     * Tests the basic processor constants (name, version ...) for correctness
     */
    public void testProcessorBaseConstants() {

        assertEquals("beam.processor.baer", BaerConstants.LOGGER_NAME);
        assertEquals("baer", BaerConstants.DEFAULT_LOG_PREFIX);

        assertEquals("BAER", BaerConstants.REQUEST_TYPE);
    }

    /**
     * Tests all constants concerning parameter io for correctness.
     */
    public void testParameterConstants() {
        assertEquals("bitmask", BaerConstants.BITMASK_PARAM_NAME);
        assertEquals("l2_flags.LAND", BaerConstants.BITMASK_PARAM_DEFAULT);
        assertEquals("aer_phase_lut", BaerConstants.AER_PHASE_PARAM_NAME);
    }

    /**
     * Tests all constants concerning the output product for correctness.
     */
    public void testOutputProductConstants() {
        assertEquals(13, BaerConstants.NUM_IN_REFLEC_BANDS);
        assertEquals(13, BaerConstants.NUM_OUT_REFLEC_BANDS);
        assertEquals("AOT_412", BaerConstants.AOT_412_BAND_NAME);
        assertEquals("AOT_560", BaerConstants.AOT_560_BAND_NAME);
        assertEquals("ALPHA", BaerConstants.ALPHA_BAND_NAME);
        assertEquals("TOA_VEG", BaerConstants.TOA_VEG_BAND_NAME);
        assertEquals("BAER_FLAGS", BaerConstants.OUT_FLAGS_BAND_NAME);
    }

    /**
     * Tests the parameter set used for the processor configuration for correctness
     */
    public void testConfigurationConstants() {
        assertEquals("BaerConfig", BaerConstants.CONFIGURATION_TAG);
        assertEquals("Parameter", BaerConstants.PARAMETER_TAG);
        assertEquals("name", BaerConstants.ATTRIB_NAME);
        assertEquals("value", BaerConstants.ATTRIB_VALUE);
        assertEquals("ndvi_tuning", BaerConstants.NDVI_FILE_ATTRIB_NAME);
        assertEquals("aerosol_phase", BaerConstants.AER_PHASE_FILE_ATTRIB_NAME);
        assertEquals("rel_aerosol_phase", BaerConstants.REL_AER_PHASE_FILE_ATTRIB_NAME);
        assertEquals("ground_reflectance", BaerConstants.GROUND_REFLECTANCE_FILE_ATTRIB_NAME);
        assertEquals("soil_fraction", BaerConstants.SOIL_FRACTION_FILE_ATTRIB_NAME);
        assertEquals("f_tuning", BaerConstants.F_TUNING_FILE_ATTRIB_NAME);
        assertEquals("aerosol_diffuse_transmission", BaerConstants.AER_DIFF_TRANSM_FILE_ATTRIB_NAME);
         assertEquals("hemispherical_reflectance", BaerConstants.HEMISPH_REFLEC_FILE_ATTRIB_NAME);
    }

    /**
     * Tests the parameter set used for the auxiliary files for correctness
     */
    public void testAuxFileConstants() {
        assertEquals("version", BaerConstants.AUX_VERSION_KEY);
        assertEquals("description", BaerConstants.AUX_DESCRIPTION_KEY);

        assertEquals("ndvi", BaerConstants.AUX_NDVI_KEY);
        assertEquals(0.6, BaerConstants.AUX_NDVI_DEFAULT, 1e-6);

        assertEquals("soil_fraction", BaerConstants.AUX_SOIL_FRACTION_KEY);
        assertEquals(1.3, BaerConstants.AUX_SOIL_FRACTION_DEFAULT, 1e-6);

        assertEquals("f_tuning", BaerConstants.AUX_F_TUNING_KEY);
        assertEquals(1.65, BaerConstants.AUX_F_TUNING_DEFAULT, 1e-6);

        assertEquals(3, BaerConstants.AUX_NUM_REL_PHASE_COEFFS);
        assertEquals("a.", BaerConstants.AUX_REL_PHASE_KEY_STUB);

        assertEquals(5, BaerConstants.AUX_NUM_AER_DIFF_TRANSM_COEFFS);
        assertEquals("caer.", BaerConstants.AUX_AER_DIFF_TRANSM_KEY_STUB);

        assertEquals(5, BaerConstants.AUX_NUM_RAY_DIFF_TRANSM_COEFFS);
        assertEquals("cray.", BaerConstants.AUX_RAY_DIFF_TRANSM_KEY_STUB);

        assertEquals(5, BaerConstants.AUX_NUM_HEMISPH_REFLEC_COEFFS);
        assertEquals("crhem.", BaerConstants.AUX_HEMISPH_REFLEC_KEY_STUB);

        assertEquals("AerLutList", BaerConstants.AER_LUT_LIST_TAG);
        assertEquals("AerLut", BaerConstants.AER_LUT_TAG);
        assertEquals("Band", BaerConstants.AER_BAND_TAG);
        assertEquals("Parameter", BaerConstants.AER_PARAMETER_TAG);
        assertEquals("name", BaerConstants.AUX_LUT_NAME_KEY);
        assertEquals("value", BaerConstants.AUX_LUT_VALUE_KEY);
        assertEquals("bands", BaerConstants.AUX_LUT_NUM_BANDS_KEY);

        assertEquals("GndReflList", BaerConstants.GND_REFL_LIST_TAG);
        assertEquals("GndRefl", BaerConstants.GND_REFL_TAG);
        assertEquals("Band", BaerConstants.GND_REFL_BAND_TAG);
        assertEquals("name", BaerConstants.GND_REFL_NAME_KEY);
        assertEquals("description", BaerConstants.GND_REFL_DESCRIPTION_KEY);
        assertEquals("ground", BaerConstants.GND_REFL_GROUND_KEY);
        assertEquals("VEG", BaerConstants.GND_REFL_VEG_TYPE);
        assertEquals("SOIL", BaerConstants.GND_REFL_SOIL_TYPE);
        assertEquals("index", BaerConstants.GND_REFL_INDEX_KEY);
        assertEquals("reflec", BaerConstants.GND_REFL_REFLEC_KEY);

    }

    /**
     * Tests the parameter set used for the metadata for correctness
     */
    public void testMetadataConstants() {
        assertEquals("SRC_METADATA", BaerConstants.SRC_METADATA_NAME);
        assertEquals("MPH", BaerConstants.MPH_METADATA_NAME);
        assertEquals("PRODUCT", BaerConstants.PRODUCT_METADATA_NAME);
        assertEquals("SRC_PRODUCT", BaerConstants.SRC_PRODUCT_METADATA_NAME);
        assertEquals("PROCESSOR", BaerConstants.PROCESSOR_METADATA_NAME);
        assertEquals("PROCESSOR_VERSION", BaerConstants.PROCESSOR_VERSION_METADATA_NAME);
        assertEquals("PROC_TIME", BaerConstants.PROCESSING_TIME_METADATA_NAME);

        assertEquals("AUX_FILE_NAME", BaerConstants.AUX_FILE_NAME_METADATA_NAME);
        assertEquals("AUX_FILE_VERSION", BaerConstants.AUX_FILE_VERSION_METADATA_NAME);
        assertEquals("AUX_FILE_DESCRIPTION", BaerConstants.AUX_FILE_DESCRIPTION_METADATA_NAME);
        assertEquals("unknown", BaerConstants.AUX_VAL_UNKNOWN);
        assertEquals("none", BaerConstants.AUX_VAL_NONE);

        // NDVI
        assertEquals("NDVI_TUNING", BaerConstants.NDVI_AUX_METADATA_NAME);
        // soil fraction
        assertEquals("SOIL_FRACTION", BaerConstants.SOIL_FRACTION_AUX_METADATA_NAME);
        // f tuning
        assertEquals("F_TUNING", BaerConstants.F_TUNING_AUX_METADATA_NAME);
        // relative aerosol phase
        assertEquals("REL_AEROSOL_PHASE", BaerConstants.REL_AER_PHASE_AUX_METADATA_NAME);
        assertEquals("a.", BaerConstants.REL_AER_PHASE_A_METADATA_STUB);
        // aersosol phase LUT
        assertEquals("AER_PHASE_LUT", BaerConstants.AER_PHASE_LUT_AUX_METADATA_NAME);
        // ground reflectances
        assertEquals("GROUND_REFLEC", BaerConstants.GND_REFLEC_AUX_METADATA_NAME);
        // aerosol diffuse transmission
        assertEquals("AEROSOL_DIFFUSE_TRANSMISSION", BaerConstants.AER_DIFF_TRANSM_AUX_METADATA_NAME);
        assertEquals("caer.", BaerConstants.AER_DIFF_TRANSM_METADATA_STUB);

        // hemispherical reflectzance
        assertEquals("HEMISPHERICAL_REFLECTANCE", BaerConstants.HEMISPH_REFLEC_AUX_METADATA_NAME);
        assertEquals("crhem.", BaerConstants.HEMISPH_REFLEC_METADATA_STUB);
    }
}
