package demo;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import demo.ApplicationWorkbenchAdvisor;
import demo.HostInfo;

public class EntryPoint implements IEntryPoint {

	public int createUI() {
		HostInfo.getInstance().initialize();
		Display display = PlatformUI.createDisplay();
		return PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
	}
}
