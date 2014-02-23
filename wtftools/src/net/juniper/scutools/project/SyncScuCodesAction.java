package net.juniper.scutools.project;

import java.io.File;
import java.io.IOException;

import net.juniper.scutools.ScuToolsActivator;
import net.juniper.scutools.builder.ScuNodeProjectNature;
import net.juniper.scutools.common.ScuProjectCommonTools;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SyncScuCodesAction implements IObjectActionDelegate{
	private ISelection	fSelection;
	@Override
	public void run(IAction action){
		if (fSelection instanceof IStructuredSelection){
			IPath nodeHome = ScuProjectCommonTools.getNodeHome();
			if(nodeHome == null){
				MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_ERROR |SWT.YES);
				messageBox.setMessage("Can not find path of NodeJs. Please set it either in preference page or NODE_HOME env");
				messageBox.open();
				return;
			}
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for(int i = 0; i < projects.length; i ++){
				IProject proj = projects[i];
				try{
					if(!proj.hasNature(ScuNodeProjectNature.class.getName()))
						continue;
					syncProjectFiles(proj, nodeHome.toPortableString());
				}
				catch(Exception e){
					ScuToolsActivator.getDefault().logError(e);
				}
			}
		}
	}
	private void syncProjectFiles(IProject proj, String nodePath) {
		String projName = proj.getName();
		String spacePath = nodePath + "/space/" + projName;
		File spaceDir = new File(spacePath);
		if(!spaceDir.exists())
			spaceDir.mkdirs();
		
		String location = proj.getLocation().toPortableString();
		try {
			FileUtils.copyDirectory(new File(location + "/src"), new File(spacePath + "/src"));
		} 
		catch (IOException e) {
			ScuToolsActivator.getDefault().logError(e);
		}
		try {
			FileUtils.copyDirectory(new File(location + "/web"), new File(spacePath + "/web"));
		} 
		catch (IOException e) {
			ScuToolsActivator.getDefault().logError(e);
		}
	}
	@Override
	public void selectionChanged(IAction action, ISelection selection){
		this.fSelection = selection;
	}
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart){
	}
}