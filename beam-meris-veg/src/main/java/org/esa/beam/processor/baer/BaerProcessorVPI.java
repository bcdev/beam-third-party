/*
 * $Id: BaerProcessorVPI.java,v 1.5 2006/03/30 09:24:57 meris Exp $
 *
 * Copyright (C) 2004 by Brockmann Consult (info@brockmann-consult.de)
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
package org.esa.beam.processor.baer;

import org.esa.beam.framework.processor.Processor;

public class BaerProcessorVPI {

    /**
     * Retrieves the description for the processor menu item
     */
    protected String getMenuDescription() {
        return "Invoke the MERIS BAER Aerosol Correction Processor";
    }

    /**
     * Retrieves the mnemonic of the processor menu item
     */
    protected int getMnemonic() {
        return 'A';
    }

    /**
     * Retrieves the menu item text
     */
    protected String getMenuText() {
        return "BAER Aerosol Correction Processor (MERIS)...";
    }

    /**
     * Retrieves the parent menu where the processor likes to be inserted
     */
    protected String getParent() {
        return "tools";
    }

    /**
     * Retrieves the name of the command
     */
    protected String getCommandName() {
        return "BaerProcessor";
    }

    /**
     * This method is used by {@link #initPlugIn(org.esa.beam.visat.VisatApp)} to add a processor specific helpset to the standard helpset.
     * This default implementation returns <code>null</code>. In this case no additional helpset was added.
     * Overwrite this method to set the helpset path if the plugin deploys a specific helpset for the processor.
     */
    protected String getHelpsetPath() {
        return BaerConstants.HELPSET_RESOURCE_PATH;
    }

    /**
     * Retrieves the help id of the command
     */
    protected String getHelpId() {
        return BaerConstants.HELP_ID;
    }

    /**
     * Retrieve the processor of the derived class
     */
    protected Processor createProcessor() {
        return new BaerProcessor();
    }

    /**
     * Retrieves the <code>placeAfter</code> value for the processor menu item
     */
    protected String getPlaceAfter() {
        return "Binning";
    }

    /**
     * Retrieves the <code>placeBefore</code> value for the processor menu item
     */
    protected String getPlaceBefore() {
        return null;
    }
}

