/*
 * $Id: BaerAllTests.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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

public class BaerAllTests extends TestCase {

     public BaerAllTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(RelAerPhaseLoaderTest.suite());
        suite.addTest(AerPhaseLoaderTest.suite());
        suite.addTest(NdviLoaderTest.suite());
        suite.addTest(GroundReflectanceLoaderTest.suite());
        suite.addTest(AerBandParamTest.suite());
        suite.addTest(AerLutTest.suite());
        suite.addTest(SoilFractionLoaderTest.suite());
        suite.addTest(F_TuningLoaderTest.suite());
        suite.addTest(SpectrumTest.suite());
        suite.addTest(AerDiffTransmLoaderTest.suite());
        suite.addTest(HemisphReflecLoaderTest.suite());

        return suite;
    }
}
