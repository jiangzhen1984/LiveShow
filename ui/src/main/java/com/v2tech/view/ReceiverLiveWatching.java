package com.v2tech.view;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.v2tech.net.lv.LiveWatchingReqPacket;
import com.v2tech.presenter.GlobalPresenterManager;
import com.v2tech.service.LiveWathcingHandler;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;

public class ReceiverLiveWatching extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (!"com.v2tech.notification_action".equals(action)) {
			return;
		}
		String sub = intent.getStringExtra("sub");
		if (!"com.v2tech.live_watching".equals(sub)) {
			return;
		}
		Long nid = (Long) intent.getLongExtra("live", -1);
		User u = (User) intent.getSerializableExtra("user");
		int type = intent.getIntExtra("type", -1);
		List<LiveWathcingHandler> handlers = GlobalPresenterManager
				.getInstance().getLiveWathcingHandler();
		Live l = new Live(null, -1, nid, 0, 0);
		for (LiveWathcingHandler h : handlers) {
			// watching
			if (type == LiveWatchingReqPacket.WATCHING) {
				h.onUserWatched(l, u);
				// leave
			} else if (type == LiveWatchingReqPacket.CANCEL) {
				h.onWatcherLeaved(l, u);
			}
		}
	}

}
