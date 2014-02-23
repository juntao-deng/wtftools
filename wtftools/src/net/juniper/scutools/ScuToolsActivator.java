package net.juniper.scutools;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScuToolsActivator extends AbstractUIPlugin implements ILogSupport {

	// The plug-in ID
	public static final String PLUGIN_ID = "WtfTools"; //$NON-NLS-1$

	// The shared instance
	private static ScuToolsActivator plugin;
	
	private LogUtility logUtility;
	
	/**
	 * The constructor
	 */
	public ScuToolsActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ScuToolsActivator getDefault() {
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
	
	public void logError(String msg) {
		getLogProxy().logErrorMessage(msg);
	}

	public void logError(Throwable e) {
		getLogProxy().log(e);
	}

	public void logError(String msg, Throwable e) {
		getLogProxy().log(msg, e);
	}

	public void logInfo(String msg) {
		getLogProxy().logInfoMessage(msg);
	}
	
	public LogUtility getLogProxy() {
		if(logUtility == null){
			logUtility = new LogUtility(getLog(), getBundle().getSymbolicName());
		}
		return logUtility;
	}
}
