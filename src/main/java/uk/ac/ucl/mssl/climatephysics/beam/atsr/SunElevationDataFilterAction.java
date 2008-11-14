package uk.ac.ucl.mssl.climatephysics.beam.atsr;

import org.esa.beam.framework.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.beam.framework.gpf.ui.SingleTargetProductDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.actions.AbstractVisatAction;

public class SunElevationDataFilterAction extends AbstractVisatAction {
    private SingleTargetProductDialog dialog;

    @Override
    public void actionPerformed(CommandEvent event) {
        if (null == dialog) {
            dialog = new DefaultSingleTargetProductDialog("SunElevationDataFilter",
                                                          getAppContext(),
                                                          "MSSL Filter Sun Elevation", 
                                                          null);
        }
        dialog.show();
    }
}