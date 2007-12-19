/*
 * $Id: VegAuxFileLoader.java,v 1.2 2006/03/21 17:26:50 meris Exp $
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
package org.esa.beam.processor.common.auxdata;

import java.util.logging.Logger;

public abstract class VegAuxFileLoader {

    protected Logger _logger;

    /**
     * Retrieves the version string of the aux file - or null if no version is present
     * @return
     */
    public abstract String getVersionString(String aux_version_key);

    /**
     * Retrieves the description of the aux file - or null if no description is present
     * @return
     */
    public abstract String getDescription(String aux_version_key);

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    protected VegAuxFileLoader() {
    }


}

