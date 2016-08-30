package com.v2tech.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.V2.jni.util.V2Log;
import com.V2.jni.util.XmlAttributeExtractor;
import com.v2tech.presenter.GlobalPresenterManager;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.LiveMessageHandler;
import com.v2tech.service.P2PMessageService;
import com.v2tech.service.jni.MessageInd;
import com.v2tech.vo.User;
import com.v2tech.vo.msg.VMessage;
import com.v2tech.vo.msg.VMessageAudioItem;
import com.v2tech.vo.msg.VMessageAudioVideoRequestItem;
import com.v2tech.vo.msg.VMessageFaceItem;
import com.v2tech.vo.msg.VMessageTextItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiverLiveMessage extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		V2Log.i("===> receive broadcast : " + action);
		if ("com.v2tech.notification_action".equals(action)) {
			handleNotification(context, intent);
		}
	}

	private void handleNotification(Context context, Intent i) {
		String sub = i.getStringExtra("sub");
		if ("com.v2tech.live_message".equals(sub)) {
			List<LiveMessageHandler> handlers = GlobalPresenterManager
					.getInstance().getLiveMessageHandler();
			MessageInd ind = (MessageInd) i.getSerializableExtra("obj");
			Meta meta = handleNewMessage(ind);
			VMessage vm = null;
			
			if (meta.mt == MetaType.MESSAGE && meta.lid <= 0) {
				vm = extraMessage(ind);
			}
			for (LiveMessageHandler h : handlers) {
				if (meta.mt == MetaType.MESSAGE) {
					if (meta.lid > 0) {
						vm = extraMessage(ind);
						h.onLiveMessage(meta.lid, meta.uid, vm);
					} else {
						h.onP2PMessage(vm);
					}
				} else if (meta.mt == MetaType.AUDIO_VIDEO_CTL) {
					if (meta.type == VMessageAudioVideoRequestItem.TYPE_VIDEO) {
						h.onVideoMessage(meta.lid, meta.uid, meta.action);
					} else if (meta.type == VMessageAudioVideoRequestItem.TYPE_AUDIO) {
						h.onAudioMessage(meta.lid, meta.uid, meta.action);
					}
				}
			}
			
			if (vm != null) {
				P2PMessageService.saveP2PMessage(context.getApplicationContext(), vm);
				P2PMessageService.saveOrUpdateMessageSession(context.getApplicationContext(), vm,  vm.getFromUser(), false, true);
			}
		}
	}
	
	private VMessage extraMessage(MessageInd ind) {
		Document doc = XmlAttributeExtractor.buildDocument(ind.content);
		NodeList  itemNode = doc.getElementsByTagName("ItemList");
		if (itemNode.getLength() <= 0) {
			return null;
		}
		// 4 for group  2 for p2p
		VMessage vm = new VMessage(ind.lid > 0 ? 4 : 2 , ind.lid, new User(ind.uid) , new Date());
		vm.setToUser(GlobalHolder.getInstance().getCurrentUser());
		NodeList iList = itemNode.item(0).getChildNodes();
		int len = iList.getLength();
		for (int i = 0; i < len; i++) {
			Node n = iList.item(i);
			if (!(n instanceof Element)) {
				continue;
			}
			Element e = (Element) n;
			if ("TTextChatItem".equalsIgnoreCase(e.getTagName())) {
				String content = e.getAttribute("Text");
				new VMessageTextItem(vm, content);
			} else if ("TSysFaceChatItem".equalsIgnoreCase(e.getTagName())) {
				int idx = Integer.parseInt(e.getAttribute("idx"));
				new VMessageFaceItem(vm, idx);
			}  else if ("TAudioChatItem".equalsIgnoreCase(e.getTagName())) {
				String uuid =  e.getAttribute("FileID");
				String extension =  e.getAttribute("FileExt");
				int ses = Integer.parseInt(e.getAttribute("Seconds"));
				new VMessageAudioItem(vm, uuid, extension, ses);
			}
		}
		
		return vm;
	}
	

	private Meta handleNewMessage(MessageInd ind) {

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
