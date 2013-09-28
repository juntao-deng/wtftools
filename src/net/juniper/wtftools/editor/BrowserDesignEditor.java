package net.juniper.wtftools.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class BrowserDesignEditor extends EditorPart {

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);
		this.setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		final Browser browser = new Browser(parent, SWT.WEBKIT);
		browser.setJavascriptEnabled(true);
		String url = getUrl();
		browser.setUrl(url);
		browser.addStatusTextListener(new StatusTextListener(){
			@Override
			public void changed(StatusTextEvent event) {
				String text = event.text;
				if(text != null && text.startsWith("wtf_event:")){
				}
			}
			
		});
	}

	private String getUrl() {
		return "file:///D:/devspace/workspace/workspace_wtf/com.juniper.jnrd.wtftools/web/src/main/webapp/designsupport/design.html";
	}

	@Override
	public void setFocus() {

	}

}
