package net.juniper.scutools.project;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class UpdateWtfClasspathAction implements IObjectActionDelegate{
	private ISelection	fSelection;
	@Override
	public void run(IAction action){
		if (fSelection instanceof IStructuredSelection){
			ProjCoreUtility.updateWorkspaceClasspath();
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