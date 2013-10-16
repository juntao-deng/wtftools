package net.juniper.wtftools.designer;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EjbParserEventHandler extends AbstractBrowserEventHandler{

	private static final String CLASSNAME = "classname";
	private static final String ENTITY = "entity";

	private JSONArray getColumnInfos(Class c) {
		return null;
	}

	@Override
	public JSONObject handle(JSONObject json) {
		try{
			String className = (String) json.get(CLASSNAME);
			WtfToolsActivator.getDefault().logInfo("=== parsing class:" + className);
			Class c = Class.forName(className, true, WtfProjectCommonTools.getCurrentProjectClassLoader());
			JSONObject result = new JSONObject();
			JSONArray columnInfos = getColumnInfos(c);
			result.put("columns", columnInfos);
			return result;
		}
		catch(Exception e){
			WtfToolsActivator.getDefault().logError(e);
			return dumpError(e.getMessage());
		}
	}

	@Override
	protected String getActionName() {
		return ENTITY;
	}
}
