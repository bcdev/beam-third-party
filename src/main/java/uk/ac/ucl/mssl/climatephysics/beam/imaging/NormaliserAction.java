package uk.ac.ucl.mssl.climatephysics.beam.imaging;

import org.esa.beam.framework.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.beam.framework.gpf.ui.SingleTargetProductDialog;
import org.esa.beam.framework.ui.command.CommandEvent;
import org.esa.beam.visat.actions.AbstractVisatAction;

public class NormaliserAction extends AbstractVisatAction {
	private SingleTargetProductDialog dialog;
	public void actionPerformed(CommandEvent event){
		if (null == dialog){
			dialog = new DefaultSingleTargetProductDialog(
					"Normaliser",
					getAppContext(),
					"MSSL Image Normaliser",
					null);
		}
		dialog.show();
	}	
}