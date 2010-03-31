/*
 * $Id: ToaVegProcessorConfigurationParserTest.java,v 1.7 2006/03/15 14:16:11 meris Exp $
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
package org.esa.beam.processor.toa.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.processor.common.utils.VegProcessorConfiguration;
import org.esa.beam.processor.toa.ToaVegConstants;
import org.esa.beam.processor.toa.ToaVegProcessor;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

public class ToaVegProcessorConfigurationParserTest extends TestCase {

    private ToaVegProcessorConfigurationParser _parser;
    private ToaVegProcessor _processor;
    private File _auxdataDir;

    public ToaVegProcessorConfigurationParserTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ToaVegProcessorConfigurationParserTest.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _parser = new ToaVegProcessorConfigurationParser();
        assertNotNull(_parser);
        _processor = new ToaVegProcessor();
        _auxdataDir = new File( _processor.getDefaultAuxdataInstallDir(), ToaVegConstants.AUXDATA_DIR );
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
            URL correct = getClass().getResource("/org/esa/beam/processor/toa/testData/CorrectProcessorConfig.xml");
            _parser.parseConfigurationFile(correct, _auxdataDir);
        } catch (IllegalArgumentException e) {
            fail("No exception expected");
        } catch (ProcessorException e) {
            fail("No exception expected");
        }
    }

    /**
     * Tests the parsing capabilities of the parser
     */
    public void testParse() {
        VegProcessorConfiguration config;

        try {
            URL correct = getClass().getResource("/org/esa/beam/processor/toa/testData/CorrectProcessorConfig.xml");
            _parser.parseConfigurationFile(correct, _auxdataDir);
        } catch (IllegalArgumentException e) {
            fail("No exception expected");
        } catch (ProcessorException e) {
            fail("No exception expected");
        }

        File auxdataDir = _auxdataDir;
        String expInStatPath = new File(auxdataDir, "input_statistics.par").getPath();
        String expOutStatPath = new File(auxdataDir, "output_statistics.par").getPath();
        String expNnLaiPath = new File(auxdataDir, "nn_LAI.nna").getPath();
        String expNnfCoverPath = new File(auxdataDir, "nn_fCover.nna").getPath();
        String expNnfAPARPath = new File(auxdataDir, "nn_fAPAR.nna").getPath();
        String expNnLAIxCabPath = new File(auxdataDir, "nn_LAIxCab.nna").getPath();

        config = _parser.getConfiguration();
        assertNotNull(config);

        assertEquals(expInStatPath, config.getInputStatisticsAuxFile());
        assertEquals(expOutStatPath, config.getOutputStatisticsAuxFile());
        assertEquals(expNnLaiPath, config.getNN_LaiAuxFile());
        assertEquals(expNnfCoverPath, config.getNN_fCoverAuxFile());
        assertEquals(expNnfAPARPath, config.getNN_fAPARAuxFile());
        assertEquals(expNnLAIxCabPath, config.getNN_LAIxCabAuxFile());
    }
}
