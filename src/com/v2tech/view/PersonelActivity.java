/**
 * 
 */
package com.v2tech.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.PersonelPresenter;
import com.v2tech.presenter.PersonelPresenter.PersonelPresenterUI;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.User;

/**
 * @author jiangzhen
 * 
 */
public class PersonelActivity extends BaseActivity implements OnClickListener,
		PersonelPresenterUI {

	private View mMyFollowingBtn;
	private View mMyFansBtn;
	private View mSettingBtn;
	private View mFriendsBtn;
	private View mWalletBtn;
	private View mVideoBtn;
	private View mFriendsInvitationBtn;
	private View mMyMessageBtn;
	private TextView titleBarName;
	private View mPerBtn;
	private TextView mPersonalNameTv;
	private TextView mAccountName;
	private ImageView mGender;
	private ImageView mVipLevel;
	private ImageView mAvatar;

	private PersonelPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.personal_activity);
		

		mPersonalNameTv = (TextView) findViewById(R.id.personal_name);
		mMyFollowingBtn = findViewById(R.id.personal_follow_btn);
		mMyFansBtn = findViewById(R.id.personal_fans_btn);
		mSettingBtn = findViewById(R.id.personal_setting_btn);
		mFriendsBtn = findViewById(R.id.personal_friends_btn);
		mWalletBtn = findViewById(R.id.personal_wallet_btn);
		mVideoBtn = findViewById(R.id.personal_video_btn);
		mFriendsInvitationBtn = findViewById(R.id.personal_friends_invitation_btn);
		mMyMessageBtn = findViewById(R.id.personal_my_message_btn);
		mPerBtn = findViewById(R.id.avatar_layout);
		mAccountName = (TextView) findViewById(R.id.personel_username);
		mGender = (ImageView) findViewById(R.id.personel_gender);
		mVipLevel = (ImageView) findViewById(R.id.personel_vip_level);
		mAvatar = (ImageView) findViewById(R.id.avatar);
		
		mVideoBtn.setOnClickListener(this);
		mPerBtn.setOnClickListener(this);
		mFriendsBtn.setOnClickListener(this);
		mMyFollowingBtn.setOnClickListener(this);
		mMyFansBtn.setOnClickListener(this);
		mSettingBtn.setOnClickListener(this);
		mWalletBtn.setOnClickListener(this);
		mFriendsInvitationBtn.setOnClickListener(this);
		mMyMessageBtn.setOnClickListener(this);

		findViewById(R.id.title_bar_left_btn).setOnClickListener(this);
		titleBarName = (TextView) findViewById(R.id.title_bar_center_tv);

		this.overridePendingTransition(R.animator.left_to_right_in,
				R.animator.left_to_right_out);

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
		case R.id.personal_my_message_btn:
			presenter.myMessageBtnClicked();
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
	public BasePresenter getPresenter() {
		if (presenter == null) {
			presenter = new PersonelPresenter(this, this);
		}
		return presenter;
	}

	@Override
	public void updateTitleBar() {
		titleBarName.setText(R.string.personal_title_bar);
	}

	public void showPersonelDetailUI() {
		Intent i = new Intent();
		i.setClass(this, PersonelShowActivity.class);
		this.startActivity(i);
	}

	public void updateUserUI(User user) {
		mPersonalNameTv.setText(user.getName());
		mAccountName.setText(user.getMobile());
		if ("".equals(user.getSex())) {
			mGender.setImageResource(R.drawable.gender_female);
		} else {
			mGender.setImageResource(R.drawable.gender_male);
		}
		switch (user.vipLevel) {
		case 1:
			mVipLevel.setImageResource(R.drawable.level_1);
			break;
		case 2:
			mVipLevel.setImageResource(R.drawable.level_2);
			break;
		case 3:
			mVipLevel.setImageResource(R.drawable.level_3);
			break;
		case 4:
			mVipLevel.setImageResource(R.drawable.level_4);
			break;
		default:
			mVipLevel.setImageResource(R.drawable.level_5);
			break;
		}
		
		//TODO update user avatar
	}

}
