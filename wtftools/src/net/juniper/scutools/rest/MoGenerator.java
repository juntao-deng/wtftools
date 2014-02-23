package net.juniper.scutools.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.juniper.scutools.ScuToolsActivator;
import net.juniper.scutools.common.ScuProjectCommonTools;

import org.apache.commons.lang.StringUtils;

public class MoGenerator extends AbstractServiceGenerator{

	public MoGenerator(String entityPath) {
		super(entityPath);
	}

	@Override
	protected String getTargetPath() {
		return getPackageName() + "/" + getTargetClassName() + ".java";
	}

	private String getPackageName() {
		return "net/juniper/space/models/" + getSimpleName().toLowerCase();
	}

	private String getTargetClassName() {
		return getMOName();
	}

	@Override
	protected String replaceFile(String template) {
		List<MethodDesc> methodDesc = getMethodDesc();
		String propStr = getPropsStr(methodDesc);
		String fieldStr = getFieldStr(methodDesc);
		String methodStr = getMethodStr(methodDesc);
		template = template.replace("#MO_PACKAGE_NAME#", getPackageName().replaceAll("/", "."));
		template = template.replace("#SERVICE_NAME#", getSimpleName().toLowerCase());
		return template;
	}

	private String getFieldStr(List<MethodDesc> methodDesc) {
		StringBuffer buf = new StringBuffer();
		Iterator<MethodDesc> it = methodDesc.iterator();
		while(it.hasNext()){
			MethodDesc method = it.next();
			buf.append("\tprivate ");
			buf.append(getTypeString(method.fullType)).append(" ");
			buf.append(method.name);
			if(it.hasNext())
				buf.append(",");
		}
		return buf.toString();
	}

	private String getTypeString(String fullType){
		if(fullType.equals(String.class.getName()))
			return "String";
		else if(fullType.equals(Integer.class.getName()))
			return "Integer";
		return "String";
	}
	
	private String getMethodStr(List<MethodDesc> methodDesc) {
		StringBuffer buf = new StringBuffer();
		Iterator<MethodDesc> it = methodDesc.iterator();
		while(it.hasNext()){
			MethodDesc method = it.next();
			buf.append("\n\tpublic void set").append(StringUtils.capitalize(method.name)).append("(");
			buf.append(getTypeString(method.fullType)).append(" ");
			buf.append(method.name);
			buf.append("){\n");
			buf.append("\t\t").append("return ");
			buf.append(method.name).append(";\n");
			buf.append("\t}\n");
			
			buf.append("\n\tpublic ");
			
			//void get").append(StringUtils.capitalize(method.name)).append("(");
			if(method.fullType.equals(String.class.getName()))
				buf.append("String ");
			else if(method.fullType.equals(Integer.class.getName()))
				buf.append("Integer ");
			buf.append(method.name);
			buf.append("){\n");
			buf.append("\t\t").append("return ");
			buf.append(method.name).append(";\n");
			buf.append("\t}\n");
			
			if(it.hasNext())
				buf.append(",");
		}
		return buf.toString();
	}

	private String getPropsStr(List<MethodDesc> methodDesc) {
		StringBuffer buf = new StringBuffer();
		Iterator<MethodDesc> it = methodDesc.iterator();
		while(it.hasNext()){
			MethodDesc method = it.next();
			buf.append(method.name);
			if(it.hasNext())
				buf.append(",");
		}
		return buf.toString();
	}

	private List<MethodDesc> getMethodDesc() {
		try {
			List<MethodDesc> descList = new ArrayList<MethodDesc>();
			Class c = Class.forName(entityPath, true, ScuProjectCommonTools.getCurrentProjectClassLoader());
			Method[] mds = c.getDeclaredMethods();
			for(Method m : mds){
				String methodName = m.getName();
				if(methodName.startsWith("get") && m.getParameterTypes().length == 0){
					MethodDesc desc = new MethodDesc();
					String name = methodName.substring("get".length()).toLowerCase();
					desc.name = name;
					Class<?> rtC = m.getReturnType();
					if(rtC.equals(String.class)){
						desc.type = "String";
					}
					else if(rtC.equals(Integer.class)){
						desc.type = "Integer";
					}
					else if(rtC.equals(Date.class)){
						desc.type = "Date";
					}
					descList.add(desc);
				}
			}
			return descList;
		} 
		catch (Exception e) {
			ScuToolsActivator.getDefault().logError(e);
			return null;
		}
	}

	@Override
	protected String getTemplatePath() {
		return "mo";
	}

	@Override
	protected String getSourcePath() {
		return "src/restapis";
	}

	class MethodDesc{
		private String type;
		private String fullType;
		private String name;
	}
}
