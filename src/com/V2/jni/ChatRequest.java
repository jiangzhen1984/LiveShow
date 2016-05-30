package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.ChatRequestCallback;
import com.V2.jni.util.V2Log;

public class ChatRequest {
	private static ChatRequest mChatRequest;
	private List<WeakReference<ChatRequestCallback>> callbacks;

	List<ChatText> ctL = new ArrayList<ChatText>();
	List<ChatBinary> btL = new ArrayList<ChatBinary>();

	private ChatRequest() {
		callbacks = new ArrayList<WeakReference<ChatRequestCallback>>();
	}

	public static synchronized ChatRequest getInstance() {
		if (mChatRequest == null) {
			synchronized (ChatRequest.class) {
				if (mChatRequest == null) {
					mChatRequest = new ChatRequest();
					if (!mChatRequest.initialize(mChatRequest)) {
						throw new RuntimeException(
								"can't initilaize ChatRequest");
					}
				}
			}
		}
		return mChatRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addChatRequestCallback(ChatRequestCallback callback) {
		synchronized (callbacks) {
			this.callbacks
					.add(new WeakReference<ChatRequestCallback>(callback));
			for (ChatText ct : ctL) {
				callback.OnRecvChatTextCallback(ct.eGroupType, ct.nGroupID,
						ct.nToUserID, ct.nFromUserID, ct.nTime, ct.szSeqID,
						ct.szXmlText);
			}

			for (ChatBinary cb : btL) {
				callback.OnRecvChatBinaryCallback(cb.eGroupType, cb.nGroupID,
						cb.nFromUserID, cb.nToUserID, cb.nTime, cb.binaryType,
						cb.messageId, cb.binaryPath);
			}
			// clear cache.
			ctL.clear();
			btL.clear();
		}
	}

	public void removeChatRequestCallback(ChatRequestCallback callback) {
		synchronized (callbacks) {
			for (WeakReference<ChatRequestCallback> wf : callbacks) {
				if (wf.get() == callback) {
					callbacks.remove(wf);
					break;
				}
			}
		}
	}

	public native boolean initialize(ChatRequest request);

	public native void unInitialize();

	/**
	 * 发送文字消息
	 * 
	 * @param eGroupType
	 *            组的类型
	 * @param nGroupID
	 *            组的ID
	 * @param nToUserID
	 *            远端用户的ID
	 * @param szTextID
	 *            消息的唯一ID
	 * @param szTextXml
	 *            消息体
	 * @param nTextLen
	 *            长度
	 */
	public native void ChatSendTextMessage(int eGroupType, long nGroupID,
			long nToUserID, String szTextID, byte[] szTextXml, int nTextLen);

	/**
	 * 发送聊天的图片或语音的二进制信息
	 * 
	 * @param eGroupType
	 *            组的类型
	 * @param nGroupID
	 *            组的ID
	 * @param nToUserID
	 *            远端用户的ID
	 * @param nBinaryType
	 *            标识二进制数据是音频还是图片
	 * @param szBinaryID
	 *            消息的唯一ID
	 * @param szFileName
	 *            文件路径
	 */
	public native void ChatSendBinaryMessage(int eGroupType, long nGroupID,
			long nToUserID, int nBinaryType, String szBinaryID,
			String szFileName);

	/**
	 * 关注传输二进制图片是否超时
	 * 
	 * @param nBinaryType
	 *            类型是图片还是语音
	 * @param szBinaryID
	 *            接受数据的ID
	 */
	public native void ChatMonitorRecvBinary(int nBinaryType, String szBinaryID);

	/**
	 * @brief 收到文字聊天消息的回调函数
	 * 
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nFromUser
	 *            发送者的用户ID
	 * @param nToUserID
	 *            接收者的用户ID
	 * @param nTime
	 *            发送的时间
	 * @param szMessageID
	 *            文字聊天数据ID
	 * @param szTextXml
	 *            文字聊天数据
	 * @return None
	 */
	private void OnChatRecvTextMessage(int nGroupType, long nGroupID,
			long nFromUser, long nToUserID, long nTime, String szMessageID,
			String szTextXml) {
		V2Log.d(szTextXml);
		synchronized (callbacks) {
			if (callbacks.size() > 0) {
				for (WeakReference<ChatRequestCallback> wf : callbacks) {
					if (wf.get() == null) {
						continue;
					}
					wf.get()
							.OnRecvChatTextCallback(nGroupType, nGroupID,
									nFromUser, nToUserID, nTime, szMessageID,
									szTextXml);
				}
			} else {
				ctL.add(new ChatText(nGroupType, nGroupID, nFromUser,
						nToUserID, nTime, szMessageID, szTextXml));
			}
		}
	}

	/**
	 * @brief 收到二进制消息
	 * 
	 * @param eGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nFromUserID
	 *            发送者的用户ID
	 * @param nToUserID
	 *            接收者的用户ID
	 * @param nTime
	 *            发送的时间
	 * @param nBinaryType
	 *            二进制数据类型
	 * @param szBinaryID
	 *            二进制唯一标识
	 * @param szFileName
	 *            在本地的保存地址
	 * 
	 * @return None
	 */
	private void OnChatRecvBinaryMessage(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, long nTime, int nBinaryType,
			String szBinaryID, String szFileName) {
		synchronized (callbacks) {
			if (callbacks.size() > 0) {
				for (WeakReference<ChatRequestCallback> wf : callbacks) {
					if (wf.get() == null) {
						continue;
					}
					wf.get().OnRecvChatBinaryCallback(eGroupType, nGroupID,
							nFromUserID, nToUserID, nTime, nBinaryType,
							szBinaryID, szFileName);
				}
			} else {
				btL.add(new ChatBinary(eGroupType, nGroupID, nFromUserID,
						nToUserID, nTime, nBinaryType, szBinaryID, szFileName));
			}
		}
	}

	/**
	 * @brief 发送聊天数据结果
	 * @param eGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nFromUserID
	 *            发送者的用户ID
	 * @param nToUserID
	 *            接收者的用户ID
	 * @param sSeqID
	 *            消息ID
	 * @param nResult
	 *            结果
	 */
	private void OnChatSendTextMessageResult(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, String sSeqID, int nResult) {
		synchronized (callbacks) {
			if (callbacks.size() > 0) {
				for (WeakReference<ChatRequestCallback> wf : callbacks) {
					if (wf.get() == null) {
						continue;
					}
					wf.get().OnSendTextResultCallback(eGroupType, nGroupID,
							nFromUserID, nToUserID, sSeqID, nResult);
				}
			}
		}
	}

	/**
	 * @brief 发送二进制数据结果
	 * @param eGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nFromUserID
	 *            发送者的用户ID
	 * @param nToUserID
	 *            接收者的用户ID
	 * @param mediaType
	 *            二进制数据类型
	 * @param sSeqID
	 *            消息ID
	 * @param nResult
	 *            发送结果
	 */
	private void OnChatSendBinaryMessageResult(int eGroupType, long nGroupID,
			long nFromUserID, long nToUserID, int mediaType, String sSeqID,
			int nResult) {
		synchronized (callbacks) {
			if (callbacks.size() > 0) {
				for (WeakReference<ChatRequestCallback> wf : callbacks) {
					if (wf.get() == null) {
						continue;
					}
					wf.get().OnSendBinaryResultCallback(eGroupType, nGroupID,
							nFromUserID, nToUserID, mediaType, sSeqID, nResult);
				}
			}
		}
	}

	private void OnChatMonitorRecvBinaryResult(int eGroupType, String sSeqID,
			int nResult) {
		synchronized (callbacks) {
			if (callbacks.size() > 0) {
				for (WeakReference<ChatRequestCallback> wf : callbacks) {
					if (wf.get() == null) {
						continue;
					}
					wf.get().OnMonitorRecv(eGroupType, sSeqID, nResult);
				}
			}
		}
	}

	class ChatText {
		int eGroupType;
		long nGroupID;
		long nFromUserID;
		long nToUserID;
		long nTime;
		String szSeqID;
		String szXmlText;

		public ChatText(int eGroupType, long nGroupID, long nFromUserID,
				long nToUserID, long nTime, String szSeqID, String szXmlText) {
			super();
			this.eGroupType = eGroupType;
			this.nGroupID = nGroupID;
			this.nToUserID = nToUserID;
			this.szSeqID = szSeqID;
			this.nFromUserID = nFromUserID;
			this.nTime = nTime;
			this.szXmlText = szXmlText;
		}

	}

	class ChatBinary {
		int eGroupType;
		long nGroupID;
		long nFromUserID;
		long nToUserID;
		long nTime;
		int binaryType;
		String messageId;
		String binaryPath;

		public ChatBinary(int eGroupType, long nGroupID, long nFromUserID,
				long nToUserID, long nTime, int binaryType, String messageId,
				String binaryPath) {
			super();
			this.eGroupType = eGroupType;
			this.nGroupID = nGroupID;
			this.nFromUserID = nFromUserID;
			this.nToUserID = nToUserID;
			this.nTime = nTime;
			this.binaryType = binaryType;
			this.messageId = messageId;
			this.binaryPath = binaryPath;
		}
	}
}