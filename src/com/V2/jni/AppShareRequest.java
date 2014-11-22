package com.V2.jni;

public class AppShareRequest
{
	public native boolean initialize(AppShareRequest requestObj);
	public native void unInitialize();
	
	//发起应用程序共享
	public native void InviteAppShare(long  nGroupID, long  nDstUserID, int nPID, int type); //type=0:driver type=1: hook
	//结束应用程序共享
	public native void CloseAppShare(long  nGroupID, long  nDstUserID);
	//控制应用程序共享
	public native void EnableControl(boolean bEnable);
	//设置位宽
	public native void SetBitWidth(int width);
	//设置帧率
	public native void SetFrameRate(int rate);
	//缩放
	public native void SetScaleMode(boolean bZoom);
	
	private void OnInviteAppHostShare(int result)
	{
		// TODO
	}
	
	private void OnInviteAppViewerShare(Object hwnd,int result)
	{
		// TODO
	}
	
	private void OnClostAppHostShare(int result)
	{
		// TODO
	}
	
	private void OnCloseAppViewerShare( int result)
	{
		// TODO
	}
}
