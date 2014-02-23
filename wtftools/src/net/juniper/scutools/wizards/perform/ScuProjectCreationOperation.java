package net.juniper.scutools.wizards.perform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.juniper.scutools.ScuToolsActivator;
import net.juniper.scutools.common.ScuProjectCommonTools;
import net.juniper.scutools.common.ScuToolsConstants;
import net.juniper.scutools.project.ProjCoreUtility;
import net.juniper.scutools.wizards.ScuNodeProjectProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ScuProjectCreationOperation extends WorkspaceModifyOperation{
	private ScuNodeProjectProvider projectProvider;

	public ScuProjectCreationOperation(ScuNodeProjectProvider provider){
		this.projectProvider = provider;
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException{
//		String location = WtfProjectCommonTools.getFrameworkLocation();
//		if(location == null || location.equals("")){
//			MessageDialog.openError(Display.getCurrent().getActiveShell(), "No scubase project", "Please add scubase project first");
//			return;
//		}
		
		monitor.beginTask("Creating Project...", 5);
		monitor.subTask("Creating Project");
		IProject project = createProject(monitor);
		project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
	}

	private void addApplicationFiles(IProject project) {
		if(projectProvider.isWithHome()){
			String fwLocation = ScuProjectCommonTools.getFrameworkWebLocation() + "/";
			try {
				String projectPath = project.getLocation().toPortableString();
				String projectLocation = projectPath + "/web/";
				FileUtils.copyFile(new File(fwLocation + "index.html"), new File(projectLocation + "index.html"));
//				FileUtils.copyFile(new File(fwLocation + "rest/homeinfos.js"), new File(projectLocation + "rest/homeinfos.js"));
//				FileUtils.copyFile(new File(fwLocation + "rest/dashboard.js"), new File(projectLocation + "rest/dashboard.js"));
				FileUtils.copyDirectory(new File(fwLocation + "applications"), new File(projectLocation + "applications"));
				FileUtils.copyDirectory(new File(fwLocation + "widgets"), new File(projectLocation + "widgets"));
				FileUtils.copyDirectory(new File(fwLocation + "templates"), new File(projectLocation + "templates"));
				FileUtils.copyDirectory(new File(fwLocation + "rest"), new File(projectLocation + "rest"));//			FileUtils.copyDirectory(new File(fwLocation + "applications/dashboard"), new File(projectLocation + "applications/dashboard"));
				FileUtils.copyDirectory(new File(fwLocation + "configuration"), new File(projectLocation + "configuration"));
				updateFileContent(fwLocation, projectLocation);
				
				
				InputStream input1 = null;
				OutputStream output1 = null;
				try{
					input1 = ScuToolsActivator.class.getClassLoader().getResourceAsStream("resource/src/config.js");
					output1 = new FileOutputStream(new File(projectPath + "/src/config.js"));
					IOUtils.copy(input1, output1);
				} 
				catch (Exception e) {
					ScuToolsActivator.getDefault().logError(e);
				}
				finally{
					if(input1 != null)
						IOUtils.closeQuietly(input1);
					if(output1 != null)
						IOUtils.closeQuietly(output1);
				}
			} 
			catch (IOException e) {
				ScuToolsActivator.getDefault().logError(e);
			}
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
			ScuToolsActivator.getDefault().logError(e);
		}
	}

//	private void writeJpaConfig(IProject project) {
//		
//	}
//
	private void writeProjectConfig(IProject project) {
		String projectPath = project.getLocation().toPortableString();
		File settingsDir = new File(projectPath + "/.settings");
		if(!settingsDir.exists())
			settingsDir.mkdirs();
		InputStream input1 = null;
		OutputStream output1 = null;
		try{
			input1 = ScuToolsActivator.class.getClassLoader().getResourceAsStream("resource/jsdt_settings/jsdtscope");
			output1 = new FileOutputStream(new File(projectPath + "/.settings/.jsdtscope"));
			IOUtils.copy(input1, output1);
		} 
		catch (Exception e) {
			ScuToolsActivator.getDefault().logError(e);
		}
		finally{
			if(input1 != null)
				IOUtils.closeQuietly(input1);
			if(output1 != null)
				IOUtils.closeQuietly(output1);
		}
		
		InputStream input2 = null;
		OutputStream output2 = null;
		try{
			input2 = ScuToolsActivator.class.getClassLoader().getResourceAsStream("resource/jsdt_settings/org.eclipse.wst.jsdt.ui.superType.container");
			output2 = new FileOutputStream(new File(projectPath + "/.settings/org.eclipse.wst.jsdt.ui.superType.container"));
			IOUtils.copy(input2, output2);
		} 
		catch (Exception e) {
			ScuToolsActivator.getDefault().logError(e);
		}
		finally{
			if(input2 != null)
				IOUtils.closeQuietly(input2);
			if(output1 != null)
				IOUtils.closeQuietly(output2);
		}
		
		InputStream input3 = null;
		OutputStream output3 = null;
		try{
			input3 = ScuToolsActivator.class.getClassLoader().getResourceAsStream("resource/jsdt_settings/org.eclipse.wst.jsdt.ui.superType.name");
			output3 = new FileOutputStream(new File(projectPath + "/.settings/org.eclipse.wst.jsdt.ui.superType.name"));
			IOUtils.copy(input3, output3);
		} 
		catch (Exception e) {
			ScuToolsActivator.getDefault().logError(e);
		}
		finally{
			if(input3 != null)
				IOUtils.closeQuietly(input3);
			if(output1 != null)
				IOUtils.closeQuietly(output3);
		}
	}
	
	private IProject createProject(IProgressMonitor monitor) throws CoreException{
		IProject project = projectProvider.getProject();
		if (!project.exists()){
			IPath projPath = projectProvider.getLocationPath();
			if(!ScuProjectCommonTools.getWorkspaceDirPath().equals(projPath.toOSString()))
				projPath = projPath.append(projectProvider.getProjectName());
			ProjCoreUtility.createProject(project, projPath, null);
			project.open(null);
		}
		List<String> natures = new ArrayList<String>();
//		natures.add(ScuToolsConstants.SCU_JS_NATURE_ID);
		natures.add(ScuToolsConstants.SCU_NATURE_ID);
		
		ProjCoreUtility.addNatureToProject(project, natures.toArray(new String[0]), null);
		
		writeProjectConfig(project);
		createStandardDirs(project, monitor);
		
		monitor.worked(1);

		monitor.subTask("Updating project's classpath...");
		
		addApplicationFiles(project);
		monitor.worked(1);
		
		return project;
	}

	private void createStandardDirs(IProject project, IProgressMonitor monitor) throws CoreException{
		
		IFolder src = project.getFolder("src");
		ProjCoreUtility.createFolder(src);
		
		IFolder restSrc = project.getFolder("src/rest");
		ProjCoreUtility.createFolder(restSrc);
		
		IFolder serviceSrc = project.getFolder("src/service");
		ProjCoreUtility.createFolder(serviceSrc);
		
		IFolder shareSrc = project.getFolder("src/share");
		ProjCoreUtility.createFolder(shareSrc);
		
		IFolder privateSrc = project.getFolder("src/private");
		ProjCoreUtility.createFolder(privateSrc);
		
		IFolder folder = project.getFolder("web");
		ProjCoreUtility.createFolder(folder);
		
		IFolder apps = project.getFolder("web/applications");
		ProjCoreUtility.createFolder(apps);
		
		IFolder rest = project.getFolder("web/rest");
		ProjCoreUtility.createFolder(rest);
		
		IFolder widgets = project.getFolder("web/widgets");
		ProjCoreUtility.createFolder(widgets);
		
		IFolder templates = project.getFolder("web/templates");
		ProjCoreUtility.createFolder(templates);

	}
}