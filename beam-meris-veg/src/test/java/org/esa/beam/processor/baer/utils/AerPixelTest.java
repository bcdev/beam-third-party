/*
 * $Id: AerPixelTest.java,v 1.3 2006/03/21 17:38:42 meris Exp $
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

public class AerPixelTest extends TestCase {

    private AerPixel _pixel;

    public AerPixelTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(AerPixelTest.class);
    }

    /**
     * Initializes the test environment.
     */
    protected void setUp() {
        _pixel = new AerPixel();
        assertNotNull(_pixel);
    }

    /**
     * Tests the correct functionality of the default constructor.
     */
    public void testDefaultConstruction() {
        // all field shall be set to zero
        for (int n = 0; n < 13; n++) {
            assertEquals(0.f, _pixel.getBand(n), 1e-6);
        }

        assertEquals(0.f, _pixel.getAot_412(), 1e-6);
        assertEquals(0.f, _pixel.getAot_560(), 1e-6);
        assertEquals(0.f, _pixel.getAlpha(), 1e-6);
        assertEquals(0, _pixel.getFlagMask());
    }

    /**
     * Tests the correct functionality of the accessor methods.
     */
    public void testAcessors() {
        float fVal = 1.f;

        for (int n = 0; n < 13; n++) {
            ++fVal;
            _pixel.setBand(fVal, n);
            assertEquals(fVal, _pixel.getBand(n), 1e-6);
        }

        ++fVal;
        _pixel.setAot_412(fVal);
        assertEquals(fVal, _pixel.getAot_412(), 1e-6);

        ++fVal;
        _pixel.setAot_560(fVal);
        assertEquals(fVal, _pixel.getAot_560(), 1e-6);

        ++fVal;
        _pixel.setAlpha(fVal);
        assertEquals(fVal, _pixel.getAlpha(), 1e-6);
    }

    /**
     * Tests that the reflectance accessors are behaving correctly when fed with out-of-range indices
     */
    public void testAccessorFailures() {
        try {
            _pixel.setBand(2.7f, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        try {
            _pixel.setBand(0.2f, 13);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        try {
            _pixel.getBand(-3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        try {
            _pixel.getBand(16);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Tests the correct functionality of the reset method
     */
    public void testReset() {
        float fVal = 45.f;

        for (int n = 0; n < 13; n++) {
            _pixel.setBand(fVal, n);
            ++fVal;
        }
        _pixel.setAot_412(fVal);
        ++fVal;
        _pixel.setAot_560(fVal);
        ++fVal;
        _pixel.setAlpha(fVal);
        _pixel.setAlphaOutOfRangeFlag();
        _pixel.setAotOutOfRangeFlag();
        _pixel.setAtmosphericCorrectionFlag();
        _pixel.setCloudInputFlag();
      //  _pixel.setCloudShadowFlag();
        _pixel.setInvalidOutputFlag();

        _pixel.reset();

        for (int n = 0; n < 13; n++) {
            assertEquals(0.f, _pixel.getBand(n), 1e-6);
            ++fVal;
        }
        assertEquals(0.f, _pixel.getAot_412(), 1e-6);
        assertEquals(0.f, _pixel.getAot_560(), 1e-6);
        assertEquals(0.f, _pixel.getAlpha(), 1e-6);
        assertEquals(0, _pixel.getFlagMask());
    }

    /**
     * Tests the correct functionality of the flag accessing methods.
     */
    public void testFlagAccessors() {
        int flagMask = 0;

        _pixel.setInvalidInputFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(FlagsManager.INVALID_FLAG_MASK, (flagMask & FlagsManager.INVALID_FLAG_MASK));
        assertEquals(FlagsManager.INVALID_INPUT_FLAG_MASK, (flagMask & FlagsManager.INVALID_INPUT_FLAG_MASK));

        _pixel.reset();
        _pixel.setAotOutOfRangeFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(FlagsManager.AOT_OUT_OF_RANGE_FLAG_MASK, (flagMask & FlagsManager.AOT_OUT_OF_RANGE_FLAG_MASK));

        _pixel.reset();
        _pixel.setAlphaOutOfRangeFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(FlagsManager.ALPHA_OUT_OF_RANGE_FLAG_MASK, (flagMask & FlagsManager.ALPHA_OUT_OF_RANGE_FLAG_MASK));

        _pixel.reset();
        _pixel.setAtmosphericCorrectionFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(FlagsManager.CORRECTION_FLAG_MASK, (flagMask & FlagsManager.CORRECTION_FLAG_MASK));

        _pixel.reset();
        _pixel.setCloudInputFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(FlagsManager.INVALID_FLAG_MASK, (flagMask & FlagsManager.INVALID_FLAG_MASK));
        assertEquals(FlagsManager.CLOUD_INPUT_FLAG_MASK, (flagMask & FlagsManager.CLOUD_INPUT_FLAG_MASK));

     /*   _pixel.reset();
        _pixel.setCloudShadowFlag();
        flagMask = _pixel.getFlagMask();
        assertEquals(FlagsManager.CLOUD_SHADOW_FLAG_MASK, (flagMask & FlagsManager.CLOUD_SHADOW_FLAG_MASK));
        assertEquals(FlagsManager.CLOUD_SHADOW_FLAG_MASK, (flagMask & FlagsManager.CLOUD_SHADOW_FLAG_MASK));
       */
        _pixel.reset();
        _pixel.setInvalidOutputFlag();
        flagMask = _pixel.getFlagMask();
       assertEquals(FlagsManager.INVALID_OUTPUT_FLAG_MASK, (flagMask & FlagsManager.INVALID_OUTPUT_FLAG_MASK));



        // check the clear functionality
        // -----------------------------
        _pixel.reset();
        _pixel.setInvalidInputFlag();
        _pixel.setAlphaOutOfRangeFlag();
        _pixel.setAtmosphericCorrectionFlag();
        _pixel.setCloudInputFlag();
      //  _pixel.setCloudShadowFlag();
        _pixel.setAotOutOfRangeFlag();
        _pixel.setInvalidOutputFlag();
        _pixel.clearFlags();
        assertEquals(0, _pixel.getFlagMask());
      }
}
