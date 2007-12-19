/*
 * $Id: TocVegPixelTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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
import org.esa.beam.processor.common.utils.VegFlagsManager;

public class TocVegPixelTest extends TestCase {

    private TocVegPixel _pixel;

    public TocVegPixelTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TocVegPixelTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _pixel = new TocVegPixel();
        assertNotNull(_pixel);
    }

    /**
     * Tests the correct default constructor.
     */
    public void testDefaultConstruction() {
        assertEquals(0.f, _pixel.getBand_LAI(), 1e-6);
        assertEquals(0.f, _pixel.getBand_fCover(), 1e-6);
        assertEquals(0.f, _pixel.getBand_CabxLAI(), 1e-6);
        assertEquals(0.f, _pixel.getBand_fAPAR(), 1e-6);
        assertEquals(0.f, _pixel.getBand_delta_R(), 1e-6);
        assertEquals(0.f, _pixel.getBand_sigma_LAI(), 1e-6);
        assertEquals(0.f, _pixel.getBand_sigma_fCover(), 1e-6);
        assertEquals(0.f, _pixel.getBand_sigma_LAIxCab(), 1e-6);
        assertEquals(0.f, _pixel.getBand_delta_fAPAR(), 1e-6);
        assertEquals(VegFlagsManager.INVALID_FLAG_MASK, (_pixel.getFlagMask() & VegFlagsManager.INVALID_FLAG_MASK));
    }

    /**
     * Tests the correct functionality of the accessor methods.
     */
    public void testAcessors() {
        float fVal = 34.3f;

        _pixel.setBand_LAI(fVal);
        assertEquals(fVal, _pixel.getBand_LAI(), 1e-6);

        ++fVal;
        _pixel.setBand_fCover(fVal);
        assertEquals(fVal, _pixel.getBand_fCover(), 1e-6);

        ++fVal;
        _pixel.setBand_CabxLAI(fVal);
        assertEquals(fVal, _pixel.getBand_CabxLAI(), 1e-6);

        ++fVal;
        _pixel.setBand_fAPAR(fVal);
        assertEquals(fVal, _pixel.getBand_fAPAR(), 1e-6);

        ++fVal;
        _pixel.setBand_delta_R(fVal);
        assertEquals(fVal, _pixel.getBand_delta_R(), 1e-6);

        ++fVal;
        _pixel.setBand_sigma_LAI(fVal);
        assertEquals(fVal, _pixel.getBand_sigma_LAI(), 1e-6);

        ++fVal;
        _pixel.setBand_sigma_fCover(fVal);
        assertEquals(fVal, _pixel.getBand_sigma_fCover(), 1e-6);

        ++fVal;
        _pixel.setBand_sigma_LAIxCab(fVal);
        assertEquals(fVal, _pixel.getBand_sigma_LAIxCab(), 1e-6);

        ++fVal;
        _pixel.setBand_delta_fAPAR(fVal);
        assertEquals(fVal, _pixel.getBand_delta_fAPAR(), 1e-6);
    }

    /**
     * Tests the correct functionality of the reset() method.
     */
    public void testReset() {
        float fVal = 87.44f;

        _pixel.setBand_LAI(fVal);
        ++fVal;
        _pixel.setBand_fCover(fVal);
        ++fVal;
        _pixel.setBand_CabxLAI(fVal);
        ++fVal;
        _pixel.setBand_fAPAR(fVal);
        ++fVal;
        _pixel.setBand_delta_R(fVal);
        ++fVal;
        _pixel.setBand_sigma_LAI(fVal);
        ++fVal;
        _pixel.setBand_sigma_fCover(fVal);
        ++fVal;
        _pixel.setBand_sigma_LAIxCab(fVal);
        ++fVal;
        _pixel.setBand_delta_fAPAR(fVal);
        _pixel.setFaparOutOfRangeFlag();
        _pixel.setLAIOutOfRangeFlag();

        _pixel.resetPixel();
        assertEquals(0.f, _pixel.getBand_LAI(), 1e-6);
        assertEquals(0.f, _pixel.getBand_fCover(), 1e-6);
        assertEquals(0.f, _pixel.getBand_CabxLAI(), 1e-6);
        assertEquals(0.f, _pixel.getBand_fAPAR(), 1e-6);
        assertEquals(0.f, _pixel.getBand_delta_R(), 1e-6);
        assertEquals(0.f, _pixel.getBand_sigma_LAI(), 1e-6);
        assertEquals(0.f, _pixel.getBand_sigma_fCover(), 1e-6);
        assertEquals(0.f, _pixel.getBand_sigma_LAIxCab(), 1e-6);
        assertEquals(0.f, _pixel.getBand_delta_fAPAR(), 1e-6);

        assertEquals(0, _pixel.getFlagMask());
    }

    /**
     * Tests the correct functionality of the flag accessor methods.
     */
    public void testFlagAcessors() {
        int flagMask = 0;

        _pixel.setInvalidInputFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(VegFlagsManager.INVALID_FLAG_MASK, (flagMask & VegFlagsManager.INVALID_FLAG_MASK));
        assertEquals(VegFlagsManager.INVALID_INPUT_FLAG_MASK, (flagMask & VegFlagsManager.INVALID_INPUT_FLAG_MASK));

        _pixel.reset();
        _pixel.setAlgorithmFailureFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(VegFlagsManager.INVALID_FLAG_MASK, (flagMask & VegFlagsManager.INVALID_FLAG_MASK));
        assertEquals(VegFlagsManager.ALGORITHM_FAILURE_FLAG_MASK, (flagMask & VegFlagsManager.ALGORITHM_FAILURE_FLAG_MASK));

        _pixel.reset();
        _pixel.clearFlags();
        _pixel.setLAIOutOfRangeFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_MASK, (flagMask & VegFlagsManager.LAI_OUT_OF_RANGE_FLAG_MASK));

        _pixel.reset();
        _pixel.clearFlags();
        _pixel.setFCoverOutOfRangeFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_MASK, (flagMask & VegFlagsManager.FCOVER_OUT_OF_RANGE_FLAG_MASK));

        _pixel.reset();
        _pixel.clearFlags();
        _pixel.setLAIxCabOutOfRangeFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_MASK, (flagMask & VegFlagsManager.LAIXCAB_OUT_OF_RANGE_FLAG_MASK));

        _pixel.reset();
        _pixel.clearFlags();
        _pixel.setFaparOutOfRangeFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_MASK, (flagMask & VegFlagsManager.FAPAR_OUT_OF_RANGE_FLAG_MASK));

        // check the clear functionality
        // -----------------------------
        _pixel.reset();
        _pixel.setInvalidInputFlag();
        _pixel.clearFlags();
        assertEquals(0, _pixel.getFlagMask());

        _pixel.reset();
        _pixel.setLAIxCabOutOfRangeFlag();
        _pixel.setFCoverOutOfRangeFlag();
        _pixel.clearFlags();
        assertEquals(0, _pixel.getFlagMask());
    }
}

