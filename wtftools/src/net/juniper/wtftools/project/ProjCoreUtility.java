package net.juniper.wtftools.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;
import net.juniper.wtftools.core.WtfToolsConstants;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

public class ProjCoreUtility {
	public static IAccessRule[]	Fobidden	= new IAccessRule[] { JavaCore.newAccessRule(new Path("**/*"), IAccessRule.K_NON_ACCESSIBLE) };
	public static IAccessRule[]	Discouraged	= new IAccessRule[] { JavaCore.newAccessRule(new Path("**/*"), IAccessRule.K_DISCOURAGED) };
	public static IAccessRule[]	Accessible	= {};

	public static void createProject(IProject project, IPath location, IProgressMonitor monitor) throws CoreException
	{
		if (!Platform.getLocation().equals(location))
		{
			IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
			desc.setLocation(location);
			project.create(desc, monitor);
		}
		else
			project.create(monitor);
	}
	
	public static void addNatureToProject(IProject proj, String[] natureIds, IProgressMonitor monitor) throws CoreException
	{
		IProjectDescription description = proj.getDescription();
		description.setNatureIds(natureIds);
		proj.setDescription(description, monitor);
	}

	public static IClasspathEntry[] getClasspathEntry(IProject project, WtfProjectClassPathContainerID id) throws CoreException{
		if(WtfProjectCommonTools.isTomcat()){
			if(WtfProjectCommonTools.getTomcatHome().equals("")){
				WtfToolsActivator.getDefault().logError("Please config tomcat's path first");
				return new IClasspathEntry[0];
			}
			switch (id){
//				case Middleware_Library:
//					return computeMiddlewareJarsInPath();
//				case ThdParty_Library:
//					String path = WtfProjectCommonTools.getFrameworkWebLocation() + "/init/thirdparty.txt";
//					return computeClasspathEntry(ClasspathComputer.compute3rdPartyJarsInPath(path), Accessible);
//				case Product_Common:
//					String dir = WtfProjectCommonTools.getFrameworkWebLocation() + "/init/common-lib";
//					return computeClasspathEntry(ClasspathComputer.computeProductJarsInPath(dir), Accessible);
				default:
					return new IClasspathEntry[0];
			}
		}
		return new IClasspathEntry[0];
	}

//	private static IClasspathEntry[] computeMiddlewareJarsInPath() {
//		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
//		if(WtfProjectCommonTools.isTomcat()){
//			String tomcat = WtfProjectCommonTools.getTomcatHome();
//			list.add(JavaCore.newLibraryEntry(new Path(tomcat + "/lib/servlet-api.jar"), null, null, null, null, false));
//			list.add(JavaCore.newLibraryEntry(new Path(tomcat + "/lib/jasper.jar"), null, null, null, null, false));
//			list.add(JavaCore.newLibraryEntry(new Path(tomcat + "/lib/jsp-api.jar"), null, null, null, null, false));
//			list.add(JavaCore.newLibraryEntry(new Path(tomcat + "/lib/el-api.jar"), null, null, null, null, false));
//			list.add(JavaCore.newLibraryEntry(new Path(tomcat + "/lib/annotations-api.jar"), null, null, null, null, false));
//		}
//		return list.toArray(new IClasspathEntry[0]);
//	}

	public static IClasspathEntry[] computeClasspathEntry(LibraryLocation[] libs, IAccessRule[] rules) throws CoreException{
		ArrayList<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		if (libs != null){
			for (LibraryLocation lib : libs){
				IClasspathAttribute[] atts = new IClasspathAttribute[0];
				if (lib.getDocLocation() != null)
				{
					atts = new IClasspathAttribute[] { JavaCore
							.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, lib.getDocLocation()) };
				}
				IClasspathEntry entry = JavaCore.newLibraryEntry(lib.getLibPath(), lib.getSrcPath(), null, rules, atts, false);
				list.add(entry);
			}
		}
		return list.toArray(new IClasspathEntry[0]);
	}


	public static void createFolder(IFolder folder) throws CoreException
	{
		if (!folder.exists())
		{
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder)
			{
				createFolder((IFolder) parent);
			}
			folder.create(true, true, null);
		}
	}

	public static IClasspathEntry createJREEntry()
	{
		return JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER"));
	}
	
	public static IClasspathEntry createVarEntry(IPath path, IPath sourcePath) {
		return JavaCore.newVariableEntry(path, sourcePath, null, false);
	}

	public static IClasspathEntry createSourceEntry(IProject project, String src) throws CoreException{
		IFolder folder = project.getFolder(src);
		if (!folder.exists())
			ProjCoreUtility.createFolder(folder);
		IPath path = project.getFullPath().append(src);
		return JavaCore.newSourceEntry(path, new IPath[0]);
	}

	public static void updateWorkspaceClasspath(){
		Job job = new Job("Update Wtf Project Classpath"){
			@Override
			public IStatus run(IProgressMonitor monitor)
			{
				updateWorkspaceClasspath(monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public static void updateWorkspaceClasspath(IProgressMonitor monitor){
//		String path = WtfProjectCommonTools.getFrameworkWebLocation() + "/init/thirdparty.txt";
//		try {
//			LibraryLocation[] locations = ClasspathComputer.compute3rdPartyJarsInPath(path);
//			for(int i = 0; i < locations.length; i ++){
//				String libDir = WtfProjectCommonTools.getTomcatHome() + "/lib";
//				File dir = new File(libDir);
//				String fileStr = locations[i].getLibPath().toOSString();
//				FileUtils.copyFileToDirectory(new File(fileStr), dir);
//			}
//		} 
//		catch (Exception e) {
//			WtfToolsActivator.getDefault().logError(e);
//		}
//		
//		
//		try {
//			String dirPath = WtfProjectCommonTools.getFrameworkWebLocation() + "/init/common-lib";
//			LibraryLocation[] locations = ClasspathComputer.computeProductJarsInPath(dirPath);
//			for(int i = 0; i < locations.length; i ++){
//				String libDir = WtfProjectCommonTools.getTomcatHome() + "/lib";
//				File dir = new File(libDir);
//				String fileStr = locations[i].getLibPath().toOSString();
//				FileUtils.copyFileToDirectory(new File(fileStr), dir);
//			}
//		} 
//		catch (Exception e) {
//			WtfToolsActivator.getDefault().logError(e);
//		}
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects){
			try{
				if (project.isOpen() && project.hasNature(WtfToolsConstants.WTF_NATURE_ID)){
					monitor.beginTask("Updating classpath", 100);
					project.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(monitor, 80));
					ClasspathComputer.updateClasspath(project, new SubProgressMonitor(monitor, 20));
				}
			}
			catch (CoreException e){
				WtfToolsActivator.getDefault().logError(e);
			}
		}
	}

	public static IClasspathEntry createContainerClasspathEntry(WtfProjectClassPathContainerID id){
		return JavaCore.newContainerEntry(new Path(WtfToolsConstants.WTF_LIBRARY_CONTAINER_ID).append(id.name()), false);
	}
}
