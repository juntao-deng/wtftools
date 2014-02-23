package net.juniper.scutools.designer;

import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlType;

import net.juniper.scutools.ScuToolsActivator;
import net.juniper.scutools.common.ScuProjectCommonTools;
import net.juniper.scutools.editor.BrowserDesignEditor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class GetEntityInfoEventHandler extends AbstractBrowserEventHandler{

	private static final String ENTITY_INFO = "entityInfo";

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
			json.put("name", name);
			json.put("text", cName);
			json.put("visible", true);
			json.put("sortable", true);
			json.put("width", "90");
			return json;
		} 
		catch (Exception e) {
			ScuToolsActivator.getDefault().logError(e);
			return null;
		}
	}

	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		try{
			String className = json.getString("entityName");
			if(className == null){
				return null;
			}
			ScuToolsActivator.getDefault().logInfo("=== parsing class:" + className);
			Class c = Class.forName(className, true, ScuProjectCommonTools.getCurrentProjectClassLoader());
			JSONObject result = new JSONObject();
			JSONArray columnInfos = getColumnInfos(c);
			result.put("columnInfos", columnInfos); 
			return result;
		}
		catch(EventHandlerException e){
			ScuToolsActivator.getDefault().logError(e);
			return dumpError(e.getMessage());
		} 
		catch (Exception e) {
			ScuToolsActivator.getDefault().logError(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "ERROR", e.getMessage());
			return null;
		}
	}



	@Override
	protected String getActionName() {
		return ENTITY_INFO;
	}
}
