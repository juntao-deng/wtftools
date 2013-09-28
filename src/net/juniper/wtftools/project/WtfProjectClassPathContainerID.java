package net.juniper.wtftools.project;


import net.juniper.wtftools.core.WtfToolsConstants;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public enum WtfProjectClassPathContainerID 
{
	JBoss_Library,
	ThdParty_Library;
	
	public IPath getPath()
	{
		return new Path(WtfToolsConstants.WTF_LIBRARY_CONTAINER_ID).append(name());
	}
}
