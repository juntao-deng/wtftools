package net.juniper.wtftools.preferences;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class WtfPreferencePagesLocation extends PreferencePage implements
		IWorkbenchPreferencePage {
	private LocationGroup locationGroup;
//	private NameGroup fNameGroup;
	private final class LocationGroup extends Observable implements Observer, IStringButtonAdapter, IDialogFieldListener
	{
		protected final StringButtonDialogField fLocation;
		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = JavaUI.ID_PLUGIN + ".last.external.project"; //$NON-NLS-1$

		public LocationGroup(Composite parent)
		{
			final int numColumns = 3;
			
			final Composite group = new Composite(parent, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setLayout(initGridLayout(new GridLayout(numColumns, false), true));
//			group.setText("Location"); //$NON-NLS-1$
			fLocation = new StringButtonDialogField(this);
			fLocation.setLabelText("Location:");
			fLocation.setButtonLabel("Browse..."); //$NON-NLS-1$
			fLocation.doFillIntoGrid(group, numColumns);
			fLocation.setDialogFieldListener(this);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));
		}

		protected void fireEvent()
		{
			setChanged();
			notifyObservers();
		}

//		protected String getDefaultPath(String name)
//		{
//			final IPath path = Platform.getLocation().append(name);
//			return path.toOSString();
//		}

		public void update(Observable o, Object arg)
		{
//			if (isInWorkspace())
//			{
//				//fLocation.setText(getDefaultPath(fNameGroup.getName()));
//			}
			fireEvent();
		}

//		public IPath getLocation()
//		{
//			if (isInWorkspace())
//			{
//				return Platform.getLocation();
//			}
//			return new Path(fLocation.getText().trim());
//		}
//
//		public boolean isInWorkspace()
//		{
//			return fWorkspaceRadio.isSelected();
//		}

		public void changeControlPressed(DialogField field)
		{
			final DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setMessage("Choose a directory for the project contents:");
			String directoryName = fLocation.getText().trim();
			if (directoryName.length() == 0)
			{
				String prevLocation = JavaPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
				if (prevLocation != null)
				{
					directoryName = prevLocation;
				}
			}
			if (directoryName.length() > 0)
			{
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

		public void dialogFieldChanged(DialogField field)
		{
			fireEvent();
		}
		
		public String getLocation() {
			return fLocation.getText();
		}

		public void setLocation(String preLocation) {
			fLocation.setText(preLocation);
		}
	}
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		locationGroup = new LocationGroup(composite);
		String preLocation = WtfProjectCommonTools.getFrameworkLocation();
		if(preLocation != null)
			locationGroup.setLocation(preLocation);
		return composite;
	}

	public void init(IWorkbench workbench) {
		
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
	
	public boolean performOk() {
		IPreferenceStore store = WtfToolsActivator.getDefault().getPreferenceStore();
		if(validLocation()){
//			store.setValue(WtfProjectCommonTools.LOCATION_KEY, locationGroup.getLocation());
		}
		else{
			showErrorMsg("The path must be pointed to the framework project's root directory");
			return false;
		}
		return true;
    }

	private boolean validLocation() {
		return true;
	}

	private void showErrorMsg(String string) {
		
	}
}