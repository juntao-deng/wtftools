package net.juniper.wtftools.project;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import net.juniper.wtftools.core.WtfToolsConstants;

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

public class ClasspathComputer{
//	private static final List<String> tomcatLibList = new ArrayList<String>();
//	static{
//		tomcatLibList.add("jsp-api.jar");
//		tomcatLibList.add("servlet-api.jar");
//		tomcatLibList.add("el-api.jar");
//	}
//	
	public static void updateClasspath(IProject project, IProgressMonitor monitor) throws CoreException{
		if (project != null && project.hasNature(WtfToolsConstants.WTF_NATURE_ID)){
			monitor.subTask("Update classpath");
			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] entries = getClasspath(project, false);
			javaProject.setRawClasspath(entries, monitor);
		}
	}
////
////	public static LFWLibraryLocation[] computeStandCP(IFolder... baseFolders) throws CoreException
////	{
////		ArrayList<LFWLibraryLocation> llist = new ArrayList<LFWLibraryLocation>();
////		for (IFolder folder : baseFolders)
////		{
////			IFolder classes = folder.getFolder(ClasspathConstants.CLASSES);
////			IFolder resources = folder.getFolder(ClasspathConstants.RESOURCES);
////			if (classes.exists())
////			{
////				llist.add(new LFWLibraryLocation(classes));
////			}
////			if (resources.exists())
////			{
////				//llist.addAll(Arrays.asList(computeJarsInPath(resources)));
////				llist.add(new LFWLibraryLocation(resources));
////			}
////			//
////			IFolder varclass = folder.getFolder(ClasspathConstants.VAR_CLASSES);
////			if (varclass.exists())
////			{
////				llist.add(new LFWLibraryLocation(varclass));
////			}
////			//
////			IFolder extclass = folder.getFolder(ClasspathConstants.EXTENSION_CLASSES);
////			if (extclass.exists())
////			{
////				llist.add(new LFWLibraryLocation(extclass));
////			}
////			llist.addAll(Arrays.asList(computeJarsInPath(folder.getFolder(ClasspathConstants.LIB))));
////		}
////		return llist.toArray(new LFWLibraryLocation[0]);
////	}
//
	private static IClasspathEntry[] getClasspath(IProject project, boolean clear) throws CoreException{
		IJavaProject javaproject = JavaCore.create(project);
		ArrayList<IClasspathEntry> result = new ArrayList<IClasspathEntry>();
		addSourceAndLibraries(javaproject, clear, result);
		result.add(ProjCoreUtility.createJREEntry());
		for (WtfProjectClassPathContainerID id : WtfProjectClassPathContainerID.values()){
//			ClasspathContainer container = ProjCoreUtility.getLFWClasspathContainer(id.getPath(), javaproject);
//			container.setClasspathEntries(ProjCoreUtility.getClasspathEntry(project, id));
			result.add(ProjCoreUtility.createContainerClasspathEntry(id));
//			result.addAll(Arrays.asList(ProjCoreUtility.createLibEntries()));
		}
		
		IClasspathEntry[] entries = result.toArray(new IClasspathEntry[result.size()]);
		IJavaModelStatus validation = JavaConventions.validateClasspath(javaproject, entries, javaproject.getOutputLocation());
		if (!validation.isOK()){
			throw new CoreException(validation);
		}
		return (IClasspathEntry[]) result.toArray(new IClasspathEntry[result.size()]);
	}

	private static void addSourceAndLibraries(IJavaProject project, boolean clear, ArrayList<IClasspathEntry> result) throws CoreException
	{
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
//		if (resEntry == null)
//			resEntry = ProjCoreUtility.createSourceEntry(project.getProject(), "resources", "out/resources");
//		result.add(resEntry);
	}
	
	/**
	 * get jboss library list
	 * @param folders
	 * @return
	 * @throws CoreException
	 */
	public static LibraryLocation[] computeJbossJarsInPath(IPath path) throws CoreException{
		List<LibraryLocation> list = new ArrayList<LibraryLocation>();
//		LibraryLocation[] locations = computeJarsInPath(path);
//		for (int i = 0; i < locations.length; i++) {
//			LibraryLocation loc = locations[i];
//			String jarName = loc.getLibPath().lastSegment();
//			if(isTomcatLib(jarName)){
//				list.add(loc);
//			}
//		}
		IPath fpath = Path.fromOSString("D:/devtemp/a.jar");
		Workspace wp = (Workspace) ResourcesPlugin.getWorkspace();
		IFile file = (IFile) wp.newResource(fpath, IResource.FILE);
		LibraryLocation lib = new LibraryLocation(file);
		list.add(lib);
		return list.toArray(new LibraryLocation[0]);
	}
	
	/**
	 * 获得RSD jar文件列表
	 * @param folders
	 * @return
	 * @throws CoreException
	 */
	public static LibraryLocation[] compute3rdPartyJarsInPath(IPath path) throws CoreException{
		List<LibraryLocation> list = new ArrayList<LibraryLocation>();
//		LibraryLocation[] locations = computeJarsInPath(path);
//		for (int i = 0; i < locations.length; i++) {
//			LibraryLocation loc = locations[i];
//			String jarName = loc.getLibPath().lastSegment();
//			if(isRSDLib(jarName)){
//				list.add(loc);
//			}
//		}
		return list.toArray(new LibraryLocation[0]);
	}
//	
//	/**
//	 * 获得第三方 jar文件列表
//	 * @param folders
//	 * @return
//	 * @throws CoreException
//	 */
//	public static LibraryLocation[] computeExternalJarsInPath(IPath path) throws CoreException{
//		LibraryLocation[] locations = computeJarsInPath(path);
//		List<LibraryLocation> list = new ArrayList<LibraryLocation>();
//		for (int i = 0; i < locations.length; i++) {
//			LibraryLocation loc = locations[i];
//			String jarName = loc.getLibPath().lastSegment();
//			if(!isRSDLib(jarName) && !isTomcatLib(jarName)){
//				list.add(loc);
//			}
//		}
//		return list.toArray(new LibraryLocation[0]);
//	}
//
//	private static boolean isRSDLib(String jarName) {
//		if(jarName.startsWith("rsd_") && !jarName.endsWith("_src.jar")){
//			return true;
//		}
//		return false;
//	}
//
//	private static boolean isTomcatLib(String jarName) {
//		if(jarName.startsWith("tomcat") || jarName.startsWith("catalina") || tomcatLibList.contains(jarName)){
//			return true;
//		}
//		return false;
//	}
//
	private static LibraryLocation[] computeJarsInPath(IPath path) throws CoreException{
		File dir = new File(path.toString());

		List<LibraryLocation> filterList = new ArrayList<LibraryLocation>();
		if (dir.exists()){
			File[] fs = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			});
			
			
			for(int i = 0; i < fs.length; i ++){
				Workspace wp = (Workspace) ResourcesPlugin.getWorkspace();
				IPath fpath = new Path(fs[i].getAbsolutePath());
				IFile file = (IFile) wp.newResource(fpath, IResource.FILE);
				filterList.add(new LibraryLocation(file));
			}
		}
			
		LibraryLocation[] rets = filterList.toArray(new LibraryLocation[0]);
		Arrays.sort(rets, new Comparator<LibraryLocation>(){
			public int compare(LibraryLocation o1, LibraryLocation o2)
			{
				return o1.getLibResource().getName().compareToIgnoreCase(o2.getLibResource().getName());
			}
		});
		return rets;
	}
}
