package net.juniper.scutools.wizards;

import java.io.File;
import java.io.IOException;

import net.juniper.scutools.ScuToolsActivator;
import net.juniper.scutools.common.ScuProjectCommonTools;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class WtfApplicationNewWizard extends Wizard implements INewWizard {
	private WtfNewApplicationPage page1;
	private ISelection selection;

	/**
	 * Constructor for WtfProjectNewWizard.
	 */
	public WtfApplicationNewWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page1 = new WtfNewApplicationPage(selection);
		addPage(page1);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		if(ScuProjectCommonTools.getCurrentWtfProject() == null){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Please select a wtf project first");
			return false;
		}
		String appId = page1.getApplicationId();
		if(appId == null || appId.equals("")){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Application id can not be null");
			return false;
		}
		String path = ScuProjectCommonTools.getCurrentProject().getLocation().toString();
		String dir = path + "/web/applications/" + appId;
		File f = new File(dir);
		if(f.exists()){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Application already exsits");
			return false;
		}
		String leafId = appId;
		int index = appId.lastIndexOf("/");
		if(index != -1){
			leafId = appId.substring(index + 1);
		}
		try {
			createApplication(f, leafId);
		} 
		catch (IOException e) {
			ScuToolsActivator.getDefault().logError(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Create application error");
			return false;
		}
		
		try {
			IFile file = ScuProjectCommonTools.getCurrentProject().getFile("web/applications/" + appId + "/" + leafId + ".html");
			IEditorInput input = new FileEditorInput(file);
			ScuProjectCommonTools.getCurrentProject().refreshLocal(IProject.DEPTH_INFINITE, null);
			
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, "net.juniper.wtftools.editor.BrowserDesignEditor");
		} 
		catch (Exception e) {
			ScuToolsActivator.getDefault().logError(e);
		}
		
        return true;
	}
	
	private void createApplication(File dir, String appId) throws IOException {
		dir.mkdirs();
		File f = new File(dir.getAbsolutePath() + "/" + appId + ".html");
		f.createNewFile();
		
		
		File modelFile = new File(dir.getAbsolutePath() + "/model.js");
		FileUtils.writeStringToFile(modelFile, "wdefine(function(){\n});");
		
		File controllerFile = new File(dir.getAbsolutePath() + "/controller.js");
		FileUtils.writeStringToFile(controllerFile, "wdefine(function(){\n});");
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}