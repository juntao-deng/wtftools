package net.juniper.scutools.designer;

import net.juniper.scutools.designer.utils.JsEventFileParser;
import net.juniper.scutools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class UpdateControllerEventHandler extends AbstractBrowserEventHandler {

	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		if(json.has("compId")){
			String compId = json.getString("compId");
			String eventName = json.getString("eventName");
			String eventContent = json.has("eventContent") ? json.getString("eventContent") : "";
			String type = json.getString("type");
			editor.addController(compId, eventName, eventContent, type);
		}
		if(json.has("methodGlobalContent")){
			String globalEventContent = json.getString("methodGlobalContent");
			editor.addController(JsEventFileParser.GLOBAL, JsEventFileParser.GLOBAL, globalEventContent, "");
		}
		return null;
	}

	@Override
	protected String getActionName() {
		return "updateController";
	}

}
