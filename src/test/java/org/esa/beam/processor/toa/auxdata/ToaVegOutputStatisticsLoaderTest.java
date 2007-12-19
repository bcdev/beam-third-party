/*
 * $Id: ToaVegOutputStatisticsLoaderTest.java,v 1.7 2006/03/27 15:32:35 meris Exp $
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
package org.esa.beam.processor.toa.auxdata;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.processor.MerisVegTestConfig;
import org.esa.beam.processor.common.auxdata.VegOutputStatisticsAccess;
import org.esa.beam.processor.toa.ToaVegConstants;

import java.io.IOException;

public class ToaVegOutputStatisticsLoaderTest extends TestCase {

    private static final String INVALID_FILE = "q:/what_drive/fjkhgskfgh/away_witjhkfdge.none";
    private static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "toa/testData/output_statistics.par";
    private static final String MISSING_FILE = MerisVegTestConfig.testFileBaseDirPath + "toa/testData/output_statistics_miss.par";


    private ToaVegOutputStatisticsLoader _loader;

    public ToaVegOutputStatisticsLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ToaVegOutputStatisticsLoaderTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _loader = new ToaVegOutputStatisticsLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        assertTrue(_loader instanceof VegOutputStatisticsAccess);

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
           // _loader.load(_beamPath+CORRECT_FILE);
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
        assertEquals("3.3", _loader.getVersionString(ToaVegConstants.AUX_VERSION_KEY));
        assertEquals("unit level test file", _loader.getDescription(ToaVegConstants.AUX_DESCRIPTION_KEY));

        double dVal = 1.0;
        double[] ret = new double[4];

        ret = _loader.getFAPARConstants(ret);
        assertEquals(dVal, ret[0], 1e-6);
        ++dVal;
        assertEquals(dVal, ret[1], 1e-6);


        ret = _loader.getFCoverConstants(ret);
        ++dVal;
        assertEquals(dVal, ret[0], 1e-6);
        ++dVal;
        assertEquals(dVal, ret[1], 1e-6);

        ret = _loader.getLAIConstants(ret);
        ++dVal;
        assertEquals(dVal, ret[0], 1e-6);
        ++dVal;
        assertEquals(dVal, ret[1], 1e-6);

        ret = _loader.getLAIxCabConstants(ret);
        ++dVal;
        assertEquals(dVal, ret[0], 1e-6);
        ++dVal;
        assertEquals(dVal, ret[1], 1e-6);
    }

    /**
     * Tests that the test file is loaded and parsed correctly and missing values
     * are replaced with their defaults
     */
    public void testLoadMissingParamsFile() {
        try {
            _loader.load(MISSING_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO exception expected");
        } catch (IOException e) {
            fail("NO exception expected");
        }

        // check version and description
        assertEquals("6.5", _loader.getVersionString(ToaVegConstants.AUX_VERSION_KEY));
        assertEquals("unit level test file missing parameters", _loader.getDescription(ToaVegConstants.AUX_DESCRIPTION_KEY));

         double dVal = 1.0;
        double[] ret = new double[4];

        ret = _loader.getFAPARConstants(ret);
        assertEquals(ToaVegConstants.FAPAR_MIN_DEFAULT, ret[0], 1e-6);
        ++dVal;
        assertEquals(dVal, ret[1], 1e-6);

        ret = _loader.getFCoverConstants(ret);
        ++dVal;
        assertEquals(ToaVegConstants.FCOVER_MIN_DEFAULT, ret[0], 1e-6);
        ++dVal;
        assertEquals(dVal, ret[1], 1e-6);

        ret = _loader.getLAIConstants(ret);
        ++dVal;
        assertEquals(ToaVegConstants.LAI_MIN_DEFAULT, ret[0], 1e-6);
        ++dVal;
        assertEquals(dVal, ret[1], 1e-6);

        ret = _loader.getLAIxCabConstants(ret);
        ++dVal;
       assertEquals(ToaVegConstants.LAIXCAB_MIN_DEFAULT, ret[0], 1e-6);
        ++dVal;
        assertEquals(dVal, ret[1], 1e-6);
    }
}
