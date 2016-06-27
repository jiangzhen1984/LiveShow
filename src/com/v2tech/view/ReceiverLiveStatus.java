package com.v2tech.view;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.V2.jni.util.V2Log;
import com.v2tech.presenter.GlobalPresenterManager;
import com.v2tech.service.LiveStatusHandler;
import com.v2tech.vo.Live;

public class ReceiverLiveStatus extends BroadcastReceiver {



	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if ("com.v2tech.notification_action".equals(action)) {
			handleNotification(intent);
		}
	}
	
	
	private void handleNotification(Intent i) {
		String sub = i.getStringExtra("sub");
		if ("com.v2tech.live_publish".equals(sub)) {
			List<LiveStatusHandler> handlers = GlobalPresenterManager.getInstance().getLiveStatusHandler();
			Live l = (Live)i.getSerializableExtra("obj");
			for (LiveStatusHandler h : handlers) {
				h.handleNewLivePushlishment(l);
			}
		} else if ("com.v2tech.live_publish".equals(i.getAction())) {
			List<LiveStatusHandler> handlers = GlobalPresenterManager.getInstance().getLiveStatusHandler();
			Live l = (Live)i.getSerializableExtra("obj");
			for (LiveStatusHandler h : handlers) {
				h.handleLiveFinished(l);
			}
		}
	}

	
	
}
