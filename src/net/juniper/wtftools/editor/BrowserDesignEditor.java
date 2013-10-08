package net.juniper.wtftools.editor;

import java.io.File;
import java.io.IOException;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.ui.part.FileEditorInput;

public class BrowserDesignEditor extends EditorPart {

	private static final String TEMP_WORK_DIR = "c:/wtf_design_temp/";
	private static final String APP_REPLACE = "<div wtftype=\"application\" wtfmetadata=\"#REPLACE#\"/>";

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
		WtfToolsActivator.getDefault().logInfo("open url:" + url);
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
		try {
			String[] appPaths = getAppPath();
			String appDir = StringUtils.join(appPaths, "/", 0, appPaths.length - 1);
			String designBasePath = getDesignBasePath();
			String remoteUrl = "file:///" + designBasePath;
			String replaceStr = APP_REPLACE.replace("#REPLACE#", "apps/" + appDir);
			File template = new File(designBasePath + "design.html");
			String templateStr = FileUtils.readFileToString(template);
			String generatedPath = TEMP_WORK_DIR + appDir + "/design.html";
			File target = new File(generatedPath);
			String result = templateStr.replace("#REPLACE#", replaceStr);
			result = result.replaceAll("#REPLACE_URL#", remoteUrl);
			FileUtils.writeStringToFile(target, result);
			return "file:///" + generatedPath;
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
			return "about:blank";
		}
	}

	private String getDesignBasePath() {
		return "D:/devspace/workspace/workspace_wtf/com.juniper.jnrd.wtftools/web/src/main/webapp/designsupport/";
	}

	private String[] getAppPath() {
		IPath projectPath = WtfProjectCommonTools.getCurrentProject().getLocation();
		String[] paths = ((FileEditorInput)this.getEditorInput()).getPath().makeRelativeTo(projectPath).segments();
		int appDirIndex = getAppDirIndex(paths);
		String[] newPaths = new String[paths.length - appDirIndex];
		System.arraycopy(paths, appDirIndex, newPaths, 0, newPaths.length);
		return newPaths;
	}

	private int getAppDirIndex(String[] paths) {
		for(int i = 0; i < paths.length; i ++){
			String path = paths[i];
			if(path.equals("applications"))
				return i + 1;
		}
		return 2;
	}

	@Override
	public void setFocus() {

	}

}
