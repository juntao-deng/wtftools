package net.juniper.wtftools.wizards;

import java.io.File;

import net.juniper.wtftools.WtfToolsActivator;
import net.juniper.wtftools.core.WtfProjectCommonTools;

import org.apache.commons.io.FileUtils;

public class TomcatHelper {

	private static final String CTX_FILE_PATH = "/conf/Catalina/localhost";


	/**
 	 * write tomcat context configration
 	 * @param contex
 	 * @param docbase
 	 * @return
 	 */
 	public static boolean writeContextFile(String contex, String docbase){
 		String filePath = getContextFilePath(contex);
 		try{
 			StringBuffer buffer = new StringBuffer();
 			buffer.append("<!--\n");
 			buffer.append("    Context configuration file for the ");
 			buffer.append(contex);
 			buffer.append("\n-->\n\n");
 			buffer.append("<Context path=\"/");
 			buffer.append(contex);
 			buffer.append("\" docBase=\"");
 			buffer.append(docbase);
 			buffer.append("\" privileged=\"true\" antiResourceLocking=\"false\" antiJARLocking=\"false\" useNaming=\"false\" override=\"true\"  cachingAllowed=\"false\">\n");
 			buffer.append("\n    <WatchedResource>WEB-INF/web.xml</WatchedResource>\n\n</Context>\n");
 			FileUtils.writeStringToFile(new File(filePath), buffer.toString());
 		}
 		catch(Exception e){
 			WtfToolsActivator.getDefault().logError(e);
 			return false;
 		}
 		return true;
 	}
 	
 	
 	/**
 	 * get tomcat context file path
 	 * @param contex
 	 * @return
 	 */
	private static String getContextFilePath(String contex) {
		String filePath = "";
		String tomcatPath = WtfProjectCommonTools.getTomcatHome();
		filePath = tomcatPath + CTX_FILE_PATH + "/" + contex + ".xml";
		return filePath;
	}


	public static void writeConfigFile(String projPath, String context) {
		String filePath = projPath + "/.tomcatplugin";
 		try{
 			StringBuffer buffer = new StringBuffer();
 			buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
 				  .append("<tomcatProjectProperties>\n")
 				  .append("<rootDir>/web</rootDir>\n")
 				  .append("<exportSource>false</exportSource>\n")
 				  .append("<reloadable>true</reloadable>\n")
 				  .append("<redirectLogger>false</redirectLogger>\n")
 				  .append("<updateXml>true</updateXml>\n")
 				  .append("<warLocation></warLocation>\n")
 				  .append("<extraInfo></extraInfo>\n")
 				  .append("<webPath>/" + context + "</webPath>\n")
 				  .append("</tomcatProjectProperties>\n");
 			FileUtils.writeStringToFile(new File(filePath), buffer.toString());
 		}
 		catch(Exception e){
 			WtfToolsActivator.getDefault().logError(e);
 		}
	}
}
