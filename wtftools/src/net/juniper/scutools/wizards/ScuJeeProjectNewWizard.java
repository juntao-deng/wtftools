package net.juniper.scutools.wizards;

import java.lang.reflect.InvocationTargetException;

import net.juniper.scutools.ScuToolsActivator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * 
 * @author juntaod
 *
 */
public class ScuJeeProjectNewWizard extends Wizard implements INewWizard {
	private ScuNewNodeProjectPage1 page1;
	private WtfNewProjectPage2 page2;
	private ISelection selection;
	private ScuJeeProjectProvider projectProvider;

	/**
	 * Constructor for WtfProjectNewWizard.
	 */
	public ScuJeeProjectNewWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		page1 = new ScuNewNodeProjectPage1(selection);
		addPage(page1);
		
		page2 = new WtfNewProjectPage2(selection);
		addPage(page2);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		// BasicNewProjectResourceWizard.updatePerspective(fConfig);
        try {
        	projectProvider = new ScuJeeProjectProvider();
        	projectProvider.setProjectName(page1.getProjectName());
        	projectProvider.setProject(page1.getProjectHandle());
        	projectProvider.setLocationPath(page1.getLocationPath());
        	projectProvider.setContext(page2.getContext());
        	projectProvider.setWithHome(page2.isWithHome());
        	projectProvider.setWithEjb(page2.isWithEjb());
       		performBasicOperation();
		} 
        catch (InvocationTargetException e) {
			ScuToolsActivator.getDefault().logError(e);
		} 
        catch (InterruptedException e) {		
        	ScuToolsActivator.getDefault().logError(e);
		}
        return true;
	}
	

	
	private void performBasicOperation() throws InvocationTargetException,
			InterruptedException {
		getContainer().run(false, true, new WtfNewProjectCreationOperation(projectProvider));
	}
	

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}