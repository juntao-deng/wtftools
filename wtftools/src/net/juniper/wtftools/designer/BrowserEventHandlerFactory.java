package net.juniper.wtftools.designer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public class BrowserEventHandlerFactory {
	public static List<IBrowserEventHandler> handlers = new ArrayList<IBrowserEventHandler>();
	static{
		handlers.add(new EjbParserEventHandler());
	}
	
	public static JSONObject handleEvent(String str){
		JSONObject json = toJSONObject(str);
		JSONObject result = null;
		for(IBrowserEventHandler handler : handlers){
			if(handler.canHanle(json)){
				result = handler.handle(json);
				break;
			}
		}
		return result;
	}

	private static JSONObject toJSONObject(String str) {
		return JSONObject.fromObject(str);
	}
}
