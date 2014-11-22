package com.V2.jni;

import com.V2.jni.ind.VideoJNIObjectInd;



public interface VideoRequestCallback {

	/**
	 * <ul>Xml file content as below:
	 * {@code 
	 * <xml><videolist defaultid='136:CyberLink Webcam Sharing
	 * Manager____2056417056'><video bps='256' camtype='0' comm='0'
	 * desc='CyberLink Webcam Sharing Manager____2056417056' fps='15'
	 * id='136:CyberLink Webcam Sharing Manager____2056417056' selectedindex='0'
	 * videotype='vid'><sizelist><size h='240' w='320'/><size h='360'
	 * w='640'/><size h='480' w='640'/><size h='600' w='800'/><size h='720'
	 * w='1280'/><size h='960' w='1280'/><size h='900' w='1600'/><size h='1200'
	 * w='1600'/></sizelist></video><video bps='256' camtype='0' comm='0'
	 * desc='HP HD Webcam [Fixed]____1388682949' fps='15' id='136:HP HD Webcam
	 * [Fixed]____1388682949' selectedindex='3' videotype='vid'><sizelist><size
	 * h='480' w='640'/><size h='400' w='640'/><size h='288' w='352'/><size
	 * h='240' w='320'/><size h='720'
	 * w='1280'/></sizelist></video></videolist></xml>
	 * }
	 * </ul>
	 * @param szXmlData returned user's device data as xml
	 */
	public void OnRemoteUserVideoDevice(long uid, String szXmlData);
	
	
	
	
	public void OnVideoChatInviteCallback(VideoJNIObjectInd ind);
	
	/**
	 * 
	 * @param szDevID
	 * @param nSizeIndex
	 * @param nFrameRate
	 * @param nBitRate
	 */
	public void OnSetCapParamDone(String szDevID, int nSizeIndex,
			int nFrameRate, int nBitRate);
	
	
	/**
	 * Other user accept current invitation
	 * @param ind
	 */
	public void OnVideoChatAccepted(VideoJNIObjectInd ind);

	/**
	 * Remote user refuse current user invite video conversation
	 * @param ind
	 */
	public void OnVideoChatRefused(VideoJNIObjectInd ind);
	
	
	/**
	 * Remote user close current connected conversation
	 * @param ind
	 */
	public void OnVideoChatClosed(VideoJNIObjectInd ind);
	
	
	/**
	 * Remote user close current connected conversation
	 * @param ind
	 */
	public void OnVideoChating(VideoJNIObjectInd ind);
	

}
