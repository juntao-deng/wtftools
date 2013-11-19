package net.juniper.wtftools.designer;

import java.util.Iterator;
import java.util.List;

import net.juniper.wtftools.designer.utils.JsEventDesc;
import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ClearEventsEventHandler extends AbstractBrowserEventHandler {

	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		String compId = json.getString("compId");
		JSONArray eventNames = (JSONArray) json.get("eventNames");
		List<JsEventDesc> eventList = editor.getExistingEvents();
		Iterator<JsEventDesc> it = eventList.iterator();
		while(it.hasNext()){
			JsEventDesc desc = it.next();
			if(desc.getCompId().equals(compId)){
				boolean find = false;
				for(int i = 0; i < eventNames.size(); i ++){
					if(eventNames.get(i).equals(desc.getName())){
						find = true;
						break;
					}
				}
				if(!find)
					desc.setDirty(true);
			}
		}
		return null;
	}

	@Override
	protected String getActionName() {
		return "clearEvents";
	}

}
