package net.juniper.wtftools.rest;

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
		template = template.replace("#MO_PACKAGE_NAME#", getPackageName().replaceAll("/", "."));
		template = template.replace("#SERVICE_NAME#", getSimpleName().toLowerCase());
		return template;
	}

	@Override
	protected String getTemplatePath() {
		return "mo";
	}

	@Override
	protected String getSourcePath() {
		return "src/restapis";
	}

}
