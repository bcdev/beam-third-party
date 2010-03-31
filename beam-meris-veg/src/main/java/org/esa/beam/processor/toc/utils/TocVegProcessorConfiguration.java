/*
 * $Id: TocVegProcessorConfiguration.java,v 1.3 2006/03/27 15:29:11 meris Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.esa.beam.processor.toc.utils;

import org.esa.beam.util.Guardian;
import org.esa.beam.processor.common.utils.VegProcessorConfiguration;

public class TocVegProcessorConfiguration extends VegProcessorConfiguration {


   // private String _nn_TrainingDbFile;
    private String _nn_File;

    /**
     * Constructs the object with default parameters
     */
    public TocVegProcessorConfiguration() {
        _normalisationFactorFile = "";
        _inputStatisticsFile = "";
        _outputStatisticsFile = "";
         _nn_File = "";
     }

   /**
     * Retrieves the auxiliary file for the LAI neural net
     * @return
     */
    public String getNN_AuxFile() {
        return _nn_File;
    }

    /**
     * Sets the auxiliary file for the LAI neural net
     * @param filePath
     */
    public void setNN_AuxFile(String filePath) {
        Guardian.assertNotNull("filePath", filePath);
        _nn_File = filePath;
    }

}
