package net.juniper.wtftools.designer;

import java.util.Map;

public interface IBrowserEventHandler {
	public static final String ACTION = "action";
	public boolean canHanle(Map<String, Object> json);
	public Map<String, Object> handle(Map<String, Object> json);
}
