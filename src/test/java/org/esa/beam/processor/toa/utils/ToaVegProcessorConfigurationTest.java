/*
 * $Id: ToaVegProcessorConfigurationTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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
package org.esa.beam.processor.toa.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.processor.common.utils.VegProcessorConfiguration;

public class ToaVegProcessorConfigurationTest extends TestCase {

    private VegProcessorConfiguration _config;

    public ToaVegProcessorConfigurationTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ToaVegProcessorConfigurationTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _config = new VegProcessorConfiguration();
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
     * Tests the correctness of the nn_Lai accessor methods
     */
    public void testSetGetNNLai() {
        String auxPath_1 = "c:\\isnothereagain\\test_file.nna";
        String auxPath_2 = "/usr/local/popocal/strange/dir/file.nnb";

        // initially must be an empty string
        assertEquals("", _config.getNN_LaiAuxFile());

        // null is not allowed to set
        try {
            _config.setNN_LaiAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        _config.setNN_LaiAuxFile(auxPath_1);
        assertEquals(auxPath_1, _config.getNN_LaiAuxFile());

        _config.setNN_LaiAuxFile(auxPath_2);
        assertEquals(auxPath_2, _config.getNN_LaiAuxFile());
    }

    /**
     * Tests the correctness of the nn_fCover accessor methods
     */
    public void testSetGetNn_fCover() {
        String auxPath_1 = "c:\\isnothereagain\\test_fcover.nna";
        String auxPath_2 = "/usr/local/popocal/strange/dir/file.nnb";

        // initially must be an empty string
        assertEquals("", _config.getNN_fCoverAuxFile());

        // null is not allowed to set
        try {
            _config.setNN_fCoverAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        _config.setNN_fCoverAuxFile(auxPath_1);
        assertEquals(auxPath_1, _config.getNN_fCoverAuxFile());

        _config.setNN_fCoverAuxFile(auxPath_2);
        assertEquals(auxPath_2, _config.getNN_fCoverAuxFile());
    }

    /**
     * Tests the correctness of the nn_fAPAR accessor methods
     */
    public void testSetGetNn_fAPAR() {
        String auxPath_1 = "c:\\isnothereagain\\test_fAPAR.nna";
        String auxPath_2 = "/usr/local/popocal/strange/dir/file.nnb";

        // initially must be an empty string
        assertEquals("", _config.getNN_fAPARAuxFile());

        // null is not allowed to set
        try {
            _config.setNN_fAPARAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        _config.setNN_fAPARAuxFile(auxPath_1);
        assertEquals(auxPath_1, _config.getNN_fAPARAuxFile());

        _config.setNN_fAPARAuxFile(auxPath_2);
        assertEquals(auxPath_2, _config.getNN_fAPARAuxFile());
    }

    /**
     * Tests the correctness of the nn_LAIxCab accessor methods
     */
    public void testSetGetNn_LAIxCab() {
        String auxPath_1 = "c:\\isnothereagain\\test_fAPAR.nna";
        String auxPath_2 = "/usr/local/popocal/strange/dir/file.nnb";

        // initially must be an empty string
        assertEquals("", _config.getNN_LAIxCabAuxFile());

        // null is not allowed to set
        try {
            _config.setNN_LAIxCabAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        _config.setNN_LAIxCabAuxFile(auxPath_1);
        assertEquals(auxPath_1, _config.getNN_LAIxCabAuxFile());

        _config.setNN_LAIxCabAuxFile(auxPath_2);
        assertEquals(auxPath_2, _config.getNN_LAIxCabAuxFile());
    }



    /**
     * Tests the correctness of the nn_training_db accessor methods
     */
    public void testSetGetUncertaintyAuxFile() {
        String auxPath_1 = "c:\\isnothereagain\\uncert.par";
        String auxPath_2 = "/usr/local/popocal/strange/dir/file.nnb";

        // initially must be an empty string
        assertEquals("", _config.getUncertaintyAuxFile());

        // null is not allowed to set
        try {
            _config.setUncertaintyAuxFile(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        }

        _config.setUncertaintyAuxFile(auxPath_1);
        assertEquals(auxPath_1, _config.getUncertaintyAuxFile());

        _config.setUncertaintyAuxFile(auxPath_2);
        assertEquals(auxPath_2, _config.getUncertaintyAuxFile());
    }
}
