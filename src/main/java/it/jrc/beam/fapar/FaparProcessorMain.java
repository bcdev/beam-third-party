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
