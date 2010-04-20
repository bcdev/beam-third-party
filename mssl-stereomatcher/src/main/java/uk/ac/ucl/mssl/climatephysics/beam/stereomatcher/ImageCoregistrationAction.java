package uk.ac.ucl.mssl.climatephysics.beam.stereomatcher;

import org.esa.beam.framework.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.actions.AbstractVisatAction;

public class ImageCoregistrationAction extends AbstractVisatAction {
    private DefaultSingleTargetProductDialog dialog;

    @Override
    public void actionPerformed(CommandEvent event) {
        if (null == dialog) {
            dialog = new DefaultSingleTargetProductDialog("ImageCoregistration", 
                                                          getAppContext(),
                                                          "MSSL Image Coregistration", 
                                                          "stereoMatcher");
            dialog.setTargetProductNameSuffix("_ImageCoregistration");
        }
        dialog.show();
    }

}
