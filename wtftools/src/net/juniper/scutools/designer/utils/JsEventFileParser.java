package net.juniper.scutools.designer.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.juniper.scutools.ScuToolsActivator;

import org.apache.commons.io.FileUtils;

public class JsEventFileParser {
	public static final String APP_EVENT_PATTERN = "(\\$app)\\.on\\('(\\w+)'\\s*\\,\\s*function\\(\\s*options\\s*\\)\\s*\\{(\\n*)(.*?)(\\n*)\\}\\s*\\)\\s*;";
	public static final String COMPONENT_EVENT_PATTERN = "\\$app\\.component\\('(\\w+)'\\)\\.on\\('(\\w+)'\\s*\\,\\s*function\\(\\s*options\\s*\\)\\s*\\{(\\n*)(.*?)(\\n*)\\}\\s*\\)\\s*;";
	public static final String MODEL_EVENT_PATTERN = "\\$app\\.model\\('(\\w+)'\\)\\.on\\('(\\w+)'\\s*\\,\\s*function\\(\\s*options\\s*\\)\\s*\\{(\\n*)(.*?)(\\n*)\\}\\s*\\)\\s*;";
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
			ScuToolsActivator.getDefault().logError(e);
		}
		return eventList;
	}

	private void parseEvents(List<JsEventDesc> eventList, String eventStr) {
		String compPattern = COMPONENT_EVENT_PATTERN;
		Pattern p = Pattern.compile(compPattern, Pattern.DOTALL);
		Matcher m = p.matcher(eventStr);
		while(m.find()){
			JsEventDesc jsEvent = new JsEventDesc();
			jsEvent.setCompId(m.group(1));
			jsEvent.setFunc(WtfStringUtils.removeTab(m.group(4)));
			jsEvent.setName(m.group(2));
			jsEvent.setType(JsEventDesc.TYPE_COMPONENT);
			eventList.add(jsEvent);
		}
		
		String modelPattern = MODEL_EVENT_PATTERN;
		p = Pattern.compile(modelPattern, Pattern.DOTALL);
		m = p.matcher(eventStr);
		while(m.find()){
			JsEventDesc jsEvent = new JsEventDesc();
			jsEvent.setCompId(m.group(1));
			jsEvent.setFunc(WtfStringUtils.removeTab(m.group(4)));
			jsEvent.setName(m.group(2));
			jsEvent.setType(JsEventDesc.TYPE_MODEL);
			eventList.add(jsEvent);
		}
		String appPattern = APP_EVENT_PATTERN;
		p = Pattern.compile(appPattern, Pattern.DOTALL);
		m = p.matcher(eventStr);
		while(m.find()){
			JsEventDesc jsEvent = new JsEventDesc();
			jsEvent.setCompId("$app");
			jsEvent.setFunc(WtfStringUtils.removeTab(m.group(4)));
			jsEvent.setName(m.group(2));
			jsEvent.setType(JsEventDesc.TYPE_APP);
			eventList.add(jsEvent);
		}
//		int begin = 0;
//		String eventSign = ".on('";
//		int index = eventStr.indexOf(eventSign, begin);
//		while(index != -1){
//			String eventName = extractEventName(eventStr, begin);
//			String exactCompId = extractCompId(eventStr, begin);
//			String method = exactEventContent(eventStr, begin);
//			begin = fetchNextBegin(eventStr, begin);
//			index = eventStr.indexOf(eventSign, begin);
//			if(eventName == null){
//				continue;
//			}
//			String[] pairs = exactCompId.split(",");
//			JsEventDesc jsEvent = new JsEventDesc();
//			jsEvent.setCompId(pairs[0]);
//			jsEvent.setFunc(method);
//			jsEvent.setName(eventName);
//			jsEvent.setType(pairs[1]);
//			eventList.add(jsEvent);
//		}
	}

//	private int fetchNextBegin(String eventStr, int begin) {
//		return eventStr.indexOf("});", begin + "});".length());
//	}
//
//	private String exactEventContent(String eventStr, int begin) {
//		int index = eventStr.indexOf(".on(", begin);
//		if(index == -1)
//			return "";
//		index = eventStr.indexOf("{");
//		int lindex = eventStr.indexOf("});", index);
//		return eventStr.substring(index + 2, lindex - 1);
//	}
//
//	private String extractCompId(String eventStr, int begin) {
//		String compPattern = "\\$app\\.component\\('(\\w+)'\\)\\.on\\('(\\w+)'\\s*\\,\\s*function\\(\\s*options\\s*\\)\\s*\\{(.*?)\\}\\s*\\)\\s*;";
//		String appPattern = "\\$app\\.on\\('(\\w+)'\\s*\\,\\s*function\\(\\s*options\\s*\\)\\s*\\{(.*?)\\}\\s*\\)\\s*;";
//		String modelPattern = "\\$app\\.model\\('(\\w+)'\\)\\.on\\('(\\w+)'\\s*\\,\\s*function\\(\\s*options\\s*\\)\\s*\\{(.*?)\\}\\s*\\)\\s*;";
//		Pattern p = Pattern.compile(compPattern + "|" + appPattern + "|" + modelPattern, Pattern.DOTALL);
//		Matcher m = p.matcher(eventStr);
//		if(m.find()){
//			int start = m.start();
//			int end = m.end();
//		}
//		return null;
////		 Matcher m = p.matcher("aaaaab");
////		 boolean b = m.matches();
////		int index = eventStr.indexOf("$app.on(", begin);
////		int index = eventStr.indexOf("$app.component('on(", begin);
////		int index = eventStr.indexOf("$app.on(", begin);
////		if(index != -1)
////			return "$app" + "," + JsEventDesc.TYPE_APP;
////		String prefix = ".component('";
////		String type = JsEventDesc.TYPE_COMPONENT;
////		index = eventStr.indexOf(prefix, begin);
////		if(index == -1){
////			prefix = ".model('";
////			type = JsEventDesc.TYPE_MODEL;
////			index = eventStr.indexOf(prefix, begin);
////		}
////		if(index == -1){
////			return null;
////		}
////		int eindex = eventStr.indexOf("'", index + prefix.length());
////		String eventName = eventStr.substring(index + prefix.length(), eindex);
////		return eventName + "," + type;
//	}
//
//	private String extractEventName(String eventStr, int begin) {
//		int index = eventStr.indexOf("on('", begin);
//		if(index == -1)
//			return null;
//		int eindex = eventStr.indexOf("'", index + "on('".length());
//		String eventName = eventStr.substring(index + "on('".length(), eindex);
//		return eventName;
//	}

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
			desc.setType("");
			return desc;
		}
		return null;
	}

	
	public static void main(String[] args){
		new JsEventFileParser().getEvents(new File("/home/juntaod/Dev/projects/wtfproject/web/designsupport/compattr/grid/controller.js"));
	}
}
