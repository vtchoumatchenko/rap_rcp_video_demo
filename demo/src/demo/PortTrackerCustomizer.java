package demo;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class PortTrackerCustomizer implements ServiceTrackerCustomizer {
    private Integer httpPort = null;

    private final BundleContext context;

    public PortTrackerCustomizer(final BundleContext context) {
        this.context = context;
    }

    public Object addingService(final ServiceReference reference) {
        extractPort(reference);
        return context.getService(reference);
    }

    public void modifiedService(final ServiceReference reference, final Object service) {
        extractPort(reference);
    }

    private void extractPort(final ServiceReference reference) {
        // ignore if the help is starting
        if ("org.eclipse.help".equals(reference.getProperty("other.info"))) {return;} //$NON-NLS-1$ //$NON-NLS-2$
        try {
            // The http.port property is not defined if the ServletBridge is
            // used to load Equinox.
            Object httpPortObject = reference.getProperty("http.port"); //$NON-NLS-1$
            httpPort = Integer.parseInt(httpPortObject.toString());
        } catch (Exception ex) {
            // Do not throw exception - it will prevent the application from
            // starting when deployed with the ServletBridge.
            httpPort = null;
        }

    }

    public void removedService(final ServiceReference reference, final Object service) {
        context.ungetService(reference);
        httpPort = null;
    }

    public int getHttpPort() {
        if (httpPort == null) {
            throw new IllegalStateException("No OSGi HTTP Service running"); //$NON-NLS-1$
        }
        return httpPort;
    }
}