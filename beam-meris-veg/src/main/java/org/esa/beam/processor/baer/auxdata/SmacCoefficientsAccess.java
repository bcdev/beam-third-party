/*
 * $Id: SmacCoefficientsAccess.java,v 1.1.1.1 2005/02/15 11:13:36 meris Exp $
 *
 * Copyright (C) 2002,2003 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation. This program is distributed in the hope it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 */
package org.esa.beam.processor.baer.auxdata;

public interface SmacCoefficientsAccess {

    /**
     * Retrieves the gaseous absorption coefficient for h2o
     */
    public double getAh2o();

    /**
     * Retrieves the gaseous absorption exponent for h2o
     */
    public double getNh2o();

    /**
     * Retrieves the gaseous absorption coefficient for o3
     */
    public double getAo3();

    /**
     * Retrieves the gaseous absorption coefficient for o2.
     */
    public double getAo2();
 
    /**
     * Retrieves the gaseous absorption exponent for o2.
     */
    public double getNo2();

    /**
     * Retrieves the gaseous transmission coefficient for o2.
     */
    public double getPo2();

    /**
     * Retrieves the gaseous absorption coefficient for co2.
     */
    public double getAco2();

    /**
     * Retrieves the gaseous absorption exponent for co2.
     */
    public double getNco2();

    /**
     * Retrieves the gaseous transmission coefficient for co2.
     */
    public double getPco2();

    /**
     * Retrieves the gaseous absorption coefficient for ch4.
     */
    public double getAch4();

    /**
     * Retrieves the gaseous absorption exponent for ch4.
     */
    public double getNch4();

    /**
     * Retrieves the gaseous transmission coefficient for ch4.
     */
    public double getPch4();

    /**
     * Retrieves the gaseous absorption coefficient for no2.
     */
    public double getAno2();

    /**
     * Retrieves the gaseous absorption exponent for no2.
     */
    public double getNno2();

    /**
     * Retrieves the gaseous transmission coefficient for no2.
     */
    public double getPno2();

    /**
     * Retrieves the gaseous absorption coefficient for co2.
     */
    public double getAco();

    /**
     * Retrieves the gaseous absorption exponent for co2.
     */
    public double getNco();
 
    /**
     * Retrieves the gaseous transmission coefficient for co.
     */
    public double getPco();
 
    /**
     * Retrieves the spherical albedo coefficient 0.
     */
    public double getA0s();

    /**
     * Retrieves the spherical albedo coefficient 1.
     */
    public double getA1s();
 
    /**
     * Retrieves the spherical albedo coefficient 2.
     */
    public double getA2s();

    /**
     * Retrieves the spherical albedo coefficient 3.
     */
    public double getA3s();
 
    /**
     * Retrieves the scattering transmission coefficient 0.
     */
    public double getA0T();

    /**
     * Retrieves the scattering transmission coefficient 1.
     */
    public double getA1T();
 
    /**
     * Retrieves the scattering transmission coefficient 2.
     */
    public double getA2T();
 
    /**
     * Retrieves the scattering transmission coefficient 3.
     */
    public double getA3T();
 
    /**
     * Retrieves the molecular optical depth.
     */
    public double getTaur(); 
    public double getSr();

    /**
     * Retrieves aerosol optical depth coefficient 0.
     */
    public double getA0taup();

    /**
     * Retrieves aerosol optical depth coefficient 1.
     */
    public double getA1taup(); 
    public double getWo(); 
    public double getGc();
 
    /**
     * Retrieves aerosol reflectance coefficient 0.
     */
    public double getA0P();
 
    /**
     * Retrieves aerosol reflectance coefficient 1.
     */
    public double getA1P();

    /**
     * Retrieves aerosol reflectance coefficient 2.
     */
    public double getA2P();
 
    /**
     * Retrieves aerosol reflectance coefficient 3.
     */
    public double getA3P();
 
    /**
     * Retrieves aerosol reflectance coefficient 4.
     */
    public double getA4P();
 
    /**
     * Retrieves the residual transmission coefficient 1.
     */
    public double getRest1();

    /**
     * Retrieves the residual transmission coefficient 2.
     */
    public double getRest2();
 
    /**
     * Retrieves the residual transmission coefficient3.
     */
    public double getRest3();
 
    /**
     * Retrieves the residual transmission coefficient 4.
     */
    public double getRest4();
 
    /**
     * Retrieves the residual rayleigh coefficient 1.
     */
    public double getResr1();
 
    /**
     * Retrieves the residual rayleigh coefficient 2.
     */
    public double getResr2();

    /**
     * Retrieves the residual rayleigh coefficient 3.
     */
    public double getResr3();
 
    /**
     * Retrieves the residual aerosol coefficient 1.
     */
    public double getResa1(); 

    /**
     * Retrieves the residual aerosol coefficient 2.
     */
    public double getResa2();

    /**
     * Retrieves the residual aerosol coefficient 3.
     */
    public double getResa3();
 
    /**
     * Retrieves the residual aerosol coefficient 4.
     */
    public double getResa4();
 
   }
