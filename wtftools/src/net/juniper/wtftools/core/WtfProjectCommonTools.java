package net.juniper.wtftools.core;

import net.juniper.wtftools.WtfToolsActivator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.internal.Workbench;

public final class WtfProjectCommonTools {
	public static IPath getJbossHome(){
		String jbossHome = System.getenv("JBOSS_HOME");
		WtfToolsActivator.getDefault().logInfo("get jboss home path:" + jbossHome);
		return Path.fromOSString(jbossHome);
	}
	
	public static IProject getCurrentProject() {
		 ISelectionService selectionService =     
		 Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    
		 ISelection selection = selectionService.getSelection();
		 IProject project = null;
		 if(selection instanceof IStructuredSelection) {    
             Object element = ((IStructuredSelection)selection).getFirstElement();   
			 if (element instanceof IResource) {    
	             project= ((IResource)element).getProject();    
	         }
			 else if (element instanceof PackageFragmentRootContainer) {    
	             IJavaProject jProject =     
	                 ((PackageFragmentRootContainer)element).getJavaProject();    
	             project = jProject.getProject();    
	         } 
	         else if (element instanceof IJavaElement) {    
	             IJavaProject jProject= ((IJavaElement)element).getJavaProject();    
	             project = jProject.getProject();    
	         }
		 }
		 return project;
	}
}
