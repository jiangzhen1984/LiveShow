package com.v2tech.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

import com.V2.jni.InteractionRequest;
import com.V2.jni.util.V2Log;
import com.v2tech.net.DeamonWorker;
import com.v2tech.net.NotificationListener;
import com.v2tech.net.lv.LivePublishReqPacket;
import com.v2tech.net.lv.LivePublishRespPacket;
import com.v2tech.net.lv.LiveQueryReqPacket;
import com.v2tech.net.lv.LiveQueryRespPacket;
import com.v2tech.net.lv.LiveRecommendReqPacket;
import com.v2tech.net.lv.LocationReportReqPacket;
import com.v2tech.net.lv.WatcherListQueryReqPacket;
import com.v2tech.net.lv.WatcherListQueryRespPacket;
import com.v2tech.net.pkt.IndicationPacket;
import com.v2tech.net.pkt.Packet;
import com.v2tech.net.pkt.PacketProxy;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.SearchLiveResponse;
import com.v2tech.vo.Live;
import com.v2tech.vo.Watcher;

public class LiveService extends AbstractHandler {
	
	
	private static final int REQUEST_PUBLISH = 101;
	private static final int REQUEST_FINISH_PUBLISH = 102;
	
	

	public LiveService() {
	}
	
	
	public void updateGps(double lat, double lng) {
		if (GlobalHolder.getInstance().getCurrentUser() == null) {
			V2Log.e("Can not update gps, not login yet");
			return;
		}
		DeamonWorker.getInstance().request(
				new PacketProxy(new LocationReportReqPacket(GlobalHolder
						.getInstance().getCurrentUser().nId, lat, lng), null));
	}
	

	@Override
	public void clearCalledBack() {
	}
	
	public void scanNear(double lat, double lng, float radius,
			final MessageListener caller) {
		if (GlobalHolder.getInstance().getCurrentUser() == null) {
			V2Log.e("Can not update gps, not login yet");
			if (caller != null) {
				Message.obtain(
						caller.getHandler(),
						caller.what,
						new SearchLiveResponse(
								JNIResponse.Result.NO_RESOURCE,
								null)).sendToTarget();
			}
			return;
		}
		
		DeamonWorker.getInstance().requestAsync(
				new PacketProxy(new LiveQueryReqPacket(GlobalHolder.getInstance().getCurrentUser().nId,lat, lng, (int) radius),
						new NotificationListener() {

							@Override
							public void onNodification(IndicationPacket ip) {
								Handler h = caller.refH.get();
								if (h != null) {
									Message.obtain(
											h,
											caller.what,
											new SearchLiveResponse(
													JNIResponse.Result.NO_RESOURCE,
													null)).sendToTarget();
								}
							}

							@Override
							public void onResponse(ResponsePacket rp) {
								Handler h = caller.refH.get();
								if (h != null) {
									Message.obtain(
											h,
											caller.what,
											new SearchLiveResponse(
													JNIResponse.Result.SUCCESS,
													(LiveQueryRespPacket) rp))
											.sendToTarget();
								}

							}
							
							

							@Override
							public void onTimeout(ResponsePacket rp) {
							}

							@Override
							public void onStateChanged() {

							}

						}));
	}

	public void requestPublish( MessageListener caller) {
		initTimeoutMessage(REQUEST_PUBLISH, DEFAULT_TIME_OUT_SECS, caller);
		InteractionRequest.getInstance().startLive();
	}
	
	
	public void requestFinishPublish(MessageListener caller) {
		initTimeoutMessage(REQUEST_FINISH_PUBLISH, DEFAULT_TIME_OUT_SECS, caller);
		InteractionRequest.getInstance().stopLive();
	}
	
	
	
	public void reportLiveStatus(Live l, MessageListener caller) {
		Packet resp = DeamonWorker.getInstance().request(
				new LivePublishReqPacket(GlobalHolder.getInstance()
						.getCurrentUser().nId, l.getLid(),
						l.getLat(),
						l.getLng()));
		V2Log.i("===reportLiveStatus ==>" + resp.getHeader().isError());
		if (!resp.getHeader().isError()) {
			l.setNid(((LivePublishRespPacket)resp).nvid);
		}
	}
	
	
	public void getWatcherList(Live l, MessageListener caller) {
		DeamonWorker.getInstance().requestAsync(
				new PacketProxy(
						new WatcherListQueryReqPacket(l.getNid(), 1, 50),
						new LocalNotificationListener(caller)));
	}
	
	
	public void recommend(Live l, boolean rend) {
		if (l == null) {
			return;
		}
		l.setRend(rend);
		DeamonWorker.getInstance().request(new LiveRecommendReqPacket(l.getNid(), rend));
	}
	
	public void sendComments(long userId, String msg) {
		V2Log.e(userId+":"+msg);
		InteractionRequest.getInstance().CommentVideo(userId, msg);
	}


	
	class LocalNotificationListener implements NotificationListener {
		
		private WeakReference<MessageListener> listener;
		
		public LocalNotificationListener(MessageListener listener) {
			this.listener = new WeakReference<MessageListener> (listener);
		}

		@Override
		public void onNodification(IndicationPacket ip) {
			
		}

		@Override
		public void onResponse(ResponsePacket rp) {
			if (listener.get() == null) {
				V2Log.e("===> no reference for handle " + rp);
			}
			if (rp instanceof WatcherListQueryRespPacket) {
				handleWatcherListQueryRespPacket((WatcherListQueryRespPacket)rp);
			}
		}

		@Override
		public void onStateChanged() {
			
		}

		@Override
		public void onTimeout(ResponsePacket rp) {
			
		}
		
		
		private void handleWatcherListQueryRespPacket(WatcherListQueryRespPacket resp) {
			MessageListener ml = listener.get();
			if (resp.getHeader().isError()) {
				Message.obtain(ml.getHandler(), ml.what, new AsyncResult(ml.userObj, null)).sendToTarget();
				return;
			}
			List<Watcher> l = new ArrayList<Watcher>(resp.watcherList.size());
			for (Map<String, String> mu : resp.watcherList) {
				Watcher w = new Watcher(-1);
				w.nId = Long.parseLong(mu.get("id"));
				w.setName(mu.get("name"));
				w.lat = Double.parseDouble(mu.get("latitude"));
				w.lng= Double.parseDouble(mu.get("longitude"));
				l.add(w);
			}
			Message.obtain(ml.getHandler(), ml.what, new AsyncResult(ml.userObj, l)).sendToTarget();
		}
		
	}
}


