/*
 * $Id: ToaVegAllTests.java,v 1.4 2006/03/24 08:22:34 meris Exp $
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
package org.esa.beam.processor.toa;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ToaVegAllTests extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    public ToaVegAllTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(ToaVegConstantsTest.suite());
        suite.addTest(ToaVegProcessorTest.suite());
        suite.addTest(ToaVegRequestElementFactoryTest.suite());
        
        suite.addTest(org.esa.beam.processor.toa.algorithm.ToaVegAlgorithmTest.suite());
        suite.addTest(org.esa.beam.processor.toa.auxdata.ToaVegAllTests.suite());
        suite.addTest(org.esa.beam.processor.toa.utils.ToaVegAllTests.suite());

        return suite;
    }

}
