/*
 * $Id: ToaVegProcessorTest.java,v 1.3 2006/03/23 18:01:25 meris Exp $
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
import org.esa.beam.framework.processor.ProcessorException;

import java.io.File;

public class TocVegProcessorTest extends TestCase {

    public TocVegProcessorTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TocVegProcessorTest.class);
    }

    public void testThatAuxdataDirIsReturnedCorrectly() {
        TocVegProcessor processor = new TocVegProcessor();

    	try {
			processor.initProcessor();
		} catch (ProcessorException e) {
            // thrown, if auxdata dir doesn't exist
            fail("NO exception expected here.");
		}

        final File auxdataDir = processor.getAuxdataInstallDir();
        assertEquals(processor.getSymbolicName(), auxdataDir.getParentFile().getName());
    }
}