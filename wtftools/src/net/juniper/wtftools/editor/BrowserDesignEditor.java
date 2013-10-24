package net.juniper.wtftools.editor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;
import net.juniper.wtftools.designer.BrowserEventHandlerFactory;
import net.juniper.wtftools.rest.RestGeneratorHelper;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
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
	private boolean dirty = false;
	private Map<String, String> modelMap = new HashMap<String, String>();
	private Map<String, String> controllerMap = new HashMap<String, String>();
	private Map<String, JSONObject> restMap = new HashMap<String, JSONObject>();
	private String htmlContent;
	
	public void setDirty(boolean dirty){
		this.dirty = dirty;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		ApplicationUpdateHelper.update(appPath, restPath, htmlPath, modelMap, controllerMap, htmlContent);
		modelMap.clear();
		controllerMap.clear();
		htmlContent = null;
		setDirty(false);
		try {
			WtfProjectCommonTools.getCurrentProject().refreshLocal(IProject.DEPTH_INFINITE, null);
		} 
		catch (CoreException e) {
			WtfToolsActivator.getDefault().logError(e);
		}
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
		IPath restPath = htmlPath.removeLastSegments(1);
		while(!restPath.lastSegment().equals("applications")){
			restPath = restPath.removeLastSegments(1);
		}
		restPath = restPath.removeLastSegments(1).append("rest");
	}

	@Override
	public boolean isDirty() {
		return dirty ;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		RestGeneratorHelper.generate(null);
		browser = new Browser(parent, SWT.NONE);
		browser.setJavascriptEnabled(true);
		String url = getUrl();
		WtfToolsActivator.getDefault().logInfo("open url:" + url);
		browser.setUrl(url);
		final BrowserDesignEditor editor = this;
		browser.addStatusTextListener(new StatusTextListener(){
			@Override
			public void changed(StatusTextEvent event) {
				String text = event.text;
				if(text != null && text.startsWith(WTF_EVENT)){
					responseToRequest(editor, text);
				}
			}

		});
	}
	private void responseToRequest(BrowserDesignEditor editor, String text) {
		String jsonStr = text.substring(WTF_EVENT.length());
		Map obj = BrowserEventHandlerFactory.handleEvent(editor, jsonStr);
		if(obj != null){
			String result = JSONObject.fromObject(obj).toString();
			browser.execute("FwBase.Wtf.Design.DesignSupport.fireInput('" + result + "')");
		}
	}
	private String getUrl() {
		try {
			String ctx = WtfProjectCommonTools.getCurrentProjectCtx();
			String[] appPaths = getAppPath();
			String appDir = ctx + "/" + StringUtils.join(appPaths, "/", 0, appPaths.length - 1);
			String designBasePath = getDesignBasePath();
//			String remoteUrl = "file:///" + designBasePath;
			String replaceStr = APP_REPLACE.replace("#REPLACE#", appDir);
			File template = new File(designBasePath + "design.html");
			String templateStr = FileUtils.readFileToString(template);
			String generatedPath = TEMP_WORK_DIR + "/" + appDir + "/design.html";
			File target = new File(generatedPath);
			String result = templateStr.replace("#REPLACE#", replaceStr);
			
			result = result.replace("#CTX#", ctx);
			result = result.replaceAll("#FRAME_PATH#", WtfProjectCommonTools.getFrameworkWebLocation() + "/");
			result = result.replace("#CTXPATH#", WtfProjectCommonTools.getCurrentWtfProject().getLocation().toOSString() + "/web/");
			FileUtils.writeStringToFile(target, result);
			return "file:///" + generatedPath;
		} 
		catch (IOException e) {
			WtfToolsActivator.getDefault().logError(e);
			return "about:blank";
		}
	}

	private String getDesignBasePath() {
		return WtfProjectCommonTools.getFrameworkWebLocation() + "/designsupport/";
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

	public void setHtmlContent(String html) {
		this.htmlContent = html;
	}

	public void addModel(String key, String metadata) {
		this.modelMap.put(key, metadata);
	}

	public void addController(String key, String controller) {
		this.controllerMap.put(key, controller);
	}
	
	public void addRest(String file, JSONObject api){
		this.restMap.put(file, api);
	}
}
