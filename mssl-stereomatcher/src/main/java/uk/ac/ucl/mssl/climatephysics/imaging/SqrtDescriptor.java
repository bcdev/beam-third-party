package uk.ac.ucl.mssl.climatephysics.imaging;


import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.RenderableRegistryMode;
import javax.media.jai.registry.RenderedRegistryMode;

public class SqrtDescriptor extends OperationDescriptorImpl {

	private static final long serialVersionUID = 2204440659701736425L;

	// The resource strings that provide the general documentation
	// and specify the parameter list for the "Sqrt" operation.
	private static final String[][] resources = {
		{"GlobalName",  "uk.ac.ucl.mssl.climatephysics.imaging.sqrt"},
		{"LocalName",   "Sqrt"},
		{"Vendor",      "uk.ac.ucl.mssl.climatephysics"},
		{"Description", "Operations that returns sqrt of pixels"},
		{"DocURL",      ""},
		{"Version",     "1.0"},
	};

	private static final String[] paramNames = {
	};
	@SuppressWarnings("unchecked")
	private static final Class[] paramClasses = {
	};
	private static final Object[] validParamValues = {
	};
	private static final Object[] paramDefaults = {
	};

	// Constructor.
	public SqrtDescriptor() {
		super(resources,
	          new String[] {RenderedRegistryMode.MODE_NAME,
              RenderableRegistryMode.MODE_NAME},
			  1, paramNames, paramClasses, paramDefaults, validParamValues);
	}

	@Override
    public boolean isRenderableSupported() {
        return true;
    }

	public static void register() {
		final OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
		registry.registerDescriptor(new SqrtDescriptor());
	}
}


