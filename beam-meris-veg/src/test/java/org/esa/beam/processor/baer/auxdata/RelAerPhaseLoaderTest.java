/*
 * $Id: RelAerPhaseLoaderTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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

import java.io.IOException;

public class RelAerPhaseLoaderTest extends TestCase {

    public static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/rel_aer_phase.par";
    public static final String ERR_1_AUX = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/rel_aer_phase_err_1.par";
    public static final String ERR_2_AUX = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/rel_aer_phase_err_2.par";

    private RelAerPhaseLoader _loader;

    public RelAerPhaseLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(RelAerPhaseLoaderTest.class);
    }

    /**
     * Initializes the test environment
     */
    public void setUp() {
        _loader = new RelAerPhaseLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        // must be implementing the RelAerPhaseAccess interface
        assertTrue(_loader instanceof RelAerPhaseAccess);

        // shall not accept null files
        try {
            _loader.load(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        // shall correctly load the test file
        try {
            _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected loading ");
        } catch (IOException e) {
        	fail("NO Exception expected loading ");
        }
    }

    /**
     * Loads a correct file - test implementation
     */
    public void testLoadCorrectFile() {
        String expVersion = "1.6";
        String expDescription = "Polynominal coeffs. for the rel. aerosol phase function (LACE-98)";
        double[] expValues = {0.107555, 0.001342602, 0.0000054726475};

        // shall correctly load the test file
        try {
            _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

        assertEquals(expVersion, _loader.getVersionString());
        assertEquals(expDescription, _loader.getDescription());
        double[] result = null;
        result = _loader.getRelativeAerosolPhaseCoefficients();
        assertNotNull(result);
        assertEquals(3, result.length);
        for (int n = 0; n < result.length; n++) {
            assertEquals(expValues[n], result[n], 1e-6);
        }
    }

    /**
     * Tests that erroneous files are rejected.
     */
    public void testErroneousFiles() {
        // shall throw exception
        try {
            _loader.load(ERR_1_AUX);
            fail("IOException expected");
        } catch (IllegalArgumentException e) {
            fail("IOException expected");
        } catch (IOException e) {
        }

        // shall throw exception
        try {
            _loader.load(ERR_2_AUX);
            fail("IOException expected");
        } catch (IllegalArgumentException e) {
            fail("IOException expected");
        } catch (IOException e) {
        }
    }
}
