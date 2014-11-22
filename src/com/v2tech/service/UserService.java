package com.v2tech.service;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.V2.jni.ImRequest;
import com.V2.jni.ImRequestCallbackAdapter;
import com.V2.jni.V2ClientType;
import com.V2.jni.V2GlobalEnum;
import com.V2.jni.util.EscapedcharactersProcessing;
import com.V2.jni.util.V2Log;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.RequestLogInResponse;
import com.v2tech.service.jni.RequestUserUpdateResponse;
import com.v2tech.util.GlobalConfig;
import com.v2tech.vo.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserService extends AbstractHandler {

	// 此处的消息类型只与AbstractHandler的REQUEST_TIME_OUT消息并列。
	// 与参数caller中的消息类型what完全没有关系，上层传什么消息what回调就是什么消息what
	private static final int JNI_REQUEST_LOG_IN = 1;
	private static final int JNI_REQUEST_UPDAE_USER = 2;

	private ImRequestCB imCB = null;

	public UserService() {
		super();
		imCB = new ImRequestCB(this);
		ImRequest.getInstance().addCallback(imCB);
	}

	/**
	 * Asynchronous login function. After login, will call post message to your
	 * handler
	 * 
	 * 
	 * @param mail
	 *            user mail
	 * @param passwd
	 *            password
	 * @param caller
	 *            callback message Message.obj is {@link MessageListener}
	 */
	public void login(String mail, String passwd, MessageListener caller) {
		initTimeoutMessage(JNI_REQUEST_LOG_IN, DEFAULT_TIME_OUT_SECS, caller);
		ImRequest.getInstance().login(mail, passwd,
				V2GlobalEnum.USER_STATUS_ONLINE, V2ClientType.ANDROID, false);
	}

	/**
	 * Update user information. If updated user is logged user, can update all
	 * information.otherwise only can update nick name.
	 * 
	 * @param user
	 * @param caller
	 */
	public void updateUser(User user, MessageListener caller) {
		if (user == null) {
			if (caller != null && caller.getHandler() != null) {
				sendResult(caller,
						new RequestLogInResponse(null,
								RequestLogInResponse.Result.INCORRECT_PAR,
								caller.getObject()));
			}
			return;
		}
		initTimeoutMessage(JNI_REQUEST_UPDAE_USER, DEFAULT_TIME_OUT_SECS,
				caller);
		if (user.getmUserId() == GlobalHolder.getInstance().getCurrentUserId()) {
			ImRequest.getInstance().modifyBaseInfo(user.toXml());
		} else {
			if (!TextUtils.isEmpty(user.getNickName()))
				ImRequest.getInstance()
						.modifyCommentName(
								user.getmUserId(),
								EscapedcharactersProcessing.convert(user
										.getNickName()));
		}
	}

	@Override
	public void clearCalledBack() {
		ImRequest.getInstance().removeCallback(imCB);
	}

	class ImRequestCB extends ImRequestCallbackAdapter {

		private Handler handler;

		public ImRequestCB(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void OnLoginCallback(long nUserID, int nStatus, int nResult,
				long serverTime) {
			// 获取系统时间
			GlobalConfig.LOCAL_TIME = System.currentTimeMillis();
			GlobalConfig.SERVER_TIME = serverTime;
            SimpleDateFormat fromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = fromat.format(new Date(GlobalConfig.SERVER_TIME * 1000));
			V2Log.d("get server time ：" + date);
			RequestLogInResponse.Result res = RequestLogInResponse.Result
					.fromInt(nResult);
			Message m = Message.obtain(handler, JNI_REQUEST_LOG_IN,
					new RequestLogInResponse(new User(nUserID), res));
			handler.dispatchMessage(m);
		}

		@Override
		public void OnConnectResponseCallback(int nResult) {
			RequestLogInResponse.Result res = RequestLogInResponse.Result
					.fromInt(nResult);
			if (res != RequestLogInResponse.Result.SUCCESS) {
				Message m = Message.obtain(handler, JNI_REQUEST_LOG_IN,
						new RequestLogInResponse(null, res));
				handler.dispatchMessage(m);
			}
		}

		@Override
		public void OnModifyCommentNameCallback(long nUserId,
				String sCommmentName) {
			User u = GlobalHolder.getInstance().getUser(nUserId);
			Message m = Message
					.obtain(handler, JNI_REQUEST_UPDAE_USER,
							new RequestUserUpdateResponse(u,
									JNIResponse.Result.SUCCESS));
			handler.dispatchMessage(m);
		}

	}
}
