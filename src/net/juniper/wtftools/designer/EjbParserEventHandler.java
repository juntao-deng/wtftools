package net.juniper.wtftools.designer;

import java.util.List;
import java.util.Map;

import net.juniper.wtftools.WtfToolsActivator;

public class EjbParserEventHandler implements IBrowserEventHandler{

	private static final String CLASSNAME = "classname";
	private static final String ENTITY = "entity";

	@Override
	public boolean canHanle(Map<String, Object> json) {
		if(json.get(ACTION).equals(ENTITY)){
			try{
				String className = (String) json.get(CLASSNAME);
				WtfToolsActivator.getDefault().logInfo("=== parsing class:" + className);
				Class c = Class.forName(className);
				List<Map<String, String>> columnInfos = getColumnInfos(c);
			}
			catch(Exception e){
				WtfToolsActivator.getDefault().logError(e);
			}
		}
		return false;
	}

	private List<Map<String, String>> getColumnInfos(Class c) {
		return null;
	}

	@Override
	public Map<String, Object> handle(Map<String, Object> json) {
		return null;
	}
}
