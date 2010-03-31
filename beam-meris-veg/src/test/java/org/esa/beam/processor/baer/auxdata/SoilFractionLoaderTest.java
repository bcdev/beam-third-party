/*
 * $Id: SoilFractionLoaderTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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

public class SoilFractionLoaderTest extends TestCase {

    private static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/soil_fraction_aux.par";
    private static final String NO_SOIL_AUX_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/soil_fraction_aux_no_soil.par";

    private SoilFractionLoader _loader;

    public SoilFractionLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(SoilFractionLoaderTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _loader = new SoilFractionLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        // must be implementing the SoilFractionAccess interface
        assertTrue(_loader instanceof SoilFractionAccess);

        // shall not accept null files
        try {
            _loader.load(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        // shall be returning the default value when not having loaded an
        // aux file yet
        assertEquals(BaerConstants.AUX_SOIL_FRACTION_DEFAULT, _loader.getSoilFraction(), 1e-6);

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
        double expSoilFrac = 1.3;
        String expVersion = "0.7";
        String expComment = "Test Soil fraction factor";

        try {
            _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

        assertEquals(expSoilFrac, _loader.getSoilFraction(), 1e-6);
        assertEquals(expVersion, _loader.getVersionString());
        assertEquals(expComment, _loader.getDescription());
    }

    /**
     * Tests the reading of a correct aux file
     */
    public void testReadNoSoilFile() {
        double expSoilFrac = BaerConstants.AUX_SOIL_FRACTION_DEFAULT;
        String expVersion = "11.3";
        String expComment = "Test No Soil fraction factor";

        try {
            _loader.load(NO_SOIL_AUX_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

        // expecting to retriev the default value now
        assertEquals(expSoilFrac, _loader.getSoilFraction(), 1e-6);
        assertEquals(expVersion, _loader.getVersionString());
        assertEquals(expComment, _loader.getDescription());
    }
}
