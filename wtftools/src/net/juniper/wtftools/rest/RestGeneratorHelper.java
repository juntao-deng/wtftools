package net.juniper.wtftools.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.juniper.wtftools.WtfToolsActivator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class RestGeneratorHelper {
	public static List<String> generate(String entityClazz){
		List<ServiceGenerator> list = new ArrayList<ServiceGenerator>();
		list.add(new MoGenerator(entityClazz));
		list.add(new DaoServiceGenerator(entityClazz));
		list.add(new LogicServiceGenerator(entityClazz));
		list.add(new RestItfServiceGenerateor(entityClazz));
		list.add(new RestServiceGenerateor(entityClazz));
		list.add(new LogicServiceGenerator(entityClazz));
		list.add(new LogicServiceImplGenerator(entityClazz));
		list.add(new ClientRestServiceGenerator(entityClazz));
		
		List<String> generatedClass = new ArrayList<String>();
		for(ServiceGenerator generator : list){
			try{
				generatedClass.add(generator.run());
			}
			catch(IOException e){
				WtfToolsActivator.getDefault().logError(e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", e.getMessage());
			}
		}
		return generatedClass;
	}
	
	public static boolean restExist(String entityClazz){
		return new RestServiceGenerateor(entityClazz).exist();
	}
}
