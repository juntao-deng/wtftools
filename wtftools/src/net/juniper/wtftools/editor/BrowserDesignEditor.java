package net.juniper.wtftools.editor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;
import net.juniper.wtftools.designer.BrowserEventHandlerFactory;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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

	private static final String WTF_EVENT = "wtf_event:";
	private static final String TEMP_WORK_DIR = System.getProperty("user.dir");
	private static final String APP_REPLACE = "<div wtftype=\"application\" wtfmetadata=\"#REPLACE#\"/>";
	private Browser browser;
	private IPath appPath;
	private IPath restPath;
	private IPath htmlPath;
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
		htmlPath = ((FileEditorInput)input).getPath();
		appPath = htmlPath.removeLastSegments(1);
		WtfProjectCommonTools.setCurrentProject(htmlPath);
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
		browser = new Browser(parent, SWT.NONE);
		browser.setJavascriptEnabled(true);
		String url = getUrl();
		WtfToolsActivator.getDefault().logInfo("open url:" + url);
		browser.setUrl(url);
		browser.addStatusTextListener(new StatusTextListener(){
			@Override
			public void changed(StatusTextEvent event) {
				String text = event.text;
				if(text != null && text.startsWith(WTF_EVENT)){
					responseToRequest(text);
				}
			}

		});
	}
	private void responseToRequest(String text) {
		String jsonStr = text.substring(WTF_EVENT.length());
		Map obj = BrowserEventHandlerFactory.handleEvent(jsonStr);
		if(obj != null){
			String result = JSONObject.fromObject(obj).toString();
			browser.execute("design_callback('" + result + "')");
		}
	}

	private String getUrl() {
		try {
			String[] appPaths = getAppPath();
			String appDir = StringUtils.join(appPaths, "/", 0, appPaths.length - 1);
			String designBasePath = getDesignBasePath();
			String remoteUrl = "file:///" + designBasePath;
			String replaceStr = APP_REPLACE.replace("#REPLACE#", appDir);
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
		return WtfProjectCommonTools.getFrameworkLocation() + "/src/main/webapp/designsupport/";
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

	public static void main(String[] args){
		String text = WTF_EVENT + "{name:'1', text:2, c:{a:1,b:2}}";
		new BrowserDesignEditor().responseToRequest(text);
	}
}
