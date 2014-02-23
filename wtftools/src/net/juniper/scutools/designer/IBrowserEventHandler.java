package net.juniper.scutools.designer;

import net.juniper.scutools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public interface IBrowserEventHandler {
	public static final String ACTION = "action";
	public boolean canHanle(JSONObject json);
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json);
}
