/*
 * $Id: ToaVegConstants.java,v 1.19 2006/03/23 17:20:38 meris Exp $
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

public class ToaVegConstants {

    // processor name, version and copyright
    //
    public static final String PROC_NAME = "MERIS TOA-VEG";
    public static final String PROC_VERSION = "1.1";
    public static final String PROC_COPYRIGHT = "Copyright 2005 by NOVELTIS";

    public static final String LOGGER_NAME = "beam.processor.toa";
    public static final String DEFAULT_LOG_PREFIX = "toa";

    public static final String REQUEST_TYPE = "TOA_VEG_PROCESS";

    // processor configuration
    //
    public static final String DEFAULT_OUTPUT_FILE_NAME = "MER_TOA_VEG.dim";
    public static final String AUXDATA_DIR_PROPERTY = "toa.auxdata.dir";
    public static final String AUXDATA_DIR = "toa";
    public static final String CONFIG_FILE = "config.xml";

    // processor configuration tags & attribute names
    //
    public static final String CONFIGURATION_TAG = "ToaVegConfig";
    public static final String PARAMETER_TAG = "Parameter";
    public static final String ATTRIB_NAME = "name";
    public static final String ATTRIB_VALUE = "value";

    public static final String INPUT_STATISTICS_ATTRIB_NAME = "input_statistics";
    public static final String OUTPUT_STATISTICS_ATTRIB_NAME = "output_statistics";
    public static final String UNCERTAINTY_ATTRIB_NAME = "uncertainty";


    // Help file access constants
    //
    public static final String HELPSET_RESOURCE_PATH = "help/toa/ToaVeg.hs";
    public static final String HELP_ID = "ToaVegOverview";

    // aux file access constants
    //
    public static final String AUX_VERSION_KEY = "version";
    public static final String AUX_DESCRIPTION_KEY = "description";

    public static final String THETA_S_MIN_AUX_KEY = "theta_s_min";
    public static final double THETA_S_MIN_DEFAULT = 0.31792995;
    public static final String THETA_S_MAX_AUX_KEY = "theta_s_max";
    public static final double THETA_S_MAX_DEFAULT = 1.04718879;
    public static final String THETA_V_MIN_AUX_KEY = "theta_v_min";
    public static final double THETA_V_MIN_DEFAULT = 0.00000099616;
    public static final String THETA_V_MAX_AUX_KEY = "theta_v_max";
    public static final double THETA_V_MAX_DEFAULT = 0.68590864;
    public static final String COS_PHI_MIN_AUX_KEY = "cos_phi_min";
    public static final double COS_PHI_MIN_DEFAULT =-0.999999;
    public static final String COS_PHI_MAX_AUX_KEY = "cos_phi_max";
    public static final double COS_PHI_MAX_DEFAULT = 1;
    public static final String R1_MIN_AUX_KEY = "r1_min";
    public static final double R1_MIN_DEFAULT = 0.0891215;
    public static final String R1_MAX_AUX_KEY = "r1_max";
    public static final double R1_MAX_DEFAULT = 0.41778551;
    public static final String R2_MIN_AUX_KEY = "r2_min";
    public static final double R2_MIN_DEFAULT = 0.07151076;
    public static final String R2_MAX_AUX_KEY = "r2_max";
    public static final double R2_MAX_DEFAULT = 0.46906965;
    public static final String R3_MIN_AUX_KEY = "r3_min";
    public static final double R3_MIN_DEFAULT = 0.05094766;
    public static final String R3_MAX_AUX_KEY = "r3_max";
    public static final double R3_MAX_DEFAULT = 0.52309721;
    public static final String R4_MIN_AUX_KEY = "r4_min";
    public static final double R4_MIN_DEFAULT = 0.04281358;
    public static final String R4_MAX_AUX_KEY = "r4_max";
    public static final double R4_MAX_DEFAULT = 0.56118259;
    public static final String R5_MIN_AUX_KEY = "r5_min";
    public static final double R5_MIN_DEFAULT = 0.02512556;
    public static final String R5_MAX_AUX_KEY = "r5_max";
    public static final double R5_MAX_DEFAULT = 0.61873024;
    public static final String R6_MIN_AUX_KEY = "r6_min";
    public static final double R6_MIN_DEFAULT = 0.01927991;
    public static final String R6_MAX_AUX_KEY = "r6_max";
    public static final double R6_MAX_DEFAULT = 0.63023794;
    public static final String R7_MIN_AUX_KEY = "r7_min";
    public static final double R7_MIN_DEFAULT = 0.01944251;
    public static final String R7_MAX_AUX_KEY = "r7_max";
    public static final double R7_MAX_DEFAULT = 0.69832564;
    public static final String R8_MIN_AUX_KEY = "r8_min";
    public static final double R8_MIN_DEFAULT = 0.01832826;
    public static final String R8_MAX_AUX_KEY = "r8_max";
    public static final double R8_MAX_DEFAULT = 0.7548423;
    public static final String R9_MIN_AUX_KEY = "r9_min";
    public static final double R9_MIN_DEFAULT = 0.01060944;
    public static final String R9_MAX_AUX_KEY = "r9_max";
    public static final double R9_MAX_DEFAULT = 0.76507422;
    public static final String R10_MIN_AUX_KEY = "r10_min";
    public static final double R10_MIN_DEFAULT = 0.0595014;
    public static final String R10_MAX_AUX_KEY = "r10_max";
    public static final double R10_MAX_DEFAULT = 0.87085622;
    public static final String R11_MIN_AUX_KEY = "r11_min";
    public static final double R11_MIN_DEFAULT = 0.07529407;
    public static final String R11_MAX_AUX_KEY = "r11_max";
    public static final double R11_MAX_DEFAULT = 0.92774513;
    public static final String R12_MIN_AUX_KEY = "r12_min";
    public static final double R12_MIN_DEFAULT = 0.08980312;
    public static final String R12_MAX_AUX_KEY = "r12_max";
    public static final double R12_MAX_DEFAULT = 1.10054001;
    public static final String R13_MIN_AUX_KEY = "r13_min";
    public static final double R13_MIN_DEFAULT = 0.09010581;
    public static final String R13_MAX_AUX_KEY = "r13_max";
    public static final double R13_MAX_DEFAULT = 1.1017671;


    public static final String NN_LAI_AUX_KEY = "nn_LAI";
    public static final String NN_FCOVER_AUX_KEY = "nn_fCover";
    public static final String NN_FAPAR_AUX_KEY = "nn_fAPAR";
    public static final String NN_LAIXCAB_AUX_KEY = "nn_LAIxCab";


    public static final double FAPAR_MIN_DEFAULT = 0.000028034;
    public static final String FAPAR_MIN_KEY = "fAPAR_min";
    public static final double FAPAR_MAX_DEFAULT = 0.96796425;
    public static final String FAPAR_MAX_KEY = "fAPAR_max";
    public static final double FCOVER_MIN_DEFAULT = 0.000018282;
    public static final String FCOVER_MIN_KEY = "fCover_min";
    public static final double FCOVER_MAX_DEFAULT = 0.98850956;
    public static final String FCOVER_MAX_KEY = "fCover_max";
    public static final double LAI_MIN_DEFAULT = 0.000036178;
    public static final String LAI_MIN_KEY = "LAI_min";
    public static final double LAI_MAX_DEFAULT = 5.99982132;
    public static final String LAI_MAX_KEY = "LAI_max";
    public static final double LAIXCAB_MIN_DEFAULT = 0.00167064;
    public static final String LAIXCAB_MIN_KEY = "LAIxCab_min";
    public static final double LAIXCAB_MAX_DEFAULT = 594.623954;
    public static final String LAIXCAB_MAX_KEY = "LAIxCab_max";

    public static final double FAPAR_UNC0_DEFAULT = 0.02205;
    public static final String FAPAR_UNC0_KEY = "fAPAR.0";
    public static final double FAPAR_UNC1_DEFAULT = 0.2312;
    public static final String FAPAR_UNC1_KEY = "fAPAR.1";
    public static final double FAPAR_UNC2_DEFAULT = -0.2422;
    public static final String FAPAR_UNC2_KEY = "fAPAR.2";
    public static final double FCOVER_UNC0_DEFAULT = 0.0004322;
    public static final String FCOVER_UNC0_KEY = "fCover.0";
    public static final double FCOVER_UNC1_DEFAULT = 0.3711;
    public static final String FCOVER_UNC1_KEY = "fCover.1";
    public static final double FCOVER_UNC2_DEFAULT = -0.3485;
    public static final String FCOVER_UNC2_KEY = "fCover.2";
    public static final double LAI_UNC0_DEFAULT = -0.1535;
    public static final String LAI_UNC0_KEY = "LAI.0";
    public static final double LAI_UNC1_DEFAULT = 0.776;
    public static final String LAI_UNC1_KEY = "LAI.1";
    public static final double LAI_UNC2_DEFAULT = -0.1207;
    public static final String LAI_UNC2_KEY = "LAI.2";
    public static final double LAIXCAB_UNC0_DEFAULT = 0.5928;
    public static final String LAIXCAB_UNC0_KEY = "LAIxCab.0";
    public static final double LAIXCAB_UNC1_DEFAULT = 0.5024;
    public static final String LAIXCAB_UNC1_KEY = "LAIxCab.1";
    public static final double LAIXCAB_UNC2_DEFAULT = -0.0007351;
    public static final String LAIXCAB_UNC2_KEY = "LAIxCab.2";



    public static final String TRAIN_DB_AUX_KEY = "nn_training_db";
    public static final String TRAIN_DB_VERSION_KEY = "version";
    public static final String TRAIN_DB_DESCRIPTION_KEY = "description";
    public static final String TRAIN_DB_NUM_BANDS_KEY = "num_bands";
    public static final String TRAIN_DB_NUM_SPECTRA_KEY = "num_spectra";


    // input product constants
    //
    public static final int NUM_BANDS = 13;
    public static final String[] REFLEC_BAND_NAMES = new String[]{"radiance_1",
                                                                  "radiance_2",
                                                                  "radiance_3",
                                                                  "radiance_4",
                                                                  "radiance_5",
                                                                  "radiance_6",
                                                                  "radiance_7",
                                                                  "radiance_8",
                                                                  "radiance_9",
                                                                  "radiance_10",
                                                                  "radiance_12",
                                                                  "radiance_13",
                                                                  "radiance_14"
    };



    public static final String L1_FLAGS_BAND_NAME = "L1_FLAGS";

    public static final String LAT_TIEPOINT_NAME = "latitude";
    public static final String LON_TIEPOINT_NAME = "longitude";
    public static final String SZA_TIEPOINT_NAME = "sun_zenith";
    public static final String SAA_TIEPOINT_NAME = "sun_azimuth";
    public static final String VZA_TIEPOINT_NAME = "view_zenith";
    public static final String VAA_TIEPOINT_NAME = "view_azimuth";
    public static final String PRESS_TIEPOINT_NAME = "atm_press";

    // output product constants
    //
    public static final String PRODUCT_TYPE_APPENDIX = "_VEG";

    public static final String LAI_BAND_NAME = "LAI";
    public static final String LAI_BAND_DESCRIPTION = "Leaf Area Index";
    public static final String LAI_BAND_UNIT = "m^2 / m^2";

    public static final String FCOVER_BAND_NAME = "fCover";
    public static final String FCOVER_BAND_DESCRIPTION = "Fraction of vegetation";

    public static final String LAIXCAB_BAND_NAME = "LAIxCab";
    public static final String LAIXCAB_BAND_DESCRIPTION = "Canopy chlorophyll content";
    public static final String LAIXCAB_BAND_UNIT = "g / m^2";

    public static final String FAPAR_BAND_NAME = "fAPAR";
    public static final String FAPAR_BAND_DESCRIPTION = "Fraction of Absorbed Photosynthetically Active Radiation";

    public static final String SIGMA_LAI_BAND_NAME= "sigma_LAI";
    public static final String SIGMA_LAI_BAND_DESCRIPTION = "LAI Uncertainty";

    public static final String SIGMA_FCOVER_BAND_NAME= "sigma_fCover";
    public static final String SIGMA_FCOVER_BAND_DESCRIPTION = "fCover Uncertainty";

    public static final String SIGMA_FAPAR_BAND_NAME= "sigma_fApar";
    public static final String SIGMA_FAPAR_BAND_DESCRIPTION = "fApart Uncertainty";

    public static final String SIGMA_LAIXCAB_BAND_NAME= "sigma_LAIxCab";
    public static final String SIGMA_LAIXCAB_BAND_DESCRIPTION = "LAIxCab Uncertainty";

    public static final String VEG_FLAGS_BAND_NAME = "TOA_VEG_FLAGS";
    public static final String VEG_FLAGS_BAND_DESCRIPTION = "TOA_VEG flags dataset";

    // messaging constants
    public static final String LOG_MSG_GENERATE_PIXEL = "Generating output pixels ...";

    // metadata constants
    //
    public static final String SRC_METADATA_NAME = "SRC_METADATA";
    public static final String MPH_METADATA_NAME = "MPH";
    public static final String PRODUCT_METADATA_NAME = "PRODUCT";
    public static final String SRC_PRODUCT_METADATA_NAME = "SRC_PRODUCT";
    public static final String PROCESSOR_METADATA_NAME = "PROCESSOR";
    public static final String PROCESSOR_VERSION_METADATA_NAME = "PROCESSOR_VERSION";
    public static final String PROCESSING_TIME_METADATA_NAME = "PROC_TIME";

    public static final String AUX_VAL_UNKNOWN = "unknown";
    public static final String AUX_VAL_NONE = "none";
    public static final String AUX_FILE_NAME_METADATA_NAME = "AUX_FILE_NAME";
    public static final String AUX_FILE_VERSION_METADATA_NAME = "AUX_FILE_VERSION";
    public static final String AUX_FILE_DESCRIPTION_METADATA_NAME = "AUX_FILE_DESCRIPTION";

    public static final String INPUT_STATISTICS_AUX_METADATA_NAME = "INPUT_STATISTICS";
    public static final String OUTPUT_STATISTICS_AUX_METADATA_NAME = "OUTPUT_STATISTICS";
    public static final String UNCERTAINTY_AUX_METADATA_NAME = "UNCERTAINTY";



    // processing request constants
    //
    public static final String BITMASK_PARAM_NAME = "bitmask";
    public static final String BITMASK_PARAM_LABEL = "Bitmask";
    public static final String BITMASK_PARAM_DESCRIPTION = "Please enter a bitmask expression";
    public static final String BITMASK_PARAM_DEFAULT = "l1_flags.LAND_OCEAN";
}
