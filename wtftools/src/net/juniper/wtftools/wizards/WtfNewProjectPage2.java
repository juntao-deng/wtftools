package net.juniper.wtftools.wizards;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.DirectoryFieldEditor;
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
	public static String context;
	//public static String docname;
	public static IProject project;
	private StringDialogField contextPath;
//	private checkb
	//private StringDialogField docNameNode;
	private DirectoryFieldEditor docbasePath;
	
	
	private StringDialogField createStringDialogField(String labeltext, Composite parent)
	{
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


	public DirectoryFieldEditor getDocbasePath() {
		return docbasePath;
	}




	public void setDocbasePath(DirectoryFieldEditor docbasePath) {
		this.docbasePath = docbasePath;
	}

	private String docbase;
	
	public String getDocbase() {
		return docbasePath.getStringValue();
	}

	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}

	
//	public static void setDocnmae(String docname){
//		NewModuleWebContextPage.docname = docname;
//	}
//	
	public  String getContext() {
		return contextPath.getText();
	}


	public StringDialogField getContextPath() {
		return contextPath;
	}

	public void setContextPath(StringDialogField contextPath) {
		this.contextPath = contextPath;
	}

	public static void setContext(String context) {
		WtfNewProjectPage2.context = context;
	}

//	private SelectionButtonDialogField isPortalProject;
//	public SelectionButtonDialogField getIsPortalProject() {
//		return isPortalProject;
//	}
//
//	public void setIsPortalProject(SelectionButtonDialogField isPortalProject) {
//		this.isPortalProject = isPortalProject;
//	}

	public void createControl(Composite parent)
	{
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(initGridLayout(new GridLayout(2, false), true));
		
		contextPath = createStringDialogField("访问器路径", composite);
		
		//isPortalProject = createCheckBoxField("是否Portal工程", composite);
		
		setControl(composite);
		fInited = true;
	}

	
	private String getCurrentLFWPath(){
		return lfwprojectPath + "/web";
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

	private void validate()
	{
		if(contextPath != null){
			setContext(contextPath.getText());
			if(getContext().equals("")){
				//setErrorMessage("Context不能为空");
				setPageComplete(false);
				//return false;
			}
			Pattern pattern = Pattern.compile("[!@~#$%^&*-+]+");//just test
			if(pattern.matcher(getContext().trim()).find())
			{
				//setErrorMessage("Context中请不要包含: ! @ ~ # $ % ^ & * - + 等特殊符号!");
				setPageComplete(false);
				//return false;
			}
			
		}
		setPageComplete(true);
		//return true;
	}

	public boolean isPageComplete(){
		if(contextPath != null && contextPath.getText() != null){
			setContext(contextPath.getText());
			if(getContext().equals("")){
				//setErrorMessage("Context不能为空");
				//setPageComplete(false);
				return false;
			}
			Pattern pattern = Pattern.compile("[!@~#$%^&*-+]+");//just test
			if(pattern.matcher(getContext().trim()).find())
			{
				//setErrorMessage("Context中请不要包含: ! @ ~ # $ % ^ & * - + 等特殊符号!");
				//setPageComplete(false);
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