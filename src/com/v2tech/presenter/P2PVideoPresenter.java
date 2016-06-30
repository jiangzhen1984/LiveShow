package com.v2tech.presenter;

import java.lang.ref.WeakReference;

import v2av.VideoPlayer;
import v2av.VideoRecorder;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.V2.jni.util.V2Log;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.MessageListener;
import com.v2tech.service.P2PMessageService;
import com.v2tech.service.P2PMessageService.VideoEventListener;
import com.v2tech.vo.User;
import com.v2tech.vo.UserChattingObject;
import com.v2tech.vo.UserDeviceConfig;
import com.v2tech.vo.group.Group.GroupType;

public class P2PVideoPresenter extends BasePresenter implements SurfaceHolder.Callback, VideoEventListener {
	
	public static final int TYPE_USER_ID = 1;
	public static final int TYPE_USER_DEVICE_ID = 2;
	
	private static final int START_VIDEO_CALL_CALLBACK = 1;
	private static final int ANSWER_VIDEO_CALL_CALLBACK = 2;
	private static final int ON_ACCEPTED_EVENT = 3;
	private static final int ON_REFUCED_EVENT = 4;
	
	private Context context;
	private P2PVideoPresenterUI ui;
	private UIType uiType;
	
	private P2PMessageService servcie;
	private UserChattingObject uco;
	private LocalHandler localHandler;
	
	public interface P2PVideoPresenterUI {
		
		public long getUserId();
		
		public String getUserName();
		
		public String getRingingSession();
		
		public String getDeviceId();
		
		public int getStartType();
		
		public void showCallingLayout();
		
		public void showConnectedLayout();
		
		public void showRingingLayout();
		
		public VideoPlayer getRemoteVideoPlayer();
		
		public void quit();
		
	}

	public P2PVideoPresenter(Context context, P2PVideoPresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
		servcie = new P2PMessageService();
		localHandler = new LocalHandler(new WeakReference<P2PVideoPresenter>(this));
		servcie.registerVideoEvent(this);
	}

	
	
	
	@Override
	public void onUICreated() {
		super.onUICreated();
		int flag = UserChattingObject.VIDEO_CALL;
	
		int type = ui.getStartType();
		if (type == UIType.CALLING.ordinal()) {
			uiType = UIType.CALLING;
			flag |=UserChattingObject.OUTING_CALL;
			uco = new UserChattingObject(new User(ui.getUserId(), ui.getUserName())  , flag);
			uco.setVp(ui.getRemoteVideoPlayer());
			uco.setSzSessionID(ui.getRingingSession());
			uco.setDeviceId(ui.getDeviceId());
			updateUIOnCalling();
		} else if (type == UIType.CONNECTED.ordinal()) {
			uiType = UIType.CONNECTED;
			flag |=UserChattingObject.INCOMING_CALL;
			uco = new UserChattingObject(new User(ui.getUserId(), ui.getUserName())  , flag);
			uco.setVp(ui.getRemoteVideoPlayer());
			updateUIOnConnected();
		} else if (type == UIType.RINGING.ordinal()) {
			uiType = UIType.RINGING;
			flag |=UserChattingObject.INCOMING_CALL;
			uco = new UserChattingObject(new User(ui.getUserId(), ui.getUserName())  , flag);
			uco.setVp(ui.getRemoteVideoPlayer());
			uco.setSzSessionID(ui.getRingingSession());
			uco.setDeviceId(ui.getDeviceId());
			updateUIOnRinging();
		}
	}
	
	


	
	@Override
	public void onUIDestroyed() {
		super.onUIDestroyed();
		servcie.clearCalledBack();
		servcie.unRegisterVideoEvent(this);
	}

	
	
	
	



	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		V2Log.e("===== create and start record");
		VideoRecorder.VideoPreviewSurfaceHolder = holder;
		//open local device
		UserDeviceConfig duc = new UserDeviceConfig(GroupType.CHATING.intValue(),0 , GlobalHolder.getInstance()
				.getCurrentUserId(), "", null);
		servcie.requestOpenVideoDevice(duc, null);
	}




	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}




	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	




	@Override
	public void onAccepted() {
		Message.obtain(localHandler, ON_ACCEPTED_EVENT).sendToTarget();
	}




	@Override
	public void onDeclined() {
		Message.obtain(localHandler, ON_REFUCED_EVENT).sendToTarget();
	}




	public void onHangoffBtnClicked() {
		switch (uiType) {
		case CONNECTED:
			UserDeviceConfig duc = new UserDeviceConfig(
					GroupType.CHATING.intValue(), 0, uco.getUser().getmUserId(),
					uco.getDeviceId(), ui.getRemoteVideoPlayer());
			servcie.requestCloseVideoDevice(duc, null);
			servcie.cancelCalling(uco, null);
			break;
		case CALLING:
			duc = new UserDeviceConfig(GroupType.CHATING.intValue(),0 , GlobalHolder.getInstance().getCurrentUserId(), "", null);
			servcie.requestCloseVideoDevice(duc, null);
			servcie.cancelCalling(uco, null);
			break;
		case RINGING:
			duc = new UserDeviceConfig(GroupType.CHATING.intValue(),0 , GlobalHolder.getInstance().getCurrentUserId(), "", null);
			servcie.requestCloseVideoDevice(duc, null);
			servcie.declineCalling(uco, null);
			break;
		default:
			break;
		}
		ui.quit();
	}

	public void onAcceptBtnClicked() {
		if (uiType != UIType.RINGING) {
			throw new RuntimeException(" not support ui type: " + uiType);
		}
		uiType = UIType.CONNECTED;
		servcie.answerCalling(uco, new MessageListener(localHandler, ANSWER_VIDEO_CALL_CALLBACK, null));
		updateUIOnConnected();
	}
	
	
	public void onSwitchCameraBtnClicked() {
		UserDeviceConfig duc = new UserDeviceConfig(0, 0, GlobalHolder
				.getInstance().getCurrentUserId(), "", null);
		servcie.switchCamera(duc);
	}
	
	private void updateUIOnCalling() {
		ui.showCallingLayout();
		servcie.startVideoCall(uco, new MessageListener(localHandler, START_VIDEO_CALL_CALLBACK, null));
	}
	
	private void updateUIOnRinging() {
		ui.showRingingLayout();
	}
	
	private void updateUIOnConnected() {
		ui.showConnectedLayout();
		if (uiType == UIType.RINGING) {
		}
		//open remote device
		UserDeviceConfig duc = new UserDeviceConfig(
				GroupType.CHATING.intValue(), 0, uco.getUser().getmUserId(),
				uco.getDeviceId(), ui.getRemoteVideoPlayer());
		servcie.requestOpenVideoDevice(duc, null);
	}
	
	
	
	class LocalHandler extends Handler {
		WeakReference<P2PVideoPresenter> wr;

		public LocalHandler(WeakReference<P2PVideoPresenter> wr) {
			super();
			this.wr = wr;
		}

		@Override
		public void handleMessage(Message msg) {
			if (wr.get() == null) {
				V2Log.w("===> no reference of P2PVideoPresenter");
				return;
			}
			int what = msg.what;
			switch (what) {
			case START_VIDEO_CALL_CALLBACK:
				//TODO if error notify user
				break;
			case ANSWER_VIDEO_CALL_CALLBACK:
				//TODO if error notify user
				
				updateUIOnConnected();
				synchronized(uiType) {
					uiType = UIType.CONNECTED;
				}
				break;
			case  ON_ACCEPTED_EVENT:
				updateUIOnConnected();
				break;
			case ON_REFUCED_EVENT:
				UserDeviceConfig duc = new UserDeviceConfig(GroupType.CHATING.intValue(),0 , GlobalHolder.getInstance().getCurrentUserId(), "", null);
				servcie.requestCloseVideoDevice(duc, null);
				ui.quit();
				break;
			}
		}
		
	}
	

	public enum UIType {
		CALLING, RINGING, CONNECTED;
	}
}
