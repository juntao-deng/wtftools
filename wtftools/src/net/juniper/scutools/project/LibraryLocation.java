package net.juniper.scutools.project;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class LibraryLocation{
	private IResource libPath;
	@Override
	public String toString(){
		return getLibResource().getName();
	}


	public LibraryLocation(){
	}

	public LibraryLocation(IResource libPath){
		this.libPath = libPath;
	}

	public String getDocLocation(){
		IResource res = getLibResource();
		if (res instanceof IFile){
			IFile file = (IFile) res;
			IFile docjar = file.getParent().getFile(new Path(file.getName().substring(0, file.getName().lastIndexOf('.')) + "-javadoc." + file.getFileExtension()));
			if (docjar.exists())
			{
				return "jar:file:/" + docjar.getLocation().toOSString() + "!/";
			}
		}
		return null;
	}

	public IPath getSrcPath(){
		IResource res = getLibResource();
		if (res instanceof IFile){
			IFile file = (IFile) res;
			IFile srcjar = file.getParent().getFile(new Path(file.getName().substring(0, file.getName().lastIndexOf('.')) + "-sources." + file.getFileExtension()));
			if (srcjar.getFullPath().toFile().exists())
			{
				return srcjar.getFullPath();
			}
		}
		else if (res instanceof IFolder){
			IFolder file = (IFolder) res;
			IFolder src = file.getParent().getFolder(new Path("source"));
			if (src.exists()){
				return src.getFullPath();
			}
			src = file.getParent().getFolder(new Path("sources"));
			if (src.exists()){
				return src.getFullPath();
			}
			src = file.getParent().getFolder(new Path("src"));
			if (src.exists()){
				return src.getFullPath();
			}
		}
		return null;
	}

	public IPath getLibPath(){
		return getLibResource().getFullPath();
	}

	public IResource getLibResource(){
		return libPath;
	}

	public void setLibResource(IResource libPath){
		this.libPath = libPath;
	}
}