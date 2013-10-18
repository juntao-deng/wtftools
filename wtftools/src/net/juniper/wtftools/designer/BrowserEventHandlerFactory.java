package net.juniper.wtftools.designer;

import java.util.ArrayList;
import java.util.List;

import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class BrowserEventHandlerFactory {
	public static List<IBrowserEventHandler> handlers = new ArrayList<IBrowserEventHandler>();
	static{
		handlers.add(new EjbParserEventHandler());
		handlers.add(new HtmlFileChangeEventHandler());
		handlers.add(new JsFileChangeEventHandler());
		handlers.add(new RestChangeEventHandler());
		handlers.add(new UpdateStateEventHandler());
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
		return result;
	}

	private static JSONObject toJSONObject(String str) {
		return JSONObject.fromObject(str);
	}
}
