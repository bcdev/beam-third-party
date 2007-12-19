/*
 * $Id: GroundReflectanceAccess.java,v 1.1.1.1 2005/02/15 11:13:35 meris Exp $
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

import org.esa.beam.framework.processor.ProcessorException;

public interface GroundReflectanceAccess {

    // @todo - 3 tb/tb extend - and check with algorithm functionality. It is a bit vaguely defined here.
    /**
     * Retrieves the spectrum with the given name
     * @param name
     * @return the spectrum as array of 15 doubles (for bands 1 - 15)
     * @throws org.esa.beam.framework.processor.ProcessorException if the spectrum does not exist
     */
    public Spectrum getSpectrum(String name) throws ProcessorException;
}
