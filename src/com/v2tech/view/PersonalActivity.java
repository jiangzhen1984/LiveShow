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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.v2tech.service.GlobalHolder;
import com.v2tech.service.UserService;
import com.v2tech.v2liveshow.R;

/**
 * @author jiangzhen
 * 
 */
public class PersonalActivity extends Activity implements OnClickListener {


	private UserService us;

	HandlerThread ht;
	Handler requestHandler;

	private TextView mPersonalNameTv;
	private View mMyFollowingBtn;
	private View mMyFansBtn;
	private View mSettingBtn;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.personal_activity);
		us = new UserService();
		ht = new HandlerThread("PersonalActivity");
		ht.start();

		mPersonalNameTv =  (TextView)findViewById(R.id.personal_name);
		mMyFollowingBtn =  findViewById(R.id.personal_follow_btn);
		mMyFansBtn =  findViewById(R.id.personal_fans_btn);
		mSettingBtn =  findViewById(R.id.personal_setting_btn);


		mMyFollowingBtn.setOnClickListener(this);
		mMyFansBtn.setOnClickListener(this);
	//	mSettingBtn.setOnClickListener(this);
		
		findViewById(R.id.return_button).setOnClickListener(this);
		
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		requestHandler = new Handler(ht.getLooper());
		this.overridePendingTransition(R.animator.right_to_left_in, 0);
		
		mPersonalNameTv.setText(GlobalHolder.getInstance().getCurrentUser().getMobile());

	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent();
		int id = v.getId();
		switch (id) {
		case R.id.personal_follow_btn:
			i.setClass(getApplicationContext(), FansFollowingListActivity.class);
			i.putExtra("type", "following");
			break;
		case R.id.personal_fans_btn:
			i.setClass(getApplicationContext(), FansFollowingListActivity.class);
			i.putExtra("type", "fans");
			break;
		case R.id.personal_setting_btn:
			break;
		case R.id.return_button:
		default:
				finish();
				return;
		}
		
		startActivity(i);

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
	}



	private Handler localHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			default:
				break;
			}
		}

	};


}
