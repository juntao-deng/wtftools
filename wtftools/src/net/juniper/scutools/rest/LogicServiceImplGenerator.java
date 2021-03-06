package net.juniper.scutools.rest;

public class LogicServiceImplGenerator extends AbstractServiceGenerator{

	public LogicServiceImplGenerator(String entityPath) {
		super(entityPath);
	}

	@Override
	protected String getTargetPath() {
		return "net/juniper/space/services/impl/" + getTargetClassName() + ".java";
	}

	private String getTargetClassName() {
		return getSimpleName() + "ServiceImpl";
	}

	@Override
	protected String replaceFile(String template) {
		template = template.replace("#IMPORT_SERVICE#", "import net.juniper.space.services." + getSimpleName() + "Service;");
		template = template.replace("#SERVICE_PATH#", "net.juniper.space.services." + getSimpleName() + "Service");
		template = template.replace("#IMPORT_REPOSITORY#", "import net.juniper.space.dao." + getEntityName() + "Repository;");
		return template;
	}

	@Override
	protected String getTemplatePath() {
		return "serviceimpl";
	}

	@Override
	protected String getSourcePath() {
		return "src/implements";
	}

}
