/*
 * $Id: TocVegFlagsManagerTest.java,v 1.3 2005/11/04 07:47:05 meris Exp $
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
import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.processor.common.utils.VegFlagsManager;
import org.esa.beam.processor.toc.TocVegConstants;

public class TocVegFlagsManagerTest extends TestCase {


    public TocVegFlagsManagerTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TocVegFlagsManagerTest.class);
    }

    /**
     * Tests all flag masks for correctness.
     */
    public void testFlagMasks() {
        assertEquals(0x01, VegFlagsManager.INVALID_FLAG_MASK);
        assertEquals(0x02, VegFlagsManager.INVALID_INPUT_FLAG_MASK);
        assertEquals(0x04, VegFlagsManager.ALGORITHM_FAILURE_FLAG_MASK);
        assertEquals(0x08, VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_MASK);
        assertEquals(0x10, VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_MASK);
        assertEquals(0x20, VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_MASK);
        assertEquals(0x40, VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_MASK);
    }

    /**
     * Tests all flag names for correctness
     */
    public void testFlagNames() {
        assertEquals("INVALID", VegFlagsManager.INVALID_FLAG_NAME);
        assertEquals("INVALID_INPUT", VegFlagsManager.INVALID_INPUT_FLAG_NAME);
        assertEquals("ALGORITHM_FAILURE", VegFlagsManager.ALGORITHM_FAILURE_FLAG_NAME);
        assertEquals("LAI_OUT_OF_RANGE", VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_NAME);
        assertEquals("FCOVER_OUT_OF_RANGE", VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_NAME);
        assertEquals("LAIXCAB_OUT_OF_RANGE", VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_NAME);
        assertEquals("FAPAR_OUT_OF_RANGE", VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_NAME);
    }


    /**
     * Tests the flag-setting methods for correctness
     */
    public void testFlagSetters() {
        int flag = 0;

        // invalid
        flag = VegFlagsManager.setInvalidFlag(flag);
        assertEquals(VegFlagsManager.INVALID_FLAG_MASK, flag & VegFlagsManager.INVALID_FLAG_MASK);

        // invalid_input
        flag = 0;
        flag = VegFlagsManager.setInvalidInputFlag(flag);
        assertEquals(VegFlagsManager.INVALID_FLAG_MASK, flag & VegFlagsManager.INVALID_FLAG_MASK);
        assertEquals(VegFlagsManager.INVALID_INPUT_FLAG_MASK, flag & VegFlagsManager.INVALID_INPUT_FLAG_MASK);

        // algorithm_failure
        flag = 0;
        flag = VegFlagsManager.setAlgorithmFailureFlag(flag);
        assertEquals(VegFlagsManager.INVALID_FLAG_MASK, flag & VegFlagsManager.INVALID_FLAG_MASK);
        assertEquals(VegFlagsManager.ALGORITHM_FAILURE_FLAG_MASK, flag & VegFlagsManager.ALGORITHM_FAILURE_FLAG_MASK);

        // lai_out_of_range
        flag = 0;
        flag = VegFlagsManager.setLaiOutOfRangeFlag(flag);
        assertEquals(VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_MASK, flag & VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_MASK);

        // fcover_out_of_range
        flag = 0;
        flag = VegFlagsManager.setFCoverOutOfRangeFlag(flag);
        assertEquals(VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_MASK, flag & VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_MASK);

        // laixcab_out_of_range
        flag = 0;
        flag = VegFlagsManager.setLaixCabOutOfRangeFlag(flag);
        assertEquals(VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_MASK, flag & VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_MASK);

        // fapar_out_of_range
        flag = 0;
        flag = VegFlagsManager.setFaparOutOfRangeFlag(flag);
        assertEquals(VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_MASK, flag & VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_MASK);
    }

    /**
     * Tests the flag codings for correctness
     */
    public void testFlagCodings() {
        FlagCoding coding = null;
        MetadataAttribute attrib = null;

        coding = VegFlagsManager.getCoding(TocVegConstants.VEG_FLAGS_BAND_NAME);
        assertNotNull(coding);

        assertEquals(TocVegConstants.VEG_FLAGS_BAND_NAME, coding.getName());
        assertEquals(7, coding.getNumAttributes());

        attrib = coding.getFlag(VegFlagsManager.INVALID_FLAG_NAME);
        assertNotNull(attrib);
        assertEquals(VegFlagsManager.INVALID_FLAG_NAME, attrib.getName());
        assertEquals(VegFlagsManager.INVALID_FLAG_DESCRIPTION, attrib.getDescription());
        assertEquals(VegFlagsManager.INVALID_FLAG_MASK, attrib.getData().getElemInt());

        attrib = coding.getFlag(VegFlagsManager.INVALID_INPUT_FLAG_NAME);
        assertNotNull(attrib);
        assertEquals(VegFlagsManager.INVALID_INPUT_FLAG_NAME, attrib.getName());
        assertEquals(VegFlagsManager.INVALID_INPUT_FLAG_DESCRIPTION, attrib.getDescription());
        assertEquals(VegFlagsManager.INVALID_INPUT_FLAG_MASK, attrib.getData().getElemInt());

        attrib = coding.getFlag(VegFlagsManager.ALGORITHM_FAILURE_FLAG_NAME);
        assertNotNull(attrib);
        assertEquals(VegFlagsManager.ALGORITHM_FAILURE_FLAG_NAME, attrib.getName());
        assertEquals(VegFlagsManager.ALGORITHM_FAILURE_FLAG_DESCRIPTION, attrib.getDescription());
        assertEquals(VegFlagsManager.ALGORITHM_FAILURE_FLAG_MASK, attrib.getData().getElemInt());

        attrib = coding.getFlag(VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_NAME);
        assertNotNull(attrib);
        assertEquals(VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_NAME, attrib.getName());
        assertEquals(VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_DESCRIPTION, attrib.getDescription());
        assertEquals(VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_MASK, attrib.getData().getElemInt());

        attrib = coding.getFlag(VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_NAME);
        assertNotNull(attrib);
        assertEquals(VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_NAME, attrib.getName());
        assertEquals(VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_DESCRIPTION, attrib.getDescription());
        assertEquals(VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_MASK, attrib.getData().getElemInt());

        attrib = coding.getFlag(VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_NAME);
        assertNotNull(attrib);
        assertEquals(VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_NAME, attrib.getName());
        assertEquals(VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_DESCRIPTION, attrib.getDescription());
        assertEquals(VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_MASK, attrib.getData().getElemInt());

        attrib = coding.getFlag(VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_NAME);
        assertNotNull(attrib);
        assertEquals(VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_NAME, attrib.getName());
        assertEquals(VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_DESCRIPTION, attrib.getDescription());
        assertEquals(VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_MASK, attrib.getData().getElemInt());
    }
}
