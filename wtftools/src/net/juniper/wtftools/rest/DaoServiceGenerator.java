package net.juniper.wtftools.rest;

public class DaoServiceGenerator extends AbstractServiceGenerator {
	public DaoServiceGenerator(String entityPath) {
		super(entityPath);
	}

	@Override
	protected String getTargetPath() {
		return "net/juniper/space/dao/" + getTargetClassName() + ".java";
	}

	@Override
	protected String replaceFile(String template) {
		return template;
	}

	@Override
	protected String getTemplatePath() {
		return "dao";
	}

	private String getTargetClassName(){
		return getEntityName() + "Repository";
	}

	@Override
	protected String getSourcePath() {
		return "src/implements";
	}
}
