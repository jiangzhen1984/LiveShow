package com.v2tech.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.v2tech.v2liveshow.R;

public class VideoUnlockSettingDialog extends Dialog {
	
	
	private VideoUnlockSettingDiagLockListener listener;

	public VideoUnlockSettingDialog(Context context, VideoUnlockSettingDiagLockListener listener) {
		this(context, listener, null, null, null, null);
	}


	
	public VideoUnlockSettingDialog(Context context, VideoUnlockSettingDiagLockListener ls, String etVal1, String etVal2, String etVal3, String etVal4) {
		super(context);
		this.listener = ls;
		LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.video_unlock_setting_dialog_layout, (ViewGroup)null);
        setContentView(view);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        view.findViewById(R.id.video_unlock_close_btn).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				VideoUnlockSettingDialog.this.dismiss();
			}
        	
        });
        
        view.findViewById(R.id.video_unlock_setting_unlock_btn).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onVideoUnLockFinish(v);
				}
			}
        	
        });
        

       
	}
	
	
	
	
	@Override
	public void show() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		super.show();
	}



	
	
	public interface VideoUnlockSettingDiagLockListener {
		public void onVideoUnLockFinish(View v);
	}
	
}
