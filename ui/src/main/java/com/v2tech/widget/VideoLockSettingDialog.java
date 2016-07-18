package com.v2tech.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.v2tech.R;

public class VideoLockSettingDialog extends Dialog {
	
	private EditText pwd1;
	private EditText pwd2;
	private EditText pwd3;
	private EditText pwd4;
	
	private VideoLockSettingDiagLockListener listener;

	public VideoLockSettingDialog(Context context, VideoLockSettingDiagLockListener listener) {
		this(context, listener, null, null, null, null);
	}


	
	public VideoLockSettingDialog(Context context, VideoLockSettingDiagLockListener listener, String etVal1, String etVal2, String etVal3, String etVal4) {
		super(context);
		this.listener = listener;
		LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.video_lock_setting_dialog_layout, (ViewGroup)null);
        setContentView(view);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        view.findViewById(R.id.video_lock_close_btn).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				VideoLockSettingDialog.this.dismiss();
			}
        	
        });
        
        pwd1 = ((EditText)view.findViewById(R.id.video_lock_pwd_1_et));
        pwd1.addTextChangedListener(pwdWatcher1);
        pwd2 =((EditText)view.findViewById(R.id.video_lock_pwd_2_et));
        pwd2.addTextChangedListener(pwdWatcher2);
        pwd3 =((EditText)view.findViewById(R.id.video_lock_pwd_3_et));
        pwd3.addTextChangedListener(pwdWatcher3);
        pwd4 =((EditText)view.findViewById(R.id.video_lock_pwd_4_et));
        pwd4.addTextChangedListener(pwdWatcher4);
        
        if (etVal1 != null) {
        	pwd1.setText(etVal1);
        }
        
        if (etVal2 != null) {
        	pwd2.setText(etVal1);
        }
        
        if (etVal3 != null) {
        	pwd3.setText(etVal1);
        }
        
        if (etVal4 != null) {
        	pwd4.setText(etVal1);
        }
       
	}
	
	
	
	
	@Override
	public void show() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		super.show();
	}




	private TextWatcher pwdWatcher1 = new TextWatcher() {

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
			if (checkAllPwdFilled()) {
				doFinish();
			} else {
				pwd2.requestFocus();
			}
		}
	};
	
	
	
	private TextWatcher pwdWatcher2 = new TextWatcher() {

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
			if (checkAllPwdFilled()) {
				doFinish();
			} else {
				pwd3.requestFocus();
			}
		}
		
	};
	
	private TextWatcher pwdWatcher3 = new TextWatcher() {

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
			if (checkAllPwdFilled()) {
				doFinish();
			} else {
				pwd4.requestFocus();
			}
		}
		
	};
	
	
	private TextWatcher pwdWatcher4 = new TextWatcher() {

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
			if (checkAllPwdFilled()) {
				doFinish();
			} else {
				if (pwd1.getEditableText().length() < 1) {
					pwd1.requestFocus();
				} else if (pwd1.getEditableText().length() < 1) {
					pwd2.requestFocus();
				} else if (pwd3.getEditableText().length() < 1) {
					pwd3.requestFocus();
				}
			}
		}
		
	};
	
	
	private boolean checkAllPwdFilled() {
		return pwd1.getEditableText().length() == 1
				&& pwd2.getEditableText().length() == 1
				&& pwd3.getEditableText().length() == 1
				&& pwd4.getEditableText().length() == 1;
	}
	
	
	private void doFinish() {
		listener.onVideoLockFinish(pwd1, pwd2, pwd3, pwd4);
		pwd1.getText().clear();
		pwd2.getText().clear();
		pwd3.getText().clear();
		pwd4.getText().clear();
	}
	
	
	public interface VideoLockSettingDiagLockListener {
		public void onVideoLockFinish(EditText et1, EditText et2, EditText et3, EditText et4);
	}
	
}
