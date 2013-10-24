package net.juniper.wtftools.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.juniper.wtftools.WtfToolsActivator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class RestGeneratorHelper {
	public static void generate(String entityClazz){
		entityClazz = "net.juniper.space.models.device.Device";
		List<ServiceGenerator> list = new ArrayList<ServiceGenerator>();
		list.add(new DaoServiceGenerator(entityClazz));
		list.add(new LogicServiceGenerator(entityClazz));
		list.add(new RestServiceGenerateor(entityClazz));
		list.add(new LogicServiceGenerator(entityClazz));
		list.add(new LogicServiceImplGenerator(entityClazz));
		list.add(new ClientRestServiceGenerator(entityClazz));
		for(ServiceGenerator generator : list){
			try{
				generator.run();
			}
			catch(IOException e){
				WtfToolsActivator.getDefault().logError(e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", e.getMessage());
			}
		}
	}
}