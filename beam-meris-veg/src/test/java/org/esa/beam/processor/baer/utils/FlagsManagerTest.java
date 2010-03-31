/*
 * $Id: FlagsManagerTest.java,v 1.4 2006/03/27 15:31:28 meris Exp $
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
package org.esa.beam.processor.baer.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.MetadataAttribute;

public class FlagsManagerTest extends TestCase{

    public FlagsManagerTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(FlagsManagerTest.class);
    }

    /**
     * Tests all flag constants for correctness.
     */
    public void testFlagConstants() {
        assertEquals("INVALID", FlagsManager.INVALID_FLAG_NAME);
        assertEquals(0x01, FlagsManager.INVALID_FLAG_MASK);
        assertEquals("Pixel contains invalid data and shall not be used for further processing", FlagsManager.INVALID_FLAG_DESCRIPTION);

        assertEquals("INVALID_INPUT", FlagsManager.INVALID_INPUT_FLAG_NAME);
        assertEquals(0x02, FlagsManager.INVALID_INPUT_FLAG_MASK);
        assertEquals("Input data for this pixel was invalid or flagged out", FlagsManager.INVALID_INPUT_FLAG_DESCRIPTION);

        assertEquals("CLOUD_INPUT", FlagsManager.CLOUD_INPUT_FLAG_NAME);
        assertEquals(0x04, FlagsManager.CLOUD_INPUT_FLAG_MASK);
        assertEquals("", FlagsManager.CLOUD_INPUT_FLAG_DESCRIPTION);

       /* assertEquals("CLOUD_SHADOW_INPUT", FlagsManager.CLOUD_SHADOW_FLAG_NAME);
        assertEquals(0x08, FlagsManager.CLOUD_SHADOW_FLAG_MASK);
        assertEquals("", FlagsManager.CLOUD_SHADOW_FLAG_DESCRIPTION);  */

        assertEquals("AOT_OUT_OF_RANGE", FlagsManager.AOT_OUT_OF_RANGE_FLAG_NAME);
        assertEquals(0x10, FlagsManager.AOT_OUT_OF_RANGE_FLAG_MASK);
        assertEquals("The calculated AOT is out of the valid range", FlagsManager.AOT_OUT_OF_RANGE_FLAG_DESCRIPTION);

        assertEquals("ALPHA_OUT_OF_RANGE", FlagsManager.ALPHA_OUT_OF_RANGE_FLAG_NAME);
        assertEquals(0x20, FlagsManager.ALPHA_OUT_OF_RANGE_FLAG_MASK);
        assertEquals("The calculated ALPHA coefficient is out of the valid range", FlagsManager.ALPHA_OUT_OF_RANGE_FLAG_DESCRIPTION);

        assertEquals("INVALID_OUTPUT", FlagsManager.INVALID_OUTPUT_FLAG_NAME);
        assertEquals(0x40, FlagsManager.INVALID_OUTPUT_FLAG_MASK);
        assertEquals("", FlagsManager.INVALID_OUTPUT_FLAG_DESCRIPTION);

        assertEquals("SMAC_CORRECTION", FlagsManager.CORRECTION_FLAG_NAME);
        assertEquals(0x80, FlagsManager.CORRECTION_FLAG_MASK);
        assertEquals("The Atmospheric correction used is SMAC", FlagsManager.CORRECTION_FLAG_DESCRIPTION);
    }

    /**
     * Tests the flag coding for correctness
     */
    public void testFlagCoding() {
        MetadataAttribute   attrib = null;
        FlagCoding coding = FlagsManager.getFlagCoding();

        assertNotNull(coding);
        assertEquals(7, coding.getNumAttributes());

        attrib = coding.getFlag("INVALID");
        assertNotNull(attrib);
        assertEquals("Pixel contains invalid data and shall not be used for further processing", attrib.getDescription());
        assertEquals(0x01, attrib.getData().getElemInt());

        attrib = coding.getFlag("INVALID_INPUT");
        assertNotNull(attrib);
        assertEquals("Input data for this pixel was invalid or flagged out", attrib.getDescription());
        assertEquals(0x02, attrib.getData().getElemInt());

        attrib = coding.getFlag("CLOUD_INPUT");
        assertNotNull(attrib);
        assertEquals("", attrib.getDescription());
        assertEquals(0x04, attrib.getData().getElemInt());

      /*  attrib = coding.getFlag("CLOUD_SHADOW_INPUT");
        assertNotNull(attrib);
        assertEquals("", attrib.getDescription());
        assertEquals(0x08, attrib.getData().getElemInt());   */

        attrib = coding.getFlag("AOT_OUT_OF_RANGE");
        assertNotNull(attrib);
        assertEquals("The calculated AOT is out of the valid range", attrib.getDescription());
        assertEquals(0x10, attrib.getData().getElemInt());

        attrib = coding.getFlag("ALPHA_OUT_OF_RANGE");
        assertNotNull(attrib);
        assertEquals("The calculated ALPHA coefficient is out of the valid range", attrib.getDescription());
        assertEquals(0x20, attrib.getData().getElemInt());

        attrib = coding.getFlag("INVALID_OUTPUT");
        assertNotNull(attrib);
        assertEquals("", attrib.getDescription());
        assertEquals(0x40, attrib.getData().getElemInt());

        attrib = coding.getFlag("SMAC_CORRECTION");
        assertNotNull(attrib);
        assertEquals("The Atmospheric correction used is SMAC", attrib.getDescription());
        assertEquals(0x80, attrib.getData().getElemInt());

     }
}
