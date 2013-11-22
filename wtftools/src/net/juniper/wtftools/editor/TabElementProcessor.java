package net.juniper.wtftools.editor;

import java.util.Iterator;
import java.util.Vector;

import org.htmlparser.Node;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

public class TabElementProcessor implements ElementProcessor {
	private static WtfElementProcessor wtfProcess = new WtfElementProcessor();
	@Override
	public void process(Node parentNode) {
		NodeList nodeList = parentNode.getChildren();
		if(nodeList != null){
			Node node = getFirstDivNode(nodeList);
			NodeList newList = new NodeList();
			if(node != null){
				nodeList = node.getChildren();
				if(nodeList != null){
					SimpleNodeIterator it = nodeList.elements();
					while(it.hasMoreNodes()){
						Node cNode = it.nextNode();
						if(!(cNode instanceof Div)){
							continue;
						}
						cleanForTabItem((Div) cNode);
						newList.add(cNode);
						NodeList ccNodeList = cNode.getChildren();
						if(ccNodeList != null){
							SimpleNodeIterator ccIt = ccNodeList.elements();
							while(ccIt.hasMoreNodes()){
								Node ccNode = ccIt.nextNode();
								wtfProcess.process(ccNode);
							}
						}
					}
				}
			}
			//replace By new Children
			parentNode.setChildren(newList);
		}
	}
	private Node getFirstDivNode(NodeList nodeList) {
		SimpleNodeIterator it = nodeList.elements();
		while(it.hasMoreNodes()){
			Node node = it.nextNode();
			if(node instanceof Div)
				return node;
		}
		return null;
	}
	private void cleanForTabItem(Div cNode) {
		 //aria-labelledby="ui-id-2" class="ui-tabs-panel ui-widget-content ui-corner-bottom" role="tabpanel" style="display: none; " aria-expanded="false" aria-hidden="true"
		cNode.removeAttribute("aria-labelledby");
		cNode.removeAttribute("aria-expanded");
		cNode.removeAttribute("aria-hidden");
		cNode.removeAttribute("role");
		cNode.removeAttribute("class");
		cNode.removeAttribute("style");
	}

}

//<div id="tab1" wtftype="tab"   >
//
//
//<div id="tab" class="ui-tabs ui-widget ui-widget-content ui-corner-all">
//	
//	
//	<ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all" role="tablist">
//		
//		
//			
//		<li class="ui-state-default ui-corner-top" role="tab" tabindex="-1" aria-controls="item1" aria-labelledby="ui-id-2" aria-selected="false">
//			<a href="#item1" class="ui-tabs-anchor" role="presentation" tabindex="-1" id="ui-id-2">
//				TabItem1
//			</a>
//		</li>
//		
//		
//			
//		<li class="ui-state-default ui-corner-top ui-tabs-active ui-state-active" role="tab" tabindex="0" aria-controls="item2" aria-labelledby="ui-id-3" aria-selected="true">
//			<a href="#item2" class="ui-tabs-anchor" role="presentation" tabindex="-1" id="ui-id-3">
//				TabItem2
//			</a>
//		</li>
//		
//		
//	
//	</ul>
//	
//	
//	<div id="item1" aria-labelledby="ui-id-2" class="ui-tabs-panel ui-widget-content ui-corner-bottom" role="tabpanel" style="display: none; " aria-expanded="false" aria-hidden="true">
//		<div wtftype="container" class="design_minheight" >
//			<div id="button1" wtftype="button"  class="inline" >
//			</div>
//		</div>
//	</div>
//	<div id="item2" aria-labelledby="ui-id-3" class="ui-tabs-panel ui-widget-content ui-corner-bottom" role="tabpanel" style="display: block; " aria-expanded="true" aria-hidden="false">
//		<div wtftype="container" class="design_minheight" >
//			<div id="button2" wtftype="button">
//			</div>
//		</div>
//	</div>
//</div>
//
//
//</div>