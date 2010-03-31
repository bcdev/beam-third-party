package uk.ac.ucl.mssl.climatephysics.beam.atsr;

import org.esa.beam.framework.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.actions.AbstractVisatAction;

public class ClearLandFilterAction extends AbstractVisatAction {
    private DefaultSingleTargetProductDialog dialog;

    @Override
    public void actionPerformed(CommandEvent event) {
        if (null == dialog) {
            dialog = new DefaultSingleTargetProductDialog("ClearLandFilter", 
                                                          getAppContext(), 
                                                          "MSSL Filter Clear Land",
                                                          "stereoMatcher");
            dialog.setTargetProductNameSuffix("_ClearLandFilter");
        }
        dialog.show();
    }
}