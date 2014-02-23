package net.juniper.scutools.designer.utils;

public final class WtfStringUtils {
	public static String replaceString(int begin, int end, String sourceStr, String replace){
		String bstr = null;
		String estr = null;
		if(end <= begin){
			bstr = sourceStr.substring(0, begin);
			estr = sourceStr.substring(begin);
		}
		else{
			bstr = sourceStr.substring(0, begin);
			estr = sourceStr.substring(end);
		}
		return bstr + replace + estr;
	}
	
	public static String addTab(String sourceStr){
		if(sourceStr == null || sourceStr.equals(""))
			return sourceStr;
		sourceStr = "\t" + sourceStr;
		sourceStr = sourceStr.replaceAll("\n", "\n\t");
//		if(sourceStr.endsWith("\n\t}"))
//			sourceStr = sourceStr.substring(0, sourceStr.length() - 3) + "\n}";
		return sourceStr;
	}
	
	public static String removeTab(String sourceStr){
		if(sourceStr == null)
			return sourceStr;
		if(sourceStr.startsWith("\t"))
			sourceStr = sourceStr.substring(1);
		return sourceStr.replaceAll("\n\t", "\n");
	}
}
