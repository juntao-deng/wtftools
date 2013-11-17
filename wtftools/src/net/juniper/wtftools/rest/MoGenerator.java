package net.juniper.wtftools.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.juniper.wtftools.core.WtfProjectCommonTools;

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
		getMethodDesc();
		template = template.replace("#MO_PACKAGE_NAME#", getPackageName().replaceAll("/", "."));
		template = template.replace("#SERVICE_NAME#", getSimpleName().toLowerCase());
		return template;
	}

	private void getMethodDesc() {
		try {
			List<MethodDesc> descList = new ArrayList<MethodDesc>();
			Class c = Class.forName(entityPath, true, WtfProjectCommonTools.getCurrentProjectClassLoader());
			Method[] mds = c.getDeclaredMethods();
			for(Method m : mds){
				String name = m.getName();
				if(name.startsWith("get") && m.getParameterTypes().length == 0){
					MethodDesc desc = new MethodDesc();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
