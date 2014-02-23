package net.juniper.scutools.editor;

import net.juniper.scutools.common.ScuProjectCommonTools;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.internal.EditorSite;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.CompilationUnitEditor;

public class TextEditor {
	private Text text;
	private Composite editorParent;
	private AbstractDecoratedTextEditor editor;
//	private boolean changed = false;
	public TextEditor(Composite parent, EditorType type) {
		editorParent = new Composite(parent, SWT.NONE);
		editorParent.setLayout(new FillLayout(SWT.HORIZONTAL));
		if(type.equals(EditorType.JS)){
			editor = new JsEditor();
		}
		else
			editor = null;//new StructuredTextEditor();
	}
	public Composite getMainControl() {
		return editorParent;
	}
	public Text getText() {
		return text;
	}
	public void setText(Text text) {
		this.text = text;
	}
	public boolean isChanged() {
		boolean dirty = editor.isDirty();
		return dirty;
	}
	public void clearChanged() {
		editor.doRevertToSaved();
	}
	public void setSource(IWorkbenchPartSite iWorkbenchPartSite, IPath iPath) {
		try {
			EditorSite site = (EditorSite) iWorkbenchPartSite;
			EditorSite editorSite = new EditorSite(site.getModel(), editor, site.getPartReference(), null);
			
//			editorSite.setActionBars(createEditorActionBars((WorkbenchPage) getPage(), descriptor,
//					editorSite));
//			IEditorPart editor = (IEditorPart) part;
			editorSite.setActionBars((SubActionBars) site.getActionBars());
			IFile file = ScuProjectCommonTools.getCurrentProject().getFile(iPath.makeRelativeTo(ScuProjectCommonTools.getCurrentProject().getLocation()));
			if(!file.exists())
				throw new RuntimeException(file.getLocation().toOSString());
			editor.init(editorSite, new FileEditorInput(file));
			editor.createPartControl(editorParent);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void doSave() {
		if(editor instanceof CompilationUnitEditor){
			((CompilationUnitEditor)editor).setSelection(null);
		}
		editor.doSave(null);
	}
}
class JsEditor extends CompilationUnitEditor{
	@Override
	public void rememberSelection(){
		
	}
	@Override
	public void restoreSelection(){
		
	}
	@Override
	protected void performSave(boolean overwrite, IProgressMonitor progressMonitor) {
		super.performSave(true, progressMonitor);
	}
};	