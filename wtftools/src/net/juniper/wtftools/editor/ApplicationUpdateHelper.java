package net.juniper.wtftools.editor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.designer.utils.JsEventDesc;
import net.juniper.wtftools.designer.utils.JsEventFileParser;
import net.juniper.wtftools.designer.utils.JsonFormatTool;
import net.juniper.wtftools.designer.utils.WtfStringUtils;

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

	public static void updateModel(IPath appPath, Map<String, String> modelMap) {
		String metadataPath = appPath.append("model.js").toOSString();
		String modelStr = getFile(metadataPath);
		modelStr = fixModelContent(modelStr);
		modelStr = updateModel(modelStr, modelMap);
		updateFile(metadataPath, modelStr);
	}
	
	
	private static String updateModel(String modelStr, Map<String, String> modelMap) {
		Iterator<Entry<String, String>> it = modelMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			String id = entry.getKey();
			String startStr = "$app.model('" + id + "'";
			int index = modelStr.indexOf(startStr);
			int endIndex = -1;
			if(index == -1){
				index = modelStr.lastIndexOf("});");
			}
			else{
				endIndex = modelStr.indexOf(");\n", index) + ");\n".length();
			}
			
			String replaceStr = "$app.model('" + id + "', " + JsonFormatTool.formatJson(entry.getValue(), "\t") + "\n);\n";
			modelStr = WtfStringUtils.replaceString(index, endIndex, modelStr, replaceStr);
		}
		return modelStr;
	}


	public static void updateMetadataAndHtml(IPath appPath, IPath restPath, IPath htmlPath, Map<String, String> metadataMap, String htmlContent){
		String metadataPath = appPath.append("model.js").toOSString();
		String modelStr = getFile(metadataPath);
		modelStr = fixModelContent(modelStr);
		modelStr = updateMetadata(modelStr, metadataMap);
		updateFile(metadataPath, modelStr);
		
		htmlContent = fixHtml(htmlContent);
		updateFile(htmlPath.toOSString(), htmlContent);
	}
	
	private static String fixModelContent(String modelStr) {
		if(modelStr == null || modelStr.trim().equals(""))
			modelStr = "wdefine(function(){\n});";
		return modelStr;
	}

	public static void updateController(IPath controllerPath, List<JsEventDesc> eventList){
		try {
			String controllerStr = FileUtils.readFileToString(controllerPath.toFile());
			controllerStr = fixControllerContent(controllerStr);
			int index = controllerStr.indexOf(JsEventFileParser.GLOBALBEGIN) + (JsEventFileParser.GLOBALBEGIN + "\n").length();
			int endIndex = controllerStr.indexOf(JsEventFileParser.GLOBALEND);
			
			String content = getGlobalMethodContent(eventList);
			if(content != null && !content.equals("")){
				content = WtfStringUtils.addTab(getGlobalMethodContent(eventList)) + "\n";
				controllerStr = WtfStringUtils.replaceString(index, endIndex, controllerStr, content);
			}

			Iterator<JsEventDesc> it = eventList.iterator();
			int eventStart = controllerStr.indexOf(JsEventFileParser.EVENTBEGIN) + (JsEventFileParser.EVENTBEGIN + "\n").length();
			while(it.hasNext()){
				JsEventDesc desc = it.next();
				if(desc.getName().equals(JsEventFileParser.GLOBAL))
					continue;
				if(desc.isDirty()){
					controllerStr = updateDirtyEventController(controllerStr, desc, eventStart);
				}
				else
					controllerStr = updateEventController(controllerStr, desc, eventStart);
			}
			FileUtils.writeStringToFile(controllerPath.toFile(), controllerStr);
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
		}
	}
	
	/**
	 * fix content for global scope and event scope
	 * @param controllerStr
	 * @return
	 */
	private static String fixControllerContent(String controllerStr) {
		if(controllerStr == null || controllerStr.trim().equals(""))
			controllerStr = "wdefine(function(){\n});";
		int index = controllerStr.indexOf(JsEventFileParser.GLOBALBEGIN);
		if(index == -1){
			String modelStr = "wdefine(function(){\n";
			index = controllerStr.indexOf(modelStr) + modelStr.length();
			//add globalbegin/end
			controllerStr = WtfStringUtils.replaceString(index, -1, controllerStr, JsEventFileParser.GLOBALBEGIN + "\n" + JsEventFileParser.GLOBALEND + "\n");
		}
		
		index = controllerStr.indexOf(JsEventFileParser.EVENTBEGIN);
		if(index == -1){
			index = controllerStr.indexOf(JsEventFileParser.GLOBALEND) + (JsEventFileParser.GLOBALEND + "\n").length();
			//add globalbegin/end
			controllerStr = WtfStringUtils.replaceString(index, -1, controllerStr, "\n" + JsEventFileParser.EVENTBEGIN + "\n" + JsEventFileParser.EVENTEND + "\n");
		}
		return controllerStr;
	}

	private static String updateDirtyEventController(String controllerStr, JsEventDesc eventDesc, int eventStart) {
		String patternStr = null;
		if(eventDesc.getType().equals(JsEventDesc.TYPE_COMPONENT))
			patternStr = JsEventFileParser.COMPONENT_EVENT_PATTERN;
		else if(eventDesc.getType().equals(JsEventDesc.TYPE_MODEL))
			patternStr = JsEventFileParser.MODEL_EVENT_PATTERN;
		else if(eventDesc.getType().equals(JsEventDesc.TYPE_APP))
			patternStr = JsEventFileParser.APP_EVENT_PATTERN;
		Pattern p = Pattern.compile(patternStr, Pattern.DOTALL);
		Matcher m = p.matcher(controllerStr);
		while(m.find()){
			String compId = m.group(1);
			String eventName = m.group(2);
			if(compId.equals(eventDesc.getCompId()) && eventName.equals(eventDesc.getName())){
				return m.replaceFirst("");
			}
		}
		return controllerStr;
	}
	
	private static String updateEventController(String controllerStr, JsEventDesc eventDesc, int eventStart) {
		
		String script = "\n" + WtfStringUtils.addTab(eventDesc.getFunc()) + "\n";
		String patternStr = null;
		if(eventDesc.getType().equals(JsEventDesc.TYPE_COMPONENT))
			patternStr = JsEventFileParser.COMPONENT_EVENT_PATTERN;
		else if(eventDesc.getType().equals(JsEventDesc.TYPE_MODEL))
			patternStr = JsEventFileParser.MODEL_EVENT_PATTERN;
		else if(eventDesc.getType().equals(JsEventDesc.TYPE_APP))
			patternStr = JsEventFileParser.APP_EVENT_PATTERN;
		Pattern p = Pattern.compile(patternStr, Pattern.DOTALL);
		Matcher m = p.matcher(controllerStr);
		while(m.find()){
			String compId = m.group(1);
			String eventName = m.group(2);
			if(compId.equals(eventDesc.getCompId()) && eventName.equals(eventDesc.getName())){
				int start = m.start(3);
				int end = m.end(5);
				return WtfStringUtils.replaceString(start, end, controllerStr, script);
			}
		}
		//didn't find, add to tail
		String compId = eventDesc.getCompId();
		String keyPart = null;
		if(eventDesc.getType().equals(JsEventDesc.TYPE_MODEL))
			keyPart = "$app.model('" + compId + "')";
		else if(eventDesc.getType().equals(JsEventDesc.TYPE_COMPONENT))
			keyPart = "$app.component('" + compId + "')";
		else if(eventDesc.getType().equals(JsEventDesc.TYPE_APP))
			keyPart = "$app";
		String eventName = eventDesc.getName();
		String str = keyPart + ".on('" + eventName + "'";
		String eventStr = str + ", function(options){" + script + "});\n";
		return WtfStringUtils.replaceString(eventStart, -1, controllerStr, eventStr);
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
		if(node instanceof Div && ((Div)node).getAttribute("wtftype") != null && (!((Div)node).getAttribute("wtftype").equals("container"))){
			if(((Div)node).getAttribute("wtftype").equals("tab") || ((Div)node).getAttribute("wtftype").equals("card")){
				processTab(node);
			}
			else{
				NodeList newCList = new NodeList();
				if(cList != null){
					SimpleNodeIterator it = cList.elements();
					while(it.hasMoreNodes()){
						Node cNode = it.nextNode();
						processForPos(newCList, cNode);
						
					}
				}
				node.setChildren(newCList);
			}
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
	}

	private static void processTab(Node node) {
		
	}

	private static void processForPos(NodeList newCList, Node cNode) {
		NodeList ccNodeList = cNode.getChildren();
		if(cNode instanceof Div && ((Div)cNode).getAttribute("wtfpos") != null){
			if(ccNodeList != null){
				SimpleNodeIterator it = ccNodeList.elements();
				while(it.hasMoreNodes()){
					Node ccNode = it.nextNode();
					processNode(ccNode);
				}
				newCList.add(ccNodeList);
			}
		}
		else{
			if(ccNodeList != null){
				SimpleNodeIterator it = ccNodeList.elements();
				while(it.hasMoreNodes()){
					Node ccNode = it.nextNode();
					processForPos(newCList, ccNode);
				}
			}
		}
	}


	private static String updateMetadata(String modelStr, Map<String, String> metadataMap) {
		Iterator<Entry<String, String>> it = metadataMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			String id = entry.getKey();
			String startStr = "$app.metadata('" + id + "'";
			int index = modelStr.indexOf(startStr);
			int endIndex = -1;
			if(index == -1){
				index = modelStr.lastIndexOf("});");
			}
			else{
				endIndex = modelStr.indexOf(");\n", index) + ");\n".length();
			}
			String replaceStr = "$app.metadata('" + id + "', " + JsonFormatTool.formatJson(entry.getValue(), "\t") + "\n);\n";
			modelStr = WtfStringUtils.replaceString(index, endIndex, modelStr, replaceStr);
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
}
