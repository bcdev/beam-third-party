                           README for

                   MERIS Vegetation Processors
                        BAER Version 1.1

=======================================================================
1. Introduction
=======================================================================
Welcome to the README file for the MERIS BAER Processors.
Its purpose is to retrieve aerosol & atmospheric correction over land.


=======================================================================
2. Contents of this Distribution
=======================================================================
This distribution of the MERIS Vegetation processors comprises
two directories. "extensions" contains the libraries and
VISAT plug-ins for the processors and "auxdata" contains
auxiliary data files:

    + extensions            (BEAM extensions folder)
        o baer-*.*.*.jar
    + auxdata               (BEAM auxdata folder)
      + baer
        o config.xml        (BAER configuration)
        o *.xml, *.par      (BAER auxiliary data files)
    o baer-readme.txt       (this file)
    o baer-version.txt      (current version number)


=======================================================================
3. Installation
=======================================================================
Simply extract this archive into your BEAM installation directory, so that
the files are copied into the corresponding BEAM directories:
    baer-*.*.*.zip/extensions --> ${BEAM-HOME}/extensions
    baer-*.*.*.zip/auxdata    --> ${BEAM-HOME}/auxdata


=======================================================================
4. Introduction to the BAER Processor
=======================================================================

4.1 Purpose

The purpose of the BAER algorithm is to estimate the surface reflectances and 
two parameters that allow to characterise the aerosols contained in the 
atmosphere from MERIS L2 data. The algorithm can used both MERIS data in full 
and reduced resolution.
These parameters and variables estimated are :
	The Aerosol Optical thickness,
	The Angstrom coefficient,
	The surface reflectances in 13 channels.



4.2 Algorithm

The BAER method (Bremen AErosol Retrieval) is an algorithm for remote sensing of 
aerosols from MERIS data over land. The algorithm has been developed to monitor 
the aerosol optical thickness (proportional to the aerosol total loading), 
over most of part of the continents. The aerosol information is used in a 
second step to perform atmospheric corrections, using either the SMAC processor 
on a pixel-by pixel basis or the UBAC processor, 
to derive the remotely sensed surface reflectance over the land.

The actual MERIS Level 2 product provides reflectance data with an incomplete atmospheric 
correction over land. The atmospheric correction is made for Rayleigh scattering only and
 the variable aerosol influence is not considered. Thus, an additional step of atmospheric 
correction for L2 data over land is required, considering the effect of the atmospheric aerosol.
For the retrieval of the AOT, the BAER method is used and modified for the use with MERIS L2 data. 
The original approach has been developed to retrieve AOT over land from SeaWiFS L1 data.
 It determines the spectral aerosol optical thickness (AOT) from nadir looking multi-wavelength 
radiometers. The method is based on the determination of the aerosol reflectance over 'dark surfaces', 
using the UV and short-wave-VIS range below the red-edge of the vegetation spectrum. This requires 
a proper separation of the variable surface effects, other atmospheric effects and aerosol effect.
For L2 data over land, the variability of the vegetation cover and the kind of the vegetation will
 be considered dynamically by means of a surface reflectance model tuned from the satellite scene 
self by the NDVI. The aerosol reflectance is obtained by removing the estimation of the surface effect.
 Look-up-tables of the relationship between AOT - aerosol reflectance and the use of constraints
 enable the determination of the AOT for 7 MERIS channels in a spectral range of 0.412 - 0.670 �m. 
AOT is extrapolated, using Angstr�m power law with parameters estimated from the retrieved AOT.
 Others terms of radiative transfer (aerosol reflectance, total transmittance and hemispheric
 reflectance) are computed once the AOT known to correct the Top Of Aerosol reflectance from 
aerosol effect.

Once the aerosol optical thickness estimated, it is used as input of the atmospheric 
correction method (either SMAC or UBAC) to perform the aerosol correction and to provide 
the surface reflectance in the 13 MERIS channels. If SMAC is used, the AOT at 550 nm required
 in input is estimated using the angstrom law from the first MERIS channel.

BAER requires as input in addition to the measured top of aerosol reflectances
 in the 13 MERIS bands, the sun and view zenith angles, the relative azimuth 
and the atmospheric pressure.

4.3 References

NOv-3341-NT-3352.pdf, Algorithm Theoretical Basis Document for MERIS
Determination of aerosol optical thickness over land surfaces using Bremen aerosol retrieval (BAER) and its application to atmospheric correction over lands, Wolfgang Von Hoyningen-Huene, Alexander Kokhanovsky, John Burrows, B�atrice Berthelot, Peter Regner, November 2005, Contract ESA AO/1-4233/02/I-LG.
NOv-3341-NT-3284.pdf, Validation report of BAER products

4.4 Processor Input and Output

+Input
======

MERIS L2 data product with:

	-L2 Top of aerosol reflectance Channel 1
	-L2 Top of aerosol reflectance Channel 2
	-L2 Top of aerosol reflectance Channel 3
	-L2 Top of aerosol reflectance Channel 4
	-L2 Top of aerosol reflectance Channel 5
	-L2 Top of aerosol reflectance Channel 6
	-L2 Top of aerosol reflectance Channel 7
	-L2 Top of aerosol reflectance Channel 8
	-L2 Top of aerosol reflectance Channel 9
	-L2 Top of aerosol reflectance Channel 10
	-L2 Top of aerosol reflectance Channel 12
	-L2 Top of aerosol reflectance Channel 13
	-L2 Top of aerosol reflectance Channel 14

	-L2_Flags

Pressure

Tie-point information
	-Solar and satellite zenith angle
	-Solar and satellite azimuth angles
	-Geographical coordinates

+Output
=======

ATMOPSHERE products
	-ALPHA
	-AOT channel 1
	-AOT channel 2
	-AOT channel 3

Surface reflectances
	-L2 Surface reflectance Channel 1
	-L2 Surface reflectance Channel 2
	-L2 Surface reflectance Channel 3
	-L2 Surface reflectance Channel 4
	-L2 Surface reflectance Channel 5
	-L2 Surface reflectance Channel 6
	-L2 Surface reflectance Channel 7
	-L2 Surface reflectance Channel 8
	-L2 Surface reflectance Channel 9
	-L2 Surface reflectance Channel 10
	-L2 Surface reflectance Channel 12
	-L2 Surface reflectance Channel 13
	-L2 Surface reflectance Channel 14

BAER Flags
	-Invalid
	-Invalid_Input
	-Cloud_input
	-ALPHA_OUT_OF_RANGE
	-AOT_OUT_OF_RANGE
	-Invalid_output
	-Smac_correction


=======================================================================
5. Support & More Information
=======================================================================

Algorithm implementation:
    carine.castillon@noveltis.fr

Installation, configuration, integration:
    norman.fomferra@brockmann-consult.de


Ramonville Saint-Agne and Geesthacht the 07.04.2006
