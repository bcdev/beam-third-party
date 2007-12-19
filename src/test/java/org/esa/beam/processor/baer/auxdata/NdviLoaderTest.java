/*
 * $Id: NdviLoaderTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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
import org.esa.beam.processor.baer.BaerConstants;

import java.io.IOException;

public class NdviLoaderTest extends TestCase {

    public static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/ndvi_aux.par";
    public static final String NO_NDVI_AUX_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/ndvi_aux_no_ndvi.par";

    private NdviLoader _loader;

    public NdviLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(NdviLoaderTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _loader = new NdviLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        // must be implementing the NdviAccess interface
        assertTrue(_loader instanceof NdviAccess);

        // shall not accept null files
        try {
            _loader.load(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        // shall be returning the default value when not having loaded an
        // aux file yet
        assertEquals(BaerConstants.AUX_NDVI_DEFAULT, _loader.getNdviTuningFactor(), 1e-6);

        // shall NOT fail when reading a correct file
        try {
            _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }
    }

    /**
     * Tests the reading of a correct aux file
     */
    public void testReadCorrectFile() {
        double expNdvi = 0.45;
        String expVersion = "1.4";
        String expComment = "Ndvi tuning factor for veg. Fraction cover";

        try {
            _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

        assertEquals(expNdvi, _loader.getNdviTuningFactor(), 1e-6);
        assertEquals(expVersion, _loader.getVersionString());
        assertEquals(expComment, _loader.getDescription());
    }

    /**
     * Tests the correct behavior on a malformed file
     */
    public void testReadNoNdviFile() {
        double expNdvi = 0.6;
        String expVersion = "0.6";
        String expComment = "this is a test";

        try {
            _loader.load(NO_NDVI_AUX_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

        // expect to get the default value then
        assertEquals(expNdvi, _loader.getNdviTuningFactor(), 1e-6);
        assertEquals(expVersion, _loader.getVersionString());
        assertEquals(expComment, _loader.getDescription());
    }
}
