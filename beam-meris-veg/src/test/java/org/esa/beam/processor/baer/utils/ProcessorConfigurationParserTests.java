/*
 * $Id: ProcessorConfigurationParserTests.java,v 1.3 2005/04/26 12:14:08 meris Exp $
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
import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.processor.MerisVegTestConfig;
import org.esa.beam.processor.baer.BaerConstants;
import org.esa.beam.processor.baer.BaerProcessor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class ProcessorConfigurationParserTests extends TestCase {

    private ProcessorConfigurationParser _parser;
    private BaerProcessor _processor;
    private File _auxdataDir;
    
    private static final String CORRECT_FILE = MerisVegTestConfig.testFileBaseDirPath + "baer/testData/config.xml";

    public ProcessorConfigurationParserTests(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ProcessorConfigurationParserTests.class);
    }

    /**
     * Initializes the test environment
     */
    protected void setUp() {
        _parser = new ProcessorConfigurationParser();
        assertNotNull(_parser);
        _processor = new BaerProcessor();
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

        //File auxdataDir = BaerProcessor.getAuxdataDir();
        String expNdviPath = new File(_auxdataDir, "ndvi_aux.par").getPath();
        String expAerosolPhasePath = new File(_auxdataDir, "aer_phase_function.xml").getPath();
        String expRelAerosolPhasePath = new File(_auxdataDir, "rel_aerosol_phase_aux.par").getPath();
        String expGroundReflecPath = new File(_auxdataDir, "ground_reflectances.xml").getPath();
        String expSoilFractionPath = new File(_auxdataDir, "soil_fraction_aux.par").getPath();
        String expF_TuningPath = new File(_auxdataDir, "f_tuning_aux.par").getPath();
        String expAerDiffTransmPath = new File(_auxdataDir, "aerosol_diff_transm.par").getPath();
        String expHemisphReflecPath = new File(_auxdataDir, "hemispherical_reflectance.par").getPath();
        ProcessorConfiguration config;

        config = _parser.getConfiguration();
        assertNotNull(config);

        assertEquals(expNdviPath, config.getNdviAuxFile());
        assertEquals(expSoilFractionPath, config.getSoilFractionAuxFile());
        assertEquals(expAerosolPhasePath, config.getAerosolPhaseAuxFile());
        assertEquals(expRelAerosolPhasePath, config.getRelativeAerosolPhaseAuxFile());
        assertEquals(expGroundReflecPath, config.getGroundReflectanceAuxFile());
        assertEquals(expF_TuningPath, config.getF_TuningAuxFile());
        assertEquals(expAerDiffTransmPath, config.getAerDiffTransmAuxFile());
        assertEquals(expHemisphReflecPath, config.getHemisphReflecAuxFile());
    }
}
