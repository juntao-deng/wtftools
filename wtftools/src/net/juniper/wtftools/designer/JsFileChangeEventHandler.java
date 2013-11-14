package net.juniper.wtftools.designer;

import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class JsFileChangeEventHandler extends AbstractBrowserEventHandler {
	private static final String UPDATEJS = "updatejs";
	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		editor.addModel(json.getString("compId"), json.getString("metadata"));
		editor.addController(json.getString("compId"), json.getString("controller"));
		JSONObject restObj = (JSONObject) json.get("rest");
		editor.setDirty(true);
		return null;
	}

	@Override
	protected String getActionName() {
		return UPDATEJS;
	}
	
	
}
