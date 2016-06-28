package com.v2tech.view;

import java.io.Serializable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.V2.jni.ChatRequest;
import com.V2.jni.ChatRequestCallbackAdapter;
import com.V2.jni.VideoRequest;
import com.V2.jni.VideoRequestCallbackAdapter;
import com.V2.jni.callback.ChatRequestCallback;
import com.V2.jni.callback.VideoRequestCallback;
import com.V2.jni.ind.MessageInd;
import com.V2.jni.util.V2Log;
import com.v2tech.net.DeamonWorker;
import com.v2tech.net.NotificationListener;
import com.v2tech.net.lv.InquiryIndPacket;
import com.v2tech.net.lv.LivePublishIndPacket;
import com.v2tech.net.lv.LiveWatchingIndPacket;
import com.v2tech.net.lv.LiveWatchingReqPacket;
import com.v2tech.net.pkt.IndicationPacket;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.service.jni.JNIResponse.Result;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;

public class NotificationService extends Service {
	
	
	private static final String NOTIFICAITON_ACTION = "com.v2tech.notification_action";
	private static final String NOTIFICAITON_OBJ_TYPE_LIVE_PUBLISH = "com.v2tech.live_publish";
	private static final String NOTIFICAITON_OBJ_TYPE_LIVE_FINISH = "com.v2tech.live_finished";
	private static final String NOTIFICAITON_OBJ_TYPE_LIVE_LEAVE = "com.v2tech.live_leave";
	private static final String NOTIFICAITON_OBJ_TYPE_LIVE_WATCHING = "com.v2tech.live_watching";
	private static final String NOTIFICAITON_OBJ_TYPE_LIVE_MESSAGE = "com.v2tech.live_message";
	private static final String NOTIFICAITON_OBJ_TYPE_INQUIRY_NEW = "com.v2tech.inquiry_new";
	
	public static final String P2P_VIDEO_NOTIFICAITON_ACTION = "com.v2tech.p2p_video_notification_action";

	
	
	public NotificationService() {
	}
	
	

	@Override
	public void onCreate() {
		super.onCreate();
		((MainApplication)this.getApplication()).onMainCreate();
		DeamonWorker.getInstance().addNotificationListener(noListener);
		ChatRequest.getInstance().addChatRequestCallback(messageCallbackListener);
		VideoRequest.getInstance().addCallback(vrcb);
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		DeamonWorker.getInstance().removeNotificationListener(noListener);
		ChatRequest.getInstance().removeChatRequestCallback(messageCallbackListener);
		VideoRequest.getInstance().removeCallback(vrcb);
	}



	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	
	
	
	private NotificationListener noListener = new NotificationListener() {

		@Override
		public void onNodification(IndicationPacket ip) {
			V2Log.i(" OnNotification===>" + ip);
			if (ip instanceof LivePublishIndPacket) {
				LivePublishIndPacket lpip = (LivePublishIndPacket) ip;
				Live live = new Live(new User(lpip.v2uid), lpip.lid, lpip.vid, lpip.lat,
						lpip.lng);
				live.getPublisher().nId = lpip.uid;
				sendBroadCast(NOTIFICAITON_OBJ_TYPE_LIVE_PUBLISH, live);
			} else if (ip instanceof LiveWatchingIndPacket) {
				LiveWatchingIndPacket  lwip =(LiveWatchingIndPacket) ip;
				if (lwip.type == LiveWatchingReqPacket.CLOSE) {
					User u = new User(lwip.uid);
					sendBroadCast(NOTIFICAITON_OBJ_TYPE_LIVE_PUBLISH,
							new String[] { "obj", "lid", "opt" },
							new Serializable[] { u, lwip.nid, lwip.type });
				} else if (lwip.type == LiveWatchingReqPacket.WATCHING) {
					User u = new User(lwip.uid);
					sendBroadCast(NOTIFICAITON_OBJ_TYPE_LIVE_WATCHING,
							new String[] { "live", "user", "type" },
							new Serializable[] { lwip.nid, u,
									LiveWatchingReqPacket.WATCHING });
				} else if (lwip.type == LiveWatchingReqPacket.CANCEL) {
					User u = new User(lwip.uid);
					sendBroadCast(NOTIFICAITON_OBJ_TYPE_LIVE_LEAVE,
							new String[] { "live", "user", "type" },
							new Serializable[] { lwip.nid, u,
									LiveWatchingReqPacket.CANCEL });
				}
			} else if (ip instanceof InquiryIndPacket) {
				InquiryIndPacket  lwip =(InquiryIndPacket) ip;
				sendBroadCast(NOTIFICAITON_OBJ_TYPE_INQUIRY_NEW,
						new String[] { "irid", "iuid", "award" ,"lat", "lng"},
						new Serializable[] { lwip.inquireId, lwip.inquiryUserId, lwip.award, lwip.lat, lwip.lng});
			}

		}

		@Override
		public void onResponse(ResponsePacket rp) {

		}

		@Override
		public void onStateChanged() {

		}

		@Override
		public void onTimeout(ResponsePacket rp) {

		}

	};
	
	
	private void sendBroadCast(String action, Serializable obj) {
		sendBroadCast(action, new String[]{"obj"}, new Serializable[]{obj});
	}
	
	
	private void sendBroadCast(String action, String[] key, Serializable[] obj) {
		V2Log.i("===> send broadcast : "+ NOTIFICAITON_ACTION+"  == > sub:"+ action);
		Intent i = new Intent();
		i.setAction(NOTIFICAITON_ACTION);
		i.addCategory("com.v2tech");
		i.putExtra("sub", action);
		for (int in = 0; in < key.length; in++) {
			i.putExtra(key[in], obj[in]);
		}
		this.getApplicationContext().sendBroadcast(i);
	}
	
	
	private ChatRequestCallback messageCallbackListener = new ChatRequestCallbackAdapter() {

		@Override
		public void OnRecvChatTextCallback(int eGroupType, long nGroupID,
				long nFromUserID, long nToUserID, long nTime, String szSeqID,
				String szXmlText) {
			MessageInd ind = new MessageInd(Result.SUCCESS);
			ind.lid = nGroupID;
			ind.uid = nFromUserID;
			ind.content = szXmlText;
			sendBroadCast(NOTIFICAITON_OBJ_TYPE_LIVE_MESSAGE, ind);
		}
		
	};
	
	
	
	
	private  VideoRequestCallback  vrcb = new  VideoRequestCallbackAdapter() {

		@Override
		public void OnVideoChatInviteCallback(String szSessionID,
				long nFromUserID, String szDeviceID) {
			
			V2Log.i("===> send broadcast : "+ P2P_VIDEO_NOTIFICAITON_ACTION+"  session:"+ szSessionID+"  user:"+nFromUserID+"  device:"+szDeviceID);
			Intent i = new Intent();
			i.setAction(P2P_VIDEO_NOTIFICAITON_ACTION);
			i.addCategory("com.v2tech");
			i.putExtra("session", szSessionID);
			i.putExtra("user", nFromUserID);
			i.putExtra("device", szDeviceID);
			getApplicationContext().sendBroadcast(i);
		}

		
	};

}
