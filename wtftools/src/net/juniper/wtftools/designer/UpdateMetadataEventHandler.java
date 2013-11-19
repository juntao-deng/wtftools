package net.juniper.wtftools.designer;

import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class UpdateMetadataEventHandler extends AbstractBrowserEventHandler {
	private static final String UPDATEJS = "updatemd";
	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		editor.addMetadata(json.getString("compId"), json.getString("metadata"));
		return null;
	}

	@Override
	protected String getActionName() {
		return UPDATEJS;
	}
	
	
}
