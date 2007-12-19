/*
 * $Id: AerBandParamTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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

public class AerBandParamTest extends TestCase {

    private AerBandParam _param;

    public AerBandParamTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(AerBandParamTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _param = new AerBandParam();
        assertNotNull(_param);
    }

    /**
     * Tests the correct functionality of the default constructor
     */
    public void testDefaultConstructor() {
        double expInvalidValue = -1.0;
        int expVectorSize = 3;
        boolean expValid = false;
        double[] vRet;

        assertEquals(expValid, _param.isValid());
        vRet = _param.getA();
        assertNotNull(vRet);
        assertEquals(expVectorSize, vRet.length);
        for (int n = 0; n < vRet.length; n++) {
            assertEquals(expInvalidValue, vRet[n], 1e-6);
        }

        assertEquals("", _param.getName());
    }

    /**
     * Tests the correct functionality of the parameter accessors
     */
    public void testSetGetParameter() {
        double expInvalidValue = -1.0;
        double expVal0 = 2.45;
        double expVal1 = 8.44;
        double expVal2 = 8.12;
        double[] vRet;

        _param.setA0(expVal0);
        vRet = _param.getA();
        assertNotNull(vRet);
        assertEquals(expVal0, vRet[0], 1e-6);
        assertEquals(expInvalidValue, vRet[1], 1e-6);
        assertEquals(expInvalidValue, vRet[2], 1e-6);

        _param.setA1(expVal1);
        vRet = _param.getA();
        assertNotNull(vRet);
        assertEquals(expVal0, vRet[0], 1e-6);
        assertEquals(expVal1, vRet[1], 1e-6);
        assertEquals(expInvalidValue, vRet[2], 1e-6);

        _param.setA2(expVal2);
        vRet = _param.getA();
        assertNotNull(vRet);
        assertEquals(expVal0, vRet[0], 1e-6);
        assertEquals(expVal1, vRet[1], 1e-6);
        assertEquals(expVal2, vRet[2], 1e-6);

        _param.validate(true);
        assertEquals(true, _param.isValid());

        _param.validate(false);
        assertEquals(false, _param.isValid());
    }

    /**
     * ests the correct functionality of the name accessors
     */
    public void testSetGetName() {
        String expName_1 = "name_popame";
        String expName_2 = "gnumpf_is_me";

        _param.setName(expName_1);
        assertEquals(expName_1, _param.getName());

        _param.setName(expName_2);
        assertEquals(expName_2, _param.getName());
    }
}
