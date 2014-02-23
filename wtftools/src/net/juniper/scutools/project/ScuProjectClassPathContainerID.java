package net.juniper.scutools.project;

import net.juniper.scutools.common.ScuToolsConstants;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public enum ScuProjectClassPathContainerID {
//	Middleware_Library,
//	Product_Common,
//	WTF_BASIC_Library;
	;
	public IPath getPath(){
		return new Path(ScuToolsConstants.SCU_LIBRARY_CONTAINER_ID).append(name());
	}
}
