package net.juniper.scutools.wizards;

import java.util.Observable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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

public class ScuNewApplicationPage extends WizardPage
{
	private NameGroup fNameGroup;
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

	public String getApplicationId() {
		return fNameGroup.getName();
	}
	
	public ScuNewApplicationPage(ISelection selection){
		super("Wtf Application Wizard");
		setTitle("Create a Wtf Application");
		setDescription("Information about the application to be created"); //
	}
	
	public IWizardPage getNextPage() {
		WtfNewProjectPage2 nextpage = (WtfNewProjectPage2) super.getNextPage();
		return nextpage;
	}
	
	public void createControl(Composite parent){
		initializeDialogUnits(parent);
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		fNameGroup = new NameGroup(composite, "");
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	protected GridLayout initGridLayout(GridLayout layout, boolean margins){
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins){
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		}
		else{
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}


	public IProject getProjectHandle(){
		return ResourcesPlugin.getWorkspace().getRoot().getProject(fNameGroup.getName());
	}


	public String getProjectName(){
		return fNameGroup.getName();
	}

	public void setVisible(boolean visible){
		super.setVisible(visible);
		if (visible){
			fNameGroup.postSetFocus();
		}
	}


	protected boolean validatePage(){
		return false;
	}

}
