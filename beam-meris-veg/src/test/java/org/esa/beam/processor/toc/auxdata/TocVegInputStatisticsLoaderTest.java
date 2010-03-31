/*
 * $Id: TocVegInputStatisticsLoaderTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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
package org.esa.beam.processor.toc.auxdata;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.processor.MerisVegTestConfig;
import org.esa.beam.processor.common.auxdata.VegInputStatisticsAccess;
import org.esa.beam.processor.toc.TocVegConstants;

import java.io.IOException;

public class TocVegInputStatisticsLoaderTest extends TestCase {

    private static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "toc/testData/input_statistics.par";
    private static final String MISSING_FILE = MerisVegTestConfig.testFileBaseDirPath + "toc/testData/input_statistics_miss.par";
    private static final String INVALID_FILE = "R:/R_drive/hhgsdoia/away_with_me.none";

    private TocVegInputStatisticsLoader _loader;

    public TocVegInputStatisticsLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TocVegInputStatisticsLoaderTest.class);
    }

    /**
     * Initializes the test environment.
     */
    protected void setUp() {
        _loader = new TocVegInputStatisticsLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        // must be implementing the NormFactorAccess interface
        assertTrue(_loader instanceof VegInputStatisticsAccess);

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
        assertEquals("2.6", _loader.getVersionString(TocVegConstants.AUX_VERSION_KEY));
        assertEquals("Test unit-level stuff", _loader.getDescription(TocVegConstants.AUX_DESCRIPTION_KEY));

        int val = 12;

        assertEquals((double) val, _loader.getTheta_S_Mean(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getTheta_S_StdDev(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getTheta_V_Mean(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getTheta_V_StdDev(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getCos_Phi_Mean(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getCos_Phi_StdDev(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getR_Mean(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getR_StdDev(), 1e-6);
    }

    /**
     * Tests that the test file is loaded and parsed correctly
     */
    public void testLoadMissingParametersFile() {
        try {
            _loader.load(MISSING_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO exception expected");
        } catch (IOException e) {
            fail("NO exception expected");
        }

        // check version and description
        assertEquals("4.6", _loader.getVersionString(TocVegConstants.AUX_VERSION_KEY));
        assertEquals("missing values", _loader.getDescription(TocVegConstants.AUX_DESCRIPTION_KEY));

        int val = 12;

        assertEquals((double) val, _loader.getTheta_S_Mean(), 1e-6);
        ++val;
        assertEquals(TocVegConstants.THETA_S_STD_DEFAULT, _loader.getTheta_S_StdDev(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getTheta_V_Mean(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getTheta_V_StdDev(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getCos_Phi_Mean(), 1e-6);
        ++val;
        assertEquals(TocVegConstants.COS_PHI_STD_DEFAULT, _loader.getCos_Phi_StdDev(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getR_Mean(), 1e-6);
        ++val;
        assertEquals((double) val, _loader.getR_StdDev(), 1e-6);
    }
}