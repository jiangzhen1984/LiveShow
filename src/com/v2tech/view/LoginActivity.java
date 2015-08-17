/**
 * 
 */
package com.v2tech.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.V2.jni.ConfRequest;
import com.v2tech.service.MessageListener;
import com.v2tech.service.UserService;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.JNIResponse.Result;

/**
 * @author jiangzhen
 * 
 */
public class LoginActivity extends Activity {

	private static final int LOG_IN_CALLBACK = 1;

	private UserService us;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		us = new UserService();
		us.login("bbb", "111111", new MessageListener(localHandler,
				LOG_IN_CALLBACK, null));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		us.clearCalledBack();
	}

	private Handler localHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case LOG_IN_CALLBACK: {
				JNIResponse resp = (JNIResponse)msg.obj;
				if (resp.getResult() == Result.SUCCESS) {
					//TODO 
					ConfRequest.getInstance().concern(110001);
					ConfRequest.getInstance().concernCancel(110001);
				}
				break;
			}
			default:
				break;
			}
		}

	};

}
