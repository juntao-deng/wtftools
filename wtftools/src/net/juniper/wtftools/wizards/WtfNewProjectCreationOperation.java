package net.juniper.wtftools.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;
import net.juniper.wtftools.core.WtfToolsConstants;
import net.juniper.wtftools.project.ClasspathComputer;
import net.juniper.wtftools.project.ProjCoreUtility;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFolder;
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

		writeConfigFiles(project);
		
		monitor.subTask("Updating project's classpath...");
		computeInitClasspath(project, monitor);
		addApplicationFiles(project);
		monitor.worked(1);
		
		project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
	}
	


	private void addApplicationFiles(IProject project) {
		writeWebConfig(project);
		if(true){
			writeJpaConfig(project);
		}
		if(true){
			copyHomeApplication(project);
		}
	}

	private void writeWebConfig(IProject project) {
		String webLocation = WtfProjectCommonTools.getFrameworkWebLocation();
		try {
			String projectPath = project.getLocation().toOSString();
			FileUtils.copyDirectory(new File(webLocation + "/init/copy/WEB-INF"), new File(projectPath + "/web/WEB-INF"));
			if(true/*with home*/)
				FileUtils.copyDirectory(new File(webLocation + "/init/copy/src"), new File(projectPath + "/src"));
			if(true/*with jpa*/)
				FileUtils.copyDirectory(new File(webLocation + "/init/copy/jpares"), new File(projectPath + "/src/resources"));
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
		}
	}

	private void copyHomeApplication(IProject project) {
		String fwLocation = WtfProjectCommonTools.getFrameworkWebLocation() + "/";
		try {
			String projectPath = project.getLocation().toOSString();
			String projectLocation = projectPath + "/web/";
			FileUtils.copyFile(new File(fwLocation + "index.html"), new File(projectLocation + "index.html"));
			FileUtils.copyFile(new File(fwLocation + "rest/homeinfos.js"), new File(projectLocation + "rest/homeinfos.js"));
			FileUtils.copyFile(new File(fwLocation + "rest/dashboard.js"), new File(projectLocation + "rest/dashboard.js"));
			FileUtils.copyDirectory(new File(fwLocation + "applications/home"), new File(projectLocation + "applications/home"));
			FileUtils.copyDirectory(new File(fwLocation + "applications/dashboard"), new File(projectLocation + "applications/dashboard"));
			FileUtils.copyDirectory(new File(fwLocation + "configuration"), new File(projectLocation + "configuration"));
			updateFileContent(fwLocation, projectLocation);
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
		}
	}

	private void updateFileContent(String webLocation, String projectPath) {
		try {
			String indexContent = FileUtils.readFileToString(new File(projectPath + "index.html"));
			indexContent = indexContent.replace("#CTX#", projectProvider.getContext());
			FileUtils.writeStringToFile(new File(projectPath + "index.html"), indexContent);
			
			String restContent = FileUtils.readFileToString(new File(projectPath + "rest/homeinfos.js"));
			restContent = restContent.replaceAll("#CTX#", projectProvider.getContext());
			FileUtils.writeStringToFile(new File(projectPath + "rest/homeinfos.js"), restContent);
			
			
			String configContent = FileUtils.readFileToString(new File(projectPath + "configuration/context.js"));
			configContent = configContent.replaceAll("#FRAME_PATH#", webLocation);
			configContent = configContent.replace("#CTX#", projectProvider.getContext());
			configContent = configContent.replace("#CTXPATH#", projectPath);
			configContent = configContent.replace("#FRMCTX#", "wtf");
			FileUtils.writeStringToFile(new File(projectPath + "configuration/context.js"), configContent);
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
		}
	}

	private void writeJpaConfig(IProject project) {
		
	}

	private void writeConfigFiles(IProject project) {
		if(WtfProjectCommonTools.isTomcat()){
			writeTomcatConfig(project);
		}
	}

	private void writeTomcatConfig(IProject project) {
		TomcatHelper.writeConfigFile(project.getLocation().toOSString(), projectProvider.getContext());
		TomcatHelper.writeContextFile(projectProvider.getContext(), project.getFullPath().append("web").toString());
	}

	private IProject createProject() throws CoreException
	{
		IProject project = projectProvider.getProject();
		if (!project.exists()){
			ProjCoreUtility.createProject(project, projectProvider.getLocationPath(), null);
			project.open(null);
		}
		List<String> natures = new ArrayList<String>();
		natures.add(JavaCore.NATURE_ID);
		natures.add(WtfToolsConstants.WTF_NATURE_ID);
		if(WtfProjectCommonTools.isTomcat()){
			natures.add(WtfToolsConstants.TOMCAT_NATURE_ID);
		}
		
		ProjCoreUtility.addNatureToProject(project, natures.toArray(new String[0]), null);
		return project;
	}

	private void computeInitClasspath(IProject project, IProgressMonitor monitor) throws CoreException{
		IJavaProject javaProject = JavaCore.create(project);
		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		list.add(ProjCoreUtility.createSourceEntry(project, "src/restapis"));
		list.add(ProjCoreUtility.createSourceEntry(project, "src/services"));
		list.add(ProjCoreUtility.createSourceEntry(project, "src/implements"));
		list.add(ProjCoreUtility.createSourceEntry(project, "src/resources"));
		javaProject.setRawClasspath(list.toArray(new IClasspathEntry[0]), null);
		
		IFolder folder = project.getFolder("web");
		ProjCoreUtility.createFolder(folder);
		
		IFolder apps = project.getFolder("web/applications");
		ProjCoreUtility.createFolder(apps);
		
		IFolder rest = project.getFolder("web/rest");
		ProjCoreUtility.createFolder(rest);

		ClasspathComputer.updateClasspath(project, monitor);
	}
}