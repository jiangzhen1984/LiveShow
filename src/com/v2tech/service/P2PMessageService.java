package com.v2tech.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.V2.jni.ChatRequest;
import com.V2.jni.ChatRequestCallbackAdapter;
import com.v2tech.db.MessageDescriptor;
import com.v2tech.db.MessageDescriptor.P2PMessage;
import com.v2tech.vo.User;
import com.v2tech.vo.msg.VMessage;
import com.v2tech.vo.msg.VMessageAbstractItem;
import com.v2tech.vo.msg.VMessageAudioItem;
import com.v2tech.vo.msg.VMessageFaceItem;
import com.v2tech.vo.msg.VMessageImageItem;
import com.v2tech.vo.msg.VMessageTextItem;

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
		saveMessage(vm, user);
		
	}
	
	
	
	public void saveMessage(VMessage vm,  User user) {
		if (wfCtx.get() != null) {
			saveMessage(wfCtx.get(), vm, user);
		}
	}
	
	public static void saveMessage(Context ctx, VMessage vm,  User user) {
		ContentValues values = new ContentValues();
		values.put(MessageDescriptor.P2PMessage.Cols.FROM_USER ,GlobalHolder.getInstance().getCurrentUserId()+ "");
		values.put(MessageDescriptor.P2PMessage.Cols.TO_USER,  user.getmUserId()+ "");
		values.put(MessageDescriptor.P2PMessage.Cols.DATE_TIME,  vm.getDate().getTime());
		values.put(MessageDescriptor.P2PMessage.Cols.READ_FLAG,  vm.isReadState());
		Uri uri = ctx.getContentResolver().insert(MessageDescriptor.P2PMessage.INSERT_URI, values);
		vm.setId(Long.parseLong(uri.getLastPathSegment()));
		
		Uri masterUri = MessageDescriptor.P2PMessageItem.INSERT.buildUpon().appendPath(uri.getLastPathSegment()).build();
		List<VMessageAbstractItem> list = vm.getItems();
		for (VMessageAbstractItem ai : list) {
			values.clear();
			values.put(MessageDescriptor.P2PMessageItem.Cols.TYPE, ai.getType());
			int ty = ai.getType();
			switch (ty) {
			case VMessageAbstractItem.ITEM_TYPE_TEXT:
				values.put(MessageDescriptor.P2PMessageItem.Cols.CONTENT, ((VMessageTextItem)ai).getText());
				break;
			case VMessageAbstractItem.ITEM_TYPE_FACE:
				values.put(MessageDescriptor.P2PMessageItem.Cols.CONTENT, ((VMessageFaceItem)ai).getIndex());
				break;
			case VMessageAbstractItem.ITEM_TYPE_AUDIO:
				values.put(MessageDescriptor.P2PMessageItem.Cols.CONTENT, ((VMessageAudioItem)ai).getAudioFilePath());
				break;
			case VMessageAbstractItem.ITEM_TYPE_IMAGE:
				values.put(MessageDescriptor.P2PMessageItem.Cols.CONTENT, ((VMessageImageItem)ai).getFilePath());
				break;
			}
		
			uri = ctx.getContentResolver().insert(masterUri, values);
		}
	}
	
	public List<VMessage> getVMList(long uid, int start, int count) {
		Context ctx = wfCtx.get();
		if (ctx == null) {
			return null;
		}
		Uri uri = MessageDescriptor.P2PMessage.QUERY_URI.buildUpon().appendPath(200+"").build();
		Cursor cur = ctx.getContentResolver().query(uri, P2PMessage.COL_ARR, null, null,  P2PMessage.Cols.DATE_TIME+" desc ");
		Cursor itemCur = null;
		List<VMessage> list = new ArrayList<VMessage>(cur.getCount());
		while(cur.moveToNext()) {
			long mid = cur.getLong(0);
			long fuid = cur.getLong(1);
			long tuid = cur.getLong(2);
			long time = cur.getLong(3);
			int flag = cur.getInt(4);
			VMessage vm = new VMessage(2, 0, new User(fuid), new User(tuid), new Date(time));
			vm.setId(mid);
			vm.setReadState(flag);
			list.add(vm);
			
			//Query item
			uri = MessageDescriptor.P2PMessageItem.QUERY_URI.buildUpon().appendPath(mid+"").build();
			itemCur = ctx.getContentResolver().query(uri, MessageDescriptor.P2PMessageItem.COL_ARR, null, null, "");
			while(itemCur.moveToNext()) {
				int type = itemCur.getInt(2);
				String text = itemCur.getString(3);
				switch (type) {
				case VMessageAbstractItem.ITEM_TYPE_TEXT:
					new VMessageTextItem(vm, text);
					break;
				case VMessageAbstractItem.ITEM_TYPE_FACE:
					new VMessageFaceItem(vm, Integer.parseInt(text));
					break;
				case VMessageAbstractItem.ITEM_TYPE_AUDIO:
					String[] subItem = text.split(":");
					new VMessageAudioItem(vm, subItem[0], subItem[1], Integer.parseInt(subItem[2]));
					break;
				}
				
				
			}
			itemCur.close();
		}
		
		cur.close();
		return list;
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
