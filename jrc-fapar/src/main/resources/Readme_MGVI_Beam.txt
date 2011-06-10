File name:	Readme_MGVI_BEAM.txt

Authors:	Ophelie Aussedat and Nadine Gobron

Affiliation:	Institute for Environment and Sustainability
           	Joint Research Centre
		Via Enrico Fermi, 1
		I-21020 Ispra (VA), Italy

E-mails:	Ophelie.Aussedat@jrc.ec.europa.eu
		Nadine.Gobron@jrc.ec.europa.eu

Web Site:	http://fapar.jrc.ec.europa.eu


Document title:	Documentation of the FAPAR Processor, a plug-in for MERIS/(A)ATSR Toolbox (BEAM)

Release date : 

    version 2.3 : June 2011
	version 2.2 : December 2010
	version 2.2 : December 2010
	version 2.1 : December 2007
	version 2.0 : 11 April 2005
	version 1.0 : 4 October 2004


Introduction
------------

This file briefly describes the installation and use of the accompanying software package, which computes the Fraction of Absorbed Photosynthetically Active Radiation (FAPAR), on the basis of ENVISAT/MERIS Level-1 data, following the algorithm described in the papers cited in the references section below.

Requirements
------------

The FAPAR processor is a plug-in module for the BEAM software developed by Brockmann Consult for ESA. 
This latter software must of course be installed prior to the FAPAR processor.

The BEAM software includes an application programming interface (API) and a set of executable tools to facilitate the use of MERIS, AATSR and further ASAR data products of the ESA ENVISAT satellite. It can be freely downloaded from:

http://www.brockmann-consult.de/beam/downloads.html

!!!!!  WARNING: FAPAR processor version 2.1 needs BEAM VISAT Version 4.0 or more while the version 1.0 can be run under previous BEAM releases.

Installation
------------

The BEAM package (here : BEAM VISAT Version 3.2) is installed within a particular, user-selectable, directory. For the purpose of this documentation file, that directory will be denoted $BEAM_DIR$.

CASE 1: Already built JAR file, containing classes:
---------------------------------------------------
The FAPAR processor package (.zip) normally contains the module in the form of a Java-archive file (fapar-processor-2.x.jar) and this readmeMGVI_BEAM.txt file . All that has to be done is to copy the .jar file to $BEAM_DIR$/modules/.

CASE 2: Build from Source code
------------------------------
If instead of the jar file (which should contain only java classes and the /help and /DOC directories) there are only the source files (.java files) the processor should be built from scratch. Ensure the following:
	- The directory ./com/bc/beam/processor/fapar/ exists. If not, create it.
        - The paths in the top section of the "build.sh" installation script
          exist and are suitable 
Then, in the directory of the sources, run the "build.sh" script. This should
compile and install the package. Running the script with the "-d" option will
also rebuild the documentation, using javadoc.
	 

Operation
---------

Once the BEAM software and the FAPAR processor have been installed, the package can be operated in two different modes: interactive and automatic.

To launch an interactive session, start the main BEAM software application (VISAT) and select the FAPAR processor using the following menu selections: Tools -> FAPAR Processor. A dialog window will appear:

	- Select the input file containing the MERIS Level-1 data to be processed.
	- Specify the output file where you want the results to be written.
	- Select the output format from the pull-down menu, if different from the BEAM default.
	- Optionally, save this configuration in a separate XML file, known to BEAM software as a (reusable) 'processing request'.
	- Initiate the FAPAR processor itself by clicking on the 'start' button.


To process one or more data sets automatically, i.e., without requiring manual input, it is also possible to launch the application from the command line (or an executable script, for that matter). 

At the operating system prompt (if you have defined $BEAM_HOME to something like /opt/beam4.1 ), type:

	$BEAM_HOME/bin/fapar.sh [options] [processing_request]

where [processing_request] stands for the name of the corresponding file, which can either be saved from an interactive session or created manually with a plain text editor.
Note: In fapar.sh you can adjust the Java minimum and maximum heap space with the java options -Xms(minimum) and -Xmx(maximum). 
	Example: r_processor.jar [options] [processing_request]

For the options, refer to the "Data Processors" section in the "Help" of VISAT.

The BEAM 'processing request', an XML file (in plain ASCII, with a '.xml' extension), must be supplied when the processor runs in non-interactive mode. In interactive mode, the request is an optional command line argument. Again, for more details, refer to "Help" in the VISAT window.

Help For The Plug-In
--------------------

The FAPAR processor version 2.x is provided with a help directory named 'fapar_processor'.
This help directory must be copied into $BEAM_DIR$/extensions. The help can then be viewed directly in the help menu of the VISAT application or from the processor help menu.

Changelog
---------

Changes from version 2.1 to 2.2
- Support for data products coming from Amorgos
- Adapted to API modifications in BEAM 4.9

Changes from version 2.1 to 2.2
- Necessary code modifications in order to make to output product more compliant with "official" MERIS Level 2 products. This has required the following changes:
  - The raw datatype of the FAPAR band has been set from uint16 to uint8
  - Parameters "no-data value" and "valid pixel expression" of the FAPAR band have been set
  - Scaling factor of the FAPAR band has been set from 1/250 to 1/254
  - Scaling offset of the FAPAR band has been set to -1/254
  - Marked "cloud over land" pixels not detected through MGVI processing.
  - Computed anysotropic normalization and atmospheric rectification on pixels corresponding to bright surfaces.
  - The maximum value of one of the parameters determining whether a pixel corresponds to a bright surface has been set from 1.25\rho_{681} to 1.3\rho_{681}.

Changes from version 2.0 to 2.1

- Necessary code modifications and file additions (module.xml) to accomodate the structure of Beam 4.1. This version CANNOT be used on earlier versions of BEAM-VISAT.
- The processor can work either with plain MERIS data or with AMORGOS data output, carrying the extra bands CorrectedLatitude and CorrectedLongitude. 

Changes from version 1.0 to version 2.0:

- Fapar Processor 2.0 can write the output in HDF5 as well as BEAM-DIMAP
- A Help directory is provided that enable the user to find more information about the processor directly from BEAM VISAT Help menu.
- Output bands reflectances and rectified reflectances are set as sprectal bands and can be viewed using the spectrum tool. Bands can be viewed all together but it is also possible to view only some bands by selecting them in the spectrum tool.
- Fapar Processor 2.0 uses the processor API as of BEAM 3.1 and thus can not be run with BEAM VISAT previous versions

Warranties and copyright information
------------------------------------

The FAPAR processor package described in this document is provided 'as is', with no warranty of merchantability or fitness to any particular purpose. Although every effort has been made to ensure accuracy of computations and conformity to the algorithms as published in the references below, the authors assume no responsibility whatsoever for any direct, indirect or consequential damage resulting from the use of this software. The FAPAR processor is distributed free of charge and cannot be sold or re-sold. It can be copied and distributed further, provided all documentation is attached and provided the original source of the software is explicitly and prominently described.

Questions, concerns and problems should be referred to the authors of the software package at the address indicated at the start of this file.

The copyright on this file and the associated software remains with the Joint Research Centre, an institution of the European Commission.


Last References:
---------------


[1] Gobron, N., Pinty, B., Aussedat, O., Taberner, M., Faber, O., Mélin, F., Lavergne, T., Robustelli, M., Snoeij, P. (2008)
    Uncertainty Estimates for the FAPAR Operational Products Derived from MERIS - Impact of Top-of-Atmosphere Radiance Uncertainties and Validation with Field Data.
    Remote Sensing of Environment, 112(4):1871–1883.
    Special issue: Remote Sensing Data Assimilation. Edited by Loew, A.
    DOI: 10.1016/j.rse.2007.09.011
    
[2] Gobron, N., Mélin, F., Pinty, B., Taberner, M., Verstraete, M. M. (2004)
    MERIS Global Vegetation Index: Evaluation and Performance.
    In: Proceedings of the MERIS User Workshop. 10-14 November 2003, Frascati, Italy, volume 549 of ESA Special Publication, European Space Agency.
    Online: http://envisat.esa.int/workshops/meris03/participants/48/paper_23_gobron.pdf
    
[3] Gobron, N., Aussedat, O., Pinty, B., Taberner, M., Verstraete, M. M. (2004)
    Medium Resolution Imaging Spectrometer (MERIS) - Level 2 Land Surface Products - Algorithm Theoretical Basis Document.
    EUR Report 21387 EN, European Commission - DG Joint Research Centre, Institute for Environment and Sustainability, 20 pages.
    Available at: http://fapar.jrc.ec.europa.eu/pubs/?pubid=2004.eur-report.21387&format=html
    
[4] Gobron, N., Taberner, M., Pinty, B., Mélin, F., Verstraete, M. M., Widlowski, J.-L. (2003)
    Evaluation of the MERIS Global Vegetation Index: Methodology and Initial Results.
    In: Proceedings of the Working Meeting on the MERIS and ATSR Calibration and Geophysical Validation. 20-23 October 2003, Frascati, Italy, volume 541 of ESA Special Publication, European Space Agency.
    Online: http://envisat.esa.int/workshops/mavt_2003/MAVT-2003_504-paper_NGobron.pdf

