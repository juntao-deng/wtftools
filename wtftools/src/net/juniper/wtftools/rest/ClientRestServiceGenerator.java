package net.juniper.wtftools.rest;

public class ClientRestServiceGenerator extends AbstractServiceGenerator {

	public ClientRestServiceGenerator(String entityPath) {
		super(entityPath);
	}

	@Override
	protected String getTargetPath() {
		return "rest/" + getTargetClassName() + ".js";
	}

	private String getTargetClassName() {
		return getEntityName().toLowerCase() + "s";
	}

	@Override
	protected String replaceFile(String template) {
		template = template.replace("#SERVICE_NAME#", getEntityName().toLowerCase() + "s");
		return template;
	}

	@Override
	protected String getTemplatePath() {
		return "restjs";
	}

	@Override
	protected String getSourcePath() {
		return "web";
	}

}
