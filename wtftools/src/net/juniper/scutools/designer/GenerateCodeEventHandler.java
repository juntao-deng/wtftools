package net.juniper.scutools.designer;

import java.util.List;

import net.juniper.scutools.ScuToolsActivator;
import net.juniper.scutools.editor.BrowserDesignEditor;
import net.juniper.scutools.rest.RestGeneratorHelper;
import net.sf.json.JSONObject;

public class GenerateCodeEventHandler extends AbstractBrowserEventHandler{

	private static final String GENERATE_CODE = "generatecode";
	@Override
	public JSONObject handle(BrowserDesignEditor editor, JSONObject json) {
		String className = json.getString("entityName");
		JSONObject result = new JSONObject();
		if(className == null){
			result.put("errormsg", "No entity selected");
			return result;
		}
		ScuToolsActivator.getDefault().logInfo("== Generating codes for class:" + className);
		List<String> list = RestGeneratorHelper.generate(className);
		result.put("generatedList", list);
		return result;
	}



	@Override
	protected String getActionName() {
		return GENERATE_CODE;
	}
}
