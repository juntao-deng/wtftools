package net.juniper.scutools.project;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;

public class ClasspathContainer implements IClasspathContainer{
	protected IClasspathEntry[]	entries;
	private WtfProjectClassPathContainerID	id;

	public ClasspathContainer(WtfProjectClassPathContainerID id, IClasspathEntry[] entries){
		this.id = id;
		this.entries = entries;
	}

	@Override
	public int getKind(){
		return K_APPLICATION;
	}

	@Override
	public IClasspathEntry[] getClasspathEntries(){
		return entries;
	}

	@Override
	public String getDescription(){
		return id.name();
	}

	@Override
	public IPath getPath(){
		return id.getPath();
	}

	public void setClasspathEntries(IClasspathEntry[] entries){
		this.entries = entries;
	}
}
