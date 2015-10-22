package com.V2.jni;

public class ConfigRequest {
	private static ConfigRequest mConfigRequest;

	public static synchronized ConfigRequest getInstance() {
		if (mConfigRequest == null) {
			synchronized (ConfigRequest.class) {
				if (mConfigRequest == null) {
					mConfigRequest = new ConfigRequest();
				}
			}
		}
		return mConfigRequest;
	}

	/**
	 * 设置配置文件的属性
	 * 
	 * @param szItemPath
	 * @param szConfigAttr
	 * @param szValue
	 */
	public native void setConfigProp(String szItemPath, String szConfigAttr, String szValue);

	/**
	 * 获取配置文件的属性
	 * 
	 * @param szItemPath
	 * @param szConfigAttr
	 * @param pValueBuf
	 * @param nBufLen
	 */
	public native void getConfigProp(String szItemPath, String szConfigAttr, byte[] pValueBuf, int nBufLen);

	/**
	 * 获取配置文件的属性数量
	 * 
	 * @param szItemPath
	 */
	public native void getConfigPropCount(String szItemPath);

	/**
	 * 删除配置文件中某个属性
	 * 
	 * @param szItemPath
	 * @param szConfigAttr
	 */
	public native void removeConfigProp(String szItemPath, String szConfigAttr);

	/**
	 * 设置服务器地址
	 * 
	 * @param szServerIP
	 * @param nPort
	 */
	public native void setServerAddress(String szServerIP, int nPort);
}
