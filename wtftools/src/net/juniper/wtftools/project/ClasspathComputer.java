package net.juniper.wtftools.project;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;
import net.juniper.wtftools.core.WtfToolsConstants;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;

@SuppressWarnings("restriction")
public class ClasspathComputer{

	public static void updateClasspath(IProject project, IProgressMonitor monitor) throws CoreException{
		if (project != null && project.hasNature(WtfToolsConstants.WTF_NATURE_ID)){
			monitor.subTask("Update classpath");
			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] entries = getClasspath(project, false);
			javaProject.setRawClasspath(entries, javaProject.getPath().append("web/WEB-INF/classes"), monitor);
		}
	}

	public static IClasspathEntry[] getClasspath(IProject project, boolean clear) throws CoreException{
		IJavaProject javaproject = JavaCore.create(project);
		ArrayList<IClasspathEntry> result = new ArrayList<IClasspathEntry>();
		addSourceAndLibraries(javaproject, clear, result);
		result.add(ProjCoreUtility.createJREEntry());
		for (WtfProjectClassPathContainerID id : WtfProjectClassPathContainerID.values()){
			result.add(ProjCoreUtility.createContainerClasspathEntry(id));
		}
		try{
			String path = WtfProjectCommonTools.getFrameworkWebLocation() + "/init/thirdparty.txt";
			List<String> lines = FileUtils.readLines(new File(path));
			if(lines != null){
				Iterator<String> it = lines.iterator();
				Workspace wp = (Workspace) ResourcesPlugin.getWorkspace();
				while(it.hasNext()){
					String lib = it.next();
					if(lib.startsWith("$project_")){
						String projName = lib.substring("$project_".length());
						IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
						if(proj == project)
							continue;
						if(proj != null)
							result.add(JavaCore.newProjectEntry(proj.getFullPath()));
						else
							WtfToolsActivator.getDefault().logError("can not find project with name:" + projName);
					}
					else{
						String libPath = "org.eclipse.jdt.USER_LIBRARY/" + lib;
						result.add(JavaCore.newContainerEntry(new Path(libPath)));
					}
				}
			}
		}
		catch(Exception e){
			WtfToolsActivator.getDefault().logError(e.getMessage(), e);
		}
		IClasspathEntry[] entries = result.toArray(new IClasspathEntry[result.size()]);
		IJavaModelStatus validation = JavaConventions.validateClasspath(javaproject, entries, javaproject.getOutputLocation());
		if (!validation.isOK()){
			throw new CoreException(validation);
		}
		return (IClasspathEntry[]) result.toArray(new IClasspathEntry[result.size()]);
	}

	private static void addSourceAndLibraries(IJavaProject project, boolean clear, ArrayList<IClasspathEntry> result) throws CoreException{
		HashSet<IPath> paths = new HashSet<IPath>();
		if (!clear){
			IClasspathEntry[] entries = project.getRawClasspath();
			for (int i = 0; i < entries.length; i++){
				IClasspathEntry entry = entries[i];
				int entryType = entry.getEntryKind();
				if (entryType == IClasspathEntry.CPE_SOURCE || entryType == IClasspathEntry.CPE_PROJECT || entryType == IClasspathEntry.CPE_LIBRARY){
					// avoid duplicate entry
					if (paths.add(entry.getPath())){
						result.add(entry);
					}
				}
				else if (entryType == IClasspathEntry.CPE_CONTAINER){
					entry = null;
				}
			}
		}
	}
	
	
//	/**
//	 * @param folders
//	 * @return
//	 * @throws CoreException
//	 */
//	public static LibraryLocation[] compute3rdPartyJarsInPath(String path) throws CoreException{
//		List<LibraryLocation> list = new ArrayList<LibraryLocation>();
//		try {
//			List lines = FileUtils.readLines(new File(path));
//			if(lines != null){
//				Iterator it = lines.iterator();
//				Workspace wp = (Workspace) ResourcesPlugin.getWorkspace();
//				while(it.hasNext()){
//					return JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER"));
////					String line = (String) it.next();
////					File f = new File(line);
////					if(f.exists()){
////						IPath fpath = Path.fromOSString(line);
////						IFile file = (IFile) wp.newResource(fpath, IResource.FILE);
////						list.add(new LibraryLocation(file));
////					}
//				}
//			}
//		} 
//		catch (IOException e) {
//			WtfToolsActivator.getDefault().logError(e);
//		}
//		return list.toArray(new LibraryLocation[0]);
//	}

	public static LibraryLocation[] computeProductJarsInPath(String path) {
		List<LibraryLocation> list = new ArrayList<LibraryLocation>();
		File dir = new File(path);
		if(dir.exists()){
			File[] fs = dir.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar") && !name.endsWith("-sources.jar");
				}
				
			});
			if(fs != null && fs.length > 0){
				Workspace wp = (Workspace) ResourcesPlugin.getWorkspace();
				for(int i = 0; i < fs.length; i ++){
					IPath fpath = Path.fromOSString(fs[i].getAbsolutePath());
					IFile file = (IFile) wp.newResource(fpath, IResource.FILE);
					list.add(new LibraryLocation(file));
				}
			}
		}
		return list.toArray(new LibraryLocation[0]);
	}
}
