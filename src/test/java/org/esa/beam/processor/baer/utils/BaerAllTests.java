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
package org.esa.beam.processor.baer.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BaerAllTests extends TestCase {

    public BaerAllTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(MerisPixelTest.suite());
        suite.addTest(AerPixelTest.suite());
        suite.addTest(FlagsManagerTest.suite());
        suite.addTest(ProcessorConfigurationTests.suite());
        suite.addTest(ProcessorConfigurationParserTests.suite());
        return suite;
    }

}
