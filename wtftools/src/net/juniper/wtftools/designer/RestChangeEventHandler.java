package net.juniper.wtftools.designer;

import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class RestChangeEventHandler extends AbstractBrowserEventHandler {

	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		editor.addRest((String)json.getString("file"), (JSONObject)json.get("api"));
		editor.setDirty(true);
		return null;
	}

	@Override
	protected String getActionName() {
		return "updaterest";
	}

}
