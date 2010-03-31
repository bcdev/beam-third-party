/*
 * $Id: HemisphReflecLoaderTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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
package org.esa.beam.processor.baer.auxdata;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.processor.MerisVegTestConfig;
import org.esa.beam.processor.baer.BaerConstants;

import java.io.IOException;

public class HemisphReflecLoaderTest extends TestCase {

    private static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/hemisph_reflec.par";
    private static final String ERR_1_AUX = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/hemisph_reflec_err_1.par";
    private static final String ERR_2_AUX = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/hemisph_reflec_err_2.par";

    private HemisphReflecLoader _loader;

    public HemisphReflecLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(HemisphReflecLoaderTest.class);
    }

    protected void setUp() {
        _loader = new HemisphReflecLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        // must be implementing the AerDiffTransmAccess interface
        assertTrue(_loader instanceof HemisphReflecAccess);

        // shall not accept null files
        try {
            _loader.load(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        // shall return a vector of zeroes when aux data is not loaded yet
        double[] ret;
        ret = _loader.getHemisphReflecCoefficients();
        assertNotNull(ret);
        assertEquals(BaerConstants.AUX_NUM_HEMISPH_REFLEC_COEFFS, ret.length);
        for (int n = 0; n < BaerConstants.AUX_NUM_HEMISPH_REFLEC_COEFFS; n++) {
            assertEquals(0.0, ret[n], 1e-6);
        }

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
        String expVersion = "6.4";
        String expDescription = "another set of test coefficients";
        double[] dRet;

        try {
            _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

        assertEquals(expVersion, _loader.getVersionString());
        assertEquals(expDescription, _loader.getDescription());

        dRet = _loader.getHemisphReflecCoefficients();
        assertNotNull(dRet);
        assertEquals(BaerConstants.AUX_NUM_HEMISPH_REFLEC_COEFFS, dRet.length);

        for (int n = 0; n < BaerConstants.AUX_NUM_HEMISPH_REFLEC_COEFFS; n++) {
            assertEquals((double) n, dRet[n], 1e-6);
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
