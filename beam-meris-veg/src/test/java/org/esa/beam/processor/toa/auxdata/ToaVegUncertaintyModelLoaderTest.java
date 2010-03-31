/*
 * $Id: ToaVegUncertaintyModelLoaderTest.java,v 1.7 2006/03/23 18:01:25 meris Exp $
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
package org.esa.beam.processor.toa.auxdata;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.processor.MerisVegTestConfig;
import org.esa.beam.processor.common.auxdata.VegUncertaintyModelAccess;
import org.esa.beam.processor.toa.ToaVegConstants;

import java.io.IOException;

public class ToaVegUncertaintyModelLoaderTest extends  TestCase {


    private static final String INVALID_FILE = "q:/what_drive/fjkhgskfgh/away_witjhkfdge.none";
       private static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "toa/testData/uncertainty.par";
       private static final String MISSING_FILE = MerisVegTestConfig.testFileBaseDirPath + "toa/testData/uncertainty_miss.par";


       private ToaVegUncertaintyModelLoader _loader;

       public ToaVegUncertaintyModelLoaderTest(String name) {
           super(name);
       }

       public static Test suite() {
           return new TestSuite(ToaVegUncertaintyModelLoaderTest.class);
       }

       /**
        * Initializes the test environment
        */
       protected void setUp() {
           _loader = new ToaVegUncertaintyModelLoader();
           assertNotNull(_loader);
       }


     /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        assertTrue(_loader instanceof VegUncertaintyModelAccess);

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
    public void testLoadCorrectFile() throws IOException {
            _loader.load(CORRECT_FILE);

        // check version and description
        assertEquals("1.0", _loader.getVersionString(ToaVegConstants.AUX_VERSION_KEY));
        assertEquals("first test implementation", _loader.getDescription(ToaVegConstants.AUX_DESCRIPTION_KEY));

        double dVal = 0.0;
        double[] ret = new double[4];

        ret = _loader.getfAPARCoefficients(ret);
        assertEquals(dVal, ret[0],1e-6);
        ++dVal;
        assertEquals(dVal, ret[1],1e-6);
        ++dVal;
        assertEquals(dVal, ret[2],1e-6);
        ++dVal;

        ret = _loader.getfCoverCoefficients(ret);
        assertEquals(dVal, ret[0],1e-6);
        ++dVal;
        assertEquals(dVal, ret[1],1e-6);
        ++dVal;
        assertEquals(dVal, ret[2],1e-6);
        ++dVal;

        ret = _loader.getLAICoefficients(ret);
        assertEquals(dVal, ret[0],1e-6);
        ++dVal;
        assertEquals(dVal, ret[1],1e-6);
        ++dVal;
        assertEquals(dVal, ret[2],1e-6);
        ++dVal;

        ret = _loader.getLAIxCabCoefficients(ret);
        assertEquals(dVal, ret[0],1e-6);
        ++dVal;
        assertEquals(dVal, ret[1],1e-6);
        ++dVal;
        assertEquals(dVal, ret[2],1e-6);

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
        assertEquals("1.0", _loader.getVersionString(ToaVegConstants.AUX_VERSION_KEY));
        assertEquals("first test implementation", _loader.getDescription(ToaVegConstants.AUX_DESCRIPTION_KEY));

        double dVal = 0.0;
        double[] ret = new double[3];

        ret = _loader.getfAPARCoefficients(ret);
        assertEquals(dVal, ret[0],1e-6);
        ++dVal;
        assertEquals(dVal, ret[1],1e-6);
        ++dVal;
        assertEquals(ToaVegConstants.FAPAR_UNC2_DEFAULT, ret[2],1e-6);
        ++dVal;

        ret = _loader.getfCoverCoefficients(ret);
        assertEquals(dVal, ret[0],1e-6);
        ++dVal;
        assertEquals(dVal, ret[1],1e-6);
        ++dVal;
        assertEquals(ToaVegConstants.FCOVER_UNC2_DEFAULT, ret[2],1e-6);
        ++dVal;

        ret = _loader.getLAICoefficients(ret);
        assertEquals(dVal, ret[0],1e-6);
        ++dVal;
        assertEquals(ToaVegConstants.LAI_UNC1_DEFAULT, ret[1],1e-6);
        ++dVal;
        assertEquals(dVal, ret[2],1e-6);
        ++dVal;

        ret = _loader.getLAIxCabCoefficients(ret);
        assertEquals(dVal, ret[0],1e-6);
        ++dVal;
        assertEquals(dVal, ret[1],1e-6);
        ++dVal;
        assertEquals(ToaVegConstants.LAIXCAB_UNC2_DEFAULT, ret[2],1e-6);
    }
}






