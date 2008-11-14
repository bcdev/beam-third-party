package uk.ac.ucl.mssl.climatephysics.beam.stereomatcher;

import org.esa.beam.framework.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.actions.AbstractVisatAction;

public class M5StereoMatcherAction extends AbstractVisatAction {
    private DefaultSingleTargetProductDialog dialog;

    @Override
    public void actionPerformed(CommandEvent event) {
        if (null == dialog) {
            dialog = new DefaultSingleTargetProductDialog("M5StereoMatcher", 
                                                          getAppContext(), 
                                                          "MSSL M5 Stereo Matcher",
                                                          null);
            dialog.setTargetProductNameSuffix("_M5StereoMatcher");
        }
        dialog.show();
    }

}
