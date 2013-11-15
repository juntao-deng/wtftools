package net.juniper.wtftools.rest;

public class LogicServiceGenerator extends AbstractServiceGenerator{

	public LogicServiceGenerator(String entityPath) {
		super(entityPath);
	}

	@Override
	protected String getTargetPath() {
		return "net/juniper/space/services/" + getTargetClassName() + ".java";
	}

	private String getTargetClassName() {
		return getSimpleName() + "Service";
	}

	@Override
	protected String replaceFile(String template) {
		return template;
	}

	@Override
	protected String getTemplatePath() {
		return "service";
	}

	@Override
	protected String getSourcePath() {
		return "src/services";
	}

}
