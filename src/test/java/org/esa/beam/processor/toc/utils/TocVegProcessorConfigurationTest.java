/*
 * $Id: TocVegProcessorConfigurationTest.java,v 1.4 2006/03/27 15:33:02 meris Exp $
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
package org.esa.beam.processor.toc.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TocVegProcessorConfigurationTest extends TestCase {

    private TocVegProcessorConfiguration _config;

    public TocVegProcessorConfigurationTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TocVegProcessorConfigurationTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _config = new TocVegProcessorConfiguration();
        assertNotNull(_config);
    }

    /**
     * Tests the correctness of the normalisation factor accessors
     */
    public void testSetGetNormalisationFactor() {
        String auxPath_1 = "c:\\wurstmann\\test_file.par";
        String auxPath_2 = "/usr/local/home/strange/dir/file.pop";

        // initially must be an empty string
        assertEquals("", _config.getNormalisationFactorAuxFile());

        // null is not allowed to set
        try {
            _config.setNormalisationFactorAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        _config.setNormalisationFactorAuxFile(auxPath_1);
        assertEquals(auxPath_1, _config.getNormalisationFactorAuxFile());

        _config.setNormalisationFactorAuxFile(auxPath_2);
        assertEquals(auxPath_2, _config.getNormalisationFactorAuxFile());
    }

    /**
     * Tests the correctness of the input statistics aux file
     * accessors
     */
    public void testSetGetInputStatistics() {
        String auxPath_1 = "c:\\isnothere\\test_file.par";
        String auxPath_2 = "/usr/local/home/strange/dir/file.pop";

        // initially must be an empty string
        assertEquals("", _config.getInputStatisticsAuxFile());

        // null is not allowed to set
        try {
            _config.setInputStatisticsAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        _config.setInputStatisticsAuxFile(auxPath_1);
        assertEquals(auxPath_1, _config.getInputStatisticsAuxFile());

        _config.setInputStatisticsAuxFile(auxPath_2);
        assertEquals(auxPath_2, _config.getInputStatisticsAuxFile());
    }

    /**
     * Tests the correctness of the output statistics aux file
     * accessors
     */
    public void testSetGetOutputStatistics() {
        String auxPath_1 = "c:\\isnothere\\test_file.par";
        String auxPath_2 = "/usr/local/home/strange/dir/file.pop";

        // initially must be an empty string
        assertEquals("", _config.getOutputStatisticsAuxFile());

        // null is not allowed to set
        try {
            _config.setOutputStatisticsAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        _config.setOutputStatisticsAuxFile(auxPath_1);
        assertEquals(auxPath_1, _config.getOutputStatisticsAuxFile());

        _config.setOutputStatisticsAuxFile(auxPath_2);
        assertEquals(auxPath_2, _config.getOutputStatisticsAuxFile());
    }

    /**
     * Tests the correctness of the nn accessor methods
     */
    public void testSetGetNN() {
        String auxPath_1 = "c:\\isnothereagain\\test_file.nna";
        String auxPath_2 = "/usr/local/popocal/strange/dir/file.nnb";

        // initially must be an empty string
        assertEquals("", _config.getNN_AuxFile());

        // null is not allowed to set
        try {
            _config.setNN_AuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        _config.setNN_AuxFile(auxPath_1);
        assertEquals(auxPath_1, _config.getNN_AuxFile());

        _config.setNN_AuxFile(auxPath_2);
        assertEquals(auxPath_2, _config.getNN_AuxFile());
    }
}
