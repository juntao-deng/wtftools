package net.juniper.wtftools.core;

import java.net.URL;
import java.net.URLClassLoader;

import net.juniper.wtftools.WtfToolsActivator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.internal.Workbench;

public final class WtfProjectCommonTools {
	public static final String LOCATION_KEY = "framework_location_key";
	private static IProject currProject;
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
		 if(project == null)
			 project = currProject;
		 return project;
	}
	
	public static IProject getCurrentWtfProject() {
		IProject proj = getCurrentProject();
		try {
			if(proj.hasNature(WtfToolsConstants.WTF_NATURE_ID)){
				return proj;
			}
		} catch (CoreException e) {
			WtfToolsActivator.getDefault().logError(e);
		}
		return null;
	}
	
	public static String getFrameworkLocation() {
		IPreferenceStore store = WtfToolsActivator.getDefault().getPreferenceStore();
		return store.getString(LOCATION_KEY);
	}
	
	public static ClassLoader getCurrentProjectClassLoader() {
		URL[] urls = null;
		URLClassLoader loader = new URLClassLoader(urls, WtfProjectCommonTools.class.getClassLoader());
		return loader;
	}

	public static void setCurrentProject(IPath path) {
		IProject[] projects = getJavaProjects();
		for(int i = 0; i < projects.length; i ++){
			IPath projPath = projects[i].getLocation();
			if(projPath.isPrefixOf(path)){
				currProject = projects[i];
				break;
			}
		}
		
	}
	
	public static String getWrokspaceDirPath() {
		IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		String strPath = path.toOSString();
		return strPath;
	}
	
	public static IProject[] getJavaProjects() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		return projects;
	}
}
