package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.ConfRequestCallback;
import com.V2.jni.util.V2Log;

public class ConfRequest {
	private static ConfRequest mConfRequest;
	private List<WeakReference<ConfRequestCallback>> mCallBacks;

	private ConfRequest() {
		this.mCallBacks = new ArrayList<WeakReference<ConfRequestCallback>>();
	};

	public static synchronized ConfRequest getInstance() {
		if (mConfRequest == null) {
			synchronized (ConfRequest.class) {
				if (mConfRequest == null) {
					mConfRequest = new ConfRequest();
					if (!mConfRequest.initialize(mConfRequest)) {
						throw new RuntimeException("can't initilaize ConfRequest");
					}
				}
			}
		}
		return mConfRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallback(ConfRequestCallback callback) {
		this.mCallBacks.add(new WeakReference<ConfRequestCallback>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(ConfRequestCallback callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native boolean initialize(ConfRequest request);

	public native void unInitialize();

	/**
	 * @brief 进入会议
	 *@deprecated
	 * @param nConfID
	 *            会议ID
	 *
	 * @return None
	 */
	public native void ConfEnter(long nConfID);

	/**
	 * @brief 退出会议
	 *
	 * @param nConfID
	 *            会议ID
	 *
	 * @return None
	 */
	public native void ConfExit(long nConfID);
	
	
	public native void ConfEnumMembers(long gid);


	/**
	 * @brief 将某人请出会议
	 *
	 * @param nUserID
	 *            需要请出会议的用户ID
	 *
	 * @return None
	 */
	// public native void ConfKickUser(long nUserID);

	/**
	 * 获取会议中的用户列表
	 * 
	 * @param nConfID
	 */
	// public native void getConfUserList(long nConfID);

	/**
	 * 重新开始一个会议
	 * 
	 * @param sXmlConfData
	 * @param sXmlInviteUsers
	 */
	// public native void resumeConf(String sXmlConfData, String
	// sXmlInviteUsers);

	public native void ConfMute();

	/**
	 * @brief 会议中申请权限
	 *
	 * @param type
	 *            权限类型
	 * @return None
	 */
	public native void ConfApplyPermission(int type);

	/**
	 * @brief 会议中释放权限
	 *
	 * @param type
	 *            权限类型
	 *
	 * @return None
	 */
	public native void ConfReleasePermission(int type);

	/**
	 * @brief 给一个用户授权
	 *
	 * @param nUserID
	 *            用户ID
	 * @param nType
	 *            权限类型
	 * @param nPermissionStatus
	 *            权限状态
	 *
	 * @return None
	 */
	public native void ConfGrantPermission(long nUserID, int nType, int nPermissionStatus);
	
	
	
	

	/**
	 * 同步打开某人视频
	 * 
	 * @param nGroupID
	 * @param nToUserID
	 * @param szDeviceID
	 * @param nPos
	 */
	// public native void syncConfOpenVideo(long nGroupID, long nToUserID,
	// String szDeviceID, int nPos);

	/**
	 * 同步取消某人视频
	 * 
	 * @param nGroupID
	 * @param nToUserID
	 * @param szDeviceID
	 * @param bCloseVideo
	 */
	// public native void cancelSyncConfOpenVideo(long nGroupID, long nToUserID,
	// String szDeviceID, boolean bCloseVideo);

	/**
	 * @brief 会议中更改同步到Android端的视频的位置
	 *
	 * @param nUserID
	 *            视频所属的用户的ID
	 * @param szDeviceID
	 *            视频设备ID
	 * @param szPositionXml
	 *            新位置XML
	 *
	 * @return None
	 */
	// public native void changeSyncConfOpenVideoPos(long nUserID, String
	// szDeviceID, String szPositionXml);

	/**
	 * @brief 会议中同步视频到Android端
	 *
	 * @param nGroupID
	 *            会议ID
	 * @param szVideosXml
	 *            需要同步的视频列表XML
	 *
	 * @return None
	 */
	// public native void syncConfOpenVideoToMobile(long nGroupID, String
	// szVideosXml);

	/**
	 * @brief 会议中取消一路同步到android端的视频
	 *
	 * @param nGroupID
	 *            会议ID
	 * @param nUserID
	 *            视频所属的用户的ID
	 * @param szDeviceID
	 *            视频设备ID
	 *
	 * @return None
	 */
	// public native void cancelSyncConfOpenVideoToMobile(long nGroupID, long
	// nUserID, String szDeviceID);

	/**
	 * 指定视频被Sip用户打开
	 * 
	 * @param nGroupID
	 * @param nSipUserID
	 * @param nDstUserID
	 * @param sDstDevID
	 */
	// public native void TestConfSipOpenVideo(long nGroupID, long nSipUserID,
	// long nDstUserID, String sDstDevID);

	// public native void startMixerVideoToSip(long nSipUserID, String
	// szMediaID);

	// public native void stopMixerVideoToSip(long nSipUserID, String
	// szMediaID);

	/**
	 * 更改会议主席
	 * 
	 * @param nGroupID
	 * @param nUserID
	 */
	// public native void changeConfChair(long nGroupID, long nUserID);

	/**
	 * 获得会场内所有通知消息
	 * 
	 * @param nGroupID
	 */
	public native void NotifyConfAllMessage(long nGroupID);

	/**
	 * @brief 快速加入某个会议
	 * @deprecated
	 * @param eUEType
	 *            用户类型
	 * @param szUser
	 *            用户名字
	 * @param nUserID
	 *            用户ID
	 * @param nGroupID
	 *            组ID
	 * @param eUserRole
	 *            用户登录会议中的权限
	 */
//	public native void ConfQuickEnter(int eUEType, String szUser, long nUserID, long nGroupID, int eUserRole);
	
	
	public native void ConfQuickEnter(int eUEType, String szUser, long groupId, int role);


	/**
	 * Use to join meeting with CDN
	 * @param eUEType   1 pc 2 android 3 ios
	 * @param szUser use id
	 * @param groupId  group id
     * @param role   1 host 2 attendee
     */
	public native void ConfQuickEnter(int eUEType, long szUser, long groupId, int role);



	/**
	 * @brief 会议中同步视频
	 *
	 * @param nGroupID
	 *            会议ID
	 * @param szVideosXml
	 *            需要同步的视频列表XML
	 *
	 * @return None
	 */
	public native void ConfOpenSyncVideo(long nGroupID, String szVideosXml);

	/**
	 * @brief 会议中取消一路同步端的视频
	 *
	 * @param nGroupID
	 *            会议ID
	 * @param nUserID
	 *            视频所属的用户的ID
	 * @param szDeviceID
	 *            视频设备ID
	 *
	 * @return None
	 */
	public native void ConfCancelSyncVideo(long nGroupID, long nUserID, String szDeviceID);

	/**
	 * @brief 我进入会议回调函数
	 *
	 * @param nConfID
	 *            会议ID
	 * @param nTimeUTC
	 *            进入会议时间
	 * @param szConfXml
	 *            会议数据 （szConfData格式：
	 *            <conf createuserid='' createusernickname = '' endtime='' id=''
	 *            layout='' starttime='' subject='' canoper = '' inviteuser = ''
	 *            voiceactivation = '' syncdesktop='' chairuserid=''
	 *            chairnickname=''/>）
	 * @param nResult
	 *            进入会议结果
	 *
	 * @return None
	 */
	private void OnConfEnter(long nConfID, long nTimeUTC, String szConfXml, int nResult) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnEnterConfCallback(nConfID, nTimeUTC, szConfXml, nResult);
			}
		}
	}

	/**
	 * @brief 会议有用户进入回调函数
	 *
	 * @param nConfID
	 *            会议ID
	 * @param nUserID
	 *            该用户ID
	 * @param nTimeUTC
	 *            进入会议时间
	 * @param szUserXml
	 *            该用户基本信息 szUserXml格式：
	 *            <user avatarlocation='' avatarname='' bsystemavatar=''
	 *            email='' id='' needauth='' nickname="" privacy='' sign=''/>
	 *
	 * @return None
	 */
	private void OnConfMemberEnter(long nConfID, long nUserID, long nTimeUTC, String szUserXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConfMemberEnter(nConfID, nUserID, nTimeUTC, szUserXml);
			}
		}
	}

	/**
	 * @brief 会议有用户退出回调函数
	 *
	 * @param nConfID
	 *            会议ID
	 * @param nTimeUTC
	 *            退出会议时间
	 * @param nUserID
	 *            该用户ID
	 *
	 * @return None
	 */
	private void OnConfMemberExit(long nConfID, long nTimeUTC, long nUserID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConfMemberExitCallback(nConfID, nTimeUTC, nUserID);
			}
		}
	}

	/**
	 * @brief 我被请出会议回调函数
	 *
	 * @param nReason
	 *            请出会议理由
	 *
	 * @return None
	 */
	private void OnConfKicked(int nReason) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnKickConfCallback(nReason);
			}
		}
	}

	/**
	 * @brief 收到会议通知回调函数
	 *
	 * @param szConfXml
	 *            会议基本信息Xml
	 * @param nSrcUserID
	 *            邀请者用户基本信息
	 *
	 * @return None
	 */
	private void OnConfNotify(String szConfXml, String nSrcUserID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConfNotify(szConfXml, nSrcUserID);
			}
		}
	}

	/**
	 * 收到会议中某个用户申请某个权限的回调函数
	 * 
	 * @param nUserID
	 *            用户ID
	 * @param nPermissionType
	 *            权限类型
	 */
	private void OnConfPermissionApply(long nUserID, int nPermissionType) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnNotifyChair(nUserID, nPermissionType);
			}
		}
	}

	/**
	 * @brief 收到会议中某个用户被授予某个权限的回调函数
	 *
	 * @param nUserID
	 *            用户ID
	 * @param nPermissionType
	 *            权限类型
	 * @param nPermissionStatus
	 *            权限状态
	 *
	 * @return None
	 */
	private void OnConfPermissionGranted(long nUserID, int nPermissionType, int nPermissionStatus) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGrantPermissionCallback(nUserID, nPermissionType, nPermissionStatus);
			}
		}
	}

	/**
	 * @brief 收到会议中主持人静音的回调函数
	 *
	 * @return None
	 */
	private void OnConfAllSpeakMuted() {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConfMute();
			}
		}
	}

	private void OnConfSyncVideoOpened(String xml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConfSyncOpenVideo(xml);
			}
		}
	}

	private void OnConfSyncVideoCloseed(long gid, String xml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConfSyncCloseVideo(gid, xml);
			}
		}
	}

	// private void OnChangeSyncConfOpenVideoPos(long nDstUserID, String
	// szDeviceID, String sPos) {
	// for (int i = 0; i < this.mCallBacks.size(); i++) {
	// WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnChangeSyncConfOpenVideoPos(nDstUserID, szDeviceID, sPos);
	// }
	// }
	// };

	private void OnConfSyncVideoToMobileOpened(String sSyncVideoMsgXML) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConfSyncOpenVideoToMobile(sSyncVideoMsgXML);
			}
		}
	}

	private void OnConfSyncVideoToMobileCloseed(long nDstUserID, String sDstMediaID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConfSyncCloseVideoToMobile(nDstUserID, sDstMediaID);
			}
		}
	};

	private void OnConfNotifyEnd(long nConfID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConfNotifyEnd(nConfID);
			}
		}
	}

	private void OnConfChairChanged(long nSrcUserID, long arg2) {
	}

	// private void OnConfSyncCloseVideo(long nDstUserID, String sDstMediaID,
	// boolean bClose) {
	// for (int i = 0; i < this.mCallBacks.size(); i++) {
	// WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnConfSyncCloseVideo(nDstUserID, sDstMediaID, bClose);
	// }
	// }
	// }

	// private void OnGetConfVodList(long nGroupID, String sVodXmlList) {
	// for (int i = 0; i < this.mCallBacks.size(); i++) {
	// WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnGetConfVodList(nGroupID, sVodXmlList);
	// }
	// }
	// };



	private void OnConfEnter(long gid, int uid, String str, int t) {
		V2Log.e("OnConfEnter====>" + gid +"  ==" + uid +"   "+ str+"  "+t);

		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ConfRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnEnterConfCallback(gid, 0, str, 0);
			}
		}
	}
}