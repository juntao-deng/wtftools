package net.juniper.wtftools.designer;

import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class UpdateStateEventHandler extends AbstractBrowserEventHandler {

	private static final String STATE = "state";

	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		editor.setDirty(true);
		return null;
	}

	@Override
	protected String getActionName() {
		return STATE;
	}

}
