/*
 * $Id: ToaVegAlgorithm.java,v 1.12 2006/04/12 10:08:14 meris Exp $
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


import org.esa.beam.util.Guardian;
import org.esa.beam.util.math.RsMathUtils;
import org.esa.beam.processor.toa.ToaVegConstants;
import org.esa.beam.processor.toa.auxdata.ToaVegInputStatisticsAccess;
import org.esa.beam.processor.common.auxdata.VegUncertaintyModelAccess;
import org.esa.beam.processor.common.auxdata.VegOutputStatisticsAccess;
import org.esa.beam.processor.common.utils.VegGenericPixel;
import org.esa.beam.processor.toa.utils.ToaVegMerisPixel;

import java.io.File;
import java.io.IOException;

import com.bc.jnn.JnnNet;
import com.bc.jnn.Jnn;
import com.bc.jnn.JnnException;


public class ToaVegAlgorithm {

    private ToaVegInputStatisticsAccess _inputStatAccess;
    private VegOutputStatisticsAccess _outputStatAccess;
    private VegUncertaintyModelAccess _uncertaintyAccess;
    private boolean _flagLaiOutOfRange;
    private boolean _flagfCoverOutOfRange;
    private boolean _flagLaixCabOutOfRange;
    private boolean _flagFAparOutOfRange;
    JnnNet _netLAI;
    JnnNet _netFCover;
    JnnNet _netLaixCab;
    JnnNet _netFApar;
    float[] _lai;
    float[] _fCover;
    float[] _fApar;
    float[] _laixCab;
    double[] _inputNN;
    double[] _LAICoeff;
    double[] _fCoverCoeff;
    double[] _laixCabCoeff;
    double[] _fAparCoeff;
    float _sigma_lai;
    float _sigma_fapar;
    float _sigma_fcover;
    float _sigma_laixcab;

    /**
     * creates several variables
     */
    public ToaVegAlgorithm() {
        //uncertainty tables
        _LAICoeff = new double[3];
        _fCoverCoeff = new double[3];
        _fAparCoeff = new double[3];
        _laixCabCoeff = new double[3];

        // result tables of neural network
        _lai = new float[1];
        _fApar = new float[1];
        _fCover = new float[1];
        _laixCab = new float[1];

        // input table of neural network
        _inputNN = new double[16];
    }

    /**
     * inits uncertainties data and neural network
     */
    public void InitAlgo() {
        _uncertaintyAccess.getLAICoefficients(_LAICoeff);
        _uncertaintyAccess.getfAPARCoefficients(_fAparCoeff);
        _uncertaintyAccess.getfCoverCoefficients(_fCoverCoeff);
        _uncertaintyAccess.getLAIxCabCoefficients(_laixCabCoeff);

    }

      /**
     * Gets Value of uncertainty lai data
     * @param index : equal to 0,1 or 2
     * @return value of lai uncertainty coefficient at index
     */
   public double getLAICoef(int index){
        if ((index >= 0) && (index <=2))
            return _LAICoeff[index];
        return -1;
    }

   /**
     * Gets Value of uncertainty fAPAR data
     * @param index : equal to 0,1 or 2
     * @return value of fAPAR uncertainty coefficient at index
     */
   public double getfAPARCoef(int index){
        if ((index >= 0) && (index <=2))
            return _fAparCoeff[index];
        return -1;
    }

    /**
     * Gets Value of uncertainty fCOVER data
     * @param index : equal to 0,1 or 2
     * @return value of fCOVER uncertainty coefficient at index
     */
    public double getfCoverCoef(int index){
        if ((index >= 0) && (index <=2))
            return _fCoverCoeff[index];
        return -1;
    }

    /**
     * Gets Value of uncertainty laixCab data
     * @param index : equal to 0,1 or 2
     * @return value of laixcab uncertainty coefficient at index
     */
    public double getLAIxCabCoef(int index){
        if ((index >= 0) && (index <=2))
            return _laixCabCoeff[index];
        return -1;
    }

    /**
     * Sets the interface for the input statistics retrieval.
     *
     * @param access
     */
    public void setInputStatisticsAccess(ToaVegInputStatisticsAccess access) {
        Guardian.assertNotNull("access", access);
        _inputStatAccess = access;
    }

      /**
     * Sets the interface for the uncertainty model auy file.
     *
     * @param access
     */
    public void setUncertaintyModelAccess(VegUncertaintyModelAccess access) {
        Guardian.assertNotNull("access", access);
        _uncertaintyAccess = access;
    }

    /**
     * Gets Value of Theta_S_Mean from input Statistics
     * @return value
     */
    public double getTheta_S_Min(){
        return _inputStatAccess.getTheta_S_Min();
    }

    /**
     * Gets Value of Theta_S_Std from input Statistics
     * @return value
     */
    public double getTheta_S_Max(){
        return _inputStatAccess.getTheta_S_Max();
    }

       /**
     * Gets Value of Theta_V_Mean from input Statistics
     * @return value
     */
    public double getTheta_V_Min(){
        return _inputStatAccess.getTheta_V_Min();
    }

       /**
     * Gets Value of Theta_S_Mean from input Statistics
     * @return value
     */
    public double getTheta_V_Max(){
        return _inputStatAccess.getTheta_V_Max();
    }

       /**
     * Gets Value of cos_phi_Mean from input Statistics
     * @return value
     */
    public double getCos_Phi_Min(){
        return _inputStatAccess.getCos_Phi_Min();
    }

       /**
     * Gets Value of cos_phi_std from input Statistics
     * @return value
     */
    public double getCos_Phi_Max(){
        return _inputStatAccess.getCos_Phi_Max();
    }


     /**
     * Gets Value of R_Mean from input Statistics
     * @return value
     */
    public double getR_Min(int band){
        return _inputStatAccess.getR_Min(band);
    }

    /**
     * Gets Value of R_std from input Statistics
     * @return value
     */
    public double getR_Max(int band){
        return _inputStatAccess.getR_Max(band);
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
        _outputStatAccess.getLAIxCabConstantsToa(results);
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
     * Sets the path to the LAI neural network auxiliary file.
     *
     * @param auxPath
     */
    public void setNn_LaiAuxPath(String auxPath) throws IOException, JnnException {
         _netLAI = Jnn.readNna(new File(auxPath));
     }

    /**
     * Sets the path to the fCover neural network auxiliary file.
     *
     * @param auxPath
     */
    public void setNn_fCoverAuxPath(String auxPath) throws IOException, JnnException {
        _netFCover = Jnn.readNna(new File(auxPath));
    }

    /**
     * Sets the path to the fAPAR neural network auxiliary file.
     *
     * @param auxPath
     */
    public void setNn_fAPARAuxPath(String auxPath) throws IOException, JnnException {
         _netFApar = Jnn.readNna(new File(auxPath));
    }

    /**
     * Sets the path to the LAIxCab neural network auxiliary file.
     *
     * @param auxPath
     */
    public void setNn_LAIxCabAuxPath(String auxPath) throws IOException, JnnException {
        _netLaixCab = Jnn.readNna(new File(auxPath));
    }



    /**
     * Processes the veg algorithm for the input pixel.
     *
     * @param input  a TOA_VEG pixel
     * @param output the vegetation algorithm result
     */
    public void processPixel(ToaVegMerisPixel input, VegGenericPixel output) {
        double cosPhi;
        double laiNorm;
        double fAparNorm;
        double fCoverNorm;
        double laixCabNorm;
        double laiStat[] = new double[2];
        double fcoverStat[] = new double[2];
        double faparStat[] = new double[2];
        double laixcabStat[] = new double[2];
        int numInputNN;
        float reflec;
        float solarSpecFlux;
        numInputNN = 0;

        //Calculation of the relative azimuth angle image
        cosPhi = Math.cos(Math.toRadians(input.getBand_Saa() - input.getBand_Vaa()));

        //normalisation of the inputs
         //View Zenith
        _inputNN[numInputNN] = inputNormalisation(Math.toRadians(input.getBand_Vza()),
                                                  getTheta_V_Min(),
                                                  getTheta_V_Max());
        numInputNN++;

        //Sun Zenith
        _inputNN[numInputNN] = inputNormalisation(Math.toRadians(input.getBand_Sza()),
                                                    getTheta_S_Min(),
                                                    getTheta_S_Max());
          numInputNN++;

        //CosPhi
        _inputNN[numInputNN] = inputNormalisation(cosPhi,
                                                  getCos_Phi_Min(),
                                                  getCos_Phi_Max());
         numInputNN++;



         for (int b = 0; b < ToaVegConstants.NUM_BANDS; b++) {
            solarSpecFlux = input.getBand_SolarSpecFlux(b);
            reflec = RsMathUtils.radianceToReflectance(input.getBand(b), input.getBand_Sza(), solarSpecFlux);
            _inputNN[numInputNN] = inputNormalisation(reflec,
                                                      getR_Min(b),
                                                      getR_Max(b));
            numInputNN++;

        }



        //run of the neural network for the output variables
        laiNorm = neuralNetwork(_inputNN, _netLAI);
        fCoverNorm = neuralNetwork(_inputNN, _netFCover);
        laixCabNorm = neuralNetwork(_inputNN, _netLaixCab);
        fAparNorm = neuralNetwork(_inputNN, _netFApar);

        //denormalisation of the outputs
        getLAIConstants(laiStat);
        getfCoverConstants(fcoverStat);
        getfAPARConstants(faparStat);
        getLAIxCabConstants(laixcabStat);


        // using denormalisation version 2 for LAI, FCOVER, FAPAR
        // using denormalisation version 1 for LAIxCab
        _flagLaiOutOfRange = outputDenormalisation(laiNorm,
                                                    laiStat[0],
                                                    laiStat[1],
                                                    _lai);
        _flagFAparOutOfRange = outputDenormalisation(fAparNorm,
                                                    faparStat[0],
                                                    faparStat[1],
                                                    _fApar);
        _flagfCoverOutOfRange =outputDenormalisation(fCoverNorm,
                                                    fcoverStat[0],
                                                    fcoverStat[1],
                                                    _fCover);
        _flagLaixCabOutOfRange = outputDenormalisation(laixCabNorm,
                                                        laixcabStat[0],
                                                        laixcabStat[1],
                                                        _laixCab);


        //uncertainties
        _sigma_lai = uncertainty_estimation(_lai[0], _flagLaiOutOfRange, _LAICoeff);
        _sigma_fapar = uncertainty_estimation(_fApar[0],_flagFAparOutOfRange,_fAparCoeff);
        _sigma_fcover = uncertainty_estimation(_fCover[0], _flagfCoverOutOfRange, _fCoverCoeff);
        _sigma_laixcab = uncertainty_estimation(_laixCab[0], _flagLaixCabOutOfRange, _laixCabCoeff);

        // create output
        output.setBand_fCover(_fCover[0]);
        output.setBand_LAI(_lai[0]);
        output.setBand_CabxLAI(_laixCab[0]);
        output.setBand_fAPAR(_fApar[0]);
        output.setBand_sigma_LAI(_sigma_lai);
        output.setBand_sigma_LAIxCab(_sigma_laixcab);
        output.setBand_sigma_fApar(_sigma_fapar);
        output.setBand_sigma_fCover(_sigma_fcover);


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
     * Scales the input values of the neural network.
     *
     * @param val     variable before normalisation
     * @param valMin min value of val of the training database
     * @param valMax  max value of val of the training database
     *
     * @return sed variable
     */
    private static double inputNormalisation(double val, double valMin, double valMax) {
        double valNorm;
         valNorm = ((2 * (val - valMin))/(valMax - valMin)) - 1 ;
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
    private static double neuralNetwork(double[] netInput, JnnNet net) {
        double[] netOutput = new double[1];
        net.process(netInput, netOutput);
        return netOutput[0];
    }

    /**
     * Transfers the output NN value in actual range
     *
     * @param valNorm output NN value
     * @param valMin  minimum value of val of training database
     * @param valMax  maximum value of val of the training database
     * @param val     the value result
     *
     * @return value of flag
     */
     private static boolean outputDenormalisation(double valNorm,
                                          double valMin,
                                          double valMax, float[] val) {
        boolean flag = false;
        double tmp = 0.5 * (valNorm + 1) * (valMax - valMin) + valMin;

        val[0] = (float) (tmp);


        if ((val[0] < valMin) || (val[0] > valMax)) {
            flag = true;
        }

        if (val[0] < 0.0) {
            val[0] = 0.f;
        }

        return flag;


     }

    private static float uncertainty_estimation(float val, boolean flag, double[] coeff){
        float sigmaV= 0;
        if (!flag){
            sigmaV  = (float) (coeff[0]
                        + coeff[1] * val
                        + coeff[2] * Math.pow(val,2));
        }
        return sigmaV;
    }






}
