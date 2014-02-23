package net.juniper.scutools.builder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;

import net.juniper.scutools.ScuToolsActivator;
import net.juniper.scutools.common.ScuProjectCommonTools;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Juntao
 *
 */
public class ScuNodeProjectBuilder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = ScuNodeProjectBuilder.class.getName();
	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			//fullBuild(monitor);
		} 
		else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				//fullBuild(monitor);
			} 
			else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	class WtfDeltaVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			String deployPath = ScuProjectCommonTools.getDeploymentPath();
			String name = getProject().getName();
			String targetPath = findTargetName(deployPath, name);
			if(targetPath == null){
				ScuToolsActivator.getDefault().logError("error getting target path");
				return true;
			}
			else{
				ScuToolsActivator.getDefault().logInfo("got target path:" + targetPath);
			}
			
			IResource resource = delta.getResource();
			try{
				if(resource instanceof org.eclipse.core.internal.resources.File){
					org.eclipse.core.internal.resources.File f = (org.eclipse.core.internal.resources.File) resource;
					if(f.getFileExtension().equals("java") || f.getFileExtension().equals("class"))
						return true;
					String fullPath = targetPath + "/" + f.getProjectRelativePath().removeFirstSegments(1).toPortableString();
					switch (delta.getKind()) {
						case IResourceDelta.ADDED:
							ScuToolsActivator.getDefault().logInfo("adding file:" + fullPath);
							FileUtils.copyFile(new File(f.getLocation().toOSString()), new File(fullPath));
							break;
						case IResourceDelta.REMOVED:
							ScuToolsActivator.getDefault().logInfo("deleting file:" + fullPath);
							new File(fullPath).delete();
							break;
						case IResourceDelta.CHANGED:
							ScuToolsActivator.getDefault().logInfo("updating file:" + fullPath);
							FileUtils.copyFile(new File(f.getLocation().toOSString()), new File(fullPath));
							break;
					}
				}
			}
			catch(IOException e){
				ScuToolsActivator.getDefault().logError(e.getMessage(), e);
			}
			//return true to continue visiting children.
			return true;
		}
		
		private String findTargetName(String deployPath, String name) {
			final String warName = name += ".war";
			File[] fs = new File(deployPath).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(warName);
				}
			});
			if(fs.length > 0)
				return fs[0].getAbsolutePath();
			return null;
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new WtfDeltaVisitor());
	}
}
