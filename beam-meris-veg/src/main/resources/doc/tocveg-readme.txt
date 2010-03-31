                           README for

                   MERIS Vegetation Processor
                      TOC-VEG Version 0.6

=======================================================================
1. Introduction
=======================================================================
Welcome to the README file for the MERIS TOC-VEG Processor.
Its purpose is to retrieve vegetation from TOC reflectances.


=======================================================================
2. Contents of this Distribution
=======================================================================
This distribution of the MERIS Vegetation processors comprises
two directories. "extensions" contains the libraries and
VISAT plug-ins for the processors and "auxdata" contains
auxiliary data files:

    + extensions            (BEAM extensions folder)
        o tocveg-*.*.*.jar
    + auxdata               (BEAM auxdata folder)
      + toc_veg
        o config.xml        (TOC-VEG configuration)
        o *.xml, *.par      (TOC-VEG auxiliary data files)
    o tocveg-readme.txt     (this file)
    o tocveg-changelog.txt  (list of software changes)
    o tocveg-version.txt    (current version number)


=======================================================================
3. Installation
=======================================================================
Simply extract this archive into your BEAM installation directory, so that
the files are copied into the corresponding BEAM directories:
    tocveg-*.*.*.zip/extensions --> ${BEAM-HOME}/extensions
    tocveg-*.*.*.zip/auxdata    --> ${BEAM-HOME}/auxdata


=======================================================================
4. Introduction to the TOC-VEG Processor
=======================================================================

4.1 Purpose

The purpose is to estimate four biophysical products from MERIS
surface reflectance data in full and reduced resolution.
These  biophysical variables  are :
	LAI (leaf area index),
	fCover (canopy cover fraction),
	fapar,
	LAIxCBabB (canopy chlorophyll content).

4.2 Algorithm

The proposed algorithm called here TOC_VEG is based on the training
of neural networks over a data base simulated using radiative transfer
models. The SAIL and PROSPECTS models are used to simulate the
reflectance in the 11 MERIS bands considered (490 nm, 510 nm, 560 nm, 620 nm,
665 nm, 681.25 nm, 708.75 nm, 753.75 nm, 778.75 nm, 865 nm, 885 nm).
The blue bands, oxygen and water absorption bands have not been used
because they would convey significant uncertainties associated with
atmospheric correction, while providing only marginal information on
the surface. The background optical properties are simulated using a
collection of soil, water and snow typical reflectance spectra.
A brightness factor is used to provide additional flexibility of the
background reflectance. Finally, to account for the medium resolution
of MERIS observations, mixed pixels are simulated with variable fractions
of pure background and pure vegetation.

The simulation of the top of canopy reflectances in the 11 MERIS bands
requires 12 input variables. They were drawn randomly according to
an experimental plan aiming at  getting a more evenly populated space
 of canopy realization. To provide more robust performances of the network,
the distributions of each input variable was close to the actual
distributions and, when possible, realistic co-distributions were
also used. This was achieved by considering a representative distribution
of targets over the earth surface that constrains the observation geometry,
as well as possible vegetation amount. A total number of 46 656
cases were simulated. Half of this data set was used for training,
 one quarter to evaluate hyper-specialization, and the last quarter
 to quantify the theoretical performances.

Back-propagation neural networks were trained for each variable considered.
The architecture was optimized, resulting in 2 hidden layers of
tangent-sigmoid neurones corresponding to a total around 300 coefficients
to adjust, and providing a good ratio (50-100) with the size of the training
data base.

The theoretical performances were evaluated over the test simulated data set.
It allowed providing estimates of uncertainties. They are close to 0.06
(absolute value) for fAPAR and fCover, and close to 25% (relative value)
 for LAI and LAI.Cab. that shows some loss of sensitivity for the larger
values of LAI and LAI.Cab due to saturation effects.

Finally, quality assessment criterions are proposed, including the
theoretical uncertainties on the product, the reflectance mismatch
quantifying the agreement with the training data base, and flags
indicating possible values out of range.

TOC_VEG requires as input in addition to the measured surface reflectances
in the 11 MERIS bands, the sun and view zenith angles, the relative azimuth.

4.3 References

MERIS_ATBD_TOC_VEG_03_06.pdf, Algorithm Theoretical Basis Document for MERIS
top of canopy Land Products (TOC_VEG), Frédéric Baret, Cédric Bacour, David Béal
Marie Weiss, Beatrice Berthelot and Peter Regner Contract ESA
AO/1-4233/02/I-LG.
Valid_MERIS_TOC_VEG_03_06_V2.pdf, Report on the validation of MERIS TOC_VEG land
 products F. Baret, C. Bacour, M. Weiss, B. Berthelot, March 2006, Contract ESA
AO/1-4233/02/I-LG.

4.4 Processor Input and Output

Input product is a surface MERIS data product. Following parameters are used:

	- L2 surface reflectance Channel 3  (489.7 nm)
	- L2 surface reflectance Channel 4  (509.7 nm)
	- L2 surface reflectance Channel 5  (559.6 nm)
	- L2 surface reflectance Channel 6  (619.6 nm)
	- L2 surface reflectance Channel 7  (664.6 nm)
	- L2 surface reflectance Channel 8  (680.9 nm)
	- L2 surface reflectance Channel 9  (708.4 nm)
	- L2 surface reflectance Channel 10 (753.5 nm)
	- L2 surface reflectance Channel 12 (778.5 nm)
	- L2 surface reflectance Channel 13 (864.8 nm)
	- L2 surface reflectance Channel 14 (884.8 nm)
	- L2 Flags dataset
	- BAER Flags
	- Solar and satellite zenith angle
	- Solar and satellite azimuth angles
	- Geographical coordinates

The TOC-VEG output product contains the following parameters:
	- LAI
	- fApar
	- FCover
	- LAI.Cab
	- fAPAR mismatch
	- TOA_VEG_FLAGS 


=======================================================================
5. Support & More Information
=======================================================================

Algorithm implementation:
    carine.castillon@noveltis.fr

Installation, configuration, integration:
    norman.fomferra@brockmann-consult.de


Ramonville Saint-Agne and Geesthacht the 07.04.2006
