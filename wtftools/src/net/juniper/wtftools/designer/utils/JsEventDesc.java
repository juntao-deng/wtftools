package net.juniper.wtftools.designer.utils;

import java.io.Serializable;

public class JsEventDesc implements Serializable {
	private static final long serialVersionUID = -3331080908570086067L;
	public static final String TYPE_MODEL = "1";
	public static final String TYPE_COMPONENT = "0";
	public static final String TYPE_APP = "2";
	private String name;
	private String func;
	private String compId;
	private String type;
	
	private boolean dirty = false;
	public String getCompId() {
		return compId;
	}
	public void setCompId(String compId) {
		this.compId = compId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFunc() {
		return func;
	}
	public void setFunc(String func) {
		this.func = func;
	}
	public void setDirty(boolean b) {
		this.dirty = b;
	}
	public boolean isDirty(){
		return this.dirty;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
