package net.juniper.scutools.designer;

import net.juniper.scutools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class UpdateModelEventHandler extends AbstractBrowserEventHandler {
	private static final String UPDATEJS = "updatemodel";
	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		editor.addModel(json.getString("compId"), json.getString("model"));
		return null;
	}

	@Override
	protected String getActionName() {
		return UPDATEJS;
	}
	
	
}
