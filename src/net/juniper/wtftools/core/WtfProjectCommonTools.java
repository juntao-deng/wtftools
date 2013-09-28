package net.juniper.wtftools.core;

import net.juniper.wtftools.WtfToolsActivator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public final class WtfProjectCommonTools {
	public static IPath getJbossHome(){
		String jbossHome = System.getenv("JBOSS_HOME");
		WtfToolsActivator.getDefault().logInfo("get jboss home path:" + jbossHome);
		return Path.fromOSString(jbossHome);
	}
}
