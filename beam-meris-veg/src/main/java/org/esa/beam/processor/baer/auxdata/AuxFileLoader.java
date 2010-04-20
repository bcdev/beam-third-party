/*
 * $Id: AuxFileLoader.java,v 1.1.1.1 2005/02/15 11:13:35 meris Exp $
 *
 * Copyright (C) 2002,2003  by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.esa.beam.processor.baer.auxdata;

import java.util.logging.Logger;
import org.esa.beam.processor.baer.BaerConstants;

public abstract class AuxFileLoader {

    protected Logger _logger;

    /**
     * Retrieves the version string of the aux file - or null if no version is present
     * @return
     */
    public abstract String getVersionString();

    /**
     * Retrieves the description of the aux file - or null if no description is present
     * @return
     */
    public abstract String getDescription();

  //  public abstract void load(String auxPath) throws IOException;

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    protected AuxFileLoader() {
        _logger = Logger.getLogger(BaerConstants.LOGGER_NAME);
    }
}
