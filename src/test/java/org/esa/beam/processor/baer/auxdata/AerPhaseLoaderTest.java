/*
 * $Id: AerPhaseLoaderTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.processor.MerisVegTestConfig;

import java.io.IOException;

public class AerPhaseLoaderTest extends TestCase {

    private static final String INVALID_FILE = "G:/no_drive/ghurtfsrd/non_existing_file.none";
    private static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/aer_phase_function.xml";
    private static final String OPEN_LIST_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/aer_phase_open_list.xml";
    private static final String LUT_NO_NAME_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/aer_phase_LUT_no_name.xml";
    private static final String LUT_NO_BANDS_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/aer_phase_LUT_no_bands.xml";
    private static final String OPEN_LUT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/aer_phase_open_lut.xml";
    private static final String OPEN_BAND_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/aer_phase_open_band.xml";
    private static final String MISSING_PARAM_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/aer_phase_missing_param.xml";

    public static final String[] EXP_LUT_NAMES = new String[]{"LACE-98_MER", "LACE-98_VE", "DESERT"};
    public static final int EXP_NUM_BANDS = 15;

    private AerPhaseLoader _loader;

    public AerPhaseLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(AerPhaseLoaderTest.class);
    }

    /**
     * Initializes the test environment.
     */
    protected void setUp() {
        _loader = new AerPhaseLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        // must be implementing the AerPhaseAccess interface
        assertTrue(_loader instanceof AerPhaseAccess);

        // shall not accept null files
        try {
            _loader.load(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        // shall not accept not existing files
        try {
            _loader.load(INVALID_FILE);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        // but shall load the correct file without failures
        try {
               _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO exception expected");
        } catch (IOException e) {
            fail("NO exception expected");
        }
    }

    /**
     * Tests that the test file is loaded and parsed correctly
     */
    public void testLoadCorrectFile() {
        try {
               _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO exception expected");
        } catch (IOException e) {
            fail("NO exception expected");
        }

        // check version and description
        assertEquals("1.0", _loader.getVersionString());
        assertEquals("The first sketch of the format - data from Veronique", _loader.getDescription());

        // expect to contain three LUT's
        String[] lutNames = null;

        lutNames = _loader.getLUTNames();
        assertNotNull(lutNames);
        assertEquals(EXP_LUT_NAMES.length, lutNames.length);
        for (int n = 0; n < EXP_LUT_NAMES.length; n++) {
            assertEquals(EXP_LUT_NAMES[n], lutNames[n]);
        }

        try {
            // check LUTS for correctness
            // --------------------------
            AerLut lut = null;
            AerBandParam param = null;
            double[] values = null;

            // the first one
            lut = _loader.getLUT(EXP_LUT_NAMES[0]);
            assertNotNull(lut);
            assertEquals(EXP_LUT_NAMES[0], lut.getName());
            assertEquals(EXP_NUM_BANDS, lut.getNumBands());
            // pick some random bands and check
            param = lut.getBand(3);
            assertNotNull(param);
            values = param.getA();
            assertNotNull(values);
            assertEquals(true, param.isValid());
            assertEquals(3.03926, values[0], 1e-6);
            assertEquals(7.45182, values[1], 1e-6);
            assertEquals(0.0, values[2], 1e-6);

            param = lut.getBand(9);
            assertNotNull(param);
            values = param.getA();
            assertNotNull(values);
            assertEquals(false, param.isValid());
            assertEquals(-1.0, values[0], 1e-6);
            assertEquals(-1.0, values[1], 1e-6);
            assertEquals(-1.0, values[2], 1e-6);

            // the second one
            lut = _loader.getLUT(EXP_LUT_NAMES[1]);
            assertNotNull(lut);
            assertEquals(EXP_LUT_NAMES[1], lut.getName());
            assertEquals(EXP_NUM_BANDS, lut.getNumBands());
            param = lut.getBand(6);
            assertNotNull(param);
            values = param.getA();
            assertNotNull(values);
            assertEquals(true, param.isValid());
            assertEquals(-3.83648, values[0], 1e-6);
            assertEquals(7.92626, values[1], 1e-6);
            assertEquals(0.0, values[2], 1e-6);

            param = lut.getBand(4);
            assertNotNull(param);
            values = param.getA();
            assertNotNull(values);
            assertEquals(true, param.isValid());
            assertEquals(0.03718, values[0], 1e-6);
            assertEquals(7.14438, values[1], 1e-6);
            assertEquals(0.0, values[2], 1e-6);

            // the third one
            lut = _loader.getLUT(EXP_LUT_NAMES[2]);
            assertNotNull(lut);
            assertEquals(EXP_LUT_NAMES[2], lut.getName());
            assertEquals(EXP_NUM_BANDS, lut.getNumBands());

            param = lut.getBand(2);
            assertNotNull(param);
            values = param.getA();
            assertNotNull(values);
            assertEquals(true, param.isValid());
            assertEquals(-33.1053, values[0], 1e-6);
            assertEquals(22.7264, values[1], 1e-6);
            assertEquals(0.0, values[2], 1e-6);

            param = lut.getBand(11);
            assertNotNull(param);
            values = param.getA();
            assertNotNull(values);
            assertEquals(false, param.isValid());
            assertEquals(-1.0, values[0], 1e-6);
            assertEquals(-1.0, values[1], 1e-6);
            assertEquals(-1.0, values[2], 1e-6);
        } catch (ProcessorException e) {
            fail("NO exception expected");
        }
    }

    /**
     * Tests the behaviour on malformatted files
     */
    public void testLoadErroneousFiles() {
        try {
            _loader.load(OPEN_LIST_FILE);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        try {
            _loader.load(LUT_NO_BANDS_FILE);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        try {
            _loader.load(LUT_NO_NAME_FILE);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        try {
            _loader.load(OPEN_LUT_FILE);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        try {
            _loader.load(OPEN_BAND_FILE);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        try {
            _loader.load(MISSING_PARAM_FILE);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }
    }

    /**
     * Checks the LUT selection functionality
     */
    public void testSelectLuts() {
        AerBandParam param;
        boolean bRet;

        try {
            _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO exception expected");
        } catch (IOException e) {
            fail("NO exception expected");
        }

        // nothing selected - must be null
        param = _loader.getAerPhase(2);
        assertEquals(null, param);

        // select shit - must fail
        bRet = _loader.selectLut("invalidname");
        assertEquals(false, bRet);
        // must still be null
        param = _loader.getAerPhase(7);
        assertEquals(null, param);

        bRet = _loader.selectLut("LACE-98_VE");
        assertEquals(true, bRet);
        param = _loader.getAerPhase(3);
        assertNotNull(param);
    }
}
