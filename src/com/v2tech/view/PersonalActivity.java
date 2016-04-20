/**
 * 
 */
package com.v2tech.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.v2tech.presenter.PersonelPresenter;
import com.v2tech.presenter.PersonelPresenter.PersonelPresenterUI;
import com.v2tech.v2liveshow.R;

/**
 * @author jiangzhen
 * 
 */
public class PersonalActivity extends Activity implements OnClickListener,
		PersonelPresenterUI {


	private TextView mPersonalNameTv;
	private View mMyFollowingBtn;
	private View mMyFansBtn;
	private View mSettingBtn;
	private View mFriendsBtn;
	private View mWalletBtn;
	private View mVideoBtn;
	private View mFriendsInvitationBtn;
	private TextView titleBarName;
	private View mPerBtn;

	private PersonelPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.personal_activity);
		presenter = new PersonelPresenter(this, this);

		mPersonalNameTv = (TextView) findViewById(R.id.personal_name);
		mMyFollowingBtn = findViewById(R.id.personal_follow_btn);
		mMyFansBtn = findViewById(R.id.personal_fans_btn);
		mSettingBtn = findViewById(R.id.personal_setting_btn);
		mFriendsBtn = findViewById(R.id.personal_friends_btn);
		mWalletBtn = findViewById(R.id.personal_wallet_btn);
		mVideoBtn = findViewById(R.id.personal_video_btn);
		mFriendsInvitationBtn = findViewById(R.id.personal_friends_invitation_btn);
		
		mPerBtn = findViewById(R.id.avatar_layout);
		
		
		mVideoBtn.setOnClickListener(this);
		mPerBtn.setOnClickListener(this);
		mFriendsBtn.setOnClickListener(this);
		mMyFollowingBtn.setOnClickListener(this);
		mMyFansBtn.setOnClickListener(this);
		mSettingBtn.setOnClickListener(this);
		mWalletBtn.setOnClickListener(this);

		findViewById(R.id.title_bar_left_btn).setOnClickListener(this);
		titleBarName = (TextView)findViewById(R.id.title_bar_center_tv);

		presenter.onUICreated();
		this.overridePendingTransition(R.animator.left_to_right_in, R.animator.left_to_right_out);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.avatar_layout:
			presenter.personelBtnClicked();
			break;
		case R.id.personal_follow_btn:
			presenter.followBtnClicked();
			break;
		case R.id.personal_fans_btn:
			presenter.fansBtnClicked();
			break;
		case R.id.personal_setting_btn:
			presenter.settingBtnClicked();
			break;
		case R.id.personal_friends_btn:
			presenter.friendsBtnClicked();
			break;
		case R.id.title_bar_left_btn:
			presenter.returnBtnClicked();
			break;
		case R.id.personal_wallet_btn: 
			presenter.walletBtnClicked();
			break;
		case R.id.personal_video_btn:
			presenter.videoBtnClicked();
			break;
		case R.id.personal_friends_invitation_btn:
			presenter.friendsInvitationBtnClicked();
			break;
		default:
		}

	}

	private void showFollowsUI() {
		Intent i = new Intent();
		i.addCategory("com.v2tech");
		i.setAction("com.v2tech.action.FOLLOWING_FANS_ACTIVITY");
		i.putExtra("type", "following");
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.animator.left_to_right_out);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finishMainUI() {
		finish();
		
	}

	@Override
	public void updateTitleBar() {
		titleBarName.setText(R.string.personal_title_bar);
	}

	
	public void showPersonelDetailUI() {
		Intent i = new Intent();
		i.setClass(this, PersonalShowActivity.class);
		this.startActivity(i);
	}
	
	
}
