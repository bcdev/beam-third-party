package uk.ac.ucl.mssl.climatephysics.beam.imaging;

import uk.ac.ucl.mssl.climatephysics.imaging.SqrtCRIF;
import uk.ac.ucl.mssl.climatephysics.imaging.SqrtDescriptor;

import java.awt.image.renderable.ContextualRenderedImageFactory;

import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.CRIFRegistry;
import javax.media.jai.registry.RIFRegistry;

import com.bc.ceres.core.runtime.Activator;
import com.bc.ceres.core.runtime.ModuleContext;

public class ModuleActivator implements Activator {

	@Override
	public void start(ModuleContext arg0)  {

		SqrtDescriptor.register();
		OperationRegistry registry =
			JAI.getDefaultInstance().getOperationRegistry();

		ContextualRenderedImageFactory sqrtCRIF = new SqrtCRIF();
		CRIFRegistry.register(registry, "uk.ac.ucl.mssl.climatephysics.imaging.sqrt",
				sqrtCRIF);
		RIFRegistry.register(registry, "uk.ac.ucl.mssl.climatephysics.imaging.sqrt", "MSSL",
				sqrtCRIF);

	}

	@Override
	public void stop(ModuleContext arg0) {
		// nothing to do
	}

}
