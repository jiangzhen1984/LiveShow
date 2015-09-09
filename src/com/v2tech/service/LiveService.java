package com.v2tech.service;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.V2.jni.VideoBCRequest;
import com.V2.jni.VideoBCRequestCallback;
import com.V2.jni.VideoBCRequestCallbackAdapter;
import com.V2.jni.ind.V2Live;
import com.V2.jni.util.V2Log;
import com.v2tech.service.jni.GetNeiborhoodResponse;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.LiveNotification;
import com.v2tech.service.jni.RequestFinishPublishResponse;
import com.v2tech.service.jni.RequestPublishResponse;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;

public class LiveService extends AbstractHandler {
	
	
	private static final int QUERY_NERY = 100;
	private static final int REQUEST_PUBLISH = 101;
	private static final int REQUEST_FINISH_PUBLISH = 102;
	
	
	private MessageListener mLiveNotification;
	private VideoBCRequestCallback bcCallback;

	public LiveService() {
		bcCallback = new LocalBCCallback(this);
		VideoBCRequest.getInstance().addCallback(bcCallback);
	}

	@Override
	public void clearCalledBack() {
		VideoBCRequest.getInstance().removeCallback(bcCallback);
	}
	
	public void scanNear(double lat, double lng, float radius, MessageListener caller) {
		initTimeoutMessage(QUERY_NERY, DEFAULT_TIME_OUT_SECS, caller);
		VideoBCRequest.getInstance().GetNeiborhood_Region("<gps lon=\"" + lng + "\" lat=\"" + lat
								+ "\" distance=\""+radius+"\" ></gps>");
	}

	public void requestPublish( MessageListener caller) {
		initTimeoutMessage(REQUEST_PUBLISH, DEFAULT_TIME_OUT_SECS, caller);
		VideoBCRequest.getInstance().startLive();
	}
	
	
	public void requestFinishPublish( MessageListener caller) {
		initTimeoutMessage(REQUEST_FINISH_PUBLISH, DEFAULT_TIME_OUT_SECS, caller);
		VideoBCRequest.getInstance().stopLive();
	}
	
	
	
	public void registerLiveNotification(MessageListener listener) {
		this.mLiveNotification = listener;
	}
	
	
	class LocalBCCallback extends VideoBCRequestCallbackAdapter {
		
		private Handler mCallbackHandler;

		public LocalBCCallback(Handler mCallbackHandler) {
			this.mCallbackHandler = mCallbackHandler;
		}

		@Override
		public void OnStartLive(long nUserID, String szUrl) {
			long currentUid = GlobalHolder.getInstance().getCurrentUser() == null ? GlobalHolder
					.getInstance().nyUserId : GlobalHolder.getInstance()
					.getCurrentUser().getmUserId();
	
			if (nUserID == currentUid) {
				Message.obtain(mCallbackHandler, REQUEST_PUBLISH, new RequestPublishResponse(JNIResponse.Result.SUCCESS, szUrl, nUserID))
				.sendToTarget();
			} else {
				if (mLiveNotification != null) {
					Message.obtain(
							mLiveNotification.refH.get(),
							mLiveNotification.what,
							new LiveNotification(
									JNIResponse.Result.SUCCESS, new Live(new User(nUserID), szUrl), LiveNotification.TYPE_START))
							.sendToTarget();
				}
			}
		}

		@Override
		public void OnStopLive(long nUserID) {
			long currentUid = GlobalHolder.getInstance().getCurrentUser() == null ? GlobalHolder
					.getInstance().nyUserId : GlobalHolder.getInstance()
					.getCurrentUser().getmUserId();
	
			if (nUserID == currentUid) {
				Message.obtain(mCallbackHandler, REQUEST_FINISH_PUBLISH, new RequestFinishPublishResponse(JNIResponse.Result.SUCCESS, nUserID))
				.sendToTarget();
			} else {
				if (mLiveNotification != null) {
					Message.obtain(
							mLiveNotification.refH.get(),
							mLiveNotification.what,
							new LiveNotification(
									JNIResponse.Result.SUCCESS, new Live(new User(nUserID), null), LiveNotification.TYPE_STOPPED))
							.sendToTarget();
				}
			}
		}

		@Override
		public void OnGetNeiborhood(List<V2Live> liveList) {
			List<Live> list = new ArrayList<Live>(liveList.size());
			for (V2Live v2live : liveList) {
				list.add(new Live(new User(v2live.publisher.uid, v2live.publisher.name), v2live.url, v2live.location.lat, v2live.location.lng));
			}
			
			GetNeiborhoodResponse res = new GetNeiborhoodResponse(JNIResponse.Result.SUCCESS);
			res.list = list;
			Message.obtain(mCallbackHandler, QUERY_NERY, res)
			.sendToTarget();
		}
		
	}

}


