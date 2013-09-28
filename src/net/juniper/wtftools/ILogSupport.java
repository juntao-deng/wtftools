package net.juniper.wtftools;

public interface ILogSupport {
	public void logInfo(String msg);
	public void logError(String msg);
	public void logError(Throwable e);
	public void logError(String msg, Throwable e);
}
