/*
 * $Id: TocVegAlgorithm.java,v 1.7 2006/04/12 10:10:35 meris Exp $
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

import com.bc.jnn.JnnNet;
import com.bc.jnn.Jnn;
import com.bc.jnn.JnnException;
import org.esa.beam.util.Guardian;
import org.esa.beam.processor.common.auxdata.*;
import org.esa.beam.processor.toc.utils.TocVegBaerPixel;
import org.esa.beam.processor.toc.utils.TocVegPixel;

import java.io.File;
import java.io.IOException;


public class TocVegAlgorithm {

    private TocVegBaerPixel _inputBAER;
    private VegNormFactorAccess _normFactorAccess;
    private VegInputStatisticsAccess _inputStatAccess;
    private VegOutputStatisticsAccess _outputStatAccess;
   private boolean _flagLaiOutOfRange;
    private boolean _flagfCoverOutOfRange;
    private boolean _flagLaixCabOutOfRange;
    private boolean _flagFAparOutOfRange;
    float[] _lai;
    float[] _fCover;
    float[] _fApar;
    float[] _laixCab;
    JnnNet _net;
    double[] _inputNN;


    /**
     * creates several variables
     */
    public TocVegAlgorithm() {
        // result tables of neural network
        _lai = new float[1];
        _fApar = new float[1];
        _fCover = new float[1];
        _laixCab = new float[1];

        // input table of neural network
        _inputNN = new double[14];
    }

    /**
     * inits uncertainties data and neural network
     */
    public void InitAlgo() {
    }


    /**
     * Sets the interface for the normalisation factor retrieval.
     *
     * @param access
     */
    public void setNormFactorAccess(VegNormFactorAccess access) {
        Guardian.assertNotNull("access", access);
        _normFactorAccess = access;
    }

    /**
     * get value of normalisation factor
     * @return double value of normalisation factor
     */
    public double getNormFactor(){
        return _normFactorAccess.getNormalisationFactor();
    }


    /**
     * Sets the interface for the input statistics retrieval.
     *
     * @param access
     */
    public void setInputStatisticsAccess(VegInputStatisticsAccess access) {
        Guardian.assertNotNull("access", access);
        _inputStatAccess = access;
    }


    /**
     * Gets Value of Theta_S_Mean from input Statistics
     * @return value
     */
    public double getTheta_S_Mean(){
        return _inputStatAccess.getTheta_S_Mean();
    }

    /**
     * Gets Value of Theta_S_Std from input Statistics
     * @return value
     */
    public double getTheta_S_Std(){
        return _inputStatAccess.getTheta_S_StdDev();
    }

       /**
     * Gets Value of Theta_V_Mean from input Statistics
     * @return value
     */
    public double getTheta_V_Mean(){
        return _inputStatAccess.getTheta_V_Mean();
    }

       /**
     * Gets Value of Theta_S_Mean from input Statistics
     * @return value
     */
    public double getTheta_V_Std(){
        return _inputStatAccess.getTheta_V_StdDev();
    }

       /**
     * Gets Value of cos_phi_Mean from input Statistics
     * @return value
     */
    public double getCos_Phi_Mean(){
        return _inputStatAccess.getCos_Phi_Mean();
    }

       /**
     * Gets Value of cos_phi_std from input Statistics
     * @return value
     */
    public double getCos_Phi_Std(){
        return _inputStatAccess.getCos_Phi_StdDev();
    }

       /**
     * Gets Value of R_Mean from input Statistics
     * @return value
     */
    public double getR_Mean(){
        return _inputStatAccess.getR_Mean();
    }

    /**
     * Gets Value of R_std from input Statistics
     * @return value
     */
    public double getR_Std(){
        return _inputStatAccess.getR_StdDev();
    }

    /**
     * Sets the interface for the output statistics retrieval.
     *
     * @param access
     */
    public void setOutputStatisticsAccess(VegOutputStatisticsAccess access) {
        Guardian.assertNotNull("access", access);
        _outputStatAccess = access;
    }

    /**
     * Gets Value of LAI constants from output Statistics
     */
    public void getLAIConstants(double[] results){
        _outputStatAccess.getLAIConstants(results);
    }

    /**
     * Gets Value of LAIxCab constants from output Statistics
     */
    public void getLAIxCabConstants(double[] results){
        _outputStatAccess.getLAIxCabConstants(results);
    }

    /**
     * Gets Value of fAPAR constants from output Statistics
     */
    public void getfAPARConstants(double[] results){
        _outputStatAccess.getFAPARConstants(results);
    }

    /**
     * Gets Value of fCover constants from output Statistics
     */
    public void getfCoverConstants(double[] results){
        _outputStatAccess.getFCoverConstants(results);
    }



     /**
     * Sets the path to the neural network auxiliary file.
     *
     * @param auxPath
     */
    public void setNnAuxPath(String auxPath) throws IOException, JnnException {
         _net = Jnn.readNna(new File(auxPath));
     }



    /**
     * Processes the veg algorithm for the input pixel.
     *
     * @param input  a BAER pixel
     * @param output the vegetation algorithm result
     */
    public void processPixel(TocVegBaerPixel input, TocVegPixel output) {
        double cosPhi;
        double outNorm[] = new double[4];
       double laiStat[] = new double[4];
        double fcoverStat[] = new double[4];
        double faparStat[] = new double[4];
        double laixcabStat[] = new double[4];
         float dFApar=0;
        int numInputNN;

        _inputBAER = input;
        numInputNN = 0;

        //Calculation of the relative azimuth angle image
        cosPhi = Math.cos(Math.toRadians(input.getBand_Saa() - input.getBand_Vaa()));

        //normalisation of the inputs
        //Sun Zenith
        _inputNN[numInputNN] = inputNormalisation(_inputBAER.getBand_Sza(),
                                                  getTheta_S_Mean(),
                                                  getTheta_S_Std());
        numInputNN++;
        //View Zenith
        _inputNN[numInputNN] = inputNormalisation(_inputBAER.getBand_Vza(),
                                                  getTheta_V_Mean(),
                                                  getTheta_V_Std());
        numInputNN++;

        //CosPhi
        _inputNN[numInputNN] = inputNormalisation(cosPhi,
                                                  getCos_Phi_Mean(),
                                                  getCos_Phi_Std());
        numInputNN++;

        for (int b = 0; b < 11; b++) {
            _inputNN[numInputNN] = inputNormalisation(getBand(b),
                                                      getR_Mean(),
                                                      getR_Std());
            numInputNN++;
        }

        //run of the neural network for the output variables
        outNorm = neuralNetwork(_inputNN, _net);

        //denormalisation of the outputs
        getLAIConstants(laiStat);
        getfCoverConstants(fcoverStat);
        getfAPARConstants(faparStat);
        getLAIxCabConstants(laixcabStat);
        _flagFAparOutOfRange = outputDenormalisation(outNorm[0],
                                                       faparStat[1],
                                                       faparStat[0],
                                                       faparStat[2],
                                                       faparStat[3],
                                                       _fApar);
        _flagfCoverOutOfRange = outputDenormalisation(outNorm[1],
                                                      fcoverStat[1],
                                                      fcoverStat[0],
                                                      fcoverStat[2],
                                                      fcoverStat[3],
                                                      _fCover);
        _flagLaiOutOfRange = outputDenormalisation(outNorm[2],
                                                     laiStat[1],
                                                     laiStat[0],
                                                     laiStat[2],
                                                     laiStat[3],
                                                     _lai);

        _flagLaixCabOutOfRange = outputDenormalisation(outNorm[3],
                                                       laixcabStat[1],
                                                       laixcabStat[0],
                                                       laixcabStat[2],
                                                       laixcabStat[3],
                                                       _laixCab);


         //fAPAR mismatch
         //fAPAR mismatch
        dFApar = fAparMismatch(_inputBAER.getBand_TOAVEG(), _fApar[0]);


        // create output
        output.setBand_fCover(_fCover[0]);
        output.setBand_LAI(_lai[0]);
        output.setBand_CabxLAI(_laixCab[0]);
        output.setBand_fAPAR(_fApar[0]);
        output.setBand_delta_fAPAR(dFApar);
        if (_flagLaiOutOfRange) {
            output.setLAIOutOfRangeFlag();
        }
        if (_flagfCoverOutOfRange) {
            output.setFCoverOutOfRangeFlag();
        }
        if (_flagLaixCabOutOfRange) {
            output.setLAIxCabOutOfRangeFlag();
        }
        if (_flagFAparOutOfRange) {
            output.setFaparOutOfRangeFlag();
        }

    }

    ////////////////////////////////////////////////////////////////////////////
    ////////   END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////


    /**
     * get value a of BAER band
     *
     * @param index : index of the band
     *
     * @return value of band
     */
    private double getBand(int index){
        return _inputBAER.getBand(index);
    }

    /**
     * Scales the input values of the neural network.
     *
     * @param val     variable before normalisation
     * @param valMean mean of value of val of the training database
     * @param valStd  standard deviation of val of the training database
     *
     * @return normalised variable
     */
    private double inputNormalisation(double val, double valMean, double valStd) {
        double valNorm;
        valNorm = getNormFactor() *
                  (val - valMean) /
                  valStd;
        return valNorm;
    }

    /**
     * runs the neural network using nn-library
     *
     * @param netInput input values table
     * @param net      neural network to be applied to input values
     *
     * @return result of the neural network
     */
    private double[] neuralNetwork(double[] netInput, JnnNet net) {
        double[] netOutput = new double[4];
        net.process(netInput, netOutput);
        return netOutput;
    }

    /**
     * Transfers the output NN value in actual range
     *
     * @param valNorm output NN value
     * @param valStd  standard deviation value of val of the trained database
     * @param valMean mean value of val of the trained database
     * @param valMin  minimum value of val of training database
     * @param valMax  maximum value of val of the training database
     * @param val     the value result
     *
     * @return value of flag
     */
    private boolean outputDenormalisation(double valNorm, double valStd,
                                          double valMean, double valMin,
                                          double valMax, float[] val) {

        boolean flag = false;
        double tmp = valNorm * valStd;
        tmp = tmp / getNormFactor();
        val[0] = (float) (tmp + valMean);

        if ((val[0] < valMin) || (val[0] > valMax)) {
            flag = true;
        }
        if (val[0] < 0.0) {
            val[0] = 0.f;
        }
        return flag;
    }



    /**
     * computes the quality indicator
     *
     * @param mgvi  Meris Global Vegetation Index
     * @param fapar Estimated fAPAR value
     *
     * @return the fAPAR mismatch
     */
    private float fAparMismatch(float mgvi, float fapar) {
        return (mgvi - fapar);
    }
}
