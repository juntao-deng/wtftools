package net.juniper.wtftools.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class WtfProjectBuilder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = "net.juniper.wtftools.builder.WtfProjectBuilder";
	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		return null;
	}

//	class SampleDeltaVisitor implements IResourceDeltaVisitor {
//		/*
//		 * (non-Javadoc)
//		 * 
//		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
//		 */
//		public boolean visit(IResourceDelta delta) throws CoreException {
//			IResource resource = delta.getResource();
//			switch (delta.getKind()) {
//			case IResourceDelta.ADDED:
//				// handle added resource
//				checkXML(resource);
//				break;
//			case IResourceDelta.REMOVED:
//				// handle removed resource
//				break;
//			case IResourceDelta.CHANGED:
//				// handle changed resource
//				checkXML(resource);
//				break;
//			}
//			//return true to continue visiting children.
//			return true;
//		}
//	}
//
//	class SampleResourceVisitor implements IResourceVisitor {
//		public boolean visit(IResource resource) {
//			checkXML(resource);
//			//return true to continue visiting children.
//			return true;
//		}
//	}
//
//	class XMLErrorHandler extends DefaultHandler {
//		
//		private IFile file;
//
//		public XMLErrorHandler(IFile file) {
//			this.file = file;
//		}
//
//		private void addMarker(SAXParseException e, int severity) {
//			WtfProjectBuilder.this.addMarker(file, e.getMessage(), e
//					.getLineNumber(), severity);
//		}
//
//		public void error(SAXParseException exception) throws SAXException {
//			addMarker(exception, IMarker.SEVERITY_ERROR);
//		}
//
//		public void fatalError(SAXParseException exception) throws SAXException {
//			addMarker(exception, IMarker.SEVERITY_ERROR);
//		}
//
//		public void warning(SAXParseException exception) throws SAXException {
//			addMarker(exception, IMarker.SEVERITY_WARNING);
//		}
//	}
//

//
//	private static final String MARKER_TYPE = "WtfTools.xmlProblem";
//
//	private SAXParserFactory parserFactory;
//
//	private void addMarker(IFile file, String message, int lineNumber,
//			int severity) {
//		try {
//			IMarker marker = file.createMarker(MARKER_TYPE);
//			marker.setAttribute(IMarker.MESSAGE, message);
//			marker.setAttribute(IMarker.SEVERITY, severity);
//			if (lineNumber == -1) {
//				lineNumber = 1;
//			}
//			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
//		} catch (CoreException e) {
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
//	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
//	 */
//	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
//			throws CoreException {
//		if (kind == FULL_BUILD) {
//			fullBuild(monitor);
//		} else {
//			IResourceDelta delta = getDelta(getProject());
//			if (delta == null) {
//				fullBuild(monitor);
//			} else {
//				incrementalBuild(delta, monitor);
//			}
//		}
//		return null;
//	}
//
//	protected void clean(IProgressMonitor monitor) throws CoreException {
//		// delete markers set and files created
//		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
//	}
//
//	void checkXML(IResource resource) {
//		if (resource instanceof IFile && resource.getName().endsWith(".xml")) {
//			IFile file = (IFile) resource;
//			deleteMarkers(file);
//			XMLErrorHandler reporter = new XMLErrorHandler(file);
//			try {
//				getParser().parse(file.getContents(), reporter);
//			} catch (Exception e1) {
//			}
//		}
//	}
//
//	private void deleteMarkers(IFile file) {
//		try {
//			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
//		} catch (CoreException ce) {
//		}
//	}
//
//	protected void fullBuild(final IProgressMonitor monitor)
//			throws CoreException {
//		try {
//			getProject().accept(new SampleResourceVisitor());
//		} catch (CoreException e) {
//		}
//	}
//
//	private SAXParser getParser() throws ParserConfigurationException,
//			SAXException {
//		if (parserFactory == null) {
//			parserFactory = SAXParserFactory.newInstance();
//		}
//		return parserFactory.newSAXParser();
//	}
//
//	protected void incrementalBuild(IResourceDelta delta,
//			IProgressMonitor monitor) throws CoreException {
//		// the visitor does the work.
//		delta.accept(new SampleDeltaVisitor());
//	}
}
