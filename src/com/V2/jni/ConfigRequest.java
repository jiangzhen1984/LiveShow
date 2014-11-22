package com.V2.jni;

/**
 * @author jiangzhen
 *
 */
public class ConfigRequest
{
	
	public native void setConfigProp(String szItemPath, String szConfigAttr, String szValue);
	public native void getConfigProp(String szItemPath,String szConfigAttr, byte[] pValueBuf, int nBufLen);
	public native void getConfigPropCount(String szItemPath);
	public native void removeConfigProp(String szItemPath,String szConfigAttr);
	
	/**
	 * 
	 * @param szServerIP
	 * @param nPort
	 */
	public native void setServerAddress(String szServerIP, int nPort);
}
