package net.juniper.scutools.wizards;

import java.io.File;

import net.juniper.scutools.ScuToolsActivator;

import org.apache.commons.io.FileUtils;

public class WtfProjectHelper {

//	private static final String CTX_FILE_PATH = "/conf/Catalina/localhost";
//
//
//	/**
// 	 * write tomcat context configration
// 	 * @param contex
// 	 * @param docbase
// 	 * @return
// 	 */
// 	public static boolean writeContextFile(String contex, String docbase){
// 		String filePath = getContextFilePath(contex);
// 		try{
// 			StringBuffer buffer = new StringBuffer();
// 			buffer.append("<!--\n");
// 			buffer.append("    Context configuration file for the ");
// 			buffer.append(contex);
// 			buffer.append("\n-->\n\n");
// 			buffer.append("<Context path=\"/");
// 			buffer.append(contex);
// 			buffer.append("\" docBase=\"");
// 			buffer.append(docbase);
// 			buffer.append("\" privileged=\"true\" antiResourceLocking=\"false\" antiJARLocking=\"false\" useNaming=\"false\" override=\"true\"  cachingAllowed=\"false\">\n");
// 			buffer.append("\n    <WatchedResource>WEB-INF/web.xml</WatchedResource>\n\n</Context>\n");
// 			FileUtils.writeStringToFile(new File(filePath), buffer.toString());
// 		}
// 		catch(Exception e){
// 			WtfToolsActivator.getDefault().logError(e);
// 			return false;
// 		}
// 		return true;
// 	}
// 	
// 	
// 	/**
// 	 * get tomcat context file path
// 	 * @param contex
// 	 * @return
// 	 */
//	private static String getContextFilePath(String contex) {
//		String filePath = "";
//		String tomcatPath = WtfProjectCommonTools.getTomcatHome();
//		filePath = tomcatPath + CTX_FILE_PATH + "/" + contex + ".xml";
//		return filePath;
//	}


	public static void writeConfigFile(String projPath, String context) {
		String filePath = projPath + "/.wtf_project";
 		try{
 			StringBuffer buffer = new StringBuffer();
 			buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
 				  .append("<projectProperties>\n")
 				  .append("<rootDir>/web</rootDir>\n")
 				  .append("<webPath>/" + context + "</webPath>\n")
 				  .append("</projectProperties>\n");
 			FileUtils.writeStringToFile(new File(filePath), buffer.toString());
 		}
 		catch(Exception e){
 			ScuToolsActivator.getDefault().logError(e);
 		}
	}
}
