package net.juniper.wtftools.designer;

import java.util.List;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.editor.BrowserDesignEditor;
import net.juniper.wtftools.rest.RestGeneratorHelper;
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
		WtfToolsActivator.getDefault().logInfo("== Generating codes for class:" + className);
		List<String> list = RestGeneratorHelper.generate(className);
		result.put("generatedList", list);
		return result;
	}



	@Override
	protected String getActionName() {
		return GENERATE_CODE;
	}
}
