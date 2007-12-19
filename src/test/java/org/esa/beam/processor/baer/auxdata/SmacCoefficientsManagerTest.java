/*
 * $Id: SmacCoefficientsManagerTest.java,v 1.3 2006/03/24 08:09:15 meris Exp $
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

package org.esa.beam.processor.baer.auxdata;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.processor.MerisVegTestConfig;

import java.io.File;
import java.io.IOException;

public class SmacCoefficientsManagerTest extends TestCase {

    public static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/smac_coefficients.dat";
    public static final String NO_SMAC_COEFF_AUX_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/aux_no_smac_coefficients.dat";
    public static final String SMAC_COEFFICIENTS_LOCATION = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/smac";

    public static final String SENSOR = "MERIS";
    public static final String BAND = "radiance_1";
    public static final String AEROSOL_TYPE = "DES";

    private SmacCoefficientsManager _manager;

    public SmacCoefficientsManagerTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(SmacCoefficientsManagerTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() throws IOException {
        _manager = new SmacCoefficientsManager(new File(SMAC_COEFFICIENTS_LOCATION));
        assertNotNull(_manager);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {

        // shall not accept null files
        try {
            _manager.getCoefficientFile(null, null, null);
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
        }

        // shall be returning the default value when not having loaded an
        // aux file yet

        // shall NOT fail when reading a correct file
        try {
            _manager.getCoefficientFile(SENSOR, BAND, AEROSOL_TYPE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (Exception e) {
            fail("NO Exception expected");
        }
    }
}

