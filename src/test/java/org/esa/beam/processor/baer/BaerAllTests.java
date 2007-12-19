package org.esa.beam.processor.baer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Jul 22, 2003
 * Time: 9:15:09 AM
 * To change this template use Options | File Templates.
 */
public class BaerAllTests extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    public BaerAllTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(BaerConstantsTest.suite());
        suite.addTest(BaerRequestElementFactoryTest.suite());
        suite.addTest(org.esa.beam.processor.baer.utils.BaerAllTests.suite());
        suite.addTest(org.esa.beam.processor.baer.algorithm.BaerAllTests.suite());
        suite.addTest(org.esa.beam.processor.baer.auxdata.BaerAllTests.suite());
        return suite;
    }
}
