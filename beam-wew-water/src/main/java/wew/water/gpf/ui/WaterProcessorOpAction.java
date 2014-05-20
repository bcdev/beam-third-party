/*
 * Copyright (C) 2011 Brockmann Consult GmbH (info@brockmann-consult.de)
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package wew.water.gpf.ui;

import com.bc.ceres.core.CoreException;
import com.bc.ceres.core.runtime.ConfigurationElement;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.actions.DefaultOperatorAction;

/**
 * Action for starting the FUB water processor operator user interface.
 *
 * @author Tonio Fincke
 */
public class WaterProcessorOpAction extends DefaultOperatorAction {

    private String operatorName;
    private String targetProductNameSuffix;

    private ModelessDialog dialog;

    @Override
    public void configure(ConfigurationElement config) throws CoreException {
        operatorName = getConfigString(config, "operatorName");
        if (operatorName == null) {
            throw new CoreException("Missing DefaultOperatorAction property 'operatorName'.");
        }
        targetProductNameSuffix = getConfigString(config, "targetProductNameSuffix");
        super.configure(config);
    }

    @Override
    public void actionPerformed(CommandEvent event) {
        if (dialog == null) {
            WaterProcessorDialog waterProcessorDialog = new WaterProcessorDialog(operatorName, getAppContext(), "FUB/WeW Water Processor",
                                                                                 event.getCommand().getHelpId());
            if (targetProductNameSuffix != null) {
                waterProcessorDialog.setTargetProductNameSuffix(targetProductNameSuffix);
            }
            dialog = waterProcessorDialog;
        }
        dialog.show();
    }

    @Override
    public boolean isEnabled() {
        return GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi("FUB.Water") != null;
    }

}
