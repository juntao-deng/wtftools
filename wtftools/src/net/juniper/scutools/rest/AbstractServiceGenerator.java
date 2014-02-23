package net.juniper.scutools.rest;

import java.io.File;
import java.io.IOException;

import net.juniper.scutools.common.ScuProjectCommonTools;

import org.apache.commons.io.FileUtils;

public abstract class AbstractServiceGenerator implements ServiceGenerator {
	protected String entityPath;
	
	public AbstractServiceGenerator(String entityPath){
		this.entityPath = entityPath;
	}
	protected String getEntityName(){
		return entityPath.substring(entityPath.lastIndexOf(".") + 1);
	}
	
	protected String getMOName(){
		return getSimpleName() + "MO";
	}
	
	protected String getSimpleName(){
		String entityName = getEntityName();
		if(entityName.endsWith("Entity"))
			entityName = entityName.substring(0, "Entity".length());
		return entityName;
	}
	protected String getMOPath() {
		return "net.juniper.space.models." + getSimpleName().toLowerCase() + "." + getMOName();
	}
	
	@Override
	public String run() throws IOException {
		String tPath = getTemplatePath();
		tPath = ScuProjectCommonTools.getFrameworkWebLocation() + "/init/backend/" + tPath;
		String template = readTemplateFile(tPath);
		template = doReplace(template);
		String path = getTargetPath();
		String fullPath = ScuProjectCommonTools.getCurrentWtfProject().getLocation().toOSString() + "/" + getSourcePath() + "/" + path;
		writeToSystem(template, fullPath);
		return path;
	}
	
	public boolean exist() {
		String path = getTargetPath();
		path = ScuProjectCommonTools.getCurrentWtfProject().getLocation().toOSString() + "/" + getSourcePath() + "/" + path;
		return new File(path).exists();
	}
	
	private String doReplace(String template) {
		template = replaceEntityImport(template);
		template = replaceMOImport(template);
		template = replaceEntityName(template);
		template = replaceMOName(template);
		template = replaceSimpleName(template);
		return replaceFile(template);
	}
	private void writeToSystem(String template, String path) throws IOException {
		File f = new File(path);
		if(f.exists()){
			f.delete();
		}
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
	
	protected String replaceMOImport(String template){
		return template.replaceAll("#IMPORT_MO#", "import " + this.getMOPath() + ";");
	}
	
	protected String replaceEntityName(String template){
		return template.replaceAll("#ENTITY_NAME#", getEntityName());
	}
	protected String replaceMOName(String template){
		return template.replaceAll("#MO_NAME#", getMOName());
	}
	protected String replaceSimpleName(String template){
		return template.replaceAll("#ENTITY_SIMPLENAME#", getSimpleName());
	}
	
	
	protected abstract String getSourcePath();
}
