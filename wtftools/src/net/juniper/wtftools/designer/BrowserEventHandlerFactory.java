package net.juniper.wtftools.designer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrowserEventHandlerFactory {
	public static List<IBrowserEventHandler> handlers = new ArrayList<IBrowserEventHandler>();
	static{
		handlers.add(new EjbParserEventHandler());
	}
	
	public static String handleEvent(String str){
		Map<String, Object> json = toMap(str);
		Map<String, Object> result = null;
		for(IBrowserEventHandler handler : handlers){
			if(handler.canHanle(json)){
				result = handler.handle(json);
				break;
			}
		}
		if(result != null)
			return toJsonStr(result);
		return null;
	}

	private static String toJsonStr(Map<String, Object> result) {
		return null;
	}

	private static Map<String, Object> toMap(String str) {
		return null;
	}
}
