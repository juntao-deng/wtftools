package net.juniper.scutools.designer;

import java.util.ArrayList;
import java.util.List;

import net.juniper.scutools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class BrowserEventHandlerFactory {
	public static List<IBrowserEventHandler> handlers = new ArrayList<IBrowserEventHandler>();
	static{
		handlers.add(new EntitySelectionEventHandler());
		handlers.add(new HtmlFileChangeEventHandler());
		handlers.add(new RestChangeEventHandler());
		handlers.add(new UpdateStateEventHandler());
		handlers.add(new GenerateCodeEventHandler());
		handlers.add(new GetEventMethodsHandler());
		handlers.add(new UpdateControllerEventHandler());
		handlers.add(new UpdateMetadataEventHandler());
		handlers.add(new UpdateModelEventHandler());
		handlers.add(new ClearEventsEventHandler());
		handlers.add(new GetEntityInfoEventHandler());
	}
	
	public static JSONObject handleEvent(BrowserDesignEditor editor, String str){
		JSONObject json = toJSONObject(str);
		JSONObject result = null;
		for(IBrowserEventHandler handler : handlers){
			if(handler.canHanle(json)){
				result = handler.handle(editor, json);
				break;
			}
		}
		if(result != null){
			result.put("eventId", json.get("eventId"));
		}
		return result;
	}

	private static JSONObject toJSONObject(String str) {
		return JSONObject.fromObject(str);
	}
}
