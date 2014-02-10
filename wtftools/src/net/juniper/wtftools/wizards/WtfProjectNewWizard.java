package net.juniper.wtftools.wizards;

import java.lang.reflect.InvocationTargetException;

import net.juniper.wtftools.WtfToolsActivator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
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
public class WtfProjectNewWizard extends Wizard implements INewWizard {
	private WtfNewProjectPage1 page1;
	private WtfNewProjectPage2 page2;
	private ISelection selection;
	private IProjectProvider projectProvider;

	/**
	 * Constructor for WtfProjectNewWizard.
	 */
	public WtfProjectNewWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		page1 = new WtfNewProjectPage1(selection);
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
        	projectProvider = new IProjectProvider() {
        		@Override
                public String getProjectName() {
                    return page1.getProjectName();
                }
        		@Override
                public IProject getProject() {
                    return page1.getProjectHandle();
                }
        		@Override
                public IPath getLocationPath() {
                    return page1.getLocationPath();
                }

				@Override
				public String getContext() {
					return page2.getContext();
				}

				@Override
				public boolean isWithEjb() {
					return page2.isWithJpa();
				}

				@Override
				public boolean isWithHome() {
					return page2.isWithHome();
				}
            };
       		performBasicOperation();
		} 
        catch (InvocationTargetException e) {
			WtfToolsActivator.getDefault().logError(e);
		} 
        catch (InterruptedException e) {		
        	WtfToolsActivator.getDefault().logError(e);
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