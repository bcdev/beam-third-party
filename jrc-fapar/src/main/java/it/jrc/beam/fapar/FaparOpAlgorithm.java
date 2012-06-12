/*
 * $Id: FaparAlgorithm.java,v 1.6 12/16/2010 1:08:07 PM peregan Exp $
 * Written by: Ophelie Aussedat, September, 2004
 * 
 * Copyright (C) 2004 by STARS
 * 
 *       Academic users:
 *       Are authorized to use this code for research and teaching,
 *       but must acknowledge the use of these routines explicitly
 *       and refer to the references in any publication or work.
 *       Original, complete, unmodified versions of these codes may be
 *       distributed free of charge to colleagues involved in similar
 *       activities.  Recipients must also agree with and abide by the
 *       same rules. The code may not be sold, nor distributed to
 *       commercial parties, under any circumstances.
 * 
 *       Commercial and other users:
 *       Use of this code in commercial applications is strictly
 *       forbidden without the written approval of the authors.
 *       Even with such authorization the code may not be distributed
 *       or sold to any other commercial or business partners under
 *       any circumstances.
 * 
 *  This software is provided as is without any warranty whatsoever.
 * 
 *  REFERENCES:
 *  [1] Gobron, N., Pinty, B., Aussedat, O., Taberner, M., Faber, O., Mélin, F., 
 *  Lavergne, T., Robustelli, M., Snoeij, P. (2008)
 *  Uncertainty Estimates for the FAPAR Operational Products Derived from MERIS - 
 *  Impact of Top-of-Atmosphere Radiance Uncertainties and Validation with Field Data.
 *  Remote Sensing of Environment, 112(4):1871–1883.
 *  Special issue: Remote Sensing Data Assimilation. Edited by Loew, A.
 *  DOI: 10.1016/j.rse.2007.09.011
 *
 *  [2] Gobron, N., Mélin, F., Pinty, B., Taberner, M., Verstraete, M. M. (2004)
 *  MERIS Global Vegetation Index: Evaluation and Performance.
 *  In: Proceedings of the MERIS User Workshop. 10-14 November 2003, Frascati, Italy, 
 *  volume 549 of ESA Special Publication, European Space Agency.
 *  Online: http://envisat.esa.int/workshops/meris03/participants/48/paper_23_gobron.pdf
 *
 *  [3] Gobron, N., Aussedat, O., Pinty, B., Taberner, M., Verstraete, M. M. (2004)
 *  Medium Resolution Imaging Spectrometer (MERIS) - Level 2 Land Surface Products - 
 *  Algorithm Theoretical Basis Document.
 *  EUR Report 21387 EN, European Commission - DG Joint Research Centre, Institute for 
 *  Environment and Sustainability, 20 pages.
 *  Available at: http://fapar.jrc.ec.europa.eu/pubs/?pubid=2004.eur-report.21387&format=html
 *
 *  [4] Gobron, N., Taberner, M., Pinty, B., Mélin, F., Verstraete, M. M., Widlowski, J.-L. (2003)
 *  Evaluation of the MERIS Global Vegetation Index: Methodology and Initial Results.
 *  In: Proceedings of the Working Meeting on the MERIS and ATSR Calibration and Geophysical 
 *  Validation. 20-23 October 2003, Frascati, Italy, volume 541 of ESA Special Publication, 
 *  European Space Agency.
 *  Online: http://envisat.esa.int/workshops/mavt_2003/MAVT-2003_504-paper_NGobron.pdf
 */
package it.jrc.beam.fapar;

import org.esa.beam.util.math.MathUtils;

/**
 * The <code>FaparAlgorithm</code> implements all the functionality of the FAPAR algorithm
 */
public final class FaparOpAlgorithm {

	// Invalid values for all reflectances
	//private static final float INVALID_REF = (float)1.001;

	// Relectances index
	/**Indice of the blue reflectance in the <code>getBRF()</code> array*/
	public static final int BLUE=0;
	/**Indice of the red reflectance in the <code>getBRF()</code> array*/
	public static final int RED=1;
	/**Indice of the nir reflectance in the <code>getBRF()</code> array*/
	public static final int NIR=2;

	// Algorithms polynoms coefficients
	private double[][][] _coeff;
	// Algorithms functions parameters
	private double[][][] _param;
	// Bidirectional Reflectance Factors
	private float[] _brf;

	// Rectified reflectances values
	private float _red_rec;
	private float _nir_rec;

	private float INV=-1;

	// Flags
	private int _process;

	/**
	 * Constructs the object with default parameters.
	 * <p/>
	 * Constructs and initializes the parameters and coefficients used in the formulae.
	 */
	public FaparOpAlgorithm() {
		// Initialize Parameters and Coefficients

		initializeParameters();
		initializeCoefficients();

		_brf=null;
		_red_rec=-1.0f;
		_nir_rec=-1.0f;
		_process=-1;
	}
	
	/**
	 * Returns the Red rectified reflectances values computed by the algorithm
	 * @return Red rectified reflectances values - <code>null</code> if no value has been computed yet
	 */
	public float getRedRec(){
		return _red_rec;
	}
	
	/**
	 * Returns the Nir rectified reflectances values computed by the algorithm
	 * @return Nir rectified reflectances values - <code>null</code> if no value has been computed yet
	 */
	public float getNirRec(){
		return _nir_rec;
	}
    
	/**
	 * Returns the blue, red and nir BRF values computed by the algorithm
	 * @return blue, red and nir BRF values in a 2 dimensions array - <code>null</code> if no values has been computed yet
	 */
	public float[] getBRF(){
		return _brf;
	}

    /**
     * Returns the flag that is updated by the algorithm if fapar is a value out of range
     * @return  5 for bad fapar value, same as input flag else
     */
    public int get_process() {
        return _process;
    }

    /**
     * Sets the parameters for computing the anisotropic correction.
     */
/* ANDREA: revised method initializeParameters() in order to take into account ID numbers different from 0      
     
/*     
    private void initializeParameters() {
	_param=new double[3][3];
	_param[BLUE][0]=0.24012;_param[BLUE][1]=0.56192;_param[BLUE][2]=-0.04203;
	_param[RED][0]=-0.46273;_param[RED][1]=0.70879;_param[RED][2]=0.037;
	_param[NIR][0]=0.63841;_param[NIR][1]=0.86523;_param[NIR][2]=-0.00123;
    }
*/
  private void initializeParameters() {
  
    _param=new double[5][3][3];  /*rho_HS,ki,theta_HG*/

  // vegetated surface
  
    _param[0][BLUE][0] =  0.24012;
    _param[0][BLUE][1] =  0.56192;
    _param[0][BLUE][2] = -0.04203;

    _param[0][RED][0]  = -0.46273;
    _param[0][RED][1]  =  0.70879;
    _param[0][RED][2]  =  0.037;

    _param[0][NIR][0]  =  0.63841;
    _param[0][NIR][1]  =  0.86523;
    _param[0][NIR][2]  = -0.00123;
    
  // bright surface

    _param[4][BLUE][0] =  0.42640;
    _param[4][BLUE][1] =  0.68545;
    _param[4][BLUE][2] = -0.02263;

    _param[4][RED][0] =  0.55649;
    _param[4][RED][1] =  0.87412;
    _param[4][RED][2] = -0.00357;

    _param[4][NIR][0] =  0.65740;
    _param[4][NIR][1] =  0.89788;
    _param[4][NIR][2] = -0.01377;
    
  }

    /**
     * Sets the polynoms coefficients.
     */
/*     
    private void initializeCoefficients() 
    {
	_coeff=new double[3][11];//(g1,g2 coeff)
	
	// Coeff for g1
	_coeff[0][0]=-9.2615;
  _coeff[0][1]=-0.029011;
  _coeff[0][2]=3.2545;
  _coeff[0][3]=0.055845;
  _coeff[0][4]=9.8268;
  _coeff[0][5]=_coeff[0][6]=_coeff[0][7]=_coeff[0][8]=_coeff[0][9]=0.0;
  _coeff[0][10]=1.0;
	
	// Coeff for g2
	_coeff[1][0]=-0.47131;
  _coeff[1][1]=-0.21018;
  _coeff[1][2]=-0.045159;
  _coeff[1][3]=0.076505;
  _coeff[1][4]=-0.80707;
  _coeff[1][5]=-0.048362;
  _coeff[1][6]=-1.2471;
  _coeff[1][7]=-0.54507;
  _coeff[1][8]=-0.47602;
  _coeff[1][9]=-1.1027;
  _coeff[1][10]=0.0;
	//Coeff for g0
	_coeff[2][0]=0.255;
  _coeff[2][1]=0.306;
  _coeff[2][2]=-0.0045;
  _coeff[2][3]=-0.32;
  _coeff[2][4]=0.32;
  _coeff[2][5]=-0.005;
	_coeff[2][6]=_coeff[2][7]=_coeff[2][8]=_coeff[2][9]=_coeff[2][10]=0.0;
    }
*/    
  private void initializeCoefficients() 
  {
    _coeff=new double[5][3][11];  //(g1,g2 coeff)
    
    // vegetated surface -> flag = 0
    
  	// Coeff for g1
  	_coeff[0][0][0]  = -9.2615;
    _coeff[0][0][1]  = -0.029011;
    _coeff[0][0][2]  =  3.2545;
    _coeff[0][0][3]  =  0.055845;
    _coeff[0][0][4]  =  9.8268;
    _coeff[0][0][5]  = _coeff[0][0][6] = _coeff[0][0][7] = _coeff[0][0][8] = _coeff[0][0][9]=0.0;
    _coeff[0][0][10] = 1.0;
  	// Coeff for g2
  	_coeff[0][1][0]  = -0.47131;
    _coeff[0][1][1]  = -0.21018;
    _coeff[0][1][2]  = -0.045159;
    _coeff[0][1][3]  =  0.076505;
    _coeff[0][1][4]  = -0.80707;
    _coeff[0][1][5]  = -0.048362;
    _coeff[0][1][6]  = -1.2471;
    _coeff[0][1][7]  = -0.54507;
    _coeff[0][1][8]  = -0.47602;
    _coeff[0][1][9]  = -1.1027;
    _coeff[0][1][10] =  0.0;
  	//Coeff for g0
  	_coeff[0][2][0]  =  0.255;
    _coeff[0][2][1]  =  0.306;
    _coeff[0][2][2]  = -0.0045;
    _coeff[0][2][3]  = -0.32;
    _coeff[0][2][4]  =  0.32;
    _coeff[0][2][5]  = -0.005;
  	_coeff[0][2][6]  = _coeff[0][2][7] = _coeff[0][2][8] = _coeff[0][2][9] = _coeff[0][2][10] = 0.0;

    // bright surface -> flag = 4

  	// Coeff for g1
  	_coeff[4][0][0]  =  0.79990;
    _coeff[4][0][1]  =  0.25117;
    _coeff[4][0][2]  = -0.24396;
    _coeff[4][0][3]  =  0.61913;
    _coeff[4][0][4]  = -1.7330;
    _coeff[4][0][5]  =  6.3093;
    _coeff[4][0][6]  =  0.16104;
    _coeff[4][0][7]  = -0.10645;
    _coeff[4][0][8]  = -2.8388;
    _coeff[4][0][9]  = -9.1247;
    _coeff[4][0][10] =  0.0;
  	// Coeff for g2
  	_coeff[4][1][0]  = -0.10065;
    _coeff[4][1][1]  = -0.41872;
    _coeff[4][1][2]  =  0.12671;
    _coeff[4][1][3]  = -0.30530;
    _coeff[4][1][4]  = -0.39783;
    _coeff[4][1][5]  =  0.56605;
    _coeff[4][1][6]  =  0.049710;
    _coeff[4][1][7]  = -0.11131;
    _coeff[4][1][8]  = -1.0396;
    _coeff[4][1][9]  = -0.87161;
    _coeff[4][1][10] =  0.0;
  	//No Coeff for g0 if flag = 4

  }

    /**
     * Computes the anisotropic normalisation. 
     * Update the array of values of bidirectional reflectance factor normalized by the anisotropic function values (brf) of this algorithm 
     * 
     * @param sza 	    	array of sun zenith angles in decimal degrees
     * @param saa           	array of sun azimuth angles in decimal degrees
     * @param vza           	array of view zenith angles in decimal degrees
     * @param vaa           	array of view azimuth angles in decimal degrees
     * @param blue_reflectance	array of reflectances values
     * @param red_reflectance	array of reflectances values
     * @param nir_reflectance	array of reflectances values
     *
     */
// ANDREA: revised in order to take into account also flag = 4   
    public void anisotropicCorrection(float sza, float saa, float vza, float vaa, float blue_reflectance, float red_reflectance, float nir_reflectance)
    {
    	double f1;	//f1(theta_0,theta_v,ki)
    	double f2;	//f2(omega,theta_HG)
    	double f3;	//f3(omega,rho_HS)
    	double G, cos_g, theta_0, theta_v, theta_0_rad, theta_v_rad, phi;		
    	// parameters
    	double rho_HS, ki, theta_HG;
    	// return value
    	_brf = new float[3];
    	
    	// initialize the brf with the reflectances values
    	_brf[BLUE] = blue_reflectance;
        _brf[RED]  = red_reflectance;
        _brf[NIR]  = nir_reflectance;
    	
     	// loop over the 3 reflectance array (blue, red, nir)
      	for (int p = 0; p < 3; p++)
      	{
    			// check for process flag. If set to false we must set the default value for
    			// invalid pixels and process the next pixel
// ANDREA: revised in order to take into account also flag = 4			
  /*			
    			if (_process[i] != 0) {
    				continue;
    			}
  */			
    			if (_process != 0 && _process != 4) {
    				continue;
    			}
    			
      		// Get the parameter for the corresponding reflectance
      		rho_HS   = _param[_process][p][0];
      		ki       = _param[_process][p][1];
      		theta_HG = _param[_process][p][2];

    			// Get some useful temporary values
    			theta_0     = (double)sza;
    			theta_v     = (double)vza;
    			theta_0_rad = MathUtils.DTOR*theta_0; // value in radian
    			theta_v_rad = MathUtils.DTOR*theta_v;
    			phi         = (double)saa - (double)vaa; // Relative azimuth angle
    			
    			// G
    			G     = Math.pow( Math.pow( Math.tan( theta_0_rad ), 2) + Math.pow( Math.tan( theta_v_rad ), 2) - 2*Math.tan( theta_0_rad ) * Math.tan( theta_v_rad ) * Math.cos( MathUtils.DTOR * phi), 0.5);
    			//cos g
    			cos_g = Math.cos(theta_0_rad)*Math.cos(theta_v_rad)+Math.sin(theta_0_rad)*Math.sin(theta_v_rad)*Math.cos(MathUtils.DTOR * phi);
    			
    			// f1
    			double x = Math.pow(Math.cos(theta_0_rad) * Math.cos(theta_v_rad),ki-1);
    			double y = Math.pow(Math.cos(theta_0_rad) + Math.cos(theta_v_rad),1-ki);
    			f1 = x/y;
    			
    			// f2
    			x = 1 - Math.pow(theta_HG,2);
    			y = Math.pow(1+2*theta_HG*cos_g+Math.pow(theta_HG,2),1.5);
    			f2 = x/y;
    			
    			// f3
    			x = 1 - rho_HS;
    			y = 1 + G;
    			f3 = 1 + x/y;
    			
    			_brf[p] = _brf[p]/(float)(f1*f2*f3);
    		}
    }

    /**
     * Computes the atmospheric rectification.
     * Update the red and nir reflectance rectified values of this algorithm
     * 
     * @param blue_reflectance	array of values of the blue bidirectional reflectance factor normalized
     * @param red_reflectance	array of values of the red bidirectional reflectance factor normalized
     * @param nir_reflectance	array of values of the nir bidirectional reflectance factor normalized
     */
    public void atmosphericRectification(float blue_reflectance, float red_reflectance, float nir_reflectance)
    {
	    _red_rec=Poly1(blue_reflectance,red_reflectance);

	    _nir_rec=Poly2(blue_reflectance,nir_reflectance);
    }
    
    /**
     * First Polynom
     * @param band1 first brf
     * @param band2 second brf
     *
     * @return New array result of the formula
     */
// ANDREA: revised in order to take into account also flag = 4     
    private float Poly1(float band1, float band2)
    {
	    float return_r = INV;
	    double B1, B2;
		    // check for process flag. If set to false we must set the default value for
		    // invalid pixels and process the next pixel
	      if (_process != 0 && _process != 4) {
			    return INV;
		    }
		    B1 = (double)band1;
		    B2 = (double)band2;
		    switch (_process)
		    {
          case 0:
     		    return_r = (float)(_coeff[_process][0][0] * Math.pow(B1+_coeff[_process][0][1], 2) + _coeff[_process][0][2] * Math.pow(B2+_coeff[_process][0][3], 2) + _coeff[_process][0][4] * B1 * B2);
     		    break;
          case 4:
     		    return_r = (float)((_coeff[_process][0][0] * Math.pow(B1+_coeff[_process][0][1], 2) + _coeff[_process][0][2] * Math.pow(B2+_coeff[_process][0][3], 2) + _coeff[_process][0][4] * B1 * B2) / (_coeff[_process][0][5] * Math.pow(B1 + _coeff[_process][0][6], 2) + _coeff[_process][0][7] * Math.pow(B2 + _coeff[_process][0][8], 2) + _coeff[_process][0][9] * B1 * B2));
     		    break;
        }
		    if (return_r < 0.0 || return_r > 1.0)
        { 
          _process = 5;
          return_r = INV;
        }
	    return return_r;
    }
    
    /**
     * Second Polynom
     * @param band1 first brf
     * @param band2 second brf
     *
     * @return New array result of the formula
     */
// ANDREA: revised in order to take into account also flag = 4     
    private float Poly2(float band1, float band2)
    {
	    float return_r = INV;
	    double B1, B2;
		    // check for process flag. If set to false we must set the default value for
		    // invalid pixels and process the next pixel
		    if (_process != 0 && _process != 4) {
			    return INV;
		    }
		    B1 = (double)band1;
		    B2 = (double)band2;
		    switch (_process)
        {
          case 0:
     		    return_r = (float)((_coeff[_process][1][0] * Math.pow(B1 + _coeff[_process][1][1], 2) + _coeff[_process][1][2] * Math.pow(B2 + _coeff[_process][1][3], 2) + _coeff[_process][1][4] * B1 * B2) / (_coeff[_process][1][5] * Math.pow(B1 + _coeff[_process][1][6], 2) + _coeff[_process][1][7] * Math.pow(B2 + _coeff[_process][1][8], 2) + _coeff[_process][1][9] * B1 * B2 + _coeff[_process][1][10]));
            break;
          case 4:
    		    return_r = (float)((_coeff[_process][1][0] * Math.pow(B1 + _coeff[_process][1][1], 2) + _coeff[_process][1][2] * Math.pow(B2 + _coeff[_process][1][3], 2) + _coeff[_process][1][4] * B1 * B2) / (_coeff[_process][1][5] * Math.pow(B1 + _coeff[_process][1][6], 2) + _coeff[_process][1][7] * Math.pow(B2 + _coeff[_process][1][8], 2) + _coeff[_process][1][9] * B1 * B2));
    		    break;
        }
		    if (return_r < 0.0 || return_r > 1.0)
        {
          _process = 5;
          return_r = INV;
        }
	    return return_r;
    }


    /**
     * Performs the FAPAR algorithm.
     * <li>Performes the anisotropic normalisation upon the reflectance values</li>
     * <li>Performes the atmospheric rectification upon the prerectified reflectance values</li>
     * <li>Computes the fapar algorithm from the rectified red and near infrared channels</li>
     * <li>Checks for any <code>invalid fapar</code> flag to add</li>
     *
     * @param sza           	array of sun zenith angles in decimal degrees
     * @param saa           	array of sun azimuth angles in decimal degrees
     * @param vza           	array of view zenith angles in decimal degrees
     * @param vaa           	array of view azimuth angles in decimal degrees
     * @param blueReflectance	array of blue reflectances values
     * @param redReflectance	array of red reflectances values
     * @param nirReflectance	array of nir reflectances values
     * @param process       	boolean array indicating whether a pixel has to be processed or not
     *
     * @return array of fapar values
     */
    public final float run(float sza, float saa, float vza, float vaa, float blueReflectance, float redReflectance, float nirReflectance, int process)
    {
    
      // array to be returned
      float r_return = -1.0f;
		
    	// Allocate space for rectified reflectances array
    	_red_rec = -1.0f;
    	_nir_rec = -1.0f;
    	
    	_process = process;
    
     	// Apply the anisotropic normalisation
    	// -----------------------------------
    	anisotropicCorrection(sza, saa, vza, vaa, blueReflectance, redReflectance, nirReflectance);
    	
    	// Apply the atmospheric rectification and initialize the rectified red and near values
    	// ------------------------------------------------------------------------------------
    	atmosphericRectification(_brf[BLUE], _brf[RED], _brf[NIR]);
            
    	// loop over all the values
      // ------------------------
    		if (_process == 0)
    		{
 			// Compute the fapar
    			r_return = (float)(_coeff[0][2][0]*_nir_rec-_coeff[0][2][1]*_red_rec-_coeff[0][2][2])/(float)(Math.pow(_coeff[0][2][3]-_red_rec,2) + Math.pow(_coeff[0][2][4]-_nir_rec,2) + _coeff[0][2][5]);
      // If fapar has a bad value set the flag
    			if (r_return < 0.0 || r_return > 1.0)
          {
            _process = 5;
            r_return = INV;
          }
    	}
      return r_return;
    }
}
