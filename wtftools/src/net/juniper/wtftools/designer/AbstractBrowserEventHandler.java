package net.juniper.wtftools.designer;

import net.sf.json.JSONObject;


public abstract class AbstractBrowserEventHandler implements IBrowserEventHandler {

	protected JSONObject dumpError(String error){
		JSONObject obj = new JSONObject();
		obj.put("errormsg", error);
		return obj;
	}
	
	@Override
	public boolean canHanle(JSONObject json) {
		if(json.get(ACTION).equals(getActionName())){
			return true;
		}
		return false;
	}

	protected abstract String getActionName();
}
