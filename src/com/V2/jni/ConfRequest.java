package com.V2.jni;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.V2.jni.ind.V2Conference;
import com.V2.jni.ind.V2User;
import com.V2.jni.util.V2Log;
import com.V2.jni.util.XmlAttributeExtractor;

public class ConfRequest {

	private static ConfRequest mConfRequest;

	private List<WeakReference<ConfRequestCallback>> mCallbacks;

	private ConfRequest(Context context) {
		this.mCallbacks = new ArrayList<WeakReference<ConfRequestCallback>>();
	};

	public static synchronized ConfRequest getInstance(Context context) {
		if (mConfRequest == null) {
			mConfRequest = new ConfRequest(context);
			mConfRequest.initialize(mConfRequest);
		}

		return mConfRequest;
	}

	public static synchronized ConfRequest getInstance() {
		if (mConfRequest == null) {
			mConfRequest = new ConfRequest(null);
			mConfRequest.initialize(mConfRequest);
		}

		return mConfRequest;
	}

	public void addCallback(ConfRequestCallback callback) {
		this.mCallbacks.add(new WeakReference<ConfRequestCallback>(callback));
	}

	public void removeCallback(ConfRequestCallback callback) {
		for (int i = 0; i < this.mCallbacks.size(); i++) {
			if (this.mCallbacks.get(i).get() == callback) {
				this.mCallbacks.remove(i);
				break;
			}
		}
	}

	private boolean isInConf = false;

	public native boolean initialize(ConfRequest request);

	public native void unInitialize();

	public native void changeSyncConfOpenVideoPos(long ty, String str,
			String str1);

	private void OnChangeSyncConfOpenVideoPos(long ty, String str, String str1) {

	}

	public native void cancelSyncConfOpenVideoToMobile(long l1, long l2,
			String str);

	private void onCancelSyncConfOpenVideoToMobile(long l1, long l2, String str) {

	}

	public native void syncConfOpenVideoToMobile(long ty, String str);

	
	private void OnConfSyncCloseVideoToMobile(long ty, String str) {
		
	}


	public void OnConfSyncOpenVideoToMobile(String str) {

	}

	// 鍒犻櫎浼氳
	public native void destroyConf(long nConfID);

	/**
	 * <ul>
	 * User request to enter conference. will call {@link OnEnterConf} function
	 * to indicate response.
	 * </ul>
	 * 
	 * @param nConfID
	 *            conference ID which user request to enter
	 * 
	 * @see #OnEnterConf(long, long, String, int)
	 * @see ConfRequestCallback
	 */
	public native void enterConf(long nConfID);

	/**
	 * <ul>
	 * Callback of {@link #enterConf(long)}, Indicate response of result.
	 * </ul>
	 * 
	 * @param nConfID
	 *            conference ID which user request to enter
	 * @param nTime
	 *            entered time
	 * @param szConfData
	 * @param nJoinResult
	 *            result of join. 0 success 1:falied
	 */
	private void OnEnterConf(long nConfID, long nTime, String szConfData,
			int nJoinResult) {
		for (int i = 0; i < this.mCallbacks.size(); i++) {
			WeakReference<ConfRequestCallback> we = this.mCallbacks.get(i);
			Object obj = we.get();
			if (obj != null) {
				ConfRequestCallback cb = (ConfRequestCallback) obj;
				cb.OnEnterConfCallback(nConfID, nTime, szConfData, nJoinResult);
			}
		}
		V2Log.d("OnEnterConf called  nConfID:" + nConfID + "  nTime:" + nTime
				+ " szConfData:" + szConfData + " nJoinResult:" + nJoinResult);
	}

	/**
	 * <ul>
	 * User request exit conference, this request no callback call.
	 * </ul>
	 * 
	 * @param nConfID
	 *            conference ID which user request to exit
	 * 
	 * @see ConfRequestCallback
	 */
	public native void exitConf(long nConfID);

	/**
	 * 
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param szUserInfos
	 *            <user accounttype='2' id='81003' nickname='1234' uetype='1'/>
	 * 
	 * @see ConfRequestCallback
	 */
	private void OnConfMemberEnter(long nConfID, long nTime, String szUserInfos) {
		V2Log.d("-->OnConfMemberEnter " + nConfID + " " + nTime + " "
				+ szUserInfos);

		String uid = XmlAttributeExtractor.extract(szUserInfos, "id='", "'");
		String act = XmlAttributeExtractor.extract(szUserInfos,
				"accounttype='", "'");
		String nickName = XmlAttributeExtractor.extract(szUserInfos,
				"nickname='", "'");

		if (uid == null) {
			V2Log.e("Can not dispatch request for member joined, cause by uid is null");
			return;
		}

		V2User v2user = new V2User();
		v2user.uid = Long.parseLong(uid);
		if (act != null) {
			v2user.type = Integer.parseInt(act);
		}
		v2user.name = nickName;

		for (int i = 0; i < this.mCallbacks.size(); i++) {
			WeakReference<ConfRequestCallback> we = this.mCallbacks.get(i);
			Object obj = we.get();
			if (obj != null) {
				ConfRequestCallback cb = (ConfRequestCallback) obj;
				cb.OnConfMemberEnterCallback(nConfID, nTime, v2user);
			}
		}
	}

	/**
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param nUserID
	 */
	private void OnConfMemberExit(long nConfID, long nTime, long nUserID) {
		V2Log.d("-->OnConfMemberExit " + nConfID + " " + nTime + " " + nUserID);

		for (int i = 0; i < this.mCallbacks.size(); i++) {
			WeakReference<ConfRequestCallback> we = this.mCallbacks.get(i);
			Object obj = we.get();
			if (obj != null) {
				ConfRequestCallback cb = (ConfRequestCallback) obj;
				cb.OnConfMemberExitCallback(nConfID, nTime, nUserID);
			}
		}

	}

	/**
	 * 204 user deleted group 203 current user is kicked by chairman
	 * 
	 * @param nReason
	 */
	private void OnKickConf(int nReason) {
		V2Log.d("-->OnKickConf " + nReason);
		for (int i = 0; i < this.mCallbacks.size(); i++) {
			WeakReference<ConfRequestCallback> we = this.mCallbacks.get(i);
			Object obj = we.get();
			if (obj != null) {
				ConfRequestCallback cb = (ConfRequestCallback) obj;
				cb.OnKickConfCallback(nReason);
			}
		}

	}

	private void OnGrantPermission(long userid, int type, int status) {
		V2Log.d("OnGrantPermission " + userid + " type:" + type + " status:"
				+ status);

		for (int i = 0; i < this.mCallbacks.size(); i++) {
			WeakReference<ConfRequestCallback> we = this.mCallbacks.get(i);
			Object obj = we.get();
			if (obj != null) {
				ConfRequestCallback cb = (ConfRequestCallback) obj;
				cb.OnGrantPermissionCallback(userid, type, status);
			}
		}
	}

	/**
	 * User invite current user to join further conference, this function will
	 * be called
	 * 
	 * @param str
	 *            {@code <conf createuserid='18' id='514000758190' starttime='1400162220' subject=' 就'/> }
	 * @param creatorXml
	 *            {@code <user id='18'/>}
	 */
	private void OnConfNotify(String confXml, String creatorXml) {
		if (confXml == null || confXml.isEmpty()) {
			V2Log.e(" confXml is null ");
			return;
		}
		V2Log.d("OnConfNotify " + confXml + " creatorXml:" + creatorXml);

		V2Conference conf = new V2Conference();
		String confId = XmlAttributeExtractor.extract(creatorXml, "id='", "'");
		if (confId == null || confId.isEmpty()) {
			V2Log.e("confId is null  can not pasrse");
			return;
		}
		String startTime = XmlAttributeExtractor.extract(creatorXml,
				"starttime='", "'");
		String subject = XmlAttributeExtractor.extract(creatorXml, "subject='",
				"'");
		// String creator = XmlAttributeExtractor.extract(creatorXml,
		// "createuserid='", "'");
		conf.cid = Long.parseLong(confId);
		conf.name = subject;
		if (!TextUtils.isEmpty(startTime))
			conf.startTime = new Date(Long.parseLong(startTime) / 1000);
		else {
			V2Log.e("OnConfNotify : get startTime is null...");
		}

		V2User user = new V2User();
		String uid = XmlAttributeExtractor.extract(creatorXml, "id='", "'");
		if (uid == null || uid.isEmpty()) {
			V2Log.e("uid is null  can not pasrse");
			return;
		}
		user.uid = Long.parseLong(uid);

		conf.creator = user;

		for (int i = 0; i < this.mCallbacks.size(); i++) {
			WeakReference<ConfRequestCallback> we = this.mCallbacks.get(i);
			Object obj = we.get();
			if (obj != null) {
				ConfRequestCallback cb = (ConfRequestCallback) obj;
				cb.OnConfNotify(conf, user);
			}
		}

	}

	public native void openVideoMixer(String szMediaID, int nLayout,
			int videoSizeFormat, int nID);

	public native void closeVideoMixer(String szMediaID);

	public native void syncOpenVideoMixer(String szMediaID, int nLayout,
			int videoSizeFormat, int nPos, String szVideoMixerListXml);

	public native void syncCloseVideoMixer(String szMediaID);

	public native void addVideoMixerDevID(String szMediaID, long nDstUserID,
			String szDstDevID, int nPos);

	public native void delVideoMixerDevID(String szMediaID, long nDstUserID,
			String szDstDevID);

	public native void startMixerVideoToSip(long nSipUserID, String szMediaID);

	public native void stopMixerVideoToSip(long nSipUserID, String szMediaID);

	// 灏嗘煇浜鸿鍑轰細璁�
	public native void kickConf(long nUserID);

	// 閭�鍔犲叆浼氳
	public native void inviteJoinConf(long nDstUserID);

	// 鑾峰彇浼氳鐢ㄦ埛鍒楄〃(鏈繘鍏ヤ細璁級
	public native void getConfUserList(long nConfID);

	// 閲嶆柊寮�涓�釜浼氳
	public native void resumeConf(String sXmlConfData, String sXmlInviteUsers);

	// 浼氬満闈欓煶
	public native void muteConf();

	// 淇敼浼氳鎻忚堪
	public native void modifyConfDesc(String sXml);

	// 淇敼浼氳鍚屾
	public native void setConfSync(int syncMode);

	// 鍙栧緱浼氳鍒楄〃
	public native void getConfList();

	// 鐢宠鎺у埗鏉�
	public native void applyForControlPermission(int type);

	// 閲婃斁鎺у埗鏉�
	public native void releaseControlPermission(int type);

	// 涓诲腑鎺堜簣鏌愪汉鏉冮檺
	public native void grantPermission(long userid, int type, int status);

	// 璁剧疆鍏朵粬鐢ㄦ埛瑙嗛閲囬泦鍙傛暟
	public native void setCapParam(String szDevID, int nSizeIndex,
			int nFrameRate, int nBitRate);

	// 鍚屾鎵撳紑鏌愪汉瑙嗛
	public native void syncConfOpenVideo(long nGroupID, long nToUserID,
			String szDeviceID, int nPos);

	// 鍚屾鍙栨秷鏌愪汉瑙嗛
	public native void cancelSyncConfOpenVideo(long nGroupID, long nToUserID,
			String szDeviceID, boolean bCloseVideo);

	// 杞鎵撳紑鏌愪汉瑙嗛
	public native void pollingConfOpenVideo(long nGroupID, long nToUserID,
			String sToDevID, long nFromUserID, String sFromDevID);

	// 缃崲浼氳涓诲腑
	public native void changeConfChair(long nGroupID, long nUserID);

	// public List<Conf> confs = new ArrayList<Conf>();

	public boolean enterConf = false;

	private void OnAddUser(long userid, String xml) {
		Log.e("ImRequest UI", "OnAddUser " + userid + "  " + xml);
	}

	private void OnConfMute() {
		Log.e("ImRequest UI", "OnConfMute ");
	}

	// 浼氳琚垹闄ょ殑鍥炶皟
	private void OnDestroyConf(long nConfID) {
		Log.e("ImRequest UI", "OnDestroyConf " + nConfID);
	}

	// 鎴戞敹鍒板姞鍏ヤ細璁殑閭�鐨勫洖璋� private void OnInviteJoinConf(String confXml, String
	// userXml)
	{
		// System.out.println(confXml+";"+userXml);
		// ByteArrayInputStream in=new
		// ByteArrayInputStream(pUserBaseMsg.getBytes());
		// User user=XmlParserUtils.parserInviteConf(in);
		// Bundle bundle=new Bundle();
		// bundle.putSerializable("OnInviteJoinConf", user);
		// bundle.putString("invite_subject", szSubject);
		// bundle.putString("invite_password", Password);
		// bundle.putLong("invite_conf", nConfID);

		// if(isInConf){
		// MeetingRoom.mMeetingRoom.SendMessage(Constant.INVITE_JOIN_CONF,
		// bundle);
		// }else{
		// //
		// ConfMainActivity.mConfMainActivity.SendMessage(Constant.INVITE_JOIN_CONF,
		// bundle);
		// }

	}

	// strUserList :
	// <xml>
	// <user id='' nickname=''/>
	// <user id='' nickname=''/>
	// </xml>
	private void OnConfUserListReport(long nConfID, String strUserList) {
		Log.e("ImRequest UI", "浼氳鍐呭ソ鍙嬪垪琛�->OnConfUserListReport "
				+ strUserList);
	}

	// 浼氳鏈夌敤鎴风寮�殑鍥炶皟
	private void OnConfMemberLeave(long nConfID, long nUserID) {
		Log.e("ImRequest UI", "浼氳鏈変汉閫�嚭-->OnConfMemberLeave " + nConfID + " "
				+ nUserID);
		Bundle bundle = new Bundle();
		bundle.putLong("OnConfMemberLeave", nUserID);
		// MeetingRoom.mMeetingRoom.SendMessage(Constant.USER_LEAVE_CONF,
		// bundle);
	}

	public void OnConfSyncOpenVideo(String str) {

	}

	public void OnConfSyncCloseVideo(long gid, String str) {

	}

	private void OnConfNotify(long nSrcUserID, String srcNickName,
			long nConfID, String subject, long nTime) {
		// TODO
		Log.e("ImRequest UI", "OnConfNotify " + nSrcUserID + " " + srcNickName
				+ " " + nConfID + " " + subject + " " + nTime);
	}

	private void OnConfNotifyEnd(long nConfID) {

	}

	// 寰楀埌浼氳瀹ゅ垪琛�
	private void OnGetConfList(String szConfListXml) {
		// Logger.i(null,"寰楀埌浼氳鍒楄〃:"+szConfListXml);

	}

	// 浼氳鎻忚堪淇℃伅琚慨鏀圭殑鍥炶皟
	private void OnModifyConfDesc(long nConfID, String szConfDescXml) {
		// TODO
		Log.e("ImRequest UI", "OnModifyConfDesc " + nConfID + " "
				+ szConfDescXml);
	}

	// 閫氱煡涓诲腑鏌愪汉鐢宠鎺у埗鏉冨洖璋�
	private void OnNotifyChair(long userid, int type) {
		Log.e("ImRequest UI", "OnNotifyChair " + userid + " " + type);

	}

	// 鍚屾鎵撳紑鏌愪汉瑙嗛
	private void OnConfSyncOpenVideo(long nDstUserID, String sDstMediaID,
			int nPos) {
		Log.e("ImRequest UI", "OnConfSyncOpenVideo " + sDstMediaID);
	}

	private void OnConfPollingOpenVideo(long nToUserID, String sToDevID,
			long nFromUserID, String sFromDevID) {
		// TODO
		Log.e("ImRequest UI", "OnConfPollingOpenVideo " + nToUserID + " "
				+ sToDevID + " " + nFromUserID + " " + sFromDevID);
	}

	// 鍚屾鍙栨秷鏌愪汉瑙嗛
	private void OnConfSyncCloseVideo(long nDstUserID, String sDstMediaID,
			boolean bClose) {
		Log.e("ImRequest UI", "OnConfSyncCloseVideo " + nDstUserID + " "
				+ sDstMediaID + " " + bClose);
	}

	private void OnSetConfMode(long confid, int mode) {
		Log.e("ImRequest UI", "OnConfSyncCloseVideo " + confid + " " + mode);
	}

	private void OnConfChairChanged(long nConfID, long nChairID) {
		Log.e("ImRequest UI", "OnConfChairChanged " + nConfID + " " + nChairID);
	}

	private void OnSetCanOper(long nConfID, boolean bCanOper) {
		Log.e("ImRequest UI", "OnSetCanOper " + nConfID + " " + bCanOper);
	}

	private void OnSetCanInviteUser(long nConfID, boolean bInviteUser) {
		Log.e("ImRequest UI", "OnSetCanInviteUser " + nConfID + " "
				+ bInviteUser);
	}

	private void OnSyncOpenVideoMixer(String sMediaID, int nLayout,
			int videoSizeFormat, int nPos, String sSyncVideoMixerXml) {
		V2Log.d("OnSyncOpenVideoMixer");
	}

	private void OnSyncCloseVideoMixer(String sMediaID) {
		V2Log.d("OnSyncCloseVideoMixer");
	}

	private void OnAddVideoMixer(String sMediaID, long nDstUserID,
			String sDstDevID, int nPos) {
		V2Log.d("OnAddVideoMixer");
	}

	private void OnDelVideoMixer(String sMediaID, long nDstUserID,
			String sDstDevID) {
		V2Log.d("OnDelVideoMixer");
	}
}