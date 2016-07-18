package com.v2tech.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;

import com.V2.jni.ConfRequest;
import com.V2.jni.ConfRequestCallbackAdapter;
import com.V2.jni.GroupRequest;
import com.V2.jni.GroupRequestCallbackAdapter;
import com.V2.jni.ImRequest;
import com.V2.jni.ImRequestCallbackAdapter;
import com.V2.jni.V2GlobalEnum;
import com.V2.jni.VideoRequest;
import com.V2.jni.VideoRequestCallbackAdapter;
import com.V2.jni.callback.ImRequestCallback;
import com.V2.jni.ind.GroupJoinErrorJNIObject;
import com.V2.jni.ind.V2Group;
import com.V2.jni.ind.V2User;
import com.V2.jni.util.V2Log;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.vo.NetworkStateCode;
import com.v2tech.vo.User;
import com.v2tech.vo.UserDeviceConfig;
import com.v2tech.vo.group.Group;
import com.v2tech.vo.msg.VMessage;

/**
 * This service is used to wrap JNI call.<br>
 * JNI calls are asynchronous, we don't expect activity involve JNI.<br>
 * 
 * @author 28851274
 * 
 */
public class JNIService extends Service 
		 {
	private static final String TAG = "JNIService";

	public static final String JNI_BROADCAST_CATEGROY = "com.v2tech.jni.broadcast";
	public static final String JNI_ACTIVITY_CATEGROY = "com.v2tech";
	public static final String JNI_BROADCAST_CONNECT_STATE_NOTIFICATION = "com.v2tech.jni.broadcast.connect_state_notification";
	public static final String JNI_BROADCAST_USER_STATUS_NOTIFICATION = "com.v2tech.jni.broadcast.user_stauts_notification";

	/**
	 * Notify user avatar changed, notice please do not listen this broadcast if
	 * you are UI. Use
	 * {@link BitmapManager#registerBitmapChangedListener(com.v2tech.service.BitmapManager.BitmapChangedListener)}
	 * to listener bitmap change if you are UI.<br>
	 * key avatar : #UserAvatarObject
	 */
	public static final String JNI_BROADCAST_USER_AVATAR_CHANGED_NOTIFICATION = "com.v2tech.jni.broadcast.user_avatar_notification";
	public static final String JNI_BROADCAST_USER_UPDATE_NAME_OR_SIGNATURE = "com.v2tech.jni.broadcast.user_update_sigature";
	public static final String JNI_BROADCAST_GROUP_NOTIFICATION = "com.v2tech.jni.broadcast.group_geted";
	public static final String JNI_BROADCAST_GROUP_USER_UPDATED_NOTIFICATION = "com.v2tech.jni.broadcast.group_user_updated";
	public static final String JNI_BROADCAST_GROUP_UPDATED = "com.v2tech.jni.broadcast.group_updated";
	public static final String JNI_BROADCAST_GROUP_JOIN_FAILED = "com.v2tech.jni.broadcast.group_join_failed";
	public static final String JNI_BROADCAST_NEW_MESSAGE = "com.v2tech.jni.broadcast.new.message";
	public static final String JNI_BROADCAST_MESSAGE_SENT_FAILED = "com.v2tech.jni.broadcast.message_sent_failed";
	public static final String JNI_BROADCAST_NEW_CONF_MESSAGE = "com.v2tech.jni.broadcast.new.conf.message";
	public static final String JNI_BROADCAST_CONFERENCE_INVATITION = "com.v2tech.jni.broadcast.conference_invatition_new";
	public static final String JNI_BROADCAST_CONFERENCE_REMOVED = "com.v2tech.jni.broadcast.conference_removed";
	public static final String JNI_BROADCAST_GROUP_USER_REMOVED = "com.v2tech.jni.broadcast.group_user_removed";
	public static final String JNI_BROADCAST_GROUP_USER_ADDED = "com.v2tech.jni.broadcast.group_user_added";
	public static final String JNI_BROADCAST_VIDEO_CALL_CLOSED = "com.v2tech.jni.broadcast.video_call_closed";
	public static final String JNI_BROADCAST_FRIEND_AUTHENTICATION = "com.v2tech.jni.broadcast.friend_authentication";
	public static final String JNI_BROADCAST_NEW_QUALIFICATION_MESSAGE = "com.v2tech.jni.broadcast.new.qualification_message";
	public static final String BROADCAST_CROWD_NEW_UPLOAD_FILE_NOTIFICATION = "com.v2tech.jni.broadcast.new.upload_crowd_file_message";
	/**
	 * Current user kicked by crowd master key crowd : crowdId
	 */
	public static final String JNI_BROADCAST_KICED_CROWD = "com.v2tech.jni.broadcast.kick_crowd";

	/**
	 * Crowd invitation with key crowd
	 */
	public static final String JNI_BROADCAST_CROWD_INVATITION = "com.v2tech.jni.broadcast.crowd_invatition";

	private boolean isDebug = true;

	private final LocalBinder mBinder = new LocalBinder();

	private Integer mBinderRef = 0;

	/**
	 * @see V2GlobalEnum
	 */
	private List<Integer> delayBroadcast = new ArrayList<Integer>();
	private List<GroupUserInfoOrig> delayUserBroadcast = new ArrayList<GroupUserInfoOrig>();
	private boolean noNeedBroadcast;
	private boolean isAcceptApply;

	private JNICallbackHandler mCallbackHandler;

	// ////////////////////////////////////////
	// JNI call back definitions
	private ImRequestCallback mImCB;

	private GroupRequestCB mGRCB;

	private VideoRequestCB mVRCB;

	private ConfRequestCB mCRCB;


	// ////////////////////////////////////////

	private Context mContext;

	private List<VMessage> cacheImageMeta = new ArrayList<VMessage>();
	private List<VMessage> cacheAudioMeta = new ArrayList<VMessage>();

	// ////////////////////////////////////////

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		((MainApplication)this.getApplication()).onMainCreate();
		HandlerThread callback = new HandlerThread("JNI-Callbck");
		callback.start();
		synchronized (callback) {
			while (!callback.isAlive()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		mCallbackHandler = new JNICallbackHandler(callback.getLooper());

		mImCB = new ImRequestCB(mCallbackHandler);
		ImRequest.getInstance().addCallback(mImCB);

		mGRCB = new GroupRequestCB(mCallbackHandler);
		GroupRequest.getInstance().addCallback(
				mGRCB);


		mVRCB = new VideoRequestCB(mCallbackHandler);
		VideoRequest.getInstance().addCallback(
				mVRCB);


		mCRCB = new ConfRequestCB(mCallbackHandler);
		ConfRequest.getInstance().addCallback(mCRCB);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		synchronized (mBinderRef) {
			mBinderRef++;
		}
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		synchronized (mBinderRef) {
			mBinderRef--;
		}
		// if mBinderRef equals 0 means no activity
		if (mBinderRef == 0) {
		}
		return super.onUnbind(intent);
	}

	/**
	 * Used to local binder
	 * 
	 * @author 28851274
	 * 
	 */
	public class LocalBinder extends Binder {
		public JNIService getService() {
			return JNIService.this;
		}
	}

	class GroupUserInfoOrig {
		int gType;
		long gId;
		String xml;

		public GroupUserInfoOrig(int gType, long gId, String xml) {
			super();
			this.gType = gType;
			this.gId = gId;
			this.xml = xml;
		}

	}

	private void broadcastNetworkState(NetworkStateCode code) {

		Intent i = new Intent();
		i.setAction(JNI_BROADCAST_CONNECT_STATE_NOTIFICATION);
		i.addCategory(JNI_BROADCAST_CATEGROY);
		i.putExtra("state", (Parcelable) code);
		sendBroadcast(i);
	}

	private User convertUser(V2User user) {
		if (user == null) {
			return null;
		}

		User u = new User(user.uid, user.name);
		u.setSignature(user.mSignature);
		u.setJob(user.mJob);
		u.setTelephone(user.mTelephone);
		u.setMobile(user.mMobile);
		u.setAddress(user.mAddress);
		u.setSex(user.mSex);
		u.setEmail(user.mEmail);
		u.setFax(user.mFax);
		if (user.mCommentname != null && !user.mCommentname.isEmpty()) {
			u.setNickName(user.mCommentname);
		}
		u.setmCommentname(user.mCommentname);
		u.setAccount(user.mAccount);
		u.setAuthtype(user.mAuthtype);
		u.setBirthday(user.mBirthday);
		return u;
	}

	static long lastNotificatorTime = 0;

	// //////////////////////////////////////////////////////////
	// Internal message definition //
	// //////////////////////////////////////////////////////////

	private static final int JNI_CONNECT_RESPONSE = 23;
	private static final int JNI_UPDATE_USER_INFO = 24;
	private static final int JNI_LOG_OUT = 26;
	private static final int JNI_GROUP_NOTIFY = 35;
	private static final int JNI_GROUP_USER_INFO_NOTIFICATION = 60;
	private static final int JNI_CONFERENCE_INVITATION = 61;
	private static final int JNI_RECEIVED_MESSAGE = 91;
	private static final int JNI_RECEIVED_VIDEO_INVITION = 92;

	class JNICallbackHandler extends Handler {

		public JNICallbackHandler(Looper looper) {
			super(looper);
		}

		@Override
		public synchronized void handleMessage(Message msg) {
			switch (msg.what) {

			case JNI_CONNECT_RESPONSE:
				broadcastNetworkState(NetworkStateCode.fromInt(msg.arg1));
				break;
			case JNI_UPDATE_USER_INFO:
				User u = (User) msg.obj;
				GlobalHolder.getInstance().putUser(u.getmUserId(), u);

				Intent sigatureIntent = new Intent();
				sigatureIntent
						.setAction(JNI_BROADCAST_USER_UPDATE_NAME_OR_SIGNATURE);
				sigatureIntent.addCategory(JNI_BROADCAST_CATEGROY);
				sigatureIntent.putExtra("uid", u.getmUserId());
				sendBroadcast(sigatureIntent);
				break;
			case JNI_LOG_OUT:
				break;
			case JNI_GROUP_NOTIFY:
				List<V2Group> gl = (List<V2Group>) msg.obj;
				break;

			case JNI_GROUP_USER_INFO_NOTIFICATION:
				GroupUserInfoOrig go = (GroupUserInfoOrig) msg.obj;
				if (go != null && go.xml != null) {} else {
					V2Log.e("Invalid group user data");
				}
				break;
			case JNI_CONFERENCE_INVITATION:
				Group g = (Group) msg.obj;
				break;
			case JNI_RECEIVED_MESSAGE:
				VMessage vm = (VMessage) msg.obj;
				break;
			case JNI_RECEIVED_VIDEO_INVITION:
				break;

			}

		}


	}


	class ImRequestCB extends ImRequestCallbackAdapter {

		private JNICallbackHandler mCallbackHandler;

		public ImRequestCB(JNICallbackHandler mCallbackHandler) {
			this.mCallbackHandler = mCallbackHandler;
		}

		
		

		@Override
		public void OnLoginCallback(long nUserID, int nStatus, int nResult,
				long serverTime, String sDBID) {
			if (JNIResponse.Result.fromInt(nResult) == JNIResponse.Result.SUCCESS) {
				// Just request current logged in user information
				ImRequest.getInstance().ImGetUserBaseInfo(nUserID);
			}
		}

		@Override
		public void OnLogoutCallback(int nUserID) {
			Message.obtain(mCallbackHandler, JNI_LOG_OUT).sendToTarget();
		}

		@Override
		public void OnConnectResponseCallback(int nResult) {
			Message.obtain(mCallbackHandler, JNI_CONNECT_RESPONSE, nResult, 0)
					.sendToTarget();
		}



		@Override
		public void OnUpdateBaseInfoCallback(long nUserID, String updatexml) {
			// TODO Auto-generated method stub
			super.OnUpdateBaseInfoCallback(nUserID, updatexml);
		}

		
		

	}

	class GroupRequestCB extends GroupRequestCallbackAdapter {
		private static final String TAG = "GroupRequestCB";
		private JNICallbackHandler mCallbackHandler;

		public GroupRequestCB(JNICallbackHandler mCallbackHandler) {
			this.mCallbackHandler = mCallbackHandler;
		}

		

		@Override
		public void OnGetGroupInfo(int groupType, String sXml) {
//			Message.obtain(mCallbackHandler, JNI_GROUP_NOTIFY, groupType, 0,
//					list).sendToTarget();
		}

		@Override
		public void OnGetGroupUserInfoCallback(int groupType, long nGroupID,
				String sXml) {
			if (isDebug) {
				V2Log.d("group type:" + groupType + " " + nGroupID + " " + sXml);
			}
			Message.obtain(mCallbackHandler, JNI_GROUP_USER_INFO_NOTIFICATION,
					new GroupUserInfoOrig(groupType, nGroupID, sXml))
					.sendToTarget();
		}
		
		

		@Override
		public void OnModifyGroupInfo(int groupType, long nGroupID, String sXml) {
			// TODO Auto-generated method stub
			super.OnModifyGroupInfo(groupType, nGroupID, sXml);
			
//			if (group == null) {
//				V2Log.e(TAG,
//						"OnModifyGroupInfoCallback --> update Group Infos failed...get V2Group is null!");
//				return;
//			}
//
//			if (group.type == GroupType.CONFERENCE.intValue()) {
//
//			} else if (group.type == GroupType.CHATING.intValue()) {
//				CrowdGroup cg = (CrowdGroup) GlobalHolder.getInstance()
//						.getGroupById(group.id);
//				cg.setAnnouncement(group.announce);
//				cg.setBrief(group.brief);
//				cg.setAuthType(CrowdGroup.AuthType.fromInt(group.authType));
//				cg.setName(group.name);
//				// update crowd group infos in globle collections
//				// GlobalHolder.getInstance().updateCrwodGroupByID(V2GlobalEnum.GROUP_TYPE_CROWD
//				// , group);
//			}
//
//			// Send broadcast
//			Intent i = new Intent(JNI_BROADCAST_GROUP_UPDATED);
//			i.addCategory(JNI_BROADCAST_CATEGROY);
//			i.putExtra("gid", group.id);
//			mContext.sendBroadcast(i);
		}




	
		@Override
		public void OnJoinGroupError(int eGroupType, long nGroupID, int nErrorNo) {
			// Send broadcast
			Intent i = new Intent(JNI_BROADCAST_GROUP_JOIN_FAILED);
			i.addCategory(JNI_BROADCAST_CATEGROY);
			i.putExtra("joinCode", new GroupJoinErrorJNIObject(eGroupType,
					nGroupID, nErrorNo));
			mContext.sendBroadcast(i);
		}



		
	}


	class ConfRequestCB extends ConfRequestCallbackAdapter {

		public ConfRequestCB(JNICallbackHandler mCallbackHandler) {
		}

//		@Override
//		public void OnConfNotify(V2Conference v2conf, V2User user) {
//			if (v2conf == null || user == null) {
//				V2Log.e(" v2conf is " + v2conf + " or user is null" + user);
//				return;
//			}
//
//			User owner = GlobalHolder.getInstance().getUser(user.uid);
//			Group g = new ConferenceGroup(v2conf.cid, v2conf.name, owner,
//					v2conf.startTime, owner);
//			User u = GlobalHolder.getInstance().getUser(user.uid);
//			g.setOwnerUser(u);
//			GlobalHolder.getInstance().addGroupToList(
//					Group.GroupType.CONFERENCE.intValue(), g);
//
//			Intent i = new Intent();
//			i.setAction(JNIService.JNI_BROADCAST_CONFERENCE_INVATITION);
//			i.addCategory(JNIService.JNI_BROADCAST_CATEGROY);
//			i.putExtra("gid", g.getmGId());
//			sendBroadcast(i);
//		}

	}
	
	
	class VideoRequestCB extends VideoRequestCallbackAdapter {

		private JNICallbackHandler mCallbackHandler;

		public VideoRequestCB(JNICallbackHandler mCallbackHandler) {
			this.mCallbackHandler = mCallbackHandler;
		}

	
		@Override
		public void OnRemoteUserVideoDevice(long uid, String szXmlData) {
			if (szXmlData == null) {
				V2Log.e(" No avaiable user device configuration");
				return;
			}
			List<UserDeviceConfig> ll = UserDeviceConfig.parseFromXml(uid,
					szXmlData);
		}
	}


}
