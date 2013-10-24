package net.juniper.wtftools.rest;

import java.io.File;
import java.io.IOException;

import net.juniper.wtftools.core.WtfProjectCommonTools;

import org.apache.commons.io.FileUtils;

public abstract class AbstractServiceGenerator implements ServiceGenerator {
	private String entityPath;
	
	public AbstractServiceGenerator(String entityPath){
		this.entityPath = entityPath;
	}
	protected String getEntityName(){
		return entityPath.substring(entityPath.lastIndexOf(".") + 1);
	}
	
	@Override
	public void run() throws IOException {
		String tPath = getTemplatePath();
		tPath = WtfProjectCommonTools.getFrameworkWebLocation() + "/init/backend/" + tPath;
		String template = readTemplateFile(tPath);
		template = doReplace(template);
		String path = getTargetPath();
		path = WtfProjectCommonTools.getCurrentWtfProject().getLocation().toOSString() + "/" + getSourcePath() + "/" + path;
		writeToSystem(template, path);
	}
	
	public boolean exist() {
		String path = getTargetPath();
		path = WtfProjectCommonTools.getCurrentWtfProject().getLocation().toOSString() + "/" + getSourcePath() + "/" + path;
		return new File(path).exists();
	}

	private String doReplace(String template) {
		template = replaceEntityImport(template);
		template = replaceEntityName(template);
		return replaceFile(template);
	}
	private void writeToSystem(String template, String path) throws IOException {
		FileUtils.writeStringToFile(new File(path), template);
	}

	protected abstract String getTargetPath();

	protected abstract String replaceFile(String template);

	protected abstract String getTemplatePath();

	private String readTemplateFile(String path) throws IOException {
		return FileUtils.readFileToString(new File(path));
	}
	
	protected String replaceEntityImport(String template){
		return template.replaceAll("#IMPORT_ENTITY#", "import " + this.entityPath + ";");
	}
	
	protected String replaceEntityName(String template){
		return template.replaceAll("#ENTITY_NAME#", getEntityName());
	}
	
	protected abstract String getSourcePath();
}
