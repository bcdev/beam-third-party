/*
 * $Id: VegProcessorConfiguration.java,v 1.1.1.1 2005/02/15 11:13:40 meris Exp $
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

import org.esa.beam.util.Guardian;

public class VegProcessorConfiguration {

    protected String _normalisationFactorFile;
    protected String _inputStatisticsFile;
    protected String _outputStatisticsFile;
    protected String _nn_LaiFile;
    protected String _nn_fCoverFile;
    protected String _nn_fAPARFile;
    protected String _nn_LAIxCabFile;
    protected String _uncertaintyFile;

    /**
     * Constructs the object with default parameters
     */
    public VegProcessorConfiguration() {
        _normalisationFactorFile = "";
        _inputStatisticsFile = "";
        _outputStatisticsFile = "";
        _nn_LaiFile = "";
        _nn_fCoverFile = "";
        _nn_fAPARFile = "";
        _nn_LAIxCabFile = "";
        _uncertaintyFile = "";
    }

    /**
     * Retrieves the auxiliary file for the normalisation factors
     * @return
     */
    public String getNormalisationFactorAuxFile() {
        return _normalisationFactorFile;
    }

    /**
     * Sets the normalisation factor auxiliary file path
     * @param filePath
     */
    public void setNormalisationFactorAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _normalisationFactorFile = filePath;
    }

    /** Retrieves the auxiliary file for the input statistics
     * @return
     */
    public String getInputStatisticsAuxFile() {
        return _inputStatisticsFile;
    }

    /**
     * Sets the input statistics auxiliary file path
     * @param filePath
     */
    public void setInputStatisticsAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _inputStatisticsFile = filePath;
    }

    /** Retrieves the auxiliary file for the output statistics
     * @return
     */
    public String getOutputStatisticsAuxFile() {
        return _outputStatisticsFile;
    }

    /**
     * Sets the output statistics auxiliary file path
     * @param filePath
     */
    public void setOutputStatisticsAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _outputStatisticsFile = filePath;
    }

    /**
     * Retrieves the auxiliary file for the LAI neural net
     * @return
     */
    public String getNN_LaiAuxFile() {
        return _nn_LaiFile;
    }

    /**
     * Sets the auxiliary file for the LAI neural net
     * @param filePath
     */
    public void setNN_LaiAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _nn_LaiFile = filePath;
    }

    /**
     * Retrieves the auxiliary file for the fCover neural net
     * @return
     */
    public String getNN_fCoverAuxFile() {
        return _nn_fCoverFile;
    }

    /**
     * Sets the auxiliary file for the fCover neural net
     * @param filePath
     */
    public void setNN_fCoverAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _nn_fCoverFile = filePath;
    }

    /**
     * Retrieves the auxiliary file for the fAPAR neural net
     * @return
     */
    public String getNN_fAPARAuxFile() {
        return _nn_fAPARFile;
    }

    /**
     * Sets the auxiliary file for the fAPAR neural net
     * @param filePath
     */
    public void setNN_fAPARAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _nn_fAPARFile = filePath;
    }

    /**
     * Retrieves the auxiliary file for the LAIxCab neural net
     * @return
     */
    public String getNN_LAIxCabAuxFile() {
        return _nn_LAIxCabFile;
    }

    /**
     * Sets the auxiliary file for the LAIxCab neural net
     * @param filePath
     */
    public void setNN_LAIxCabAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _nn_LAIxCabFile = filePath;
    }

    /**
     * Retrieves the auxiliary file for the uncertainty file
     * @return
     */
    public String getUncertaintyAuxFile() {
        return _uncertaintyFile;
    }

    /**
     * Sets the auxiliary file for the uncertainty
     * @param filePath
     */
    public void setUncertaintyAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _uncertaintyFile = filePath;
    }
}
