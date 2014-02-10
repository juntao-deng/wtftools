package net.juniper.wtftools.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public interface IProjectProvider{
	public String getProjectName();

	public IProject getProject();

	public IPath getLocationPath();
	
	public boolean isWithEjb();
	
	public boolean isWithHome();
	
	public String getContext();
}