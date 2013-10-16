package net.juniper.wtftools.designer;

import net.sf.json.JSONObject;

public interface IBrowserEventHandler {
	public static final String ACTION = "action";
	public boolean canHanle(JSONObject json);
	public JSONObject handle(JSONObject json);
}
