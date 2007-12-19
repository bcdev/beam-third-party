/*
 * $Id: ProcessorConfigurationTests.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ProcessorConfigurationTests extends TestCase {

    private ProcessorConfiguration _config;

    public ProcessorConfigurationTests(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ProcessorConfigurationTests.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _config = new ProcessorConfiguration();
        assertNotNull(_config);
    }

    /**
     * Tests the correct functionality of the ndvi aux file accessor methods
     */
    public void testSetGetNdviPath() {
        // initially must be an empty string
        assertEquals("", _config.getNdviAuxFile());

        // null is not allowed to set
        try {
            _config.setNdviAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        // set some values and check
        String expOne = "c:\\Test\\ndvi_aux_file.xml";
        String expTwo = "SomeVeryLongsTUPIDaNDStupidNameForAConfigUrationFile";

        _config.setNdviAuxFile(expOne);
        assertEquals(expOne, _config.getNdviAuxFile());

        _config.setNdviAuxFile(expTwo);
        assertEquals(expTwo, _config.getNdviAuxFile());
    }

    /**
     * Tests the correct functionality of the ndvi aux file accessor methods
     */
    public void testSetGetAerosolPhasePath() {
        // initially must be an empty string
        assertEquals("", _config.getAerosolPhaseAuxFile());

        // null is not allowed to set
        try {
            _config.setAerosolPhaseAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        // set some values and check
        String expOne = "c:\\Test\\aersosol_phase_aux_file.xml";
        String expTwo = "SomeVeryLongsTUPIDaNDStupwahteverForAConfigUrationFile";

        _config.setAerosolPhaseAuxFile(expOne);
        assertEquals(expOne, _config.getAerosolPhaseAuxFile());

        _config.setAerosolPhaseAuxFile(expTwo);
        assertEquals(expTwo, _config.getAerosolPhaseAuxFile());
    }

    /**
     * Tests the correct functionality of the ndvi aux file accessor methods
     */
    public void testSetGetRelativeAerosolPhasePath() {
        // initially must be an empty string
        assertEquals("", _config.getRelativeAerosolPhaseAuxFile());

        // null is not allowed to set
        try {
            _config.setRelativeAerosolPhaseAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        // set some values and check
        String expOne = "c:\\Test\\rel_aersosol_phase_aux_file.xml";
        String expTwo = "SomeVeryLongsTUPIDaNDStuidWwahteverForAConfigUrationFile";

        _config.setRelativeAerosolPhaseAuxFile(expOne);
        assertEquals(expOne, _config.getRelativeAerosolPhaseAuxFile());

        _config.setRelativeAerosolPhaseAuxFile(expTwo);
        assertEquals(expTwo, _config.getRelativeAerosolPhaseAuxFile());
    }

    /**
     * Tests the correct functionality of the ndvi aux file accessor methods
     */
    public void testSetGetGroundReflectancePath() {
        // initially must be an empty string
        assertEquals("", _config.getGroundReflectanceAuxFile());

        // null is not allowed to set
        try {
            _config.setGroundReflectanceAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        // set some values and check
        String expOne = "c:\\Test\\aersosol_phase_aux_file.xml";
        String expTwo = "SomeVeryLongsTUPIDaNDStuidWwahteverForAConfigUrationFile";

        _config.setGroundReflectanceAuxFile(expOne);
        assertEquals(expOne, _config.getGroundReflectanceAuxFile());

        _config.setGroundReflectanceAuxFile(expTwo);
        assertEquals(expTwo, _config.getGroundReflectanceAuxFile());
    }

    /**
     * Tests the correct functionality of the soil fraction factor aux file accessor methods
     */
    public void testSetGetSoilFractionPath() {
        // initially must be an empty string
        assertEquals("", _config.getSoilFractionAuxFile());

        // null is not allowed to set
        try {
            _config.setSoilFractionAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        // set some values and check
        String expOne = "c:\\Test\\soil_fraction_file.xml";
        String expTwo = "anotherlongblablablafilenamjusttotestthisfunctionality";

        _config.setSoilFractionAuxFile(expOne);
        assertEquals(expOne, _config.getSoilFractionAuxFile());

        _config.setSoilFractionAuxFile(expTwo);
        assertEquals(expTwo, _config.getSoilFractionAuxFile());
    }

    /**
     * Tests the correct functionality of the F tuning factor aux file accessor methods
     */
    public void testSetGetF_TuningPath() {
        // initially must be an empty string
        assertEquals("", _config.getF_TuningAuxFile());

        // null is not allowed to set
        try {
            _config.setF_TuningAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        // set some values and check
        String expOne = "c:\\Test\\f_tuning_file.par";
        String expTwo = "anotherlongblablablafilenamjusttotestthisfunctionality";

        _config.setF_TuningAuxFile(expOne);
        assertEquals(expOne, _config.getF_TuningAuxFile());

        _config.setF_TuningAuxFile(expTwo);
        assertEquals(expTwo, _config.getF_TuningAuxFile());
    }

    /**
     * Tests the correct functionality of the F tuning factor aux file accessor methods
     */
    public void testSetGetAerDiffTransmPath() {
        // initially must be an empty string
        assertEquals("", _config.getAerDiffTransmAuxFile());

        // null is not allowed to set
        try {
            _config.setAerDiffTransmAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        // set some values and check
        String expOne = "c:\\Test\\aerosol_diffuse_transmission.par";
        String expTwo = "aerosol_diffuse_transmission_longer_parameter_name";

        _config.setAerDiffTransmAuxFile(expOne);
        assertEquals(expOne, _config.getAerDiffTransmAuxFile());

        _config.setAerDiffTransmAuxFile(expTwo);
        assertEquals(expTwo, _config.getAerDiffTransmAuxFile());
    }


    /**
     * Tests the correct functionality of the F tuning factor aux file accessor methods
     */
    public void testSetGetHemisphReflecPath() {
        // initially must be an empty string
        assertEquals("", _config.getHemisphReflecAuxFile());

        // null is not allowed to set
        try {
            _config.setHemisphReflecAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        // set some values and check
        String expOne = "c:\\Test\\hemispherical_reflectance.par";
        String expTwo = "hemispherical_reflectance to be loaded";

        _config.setHemisphReflecAuxFile(expOne);
        assertEquals(expOne, _config.getHemisphReflecAuxFile());

        _config.setHemisphReflecAuxFile(expTwo);
        assertEquals(expTwo, _config.getHemisphReflecAuxFile());
    }
}
