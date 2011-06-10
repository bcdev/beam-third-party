                           README for

                   MERIS Vegetation Processor
                     TOA-VEG Version 1.1.2

=======================================================================
1. Introduction
=======================================================================
Welcome to the README file for the MERIS TOA-VEG Processor.
Its purpose is to retrieve vegetation from TOA radiances.


=======================================================================
2. Contents of this Distribution
=======================================================================
This distribution of the MERIS Vegetation processors comprises
two directories. "extensions" contains the libraries and
VISAT plug-ins for the processors and "auxdata" contains
auxiliary data files:

    + extensions            (BEAM extensions folder)
        o toaveg-*.*.*.jar
    + auxdata               (BEAM auxdata folder)
      + toa_veg
        o config.xml        (TOA-VEG configuration)
        o *.xml, *.par      (TOA-VEG auxiliary data files)
    o toaveg-readme.txt     (this file)
    o toaveg-version.txt    (current version number)

=======================================================================
3. Installation
=======================================================================
Simply extract this archive into your BEAM installation directory, so that
the files are copied into the corresponding BEAM directories:
    toaveg-*.*.*.zip/extensions --> ${BEAM-HOME}/extensions
    toaveg-*.*.*.zip/auxdata    --> ${BEAM-HOME}/auxdata


=======================================================================
4. Introduction to the TOA-VEG Processor
=======================================================================

4.1 Purpose

The purpose is to estimate four biophysical products from MERIS L1b data,
in full and reduced resolution.
These  biophysical variables  are :
	LAI (leaf area index),
	fCover (canopy cover fraction),
	fapar,
	LAIxCab (canopy chlorophyll content).

4.2 Algorithm

The algorithm is based on the training of neural networks over a data base
simulated using radiative transfer canopy and atmosphere models.
The SAIL and PROSPECTS models are used to simulate the canopy reflectance in 
the 13 MERIS bands considered (413 nm, 443 nm, 490 nm, 510 nm, 560 nm, 620 nm,
665 nm, 681.25 nm, 708.75 nm, 753.75 nm, 778.75 nm, 865 nm, 885 nm). 
The background optical properties are simulated using a collection of soil, 
water and snow typical reflectance spectra. A brightness factor is used to 
provide additional flexibility of the background reflectance. Finally, to 
account for the medium resolution of MERIS observations, mixed pixels are 
simulated with variable fractions of pure background and pure vegetation.

The simulation of the top of atmosphere reflectance in the 13 MERIS bands
requires 16 input variables. They were drawn randomly according to an 
experimental plan aiming at getting a more evenly populated space of canopy 
realization. To provide more robust performances of the network, the 
distributions of each input variable was close to the actual distributions and, 
when possible, realistic co-distributions were also used. This was achieved by 
considering a representative distribution of targets over the earth surface that
constrains the observation geometry, as well as possible vegetation amount. 
A total number of 65909 cases were simulated. Half of this data set was used for
training, one quarter to evaluate hyper-specialization, and the last quarter to 
quantify the theoretical performances.

Back-propagation neural networks were trained for each variable considered. The
architecture was optimized, resulting in 2 hidden layers of tangent-sigmoid 
neurones corresponding to a total around 340 coefficients to adjust.

The theoretical performances were evaluated over the test simulated data set.
The quality of the results is not guaranteed when solar zenith angle is larger
than 60� due to the limitations in representativity of the canopy radiative
transfer model used for training the neural network.

TOA_VEG requires as input in addition to the measured top of atmosphere 
reflectances in the 13 MERIS bands, the sun and view zenith angles, the relative
azimuth and the atmospheric pressure.

4.3 References

MERIS_ATBD_TOA_VEG_03_06.pdf, Algorithm Theoretical Basis Document for MERIS
Top of canopy Land Products (TOA_VEG version 3), Marie Weiss, Fr�d�ric Baret, K. Pavageau, David B�al
Marie Weiss, Beatrice Berthelot and Peter Regner, March 2006, Contract ESA AO/1-4233/02/I-LG.
Valid_MERIS_TOA_VEG_03_06.pdf, Validation report, April 2006.

4.4 Processor Input and Output

Input product is a MERIS L1b data product. Following parameters are used:
	- TOA radiance Channel 1
	- TOA radiance Channel 2
	- TOA radiance Channel 3
	- TOA radiance Channel 4
	- TOA radiance Channel 5
	- TOA radiance Channel 6
	- TOA radiance Channel 7
	- TOA radiance Channel 8
	- TOA radiance Channel 9
	- TOA radiance Channel 10
	- TOA radiance Channel 12
	- TOA radiance Channel 13
	- TOA radiance Channel 14
	- L1b Flags dataset
	- Solar and satellite zenith angle
	- Solar and satellite azimuth angles
	- Geographical coordinates

The TOA-VEG output product contain the following parameters:
	- LAI
	- fApar
	- FCover
	- LAI.Cab
	- TOA_VEG_FLAGS 
	- Uncertainy on FApar
	- Uncertainy on LAI
	- Uncertainy on fCover
	- Uncertainy on LAICab
	- L1b Flags
	- MetaData
	- Tie points Grids

=======================================================================
5. Support & More Information
=======================================================================

Algorithm implementation:
    carine.castillon@noveltis.fr

Installation, configuration, integration:
    norman.fomferra@brockmann-consult.de


Ramonville Saint-Agne and Geesthacht the 13.05.2008
