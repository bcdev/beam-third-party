/*
 * $Id: BaerProcessorTest.java,v 1.1 2005/04/26 12:14:08 meris Exp $
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
package org.esa.beam.processor.baer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;

import org.esa.beam.framework.processor.ProcessorException;

public class BaerProcessorTest extends TestCase {

    public BaerProcessorTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(BaerProcessorTest.class);
    }

    public void testThatAuxdataDirIsReturnedCorrectly() {
        BaerProcessor processor = new BaerProcessor();
    	
    	try {
			processor.initProcessor();
		} catch (ProcessorException e) {
			fail("NO exception expected here.");
		}
    	
        final File auxdataDir = processor.getAuxdataInstallDir();
        assertEquals(processor.getSymbolicName(), auxdataDir.getParentFile().getName());
    }
}
