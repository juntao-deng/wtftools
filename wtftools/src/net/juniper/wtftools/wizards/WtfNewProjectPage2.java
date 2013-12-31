package net.juniper.wtftools.wizards;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class WtfNewProjectPage2 extends WizardPage implements  IPropertyChangeListener, IDialogFieldListener
 {
	public static String lfwprojectPath;
	private boolean fInited = false;
	public static IProject project;
	private StringDialogField contextPath;
	private SelectionButtonDialogField homePageField;
	private SelectionButtonDialogField jpaConfigField;
	
	private StringDialogField createStringDialogField(String labeltext, Composite parent){
		StringDialogField field = new StringDialogField();
		field.setLabelText(labeltext);
		field.doFillIntoGrid(parent, 2);
		field.setDialogFieldListener(this);
		LayoutUtil.setHorizontalGrabbing(field.getTextControl(parent));
		return field;
	}
	
	private SelectionButtonDialogField createCheckBoxField(String labeltext, Composite parent)
	{
		SelectionButtonDialogField field = new SelectionButtonDialogField(SWT.CHECK);
		field.setLabelText(labeltext);
		field.doFillIntoGrid(parent, 2);
		field.setDialogFieldListener(this);
		return field;
	}
		
	protected WtfNewProjectPage2(ISelection selection) {
		super("Wtf Project Wizard");
		setTitle("Create a Wtf Project");
		setDescription("Information about the project to be created"); //
		lfwprojectPath = "";
	}

	public boolean isFInited() {
		return fInited;
	}

	public void setFInited(boolean inited) {
		fInited = inited;
	}
	
	public  String getContext() {
		return contextPath.getText();
	}

	public void setContext(String context) {
		contextPath.setText(context);
	}
	
	public boolean isWithHome(){
		return homePageField.isSelected();
	}
	
	public boolean isWithJpa() {
		return jpaConfigField.isSelected();
	}

	public void createControl(Composite parent){
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(initGridLayout(new GridLayout(2, false), true));
		
		contextPath = createStringDialogField("Context Path", composite);
		
		homePageField = createCheckBoxField("With Home Page", composite);
		homePageField.setSelection(true);
		
		jpaConfigField = createCheckBoxField("Jpa Configuration", composite);
		jpaConfigField.setSelection(true);
		
		setControl(composite);
		fInited = true;
	}

	protected GridLayout initGridLayout(GridLayout layout, boolean margins){
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

	private void validate(){
	}

	public boolean isPageComplete(){
		if(contextPath != null && contextPath.getText() != null){
			setContext(contextPath.getText());
			if(getContext().equals("")){
				return false;
			}
			Pattern pattern = Pattern.compile("[!@~#$%^&*-+]+");//just test
			if(pattern.matcher(getContext().trim()).find())
			{
				return false;
			}
			
		}
		return true;
	}

	public void propertyChange(PropertyChangeEvent event) {
		validate();
	}

	public void dialogFieldChanged(DialogField field) {
		validate();
	}
}