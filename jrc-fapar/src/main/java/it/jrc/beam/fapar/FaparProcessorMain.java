/*
 * $Id: FaparProcessorMain.java,v 1.3 2007/12/11 15:56:31 andreio Exp $
 * Written by: Ophelie Aussedat, September, 2004
 * 
 *  Copyright (C) 2004 by STARS
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

import org.esa.beam.framework.processor.ProcessorRunner;

/**
 * This class is the entry point for the BEAM Fapar Processor when invoked from the command line. The command
 * line arguments that can be understood by the processor are: <ul> <li>-i or --interactive (optional): open the user
 * interface for this Fapar Processor</li> <li>-d or --debug (optional): swicth the BEAM framework into debugging mode.
 * This will give a wealth of additional state information logged to the console window</li> <li>the path to a request
 * file (mandatory)</li> </ul>
 */
public class FaparProcessorMain {

    /**
     * Runs this module as stand-alone application
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        ProcessorRunner.runProcessor(args, new FaparProcessor());
    }
}
