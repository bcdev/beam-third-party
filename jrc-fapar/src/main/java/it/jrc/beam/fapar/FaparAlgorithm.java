/*
 * $Id: FaparAlgorithm.java,v 1.5 2007/12/11 15:56:31 andreio Exp $
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
 *       N. Gobron, M. Taberner, B. Pinty, F. Melin, M. M. Verstraete and J.-L.
 *       Widlowski (2002) 'MERIS Land Algorithm: preliminary results', in
 *       Proceedings of the ENVISAT Validation Workshop, Frascati, Italy, 09-13
 *       December, 2002, European Space Agency, SP 531
 *
 *       N. Gobron, B. Pinty, M. M. Verstraete and M. Taberner (2002) 'Medium
 *       Resolution Imaging Spectrometer (MERIS) - Level 2 Land Surface Products
 *       - Algorithm Theoretical Basis Document, Institute for Environment and
 *       Sustainability, *EUR Report No. 20143 EN*, 19 pp
 *
 *       Gobron, N., B. Pinty, M. M. Verstraete and M. Taberner (2002) 'Medium
 *       Resolution Imaging Spectrometer (MERIS) - An optimized FAPAR Algorithm -
 *       Theoretical Basis Document, Institute for Environment and
 *       Sustainability, *EUR Report No. 20149 EN*, 19 pp
 */
package it.jrc.beam.fapar;

import org.esa.beam.util.math.MathUtils;

/**
 * The <code>FaparAlgorithm</code> implements all the functionality of the FAPAR algorithm
 */
public final class FaparAlgorithm {
	
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
	private double[][] _coeff;
	// Algorithms functions parameters 
	private double[][] _param;
	// Bidirectional Reflectance Factors
	private float[][] _brf;
	
	// Rectified reflectances values
	private float[] _red_rec;
	private float[] _nir_rec;
	
	private float INV=-1;

	// Flags
	private int[] _process;
	
	/**
	 * Constructs the object with default parameters.
	 * <p/>
	 * Constructs and initializes the parameters and coefficients used in the formulae.
	 */
	public FaparAlgorithm() {
		// Initialize Parameters and Coefficients
		initializeParameters();
		initializeCoefficients();	

		_brf=null;
		_red_rec=null;
		_nir_rec=null;
		_process=null;
	}
	
	/**
	 * Returns the Red rectified reflectances values computed by the algorithm
	 * @return Red rectified reflectances values - <code>null</code> if no value has been computed yet
	 */
	public float[] getRedRec(){
		return _red_rec;
	}
	
	/**
	 * Returns the Nir rectified reflectances values computed by the algorithm
	 * @return Nir rectified reflectances values - <code>null</code> if no value has been computed yet
	 */
	public float[] getNirRec(){
		return _nir_rec;
	}
    
	/**
	 * Returns the blue, red and nir BRF values computed by the algorithm
	 * @return blue, red and nir BRF values in a 2 dimensions array - <code>null</code> if no values has been computed yet
	 */
	public float[][] getBRF(){
		return _brf;
	}
	
    /**
     * Sets the parameters for computing the anisotropic correction.
     */
    private void initializeParameters() {
	_param=new double[3][3];/*rho_HS,ki,theta_HG*/
	_param[BLUE/*0*/][0]=0.24012;_param[BLUE][1]=0.56192;_param[BLUE][2]=-0.04203;
	_param[RED/*1*/][0]=-0.46273;_param[RED][1]=0.70879;_param[RED][2]=0.037;
	_param[NIR/*2*/][0]=0.63841;_param[NIR][1]=0.86523;_param[NIR][2]=-0.00123;
    }

    /**
     * Sets the polynoms coefficients.
     */
    private void initializeCoefficients() 
    {
	_coeff=new double[3][11];//(g1,g2 coeff)
	
	// Coeff for g1
	_coeff[0][0]=-9.2615;_coeff[0][1]=-0.029011;_coeff[0][2]=3.2545;_coeff[0][3]=0.055845;_coeff[0][4]=9.8268;_coeff[0][5]=_coeff[0][6]=_coeff[0][7]=_coeff[0][8]=_coeff[0][9]=0.0;_coeff[0][10]=1.0;
	
	// Coeff for g2
	_coeff[1][0]=-0.47131;_coeff[1][1]=-0.21018;_coeff[1][2]=-0.045159;_coeff[1][3]=0.076505;_coeff[1][4]=-0.80707;_coeff[1][5]=-0.048362;_coeff[1][6]=-1.2471;_coeff[1][7]=-0.54507;_coeff[1][8]=-0.47602;_coeff[1][9]=-1.1027;_coeff[1][10]=0.0;
	//Coeff for g0
	_coeff[2][0]=0.255;_coeff[2][1]=0.306;_coeff[2][2]=-0.0045;_coeff[2][3]=-0.32;_coeff[2][4]=0.32;_coeff[2][5]=-0.005;
	_coeff[2][6]=_coeff[2][7]=_coeff[2][8]=_coeff[2][9]=_coeff[2][10]=0.0;
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
    public void anisotropicCorrection(float[] sza, float[] saa, float[] vza, float[] vaa, float[] blue_reflectance, float red_reflectance[], float nir_reflectance[])
    {
	int width=sza.length;
	double f1;	//f1(theta_0,theta_v,ki)
	double f2;	//f2(omega,theta_HG)
	double f3;	//f3(omega,rho_HS)
	double G, cos_g, theta_0, theta_v, theta_0_rad, theta_v_rad, phi;		
	// parameters
	double rho_HS, ki, theta_HG;
	// return value
	_brf=new float[3][width];
	
	// initialize the brf with the reflectances values
	for (int i=0;i<width;i++)
	{
		// Copy the values
		_brf[BLUE][i]=blue_reflectance[i];_brf[RED][i]=red_reflectance[i];_brf[NIR][i]=nir_reflectance[i];
	}
	
	// loop over the 3 reflectance array (blue, red, nir)
	for (int p=0;p<3;p++)
	{
		// Get the parameter for the corresponding reflectance
		rho_HS=_param[p][0];
		ki=_param[p][1];
		theta_HG=_param[p][2];
				
		// loop over all the values
		for (int i=0;i<width;i++)
		{
			// check for process flag. If set to false we must set the default value for
			// invalid pixels and process the next pixel
			if (_process[i]!=0) {
				continue;
			}
			
			// Get some useful temporary values
			theta_0=(double)sza[i];
			theta_v=(double)vza[i];
			theta_0_rad=MathUtils.DTOR*theta_0; // value in radian
			theta_v_rad=MathUtils.DTOR*theta_v;
			phi=(double)saa[i]-(double)vaa[i]; // Relative azimuth angle
			
			// G
			G=Math.pow( Math.pow( Math.tan( theta_0_rad ), 2) +
					Math.pow( Math.tan( theta_v_rad ), 2) -
					2*Math.tan( theta_0_rad ) * Math.tan( theta_v_rad ) * Math.cos( MathUtils.DTOR * phi), 0.5);
			//cos g
			cos_g=Math.cos(theta_0_rad)*Math.cos(theta_v_rad)+Math.sin(theta_0_rad)*Math.sin(theta_v_rad)*Math.cos(MathUtils.DTOR * phi);
			
			// f1
			double x=Math.pow(Math.cos(theta_0_rad)*Math.cos(theta_v_rad),ki-1);
			double y=Math.pow(Math.cos(theta_0_rad)+Math.cos(theta_v_rad),1-ki);
			f1=x/y;
			
			// f2
			x=1-Math.pow(theta_HG,2);
			y=Math.pow(1+2*theta_HG*cos_g+Math.pow(theta_HG,2),1.5);
			f2=x/y;
			
			// f3
			x=1-rho_HS;
			y=1+G;
			f3=1+x/y;
			
			_brf[p][i]=_brf[p][i]/(float)(f1*f2*f3);
		}				
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
    public void atmosphericRectification(float[] blue_reflectance, float red_reflectance[], float nir_reflectance[])
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
    private final float[] Poly1(float[] band1, float[] band2)
    {
	    float [] return_r=new float[band1.length];
	    
	    for (int i=0;i<band1.length;i++)
	    {
		    // check for process flag. If set to false we must set the default value for
		    // invalid pixels and process the next pixel
	            if (_process[i]!=0) {
			    return_r[i]=INV;
			    continue;
		    }
		    
		    return_r[i]=(float)((_coeff[0][0] * Math.pow(band1[i]+_coeff[0][1], 2) + _coeff[0][2] * Math.pow(band2[i]+_coeff[0][3], 2) + _coeff[0][4] * (double)band1[i] * (double)band2[i]));

		    if (return_r[i]<0.0 || return_r[i]>1.0) { _process[i]=5; return_r[i]=INV;}
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
    private final float[] Poly2(float[] band1, float[] band2)
    {
	    float [] return_r=new float[band1.length];
	    
	    double B1, B2;
	   
	    for (int i=0;i<band1.length;i++)
	    {
		    // check for process flag. If set to false we must set the default value for
		    // invalid pixels and process the next pixel
		    if (_process[i]!=0) {
			    return_r[i]=INV;
			    continue;
		    }
		    
		    B1=(double)band1[i];
		    B2=(double)band2[i];
		    
		    return_r[i]=(float)((_coeff[1][0] * Math.pow(B1 + _coeff[1][1], 2) + _coeff[1][2] * Math.pow(B2 + _coeff[1][3], 2) + _coeff[1][4] * B1 * B2) / 
				    (_coeff[1][5] * Math.pow(B1 + _coeff[1][6], 2) + _coeff[1][7] * Math.pow(B2 + _coeff[1][8], 2) + _coeff[1][9] * B1 * B2 + _coeff[1][10]));
		    
		    if (return_r[i]<0.0 || return_r[i]>1.0) { _process[i]=5; return_r[i]=INV;}
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
    public final float[] run(float[] sza, float[] saa, float[] vza, float[] vaa,
                             float[] blueReflectance, float[] redReflectance, float[] nirReflectance, 
			     int[] process) {
	int length=sza.length;
	
        // array to be returned
        float[] r_return=new float[length];
		
	// Allocate space for rectified reflectances array
	_red_rec=new float[length];
	_nir_rec=new float[length];
	
	_process=process;
	
	// Apply the anisotropic normalisation
	// -----------------------------------
	anisotropicCorrection(sza, saa, vza, vaa, blueReflectance, redReflectance, nirReflectance);
	
	// Apply the atmospheric rectification and initialize the rectified red and near values
	// ------------------------------------------------------------------------------------
	atmosphericRectification(_brf[BLUE], _brf[RED], _brf[NIR]);
        
	// loop over all the values
        // ------------------------
        for (int n = 0; n < length; n++) 
	{
		if (_process[n]==0)
		{
			// Compute the fapar
			 r_return[n]=(float)(_coeff[2][0]*_nir_rec[n]-_coeff[2][1]*_red_rec[n]-_coeff[2][2])/
				 (float)(Math.pow(_coeff[2][3]-_red_rec[n],2) + Math.pow(_coeff[2][4]-_nir_rec[n],2) + _coeff[2][5]);
			 
			 // If fapar has a bad value set the flag
			 if (r_return[n]<0.0 || r_return[n]>1.0) {_process[n]=5; r_return[n]=INV;}
		}
	}

        return r_return;
    }
}
