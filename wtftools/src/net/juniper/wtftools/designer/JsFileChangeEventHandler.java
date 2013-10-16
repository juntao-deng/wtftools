package net.juniper.wtftools.designer;

import net.sf.json.JSONObject;

public class JsFileChangeEventHandler extends AbstractBrowserEventHandler {
	private static final String UPDATEJS = "updatejs";
	@Override
	public JSONObject handle(JSONObject json) {
		return null;
	}

	@Override
	protected String getActionName() {
		return UPDATEJS;
	}

}
