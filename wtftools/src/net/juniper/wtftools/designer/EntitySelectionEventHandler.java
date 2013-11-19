package net.juniper.wtftools.designer;

import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.juniper.wtftools.rest.RestGeneratorHelper;
import net.sf.json.JSONObject;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

public class EntitySelectionEventHandler extends AbstractBrowserEventHandler{

//	private static final String CLASSNAME = "classname";
	private static final String ENTITY = "entity";

	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		String className = getClassName();
		JSONObject result = new JSONObject();
		if(className == null){
			return result;
		}
		result.put("selectedClass", className);
		if(!RestGeneratorHelper.restExist(className)){
			result.put("restapiExist", true);
		}
		
		String simpleName = className.substring(className.lastIndexOf(".") + 1);
		
		String serviceName = null;
		if(simpleName.endsWith("Entity"))
			serviceName = simpleName.substring(0, simpleName.length() - "Entity".length()).toLowerCase() + "s";
		else
			serviceName = simpleName.toLowerCase() + "s";
		result.put("serviceName", serviceName);
		result.put("modelId", serviceName + "Model");
//			WtfToolsActivator.getDefault().logInfo("=== parsing class:" + className);
//			Class c = Class.forName(className, true, WtfProjectCommonTools.getCurrentProjectClassLoader());
//			JSONArray columnInfos = getColumnInfos(c);
//			result.put("columnInfos", columnInfos); 
//			if(!RestGeneratorHelper.restExist(className)){
//			}
//			result.put("restservice", className.substring(className.lastIndexOf(".") + 1).toLowerCase());
		return result;
	}

	private String getClassName() {
		SelectionDialog dialog= new OpenTypeSelectionDialog(Display.getCurrent().getActiveShell(), true, PlatformUI.getWorkbench().getProgressService(), null, IJavaSearchConstants.TYPE);
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);

		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return null;

		Object[] types= dialog.getResult();
		if (types == null || types.length == 0)
			return null;

		if (types.length == 1) {
			String pack = ((PackageFragment)((IJavaElement)types[0]).getParent().getParent()).getElementName();
			return pack + "." + ((IJavaElement)types[0]).getElementName();
		}
		return null;
	}

	@Override
	protected String getActionName() {
		return ENTITY;
	}
}
