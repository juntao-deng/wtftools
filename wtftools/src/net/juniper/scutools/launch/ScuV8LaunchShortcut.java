package net.juniper.scutools.launch;

import java.util.ArrayList;
import java.util.List;

import net.juniper.scutools.ScuToolsActivator;
import net.juniper.scutools.common.ScuProjectCommonTools;
import net.juniper.scutools.common.ScuToolsConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * 
 * @author Juntao
 *
 */
public class ScuV8LaunchShortcut implements ILaunchShortcut{
	public static IProject currProject;
	public void launch(ISelection selection, String mode){
		if (selection instanceof IStructuredSelection){
			IStructuredSelection structSelection = (IStructuredSelection) selection;
			Object prjObject = structSelection.getFirstElement();
			if (prjObject instanceof IAdaptable){
				IProject project = (IProject) ((IAdaptable) prjObject).getAdapter(IProject.class);
				try{
					if(project != null && project.hasNature(ScuToolsConstants.SCU_NATURE_ID)){
						currProject = project;
						launch(project, mode);
					}
				}
				catch (CoreException e){
					ScuToolsActivator.getDefault().logError(e);
				}
			}
		}
	}

	private void launch(IProject project, String mode) throws CoreException{
		ILaunchConfiguration config = findLaunchConfiguration(project, mode);
		String nodeHome = launchNodeJS();
		if(nodeHome == null){
			MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_ERROR |SWT.YES);
			messageBox.setMessage("Can not find path of NodeJs. Please set it either in preference page or NODE_HOME env");
			messageBox.open();
			return;
		}
		syncProjects(nodeHome);
//		try {
//			Process p = Runtime.getRuntime().exec(nodeHome + "/node --debug=5858 " + nodeHome + "/space/static.js");
//		} 
//		catch (Exception e) {
//			ScuToolsActivator.getDefault().logError(e);
//			MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_ERROR |SWT.YES);
//			messageBox.setMessage(e.getMessage());
//			messageBox.open();
//			return;
//		}
		
		if (config != null){
			DebugUITools.launch(config, mode);
		}
	}
	
	private void syncProjects(String nodeHome) {
		
	}

	private String launchNodeJS() {
		IPath nodeHome = ScuProjectCommonTools.getNodeHome();
		return nodeHome == null ? null : nodeHome.toOSString();
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		
	}

	protected ILaunchConfigurationType getConfigurationType(){
		return getLaunchManager().getLaunchConfigurationType(ScuToolsConstants.SCU_VM_LAUNCH_ID);
	}

	protected ILaunchManager getLaunchManager(){
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected ILaunchConfiguration findLaunchConfiguration(IProject project, String mode){
		ILaunchConfigurationType configType = getConfigurationType();
		try{
			List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
			for (int i = 0; i < configs.length; i++){
				ILaunchConfiguration config = configs[i];
				if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "").equals("SCU_NODEJS")) { //$NON-NLS-1$
					candidateConfigs.add(config);
				}
			}
			int candidateCount = candidateConfigs.size();
			if (candidateCount < 1){
				return createConfiguration(project, configType);
			}
			else if (candidateCount == 1){
				return (ILaunchConfiguration) candidateConfigs.get(0);
			}
			else{
				ILaunchConfiguration config = chooseConfiguration(candidateConfigs, mode);
				if (config != null){
					return config;
				}
			}
			return null;
		}
		catch (CoreException e){
			ScuToolsActivator.getDefault().logError(e);
			return null;
		}
	}

	private ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configList, String mode){
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(Display.getDefault().getActiveShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle("Please select a configuration to start");
		if (mode.equals(ILaunchManager.DEBUG_MODE)){
			dialog.setMessage("a");
		}
		else{
			dialog.setMessage("b");
		}
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK){
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	protected void configLaunchConfiguration(ILaunchConfigurationWorkingCopy wc){
		wc.setAttribute("debug_port", 5858);
	}

	private ILaunchConfiguration createConfiguration(IProject javaProject, ILaunchConfigurationType configType){
		try{
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, "Scu_Launcher_5858");
			configLaunchConfiguration(wc);
			return wc.doSave();
		}
		catch (CoreException e){
			ScuToolsActivator.getDefault().logError(e);
			return null;
		}
	}
}
