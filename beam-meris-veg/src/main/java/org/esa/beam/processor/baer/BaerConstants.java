package org.esa.beam.processor.baer;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Jul 22, 2003
 * Time: 8:59:48 AM
 * To change this template use Options | File Templates.
 */
public class BaerConstants {

    // processor name, version and copyright
    // -------------------------------------
    public static final String PROC_NAME = "MERIS BAER";
    public static final String PROC_VERSION = "1.1";
    public static final String PROC_COPYRIGHT = "Copyright 2005 by NOVELTIS";

    // Help file access constants
    //
    public static final String HELPSET_RESOURCE_PATH = "help/baer/Baer.hs";
    public static final String HELP_ID = "BaerProcessor";

    // processor configuration
    // -----------------------

    public static final String DEFAULT_OUTPUT_FILE_NAME = "MER_BAER.dim";
    public static final String AUXDATA_DIR_PROPERTY = "baer.auxdata.dir";
    public static final String AUXDATA_DIR = "baer";
    public static final String CONFIG_FILE = "config.xml";

    // logging constants
    // -----------------
    public static final String LOGGER_NAME = "beam.processor.baer";
    public static final String DEFAULT_LOG_PREFIX = "baer";

    // the request type
    // ----------------
    public static final String REQUEST_TYPE = "BAER";



    // contstans for the bitmask parameter
    // -----------------------------------
    public static final String BITMASK_PARAM_NAME = "bitmask";
    public static final String BITMASK_PARAM_LABEL = "Bitmask";
    public static final String BITMASK_PARAM_DESCRIPTION = "Bitmask expression which identifies valid pixels";
    public static final String BITMASK_PARAM_DEFAULT = "l2_flags.LAND";

    // constants for the aerosol phase LUT parameter
    // ---------------------------------------------
    public static final String AER_PHASE_PARAM_NAME = "aer_phase_lut";
    public static final String AER_PHASE_PARAM_LABEL = "Aerosol phase look-up table";
    public static final String AER_PHASE_PARAM_DESCRIPTION = "Look-up table for the aerosol phase function coefficients";

    // constants for the smac parameter
    // --------------------------------
    public static final String AEROSOL_TYPE_PARAM_NAME = "aero_type";
    public static final String AER_TYPE_DESERT = "Desert";
    public static final String AER_TYPE_CONTINENTAL = "Continental";
    public static final String LOG_MSG_INVALID_AEROSOL = "Invalid aerosol type ";
    public static final String LOG_MSG_OPEN_COEFF_ERROR = "Unable to open coefficient map file!";
    public static final String SMAC_PARAM_DEFAULT = "UBAC";
    public static final String SMAC_PARAM_LABEL = "Atmospheric correction algorithm";
    public static final String SMAC_PARAM_DESCRIPTION = "Select atmospheric correction algorithm";
    public static final String SMAC_PARAM_NAME = "atm_corr_method";
    public static final String[] SMAC_PARAM_VALUES ={"SMAC","UBAC"};

    public static final String USE_CLOUD_PARAM_NAME = "cloud_process";
    public static final String USE_CLOUD_PARAM_DESCRIPTION = "Compute and output cloud mask";
    public static final String USE_CLOUD_PARAM_LABEL = "Generate cloud mask only";
    
    public static final String USE_BAER_PARAM_NAME = "baer_process";
    public static final String USE_BAER_PARAM_DESCRIPTION = "Perform BAER and generate aerosol products";
    public static final String USE_BAER_PARAM_LABEL = "Perform BAER and generate aerosol products";
    
    public static final String USE_ATM_COR_PARAM_NAME = "atm_cor_process";
    public static final String USE_ATM_COR_PARAM_DESCRIPTION = "Perform atmospheric correction and generate corrected reflectances";
    public static final String USE_ATM_COR_PARAM_LABEL = "Generate atmospherically corrected reflectances";



    // messaging constants
    // -------------------
    public static final String LOG_MSG_GENERATE_PIXEL = "Generating output pixels...";

    // output product: bands names and such things
    // -------------------------------------------
    public static final String PRODUCT_TYPE_APPENDIX = "_BAER";
    public static final int NUM_IN_REFLEC_BANDS = 13;
    public static final int NUM_OUT_REFLEC_BANDS = 13;
    public static final String OUT_REFLEC_BAND_DESCRIPTION = "Surface reflectance";

    public static final String AOT_412_BAND_NAME = "AOT_412";
    public static final String AOT_412_BAND_DESCRIPTION = "Aerosol optical thickness at 412 nm";

    public static final String AOT_560_BAND_NAME = "AOT_560";
    public static final String AOT_560_BAND_DESCRIPTION = "Aerosol optical thickness at 560 nm";

    public static final String AOT_865_BAND_NAME = "AOT_865";
    public static final String AOT_865_BAND_DESCRIPTION = "Aerosol optical thickness at 865 nm";

    public static final String AOT_440_BAND_NAME = "AOT_440";
    public static final String AOT_440_BAND_DESCRIPTION = "Aerosol optical thickness at 440 nm";

    public static final String AOT_470_BAND_NAME = "AOT_470";
    public static final String AOT_470_BAND_DESCRIPTION = "Aerosol optical thickness at 470 nm";

    public static final String AOT_550_BAND_NAME = "AOT_550";
    public static final String AOT_550_BAND_DESCRIPTION = "Aerosol optical thickness at 550 nm";

    public static final String AOT_665_BAND_NAME = "AOT_665";
    public static final String AOT_665_BAND_DESCRIPTION = "Aerosol optical thickness at 665 nm";

    public static final String AERO_412_BAND_NAME = "AERO_412_ddv";
    public static final String AERO_412_BAND_DESCRIPTION = "Aerosol at 412 nm";

    public static final String AERO_565_BAND_NAME = "AERO_565_ddv";
    public static final String AERO_565_BAND_DESCRIPTION = "Aerosol at 565 nm";

    public static final String AERO_865_BAND_NAME = "AERO_865_ddv";
    public static final String AERO_865_BAND_DESCRIPTION = "Aerosol at 865 nm";

    public static final String AERO_ALPHA_BAND_NAME = "AERO_alpha_ddv";
    public static final String AERO_ALPHA_BAND_DESCRIPTION = "Aerosol Alpha";

    public static final String ALPHA_BAND_NAME = "ALPHA";
    public static final String ALPHA_BAND_DESCRIPTION = "Aerosol alpha parameter";

    public static final String BAND_LAT_NAME = "BAND_LATITUDE";
    public static final String BAND_LAT_DESCRIPTION = "Latitude";

    public static final String BAND_LON_NAME = "BAND_LONGITUDE";
    public static final String BAND_LON_DESCRIPTION = "Longitude";

    public static final String TOA_VEG_BAND_NAME = "TOA_VEG";
    public static final String TOA_VEG_BAND_DESCRIPTION = "MERIS global vegetation index";

    public static final String CLOUD_BAND_NAME = "BAND_CLOUD";
    public static final String CLOUD_BAND_DESCRIPTION = "Cloud";

    public static final String OUT_FLAGS_BAND_NAME = "BAER_FLAGS";
    public static final String OUT_FLAGS_BAND_DESCRIPTION = "Quality flags";

    public static final String VALID_PIXEL_EXPRESSION = "!BAER_FLAGS.INVALID_INPUT && !BAER_FLAGS.INVALID_OUTPUT";

    // constants for the org.esa.beam.processor.baer alogorithm
    // ---------------------------------
    public static double MU_LUT = 0.709482;
    public static final int NUM_BANDS = 15;
    public static boolean USED_MERIS_BAND[]={true, true, true, true, true,
                                            true, true, true, true, true,
                                            false, true, true, true, false};
    public static double MERIS_BANDS[] = {0.4125, 0.4424, 0.4897, 0.5097, 0.5596,
                                          0.6196, 0.6646, 0.6809, 0.7084, 0.7534,
                                          0.760625, 0.7784, 0.8648, 0.8846, 0.900};
    public static boolean FIT_SELECTED_MERIS_BAND[]={true, true, true, true, true,
                                            true, true, false, false, false,
                                            false, false, false, false, false};
    public static double RMSD_CONST = 0.005;
    public static int ITERATION_MAX = 150;
    public static int CONST_MAX = 1;
    public static double PRESSURE_SEA = 1013.25;

    public static double COEFF[] = {1.5, 3.0, 3.80, 3.90, 4.50,
                                    3.50, 3.50, 1.0, 1.0, 1.0,
                                    0.0, 1.0, 1.0, 1.0, 0.0};
    // configuration tags
    // ------------------
    public static final String CONFIGURATION_TAG = "BaerConfig";
    public static final String PARAMETER_TAG = "Parameter";
    public static final String ATTRIB_NAME = "name";
    public static final String ATTRIB_VALUE = "value";
    public static final String NDVI_FILE_ATTRIB_NAME = "ndvi_tuning";
    public static final String AER_PHASE_FILE_ATTRIB_NAME = "aerosol_phase";
    public static final String REL_AER_PHASE_FILE_ATTRIB_NAME = "rel_aerosol_phase";
    public static final String GROUND_REFLECTANCE_FILE_ATTRIB_NAME = "ground_reflectance";
    public static final String SOIL_FRACTION_FILE_ATTRIB_NAME = "soil_fraction";
    public static final String F_TUNING_FILE_ATTRIB_NAME = "f_tuning";
    public static final String AER_DIFF_TRANSM_FILE_ATTRIB_NAME = "aerosol_diffuse_transmission";
    public static final String HEMISPH_REFLEC_FILE_ATTRIB_NAME = "hemispherical_reflectance";

    // aux file access constants
    // -------------------------
    public static final String AUX_VERSION_KEY = "version";
    public static final String AUX_DESCRIPTION_KEY = "description";

    public static final String AUX_NDVI_KEY = "ndvi";
    public static final double AUX_NDVI_DEFAULT = 0.6;

    public static final String AUX_SOIL_FRACTION_KEY = "soil_fraction";
    public static final double AUX_SOIL_FRACTION_DEFAULT = 1.3;

    public static final String AUX_F_TUNING_KEY = "f_tuning";
    public static final double AUX_F_TUNING_DEFAULT = 1.65;

    public static final int AUX_NUM_REL_PHASE_COEFFS = 3;
    public static final String AUX_REL_PHASE_KEY_STUB = "a.";

    public static final int AUX_NUM_AER_DIFF_TRANSM_COEFFS = 5;
    public static final String AUX_AER_DIFF_TRANSM_KEY_STUB = "caer.";

    public static final int AUX_NUM_RAY_DIFF_TRANSM_COEFFS = 5;
    public static final String AUX_RAY_DIFF_TRANSM_KEY_STUB = "cray.";

    public static final int AUX_NUM_HEMISPH_REFLEC_COEFFS = 5;
    public static final String AUX_HEMISPH_REFLEC_KEY_STUB = "crhem.";

    public static final String AER_LUT_LIST_TAG = "AerLutList";
    public static final String AER_LUT_TAG = "AerLut";
    public static final String AER_BAND_TAG = "Band";
    public static final String AER_PARAMETER_TAG = "Parameter";
    public static final String AUX_LUT_NAME_KEY = "name";
    public static final String AUX_LUT_VALUE_KEY = "value";
    public static final String AUX_LUT_NUM_BANDS_KEY = "bands";

    public static final String GND_REFL_LIST_TAG = "GndReflList";
    public static final String GND_REFL_TAG = "GndRefl";
    public static final String GND_REFL_BAND_TAG = "Band";
    public static final String GND_REFL_NAME_KEY = "name";
    public static final String GND_REFL_DESCRIPTION_KEY = "description";
    public static final String GND_REFL_GROUND_KEY = "ground";
    public static final String GND_REFL_VEG_TYPE = "VEG";
    public static final String GND_REFL_SOIL_TYPE = "SOIL";
    public static final String GND_REFL_INDEX_KEY = "index";
    public static final String GND_REFL_REFLEC_KEY = "reflec";


    // metadata constants
    // ------------------
    public static final String SRC_METADATA_NAME = "SRC_METADATA";
    public static final String MPH_METADATA_NAME = "MPH";
    public static final String PRODUCT_METADATA_NAME = "PRODUCT";
    public static final String SRC_PRODUCT_METADATA_NAME = "SRC_PRODUCT";
    public static final String PROCESSOR_METADATA_NAME = "PROCESSOR";
    public static final String PROCESSOR_VERSION_METADATA_NAME = "PROCESSOR_VERSION";
    public static final String PROCESSING_TIME_METADATA_NAME = "PROC_TIME";

    public static final String AUX_FILE_NAME_METADATA_NAME = "AUX_FILE_NAME";
    public static final String AUX_FILE_VERSION_METADATA_NAME = "AUX_FILE_VERSION";
    public static final String AUX_FILE_DESCRIPTION_METADATA_NAME = "AUX_FILE_DESCRIPTION";

    public static final String AUX_VAL_UNKNOWN = "unknown";
    public static final String AUX_VAL_NONE = "none";

    public static final String NDVI_AUX_METADATA_NAME = "NDVI_TUNING";

    public static final String SOIL_FRACTION_AUX_METADATA_NAME = "SOIL_FRACTION";

    public static final String F_TUNING_AUX_METADATA_NAME = "F_TUNING";

    public static final String REL_AER_PHASE_AUX_METADATA_NAME = "REL_AEROSOL_PHASE";
    public static final String REL_AER_PHASE_A_METADATA_STUB = "a.";

    public static final String AER_PHASE_LUT_AUX_METADATA_NAME = "AER_PHASE_LUT";

    public static final String GND_REFLEC_AUX_METADATA_NAME = "GROUND_REFLEC";

    public static final String AER_DIFF_TRANSM_AUX_METADATA_NAME = "AEROSOL_DIFFUSE_TRANSMISSION";
    public static final String AER_DIFF_TRANSM_METADATA_STUB = "caer.";

    public static final String HEMISPH_REFLEC_AUX_METADATA_NAME = "HEMISPHERICAL_REFLECTANCE";
    public static final String HEMISPH_REFLEC_METADATA_STUB = "crhem.";
}
