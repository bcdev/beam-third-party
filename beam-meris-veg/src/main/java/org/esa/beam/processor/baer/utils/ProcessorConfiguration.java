/*
 * $Id: ProcessorConfiguration.java,v 1.2 2006/03/27 15:16:42 meris Exp $
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

import org.esa.beam.util.Guardian;

public class ProcessorConfiguration {

    private String _ndviAuxPath;
    private String _aerPhaseAuxPath;
    private String _relAerPhaseAuxPath;
    private String _groundReflectanceAuxPath;
    private String _soilFractionAuxPath;
    private String _fTuningAuxPath;
    private String _aerDiffTransmAuxPath;
    private String _hemisphReflecAuxPath;

    /**
     * Constructs the object with default parameters.
     */
    public ProcessorConfiguration() {
        _ndviAuxPath = "";
        _aerPhaseAuxPath = "";
        _relAerPhaseAuxPath = "";
        _groundReflectanceAuxPath = "";
        _soilFractionAuxPath = "";
        _fTuningAuxPath = "";
        _aerDiffTransmAuxPath = "";
        _hemisphReflecAuxPath = "";
    }

    /**
     * Retrieves the relative path to the ndvi auxiliary data file.
     * @return the path
     */
    public String getNdviAuxFile() {
        return _ndviAuxPath;
    }

    /**
     * Sets the relative path to the ndvi auxiliary data file.
     * @param filePath
     */
    public void setNdviAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _ndviAuxPath = filePath;
    }

    /**
     * Retrieves the relative path to the aerosol phase auxiliary data file.
     * @return the path
     */
    public String getAerosolPhaseAuxFile() {
        return _aerPhaseAuxPath;
    }

    /**
     * Sets the relative path to the aerosol auxiliary data file.
     * @param filePath
     */
    public void setAerosolPhaseAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _aerPhaseAuxPath = filePath;
    }

    /**
     * Retrieves the relative path to the relative aerosol phase auxiliary data file.
     * @return the path
     */
    public String getRelativeAerosolPhaseAuxFile() {
        return _relAerPhaseAuxPath;
    }

    /**
     * Sets the relative path to the relative aerosol auxiliary data file.
     * @param filePath
     */
    public void setRelativeAerosolPhaseAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _relAerPhaseAuxPath = filePath;
    }

    /**
     * Retrieves the relative path to the ground reflectance auxiliary data file.
     * @return the path
     */
    public String getGroundReflectanceAuxFile() {
        return _groundReflectanceAuxPath;
    }

    /**
     * Sets the relative path to the ground reflectance auxiliary data file.
     * @param filePath
     */
    public void setGroundReflectanceAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _groundReflectanceAuxPath = filePath;
    }

    /**
     * Retrieves the relative path to the soil fraction factor auxiliary data file.
     * @return the path
     */
    public String getSoilFractionAuxFile() {
        return _soilFractionAuxPath;
    }

    /**
     * Sets the relative path to the soil fraction factor auxiliary data file.
     * @param filePath
     */
    public void setSoilFractionAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _soilFractionAuxPath = filePath;
    }

    /**
     * Retrieves the relative path to the F_tuning factor auxiliary data file.
     * @return the path
     */
    public String getF_TuningAuxFile() {
        return _fTuningAuxPath;
    }

    /**
     * Sets the relative path to the F_tuning factor auxiliary data file.
     * @param filePath
     */
    public void setF_TuningAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _fTuningAuxPath = filePath;
    }

    /**
     * Retrieves the relative path to the aerosol diffuse transmission auxiliary data file.
     * @return the path
     */
    public String getAerDiffTransmAuxFile() {
        return _aerDiffTransmAuxPath;
    }

    /**
     * Sets the relative path to the aerosol diffuse transmission auxiliary data file.
     * @param filePath
     */
    public void setAerDiffTransmAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _aerDiffTransmAuxPath = filePath;
    }


    /**
     * Retrieves the relative path to the hemispherical reflectance auxiliary data file.
     * @return the path
     */
    public String getHemisphReflecAuxFile() {
        return _hemisphReflecAuxPath;
    }

    /**
     * Sets the relative path to the the hemispherical reflectance auxiliary data file.
     * @param filePath
     */
    public void setHemisphReflecAuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _hemisphReflecAuxPath = filePath;
    }
}
