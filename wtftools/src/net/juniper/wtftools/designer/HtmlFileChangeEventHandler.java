package net.juniper.wtftools.designer;

import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class HtmlFileChangeEventHandler extends AbstractBrowserEventHandler {

	@Override
	public JSONObject handle(BrowserDesignEditor editor,JSONObject json) {
		editor.setHtmlContent((String) json.get("html"));
		editor.setDirty(true);
		return null;
	}

	@Override
	protected String getActionName() {
		return "updatehtml";
	}

}
