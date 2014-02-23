package net.juniper.scutools.preferences;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import net.juniper.scutools.ScuToolsActivator;
import net.juniper.scutools.common.ScuProjectCommonTools;

import org.eclipse.core.runtime.IPath;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 
 * @author Juntao
 *
 */
@SuppressWarnings("restriction")
public class ScuPreferencePagesLocation extends PreferencePage implements
		IWorkbenchPreferencePage {
	private LocationGroup locationGroup;
	private final class LocationGroup extends Observable implements Observer, IStringButtonAdapter, IDialogFieldListener{
		protected final StringButtonDialogField fLocation;
		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = JavaUI.ID_PLUGIN + ".last.external.project"; //$NON-NLS-1$

		public LocationGroup(Composite parent){
			Label locationLabel = new Label(parent, SWT.NONE);
			locationLabel.setText("     Please select the location of NodeJS:");
			final int numColumns = 3;
			final Composite group = new Composite(parent, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setLayout(initGridLayout(new GridLayout(numColumns, false), true));
//			group.setText("Location"); //$NON-NLS-1$
			fLocation = new StringButtonDialogField(this);
//			fLocation.setLabelText("Node's Location:");
			fLocation.setButtonLabel("..."); //$NON-NLS-1$
			fLocation.doFillIntoGrid(group, numColumns);
			fLocation.setDialogFieldListener(this);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));
			
		}

		protected void fireEvent(){
			setChanged();
			notifyObservers();
		}

		public void update(Observable o, Object arg){
			fireEvent();
		}

		public String getLocation() {
			return fLocation.getText();
		}
		
		public void changeControlPressed(DialogField field){
			final DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setMessage("Choose the root directory of Node.js:");
			String directoryName = fLocation.getText().trim();
			if (directoryName.length() == 0){
				String prevLocation = JavaPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
				if (prevLocation != null){
					directoryName = prevLocation;
				}
			}
			if (directoryName.length() > 0){
				final File path = new File(directoryName);
				if (path.exists())
					dialog.setFilterPath(new Path(directoryName).toOSString());
			}
			final String selectedDirectory = dialog.open();
			if (selectedDirectory != null){
				fLocation.setText(selectedDirectory);
				JavaPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC, selectedDirectory);
			}
		}

		public void dialogFieldChanged(DialogField field){
			fireEvent();
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
		IPath nodeHome = ScuProjectCommonTools.getNodeHome();
		String preLocation = (nodeHome != null ? nodeHome.toPortableString() : "");
		if(preLocation != null)
			locationGroup.setLocation(preLocation);
		return composite;
	}

	public void init(IWorkbench workbench) {
		
	}

	protected GridLayout initGridLayout(GridLayout layout, boolean margins) {
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
		IPreferenceStore store = ScuToolsActivator.getDefault().getPreferenceStore();
		if(validLocation(locationGroup.getLocation())){
			store.setValue(ScuProjectCommonTools.LOCATION_KEY, locationGroup.getLocation());
		}
		else{
			MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_ERROR |SWT.YES);
			messageBox.setMessage("The path must be pointed to the root directory of Node.js");
			messageBox.open();
			return false;
		}
		return true;
    }

	private boolean validLocation(String str) {
		return ScuProjectCommonTools.isValidLocation(str);
	}
}