/*
 * $Id: F_TuningLoaderTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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

public class F_TuningLoaderTest extends TestCase {

    private static final String CORRECT_AUX_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/f_tuning_aux.par";
    private static final String NO_FTUNE_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/f_tuning_no_f_tuning.par";

    private F_TuningLoader _loader;

    public F_TuningLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(F_TuningLoaderTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _loader = new F_TuningLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        // must implement the F_TuningAccess interface
        assertTrue(_loader instanceof F_TuningAccess);

        // shall not accept null files
        try {
            _loader.load(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }
        // shall be returning the default value when not having loaded an
        // aux file yet
        assertEquals(BaerConstants.AUX_F_TUNING_DEFAULT, _loader.getF_TuningFactor(), 1e-6);

        // shall NOT fail when reading a correct file
        try {
            _loader.load(CORRECT_AUX_FILE);
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
        double expSoilFrac = 1.5;
        String expVersion = "1.5";
        String expComment = "F tuning factor for surface reflectance calculation";

        try {
             _loader.load(CORRECT_AUX_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

        assertEquals(expSoilFrac, _loader.getF_TuningFactor(), 1e-6);
        assertEquals(expVersion, _loader.getVersionString());
        assertEquals(expComment, _loader.getDescription());
    }

    /**
     * Tests the reading of a correct aux file
     */
    public void testReadNoF_TuningFile() {
        String expVersion = "1.98";
        String expComment = "test file with missing parameter";

         try {
             _loader.load(NO_FTUNE_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

        assertEquals(BaerConstants.AUX_F_TUNING_DEFAULT, _loader.getF_TuningFactor(), 1e-6);
        assertEquals(expVersion, _loader.getVersionString());
        assertEquals(expComment, _loader.getDescription());
    }
}
