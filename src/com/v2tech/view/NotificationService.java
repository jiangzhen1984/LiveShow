package com.v2tech.view;

import java.io.Serializable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.V2.jni.ChatRequest;
import com.V2.jni.ChatRequestCallbackAdapter;
import com.V2.jni.callback.ChatRequestCallback;
import com.V2.jni.ind.MessageInd;
import com.V2.jni.util.V2Log;
import com.v2tech.net.DeamonWorker;
import com.v2tech.net.NotificationListener;
import com.v2tech.net.lv.LivePublishIndPacket;
import com.v2tech.net.pkt.IndicationPacket;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.service.jni.JNIResponse.Result;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;

public class NotificationService extends Service {
	
	
	private static final String NOTIFICAITON_ACTION = "com.v2tech.notification_action";
	private static final String NOTIFICAITON_OBJ_TYPE_LIVE_PUBLISH = "com.v2tech.live_publish";
	private static final String NOTIFICAITON_OBJ_TYPE_LIVE_FINISH = "com.v2tech.live_finished";
	private static final String NOTIFICAITON_OBJ_TYPE_LIVE_MESSAGE = "com.v2tech.live_message";

	
	
	public NotificationService() {
	}
	
	

	@Override
	public void onCreate() {
		super.onCreate();
		DeamonWorker.getInstance().addNotificationListener(noListener);
		ChatRequest.getInstance().addChatRequestCallback(messageCallbackListener);
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		DeamonWorker.getInstance().removeNotificationListener(noListener);
		ChatRequest.getInstance().removeChatRequestCallback(messageCallbackListener);
	}



	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	
	
	
	private NotificationListener noListener = new NotificationListener() {

		@Override
		public void onNodification(IndicationPacket ip) {
			if (ip instanceof LivePublishIndPacket) {
				LivePublishIndPacket lpip = (LivePublishIndPacket) ip;
				Live live = new Live(new User(lpip.v2uid), lpip.lid, lpip.vid, lpip.lat,
						lpip.lng);
				live.getPublisher().nId = lpip.uid;
				sendBroadCast(NOTIFICAITON_OBJ_TYPE_LIVE_PUBLISH, live);
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
		V2Log.i("===> send broadcast : "+ NOTIFICAITON_ACTION+"  == > sub:"+ action);
		Intent i = new Intent();
		i.setAction(NOTIFICAITON_ACTION);
		i.addCategory("com.v2tech");
		i.putExtra("sub", action);
		i.putExtra("obj", obj);
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
}
