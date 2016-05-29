package com.v2tech.service;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.V2.jni.ChatRequest;
import com.V2.jni.ChatRequestCallbackAdapter;
import com.v2tech.db.MessageDescriptor;
import com.v2tech.vo.User;
import com.v2tech.vo.msg.VMessage;

public class P2PMessageService extends AbstractHandler {
	
	private WeakReference<Context> wfCtx;
	
	private LocalChatCB chatCB;
	
	private Map<String, WeakReference<MessageListener>> pendingListener;
	
	public P2PMessageService() {
		super();
		chatCB = new LocalChatCB();
		ChatRequest.getInstance().addChatRequestCallback(chatCB);
		pendingListener = new HashMap<String, WeakReference<MessageListener>>();
	}
	
	
	public P2PMessageService(Context ctx) {
		super();
		wfCtx = new WeakReference<Context>(ctx);
		chatCB = new LocalChatCB();
		ChatRequest.getInstance().addChatRequestCallback(chatCB);
		pendingListener = new HashMap<String, WeakReference<MessageListener>>();
	}

	public void sendMessage(VMessage vm, User user) {
		sendMessage(vm, user, null);
	}
	
	public void sendMessage(VMessage vm, User user, MessageListener listener) {
		pendingListener.put(vm.getUUID(), new WeakReference<MessageListener>(listener));
		byte[] buf = vm.toXml().getBytes();
		ChatRequest.getInstance().ChatSendTextMessage(vm.getMsgCode(), vm.getGroupId(), user.getmUserId(), vm.getUUID(), buf, buf.length);
		if (wfCtx.get() != null) {
			ContentValues values = new ContentValues();
			values.put(MessageDescriptor.P2PMessage.Cols.FROM_USER ,GlobalHolder.getInstance().getCurrentUserId()+ "");
			values.put(MessageDescriptor.P2PMessage.Cols.TO_USER,  user.getmUserId()+ "");
			values.put(MessageDescriptor.P2PMessage.Cols.DATE_TIME,  vm.getDate().getTime());
			values.put(MessageDescriptor.P2PMessage.Cols.DATE_TIME,  vm.isReadState());
			Uri uri = wfCtx.get().getContentResolver().insert(MessageDescriptor.P2PMessage.INSERT, values);
			vm.setId(Long.parseLong(uri.getLastPathSegment()));
		}
	}

	@Override
	public void clearCalledBack() {
		ChatRequest.getInstance().removeChatRequestCallback(chatCB);
		pendingListener.clear();
	}

	
	
	class LocalChatCB extends ChatRequestCallbackAdapter {

		@Override
		public void OnRecvChatTextCallback(int eGroupType, long nGroupID,
				long nFromUserID, long nToUserID, long nTime, String szSeqID,
				String szXmlText) {
			
		}

		@Override
		public void OnSendTextResultCallback(int eGroupType, long nGroupID,
				long nFromUserID, long nToUserID, String sSeqID, int nResult) {
			super.OnSendTextResultCallback(eGroupType, nGroupID, nFromUserID, nToUserID,
					sSeqID, nResult);
			WeakReference<MessageListener> wf = pendingListener.remove(sSeqID);
			if (wf != null && wf.get() != null) {
				//TODO send response
			}
		}
		
	}
}
