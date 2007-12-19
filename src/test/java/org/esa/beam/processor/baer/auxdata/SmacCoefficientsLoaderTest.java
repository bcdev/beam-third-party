/*
 * $Id: SmacCoefficientsLoaderTest.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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
import org.esa.beam.processor.MerisVegTestConfig;

import java.io.IOException;

public class SmacCoefficientsLoaderTest extends TestCase {

    public static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/smac_coefficients.dat";
    public static final String NO_SMAC_COEFF_AUX_FILE = "./test/org/esa/beam/processor/baer/testData/aux_no_smac_coefficients.dat";

    private SmacCoefficientsLoader _loader;

    public SmacCoefficientsLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(SmacCoefficientsLoaderTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _loader = new SmacCoefficientsLoader();
        assertNotNull(_loader);
    }

    /**
     * Test the formal interface functionality
     */
    public void testInterfaceFunctionality() {
        // must be implementing the SmacCoefficientsAccess interface
        assertTrue(_loader instanceof SmacCoefficientsAccess);

        // shall not accept null files
        try {
            _loader.load(null);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }

        // shall be returning the default value when not having loaded an
        // aux file yet


	// shall NOT fail when reading a correct file
        try {
       //     _loader.load(_beamPath+CORRECT_FILE);
                 _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }
    }

    /**
     * Tests the reading of a correct aux file
     */
    public void testReadCorrectFile() {
        double Ah2o = 0.0;
        double Nh2o = 0.0;
        double Ao3= -0.002494;
        double No3 = 0.999960;
        double Ao2 = 0.0;
        double No2 = 0.0;
        double Po2 = 0.0;
        double Aco2 = 0.0;
        double Nco2 = 0.0;
        double Pco2 = 0.0;
        double Ach4 = 0.0;
        double Nch4 = 0.0;
        double Pch4 = 0.0;
        double Ano2 = 0.0;
        double Nno2 =0.0;
        double Pno2 = 0.0;
        double Aco =0.0;
        double Nco = 0.0;
        double Pco = 0.0;
        double A0s = 0.094242;
        double A1s = 0.176841;
        double A2s = -0.074588;
        double A3s = 0.071774;
        double A0T = 1.100451;
        double A1T = -0.227970;
        double A2T = -0.190823;
        double A3T = -0.256343;
        double Taur = 0.238731;
        double Sr = 0.170024;
        double A0taup = -5e-8;
        double A1taup = 1.265563;
        double Wo = 0.900308;
        double Gc = 0.643135;
        double A0P = 6.73774049;
        double A1P = -0.189969327;
        double A2P = 2.08328921e-3;
        double A3P = -1.029542913e-5;
        double A4P = 1.94432675e-8;
        double Rest1 = -0.014628;
        double Rest2 = -0.030867;
        double Rest3 = -0.020345;
        double Rest4 = -0.002695;
        double Resr1 = -0.008784;
        double Resr2 = 0.012882;
        double Resr3 = 0.019436;
        double Resa1 = -0.011268;
        double Resa2 = -0.035676;
        double Resa3 = -0.027737;
        double Resa4 = -0.006755;


        try {
            _loader.load(CORRECT_FILE);
        } catch (IllegalArgumentException e) {
            fail("NO Exception expected");
        } catch (IOException e) {
            fail("NO Exception expected");
        }

    assertEquals(Ah2o, _loader.getAh2o(), 1e-6);
	assertEquals(Nh2o, _loader.getNh2o(), 1e-6);
	assertEquals(Ao3, _loader.getAo3(), 1e-6);
	assertEquals(No3, _loader.getNo3(), 1e-6);
	assertEquals(Ao2, _loader.getAo2(), 1e-6);
	assertEquals(No2, _loader.getNo2(), 1e-6);
	assertEquals(Po2, _loader.getPo2(), 1e-6);
	assertEquals(Aco2, _loader.getAco2(), 1e-6);
	assertEquals(Nco2, _loader.getNco2(), 1e-6);
	assertEquals(Pco2, _loader.getPco2(), 1e-6);
	assertEquals(Ach4, _loader.getAch4(), 1e-6);
	assertEquals(Nch4, _loader.getNch4(), 1e-6);
    assertEquals(Pch4, _loader.getPch4(), 1e-6);
	assertEquals(Ano2, _loader.getAno2(), 1e-6);
	assertEquals(Nno2, _loader.getNno2(), 1e-6);
	assertEquals(Pno2, _loader.getPno2(), 1e-6);
	assertEquals(Aco, _loader.getAco(), 1e-6);
	assertEquals(Nco, _loader.getNco(), 1e-6);
	assertEquals(Pco, _loader.getPco(), 1e-6);
	assertEquals(A0s, _loader.getA0s(), 1e-6);
	assertEquals(A1s, _loader.getA1s(), 1e-6);
	assertEquals(A2s, _loader.getA2s(), 1e-6);
	assertEquals(A3s, _loader.getA3s(), 1e-6);
	assertEquals(A0T, _loader.getA0T(), 1e-6);
	assertEquals(A1T, _loader.getA1T(), 1e-6);
	assertEquals(A2T, _loader.getA2T(), 1e-6);
	assertEquals(A3T, _loader.getA3T(), 1e-6);
	assertEquals(Taur, _loader.getTaur(), 1e-6);
	assertEquals(Sr, _loader.getSr(), 1e-6);
	assertEquals(A0taup, _loader.getA0taup(), 1e-6);
	assertEquals(A1taup, _loader.getA1taup(), 1e-6);
	assertEquals(Wo, _loader.getWo(), 1e-6);
	assertEquals(Gc, _loader.getGc(), 1e-6);
	assertEquals(A0P, _loader.getA0P(), 1e-6);
	assertEquals(A1P, _loader.getA1P(), 1e-6);
	assertEquals(A2P, _loader.getA2P(), 1e-6);
	assertEquals(A3P, _loader.getA3P(), 1e-6);
	assertEquals(A4P, _loader.getA4P(), 1e-6);
	assertEquals(Rest1, _loader.getRest1(), 1e-6);
	assertEquals(Rest2, _loader.getRest2(), 1e-6);
	assertEquals(Rest3, _loader.getRest3(), 1e-6);
	assertEquals(Rest4, _loader.getRest4(), 1e-6);
	assertEquals(Resr1, _loader.getResr1(), 1e-6);
	assertEquals(Resr2, _loader.getResr2(), 1e-6);
	assertEquals(Resr3, _loader.getResr3(), 1e-6);
	assertEquals(Resa1, _loader.getResa1(), 1e-6);
	assertEquals(Resa2, _loader.getResa2(), 1e-6);
	assertEquals(Resa3, _loader.getResa3(), 1e-6);
	assertEquals(Resa4, _loader.getResa4(), 1e-6);
    }
}

