package net.juniper.scutools.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.juniper.scutools.ScuToolsActivator;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

@SuppressWarnings("restriction")
public final class ScuProjectCommonTools {
private static final String SCU_BASE = "scubase";
		public static final String LOCATION_KEY = "framework_location_key";
	private static IProject currProject;
	public static IPath getJbossHome(){
		String jbossHome = System.getenv("JBOSS_HOME");
		ScuToolsActivator.getDefault().logInfo("get jboss home path:" + jbossHome);
		return Path.fromOSString(jbossHome);
	}
	
	public static IPath getNodeHome() {
		IPreferenceStore store = ScuToolsActivator.getDefault().getPreferenceStore();
		String location = store.getString(ScuProjectCommonTools.LOCATION_KEY);
		if(location == null || location.equals("")){
			location = System.getenv("NODE_HOME");
		}
		if(location == null || !isValidLocation(location))
			return null;
		return Path.fromOSString(location);
	}
	
	public static String getDeploymentPath() {
		IPath deployPath = getJbossHome();
		if(deployPath != null){
			String deployPathStr = deployPath.toOSString();
			String basePath = deployPathStr + "/standalone/tmp/vfs";
			String path = getExistsPath(basePath);
			if(path == null){
				basePath = deployPathStr + "/domain/tmp/vfs";
				path = getExistsPath(basePath);
			}
			return path;
		}
		return null;
	}
	
	private static String getExistsPath(String basePath) {
		File vfsDir = new File(basePath);
		File[] fs = vfsDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("deployment");
			}
		});
		for(int i = 0; i < fs.length; i ++){
			File f = fs[i];
			if(f.isDirectory()){
				int length = f.list(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.startsWith("wtfbase.war");
					}
				}).length;
				if(length > 0)
					return f.getAbsolutePath();
			}
		}
		return null;
	}

	public static String getTomcatHome() {
//		String dir =  TomcatLauncherPlugin.getDefault().getTomcatDir();
//		if(dir == null)
//			return "";
//		return dir;
		return "";
	}
	
	public static IProject getCurrentProject() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    
		 ISelection selection = selectionService.getSelection();
		 IProject project = null;
		 if(selection instanceof IStructuredSelection) {    
             Object element = ((IStructuredSelection)selection).getFirstElement();   
			 if (element instanceof IResource) {    
	             project= ((IResource)element).getProject();    
	         }
			 else if (element instanceof PackageFragmentRootContainer) {    
	             IJavaProject jProject = ((PackageFragmentRootContainer)element).getJavaProject();    
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
			if(proj.hasNature(ScuToolsConstants.SCU_NATURE_ID)){
				return proj;
			}
		} catch (CoreException e) {
			ScuToolsActivator.getDefault().logError(e);
		}
		return null;
	}
	
	public static IProject getFrameworkProject() {
		IProject[] projs = getJavaProjects();
		for(int i = 0; i < projs.length; i ++){
			IProject proj = projs[i];
			if(proj.getName().equals(SCU_BASE))
				return proj;
		}
		return null;
	}
	
	public static String getFrameworkLocation() {
//		IPreferenceStore store = WtfToolsActivator.getDefault().getPreferenceStore();
//		String location = store.getString(LOCATION_KEY);
//		if(location == null || location.equals("")){
//			location = detectWtfLocation();
//		}
//		return location;
		IProject proj = getFrameworkProject();
		return proj == null ? null : proj.getLocation().toPortableString();
	}
	

	public static String getFrameworkWebLocation() {
		return getFrameworkLocation() + "/web";
	}
	
	private static Map<IProject, URLClassLoader> loaderMap = new HashMap<IProject, URLClassLoader>();
	public static ClassLoader getCurrentProjectClassLoader() {
		IProject project = getCurrentProject();
		if(project == null)
			return null;
		URLClassLoader loader = loaderMap.get(project);
		if(loader == null){
			ArrayList<URL> allUrls = new ArrayList<URL>();
			IJavaProject elementJavaProject = JavaCore.create(project);
			if (elementJavaProject != null) {
				try {
					String[] classPathArray = JavaRuntime.computeDefaultRuntimeClassPath(elementJavaProject);
					for (int i = 0; i < classPathArray.length; i++) {
						File file = new File(classPathArray[i]);
						System.out.println(classPathArray[i]);
						allUrls.add(file.toURL());
					}
				} 
				catch (Exception e) {
					ScuToolsActivator.getDefault().logError(e);
				}
			}
			loader = new URLClassLoader(allUrls.toArray(new URL[0]), ScuProjectCommonTools.class.getClassLoader());
			loaderMap.put(project, loader);
		}
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
	
	public static String getWorkspaceDirPath() {
		IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		String strPath = path.toOSString();
		return strPath;
	}
	
	public static IProject[] getJavaProjects() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		return projects;
	}
	
	/**
	 * checkout files
	 */
	 public static void checkOutFile(String path){
		IPath ph = new Path(path);
		IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(ph);
		File filea = new File(path);
		IWorkbenchPart part = null;
		Shell shell = null;
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null)
			part = page.getActivePart();
		if(part != null)
			shell = part.getSite().getShell();
		IStatus statu = ResourcesPlugin.getWorkspace().validateEdit(new IFile[]{ifile}, shell);
		if(!filea.canWrite() && !statu.isOK()){
			boolean isWritable = MessageDialog.openConfirm(null, "Warning", "Make file writable?");
			if(isWritable){
				try {
					silentSetWriterable(path);
				} 
				catch (Exception e) {
					ScuToolsActivator.getDefault().logError(e.getMessage());
					MessageDialog.openInformation(null, "Warning", e.getMessage());
				}
			}
		}
	 }
	 
	/**
	 * Make file writable
	 * @param filename
	 * @throws CoreException
	 */
	public static void silentSetWriterable(String filename) throws CoreException {
	     IFileInfo fileinfo = new FileInfo(filename);
	     fileinfo.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
	     IFileSystem fs = EFS.getLocalFileSystem();
	     IFileStore store = fs.fromLocalFile(new File(filename));
	     store.putInfo(fileinfo, EFS.SET_ATTRIBUTES, null);
	 }
	
	public static String getMiddleware() {
		return "jboss";
	}
	
	public static boolean isTomcat() {
		return getMiddleware().equals("tomcat");
	}

	public static String getCurrentProjectCtx() {
		String str;
		try {
			str = FileUtils.readFileToString(new File(getCurrentProject().getFile(".wtf_project").getLocation().toString()));
			int begin = str.indexOf("<webPath>") + "<webPath>".length() + 1;
			int end = str.indexOf("</webPath>");
			String ctx = str.substring(begin, end);
			return ctx;
		} catch (IOException e) {
			ScuToolsActivator.getDefault().logError(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Get context path error");
			return null;
		}
	}

	public static boolean isValidLocation(String str) {
		if(str == null || str.equals(""))
			return false;
		try{
			File f = new File(str);
			if(!f.exists() || !f.isDirectory())
				return false;
			String[] fs = f.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("npm");
				}
			});
			if(fs.length < 1)
				return false;
			fs = f.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("node");
				}
			});
			if(fs.length < 1)
				return false;
		}
		catch(Exception e){
			ScuToolsActivator.getDefault().logError(e);
			return false;
		}
		return true;
	}
}
