package com.v2tech.vo;

import com.v2tech.service.GlobalHolder;


/**
 *  1. 单纯的语音通话: MediaChatID由 AudioChat+UUID 组成, 形如: "AudioChatC55420D6-EC6E-4618-82F7-C28272588094"<br />
 
	2. 视频通话: 视频的MediaChatID用UUID即可, 形如: "199E9742-F4B2-486C-A136-F699C9C2B81C"
   	视频中附带的语音通话的MediChatID由 ByVideo+UUID组成, 形如: "ByVideo1CBB3597-FA39-4BE8-B1E1-2BE84517EE4F"<br />

	3. 远程桌面协助中附带的语音通话的MediaChatID由 ByAppShare+UUID 组成,形如: "ByAppShare09D41672-E146-47F3-A852-A9554D8DC8B9"<br />
	代码中对语音通话相关的流程, 可以检查其MediaChatID是否包含字符串 AudioChat/ByVideo/ByAppShare, 以判断该通话的类型
 * @author 
 *
 */
public class VideoBean {

	public VideoBean(){
		
		ownerID = GlobalHolder.getInstance().getCurrentUserId();
	}
	
	public long ownerID;
	public long formUserID = -1;
	public long toUserID = -1;
	public long remoteUserID = -1;
	public String mediaChatID;
	public int mediaType;
	public int mediaState;
	public int replyType;
	public long startDate;
	public long endDate;
	public int readSatate; 
}
