package wew.water;

import com.bc.ceres.core.CoreException;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.runtime.Activator;
import com.bc.ceres.core.runtime.ModuleContext;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

public class ModuleActivator implements Activator {

    @Override
    public void start(ModuleContext moduleContext) throws CoreException {
        final URL codeSourceUrl = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        final File auxdataDir = new File(SystemUtils.getApplicationDataDir(), "beam-ui/auxdata/color-palettes");
        final ResourceInstaller resourceInstaller = new ResourceInstaller(codeSourceUrl, "auxdata/color-palettes/",
                                                                          auxdataDir);
        try {
            resourceInstaller.install(".*.cpd", ProgressMonitor.NULL);
        } catch (IOException e) {
            moduleContext.getLogger().log(Level.WARNING, "Could not install color palettes", e);
        }
    }

    @Override
    public void stop(ModuleContext moduleContext) throws CoreException {

    }
}
