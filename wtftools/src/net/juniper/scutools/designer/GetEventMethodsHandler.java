package net.juniper.scutools.designer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.juniper.scutools.designer.utils.JsEventDesc;
import net.juniper.scutools.designer.utils.JsEventFileParser;
import net.juniper.scutools.designer.utils.JsURLEncoder;
import net.juniper.scutools.designer.utils.WtfStringUtils;
import net.juniper.scutools.editor.BrowserDesignEditor;
import net.sf.json.JSONObject;

public class GetEventMethodsHandler extends AbstractBrowserEventHandler {

	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		String id = json.getString("compId");
		JSONObject result = new JSONObject();
		JsEventDesc[] descs = eventDescs(editor.getExistingEvents(), id);
		String eventStr = new String();
		if(descs.length > 0){
			for(int i = 0; i < descs.length; i ++){
				JsEventDesc desc = descs[i];
				eventStr += desc.getName();
				if(i != descs.length - 1)
					eventStr += ",";
				result.put("method_" + desc.getName(), JsURLEncoder.encode(WtfStringUtils.removeTab(desc.getFunc())));
			}
			result.put("methods", eventStr);
		}
		
		descs = eventDescs(editor.getExistingEvents(), JsEventFileParser.GLOBAL);
		if(descs != null && descs.length > 0){
			result.put("methodGlobalContent", JsURLEncoder.encode(WtfStringUtils.removeTab(descs[0].getFunc())));
		}
		return result;
	}
	
	public JsEventDesc[] eventDescs(List<JsEventDesc> eventList, String id){
		Iterator<JsEventDesc> it = eventList.iterator();
		List<JsEventDesc> list = new ArrayList<JsEventDesc>();
		while(it.hasNext()){
			JsEventDesc desc = it.next();
			if(desc.getCompId().equals(id)){
				list.add(desc);
			}
		}
		return list.toArray(new JsEventDesc[0]);
	}

	@Override
	protected String getActionName() {
		return "eventlist";
	}

}
