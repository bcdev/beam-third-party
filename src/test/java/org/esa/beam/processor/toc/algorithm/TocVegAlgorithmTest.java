/*
 * $Id: TocVegAlgorithmTest.java,v 1.4 2006/03/27 15:33:02 meris Exp $
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
package org.esa.beam.processor.toc.algorithm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.esa.beam.processor.common.auxdata.VegNormFactorLoader;
import org.esa.beam.processor.toc.TocVegConstants;
import org.esa.beam.processor.toc.auxdata.TocVegInputStatisticsLoader;
import org.esa.beam.processor.toc.auxdata.TocVegOutputStatisticsLoader;
import org.esa.beam.processor.MerisVegTestConfig;

import java.io.IOException;


public class TocVegAlgorithmTest extends TestCase {

    private TocVegAlgorithm _algo;
    private VegNormFactorLoader _normFactorAux;
    private TocVegInputStatisticsLoader _inStatAux;
    private TocVegOutputStatisticsLoader _outStatAux;

    private static final String CORRECT_FILE_NORM_FACTOR = MerisVegTestConfig.testFileBaseDirPath + "toc/testData/algo_test_norm_factor.par";
    private static final String CORRECT_FILE_INPUT_STAT = MerisVegTestConfig.testFileBaseDirPath + "toc/testData/algo_test_in_stat.par";
    private static final String CORRECT_FILE_OUTPUT_STAT = MerisVegTestConfig.testFileBaseDirPath + "toc/testData/algo_test_out_stat.par";

    //   private String _beamPath;

    public TocVegAlgorithmTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TocVegAlgorithmTest.class);
    }


    public void testWhatever() throws IOException {
        double[] testVal = new double[4];

        double testNormFactor = 0.66;
        double[] testAccessInputStat={41.491, // theta_s
                                      11.712,
                                      22.172, //theta_v
                                      10.55,
                                      0.022895, //cos_phi
                                      0.77433,
                                      0.19841, //r
                                      0.16899
        };

        double[] testAccessOutputStat={0.70321,   //FAPAR
                                       0.22361,
                                       3.7167E-5,
                                       0.98728,
                                       0.60202,   //FCOVER
                                       0.26446,
                                       2.9771E-5,
                                       0.99556,
                                       3.1099,   //LAI
                                       1.9554,
                                       6.0712E-5,
                                       7.9669,
                                       173.07,   //LAIXCAB
                                       137.97,
                                       0.00091478,
                                       765.6
        };


    /**
     * Initializes the test environment
     */
        _algo = new TocVegAlgorithm();
        assertNotNull(_algo);

        /**
         * Tests acessor functions
         */

        _normFactorAux = new VegNormFactorLoader(TocVegConstants.NORMALISATION_FACTOR_DEFAULT,TocVegConstants.LOGGER_NAME);
        _normFactorAux.load(CORRECT_FILE_NORM_FACTOR);
        _algo.setNormFactorAccess(_normFactorAux);
        assertEquals(testNormFactor, _algo.getNormFactor(), 1e-6);

        _inStatAux = new TocVegInputStatisticsLoader();
        _inStatAux.load(CORRECT_FILE_INPUT_STAT);
        _algo.setInputStatisticsAccess(_inStatAux);
        assertEquals(testAccessInputStat[0],_algo.getTheta_S_Mean(), 1e-6);
        assertEquals(testAccessInputStat[1],_algo.getTheta_S_Std(), 1e-6);
        assertEquals(testAccessInputStat[2],_algo.getTheta_V_Mean(), 1e-6);
        assertEquals(testAccessInputStat[3],_algo.getTheta_V_Std(), 1e-6);
        assertEquals(testAccessInputStat[4],_algo.getCos_Phi_Mean(), 1e-6);
        assertEquals(testAccessInputStat[5],_algo.getCos_Phi_Std(), 1e-6);
        assertEquals(testAccessInputStat[6],_algo.getR_Mean(), 1e-6);
        assertEquals(testAccessInputStat[7],_algo.getR_Std(), 1e-6);

        _outStatAux = new TocVegOutputStatisticsLoader();
        _outStatAux.load(CORRECT_FILE_OUTPUT_STAT);
        _algo.setOutputStatisticsAccess(_outStatAux);


        _algo.getfAPARConstants(testVal);
        assertEquals(testAccessOutputStat[0], testVal[0], 1e-6);
        assertEquals(testAccessOutputStat[1], testVal[1], 1e-6);
        assertEquals(testAccessOutputStat[2], testVal[2], 1e-6);
        assertEquals(testAccessOutputStat[3], testVal[3], 1e-6);

        _algo.getfCoverConstants(testVal);
        assertEquals(testAccessOutputStat[4], testVal[0], 1e-6);
        assertEquals(testAccessOutputStat[5], testVal[1], 1e-6);
        assertEquals(testAccessOutputStat[6], testVal[2], 1e-6);
        assertEquals(testAccessOutputStat[7], testVal[3], 1e-6);

        _algo.getLAIConstants(testVal);
        assertEquals(testAccessOutputStat[8], testVal[0], 1e-6);
        assertEquals(testAccessOutputStat[9], testVal[1], 1e-6);
        assertEquals(testAccessOutputStat[10], testVal[2], 1e-6);
        assertEquals(testAccessOutputStat[11], testVal[3], 1e-6);

        _algo.getLAIxCabConstants(testVal);
        assertEquals(testAccessOutputStat[12], testVal[0], 1e-6);
        assertEquals(testAccessOutputStat[13], testVal[1], 1e-6);
        assertEquals(testAccessOutputStat[14], testVal[2], 1e-6);
        assertEquals(testAccessOutputStat[15], testVal[3], 1e-6);
    }
}
