/*
 * $Id: GroundReflectanceLoaderTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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

public class GroundReflectanceLoaderTest extends TestCase {

    private static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/gnd_refl_correct.xml";
    private GroundReflectanceLoader _loader;

    public GroundReflectanceLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(GroundReflectanceLoaderTest.class);
    }

    /**
     * Initializes the test environment.
     */
    protected void setUp() {
        _loader = new GroundReflectanceLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        // must be implementing the NdviAccess interface
        assertTrue(_loader instanceof GroundReflectanceAccess);

        // shall not accept null files
        try {
            _loader.load(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
            fail("IllegalArgumentException expected");
        }
    }

    /**
     * Tests that a correct file is loaded without errors
     */
    public void testLoadCorrectFile() {
        String expVersion = "1.0";
        String expDescription = "First test data";
        String expShortName = "LACE+MAPLE";
        String expSpecDescription = "2 x LACE + MAPLE modified";
        String expGround = "VEG";
        double[] expSpectrum = {0.0020, // 1
                                0.0055, // 2
                                0.0144, // 3
                                0.0230, // 4
                                0.0475, // 5
                                0.0700, // 6
                                0.323, // 7;
                                0.0, // 8
                                0.0, // 9
                                0.0, // 10
                                0.0, // 11
                                0.0, // 12
                                0.378, // 13
                                0.0, // 14
                                0.0     // 15
        };

        try {
            _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

        assertEquals(expVersion, _loader.getVersionString());
        assertEquals(expDescription, _loader.getDescription());

        // we must retrieve a correct spectrum - and only one!
        Spectrum spec;

        assertEquals(1, _loader.getNumSpectra());

        try {
            spec = _loader.getSpectrum(expShortName);

            assertEquals(expShortName, spec.getShortName());
            assertEquals(expSpecDescription, spec.getDescription());
            assertEquals(expGround, spec.getGroundType());

            for (int n = 0; n < expSpectrum.length; n++) {
                assertEquals(expSpectrum[n], spec.getValueAt(n), 1e-6);
            }
        } catch (ProcessorException e) {
            fail("NO Exception expected");
        }
    }

}
