package demo;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "demo";

	private static Activator plugin;
    private PortTrackerCustomizer portTrackerCustomizer;
    private ServiceTracker portTracker;
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
        portTrackerCustomizer = new PortTrackerCustomizer(context);
        portTracker = new ServiceTracker(context, HttpService.class.getName(), portTrackerCustomizer);
        portTracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
    public int getServerPort() {
        return portTrackerCustomizer.getHttpPort();
    }
}
