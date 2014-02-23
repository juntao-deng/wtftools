package net.juniper.scutools.editor;

import org.htmlparser.Node;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

public class WtfElementProcessor implements ElementProcessor {
	private static TabElementProcessor tabProcess = new TabElementProcessor();
	private static WizardElementProcessor wizardProcess = new WizardElementProcessor();
	@Override
	public void process(Node node) {
		NodeList cList = node.getChildren();
		if(node instanceof Div){
			String wtftype = ((Div)node).getAttribute("wtftype");
			if(wtftype != null && !wtftype.equals("container")){
				if(wtftype.equals("tab") || wtftype.equals("card")){
					tabProcess.process(node);
				}
				else if(wtftype.equals("wizard")){
					wizardProcess.process(node);
				}
				else{
					//clean children, except for wtfpos
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
			
			//clean runtime sign
			((Div) node).removeAttribute("wtfdone");
			((Div) node).removeAttribute("designable");
			
			String className = ((Div) node).getAttribute("class");
			if(className != null && className.contains(" designele_sign")){
				className = className.replace(" designele_sign", "");
			}
			if(className != null && className.contains(" designele")){
				className = className.replace(" designele", "");
			}
			if(className != null && className.contains("wtfinline")){
				className = className.replace("wtfinline", "");
			}
			if(className != null && !className.trim().equals(""))
				((Div) node).setAttribute("class", className);
			else
				((Div) node).removeAttribute("class");
		}
		
		if(cList != null){
			SimpleNodeIterator it = cList.elements();
			while(it.hasMoreNodes()){
				Node cNode = it.nextNode();
				process(cNode);
			}
		}
	}

	private void processForPos(NodeList newCList, Node cNode) {
		NodeList ccNodeList = cNode.getChildren();
		if(cNode instanceof Div && ((Div)cNode).getAttribute("wtfpos") != null){
			if(ccNodeList != null){
				SimpleNodeIterator it = ccNodeList.elements();
				while(it.hasMoreNodes()){
					Node ccNode = it.nextNode();
					process(ccNode);
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
}
