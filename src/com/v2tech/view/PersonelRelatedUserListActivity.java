package com.v2tech.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.v2tech.frag.CardScanFragment;
import com.v2tech.frag.PersonelSearchBarFragment;
import com.v2tech.frag.UserListFragment;
import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.PersonelRelatedUserListPresenter;
import com.v2tech.presenter.PersonelRelatedUserListPresenter.PersonelRelatedUserListPresenterUI;
import com.v2tech.v2liveshow.R;

public class PersonelRelatedUserListActivity extends BaseFragmentActivity implements
		PersonelSearchBarFragment.PersonelSearchBarTextListener,
		UserListFragment.UserListFragmentConnector, PersonelRelatedUserListPresenterUI, View.OnClickListener {

	private static final int BTN_TYPE_FANS = 1;
	private static final int BTN_TYPE_FOLLOWS = 2;
	private static final int BTN_TYPE_FIRENDS = 3;
	private static final int BTN_TYPE_MESSAGE = 4;
	
	
	PersonelSearchBarFragment barFrag;
	UserListFragment listFrag;
	CardScanFragment csFrag;
	TextView title;
	View returnBtn;
	
	private boolean showTimeTV;
	private boolean showRightBtn;
	private int btnType = 0;

	private PersonelRelatedUserListPresenter presenter;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.personel_related_user_list_activity);
		title = (TextView)findViewById(R.id.title_bar_center_tv);
		returnBtn = findViewById(R.id.title_bar_left_btn);
		

		barFrag = (PersonelSearchBarFragment)this.getSupportFragmentManager().findFragmentById(R.id.search_fragment);
		listFrag = (UserListFragment)this.getSupportFragmentManager().findFragmentById(R.id.list_fragment);
		csFrag = (CardScanFragment)this.getSupportFragmentManager().findFragmentById(R.id.card_scan_fragment);
		

		barFrag.setListener(this);
		listFrag.setConnector(this);
		listFrag.setListener(presenter);
		
		returnBtn.setOnClickListener(this);
	}
	
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	public BasePresenter getPresenter() {
		if (presenter == null) {
			int type = this.getIntent().getIntExtra("type", -1);
			presenter = new PersonelRelatedUserListPresenter(this, type, this);
		}
		return this.presenter;
	}

	@Override
	public int getCount() {
		return presenter.getCount();
	}

	@Override
	public Object getItem(int position) {
		return presenter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return presenter.getItemId(position);
	}

	@Override
	public void update(int position, View convertView) {
		presenter.update(position, convertView);
	}

	@Override
	public View inflate() {
		View view = LayoutInflater.from(this).inflate(R.layout.personel_user_item, null);
		LocalBind lb = new LocalBind();
		view.setTag(lb);
		lb.avatar = (ImageView)view.findViewById(R.id.personel_item_avatar);
		lb.name = (TextView)view.findViewById(R.id.personel_item_name);
		lb.sign = (TextView)view.findViewById(R.id.personel_item_sg);
		lb.text = (TextView)view.findViewById(R.id.personel_item_cf_text);
		lb.btn = (ImageView)view.findViewById(R.id.personel_item_cf_btn);
		lb.btn.setOnClickListener(this);
		if (btnType == BTN_TYPE_FANS) {
			lb.btn.setImageResource(R.drawable.personel_item_follow_icon);
			lb.text.setText(R.string.personel_item_user_f_text);
		} else if (btnType == BTN_TYPE_FOLLOWS) {
			lb.btn.setImageResource(R.drawable.personel_item_cancel_follow_icon);
			lb.text.setText(R.string.personel_item_user_cf_text);
		} else if (btnType == BTN_TYPE_FIRENDS) {
			lb.btn.setImageResource(R.drawable.personel_item_cancel_follow_icon);
			lb.text.setText(R.string.personel_item_user_cf_text);
		}
		lb.gender = (ImageView)view.findViewById(R.id.personel_item_gender);
		lb.time =  (TextView)view.findViewById(R.id.personel_item_time);
		lb.time.setVisibility(showTimeTV?View.VISIBLE : View.GONE);
		lb.btn.setVisibility(showRightBtn?View.VISIBLE : View.GONE);
		lb.text.setVisibility(showRightBtn?View.VISIBLE : View.GONE);
		view.setOnClickListener(l);
		return view;
	}

	@Override
	public void onTextChanged(String content) {
		presenter.onTextChanged(content);
	}
	
	
	public void updateSearchBarHint(String text) {
		barFrag.updateSearchBarHint(text);
	}
	
	
	private OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			presenter.onListItemClicked(((LocalBind)v.getTag()).cu);
			
		}
		
	};
	
	public void refreshDataSet() {
		listFrag.notifyDatasetChanged();
	}
	
	public void updateItemUserTag(View parent, Object tag) {
		LocalBind lb = (LocalBind)parent.getTag();
		lb.cu = tag;
	}
	
	public void updateItemAvatar(View parent, Bitmap bm) {
		
	}
	
	public void updateItemName(View parent, CharSequence name) {
		LocalBind lb = (LocalBind)parent.getTag();
		lb.name.setText(name);
	}
	public void updateItemText(View parent, CharSequence txt) {
		LocalBind lb = (LocalBind)parent.getTag();
		lb.text.setText(txt);
	}
	public void updateItemSn(View parent, CharSequence sn) {
		LocalBind lb = (LocalBind)parent.getTag();
		lb.sign.setText(sn);
	}
	public void updateItemBtnCancel(View parent) {
		LocalBind lb = (LocalBind)parent.getTag();
		lb.btn.setImageResource(R.drawable.personel_item_cancel_follow_icon);
	}
	public void updateItemBtnFollow(View parent) {
		LocalBind lb = (LocalBind)parent.getTag();
		lb.btn.setImageResource(R.drawable.personel_item_follow_icon);
	}
	
	public void updateItemBtnTag(View parent, Object tag) {
		LocalBind lb = (LocalBind)parent.getTag();
		lb.btn.setTag(tag);
	}
	
	public void  updateItemGender(View parent, boolean male) {
		LocalBind lb = (LocalBind)parent.getTag();
		if (male) {
			lb.gender.setImageResource(R.drawable.personel_item_gender_male);
		} else {
			lb.gender.setImageResource(R.drawable.personel_item_gender_female);
		}
	}
	
	
	
	public void showFansTitle() {
		removeCardFragment();
		title.setText(R.string.personel_user_list_title_fans);
	}
	
	public void showFriendsTitle() {
		removeCardFragment();
		title.setText(R.string.personel_user_list_title_friends);
	}
	public void showFollowTitle() {
		removeCardFragment();
		title.setText(R.string.personel_user_list_title_follow);
	}
	public void showFrinedInvitationTitle() {
		title.setText(R.string.personel_user_list_title_friend_invitation);
	}
	
	public void showMessageTitle() {
		removeCardFragment();
		title.setText(R.string.personel_user_list_title_message);
	}
	
	public void showTimeView(boolean flag) {
		showTimeTV = flag;
	}
	
	public void showRightBtmView(boolean flag) {
		this.showRightBtn = flag;
	}
	
	
	public void showFansBtnIcon() {
		btnType = BTN_TYPE_FANS;
	}
	
	public void showFollowsBtnIcon() {
		btnType = BTN_TYPE_FOLLOWS;
	}
	public void showFriendsBtnIcon() {
		btnType = BTN_TYPE_FIRENDS;
	}
	
	private void removeCardFragment() {
		csFrag.show(false);
	}
	
	
	public void doFinish() {
		finish();
	}
	
	
	
	
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
		case R.id.personel_item_cf_btn:
			presenter.onItemBtnClicked(v.getTag());
			break;
		case R.id.title_bar_left_btn:
			presenter.onReturnBtnClicked();
			break;
		}
	}










	class LocalBind {
		ImageView avatar;
		TextView name;
		TextView sign;
		TextView text;
		ImageView btn;
		ImageView gender;
		TextView time;
		Object cu;
	}
	
	
	

}
