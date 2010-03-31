/*
 * $Id: TocVegAllTests.java,v 1.2 2005/02/18 14:20:19 meris Exp $
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
package org.esa.beam.processor.toc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class TocVegAllTests extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    public TocVegAllTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(TocVegConstantsTest.suite());
        suite.addTest(TocVegRequestElementFactoryTest.suite());
        suite.addTest(org.esa.beam.processor.toc.utils.TocVegAllTests.suite());
        suite.addTest(org.esa.beam.processor.toc.algorithm.TocVegAlgorithmTest.suite());
        suite.addTest(org.esa.beam.processor.toc.auxdata.TocVegAllTests.suite());

        return suite;
    }

}
