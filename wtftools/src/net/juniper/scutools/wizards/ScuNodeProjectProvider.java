package net.juniper.scutools.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
/**
 * A project info provider for nodejs type SCU project wizard.
 * @author Juntao
 *
 */
public class ScuNodeProjectProvider implements IProjectProvider{
	private String projectName;
	private IProject project;
	private IPath locationPath;
	private boolean withHome;
	private String context;
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public IProject getProject() {
		return project;
	}
	public void setProject(IProject project) {
		this.project = project;
	}
	public IPath getLocationPath() {
		return locationPath;
	}
	public void setLocationPath(IPath locationPath) {
		this.locationPath = locationPath;
	}
	public boolean isWithHome() {
		return withHome;
	}
	public void setWithHome(boolean withHome) {
		this.withHome = withHome;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	
}
