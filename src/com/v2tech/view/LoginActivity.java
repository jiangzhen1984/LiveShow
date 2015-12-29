/**
 * 
 */
package com.v2tech.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.v2tech.service.GlobalHolder;
import com.v2tech.service.MessageListener;
import com.v2tech.service.UserService;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.RequestLogInResponse;
import com.v2tech.util.Md5Util;
import com.v2tech.util.SPUtil;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.User;

/**
 * @author jiangzhen
 * 
 */
public class LoginActivity extends Activity implements OnClickListener {

	private static final int LOG_IN_CALLBACK = 1;

	private static final int REG_CALLBACK = 2;

	private UserService us;

	HandlerThread ht;
	Handler requestHandler;

	private EditText mVerificationCodeET;
	private EditText mCellphoneNumberET;
	private Button mVerificationCodeBtn;
	private Button mCellphoneNumberBtn;
	
	private Intent mPendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login_reg_activity);
		us = new UserService();
		ht = new HandlerThread("LoginActivity");
		ht.start();

		mVerificationCodeET = (EditText) findViewById(R.id.login_reg_verification_code);
		mCellphoneNumberET = (EditText) findViewById(R.id.login_reg_cellphone_number);

		mVerificationCodeBtn = (Button) findViewById(R.id.login_reg_verification_code_btn);
		mCellphoneNumberBtn = (Button) findViewById(R.id.login_reg_btn);
		mVerificationCodeBtn.setOnClickListener(this);
		mCellphoneNumberBtn.setOnClickListener(this);

		findViewById(R.id.return_button).setOnClickListener(this);
		mVerificationCodeET.addTextChangedListener(codeTextWatcher);
		mCellphoneNumberET.addTextChangedListener(cellphonTextWatcher);

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		requestHandler = new Handler(ht.getLooper());
		this.overridePendingTransition(R.animator.right_to_left_in, 0);
		
		if (getIntent() != null && getIntent().getExtras() != null) {
			mPendingIntent = (Intent)getIntent().getExtras().get("pendingintent");
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.login_reg_verification_code_btn:
			if (TextUtils.isEmpty(mCellphoneNumberET.getText())) {
				mCellphoneNumberET
						.setError(getText(R.string.error_cellphone_required));
				return;
			}
			// TODO check phone number is correct
			requestHandler.post(new LoginRegRunnable(mCellphoneNumberET
					.getText().toString()));
			break;
		case R.id.login_reg_btn:
			if (TextUtils.isEmpty(mCellphoneNumberET.getText())) {
				mCellphoneNumberET
						.setError(getText(R.string.error_cellphone_required));
				return;
			}
			if (TextUtils.isEmpty(mVerificationCodeET.getText())) {
				mVerificationCodeET
						.setError(getText(R.string.error_verification_code_required));
				return;
			}
			requestHandler.post(new LoginRegRunnable(mCellphoneNumberET
					.getText().toString(), mVerificationCodeET.getText()
					.toString()));
			break;
		case R.id.return_button:
			finish();
			break;
		}

	}

	private TextWatcher codeTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!TextUtils.isEmpty(s.toString()) && !TextUtils.isEmpty(mCellphoneNumberET.getText())) {
				mCellphoneNumberBtn.setBackgroundResource(R.drawable.login_reg_button_selector);
			} else {
				mCellphoneNumberBtn.setBackgroundResource(R.drawable.btn_gray_bg);
				
			}

		}
	};

	private TextWatcher cellphonTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (TextUtils.isEmpty(s.toString())) {
				mVerificationCodeBtn.setBackgroundResource(R.drawable.btn_gray_bg);
			} else {
				mVerificationCodeBtn.setBackgroundResource(R.drawable.login_reg_button_selector);
			}
		}
	};
	
	
	

	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(Activity.RESULT_CANCELED);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.animator.left_to_right_out);
	}

	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		us.clearCalledBack();
		ht.quit();
		
		mVerificationCodeET.removeTextChangedListener(codeTextWatcher);
		mCellphoneNumberET.removeTextChangedListener(cellphonTextWatcher);
	}

	private void handleLoginCallback(JNIResponse resp) {
		if (resp.getResult() == JNIResponse.Result.SUCCESS) {
			RequestLogInResponse rir = (RequestLogInResponse) resp;
			GlobalHolder.getInstance().setCurrentUser(rir.getUser());

			SPUtil.putConfigStrValue(getApplicationContext(), "cellphone",
					mCellphoneNumberET.getText().toString());
			SPUtil.putConfigStrValue(getApplicationContext(), "code",
					mVerificationCodeET.getText().toString());
			if (mPendingIntent != null) {
				startActivity(mPendingIntent);
			} else {
				setResult(Activity.RESULT_OK);
			}
			finish();
		} else {
			Toast.makeText(getApplicationContext(), R.string.error_login_in,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void handleRegCallback(JNIResponse resp) {
		if (resp.getResult() != JNIResponse.Result.SUCCESS) {
			String[] data = (String[])resp.callerObject;
			if (data != null) {
				us.login(data[0], data[1], null);
				requestHandler.post(new LoginRegRunnable(data[0], data[1]));
			}
			Toast.makeText(getApplicationContext(),
					R.string.error_get_verification_code, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.notification_get_verification_code,
					Toast.LENGTH_SHORT).show();
		}
	}

	private Handler localHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case LOG_IN_CALLBACK:
				handleLoginCallback((JNIResponse) msg.obj);
				break;
			case REG_CALLBACK:
				handleRegCallback((JNIResponse) msg.obj);
				break;
			default:
				break;
			}
		}

	};

	class LoginRegRunnable implements Runnable {

		private String number;
		private String code;
		private boolean isReg;

		public LoginRegRunnable(String number, String code) {
			this.number = number;
			this.code = code;
			isReg = false;
		}

		public LoginRegRunnable(String number) {
			this.number = number;
			isReg = true;
		}

		@Override
		public void run() {
			if (isReg) {
				us.register(number, code,new MessageListener(localHandler,
						REG_CALLBACK, new String[]{number, code}));
			} else {
				us.logout(null);
				us.login(number,code, new MessageListener(localHandler,
						LOG_IN_CALLBACK, null));
//				Message m = Message.obtain(localHandler, LOG_IN_CALLBACK);
//				User u = new User(123, number);
//				u.setMobile(number);
//				m.obj = new RequestLogInResponse(u, JNIResponse.Result.SUCCESS);
//				localHandler.sendMessage(m);
			}

		}
	}

}
