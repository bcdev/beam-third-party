/*
 * $Id: WaterProcessorMain.java, MS0610151415
 *
 * Copyright (C) 2005/7 by WeW (michael.schaale@wew.fu-berlin.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package wew.water;


import org.esa.beam.framework.processor.ProcessorRunner;

/*
 * This class is the entry point for the BEAM examples Water Processor when invoked from
 * the command line. The command line arguments that can be understood by the processor are:
 * <ul>
 *  <li>-i or --interactive (optional): open the user interface for this Water Processor</li>
 *  <li>-d or --debug (optional): swicth the BEAM framework into debugging mode. This will give a wealth of
 * additional state information logged to the console window</li>
 *  <li>the path to a request file (mandatory)</li>
 * </ul>
 */
public class WaterProcessorMain {

    /*
     * Runs this module as stand-alone application
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        ProcessorRunner.runProcessor(args, new WaterProcessor());
    }
}
