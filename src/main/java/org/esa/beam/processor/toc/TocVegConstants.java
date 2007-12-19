/*
 * $Id: TocVegConstants.java,v 1.8 2006/03/28 15:10:46 meris Exp $
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

public class TocVegConstants {

    // processor name, version and copyright
    //
    public static final String PROC_NAME = "MERIS TOC-VEG";
    public static final String PROC_VERSION = "0.6.0";
    public static final String PROC_COPYRIGHT = "Copyright 2005 by NOVELTIS";

    public static final String LOGGER_NAME = "beam.processor.org.esa.beam.processor.toc";
    public static final String DEFAULT_LOG_PREFIX = "toc";

    public static final String REQUEST_TYPE = "TOC_VEG_PROCESS";

    public static final String DEFAULT_OUTPUT_FILE_NAME = "MER_TOC_VEG.dim";
    public static final String AUXDATA_DIR_PROPERTY = "toc.auxdata.dir";
    public static final String AUXDATA_DIR = "toc";
    public static final String CONFIG_FILE = "config.xml";

    // processor configuration tags
    //
    public static final String CONFIGURATION_TAG = "TocVegConfig";
    public static final String PARAMETER_TAG = "Parameter";
    public static final String ATTRIB_NAME = "name";
    public static final String ATTRIB_VALUE = "value";

    public static final String NORMALISATION_FACTOR_ATTRIB_NAME = "normalisation_factor";

    public static final String INPUT_STATISTICS_ATTRIB_NAME = "input_statistics";
    public static final String OUTPUT_STATISTICS_ATTRIB_NAME = "output_statistics";

    // aux file access constants
    //
    public static final String AUX_VERSION_KEY = "version";
    public static final String AUX_DESCRIPTION_KEY = "description";

    public static final String NORMALISATION_FACTOR_AUX_KEY = "norm_factor";
    public static final double NORMALISATION_FACTOR_DEFAULT = 0.66;

    public static final String THETA_S_MEAN_AUX_KEY = "theta_s_mean";
    public static final double THETA_S_MEAN_DEFAULT = 41.491;
    public static final String THETA_S_STD_AUX_KEY = "theta_s_std";
    public static final double THETA_S_STD_DEFAULT = 11.712;
    public static final String THETA_V_MEAN_AUX_KEY = "theta_v_mean";
    public static final double THETA_V_MEAN_DEFAULT = 22.172;
    public static final String THETA_V_STD_AUX_KEY = "theta_v_std";
    public static final double THETA_V_STD_DEFAULT = 10.55;
    public static final String COS_PHI_MEAN_AUX_KEY = "cos_phi_mean";
    public static final double COS_PHI_MEAN_DEFAULT = 0.022895;
    public static final String COS_PHI_STD_AUX_KEY = "cos_phi_std";
    public static final double COS_PHI_STD_DEFAULT = 0.77433;
    public static final String R_MEAN_AUX_KEY = "r_mean";
    public static final double R_MEAN_DEFAULT = 0.19841;
    public static final String R_STD_AUX_KEY = "r_std";
    public static final double R_STD_DEFAULT = 0.16899;

    public static final String NN_AUX_KEY = "nn";


    public static final double FAPAR_MEAN_DEFAULT = 0.70321;
    public static final String FAPAR_MEAN_KEY = "fAPAR_mean";
    public static final double FAPAR_STD_DEFAULT = 0.22361;
    public static final String FAPAR_STD_KEY = "fAPAR_std";
    public static final double FAPAR_MIN_DEFAULT = 3.7167E-5;
    public static final String FAPAR_MIN_KEY = "fAPAR_min";
    public static final double FAPAR_MAX_DEFAULT = 0.98728;
    public static final String FAPAR_MAX_KEY = "fAPAR_max";
    public static final double FCOVER_MEAN_DEFAULT = 0.60202;
    public static final String FCOVER_MEAN_KEY = "fCover_mean";
    public static final double FCOVER_STD_DEFAULT = 0.26446;
    public static final String FCOVER_STD_KEY = "fCover_std";
    public static final double FCOVER_MIN_DEFAULT = 2.9771E-5;
    public static final String FCOVER_MIN_KEY = "fCover_min";
    public static final double FCOVER_MAX_DEFAULT = 0.99556;
    public static final String FCOVER_MAX_KEY = "fCover_max";
    public static final double LAI_MEAN_DEFAULT = 3.1099;
    public static final String LAI_MEAN_KEY = "LAI_mean";
    public static final double LAI_STD_DEFAULT = 1.9554;
    public static final String LAI_STD_KEY = "LAI_std";
    public static final double LAI_MIN_DEFAULT = 6.0712E-5;
    public static final String LAI_MIN_KEY = "LAI_min";
    public static final double LAI_MAX_DEFAULT = 7.9669;
    public static final String LAI_MAX_KEY = "LAI_max";
    public static final double LAIXCAB_MEAN_DEFAULT = 173.07;
    public static final String LAIXCAB_MEAN_KEY = "LAIxCab_mean";
    public static final double LAIXCAB_STD_DEFAULT = 137.97;
    public static final String LAIXCAB_STD_KEY = "LAIxCab_std";
    public static final double LAIXCAB_MIN_DEFAULT = 0.00091478;
    public static final String LAIXCAB_MIN_KEY = "LAIxCab_min";
    public static final double LAIXCAB_MAX_DEFAULT = 765.6;
    public static final String LAIXCAB_MAX_KEY = "LAIxCab_max";


    // input product constants
    //
    public static final int NUM_BANDS = 11;
    public static final String[] REFLEC_BAND_NAMES = new String[]{"reflec_3",
                                                                  "reflec_4",
                                                                  "reflec_5",
                                                                  "reflec_6",
                                                                  "reflec_7",
                                                                  "reflec_8",
                                                                  "reflec_9",
                                                                  "reflec_10",
                                                                  "reflec_12",
                                                                  "reflec_13",
                                                                  "reflec_14"
    };
    public static final String REFLEC_3_BAND_NAME = "reflec_3";
    public static final String REFLEC_4_BAND_NAME = "reflec_4";
    public static final String REFLEC_5_BAND_NAME = "reflec_5";
    public static final String REFLEC_7_BAND_NAME = "reflec_7";
    public static final String TOA_VEG_BAND_NAME = "toa_veg";
    public static final String L2_FLAGS_BAND_NAME = "l2_flags";
    
    public static final String BAER_FLAGS_BAND_NAME = "BAER_FLAGS";

    public static final String LAT_TIEPOINT_NAME = "latitude";
    public static final String LON_TIEPOINT_NAME = "longitude";
    public static final String SZA_TIEPOINT_NAME = "sun_zenith";
    public static final String SAA_TIEPOINT_NAME = "sun_azimuth";
    public static final String VZA_TIEPOINT_NAME = "view_zenith";
    public static final String VAA_TIEPOINT_NAME = "view_azimuth";

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

    public static final String DELTA_FAPAR_BAND_NAME = "delta_fAPAR";
    public static final String DELTA_FAPAR_BAND_DESCRIPTION = "fAPAR mis-match";

    public static final String VEG_FLAGS_BAND_NAME = "TOC_VEG_FLAGS";
    public static final String VEG_FLAGS_BAND_DESCRIPTION = "VEG flags dataset";

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

    public static final String NORMALISATION_AUX_METADATA_NAME = "NORMALISATION_FACTOR";
    public static final String INPUT_STATISTICS_AUX_METADATA_NAME = "INPUT_STATISTICS";
    public static final String OUTPUT_STATISTICS_AUX_METADATA_NAME = "OUTPUT_STATISTICS";

    // processing request constants
    //
    public static final String BITMASK_PARAM_NAME = "bitmask";
    public static final String BITMASK_PARAM_LABEL = "Bitmask";
    public static final String BITMASK_PARAM_DESCRIPTION = "Please enter a bitmask expression";
    public static final String BITMASK_PARAM_DEFAULT = "l2_flags.LAND";
    
    // help file access constants
    //
    public static final String HELPSET_RESOURCE_PATH = "help/toc/TocVeg.hs";
    public static final String HELP_ID = "TocVegOverview";
    
}
