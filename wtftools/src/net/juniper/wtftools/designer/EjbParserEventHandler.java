package net.juniper.wtftools.designer;

import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlType;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;
import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

public class EjbParserEventHandler extends AbstractBrowserEventHandler{

	private static final String CLASSNAME = "classname";
	private static final String ENTITY = "entity";

	private JSONArray getColumnInfos(Class c) throws EventHandlerException{
		Entity entity = (Entity) c.getAnnotation(Entity.class);
		if(entity == null)
			throw new RuntimeException("The selected class is not an entity");
		JSONArray arr = new JSONArray();
		
		String[] props = null;
		XmlType xmlType = (XmlType) c.getAnnotation(XmlType.class);
		if(xmlType != null){
			props = xmlType.propOrder();
		}
		else{
			Field[] fs = c.getDeclaredFields();
			props = new String[fs.length];
			for(int i = 0; i < fs.length; i ++){
				props[i] = fs[i].getName();
			}
		}
		for(int i = 0; i < props.length; i ++){
			JSONObject json = getJSONObjectByField(c, props[i]);
			if(json != null)
				arr.add(json);
		}
		return arr;
	}
	
	private JSONObject getJSONObjectByField(Class c, String name){
		try {
			JSONObject json = new JSONObject();
			Field f = c.getDeclaredField(name);
			Class ftype = f.getType();
			String type = "string";
			if(ftype.equals(String.class)){
				type = "string";
			}
			else if(ftype.equals(double.class) || ftype.equals(float.class)){
				type = "float";
			}
			else if(ftype.equals(int.class)){
				type = "int";
			}
			else if(ftype.equals(Date.class)){
				type = "date";
			}
			json.put("type", type);
			
			Id id = f.getAnnotation(Id.class);
			if(id != null)
				json.put("isid", true);
			
			String cName = StringUtils.capitalize(name);
			json.put("id", name);
			json.put("name", cName);
			return json;
		} 
		catch (Exception e) {
			WtfToolsActivator.getDefault().logError(e);
			return null;
		}
	}

	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		try{
			String className = getClassName();
			if(className == null){
				return null;
			}
			WtfToolsActivator.getDefault().logInfo("=== parsing class:" + className);
			Class c = Class.forName(className, true, WtfProjectCommonTools.getCurrentProjectClassLoader());
			JSONObject result = new JSONObject();
			JSONArray columnInfos = getColumnInfos(c);
			result.put("columns", columnInfos); 
			return result;
		}
		catch(EventHandlerException e){
			WtfToolsActivator.getDefault().logError(e);
			return dumpError(e.getMessage());
		} 
		catch (Exception e) {
			WtfToolsActivator.getDefault().logError(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "ERROR", e.getMessage());
			return null;
		}
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
