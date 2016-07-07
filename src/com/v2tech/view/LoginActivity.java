/**
 * 
 */
package com.v2tech.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.V2.jni.util.V2Log;
import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.LoginPresenter;
import com.v2tech.presenter.LoginPresenter.LoginPresenterUI;
import com.v2tech.v2liveshow.R;

/**
 * @author jiangzhen
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener, LoginPresenterUI {



	private EditText mUserNameET;
	private EditText mUserCodeET;
	private View mVerificationCodeBtn;
	private TextView mStartBtn;
	private View returnButton;
	private ProgressDialog proDialog;
	private TextView notificationView;
	
	
	private LoginPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login_reg_activity);
		
		
		
		mUserNameET = (EditText)findViewById(R.id.edt_user_name);
		mUserCodeET = (EditText)findViewById(R.id.edt_user_code);
		mVerificationCodeBtn = findViewById(R.id.get_verification_code_button);
		mStartBtn = (TextView)findViewById(R.id.start_button);
		returnButton = findViewById(R.id.title_bar_left_btn);
		notificationView = (TextView)findViewById(R.id.login_reg_activity_notification_view);
		
		mVerificationCodeBtn.setOnClickListener(this);
		mStartBtn.setOnClickListener(this);
		returnButton.setOnClickListener(this);
		
		mUserNameET.addTextChangedListener(textWatcher);
		mUserCodeET.addTextChangedListener(textWatcher);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.start_button:
			presenter.startButtonClicked();
			break;
		case R.id.get_verification_code_button:
			presenter.verificationCodeButtonClicked();
			break;
		case R.id.title_bar_left_btn:
			presenter.returnButtonClicked();
			break;
		}

	}



	private TextWatcher textWatcher = new TextWatcher() {

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
			if (mUserNameET.getEditableText() == s) {
				presenter.userNameTextChanged();
			} else {
				presenter.codeTextChanged();
			}
		}
	};
	
	
	
	
	@Override
	public BasePresenter getPresenter() {
		if (presenter == null) {
			presenter = new LoginPresenter(this);
		}
		return presenter;
	}
	
  ///////////////////////////////////////presenter//////////////
	
	
	@Override
	public String getUserNameText() {
		return mUserNameET.getEditableText().toString().replaceAll(" ", "");
	}

	@Override
	public String getCodeText() {
		return mUserCodeET.getEditableText().toString();
	}



	@Override
	public void updateStartButton(boolean enable) {
		if (enable) {
			mStartBtn.setBackgroundResource(R.drawable.login_start_button_red_bg);
			mStartBtn.setTextColor(this.getResources().getColor(R.color.login_start_button_text_enabled_color));
		} else {
			mStartBtn.setBackgroundResource(R.drawable.login_start_button_while_bg);
			mStartBtn.setTextColor(this.getResources().getColor(R.color.login_start_button_text_disable_color));
		}
		mStartBtn.setEnabled(enable);
		 
	}
	
	@Override
	public void appendBlankSpace() {
		Editable et = mUserNameET.getEditableText();
		mUserNameET.removeTextChangedListener(textWatcher);
		et.insert(et.length() - 1, " ");
		mUserNameET.addTextChangedListener(textWatcher);
		
	}
	
	

	

	@Override
	public void doLogonFailed() {
		proDialog.dismiss();
	}

	@Override
	public void showLogingInProgress() {
		V2Log.e("=================progress");
		if (proDialog == null) {
			proDialog = new ProgressDialog(this);
			proDialog.setCancelable(false);
		}
		proDialog.show();
	}
	

	@Override
	public void doLogedIn() {
		finish();
	}
	


	@Override
	public void doReturned() {
		finish();
	}
	
	
	@Override
	public void setPhoneNumberError() {
		mUserNameET.setError(getResources().getText(R.string.login_error_incorrect_phone_number));
	}
	
	

	@Override
	public void showKeyboard() {
		// TODO Auto-generated method stub
		
	}

	
	public void showIncorrectMsgIncorrectUsername() {
		notificationView.setText(R.string.login_error_text_incorrect_username_pwd);
	}

	
	public void showNotificaitonView(final boolean flag) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				notificationView.setVisibility(flag? View.VISIBLE:View.INVISIBLE);
			}
			
		});
		
	}
///////////////////////////////////////presenter//////////////
	


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
		mUserNameET.removeTextChangedListener(textWatcher);
		mUserCodeET.removeTextChangedListener(textWatcher);
		presenter.onUIDestroyed();
	}


	

}
