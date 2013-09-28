package net.juniper.wtftools.wizards;

import java.util.Observable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

//import com.thimda.plugin.proj.WEBProjConstants;
//import com.thimda.plugin.proj.WEBProjPlugin;
//import com.thimda.plugin.util.ProjCoreUtility;

public class WtfNewApplicationPage extends WizardPage
{
	private final class NameGroup extends Observable implements IDialogFieldListener
	{
		protected final StringDialogField fNameField;

		public NameGroup(Composite composite, String initialName)
		{
			final Composite nameComposite = new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false), false));
			nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			// text field for project name
			fNameField = new StringDialogField();
			fNameField.setLabelText("Application Id:");
			setName(initialName);
			fNameField.setDialogFieldListener(this);
			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));
		}

		protected void fireEvent()
		{
			setChanged();
			notifyObservers();
		}

		public String getName()
		{
			return fNameField.getText().trim();
		}

		public void postSetFocus()
		{
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}

		public void setName(String name)
		{
			fNameField.setText(name);
		}

		public void dialogFieldChanged(DialogField field)
		{
			validatePage();
			fireEvent();
		}
	}

	

	private NameGroup fNameGroup;
	private String fInitialName;

	public WtfNewApplicationPage(ISelection selection)
	{
		super("Wtf Project Wizard");
		setTitle("Create a Wtf Project");
		setDescription("Information about the project to be created"); //
		fInitialName = "";
	}
	
	public IWizardPage getNextPage() {
		WtfNewProjectPage2 nextpage = (WtfNewProjectPage2) super.getNextPage();
//		nextpage.getContextPath().setText(getModuleName());
		return nextpage;
	}
	
	public void createControl(Composite parent)
	{
		initializeDialogUnits(parent);
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		fNameGroup = new NameGroup(composite, fInitialName);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	protected GridLayout initGridLayout(GridLayout layout, boolean margins)
	{
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins)
		{
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		}
		else
		{
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}


	public IProject getProjectHandle()
	{
		return ResourcesPlugin.getWorkspace().getRoot().getProject(fNameGroup.getName());
	}


	public String getProjectName()
	{
		return fNameGroup.getName();
	}

	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible)
		{
			fNameGroup.postSetFocus();
		}
	}

//	public String getModuleName()
//	{
//		return fModuleInfoGroup.getModuleName();
//	}
//
//	//    public String getModuleDesc() {
//	//        return fModuleInfoGroup.getModuleDesc();
//	//    }
//	public String getModuleConfig()
//	{
//		return fModuleInfoGroup.getModuleConfig();
//	}

	protected boolean validatePage()
	{
//		if (fInited)
//		{
//			boolean vlidProjectName = validateName(getProjectName(), "Project name must be specified", null);
//			if (!vlidProjectName)
//			{
//				setPageComplete(false);
//				return false;
//			}
//			IPath path = new Path("");
//			if (!path.isValidPath(getLocationPath().toString()))
//			{
//				setErrorMessage("Invalid project contents directory"); //$NON-NLS-1$
//				setPageComplete(false);
//				return false;
//			}
//			boolean validModuleName = validateName(getModuleName(), "Module name can't be null", "Module name must composed by valid characters");
//			if (!validModuleName)
//			{
//				setPageComplete(false);
//				return false;
//			}
//			//            try {
//			//                getPriority();
//			//                setErrorMessage(null);
//			//            } catch(Throwable thr) {
//			//                setErrorMessage("Module Priority must be integer");
//			//                setMessage(null);
//			//                setPageComplete(false);
//			//                return false;
//			//            }
//			String moduleConfig = getModuleConfig();
//			if (moduleConfig != null)
//			{
//				if (!"module.xml".equals(moduleConfig))
//				{
//					String extension = ProjCoreUtility.getExtension(moduleConfig);
//					if (!"module".equals(extension))
//					{
//						setPageComplete(false);
//						setErrorMessage("Module config must with extension '.module' or as name 'module.xml'");
//						return false;
//					}
//					else
//					{
//						boolean validConfigName = validateName(getModuleName(), "Module Config Name can't be null", "Module Config name must composed by valid characters");
//						if (!validConfigName)
//						{
//							setPageComplete(false);
//							return false;
//						}
//					}
//				}
//			}
//			IProject handle = getProjectHandle();
//			if (handle.exists())
//			{
//				setErrorMessage("A project with that name already exists in the workspace.");
//				setPageComplete(false);
//				return false;
//			}
//			setErrorMessage(null);
//			setMessage(null);
//			setPageComplete(true);
//			return true;
//		}
//		setPageComplete(false);
		return false;
	}

	//    public int getPriority() {
	//        return fModuleInfoGroup.getPriority();     
	//    }
	private boolean validateName(String name, String emptyMsg, String errorMsg)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (name.equals(""))
		{
			setErrorMessage(null);
			setMessage(emptyMsg);
			return false;
		}
		IStatus nameStatus = workspace.validateName(name, 0);
		if (!nameStatus.isOK())
		{
			if (errorMsg == null)
				setErrorMessage(nameStatus.getMessage());
			else
				setErrorMessage(errorMsg);
			return false;
		}
		setErrorMessage(null);
		setMessage(null);
		return true;
	}
}
