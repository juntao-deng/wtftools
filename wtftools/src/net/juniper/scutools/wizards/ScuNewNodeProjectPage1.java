package net.juniper.scutools.wizards;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;

@SuppressWarnings("restriction")
public class ScuNewNodeProjectPage1 extends WizardPage{
	private NameGroup fNameGroup;
	private LocationGroup fLocationGroup;
	private String fInitialName;
	
	private final class NameGroup extends Observable implements IDialogFieldListener{
		protected final StringDialogField fNameField;

		public NameGroup(Composite composite, String initialName){
			final Composite nameComposite = new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false), false));
			nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			// text field for project name
			fNameField = new StringDialogField();
			fNameField.setLabelText("&Project name:");
			setName(initialName);
			fNameField.setDialogFieldListener(this);
			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));
		}

		protected void fireEvent(){
			setChanged();
			notifyObservers();
		}

		public String getName(){
			return fNameField.getText().trim();
		}

		public void postSetFocus(){
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}

		public void setName(String name){
			fNameField.setText(name);
		}

		@Override
		public void dialogFieldChanged(DialogField field){
			validatePage();
			fireEvent();
		}
	}


	private final class LocationGroup extends Observable implements Observer, IStringButtonAdapter, IDialogFieldListener{
		protected final SelectionButtonDialogField fWorkspaceRadio;
		protected final SelectionButtonDialogField fExternalRadio;
		protected final StringButtonDialogField fLocation;
		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = JavaUI.ID_PLUGIN + ".last.external.project"; //$NON-NLS-1$

		public LocationGroup(Composite composite){
			final int numColumns = 3;
			final Group group = new Group(composite, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setLayout(initGridLayout(new GridLayout(numColumns, false), true));
			group.setText("Location"); //$NON-NLS-1$
			fWorkspaceRadio = new SelectionButtonDialogField(SWT.RADIO);
			fWorkspaceRadio.setLabelText("Create project in &workspace"); //$NON-NLS-1$
			fExternalRadio = new SelectionButtonDialogField(SWT.RADIO);
			fExternalRadio.setLabelText("Create project at e&xternal location"); //$NON-NLS-1$
			fLocation = new StringButtonDialogField(this);
			fLocation.setLabelText("&Directory:");
			fLocation.setButtonLabel("B&rowse..."); //$NON-NLS-1$
			fExternalRadio.attachDialogField(fLocation);
			fWorkspaceRadio.setSelection(true);
			fExternalRadio.setSelection(false);
			fWorkspaceRadio.doFillIntoGrid(group, numColumns);
			fExternalRadio.doFillIntoGrid(group, numColumns);
			fLocation.doFillIntoGrid(group, numColumns);
			fWorkspaceRadio.setDialogFieldListener(this);
			fLocation.setDialogFieldListener(this);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));
		}

		protected void fireEvent(){
			setChanged();
			notifyObservers();
		}

		protected String getDefaultPath(String name){
			final IPath path = Platform.getLocation().append(name);
			return path.toOSString();
		}

		@Override
		public void update(Observable o, Object arg){
			if (isInWorkspace()){
				fLocation.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}

		public IPath getLocation()
		{
			if (isInWorkspace()){
				return Platform.getLocation();
			}
			return new Path(fLocation.getText().trim());
		}

		public boolean isInWorkspace(){
			return fWorkspaceRadio.isSelected();
		}

		@Override
		public void changeControlPressed(DialogField field){
			final DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setMessage("Choose a directory for the project contents:");
			String directoryName = fLocation.getText().trim();
			if (directoryName.length() == 0){
				String prevLocation = JavaPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
				if (prevLocation != null)
				{
					directoryName = prevLocation;
				}
			}
			if (directoryName.length() > 0){
				final File path = new File(directoryName);
				if (path.exists())
					dialog.setFilterPath(new Path(directoryName).toOSString());
			}
			final String selectedDirectory = dialog.open();
			if (selectedDirectory != null)
			{
				fLocation.setText(selectedDirectory);
				JavaPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC, selectedDirectory);
			}
		}

		@Override
		public void dialogFieldChanged(DialogField field){
			validatePage();
			fireEvent();
		}
	}

	public ScuNewNodeProjectPage1(ISelection selection){
		super("Scu Node Project Wizard");
		setTitle("Create a Scu Node Project");
		setDescription("Information about the project to be created"); //
		fInitialName = "";
	}
	
	@Override
	public IWizardPage getNextPage() {
		WtfNewProjectPage2 nextpage = (WtfNewProjectPage2) super.getNextPage();
		if(nextpage.getContext() == null || nextpage.getContext().equals("")){
			nextpage.setContext(this.getProjectName());
		}
		return nextpage;
	}
	
	@Override
	public void createControl(Composite parent){
		initializeDialogUnits(parent);
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		fNameGroup = new NameGroup(composite, fInitialName);
		fLocationGroup = new LocationGroup(composite);
		fNameGroup.addObserver(fLocationGroup);
		fNameGroup.notifyObservers();
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

	public IPath getLocationPath(){
		return fLocationGroup.getLocation();
	}

	public IProject getProjectHandle(){
		return ResourcesPlugin.getWorkspace().getRoot().getProject(fNameGroup.getName());
	}

	public boolean isInWorkspace(){
		return fLocationGroup.isInWorkspace();
	}

	public String getProjectName(){
		return fNameGroup.getName();
	}
	
	@Override
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
