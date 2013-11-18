package net.juniper.wtftools.editor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.designer.utils.JsEventDesc;
import net.juniper.wtftools.designer.utils.JsEventFileParser;
import net.juniper.wtftools.designer.utils.JsonFormatTool;
import net.juniper.wtftools.designer.utils.WtfStringUtils;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

public class ApplicationUpdateHelper {
	private static final String BR = "\n";
	private static final String TAB = "\t";



	public static void update(IPath appPath, IPath restPath, IPath htmlPath, Map<String, String> metadataMap, String htmlContent){
		String metadataPath = appPath.append("model.js").toOSString();
		String modelStr = getFile(metadataPath);
		if(modelStr == null || modelStr.trim().equals(""))
			modelStr = "wdefine(function(){\n});";
		modelStr = updateCode(modelStr, metadataMap, "metadata");
		updateFile(metadataPath, modelStr);
		
//		String controllerPath = appPath.append("controller.js").toOSString();
//		String controllerStr = getFile(controllerPath);
//		if(controllerStr == null || controllerStr.trim().equals(""))
//			controllerStr = "wdefine(function(){\r\n});";
//		controllerStr = updateCode(controllerStr, controllerMap, "controller");
//		updateFile(controllerPath, controllerStr);
		
		htmlContent = fixHtml(htmlContent);
		updateFile(htmlPath.toOSString(), htmlContent);
	}
	
	public static void updateController(IPath controllerPath, List<JsEventDesc> eventList){
		try {
			String controllerStr = FileUtils.readFileToString(controllerPath.toFile());
			int index = controllerStr.indexOf(JsEventFileParser.GLOBALBEGIN);
			int endIndex = -1;
			if(index == -1){
				String modelStr = "wdefine(function(){\n";
				index = controllerStr.indexOf(modelStr) + modelStr.length();
			}
			else{
				endIndex = controllerStr.indexOf(JsEventFileParser.GLOBALEND) + JsEventFileParser.GLOBALEND.length();
			}
			
			String content = JsEventFileParser.GLOBALBEGIN + "\n\n" + getGlobalMethodContent(eventList) + "\n\n" + JsEventFileParser.GLOBALEND + "\n\n";
			controllerStr = WtfStringUtils.replaceString(index, endIndex, controllerStr, content);
//			String beginStr = controllerStr.substring(0, index);
//			String endStr = null;
//			int lIndex = controllerStr.indexOf(JsEventFileParser.GLOBALEND);
//			if(lIndex == -1){
//				endStr = controllerStr.substring(index);
//			}
//			else
//				endStr = controllerStr.substring(lIndex + JsEventFileParser.GLOBALEND.length());
//			String fileStr = beginStr + content + endStr;
			
			Iterator<JsEventDesc> it = eventList.iterator();
			while(it.hasNext()){
				JsEventDesc desc = it.next();
				if(desc.getName().equals(JsEventFileParser.GLOBAL))
					continue;
				controllerStr = updateEventController(controllerStr, desc.getCompId(), desc.getName(), desc.getFunc());
			}
			FileUtils.writeStringToFile(controllerPath.toFile(), controllerStr);
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
		}
	}
	
	private static String updateEventController(String controllerStr, String compId, String eventName, String content) {
		int index = controllerStr.indexOf(JsEventFileParser.EVENTBEGIN);
		if(index == -1){
			int gindex = controllerStr.indexOf(JsEventFileParser.GLOBALEND) + JsEventFileParser.GLOBALEND.length();
			controllerStr = WtfStringUtils.replaceString(gindex, -1, controllerStr, JsEventFileParser.EVENTBEGIN + "\n" + JsEventFileParser.EVENTEND + "\n");
		}
		
		index = controllerStr.indexOf(JsEventFileParser.EVENTBEGIN) + JsEventFileParser.EVENTBEGIN.length() + 1;
		String str = "$app.component('" + compId + "').on('" + eventName + "'";
		
		int eventIndex = controllerStr.indexOf(str);
		int eventEndIndex = -1;
		if(eventIndex == -1){
			eventIndex = index;
		}
		else{
			eventEndIndex = controllerStr.indexOf("});", eventIndex) + "});".length();
		}
		
		String eventStr = str + ", function(options){\n" + content + "\n});\n";
		return WtfStringUtils.replaceString(eventIndex, eventEndIndex, controllerStr, eventStr);
//		return bstr + "\n" + eventStr + estr;
	}
	
	private static String getGlobalMethodContent(List<JsEventDesc> eventList) {
		Iterator<JsEventDesc> it = eventList.iterator();
		while(it.hasNext()){
			JsEventDesc desc = it.next();
			if(desc.getName().equals(JsEventFileParser.GLOBAL)){
				return desc.getFunc();
			}
		}
		return "";
	}


	private static String fixHtml(String htmlContent) {
		if(htmlContent == null || htmlContent.equals(""))
			return null;
		String str = "<div wtftype=\"container\"";
		int index = htmlContent.indexOf(str);
		if(index != -1){
			Parser parser;
			try {
				parser = new Parser(htmlContent);
				NodeList list = parser.parse (null);
				Node rootContainer = list.elementAt(0);
				NodeList cList = rootContainer.getChildren();
				SimpleNodeIterator it = cList.elements();
				while(it.hasMoreNodes()){
					Node node = it.nextNode();
					processNode(node);
				}
				dumpWithFormat(cList.elementAt(0), "", TAB);
				return cList.elementAt(0).toHtml();
			} 
			catch (ParserException e) {
				WtfToolsActivator.getDefault().logError(e);
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return htmlContent;
	}



	private static void dumpWithFormat(Node parentNode, String prepre, String prefix) {
		NodeList cList = parentNode.getChildren();
		if(cList != null){
			SimpleNodeIterator it = parentNode.getChildren().elements();
			NodeList newList = new NodeList();
			while(it.hasMoreNodes()){
				Node node = it.nextNode();
				TextNode text = new TextNode(prefix);
				newList.add(text);
				newList.add(node);
				
//			    if(node.getChildren() != null && node.getChildren().size() > 0){
//			    	TextNode text2 = new TextNode(prefix);
//			    	newList.add(text2);
//			    }
				
//				TextNode text = new TextNode(prefix);
//				cList.add(text);
				TextNode br = new TextNode(BR);
				newList.add(br);
				dumpWithFormat(node, prefix, prefix + TAB);
			}
			parentNode.setChildren(newList);
		}
		cList = parentNode.getChildren();
		if(cList != null){
			TextNode br = new TextNode(BR);
			cList.prepend(br);
			if(!prepre.equals("")){
			TextNode text = new TextNode(prepre);
			cList.add(text);
			}
		}
	}


	private static void processNode(Node node) {
		NodeList cList = node.getChildren();
//		if(node instanceof Div && ((Div)node).getAttribute("wtftype") != null && ((Div)node).getAttribute("wtftype").equals("container")){
//			node.getParent().setChildren(cList);
//		}
		if(node instanceof Div && ((Div)node).getAttribute("wtftype") != null && (!((Div)node).getAttribute("wtftype").equals("container"))){
			node.setChildren(null);
			cList = null;
		}
		
		if(node instanceof Div){
			((Div) node).removeAttribute("wtfdone");
			((Div) node).removeAttribute("designable");
			String className = ((Div) node).getAttribute("class");
			if(className != null && className.contains(" designele_sign")){
				className = className.replace(" designele_sign", "");
			}
			if(className != null && className.contains(" designele")){
				className = className.replace(" designele", "");
			}
			if(className != null)
				((Div) node).setAttribute("class", className);
		}
		if(cList != null){
			SimpleNodeIterator it = cList.elements();
			while(it.hasMoreNodes()){
				Node cNode = it.nextNode();
				processNode(cNode);
			}
		}
//		NodeList list = node.getChildren();
		
	}



	private static String updateCode(String modelStr, Map<String, String> metadataMap, String sign) {
		Iterator<Entry<String, String>> it = metadataMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			String id = entry.getKey();
			String startStr = getStartString(id, sign);
			int start = modelStr.indexOf(getStartString(id, sign));
			if(start == -1){
				String value = entry.getValue();
				if(value == null || value.equals("") || value.equals("null"))
					return modelStr;
				modelStr = addToString(id, modelStr, value, sign);
			}
			else{
				String value = entry.getValue();
				if(value == null)
					value = "";
				start = start + startStr.length();
				int end = modelStr.indexOf(getEndString(), start);
				modelStr = updateString(id, modelStr, start, end, value, sign);
			}
		}
		return modelStr;
	}
	
	private static String getFile(String path) {
		try {
			return FileUtils.readFileToString(new File(path));
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private static String getStartString(String id, String type) {
		return "/*\n* definition of" + type + ":" + id + "\n*/";
	}

	private static String getEndString() {
		return "/*\n* definition end\n*/";
	}
	
	private static void updateFile(String path, String str) {
		try {
			FileUtils.writeStringToFile(new File(path), str);
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	

	private static String updateString(String id, String fullCodes, int start, int end, String updateStr, String sign) {
		return fullCodes.substring(0, start) + BR + generateCodes(id, updateStr, sign) + BR + fullCodes.substring(end);
	}

	private static String addToString(String id, String fullCodes, String metadata, String sign) {
		int lastIndex = fullCodes.lastIndexOf("}");
		fullCodes = fullCodes.substring(0, lastIndex) + BR + getStartString(id, sign) + BR + generateCodes(id, metadata, sign) + BR + getEndString() + BR + fullCodes.substring(lastIndex);
		return fullCodes;
	}


	private static String generateCodes(String id, String metadata, String sign) {
		if(sign.equals("metadata")){
			JSONObject obj = JSONObject.fromObject(metadata);
			String modelId = (String) obj.get("model");
			String url = modelId.substring(0, modelId.lastIndexOf("_model")) + "s";
			String serviceUrl = url;
			String str = "$app.model('" + modelId + "', {url:'" + serviceUrl + "'});\n";
//			str += "var metadata_" + id + " = " + metadata + ";" + BR;
			return str + "$app.metadata('" + id + "', " + JsonFormatTool.formatJson(metadata, "\t") + "\n);";
		}
		else{
			return TAB + JsonFormatTool.formatJson(metadata, "\t");
		}
	}
}
