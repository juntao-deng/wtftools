package net.juniper.wtftools.designer.utils;

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
}
