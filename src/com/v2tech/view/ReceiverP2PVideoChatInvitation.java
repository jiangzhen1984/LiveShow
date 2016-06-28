package com.v2tech.view;

import com.v2tech.presenter.P2PVideoPresenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverP2PVideoChatInvitation extends BroadcastReceiver {



	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if ("com.v2tech.p2p_video_notification_action".equals(action)) {
			Intent i = new Intent();
			i.setClass(context, P2PVideoActivity.class);
			i.putExtra("type", P2PVideoPresenter.UIType.RINGING.ordinal());
			//i.putExtra("username", P2PVideoPresenter.UIType.RINGING);
			i.putExtra("session", intent.getStringExtra("session"));
			i.putExtra("device", intent.getStringExtra("device"));
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.getApplicationContext().startActivity(i);
		}
	}
	
}
