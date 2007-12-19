/*
 * $Id: ToaVegAlgorithmTest.java,v 1.6 2006/03/21 17:39:16 meris Exp $
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
package org.esa.beam.processor.toa.algorithm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.processor.MerisVegTestConfig;
import org.esa.beam.processor.toa.auxdata.ToaVegInputStatisticsLoader;
import org.esa.beam.processor.toa.auxdata.ToaVegOutputStatisticsLoader;
import org.esa.beam.processor.toa.auxdata.ToaVegUncertaintyModelLoader;

import java.io.IOException;


public class ToaVegAlgorithmTest extends TestCase {

    private ToaVegAlgorithm _algo;
    private ToaVegInputStatisticsLoader _inStatAux;
    private ToaVegOutputStatisticsLoader _outStatAux;
    private ToaVegUncertaintyModelLoader _uncertaintyAux;


    private static final String CORRECT_FILE_INPUT_STAT = MerisVegTestConfig.testFileBaseDirPath + "toa/testData/algo_test_in_stat.par";
    private static final String CORRECT_FILE_OUTPUT_STAT = MerisVegTestConfig.testFileBaseDirPath + "toa/testData/algo_test_out_stat.par";
    private static final String CORRECT_FILE_UNCERTAINTY = MerisVegTestConfig.testFileBaseDirPath + "toa/testData/algo_test_uncertainty.par";


    public ToaVegAlgorithmTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ToaVegAlgorithmTest.class);
    }


    public void testWhatever() throws IOException {
        double[] testVal = new double[4];

        double[] testAccessInputStat = {
                0.31792995,
                1.04718879,
                0.00000099616,
                0.68590864,
                -0.999999,
                1,
                0.0891215,
                0.41778551,
                0.07151076,
                0.46906965,
                0.05094766,
                0.52309721,
                0.04281358,
                0.56118259,
                0.02512556,
                0.61873024,
                0.01927991,
                0.63023794,
                0.01944251,
                0.69832564,
                0.01832826,
                0.7548423,
                0.01060944,
                0.76507422,
                0.0595014,
                0.87085622,
                0.07529407,
                0.92774513,
                0.08980312,
                1.10054001,
                0.09010581,
                1.1017671
        };


        double[] testAccessOutputStat = {
                0.000028034,   //FAPAR
                0.96796425,
                0.000018282,   //FCOVER
                0.98850956,
                0.000036178,   //LAI
                5.99982132,
                0.00167064,   //LAIXCAB
                594.623954
        };

        double[] testAccessUncertainty = {
                0.02205, // FAPAR
                0.2312,
                -0.2422,
                0.0004322,  // FCOVER
                0.3711,
                -0.3485,
                -0.1535,    //LAI
                0.776,
                -0.1207,
                0.5928, //LAIXCAB
                0.5024,
                -0.0007351
        };

        /**
         * Initializes the test environment
         */
        _algo = new ToaVegAlgorithm();
        assertNotNull(_algo);

        /**
         * Tests acessor functions
         */


        _inStatAux = new ToaVegInputStatisticsLoader();
        _inStatAux.load(CORRECT_FILE_INPUT_STAT);
        _algo.setInputStatisticsAccess(_inStatAux);
        assertEquals(testAccessInputStat[0], _algo.getTheta_S_Min(), 1e-6);
        assertEquals(testAccessInputStat[1], _algo.getTheta_S_Max(), 1e-6);
        assertEquals(testAccessInputStat[2], _algo.getTheta_V_Min(), 1e-6);
        assertEquals(testAccessInputStat[3], _algo.getTheta_V_Max(), 1e-6);
        assertEquals(testAccessInputStat[4], _algo.getCos_Phi_Min(), 1e-6);
        assertEquals(testAccessInputStat[5], _algo.getCos_Phi_Max(), 1e-6);
        assertEquals(testAccessInputStat[6], _algo.getR_Min(0), 1e-6);
        assertEquals(testAccessInputStat[7], _algo.getR_Max(0), 1e-6);
        assertEquals(testAccessInputStat[8], _algo.getR_Min(1), 1e-6);
        assertEquals(testAccessInputStat[9], _algo.getR_Max(1), 1e-6);
        assertEquals(testAccessInputStat[10], _algo.getR_Min(2), 1e-6);
        assertEquals(testAccessInputStat[11], _algo.getR_Max(2), 1e-6);
        assertEquals(testAccessInputStat[12], _algo.getR_Min(3), 1e-6);
        assertEquals(testAccessInputStat[13], _algo.getR_Max(3), 1e-6);
        assertEquals(testAccessInputStat[14], _algo.getR_Min(4), 1e-6);
        assertEquals(testAccessInputStat[15], _algo.getR_Max(4), 1e-6);
        assertEquals(testAccessInputStat[16], _algo.getR_Min(5), 1e-6);
        assertEquals(testAccessInputStat[17], _algo.getR_Max(5), 1e-6);
        assertEquals(testAccessInputStat[18], _algo.getR_Min(6), 1e-6);
        assertEquals(testAccessInputStat[19], _algo.getR_Max(6), 1e-6);
        assertEquals(testAccessInputStat[20], _algo.getR_Min(7), 1e-6);
        assertEquals(testAccessInputStat[21], _algo.getR_Max(7), 1e-6);
        assertEquals(testAccessInputStat[22], _algo.getR_Min(8), 1e-6);
        assertEquals(testAccessInputStat[23], _algo.getR_Max(8), 1e-6);
        assertEquals(testAccessInputStat[24], _algo.getR_Min(9), 1e-6);
        assertEquals(testAccessInputStat[25], _algo.getR_Max(9), 1e-6);
        assertEquals(testAccessInputStat[26], _algo.getR_Min(10), 1e-6);
        assertEquals(testAccessInputStat[27], _algo.getR_Max(10), 1e-6);
        assertEquals(testAccessInputStat[28], _algo.getR_Min(11), 1e-6);
        assertEquals(testAccessInputStat[29], _algo.getR_Max(11), 1e-6);
        assertEquals(testAccessInputStat[30], _algo.getR_Min(12), 1e-6);
        assertEquals(testAccessInputStat[31], _algo.getR_Max(12), 1e-6);

        _outStatAux = new ToaVegOutputStatisticsLoader();
        _outStatAux.load(CORRECT_FILE_OUTPUT_STAT);
        _algo.setOutputStatisticsAccess(_outStatAux);


        _algo.getfAPARConstants(testVal);
        assertEquals(testAccessOutputStat[0], testVal[0], 1e-6);
        assertEquals(testAccessOutputStat[1], testVal[1], 1e-6);

        _algo.getfCoverConstants(testVal);
        assertEquals(testAccessOutputStat[2], testVal[0], 1e-6);
        assertEquals(testAccessOutputStat[3], testVal[1], 1e-6);

        _algo.getLAIConstants(testVal);
        assertEquals(testAccessOutputStat[4], testVal[0], 1e-6);
        assertEquals(testAccessOutputStat[5], testVal[1], 1e-6);

        _algo.getLAIxCabConstants(testVal);
        assertEquals(testAccessOutputStat[6], testVal[0], 1e-6);
        assertEquals(testAccessOutputStat[7], testVal[1], 1e-6);

        _uncertaintyAux = new ToaVegUncertaintyModelLoader();
        _uncertaintyAux.load(CORRECT_FILE_UNCERTAINTY);
        _algo.setUncertaintyModelAccess(_uncertaintyAux);
        _uncertaintyAux.getfAPARCoefficients(testVal);
        assertEquals(testAccessUncertainty[0], testVal[0], 1e-6);
        assertEquals(testAccessUncertainty[1], testVal[1], 1e-6);
        assertEquals(testAccessUncertainty[2], testVal[2], 1e-6);

        _uncertaintyAux.getfCoverCoefficients(testVal);
        assertEquals(testAccessUncertainty[3], testVal[0], 1e-6);
        assertEquals(testAccessUncertainty[4], testVal[1], 1e-6);
        assertEquals(testAccessUncertainty[5], testVal[2], 1e-6);

        _uncertaintyAux.getLAICoefficients(testVal);
        assertEquals(testAccessUncertainty[6], testVal[0], 1e-6);
        assertEquals(testAccessUncertainty[7], testVal[1], 1e-6);
        assertEquals(testAccessUncertainty[8], testVal[2], 1e-6);

        _uncertaintyAux.getLAIxCabCoefficients(testVal);
        assertEquals(testAccessUncertainty[9], testVal[0], 1e-6);
        assertEquals(testAccessUncertainty[10], testVal[1], 1e-6);
        assertEquals(testAccessUncertainty[11], testVal[2], 1e-6);


    }
}
