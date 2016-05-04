package com.v2tech.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v2tech.v2liveshow.R;

public class LiverInteractionLayout extends LinearLayout {


	private ImageView avatar;
	private TextView name;
	private ImageView gender;
	private ImageView level;
	private TextView signature;
	private TextView location;
	private TextView videos;
	private TextView fans;
	private TextView follows;
	private View innerBox;
	private View chatRequestBtn;
	private View videoChatBtn;
	private View showMsgBtn;
	
	private View followBtn;
	private ImageView followBtnIV;
	private TextView  followBtnTV; 
	
	
	private InterfactionBtnClickListener outListener;
	
	private boolean flag;

	public LiverInteractionLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public LiverInteractionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LiverInteractionLayout(Context context) {
		super(context);
	}


	public void updateAvatarImg(Bitmap bm) {
		avatar.setImageBitmap(bm);
	}

	public void updateGenderImg(int res) {
		gender.setImageResource(res);
	}

	public void updateLevelImg(int res) {
		level.setImageResource(res);
	}

	public void updateNameText(String str) {
		name.setText(str);
	}
	
	public void updateSignature(String text) {
		signature.setText(text);
	}

	public void updateLocationText(String str) {
		location.setText(str);
	}

	public void updateVidoesText(String str) {
		videos.setText(str);
	}
	
	public void updateFansText(String str) {
		fans.setText(str);
	}
	
	public void updateFollowsText(String str) {
		follows.setText(str);
	}

	
	public void showInnerBox(boolean flag) {
		if (innerBox == null) {
			innerBox = findViewById(R.id.liver_interaction_box_layout);
		}
		innerBox.setVisibility(flag? View.VISIBLE : View.GONE);
	}
	
	private void initView() {
		avatar = (ImageView) findViewById(R.id.liver_interaction_avtar);
		name = (TextView) findViewById(R.id.liver_interaction_name);
		gender = (ImageView) findViewById(R.id.liver_interaction_gender);
		level = (ImageView) findViewById(R.id.liver_interaction_level);
		signature = (TextView)findViewById(R.id.liver_interaction_signature);
		location = (TextView) findViewById(R.id.liver_interaction_location);
		videos = (TextView) findViewById(R.id.liver_interaction_videos);
		fans = (TextView) findViewById(R.id.liver_interaction_fans);
		follows = (TextView) findViewById(R.id.liver_interaction_follows);
		
		chatRequestBtn = findViewById(R.id.liver_interaction_chating_btn_iv);
		chatRequestBtn.setOnClickListener(listener);
		
		videoChatBtn = findViewById(R.id.liver_interaction_video_call_btn_iv);
		videoChatBtn.setOnClickListener(listener);
		
		showMsgBtn = findViewById(R.id.liver_interaction_msg_btn_iv);
		showMsgBtn.setOnClickListener(listener);
		
		innerBox = findViewById(R.id.liver_interaction_box_layout);
		innerBox.setVisibility(View.GONE);
		
		followBtn = findViewById(R.id.liver_interaction_btn_ly);
		followBtn.setOnClickListener(listener);
		
		followBtnIV = (ImageView)findViewById(R.id.liver_interaction_btn);
		followBtnTV = (TextView)findViewById(R.id.liver_interaction_text);
		
		chatRequestBtn.setOnClickListener(listener);
	}
	
	
	
	
	
	
	

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
	}

	
	public void updateFollowBtnImageResource(int res) {
		followBtnIV.setImageResource(res);
	}

	public void updateFollowBtnTextResource(int res) {
		followBtnTV.setText(res);
	}


	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (outListener == null) {
				return;
			}
			int id = v.getId();
			switch (id) {
			case R.id.liver_interaction_chating_btn_iv:
				outListener.onChattingBtnClicked(v);
				break;
			case R.id.liver_interaction_msg_btn_iv:
				outListener.onMsgBtnClicked(v);
				break;
			case R.id.liver_interaction_video_call_btn_iv:
				outListener.onVideoCallBtnClicked(v);
				break;
			case R.id.liver_interaction_btn_ly:
				outListener.onFollowBtnClick(v);
				break;
			}
		}
		
	};
	
	
	
	
	public InterfactionBtnClickListener getOutListener() {
		return outListener;
	}

	public void setOutListener(InterfactionBtnClickListener outListener) {
		this.outListener = outListener;
		if (!flag) {
			flag = true;
			initView();
		}
	}




	public interface InterfactionBtnClickListener {
		
		public void onChattingBtnClicked(View v);
		
		public void onVideoCallBtnClicked(View v);
		
		public void onMsgBtnClicked(View v);
		
		public void onFollowBtnClick(View v);
	}

}
