package net.juniper.scutools.rest;

public class RestItfServiceGenerateor extends AbstractServiceGenerator {
	public RestItfServiceGenerateor(String entityPath) {
		super(entityPath);
	}

	@Override
	protected String getTargetPath() {
		return "net/juniper/space/restful/" + getTargetClassName() + ".java";
	}

	@Override
	protected String replaceFile(String template) {
		template = template.replace("#REST_NAME#", getSimpleName() + "RestService;");
		template = template.replace("#SERVICE_NAME#", getEntityName().toLowerCase() + "s");
		return template;
	}

	@Override
	protected String getTemplatePath() {
		return "restitf";
	}
	
	private String getTargetClassName(){
		return getSimpleName() + "RestService";
	}

	@Override
	protected String getSourcePath() {
		return "src/restapis";
	}

}
