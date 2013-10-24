package net.juniper.wtftools.rest;

public class RestServiceGenerateor extends AbstractServiceGenerator {
	public RestServiceGenerateor(String entityPath) {
		super(entityPath);
	}

	@Override
	protected String getTargetPath() {
		return "net/juniper/space/restful/" + getTargetClassName() + ".java";
	}

	@Override
	protected String replaceFile(String template) {
		template = template.replace("#IMPORT_SERVICE#", "import net.juniper.space.services." + getEntityName() + "Service;");
		template = template.replace("#SERVICE_NAME#", getEntityName().toLowerCase() + "s");
		return template;
	}

	@Override
	protected String getTemplatePath() {
		return "rest";
	}
	
	private String getTargetClassName(){
		return getEntityName() + "RestService";
	}

	@Override
	protected String getSourcePath() {
		return "src/restapis";
	}

}
