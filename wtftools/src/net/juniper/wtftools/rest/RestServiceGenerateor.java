package net.juniper.wtftools.rest;

public class RestServiceGenerateor extends AbstractServiceGenerator {
	public RestServiceGenerateor(String entityPath) {
		super(entityPath);
	}

	@Override
	protected String getTargetPath() {
		return "net/juniper/space/restful/impl/" + getTargetClassName() + ".java";
	}

	@Override
	protected String replaceFile(String template) {
		template = template.replace("#IMPORT_RESTSERVICE#", "import net.juniper.space.restful." + getSimpleName() + "RestService;");
		template = template.replace("#REST_NAME#", getSimpleName() + "RestService");
		template = template.replace("#IMPORT_SERVICE#", "import net.juniper.space.services." + getSimpleName() + "Service;");
		return template;
	}

	@Override
	protected String getTemplatePath() {
		return "rest";
	}
	
	private String getTargetClassName(){
		return getSimpleName() + "RestServiceImpl";
	}

	@Override
	protected String getSourcePath() {
		return "src/implements";
	}

}
