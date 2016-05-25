package com.v2tech.view;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.V2.jni.ind.MessageInd;
import com.V2.jni.util.V2Log;
import com.v2tech.presenter.GlobalPresenterManager;
import com.v2tech.service.LiveMessageHandler;
import com.v2tech.vo.User;
import com.v2tech.vo.msg.VMessage;
import com.v2tech.vo.msg.VMessageAudioVideoRequestItem;
import com.v2tech.vo.msg.VMessageTextItem;

public class ReceiverLiveMessage extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		V2Log.i("===> receive broadcast : " + action);
		if ("com.v2tech.notification_action".equals(action)) {
			handleNotification(intent);
		}
	}

	private void handleNotification(Intent i) {
		String sub = i.getStringExtra("sub");
		if ("com.v2tech.om.v2tech.live_message".equals(sub)) {
			List<LiveMessageHandler> handlers = GlobalPresenterManager
					.getInstance().getLiveMessageHandler();
			MessageInd ind = (MessageInd) i.getSerializableExtra("obj");
			Meta meta = handleNewMessage(ind);
			for (LiveMessageHandler h : handlers) {
				if (meta.mt == MetaType.MESSAGE) {
					if (meta.lid > 0) {
						h.onLiveMessage(meta.lid, meta.uid, ind);
					} else {
						V2Log.d("====> " + ind.content);
						VMessage vm = new VMessage(2, ind.lid, new User(ind.uid), new Date());
						vm.addItem(new VMessageTextItem(vm, ind.content));
						h.onP2PMessage(vm);
					}
				} else if (meta.mt == MetaType.AUDIO_VIDEO_CTL) {
					if (meta.type == VMessageAudioVideoRequestItem.TYPE_VIDEO) {
						h.onVdideoMessage(meta.lid, meta.uid, meta.action);
					} else if (meta.type == VMessageAudioVideoRequestItem.TYPE_AUDIO) {
						h.onAudioMessage(meta.lid, meta.uid, meta.action);
					}
				}
			}
		}
	}

	public Meta handleNewMessage(MessageInd ind) {

		String content = ind.content;
		Pattern p = Pattern.compile("(@)(t[1-2])(l)(\\d+)(u)(\\d+)(a)(\\d)(@)");
		Matcher m = p.matcher(content);
		if (m.find()) {
			int segIndex = 0;
			int actIndex = 0;
			content = m.group();
			int type = Integer.parseInt(content.substring(2, 3));
			segIndex = content.indexOf("u");
			long lid = Long.parseLong(content.substring(4, segIndex));
			actIndex = content.indexOf("a");
			long uid = Long
					.parseLong(content.substring(segIndex + 1, actIndex));
			int action = Integer.parseInt(content.substring(actIndex + 1,
					content.length() - 1));
			return new Meta(MetaType.AUDIO_VIDEO_CTL, type, lid, uid, action,
					null);
		} else {
			return new Meta(MetaType.MESSAGE, 0, ind.lid, ind.uid, 0, ind.content);
		}
	}

	class Meta {
		MetaType mt;
		int type;
		long lid;
		long uid;
		int action;
		String msg;

		public Meta(MetaType mt, int type, long lid, long uid, int action,
				String msg) {
			super();
			this.mt = mt;
			this.type = type;
			this.lid = lid;
			this.uid = uid;
			this.action = action;
			this.msg = msg;
		}

	}

	enum MetaType {
		MESSAGE, AUDIO_VIDEO_CTL,
	}

}
