package org.esa.beam.processor.baer;

import org.esa.beam.framework.processor.ProcessorRunner;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Jul 22, 2003
 * Time: 8:26:21 AM
 * To change this template use Options | File Templates.
 */
public class BaerProcessorMain {

    /**
     * Runs this module as stand-alone application
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        ProcessorRunner.runProcessor(args, new BaerProcessor());
    }
}
