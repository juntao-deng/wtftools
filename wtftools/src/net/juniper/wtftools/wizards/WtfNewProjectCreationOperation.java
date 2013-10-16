package net.juniper.wtftools.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.juniper.wtftools.core.WtfToolsConstants;
import net.juniper.wtftools.project.ProjCoreUtility;
import net.juniper.wtftools.project.WtfProjectClassPathContainerID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class WtfNewProjectCreationOperation extends WorkspaceModifyOperation
{
	IProjectProvider projectProvider;

	public WtfNewProjectCreationOperation(IProjectProvider provider)
	{
		this.projectProvider = provider;
		
	}

	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException
	{
		monitor.beginTask("Creating Project...", 5);
		monitor.subTask("Creating Project");
		IProject project = createProject();
		monitor.worked(1);

		monitor.subTask("Updating project's classpath...");
		computeInitClasspath(project);
//		monitor.worked(1);
//		monitor.subTask("Create build.properties");
//		ProjCoreUtility.createBuildProperties(project);
		monitor.worked(1);
//		monitor.subTask("Create module configuation file");
//		ProjCoreUtility.ceateInitManifest(project);
//		monitor.worked(1);
		
//		final String context = projectProvider.getContext();
//		final String docbase = projectProvider.getProject().getLocation().toString() + "/web";
//		System.setProperty("context", context);
//		boolean bl = writeConf(context, docbase);
//		try {
//			if(bl && createRsdFolder(docbase) && addRsdWebContextToProperty() && writeModuleToProperties()){
//				project.refreshLocal(IProject.DEPTH_INFINITE, null);
//			}
//		} catch (Exception e) {
//			WEBProjPlugin.getDefault().logError(e);
//		}
	}
	

//	/**
// 	 * 生成Context文件
// 	 * @param contex
// 	 * @param docbase
// 	 * @return
// 	 */
// 	private boolean writeConfiguration(String contex, String docbase){
// 		String filePath = getContextFilePath(contex);
// 		//ByteArrayInputStream stream = new ByteArrayInputStream(buffer.toString().getBytes());
// 		File file = new File(filePath);
// 		try{
// 			if (file.exists()){
// 				if (!MessageDialog.openQuestion(JavaPlugin.getActiveWorkbenchShell(), "信息", "web工程注册文件已经存在，是否覆盖？"))
// 					return true;
// 				//file.setContents(stream, true, true, null);
// 				
// 			}
// 			StringBuffer buffer = new StringBuffer();
// 			buffer.append("<!--\n");
// 			buffer.append("    Context configuration file for the ");
// 			buffer.append(contex);
// 			buffer.append("\n-->\n\n");
// 			buffer.append("<Context path=\"/");
// 			buffer.append(contex);
// 			buffer.append("\" docBase=\"");
// 			buffer.append(docbase);
// 			buffer.append("\" privileged=\"true\" antiResourceLocking=\"false\" antiJARLocking=\"false\" useNaming=\"false\" override=\"true\"  cachingAllowed=\"false\">\n");
// 			buffer.append("\n    <WatchedResource>WEB-INF/web.xml</WatchedResource>\n\n</Context>\n");
// 			FileUtilities.saveFile(filePath, buffer.toString().getBytes());
// 		}
// 		catch(Exception e){
// 			WEBProjPlugin.getDefault().logError(e);
// 			return false;
// 		}
// 		return true;
// 	}
// 	
// 	/**
// 	 * 得到context.xml文件路径
// 	 * @param contex
// 	 * @return
// 	 */
//	private String getContextFilePath(String contex) {
//		String filePath = "";
//		IPath tomcatPath = WEBProjPlugin.getTomcatHome();
//		filePath = tomcatPath.toString().concat(CTX_FILE_PATH.concat(contex)).concat(".xml");
//		return filePath;
//	}

	private IProject createProject() throws CoreException
	{
		IProject project = projectProvider.getProject();
		if (!project.exists())
		{
			ProjCoreUtility.createProject(project, projectProvider.getLocationPath(), null);
			project.open(null);
		}
		List<String> natures = new ArrayList<String>();
		natures.add(JavaCore.NATURE_ID);
		natures.add(WtfToolsConstants.WTF_NATURE_ID);
//		if (!project.hasNature(JavaCore.NATURE_ID))
//		{
//			ProjCoreUtility.addNatureToProject(project, JavaCore.NATURE_ID, null);
//		}
//		
//		if (!project.hasNature(WtfToolsConstants.WTF_NATURE_ID))
		ProjCoreUtility.addNatureToProject(project, natures.toArray(new String[0]), null);

		return project;
	}

	private void computeInitClasspath(IProject project) throws CoreException
	{
		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		list.add(ProjCoreUtility.createSourceEntry(project, projectProvider.getSrc(), projectProvider.getSrcOut()));
//		list.add(ProjCoreUtility.createSourceEntry(project, fProjectProvider.getPrivateSrc(), fProjectProvider.getPrivateOut()));
//		list.add(ProjCoreUtility.createSourceEntry(project, fProjectProvider.getClientSrc(), fProjectProvider.getClientOut()));
//		//list.add(CoreUtility.createSourceEntry(project, fProjectProvider.getGenSrc(), fProjectProvider.getGenOut()));
//		list.add(ProjCoreUtility.createSourceEntry(project, fProjectProvider.getResources(), fProjectProvider.getResourcesOut()));
//		list.add(ProjCoreUtility.createSourceEntry(project, fProjectProvider.getTestSrc(), fProjectProvider.getTestOut()));
		//
		list.add(ProjCoreUtility.createJREEntry());
		for (WtfProjectClassPathContainerID id : WtfProjectClassPathContainerID.values()){
			list.add(ProjCoreUtility.createContainerClasspathEntry(id));	
		}
//		list.addAll(Arrays.asList(ProjCoreUtility.createLibEntries()));
		
		IJavaProject javaProject = JavaCore.create(project);
		javaProject.setRawClasspath(list.toArray(new IClasspathEntry[0]), null);
	}
}