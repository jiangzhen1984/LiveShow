package com.v2tech.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.Handler;
import android.os.Message;

import com.V2.jni.ImRequest;
import com.V2.jni.ImRequestCallbackAdapter;
import com.V2.jni.V2ClientType;
import com.V2.jni.V2GlobalEnum;
import com.V2.jni.util.V2Log;
import com.v2tech.net.DeamonWorker;
import com.v2tech.net.lv.GetCodeReqPacket;
import com.v2tech.net.lv.LoginReqPacket;
import com.v2tech.net.lv.LoginRespPacket;
import com.v2tech.net.lv.LogoutReqPacket;
import com.v2tech.net.pkt.PacketProxy;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.RequestLogInResponse;
import com.v2tech.service.jni.RequestUserUpdateResponse;
import com.v2tech.util.GlobalConfig;
import com.v2tech.view.Constants;
import com.v2tech.vo.User;

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
		ResponsePacket p = DeamonWorker.getInstance().request(new LoginReqPacket(false, mail, null, passwd, true));
		if (!p.getHeader().isError()) {
			V2Log.e(p.getClass().getName()+"   "+p);
			LoginRespPacket lrp =(LoginRespPacket)p;
			User loginUser = new User(0);
			loginUser.nId = lrp.uid;
			GlobalHolder.getInstance().setCurrentUser(loginUser);
			initTimeoutMessage(JNI_REQUEST_LOG_IN, DEFAULT_TIME_OUT_SECS, caller);
			ImRequest.getInstance().ImLogin(mail, passwd, V2GlobalEnum.USER_STATUS_ONLINE,  V2ClientType.ANDROID, "", false);
		} else {
			if (caller != null) {
				Message m = Message.obtain(caller.getHandler(), caller.what,
						new RequestLogInResponse(null, JNIResponse.Result.FAILED));
				m.sendToTarget();
			}
		}
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
	public void login(String mail, String passwd, boolean isNY, MessageListener caller) {
		
		ResponsePacket p = null;
		if (isNY) {
			p = DeamonWorker.getInstance().request(new LoginReqPacket(true, mail));
		} else {
			p = DeamonWorker.getInstance().request(new LoginReqPacket(false, mail, passwd));
		}
		if (!p.getHeader().isError()) {
			LoginRespPacket lrp = (LoginRespPacket) p;
			User u = new User(-1);
			u.nId = lrp.uid;
			u.isNY = isNY;
			GlobalHolder.getInstance().setCurrentUser(u);
			initTimeoutMessage(JNI_REQUEST_LOG_IN, DEFAULT_TIME_OUT_SECS, caller);
			ImRequest.getInstance().ImLogin(mail, passwd, V2GlobalEnum.USER_STATUS_ONLINE,  V2ClientType.ANDROID, "", true);
		} else {
			if (caller != null) {
				V2Log.e("Loged in failed ");
			}
		}
	}
	
	
	
	public void logout( MessageListener caller) {
		DeamonWorker.getInstance().requestAsync(new PacketProxy(new LogoutReqPacket(), null));
		ImRequest.getInstance().ImLogout();
		if (caller != null && caller.getHandler() != null) {
			Message.obtain(caller.getHandler(), caller.getWhat(), new AsyncResult(caller.getObject(), null)).sendToTarget();
		}
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
			ImRequest.getInstance().ImModifyBaseInfo(user.toXml());
		} else {
//			if (!TextUtils.isEmpty(user.getNickName()))
//				ImRequest.getInstance().im
//						.modifyCommentName(
//								user.getmUserId(),
//								EscapedcharactersProcessing.convert(user
//										.getNickName()));
		}
	}
	
	
	
	public String  sendVaidationCode(String phoneNumber) {
		DeamonWorker.getInstance().request(new GetCodeReqPacket(phoneNumber));
		return null;
	}
	
	public static String getMD5(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();// 加密
		StringBuilder hex = new StringBuilder(m.length * 2);  
	    for (byte b : m) {  
	        if ((b & 0xFF) < 0x10)  
	            hex.append("0");  
	        hex.append(Integer.toHexString(b & 0xFF));  
	    }  
		return hex.toString();
	}

	public void register(String phoneNumber, String code,MessageListener caller) {
		JNIResponse resp = null;
		
		SoapObject request = new SoapObject(Constants.NAME_SPACE,
				"PhoneUserRegister");

		PropertyInfo propInfo = new PropertyInfo();
		propInfo.name = "phonenumber";
		propInfo.type = PropertyInfo.STRING_CLASS;
		propInfo.setValue(phoneNumber);
		
		PropertyInfo codeInfo = new PropertyInfo();
		codeInfo.name = "code";
		codeInfo.type = PropertyInfo.STRING_CLASS;
		try {
			codeInfo.setValue(getMD5(code).toUpperCase());
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			resp = new JNIResponse(JNIResponse.Result.FAILED);
			Message msg = Message.obtain();
			msg.what = caller.what;
			msg.obj = resp;
			caller.refH.get().sendMessage(msg);
			return;
		}

		request.addProperty(propInfo);
		request.addProperty(codeInfo);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);

		try {
			androidHttpTransport.call("urn:PhoneUserRegister", envelope);

			SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope
					.getResponse();
			String retVale = resultsRequestSOAP.getValue().toString();
			
			
			if ("0".equals(retVale)) {
				resp = new JNIResponse(JNIResponse.Result.SUCCESS);
				resp.callerObject = caller.userObj;
			} else {
				resp = new JNIResponse(JNIResponse.Result.FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			resp = new JNIResponse(JNIResponse.Result.FAILED);
		}

		
		
		Message msg = Message.obtain();
		msg.what = caller.what;
		msg.obj = resp;
		caller.refH.get().sendMessage(msg);
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
				long serverTime, String sDBID) {
			// 获取系统时间
			GlobalConfig.LOCAL_TIME = System.currentTimeMillis();
			GlobalConfig.SERVER_TIME = serverTime;
			SimpleDateFormat fromat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String date = fromat.format(new Date(
					GlobalConfig.SERVER_TIME * 1000));
			V2Log.i("get server time ：" + date+"   ===> "+ nUserID);
			RequestLogInResponse.Result res = RequestLogInResponse.Result
					.fromInt(nResult);
			
			User u  = GlobalHolder.getInstance().getCurrentUser();
			if (u != null) {
				u.setmUserId(nUserID);
			} else {
				u = new User(nUserID);
			}
			Message m = Message.obtain(handler, JNI_REQUEST_LOG_IN,
					new RequestLogInResponse(u, res));
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
