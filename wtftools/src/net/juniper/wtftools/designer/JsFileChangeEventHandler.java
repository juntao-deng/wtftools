package net.juniper.wtftools.designer;

import net.sf.json.JSONObject;

public class JsFileChangeEventHandler extends AbstractBrowserEventHandler {
	private static final String UPDATEJS = "updatejs";
	@Override
	public JSONObject handle(JSONObject json) {
		String modelStr = getModelFile();
		int start = modelStr.indexOf(getModelStart());
		if(start == -1){
			modelStr = addToString(modelStr);
		}
		else{
			int end = modelStr.indexOf(getEnd(), start);
			modelStr = updateString(modelStr, start, end, (String)json.get("model"));
		}
		updateModelFile(modelStr);
		return null;
	}

	private void updateModelFile(String modelStr) {
		
	}

	private String updateString(String modelStr, int start, int end, String updateStr) {
		return null;
	}

	private String addToString(String modelStr) {
		return null;
	}

	@Override
	protected String getActionName() {
		return UPDATEJS;
	}
	
	private String getModelFile() {
		return "";
	}
	
	private String getControllerFile(){
		return "";
	}
	
	private String getModelStart() {
		return "";
	}

	private String getEnd() {
		return "";
	}
}
