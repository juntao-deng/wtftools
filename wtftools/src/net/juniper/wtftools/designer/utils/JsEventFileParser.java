package net.juniper.wtftools.designer.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.juniper.wtftools.WtfToolsActivator;

import org.apache.commons.io.FileUtils;

public class JsEventFileParser {
	public static final String GLOBAL = "GLOBAL";
	public static final String GLOBALBEGIN = "/** Global scope begin*/";
	public static final String GLOBALEND = "/** Global scope end*/";
	public static final String EVENTBEGIN = "/** Events scope begin*/";
	public static final String EVENTEND = "/** Events scope end*/";
//	String eventSign = "@Event";
	
	public List<JsEventDesc> getEvents(File file){
		List<JsEventDesc> eventList = new ArrayList<JsEventDesc>();
		try {
			String fileStr = FileUtils.readFileToString(file);
			JsEventDesc globalEvent = parseGlobalScope(fileStr);
			if(globalEvent != null)
				eventList.add(globalEvent);
			
			int eventIndex = fileStr.indexOf(EVENTBEGIN);
			if(eventIndex != -1){
				int eventEndIndex = fileStr.indexOf(EVENTEND, eventIndex);
				if(eventEndIndex != -1){
					String eventStr = fileStr.substring(eventIndex + EVENTBEGIN.length(), eventEndIndex);
					parseEvents(eventList, eventStr);
				}
			}
			
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
		}
		return eventList;
	}

	private void parseEvents(List<JsEventDesc> eventList, String eventStr) {
		int begin = 0;
		String eventSign = ".on('";
		int index = eventStr.indexOf(eventSign, begin);
		while(index != -1){
			String eventName = extractEventName(eventStr, begin);
			String exactCompId = extractCompId(eventStr, begin);
			String method = exactEventContent(eventStr, begin);
			begin = fetchNextBegin(eventStr, begin);
			index = eventStr.indexOf(eventSign, begin);
			if(eventName == null){
				continue;
			}
			JsEventDesc jsEvent = new JsEventDesc();
			jsEvent.setCompId(exactCompId);
			jsEvent.setFunc(method);
			jsEvent.setName(eventName);
			eventList.add(jsEvent);
		}
	}

	private int fetchNextBegin(String eventStr, int begin) {
		return eventStr.indexOf("});", begin + "});".length());
	}

	private String exactEventContent(String eventStr, int begin) {
		int index = eventStr.indexOf(".on(", begin);
		if(index == -1)
			return "";
		index = eventStr.indexOf("{");
		int lindex = eventStr.indexOf("});", index);
		return eventStr.substring(index + 2, lindex - 1);
	}

	private String extractCompId(String eventStr, int begin) {
		int index = eventStr.indexOf("$app.on(", begin);
		if(index != -1)
			return "$app";
		String prefix = ".component('";
		index = eventStr.indexOf(prefix, index);
		if(index == -1){
			prefix = ".model('";
			index = eventStr.indexOf(prefix, index);
		}
		if(index == -1){
			return null;
		}
		int eindex = eventStr.indexOf("'", index + prefix.length());
		String eventName = eventStr.substring(index + prefix.length(), eindex);
		return eventName;
	}

	private String extractEventName(String eventStr, int begin) {
		int index = eventStr.indexOf("on('", begin);
		if(index == -1)
			return null;
		int eindex = eventStr.indexOf("'", index + "on('".length());
		String eventName = eventStr.substring(index + "on('".length(), eindex);
		return eventName;
	}

	private JsEventDesc parseGlobalScope(String fileStr){
		int bindex = fileStr.indexOf(GLOBALBEGIN);
		if(bindex != -1){
			int eindex = fileStr.indexOf(GLOBALEND, bindex);
			if(eindex == -1)
				return null;
			JsEventDesc desc = new JsEventDesc();
			desc.setCompId(GLOBAL);
			desc.setName(GLOBAL);
			desc.setFunc(fileStr.substring(bindex + GLOBALBEGIN.length() + 1, eindex));
			return desc;
		}
		return null;
	}

	
	public static void main(String[] args){
		new JsEventFileParser().getEvents(new File("/home/juntaod/Dev/projects/wtfproject/web/designsupport/compattr/grid/controller.js"));
	}
}
