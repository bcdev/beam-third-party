/*
 * $Id: AerLutTest.java,v 1.3 2006/03/24 08:09:15 meris Exp $
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

public class AerLutTest extends TestCase {

    private AerLut _lut;

    public AerLutTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(AerLutTest.class);
    }

    protected void setUp() {
        _lut = new AerLut();
        assertNotNull(_lut);
    }

    /**
     * Tests the correct functionality of the default constructor
     */
    public void testDefaultConstruction() {
        assertEquals(0, _lut.getNumBands());

        // Try to access a band - must fail
        try {
            _lut.getBand(3);
            fail("Exception expected");
        } catch (ProcessorException e) {
        }

        try {
            _lut.getBand(11);
            fail("Exception expected");
        } catch (ProcessorException e) {
        }

        // name must be empty string
        assertEquals("", _lut.getName());
    }

    /**
     * Tests the construction with a given number of bands
     */
    public void testNumBandConstruction() {
        int expNumBands = 23;

        _lut = new AerLut(expNumBands);
        assertEquals(expNumBands, _lut.getNumBands());

        // test that we get bands within the given range. Although, all of them
        // must be flagged as invalid
        AerBandParam param = null;

        try {
            param = _lut.getBand(4);
            assertNotNull(param);
            assertEquals(false, param.isValid());
        } catch (ProcessorException e) {
            fail("No exception expected");
        }

        try {
            param = _lut.getBand(18);
            assertNotNull(param);
            assertEquals(false, param.isValid());
        } catch (ProcessorException e) {
            fail("No exception expected");
        }

        // requests to bands out of range must raise an exception
        try {
            param = _lut.getBand(0);
            fail("Exception expected");
        } catch (ProcessorException e) {
        }

        try {
            param = _lut.getBand(109);
            fail("Exception expected");
        } catch (ProcessorException e) {
        }
    }

    /**
     * Tests adding and retrieving bands
     */
    public void testAddRetrieveBands() {
        int expNumBands = 9;

        _lut.setNumBands(expNumBands);
        assertEquals(expNumBands, _lut.getNumBands());

        // set up the band parameter objects
        // ---------------------------------
        AerBandParam par1 = new AerBandParam();
        AerBandParam par2 = new AerBandParam();
        AerBandParam par3 = new AerBandParam();
        double[] val_1 = new double[]{1.0, 2.0, 3.0};
        double[] val_2 = new double[]{4.0, 5.0, 6.0};
        double[] val_3 = new double[]{7.0, 8.0, 9.0};

        par1.setA0(val_1[0]);
        par1.setA1(val_1[1]);
        par1.setA2(val_1[2]);
        par1.validate(true);

        par2.setA0(val_2[0]);
        par2.setA1(val_2[1]);
        par2.setA2(val_2[2]);
        par2.validate(true);

        par3.setA0(val_3[0]);
        par3.setA1(val_3[1]);
        par3.setA2(val_3[2]);
        par3.validate(true);

        // add bands at given positions
        // ----------------------------
        try {
            _lut.addBand(5, par1);
            _lut.addBand(7, par2);
            _lut.addBand(9, par3);

            // and then try to retrieve them again
            AerBandParam paramRet;
            double[] vRet;

            paramRet = _lut.getBand(9);
            assertNotNull(paramRet);
            vRet = paramRet.getA();
            assertEquals(true, paramRet.isValid());
            for (int n = 0; n < vRet.length; n++) {
                assertEquals(val_3[n], vRet[n], 1e-6);
            }

            paramRet = _lut.getBand(7);
            assertNotNull(paramRet);
            vRet = paramRet.getA();
            assertEquals(true, paramRet.isValid());
            for (int n = 0; n < vRet.length; n++) {
                assertEquals(val_2[n], vRet[n], 1e-6);
            }

            paramRet = _lut.getBand(5);
            assertNotNull(paramRet);
            vRet = paramRet.getA();
            assertEquals(true, paramRet.isValid());
            for (int n = 0; n < vRet.length; n++) {
                assertEquals(val_1[n], vRet[n], 1e-6);
            }

            // now check some invalids
            paramRet = _lut.getBand(2);
            assertEquals(false, paramRet.isValid());
            paramRet = _lut.getBand(3);
            assertEquals(false, paramRet.isValid());

        } catch (ProcessorException e) {
            fail("NO exception expected");
        }
    }

    public void testAddRetrieveBands_Failures() {
        int expNumBands = 11;

        _lut.setNumBands(expNumBands);
        assertEquals(expNumBands, _lut.getNumBands());

        AerBandParam param = new AerBandParam();
        param.validate(true);

        // try to add it at invalid positions
        try {
            _lut.addBand(0, param);
            fail("Exception expected");
        } catch (ProcessorException e) {
        }
        try {
            _lut.addBand(12, param);
            fail("Exception expected");
        } catch (ProcessorException e) {
        }
        try {
            _lut.addBand(-6, param);
            fail("Exception expected");
        } catch (ProcessorException e) {
        }

        // now try to add null pointer at some locations
        try {
            _lut.addBand(0, null);
            fail("Exception expected");
        } catch (ProcessorException e) {
        } catch (IllegalArgumentException e) {
        }

        try {
            _lut.addBand(-8, null);
            fail("Exception expected");
        } catch (ProcessorException e) {
        } catch (IllegalArgumentException e) {
        }
        try {
            _lut.addBand(125, null);
            fail("Exception expected");
        } catch (ProcessorException e) {
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Tests the functionality of the name accessor.
     */
    public void testSetGetName() {
        String expName_1 = "nasenmann";
        String expName_2 = "heihowhataname";

        // initially no name
        assertEquals("", _lut.getName());

        _lut.setName(expName_1);
        assertEquals(expName_1, _lut.getName());

        _lut.setName(expName_2);
        assertEquals(expName_2, _lut.getName());
    }
}
