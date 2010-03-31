/*
 * $Id: TocVegProcessorConfigurationParserTest.java,v 1.6 2006/03/24 08:09:15 meris Exp $
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
package org.esa.beam.processor.toc.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.processor.baer.BaerConstants;
import org.esa.beam.processor.toc.TocVegProcessor;
import org.esa.beam.processor.MerisVegTestConfig;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class TocVegProcessorConfigurationParserTest extends TestCase {

    private static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "toc/testData/CorrectProcessorConfig.xml";
    private TocVegProcessor _processor;
    private File _auxdataDir;
    
    private TocVegProcessorConfigurationParser _parser;

    public TocVegProcessorConfigurationParserTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TocVegProcessorConfigurationParserTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _parser = new TocVegProcessorConfigurationParser();
        assertNotNull(_parser);
        _processor = new TocVegProcessor();
        _auxdataDir = new File( _processor.getDefaultAuxdataInstallDir(), BaerConstants.AUXDATA_DIR );
    }

    /**
     * Tests the parser interface for correct functionality
     */
    public void testParserInterface() {
        // null pointer as argument not allowed
        try {
            _parser.parseConfigurationFile(null, _auxdataDir);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (ProcessorException e) {
        }

        // non existing files not allowed
        try {
            URL notExistingFile = new URL("file", "", "f:\\notThereDirectory\\invalidFile.xml");
            _parser.parseConfigurationFile(notExistingFile, _auxdataDir);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
        } catch (ProcessorException e) {
        } catch (MalformedURLException e) {
            fail("This exception type is NOT expected here");
        }

        // check with correct file
        try {
            URL correct = new URL("file", "", CORRECT_FILE);
            _parser.parseConfigurationFile(correct, _auxdataDir);
        } catch (IllegalArgumentException e) {
            fail("No exception expected");
        } catch (ProcessorException e) {
            fail("No exception expected");
        } catch (MalformedURLException e) {
            fail("This exception type is NOT expected here");
        }
    }

    /**
     * Tests the parsing capabilities of the parser
     */
    public void testParse() {
        TocVegProcessorConfiguration config;

        try {
            URL correct = new URL("file", "", CORRECT_FILE);
            _parser.parseConfigurationFile(correct, _auxdataDir);
        } catch (IllegalArgumentException e) {
            fail("No exception expected");
        } catch (ProcessorException e) {
            fail("No exception expected");
        } catch (MalformedURLException e) {
            fail("This exception type is NOT expected here");
        }

        File dir = _auxdataDir;
        String expNormPath = new File(dir, "normalisation_factor.par").getPath();
        String expInStatPath = new File(dir, "input_statistics.par").getPath();
        String expOutStatPath = new File(dir, "output_statistics.par").getPath();
        String expNnPath = new File(dir, "nn.nna").getPath();

        config = _parser.getConfiguration();
        assertNotNull(config);

        assertEquals(expNormPath, config.getNormalisationFactorAuxFile());
        assertEquals(expInStatPath, config.getInputStatisticsAuxFile());
        assertEquals(expOutStatPath, config.getOutputStatisticsAuxFile());
        assertEquals(expNnPath, config.getNN_AuxFile());
    }
}
