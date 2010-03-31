/*
 * $Id: MerisPixelTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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

public class MerisPixelTest extends TestCase {

    private MerisPixel _pixel;

    public MerisPixelTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(MerisPixelTest.class);
    }

    /**
     * Initializes the test environment.
     */
    protected void setUp() {
        _pixel = new MerisPixel();
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
        assertEquals(0.f, _pixel.getBand_Lat(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Lon(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Sza(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Saa(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Vza(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Vaa(), 1e-6);
        assertEquals(0.f, _pixel.getPressure(), 1e-6);
    }

    /**
     * Tests the correct functionality of the accessor methods.
     */
    public void testAcessors() {
        float fVal = 1.f;

        for (int n = 0; n < 13; n++) {
            _pixel.setBand(fVal, n);
            assertEquals(fVal, _pixel.getBand(n), 1e-6);
            ++fVal;
        }

        ++fVal;
        _pixel.setBand_Lat(fVal);
        assertEquals(fVal, _pixel.getBand_Lat(), 1e-6);

        ++fVal;
        _pixel.setBand_Lon(fVal);
        assertEquals(fVal, _pixel.getBand_Lon(), 1e-6);

        ++fVal;
        _pixel.setBand_Sza(fVal);
        assertEquals(fVal, _pixel.getBand_Sza(), 1e-6);

        ++fVal;
        _pixel.setBand_Saa(fVal);
        assertEquals(fVal, _pixel.getBand_Saa(), 1e-6);

        ++fVal;
        _pixel.setBand_Vza(fVal);
        assertEquals(fVal, _pixel.getBand_Vza(), 1e-6);

        ++fVal;
        _pixel.setBand_Vaa(fVal);
        assertEquals(fVal, _pixel.getBand_Vaa(), 1e-6);

        ++fVal;
        _pixel.setPressure(fVal);
        assertEquals(fVal, _pixel.getPressure(), 1e-6);
    }

    /**
     * Tests that the reflectance accessors behae correctly when invoked with an out-of-range index
     */
    public void testAccessorOutOfIndex() {
        try {
            _pixel.setBand(0.9f, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        try {
            _pixel.setBand(1.3f, 14);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }

        try {
            _pixel.getBand(-1);
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
     * Tests the assignment operation for correctness
     */
    public void testAssignment() {
        MerisPixel target = new MerisPixel();

        // set values in member pixel
        float fVal = 167.f;
        for (int n = 0; n < 13; n++) {
            _pixel.setBand(fVal, n);
            --fVal;
        }
        _pixel.setBand_Lat(fVal);
        --fVal;
        _pixel.setBand_Lon(fVal);
        --fVal;
        _pixel.setBand_Sza(fVal);
        --fVal;
        _pixel.setBand_Saa(fVal);
        --fVal;
        _pixel.setBand_Vza(fVal);
        --fVal;
        _pixel.setBand_Vaa(fVal);
        --fVal;
        _pixel.setPressure(fVal);

        // now asign and check
        target.assign(_pixel);

        fVal = 167.f;
        for (int n = 0; n < 13; n++) {
            assertEquals(fVal, target.getBand(n), 1e-6);
            --fVal;
        }
        assertEquals(fVal, target.getBand_Lat(), 1e-6);
        --fVal;
        assertEquals(fVal, target.getBand_Lon(), 1e-6);
        --fVal;
        assertEquals(fVal, target.getBand_Sza(), 1e-6);
        --fVal;
        assertEquals(fVal, target.getBand_Saa(), 1e-6);
        --fVal;
        assertEquals(fVal, target.getBand_Vza(), 1e-6);
        --fVal;
        assertEquals(fVal, target.getBand_Vaa(), 1e-6);
        --fVal;
        assertEquals(fVal, target.getPressure(), 1e-6);
    }

    /**
     * Tests the correct functionality of the reset method
     */
    public void testReset() {
        float fVal = 23.8f;

        for (int n = 0; n < 13; n++) {
            _pixel.setBand(fVal, n);
            ++fVal;
        }
        _pixel.setBand_Lat(fVal);
        ++fVal;
        _pixel.setBand_Lon(fVal);
        ++fVal;
        _pixel.setBand_Sza(fVal);
        ++fVal;
        _pixel.setBand_Saa(fVal);
        ++fVal;
        _pixel.setBand_Vza(fVal);
        ++fVal;
        _pixel.setBand_Vaa(fVal);

        _pixel.reset();

        for (int n = 0; n < 13; n++) {
            assertEquals(0.f, _pixel.getBand(n), 1e-6);
        }
        assertEquals(0.f, _pixel.getBand_Lat(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Lon(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Sza(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Saa(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Vza(), 1e-6);
        assertEquals(0.f, _pixel.getBand_Vaa(), 1e-6);
    }
}
