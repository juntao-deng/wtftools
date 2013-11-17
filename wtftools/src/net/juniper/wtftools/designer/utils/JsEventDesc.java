package net.juniper.wtftools.designer.utils;

import java.io.Serializable;

public class JsEventDesc implements Serializable {
	private static final long serialVersionUID = -3331080908570086067L;
	private String name;
	private String func;
	private String compId;
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
}
