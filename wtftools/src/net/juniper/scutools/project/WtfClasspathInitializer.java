package net.juniper.scutools.project;


import net.juniper.scutools.ScuToolsActivator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;


public class WtfClasspathInitializer extends ClasspathContainerInitializer{
	public void initialize(IPath containerPath, IJavaProject javaProject) throws CoreException{
		if (javaProject != null){
			String libname = containerPath.segment(1);
			WtfProjectClassPathContainerID id = null;
			try{
				id = WtfProjectClassPathContainerID.valueOf(libname);
				ClasspathContainer container = new ClasspathContainer(id, ProjCoreUtility.getClasspathEntry(javaProject.getProject(), id));
				JavaCore.setClasspathContainer(container.getPath(), new IJavaProject[] { javaProject }, new IClasspathContainer[] { container }, null);
			}
			catch (IllegalArgumentException e){
				ScuToolsActivator.getDefault().logError(e);
			}
		}
	}
	
	@Override
	public Object getComparisonID(IPath containerPath, IJavaProject project){
		return containerPath;
	}
}