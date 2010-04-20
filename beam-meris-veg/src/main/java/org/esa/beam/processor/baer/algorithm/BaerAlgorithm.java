/*
 * $Id: BaerAlgorithm.java,v 1.10 2006/03/29 14:30:31 meris Exp $
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
package org.esa.beam.processor.baer.algorithm;

import org.esa.beam.framework.processor.ProcessorException;
import org.esa.beam.processor.baer.BaerConstants;
import org.esa.beam.processor.baer.auxdata.*;
import org.esa.beam.processor.baer.utils.AerPixel;
import org.esa.beam.processor.baer.utils.MerisPixel;
import org.esa.beam.util.Guardian;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


public class BaerAlgorithm {



    private Logger _logger;
    private org.esa.beam.processor.baer.utils.MerisPixel _inputMERIS;
    private AerPhaseAccess _aerPhaseAccess;
    private RelAerPhaseAccess _relPhaseAccess;
    private NdviAccess _ndviAccess;
    private GroundReflectanceAccess _groundReflectanceAccess;
    private SoilFractionAccess _soilFractionAccess;
    private F_TuningAccess _f_tuningAccess;
    private AerDiffTransmAccess _aerDiffTransmAccess;
    private HemisphReflecAccess _hemisphReflecAccess;
    private double[] _inputLocal;
    private double[] _surfRefl;
    private double[] _aotguess;
    private double[] _aot;
    private double[] _aeroRefl;
    private double[][] _aertable;
    private double[] _weight;
    private double[] _aotTempxx;
    private double[] _aotTempx;
    private double[] _aotMin;
    private double[] _lnLambda;
    private double[] _lnAot;
    private double[] _relPhaseCoef;

    private double _muSun;
    private double _muView;
    private double _pressure_div;
    private double _caerView;
    private double _caerSun;

    private double _p140;
    private double _pTheta;
    private float _band_vza;
    private float _band_sza;
    private float _band_saa;
    private float _band_vaa;
    private double _alpha;
    private double _alphaTempY;
    private double _beta;
    private double _rmsd;
    private double _alphaHelp;
    private int _vegSpectraNumber;
    private int _soilSpectraNumber;
    private int[] _flagAotOutOfRange;
    private int _flagAlphaOutOfRange;
    private String _atm_corr_method;
  //  private boolean _baer_process;
    private boolean _atm_cor_process;
    private boolean _hasFlagAOT;

    private Spectrum[] _reflVeg;
    private Spectrum[] _reflSoil;
    private double[] _caerCoef;
    private double[] _rhemCoef;

    private int _icx;
    private int _icheck;

    private boolean _nulloutput;

    // SMAC variables
    SmacCoefficientsManager _coeffMgr;
    String _smacAerosolType;
    String _smacProductType;
    private double[] _SmacA0taup;
    private double[] _SmacA1taup;
    private double[] _Smaca0P;
    private double[] _Smaca1P;
    private double[] _Smaca2P;
    private double[] _Smaca3P;
    private double[] _Smaca4P;
    private double[] _Smaca0T;
    private double[] _Smaca1T;
    private double[] _Smaca2T;
    private double[] _Smaca3T;
    private double[] _Smaca0s;
    private double[] _Smaca1s;
    private double[] _Smaca2s;
    private double[] _Smaca3s;
    private double[] _Smacresa1;
    private double[] _Smacresa2;
    private double[] _Smacresa3;
    private double[] _Smacresa4;
    private double[] _Smacrest1;
    private double[] _Smacrest2;
    private double[] _Smacrest3;
    private double[] _Smacrest4;
    private double[] _Smacwo;
    private double[] _Smaconemwo;
    private double[] _Smacgc;
    private double[] _Smacak, _Smacak2, _Smacpfac;
    private double[] _Smacb, _Smaconepb, _Smaconemb, _Smaconepb2, _Smaconemb2;
    private double[] _Smacww;

    private static final double _SmaconeQuarter = 1.0 / 4.0;
    private static final double _SmactwoThird = 2.0 / 3.0;

    /**
     * Constructs the object with default parameters
     */
    public BaerAlgorithm() {
        _logger = Logger.getLogger(BaerConstants.LOGGER_NAME);

        _inputLocal = new double[BaerConstants.NUM_BANDS];
        _aeroRefl = new double[BaerConstants.NUM_BANDS];
        _weight = new double[BaerConstants.NUM_BANDS];
        _aot = new double[BaerConstants.NUM_BANDS];
        _flagAotOutOfRange = new int[BaerConstants.NUM_BANDS];
        _aertable=new double[BaerConstants.NUM_BANDS][3];
        _surfRefl = new double[BaerConstants.NUM_BANDS];
        _aotguess = new double[BaerConstants.NUM_BANDS];
        _aotTempxx = new double[BaerConstants.NUM_BANDS];
        _aotTempx = new double[BaerConstants.NUM_BANDS];
        _aotMin = new double[BaerConstants.NUM_BANDS];
        _lnLambda = new double[BaerConstants.NUM_BANDS];
        _lnAot = new double[BaerConstants.NUM_BANDS];

        _atm_corr_method="SMAC";
     //   _baer_process = false;
        _atm_cor_process = false;
        _vegSpectraNumber=1;
        _soilSpectraNumber=3;

        _reflVeg = new Spectrum[2];
        _reflSoil = new Spectrum[4];
    }

    public void setSoilSpectraNumber(int spectranumber)
    {
          _soilSpectraNumber= spectranumber;
    }

    /**
     * Sets the interface to retrieve the aerosol phase coefficients from
     * @param aerPhase
     */
    public void setAerPhaseAccess(AerPhaseAccess aerPhase) {
        Guardian.assertNotNull("aerPhase", aerPhase);
        _aerPhaseAccess = aerPhase;
    }

    /**
     * Sets the interface to retrieve the relative aerosol phase coefficients from
     * @param relPhase
     */
    public void setRelAerPhaseAccess(RelAerPhaseAccess relPhase) {
        Guardian.assertNotNull("relPhase", relPhase);
        _relPhaseAccess = relPhase;
    }

    /**
     * Sets the interface to retrieve the ndvi tuning factor from
     * @param ndviAccess
     */
    public void setNdviAccess(NdviAccess ndviAccess) {
        Guardian.assertNotNull("ndviAccess", ndviAccess);
        _ndviAccess = ndviAccess;
    }

    /**
     * Sets the interface to retrieve the ground reflectances factor from
     * @param groundReflecAccess
     */
    public void setGroundReflectanceAccess(GroundReflectanceAccess groundReflecAccess) {
        Guardian.assertNotNull("groundReflecAccess", groundReflecAccess);
        _groundReflectanceAccess = groundReflecAccess;
    }

    /**
     * Sets the interface to retrieve the soil fraction factor from
     * @param soilFractionAccess
     */
    public void setSoilFractionAccess(SoilFractionAccess soilFractionAccess) {
        Guardian.assertNotNull("soilFractionAccess", soilFractionAccess);
        _soilFractionAccess = soilFractionAccess;
    }

    /**
     * Sets the interface to retrieve the F tuning factor from
     * @param f_tuningAccess
     */
    public void setF_TuningAccess(F_TuningAccess f_tuningAccess) {
        Guardian.assertNotNull("f_tuningAccess", f_tuningAccess);
        _f_tuningAccess = f_tuningAccess;
    }

    /**
     * Sets the interface to retrieve the aerosol diffuse transmission coefficients.
     * @param aerDiffTransmAccess
     */
    public void setAerDiffTransmAccess(AerDiffTransmAccess aerDiffTransmAccess) {
        Guardian.assertNotNull("aerDiffTransmAccess", aerDiffTransmAccess);
        _aerDiffTransmAccess = aerDiffTransmAccess;
    }


    /**
     * Sets the interface to retrieve the hemispherical reflectance coefficients.
     * @param hemisphReflecAccess
     */
    public void setHemisphReflecAccess(HemisphReflecAccess hemisphReflecAccess) {
        Guardian.assertNotNull("hemisphReflecAccess", hemisphReflecAccess);
        _hemisphReflecAccess = hemisphReflecAccess;
    }

    public void setSmacCoeffManager(SmacCoefficientsManager mng) {
        _coeffMgr = mng;
    }

    public void setAerosolType(String type){
        _smacAerosolType = type;
    }

    public void setProductType(String prod) {
        _smacProductType = prod;
    }

    public void setProcessFormat(String format) {
         _atm_corr_method = format;

    }

    public void setBaerProcessFormat(boolean format) {
 //        _baer_process = format;

    }
    public void setAtmCorProcessFormat(boolean format) {
         _atm_cor_process = format;

    }


    /**
     * initialises org.esa.beam.processor.baer algorithm variables
     *
     */
    public void initAlgo() {
        int b;

       _rhemCoef = _hemisphReflecAccess.getHemisphReflecCoefficients();
       _caerCoef = _aerDiffTransmAccess.getAerDiffTransmCoefficients();

       try {
              _reflVeg[0] =  _groundReflectanceAccess.getSpectrum("LACE+MAPLE");
       } catch (ProcessorException e) {
       }
       try {
              _reflVeg[1] =  _groundReflectanceAccess.getSpectrum("CAMELEO");
       } catch (ProcessorException e) {
       }

       try {
              _reflSoil[0] = _groundReflectanceAccess.getSpectrum("CASI_1");
       } catch (ProcessorException e) {
       }
       try {
              _reflSoil[1] = _groundReflectanceAccess.getSpectrum("CASI_2");
       } catch (ProcessorException e) {
       }
       try {
              _reflSoil[2] = _groundReflectanceAccess.getSpectrum("KARNIELI");
       } catch (ProcessorException e) {
       }
        try {
               _reflSoil[3] = _groundReflectanceAccess.getSpectrum("KARNIELI2");
        } catch (ProcessorException e) {
        }

        for (b=0; b<BaerConstants.NUM_BANDS; b++)
        {
            _lnLambda[b] = Math.log(BaerConstants.MERIS_BANDS[b]);
            _aertable[b] = (_aerPhaseAccess.getAerPhase(b+1)).getA();
        }
        _relPhaseCoef = _relPhaseAccess.getRelativeAerosolPhaseCoefficients();



        if (_atm_corr_method.equals("SMAC")){
                  initSmac();
        }

    }

    /**
     * Processes the aerosol algorithm for the input pixel
     * @param input a Meris L2 Pixel
     * @param output an aerosol corrected pixel
     * @return the output pixel
     */
    public AerPixel processPixel(MerisPixel input, AerPixel output) {
          output = BaerProcess(input,output);
           return output;
    }



    ////////////////////////////////////////////////////////////////////////////
    ////////   END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////

    /** Process the Baer algoritm
     *
     */
   private AerPixel BaerProcess(MerisPixel input, AerPixel output)
    {
        int b=0;
        int jcount=0;
        int numconst=0;
        int numconstT=0;
        int nbIterations=0;
        double rmsd0;
        double rmsdMin = 0.0;
        double rmsdMinT=0.0;
        double aotGuess1;
        double theta;
        boolean useAotMin = false;

        double alphaMin = 0.0;
        int countNbBandsFit=0;

        //initialisation
        _inputMERIS = input;
        for (int boucle=0;boucle<10;boucle++)
            _inputLocal[boucle] = _inputMERIS.getBand(boucle);
        _inputLocal[11] = _inputMERIS.getBand(10);
        _inputLocal[12] = _inputMERIS.getBand(11);
        _inputLocal[13] = _inputMERIS.getBand(12);


        _band_vza = _inputMERIS.getBand_Vza();
        _band_vaa = _inputMERIS.getBand_Vaa();
        _band_sza = _inputMERIS.getBand_Sza();
        _band_saa = _inputMERIS.getBand_Saa();

       _pressure_div = BaerConstants.PRESSURE_SEA / _inputMERIS.getPressure();
         _muSun = _pressure_div * Math.cos(Math.toRadians(_band_sza));
        _muView = _pressure_div * Math.cos(Math.toRadians(_band_vza));

        _caerView = _caerCoef[0]
                   + _caerCoef[1] * _muView
                   + _caerCoef[2] * _muView * _muView
                   + _caerCoef[3] * _muView * _muView * _muView
                   + _caerCoef[4] * _muView * _muView * _muView * _muView;

        _caerSun = _caerCoef[0]
                  + _caerCoef[1] * _muSun
                  + _caerCoef[2] * _muSun * _muSun
                  + _caerCoef[3] * _muSun * _muSun * _muSun
                  + _caerCoef[4] * _muSun * _muSun * _muSun * _muSun;



        _alphaTempY=1.0; //0.0;
        _rmsd=0.0;
        _beta = 0.0;
        _alpha=0.0;

         // Computing of the scattering angle for a sun and view geometry
        theta = _inputMERIS.getScatteringAngle();


         //calculation of the relative aerosol phse at 140? and the current scattering angle
        _p140 = _relPhaseCoef[0] * 19600.0    //Math.pow(140.0,2)
               + _relPhaseCoef[1] * 140.0
               + _relPhaseCoef[2];

        _pTheta = _relPhaseCoef[0] * Math.toDegrees(theta) * Math.toDegrees(theta)
                + _relPhaseCoef[1] * Math.toDegrees(theta)
                + _relPhaseCoef[2];
         /**
         * initialisation of the apparent surface reflectances
         * and calculation of a rough estimation of the AOT at 412 nm
         */
        aotGuess1 = initSurfReflectance();


         /**
         * Calculation of a rough estimation of the AOTs
        */

        for (b=0; b<BaerConstants.NUM_BANDS; b++){
              _aotguess[b] = angstroemPowerLaw(aotGuess1, BaerConstants.MERIS_BANDS[b], BaerConstants.MERIS_BANDS[0], -_alphaHelp);
         }


       whileLoop:while (true) {

            rmsd0 = _rmsd;

            // Calculation of the AOTs ans Aero_Refl
            _hasFlagAOT=false;
            numconst= 0;
            _icheck = 0;
             for (b=0; b<BaerConstants.NUM_BANDS; b++){
                    numconst = aotLand(b,numconst);
                    if (numconst > 1){
                        numconstT++;
                    }
            }
             if ((numconst >= 1) && (_icheck == 1)) {
                angstroemSimple(_aotTempx);
               if (_icx > 1)
                    _aotTempx[_icx] = _aotTempx[0] * Math.pow((BaerConstants.MERIS_BANDS[_icx]/ BaerConstants.MERIS_BANDS[0]), _alphaTempY);
               angstroemSimple(_aotTempx);
               if (_icx > 1)
                    _aotTempx[_icx] = _aotTempx[0] * Math.pow((BaerConstants.MERIS_BANDS[_icx]/ BaerConstants.MERIS_BANDS[0]), _alphaTempY);
           }


            // calculation of the alpha and beta
            angstroem();


            // calculation of RMSD
            countNbBandsFit = rmsdCalculation();

            if (rmsd0 > 0.0) {
                if (_rmsd <= rmsd0) {
                    rmsdMin = _rmsd;
                    for (b=0; b< BaerConstants.NUM_BANDS; b++) {
                        _aotMin[b] = _aotTempx[b];
                    }

                    angstroem();
                    alphaMin = _alphaTempY;
                    rmsdMinT = 1.2 * rmsdMin;
                }
                //nouveau v15
                else { //_rmsd > rmsd0
                    _hasFlagAOT = false;
                    for (b=0; b<BaerConstants.NUM_BANDS; b++){
                    if (BaerConstants.FIT_SELECTED_MERIS_BAND[b]){
                        if (_flagAotOutOfRange[b]!=0)
                        {
                            _hasFlagAOT = true;
                            break;
                        }
                        }
                    }



                    if ((!_hasFlagAOT) &&
                         (rmsdMin > 0.0) &&
                         (_rmsd > rmsdMinT)) {
                          for (b=0; b<BaerConstants.NUM_BANDS; b++){
                              _aotTempx[b] = _aotMin[b];
                          }
                        _alpha = alphaMin;

                        //nouveau v15
                        angstroem();
                        if ((alphaMin > 2.2) && (alphaMin > _alphaTempY))
                            _alpha = _alphaTempY;
                        if ((alphaMin < -0.5) && (alphaMin < _alphaTempY))
                            _alpha = _alphaTempY;

                        useAotMin = true;
                        break whileLoop;  //exit loop
                    }
                }
            }
           if (_hasFlagAOT){

               // pixel output invalid
               break whileLoop;
           }

            if (!_hasFlagAOT){
              if ((countNbBandsFit >= 2) && (_rmsd <= 0.005 )) { // v14 0.001)) {
                switch (_flagAlphaOutOfRange) {
                    case 0:
                        angstroem();
                        if ((_alphaTempY >= -0.5 ) && (_alphaTempY <= 2.2))
                            _alpha = _alphaTempY;

                        break whileLoop;
                    case 1:
                        jcount ++;
                        break;
                    default:
                        break;
                };
              }
            }

          //estimation of the surface reflectance
            jcount = iterativeSurfaceRefl( jcount);

            if(jcount > 150){
                break whileLoop;
            }

            if (numconstT > BaerConstants.CONST_MAX) {
                 break whileLoop;
            }

             if (_rmsd > BaerConstants.RMSD_CONST) {
               nbIterations++;
               if (nbIterations >= BaerConstants.ITERATION_MAX) {
                   if (!_hasFlagAOT)
                 {
                    break whileLoop;
                 }
                }

                continue whileLoop;
            }

            if (_flagAlphaOutOfRange == 1) {
               continue whileLoop;
            }

            createAotAndAlpha();
            break whileLoop;
        } // End WhileLoop

        if (_hasFlagAOT){
            if (_atm_cor_process)
            {
                   createOutput(output);
            }
            else
                   createOutputBaer(output);
        }
        else
        {

            if (_atm_cor_process)
            {
                atmCorProcess(useAotMin);
                createOutput(output);
            }
            else
            {
                createOutputBaer(output);
            }
        }
        return output;
    }


    /**
     * Saves output values
     * @param output : result of org.esa.beam.processor.baer processing
     */
    private void createOutput(AerPixel output){

        double aot_550;
        double aot_440;

        _nulloutput=false;
         if (_flagAlphaOutOfRange != 0){
            output.setAlphaOutOfRangeFlag();
             output.setInvalidOutputFlag();
             setNullOutput();
        }
          if (_hasFlagAOT){
            output.setAotOutOfRangeFlag();
            output.setInvalidOutputFlag();
            setNullOutput();
        }



        //we write the surfRefl value for band 1-10, 12, 13, 14
        if (_surfRefl[0] > 1.0 || _surfRefl[1] > 1.0 ||
                _surfRefl[2] > 1.0 || _surfRefl[3] > 1.0 ||
                _surfRefl[4] > 1.0 || _surfRefl[5] > 1.0 ||
                _surfRefl[6] > 1.0 || _surfRefl[7] > 1.0 ||
                _surfRefl[8] > 1.0 || _surfRefl[9] > 1.0 ||
                _surfRefl[11] > 1.0 || _surfRefl[12] > 1.0 ||
                _surfRefl[13] > 1.0) {
            output.setInvalidOutputFlag();
            setNullOutput();
        }

        if (_surfRefl[0] < 0.0 || _surfRefl[1] < 0.0 ||
                _surfRefl[2] < 0.0 || _surfRefl[3] < 0.0 ||
                _surfRefl[4] < 0.0 || _surfRefl[5] < 0.0 ||
                _surfRefl[6] < 0.0 || _surfRefl[7] < 0.0 ||
                _surfRefl[8] < 0.0 || _surfRefl[9] < 0.0 ||
                _surfRefl[11] < 0.0 || _surfRefl[12] < 0.0 ||
                _surfRefl[13] < 0.0) {
            output.setInvalidOutputFlag();
            setNullOutput();
        }
        for (int b=0; b<10; b++)
                 output.setBand((float)_surfRefl[b],b);       // band 1-10
        for (int b=11; b<14; b++)
                 output.setBand((float)_surfRefl[b],b-1);   // band 12-14

         if (_nulloutput)
         {
             aot_440=0.0;
             aot_550=0.0;
         }
        else
         {
            aot_440=angstroemPowerLaw(_aot[0], 0.440, BaerConstants.MERIS_BANDS[0], -_alpha);
            aot_550=angstroemPowerLaw(_aot[0], 0.550,BaerConstants.MERIS_BANDS[0], -_alpha);
         }

        output.setAlpha((float)_alpha);
        output.setAot_412((float)_aot[0]);
        output.setAot_440((float)aot_440);
        output.setAot_550((float)aot_550);



    }


       /**
     * Saves output values
     * @param output : result of org.esa.beam.processor.baer processing
     */
    private void createOutputBaer(AerPixel output){

        double aot_550;
        double aot_440;


        _nulloutput=false;
         if (_flagAlphaOutOfRange != 0){
            output.setAlphaOutOfRangeFlag();
             output.setInvalidOutputFlag();
             setNullOutput();
        }
           if (_hasFlagAOT){
            output.setAotOutOfRangeFlag();
            output.setInvalidOutputFlag();
            setNullOutput();
        }




         if (_nulloutput)
         {
             aot_440=0.0;
             aot_550=0.0;
         }
        else
         {
            aot_440=angstroemPowerLaw(_aot[0], 0.440, BaerConstants.MERIS_BANDS[0], -_alpha);
            aot_550=angstroemPowerLaw(_aot[0], 0.550, BaerConstants.MERIS_BANDS[0], -_alpha);
         }

        output.setAlpha((float)_alpha);
        output.setAot_412((float)_aot[0]);
        output.setAot_440((float)aot_440);
        output.setAot_550((float)aot_550);



    }

    private void atmCorProcess(boolean useAotMin)
    {
        int b;
         if (_atm_corr_method.equals("UBAC")){

                    surfaceRefl();
        }
        else
        {
	       double airPressure=0;
            if (useAotMin)
            for (b=0; b<BaerConstants.NUM_BANDS;b++) {
                        _aot[b] = _aotMin[b];
            }
            else
                for (b=0; b<BaerConstants.NUM_BANDS;b++) {
                        _aot[b] = _aotTempx[b];
                }
            SmacProcess(airPressure);

        }
    }
    private void setNullOutput(){
        for (int b=0; b<BaerConstants.NUM_BANDS;b++)
        {
            _nulloutput = true;
            _surfRefl[b] = 0.0;
            _aot[b] = 0.0;
            _alpha=0.0;
        }
    }


    private void createAotAndAlpha(){
            angstroemSimple(_aotTempx);
            _alpha = _alphaTempY;
    }

   private double tDifCalculation(double caer,  double aot, double mu)
   {
       double tdif;
       tdif = Math.exp(- caer
                        * aot
                        * (1/ _pressure_div)
                        /mu);
       return tdif;
   }


    private double aotCalculation(double[] aertable, double b, double sun, double view)
    {
        double result;
        result = (aertable[0] * (b * b)
                + aertable[1] * b
                + aertable[2])
                * sun * view
                / BaerConstants.MU_LUT;
        return result;
    }

    private double ndviCalculation(double a, double b)
    {
        double result;
        result = (a - b) / (a + b);
        return result;
    }


    private double reflCalculation(double vegc, int band)
    {
        double result;
        double one_vegc = 1.0 - vegc;
         result = (vegc
                   * _reflVeg[_vegSpectraNumber].getValueAt(band)
                   + one_vegc
                   * _soilFractionAccess.getSoilFraction()
                   * _reflSoil[_soilSpectraNumber].getValueAt(band))
                / (vegc
                   + one_vegc
                   * _soilFractionAccess.getSoilFraction());
        return result;
    }

    /**
     * Calculates the apparent surface reflectance over land
     * and a rough estimation of the AOT in band 1
     * @return a rough estimation of the AOT at 412 nm
     */
    private double initSurfReflectance() {
        double aotGuess1=0.0;
        double alpha=1.0;
        double aotGuess7=0.0;
        double aotGuess13=0.0;
        double reflAerGuess1=0.0;
        double reflAerGuess7=0.0;
        double reflAerGuess13=0.0;
        double ndvi;
        double vegc=0;
        double reflMix7;
        double f;
        boolean flags= false;
        double h2 = 0.0;
        double correctBand1;
        double correctBand7;
        double correctBand13;
        double one_vegc;
        double sun,view;
        double coef;
        double param4;

        sun = Math.cos(Math.toRadians(_band_sza));
        view = Math.cos(Math.toRadians(_band_vza));
        coef = BaerConstants.MU_LUT / (sun * view);


        ndvi = ndviCalculation(_inputLocal[12], _inputLocal[6]);
        if (ndvi >= 0.0)
                vegc = 0.8*ndvi;

        correctBand7 = 0.0;
        correctBand13 = 0.0;
        correctBand1 = 0.0;



        while ((correctBand7 <= 0.0 ) || (correctBand13 <= 0.0) || (correctBand1 <= 0.0))
        {
          correctBand1= _inputLocal[0] - h2;
            if ((correctBand1 <= 0.0) && (h2 > 0.002)){
                h2 -= 0.001;
                if (h2 < 0.0)
                    h2 = 0.0;
                correctBand1 = _inputLocal[0] - h2;
            }
           aotGuess1 = aotCalculation(_aertable[0], correctBand1,sun, view);

          if (aotGuess1< 0.0) {
            aotGuess1 = 0.05;

              // NOUVEAU VERSION 15
              if (_inputLocal[0] < 0.005)
                  break;
              if (h2 > 0.002)
                  continue;
          }


            aotGuess7 = angstroemPowerLaw(aotGuess1, BaerConstants.MERIS_BANDS[6], BaerConstants.MERIS_BANDS[0], -alpha);

            aotGuess13 = angstroemPowerLaw(aotGuess1, BaerConstants.MERIS_BANDS[12],BaerConstants.MERIS_BANDS[0], -alpha);

            //formula : (b*b) / ( 4* a * a)  -  (c - d*MUL/(sun*view)) / a

            param4 = aotGuess1 * coef;
            reflAerGuess1 = equation_resolution(_aertable[0][0], _aertable[0][1],_aertable[0][2], param4);


           param4 = aotGuess7 * coef;
            reflAerGuess7 = equation_resolution(_aertable[6][0], _aertable[6][1],_aertable[6][2], param4);

            param4 = aotGuess13 * coef;
            reflAerGuess13 = equation_resolution(_aertable[12][0], _aertable[12][1],_aertable[12][2], param4);


            correctBand1 = _inputLocal[0]  - reflAerGuess1;
            correctBand7 = _inputLocal[6]  - reflAerGuess7;
            correctBand13 = _inputLocal[12] - reflAerGuess13;

            if (flags)
            {
                break;
            }

            if (correctBand1 <= 1.0E-6)
                correctBand1=0;
            if (correctBand7 <= 1.0E-6)
                correctBand7=0;
            if (correctBand13 <= 1.0E-6)
                correctBand13=0;

            if ((correctBand1 <= 0.0) && (correctBand7 <= 0.0))
            {
                h2 += 0.005;
                alpha = 1.0;
                continue;
            }

            if (correctBand1 <= 0.0){
                h2 += 0.005;
                alpha = 1.0;
                continue;
            }
           if ((correctBand7 <= 0.0) )
            {
               alpha += 0.05;
               h2 += 0.005;
               if (alpha > 2.5){
                    alpha = 1.3;
                    h2 += 0.001;
                    flags=true;
                   continue;
                }
            }
             if ((correctBand13 <= 0.0) )
            {
               alpha += 0.05;
               if (alpha > 2.5){
                    alpha = 1.3;
                    h2 += 0.001;
                    flags=true;
                   continue;
                 }
            }
            break;
        }

        _alphaHelp = alpha;
        ndvi =  ndviCalculation((_inputLocal[12] - reflAerGuess13) , (_inputLocal[6] - reflAerGuess7));

	    if (ndvi >= 0.0)
	        vegc = _ndviAccess.getNdviTuningFactor() * ndvi;


          one_vegc = 1.0 - vegc;
        reflMix7 = reflCalculation(vegc, 6);




        f = (_inputLocal[6] - reflAerGuess7)/ reflMix7;


        f = _f_tuningAccess.getF_TuningFactor()
            * (vegc + _soilFractionAccess.getSoilFraction() * one_vegc)
            * f;

          for (int b=0; b< BaerConstants.NUM_BANDS; b++){
                    _surfRefl[b] = f * reflCalculation(vegc, b);

        }

         return aotGuess1;
    }


    /**
     * Calculate positive result from a discriminant
     * @param a first double
     * @param b second double
     * @param c third double
     * @param d forth double
     * @return the positive result
     */
    private double  equation_resolution(double a, double b, double c, double d){
        double discriminant;
        double temp1, temp2;
        double sqrt;

        discriminant = (b * b) / (4 * a * a)
                        - (c - d) / a;
        sqrt = Math.sqrt(discriminant);

        if (discriminant >= 0.0){
            temp1 = -b / (2 * a) - sqrt;
            temp2 = -b / (2 * a) + sqrt;
            return positiveMin(temp1,temp2);
        }
        return 0.0;
    }

    /**
     * Compares two double, return the positive min
     * @param a first double
     * @param b second double
     * @return the positive min double
     */
    private double positiveMin(double a, double b) {
        double result;

        result = 0.0;
        if (a > 0.0)
            result = a;
        if (b > 0.0)
           result = b;
        if ((a > 0.0) && (b >0.0)) {
            if (a<b)
                result = a;
            else
                result = b;
        }
        return result;
    }

    /**
     * Estimates AOT in band b
     * @param aotGuess1 a rough estimation of the AOT in band 1
     * @param num1 first MERIS band
     * @param num2 second MERIS band
     * @return estimated AOT in band b
     */
    private double angstroemPowerLaw(double aotGuess1, double num1, double num2, double pow) {
        double aotGuess;

        aotGuess = aotGuess1
                   * Math.pow((num1
                               / num2)
                              , (pow));
        return aotGuess;
    }

    /**
     * Calculates aerosol reflectance from the first rough estimation of th AOT
     * Derives the corresponding AOT
     * @param band MERIS band number
     * @return numconst
     */
    private int aotLand(int band, int numconst){
        double tdifAeroS;
        double tdifAeroV;
        double h2;
        double aeroReflMax;

        double rhem;

        tdifAeroS = tDifCalculation(_caerSun, _aotguess[band], _muSun);
        tdifAeroV = tDifCalculation(_caerView,_aotguess[band], _muView);



        // calculation of the hemispherical reflectance
        rhem =  _rhemCoef[0]
                + _rhemCoef[1] * _aotguess[band]
                + _rhemCoef[2] * _aotguess[band] * _aotguess[band]
                + _rhemCoef[3] * _aotguess[band] * _aotguess[band] * _aotguess[band]
                + _rhemCoef[4] * _aotguess[band] * _aotguess[band] * _aotguess[band] * _aotguess[band];


        // calculation of the H2 term based on the use of direct transmissions
        h2 = (tdifAeroS
              * tdifAeroV
              * Math.cos(Math.toRadians(_band_sza))
              * Math.cos(Math.toRadians(_band_vza))
              * _surfRefl[band])
              / (1.0
                 - _surfRefl[band]
                 * Math.cos(Math.toRadians(_band_vza))
                 * rhem);


         //calculation of the aerosol reflectance
         _aeroRefl[band] = (_inputLocal[band] - h2)
                         * _p140
                         / _pTheta;


         //calculation of the aot

       _aotTempx[band] = aotCalculation(_aertable[band], _aeroRefl[band], _muSun, _muView);

        //boudary of the aot

          aeroReflMax = - _aertable[band][1] /
                (2.0 * _aertable[band][0]);

        if ((aeroReflMax >= 0.0)  && (_aeroRefl[band] > aeroReflMax)) {
            _aotTempx[band] = aotCalculation(_aertable[band], aeroReflMax, _muSun, _muView);
        }
        _aotguess[band] =  _aotTempx[band];

         if (BaerConstants.FIT_SELECTED_MERIS_BAND[band])
              {
                 if (_aeroRefl[band] <= 0.0)         {
                  _aotguess[band] = angstroemPowerLaw(_aotTempx[0],BaerConstants.MERIS_BANDS[band], BaerConstants.MERIS_BANDS[0], -_alphaTempY);
                  _aotTempx[band] = angstroemPowerLaw(_aotTempx[0],BaerConstants.MERIS_BANDS[band], BaerConstants.MERIS_BANDS[0] , -_alphaTempY);
                 }
              }
         else{
                  _aotguess[band] = angstroemPowerLaw(_aotTempx[0],BaerConstants.MERIS_BANDS[band], BaerConstants.MERIS_BANDS[0], -_alphaTempY);
                  _aotTempx[band] = angstroemPowerLaw(_aotTempx[0],BaerConstants.MERIS_BANDS[band], BaerConstants.MERIS_BANDS[0] , -_alphaTempY);
             }

        _aot[band] = _aotTempx[band];

        _flagAotOutOfRange[band]=0;

        if (_aotTempx[band] <= 0.0){
            _aotTempx[band] = 0.02;
            _flagAotOutOfRange[band] = 1;
            _hasFlagAOT=true;
            numconst = numconst + 1;
        }
        else {
            if (_aotTempx[band] >= 2.0){
                _aotTempx[band] = 2.0;
                _flagAotOutOfRange[band] = 2;
                _hasFlagAOT = true;
                _icx = band;
                _icheck++;
                numconst = numconst + 1;
            }
            else
                _flagAotOutOfRange[band] = 0;
        }

       return numconst;

    }

    /**
     * Calculates alpha and beta Angstrom coefficients
     * Calculates alpha and beta Angstrom coefficients
      */
    private void angstroem(){
        int nbSelectedBands;
        int b,i;
        double meanLnAOT;
        double meanLnLambda;
        double t1, t2;
        double[] factor;
         double alphaTempX;
        double[] a;
        double[] d;

        factor =  new double[BaerConstants.NUM_BANDS];
        a = new double[6];
        d = new double[6];

      //calculation of factor

        for (b=0; b<BaerConstants.NUM_BANDS; b++){
            if (BaerConstants.FIT_SELECTED_MERIS_BAND[b]) {

             _lnAot[b] = Math.log(_aotTempx[b]);
            _weight[b] = 1.0;
            factor[b] = 1.0;
            }
        }


        if (_flagAotOutOfRange[0] != 1){
            for (i=0; i<6; i++)
            {
                a[i] = (_lnAot[0] - _lnAot[i+1]) / (_lnLambda[i+1] - _lnLambda[0]);
                d[i] = angstroemPowerLaw(_aotTempx[i+1],BaerConstants.MERIS_BANDS[0],
                        BaerConstants.MERIS_BANDS[i+1],-a[i]);
                d[i] = (_aotTempx[0] - d[i]) / _aotTempx[0];
             }
            if ((d[0] > -0.03) && (d[0] < 0.1)){
              if ((a[0] > -0.2) && (a[0] < 1.8)){
                _weight[0] = 0.15;
                _weight[1] = 0.20;
                factor[0] = 2;
                factor[1] = 2;
                }
            }

            if ((d[1] > -0.06) && (d[1] < 0.18)){
                if ((a[1] > -0.3) && (a[1] < 1.9)){
                 _weight[0] = 0.15;
                 _weight[2] = 0.20;
                 factor[0] = 2;
                 factor[2] = 2;
                }
            }

            if ((d[2] > -0.08) && (d[2] < 0.22)){
                if ((a[2] > -0.3) && (a[2] < 1.9)){
                    _weight[0] = 0.15;
                    _weight[3] = 0.20;
                    factor[0] = 2;
                    factor[3] = 2;
                }
            }

            if ((d[3] > -0.011) && (d[3] < 0.28)){
                if ((a[3] > -0.3) && (a[3] < 1.9)){
                    _weight[0] = 0.15;
                    _weight[4] = 0.60;
                    factor[0] = 2;
                    factor[4] = 1;
                }
            }

             if ((d[4] > -0.011) && (d[4] < 0.28)){
                if ((a[4] > -0.3) && (a[4] < 1.9)){
                    _weight[0] = 0.15;
                    _weight[5] = 0.20;
                    factor[0] = 2;
                    factor[5] = 1;
                }
            }

            if ((d[5] > -0.011) && (d[5] < 0.28)){
                if ((a[5] > -0.3) && (a[5] < 1.9)){
                    _weight[0] = 0.15;
                    _weight[6] = 0.20;
                    factor[0] = 2;
                    factor[6] = 1;
                }
            }


         }
        else{
         if (_flagAotOutOfRange[1] != 1){
          if (_aotTempx[0] == 0.02) {
              for (i=1; i<6; i++)
              {
                  a[i] = (_lnAot[1] - _lnAot[i+1]) / (_lnLambda[i+1] - _lnLambda[1]);
                  d[i] = angstroemPowerLaw(_aotTempx[i+1], BaerConstants.MERIS_BANDS[0], BaerConstants.MERIS_BANDS[i+1], -a[i]);

                  d[i] = (_aotTempx[1] - d[i]) / _aotTempx[1];

              }
               if ((d[1] > -0.03) && (d[1] < 0.1)){
                    if ((a[1] > -0.5) && (a[1] < 2.0)){
                        _weight[1] = 0.20;
                        _weight[2] = 0.30;
                        factor[1] = 2;
                        factor[2] = 2;
                    }
              }

              if ((d[2] > -0.03) && (d[2] < 0.1)){
                    if ((a[2] > -0.5) && (a[2] < 2.0)){
                        _weight[1] = 0.20;
                        _weight[3] = 0.30;
                        factor[1] = 2;
                        factor[3] = 2;
                    }
              }
              if ((d[3] > -0.03) && (d[3] < 0.1)){
                    if ((a[3] > -0.5) && (a[3] < 2.0)){
                        _weight[1] = 0.20;
                        _weight[4] = 0.60;
                        factor[1] = 2;
                        factor[4] = 1;
                    }
              }
              if ((d[4] > -0.03) && (d[4] < 0.1)){
                    if ((a[4] > -0.5) && (a[4] < 2.0)){
                        _weight[1] = 0.20;
                        _weight[5] = 0.30;
                        factor[1] = 2;
                        factor[5] = 1;
                    }
              }
              if ((d[5] > -0.03) && (d[5] < 0.1)){
                    if ((a[5] > -0.5) && (a[5] < 2.0)){
                        _weight[1] = 0.20;
                        _weight[6] = 0.30;
                        factor[1] = 2;
                        factor[6] = 1;
                    }
              }

          }
         }

        }


        //calculation of alpha and beta angstroem coefficients
           _alpha=alphaTempX=_alphaTempY=0;
           _beta = 0;
           nbSelectedBands=0;
           meanLnAOT = 0;
           meanLnLambda = 0;

             for (b = 0; b < BaerConstants.NUM_BANDS; b++) {
                 if (BaerConstants.FIT_SELECTED_MERIS_BAND[b]) {
                    nbSelectedBands++;
                    meanLnAOT += _lnAot[b];
                    meanLnLambda += _lnLambda[b];
                    if (factor[b] == 2 ){
                        nbSelectedBands += 2;
                        meanLnAOT += 2*_lnAot[b];
                        meanLnLambda += 2*_lnLambda[b];
                    }

                     if (_aotTempx[b] <= 0.0){
                         nbSelectedBands--;
                     }
                 }
           }

           meanLnAOT = meanLnAOT / nbSelectedBands;
           meanLnLambda = meanLnLambda / nbSelectedBands;

           t1 = 0.0;
           t2 = 0.0;

           for (b = 0; b < BaerConstants.NUM_BANDS; b++) {
                 if (BaerConstants.FIT_SELECTED_MERIS_BAND[b]) {
                     t1 += (_lnAot[b] - meanLnAOT)
                           * (_lnLambda[b] - meanLnLambda);
                     t2 += ((_lnLambda[b] - meanLnLambda) * (_lnLambda[b] - meanLnLambda));
                    if (factor[b] == 2) {
                        t1 += 2*((_lnAot[b] - meanLnAOT)
                              * (_lnLambda[b] - meanLnLambda));
                        t2 += 2*((_lnLambda[b] - meanLnLambda) * (_lnLambda[b] - meanLnLambda));
                    }
                 }
           }


           _alphaTempY = - t1 / t2;
        _beta = Math.exp(meanLnAOT + meanLnLambda * _alphaTempY);

          // alpha  constraints
          _alpha = alphaTempX = _alphaTempY;


          if (_alpha > 2.0) {
            alphaTempX = 1.3;
            _alphaTempY = - 2.0;
            _flagAlphaOutOfRange = 1;
         }
         else
            if (_alpha < 0.0) {
                alphaTempX = 1.3;
                 _alphaTempY = - 2.0;
                 _flagAlphaOutOfRange = 2;
            }
            else
                 _flagAlphaOutOfRange = 0;


        if (_flagAlphaOutOfRange != 0) {
              if (_flagAotOutOfRange[0] != 1){
                  if (_aotTempx[1] < 2.0 ){      //v14   1.0) {
                      if  (_flagAotOutOfRange[1] != 1) {
                          _beta = (_aotTempx[0] + _aotTempx[1])
                                  / 2.0
                                  * Math.pow(((BaerConstants.MERIS_BANDS[0]
                                               + BaerConstants.MERIS_BANDS[1])
                                              /2)
                                             , alphaTempX);
                      }
                  }
                  else
                  {
                      _beta = 0.1;
                  }
                   _alpha = alphaTempX;
              }

       }
    }

    /**
     * Calculates alpha and beta Angstrom coefficients
     */
    private void angstroemSimple(double[] aotTemp){
        int b;
        double meanLnAOT = 0;
        double meanLnLambda = 0;
        int nbBands = 0;
        double t1 = 0;
        double t2 = 0;

        for (b=0; b<BaerConstants.NUM_BANDS; b++) {
            if (BaerConstants.FIT_SELECTED_MERIS_BAND[b]){
            nbBands++;
            meanLnLambda += _lnLambda[b];
            _lnAot[b] = Math.log(aotTemp[b]);
            meanLnAOT += _lnAot[b];
            }
        }
        meanLnAOT = meanLnAOT / nbBands;
        meanLnLambda = meanLnLambda / nbBands;
        // calculation of T1 and T2
          for (b=0; b<BaerConstants.NUM_BANDS; b++) {
            if (BaerConstants.FIT_SELECTED_MERIS_BAND[b]){

            t1 += (_lnAot[b] - meanLnAOT) * (_lnLambda[b] - meanLnLambda);
            t2 += ((_lnLambda[b] - meanLnLambda) * (_lnLambda[b] - meanLnLambda));
            }
        }

        // calculation of alpha and beta angstroem coefficients
        _alphaTempY = - t1 / t2;
    }

    /**
     * Estimates the spectral AOT smoothness
     * @return  number of bands selected for the fitting function
     */
    private int rmsdCalculation(){
        int countNbBandsFit=0;


        _rmsd = 0.0;

        for (int b=0; b<BaerConstants.NUM_BANDS; b++) {
         if ((BaerConstants.FIT_SELECTED_MERIS_BAND[b]) &&
                    (_aotTempx[b] <= 2.0)) {
                countNbBandsFit ++;
                if	(_flagAotOutOfRange[0] != 1) {
                    if (_flagAotOutOfRange[1] != 1) {
                        _aotTempxx[b] = ((6*_aotTempx[0] + _aotTempx[1])/7)
                                      * Math.pow((BaerConstants.MERIS_BANDS[b]
                                                  / ((6 * BaerConstants.MERIS_BANDS[0]
                                                    + BaerConstants.MERIS_BANDS[1])/7))
                                                 ,(- _alpha));
                    }
                    else
                    {
                        _aotTempxx[b] = angstroemPowerLaw(_aotTempx[0],BaerConstants.MERIS_BANDS[b],
                                BaerConstants.MERIS_BANDS[0],- _alpha);
                    }
                }
                else
                {
                    if (_flagAotOutOfRange[1] != 1) {
                        _aotTempxx[b] = angstroemPowerLaw(_aotTempx[1],BaerConstants.MERIS_BANDS[b],
                                BaerConstants.MERIS_BANDS[1], -_alpha);
                    }
                    else
                    {
                        _aotTempxx[b] = angstroemPowerLaw(_beta, BaerConstants.MERIS_BANDS[b],
                                1.0, -_alpha);
                    }
                }
                 _rmsd = _rmsd + ((_aotTempx[b] - _aotTempxx[b]) * (_aotTempx[b] - _aotTempxx[b]));

            }
        }
       _rmsd = Math.sqrt(_rmsd) / countNbBandsFit;

       return countNbBandsFit;
    }

    /**
     * Modifiez the spectral surface reflectance
     * @param jcount
     * @return jcount
     */
    private int iterativeSurfaceRefl(int jcount){
        double delta=0.0;
        int b=0;
        double surfaceRefl=0.0;
       // double epsilon = 0.0001;


         for (b=0; b<BaerConstants.NUM_BANDS; b++){
            if (BaerConstants.FIT_SELECTED_MERIS_BAND[b]){
                if (_aeroRefl[b] > 0.0) {
                    delta = (_aotTempx[b] - _aotTempxx[b]) / _aotTempx[b];
                    if (delta < -0.5) {
                        delta = - 0.5;
                    }
                }
                else
                {
                    delta = (0.025 - _aotTempxx[b]) / 0.025;
                    if (delta == 0.0) {
                        delta = -0.1;
                    }
                }

                surfaceRefl = _surfRefl[b];
               _surfRefl[b] = _surfRefl[b]
                               * (1.0
                                  + BaerConstants.COEFF[b]
                                  * _weight[b]
                                  * delta);


                if (_surfRefl[b] == 0.0) {
                    _surfRefl[b] = 0.002;
                }
                if ((_surfRefl[b] < 0.0) || (_surfRefl[b] > 1.0)) {
                     jcount = jcount + 1;
                    if (jcount > 150) {
                        break;
                    }
                    if (delta == 0.0){ //((delta < epsilon) && (delta > -epsilon)) {
                        _surfRefl[b] = surfaceRefl;
                    }
                    else {
                        if (delta < 0.0) {
                            if  (_surfRefl[b] < 0.0) {
                                _surfRefl[b] = surfaceRefl
                                               * (1.0 + 0.1 * delta);
                            } else {
                               _surfRefl[b] = surfaceRefl
                                                  * (1.0 - 0.1 * delta);
                            }
                        }
                        else {
                            if (delta <1.0){
                                if (_surfRefl[b] < 0.0) {
                                    _surfRefl[b] = surfaceRefl
                                                  * (1.0 - 0.1 * delta);
                                } else {
                                        _surfRefl[b] = surfaceRefl
                                                      * (1.0
                                                         + 0.1
                                                         * delta);
                                }
                            }
                            else {
                                if (delta > 1.0) {
                                    if (_surfRefl[b] < 0.0) {
                                        _surfRefl[b] = surfaceRefl
                                                      * (1.0 + 0.01);
                                    } else {
                                        _surfRefl[b] = surfaceRefl
                                                          * (1.0 - 0.01);
                                    }
                                }
                            }
                        }
                    }
                }

            }
         }

        return jcount;
    }


    /**
     * Computes the surface reflectances
     */
    private void surfaceRefl(){
        double tdifAeroS;
        double tdifAeroV;
        double h3;
        double reflAer=0.0;
        double off = 0.005;

         angstroem();

        for (int i=1; i<10;i++)
            _aotTempx[i] = angstroemPowerLaw(_aotTempx[0], BaerConstants.MERIS_BANDS[i],
                 BaerConstants.MERIS_BANDS[0], -_alphaTempY);
        for (int i=11; i<14;i++)
            _aotTempx[i] = angstroemPowerLaw(_aotTempx[0], BaerConstants.MERIS_BANDS[i],
                 BaerConstants.MERIS_BANDS[0], -_alphaTempY);



        for (int b=0;b<BaerConstants.NUM_BANDS; b++){
           if (BaerConstants.USED_MERIS_BAND[b]){
               reflAer = equation_resolution(_aertable[b][0],_aertable[b][1],_aertable[b][2], _aotTempx[b]);

                h3 = reflAer * (BaerConstants.MU_LUT/(_muSun*_muView));
             tdifAeroS = tDifCalculation(_caerSun, _aotTempx[b], _muSun);
               tdifAeroV = tDifCalculation(_caerView, _aotTempx[b], _muView);

                _surfRefl[b] = (_inputLocal[b] - h3 + off) / (tdifAeroS * tdifAeroV);
                _aot[b] = _aotTempx[b];

           }

        }

    }
    private void initSmac(){

        _SmacA0taup = new double[BaerConstants.NUM_BANDS];
        _SmacA1taup = new double[BaerConstants.NUM_BANDS];
        _Smaca0T = new double[BaerConstants.NUM_BANDS];
        _Smaca1T = new double[BaerConstants.NUM_BANDS];
        _Smaca2T = new double[BaerConstants.NUM_BANDS];
        _Smaca3T = new double[BaerConstants.NUM_BANDS];
        _Smaca0s = new double[BaerConstants.NUM_BANDS];
        _Smaca1s = new double[BaerConstants.NUM_BANDS];
        _Smaca2s = new double[BaerConstants.NUM_BANDS];
        _Smaca3s = new double[BaerConstants.NUM_BANDS];
        _Smaca0P = new double[BaerConstants.NUM_BANDS];
        _Smaca1P = new double[BaerConstants.NUM_BANDS];
        _Smaca2P = new double[BaerConstants.NUM_BANDS];
        _Smaca3P = new double[BaerConstants.NUM_BANDS];
        _Smaca4P = new double[BaerConstants.NUM_BANDS];
        _Smacwo  = new double[BaerConstants.NUM_BANDS];
        _Smacgc  = new double[BaerConstants.NUM_BANDS];
        _Smacresa1 = new double[BaerConstants.NUM_BANDS];
        _Smacresa2 = new double[BaerConstants.NUM_BANDS];
        _Smacresa3 = new double[BaerConstants.NUM_BANDS];
        _Smacresa4 = new double[BaerConstants.NUM_BANDS];
        _Smacrest1 = new double[BaerConstants.NUM_BANDS];
        _Smacrest2 = new double[BaerConstants.NUM_BANDS];
        _Smacrest3 = new double[BaerConstants.NUM_BANDS];
        _Smacrest4 = new double[BaerConstants.NUM_BANDS];
        _Smacak2 = new double[BaerConstants.NUM_BANDS];
        _Smacak = new double[BaerConstants.NUM_BANDS];
        _Smacb = new double[BaerConstants.NUM_BANDS];
        _Smaconepb = new double[BaerConstants.NUM_BANDS];
        _Smaconepb2= new double[BaerConstants.NUM_BANDS];
        _Smaconemb = new double[BaerConstants.NUM_BANDS];
        _Smaconemb2= new double[BaerConstants.NUM_BANDS];
        _Smacww    = new double[BaerConstants.NUM_BANDS];
        _Smaconemwo = new double[BaerConstants.NUM_BANDS];
        _Smacpfac   = new double[BaerConstants.NUM_BANDS];

        for (int b=0; b<BaerConstants.NUM_IN_REFLEC_BANDS; b++){
            SmacLoadBandCoefficients(b);
         }
    }
    private void SmacLoadBandCoefficients(int band){
        File file;
        SmacCoefficientsLoader coeffload = new SmacCoefficientsLoader();
        SmacCoefficientsAccess coeffAccess;
        int nextBand = band+1;
        String name_band = "radiance_"+nextBand;
        _smacProductType = "MERIS";
        try {
            file = _coeffMgr.getCoefficientFile(_smacProductType, name_band, _smacAerosolType);
            if (file != null) {
                  coeffload.load(file.getPath());
                  coeffAccess = coeffload;
                  SmacSetSensorCoefficients(coeffAccess, band);
            }
        } catch (IOException e) {
            _logger.severe(e.getMessage());
        }

    }

    private void SmacSetSensorCoefficients(SmacCoefficientsAccess coeff, int band) {

        _SmacA0taup[band] = coeff.getA0taup();
        _SmacA1taup[band] = coeff.getA1taup();


        // scattering transmission coefficients
        _Smaca0T[band] = coeff.getA0T();
        _Smaca1T[band] = coeff.getA1T();
        _Smaca2T[band] = coeff.getA2T();
        _Smaca3T[band] = coeff.getA3T();

        // spherical albedo coefficients
        _Smaca0s[band] = coeff.getA0s();
        _Smaca1s[band] = coeff.getA1s();
        _Smaca2s[band] = coeff.getA2s();
        _Smaca3s[band] = coeff.getA3s();


        // aerosol reflectance
        _Smaca0P[band] = coeff.getA0P();
        _Smaca1P[band] = coeff.getA1P();
        _Smaca2P[band] = coeff.getA2P();
        _Smaca3P[band] = coeff.getA3P();
        _Smaca4P[band] = coeff.getA4P();

        _Smacwo[band] = coeff.getWo();
        _Smacgc[band] = coeff.getGc();

        // residual aerosols
        _Smacresa1[band] = coeff.getResa1();
        _Smacresa2[band] = coeff.getResa2();
        _Smacresa3[band] = coeff.getResa3();
        _Smacresa4[band] = coeff.getResa4();

        // residual transmission
        _Smacrest1[band] = coeff.getRest1();
        _Smacrest2[band] = coeff.getRest2();
        _Smacrest3[band] = coeff.getRest3();
        _Smacrest4[band] = coeff.getRest4();

        // do some calculations which are NOT product dependent
        // ----------------------------------------------------
        _Smacak2[band] = (1.0 - _Smacwo[band]) * 3.0 * (1.0 - _Smacwo[band] * _Smacgc[band]);
        _Smacak[band] = Math.sqrt(_Smacak2[band]);
        _Smacb[band] = _SmactwoThird * _Smacak[band] / (1.0 - _Smacwo[band] * _Smacgc[band]);
        _Smaconepb[band] = 1.0 + _Smacb[band];
        _Smaconepb2[band] = _Smaconepb[band] * _Smaconepb[band];
        _Smaconemb[band] = 1.0 - _Smacb[band];
        _Smaconemb2[band] = _Smaconemb[band] * _Smaconemb[band];
        _Smacww[band] = _Smacwo[band] * _SmaconeQuarter;
        _Smaconemwo[band] = 1.0 - _Smacwo[band];
        _Smacpfac[band] = _Smacak[band] / (3.0 * (1.0 - _Smacwo[band] * _Smacgc[band]));

    }
    private void SmacProcess(double airPressure){
        double us;
        double invUs;
        double us2;
        double uv;
        double invUv;
        double usTimesuv;
        double invUsTimesUv;
        double dphi, Peq, m, s, cksi, ksiD;
        double taup,  Res_6s;
        double ttetas, ttetav;
        double aer_phase, aer_ref, Res_aer;
        double atm_ref;
        double d, del, dp, e, f, ss;
        double q1, q2, q3;
        double c1, c2, cp1, cp2;
        double x, y, z;
        double aa1, aa2, aa3;
        double temp;
        double cdr = Math.PI / 180.0;
        double crd = 180.0 / Math.PI;
        double invMaxPressure = 1.0 / 1013.0;

        double aot550;
        int b;
        int band_meris;

       aot550=angstroemPowerLaw(_aot[0],0.550, BaerConstants.MERIS_BANDS[0], -_alpha);

        // loop over vectors
        // -----------------

        for (b = 0; b < BaerConstants.NUM_IN_REFLEC_BANDS; b++) {
            // parameter setup
            us = Math.cos(Math.toRadians(_band_sza) * cdr);
            invUs = 1.0 / us;
            us2 = us*us;

            uv = Math.cos(Math.toRadians(_band_vza) * cdr);
            invUv = 1.0 / uv;
            usTimesuv = us * uv;
            invUsTimesUv = 1.0 / usTimesuv;

            dphi = (_band_saa - _band_vaa) * cdr;
            Peq = airPressure * invMaxPressure;
            // Peq equals 0

            /*------ 1) air mass */
            m = invUs + invUv;

            /*------ 2) aerosol optical depth in the spectral band, taup  */
            taup = _SmacA0taup[b] + _SmacA1taup[b] * aot550; //taup550[n];

            /*------ 3) gaseous transmissions (downward and upward paths)*/

            /*------  5) Total scattering transmission */
            temp = _Smaca2T[b] * Peq + _Smaca3T[b];

            /* downward */
            ttetas = _Smaca0T[b] + _Smaca1T[b] * aot550 * invUs + temp / (1.0 + us);
            /* upward   */
            ttetav = _Smaca0T[b] + _Smaca1T[b] * aot550 * invUv + temp / (1.0 + uv);

            /*------ 6) spherical albedo of the atmosphere */
            s = _Smaca0s[b] * Peq + _Smaca3s[b] + _Smaca1s[b] * aot550 + _Smaca2s[b] * aot550*aot550; //Math.pow(aot550,2);

            /*------ 7) scattering angle cosine */
            cksi = -(usTimesuv + (Math.sqrt(1.0 - us2) * Math.sqrt(1.0 - uv * uv ) * Math.cos(dphi)));
            if (cksi < -1) {
                cksi = -1.0;
            }

            /*------ 8) scattering angle in degree */
            ksiD = crd * Math.acos(cksi);

            /*------ 9) rayleigh atmospheric reflectance */


            /*------ 10) aerosol atmospheric reflectance */
            temp = ksiD * ksiD;
            aer_phase = _Smaca0P[b] + _Smaca1P[b] * ksiD
                    + _Smaca2P[b] * temp + _Smaca3P[b] * temp * ksiD
                    + _Smaca4P[b] * temp *temp; //Math.pow(temp,2);

            // now the uncommented block :-)
            // -----------------------------
            temp = 1.0 / (4.0 * (1.0 - _Smacak2[b] * us2));
            e = -3.0 * us2 * _Smacwo[b] * temp;
            f = -_Smaconemwo[b] * 3.0 * _Smacgc[b] * us2 * _Smacwo[b] * temp;
            dp = e / (3.0 * us) + us * f;
            d = e + f;
            temp = Math.exp(_Smacak[b] * taup);
            del = temp * _Smaconepb2[b] - temp * _Smaconemb2[b] ;
            ss = us / (1.0 - _Smacak2[b] * us2);
            temp = 3.0 * us;
            q1 = 2.0 + temp + _Smaconemwo[b] * temp * _Smacgc[b] * (1.0 + 2.0 * us);
            q2 = 2.0 - temp - _Smaconemwo[b] * temp * _Smacgc[b] * (1.0 - 2.0 * us);
            q3 = q2 * Math.exp(-taup * invUs);
            temp = (_Smacww[b] * ss) / del;
            c1 = temp * (q1 * Math.exp(_Smacak[b] * taup) * _Smaconepb[b] + q3 * _Smaconemb[b]);
            c2 = -temp * (q1 * Math.exp(-_Smacak[b] * taup) * _Smaconemb[b] + q3 * _Smaconepb[b]);
            cp1 = c1 * _Smacpfac[b];
            cp2 = -c2 * _Smacpfac[b];
            temp = _Smacwo[b] * 3.0 * _Smacgc[b] * uv;
            z = d - temp * dp + _Smacwo[b] * aer_phase * _SmaconeQuarter;
            x = c1 - temp * cp1;
            y = c2 - temp * cp2;
            temp = _Smacak[b] * uv;
            aa1 = uv / (1.0 + temp);
            aa2 = uv / (1.0 - temp);
            aa3 = usTimesuv / (us + uv);

            aer_ref = x * aa1 * (1.0 - Math.exp(-taup / aa1));
            aer_ref += y * aa2 * (1.0 - Math.exp(-taup / aa2));
            aer_ref += z * aa3 * (1.0 - Math.exp(-taup / aa3));
            aer_ref *= invUsTimesUv;

            /*--------Residu Aerosol --------*/
            temp = taup * m * cksi;
            Res_aer = (_Smacresa1[b] + _Smacresa2[b] * temp + _Smacresa3[b] * temp * temp)
                    + _Smacresa4[b] * temp * temp * temp ;

            /*---------Residu 6s-----------*/
            temp = taup * m * cksi;
            Res_6s = (_Smacrest1[b] + _Smacrest2[b] * temp + _Smacrest3[b] * temp * temp)
                    + _Smacrest4[b] * temp * temp * temp;

            /*------ 11) total atmospheric reflectance */
              atm_ref = aer_ref - Res_aer + Res_6s;


            /* reflectance at surface */
            /*------------------------ */
            if (b<10)
                band_meris= b;
            else
                band_meris = b+1;
            temp = _inputLocal[band_meris] - atm_ref; //r_toa;
            temp = temp / ((ttetas * ttetav) + (temp * s));

           _surfRefl[band_meris] = (float) temp;

            }


    }
}
