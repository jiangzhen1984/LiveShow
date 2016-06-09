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
import com.V2.jni.util.V2Log;
import com.v2tech.db.MessageDescriptor;
import com.v2tech.db.MessageDescriptor.P2PMessage;
import com.v2tech.util.MessageUtil;
import com.v2tech.vo.User;
import com.v2tech.vo.msg.VMessageSession;
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
		Uri uri = ctx.getContentResolver().insert(MessageDescriptor.P2PMessage.INSERT_URI.buildUpon().appendPath(user.getmUserId()+"").build(), values);
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
				VMessageAudioItem vai = (VMessageAudioItem) ai;
				String content = 
						vai.getUuid() + ":" +
						vai.getExtension()+ ":" +
						vai.getSeconds() + ":" +
						vai.getReadState();
				values.put(MessageDescriptor.P2PMessageItem.Cols.CONTENT, content);
				break;
			case VMessageAbstractItem.ITEM_TYPE_IMAGE:
				values.put(MessageDescriptor.P2PMessageItem.Cols.CONTENT, ((VMessageImageItem)ai).getFilePath());
				break;
			}
		
			uri = ctx.getContentResolver().insert(masterUri, values);
		}
		
		
		///
	}
	
	public static void saveOrUpdateMessageSession(Context ctx, VMessage vm, boolean unread) {
		saveOrUpdateMessageSession(ctx, vm, null, true, unread);
	}
	
	public static void saveOrUpdateMessageSession(Context ctx, VMessage vm, User fromUser, boolean unread) {
		saveOrUpdateMessageSession(ctx, vm, fromUser, false, unread);
	}
	
	
	public static void saveOrUpdateMessageSession(Context ctx, VMessage vm, User fromUser, boolean systemFlag, boolean unread) {
		
		String selection = null;
		String[] args = null;
		if (systemFlag) {
			selection = MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_TYPE+" = ?";
			args = new String[]{MessageDescriptor.MessageSession.MES_TYPE_SYSTEM+""};
		} else {
			selection = MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_FROM_USER_ID+" = ?";
			args = new String[]{fromUser.getmUserId()+""};
		}
		Cursor cur = ctx.getContentResolver().query(
				MessageDescriptor.MessageSession.CONTENT_URI,
				MessageDescriptor.MessageSession.Cols.ALL_CLOS,
				selection,
				args, "");
		boolean insertFlag = true;
		long sessionId = 0;
		int unreadCount = 0;
		ContentValues values = new ContentValues();
		if (cur.moveToNext()) {
			insertFlag = false;
			sessionId = cur.getLong(0);
			unreadCount = cur.getInt(5);
		}
		cur.close();
		
		
		if (systemFlag) {
			values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_TYPE ,MessageDescriptor.MessageSession.MES_TYPE_SYSTEM);
		} else {
			values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_TYPE ,MessageDescriptor.MessageSession.MES_TYPE_USER);
			values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_FROM_USER_ID ,fromUser.getmUserId());
			values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_FROM_USER_NAME ,fromUser.getName());
		}
		values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_STATE,
				unread ? MessageDescriptor.MessageSession.MES_STATE_READ 
						: MessageDescriptor.MessageSession.MES_STATE_UNREAD );
		values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_UNREAD_COUNT,
				unread ? 0 : unreadCount +1);
		values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_TIMESTAMP, System.currentTimeMillis());
		
		
		if (insertFlag) {
			ctx.getContentResolver().insert(MessageDescriptor.MessageSession.CONTENT_URI, values);
		} else {
			Uri uri = MessageDescriptor.MessageSession.CONTENT_URI.buildUpon().appendPath(sessionId+"").build();
			ctx.getContentResolver().update(uri, values, null, null);
		}
	}
	
	public List<VMessage> getVMList(long uid, int start, int count) {
		Context ctx = wfCtx.get();
		if (ctx == null) {
			return null;
		}
		Uri uri = MessageDescriptor.P2PMessage.QUERY_URI.buildUpon().appendPath(uid+"").build();
		Cursor cur = ctx.getContentResolver().query(uri, P2PMessage.COL_ARR, null, null,   P2PMessage.Cols.DATE_TIME+" desc limit " + start +"," + count);
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
					new VMessageAudioItem(vm, subItem[0], subItem[1], Integer.parseInt(subItem[2]), Integer.parseInt(subItem[3]));
					break;
				}
				
				
			}
			itemCur.close();
		}
		
		cur.close();
		return list;
	}
	
	
	
	
	public List<VMessageSession> getMessageSession(int start, int count) {
		Context ctx = wfCtx.get();
		if (ctx == null) {
			throw new RuntimeException(" context is null");
		}
		Uri uri = MessageDescriptor.MessageSession.CONTENT_URI;
		Cursor cur = ctx
				.getContentResolver()
				.query(uri,
						com.v2tech.db.MessageDescriptor.MessageSession.Cols.ALL_CLOS,
						null,
						null,
						com.v2tech.db.MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_TYPE
								+ ", "
								+ com.v2tech.db.MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_TIMESTAMP
								+ " desc ");
		List<VMessageSession>  list = new ArrayList<VMessageSession>(cur.getCount());
		while (cur.moveToNext()) {
			long mid = cur.getLong(0);
			int type = cur.getInt(1);
			long uid = cur.getLong(2);
			String name = cur.getString(3);
			int state = cur.getInt(4);
			int unreadCount = cur.getInt(5);
			long timestamp = cur.getLong(6);
			VMessageSession sess = new VMessageSession();
			sess.id = mid;
			sess.type = type;
			sess.fromName = name;
			sess.fromUid = uid;
			sess.timestamp = new Date(timestamp);
			sess.read = state;
			sess.unreadCount = unreadCount;
			list.add(sess);
			
			if (type == com.v2tech.db.MessageDescriptor.MessageSession.MES_TYPE_USER) {
				List<VMessage> vmlist = getVMList(uid, 0, 1);
				if (vmlist.size() > 0) {
					VMessage vm = vmlist.get(0);
					sess.content = MessageUtil.buildContent(ctx, vm);
				}
			} else if (type == com.v2tech.db.MessageDescriptor.MessageSession.MES_TYPE_SYSTEM) {
				//TODO load system message
			}
			
		}
		cur.close();
		
		return list;
	}
	
	
	public void updateVMessageReadFlag(long id, boolean readFlag) {
		Uri uri = MessageDescriptor.P2PMessage.URI.buildUpon().appendPath(id+"").build();
		if (wfCtx.get() != null) {
			ContentValues cv = new ContentValues();
			cv.put(MessageDescriptor.P2PMessage.Cols.READ_FLAG,
					readFlag ? VMessageAbstractItem.STATE_READED
							: VMessageAbstractItem.STATE_UNREAD);
			int ret = wfCtx.get().getContentResolver().update(uri, cv, null, null);
			V2Log.i("==> update message: "+id+"  with flag :" + readFlag+"  ret:"+ ret);
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
