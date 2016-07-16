package com.v2tech.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v2tech.v2liveshow.R;

public class LivePublisherPersonelLayout extends LinearLayout {


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
	
	private View videoRecordBtn;
	private View tipsBtn;
	private View chattingBtn;
	
	private View connectedBtnLayout;
	private View normalBtnLayout;
	
	private InterfactionBtnClickListener outListener;
	
	public LivePublisherPersonelLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public LivePublisherPersonelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LivePublisherPersonelLayout(Context context) {
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
	
	
	public void showConnectedBtnLayout(boolean flag) {
		connectedBtnLayout.setVisibility(flag? View.VISIBLE : View.GONE);
		normalBtnLayout.setVisibility((!flag)? View.VISIBLE : View.GONE);
	}
	

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		
		int id = child.getId();
		switch (id) {
		case R.id.publisher_personel_layout:
			avatar = (ImageView) child.findViewById(R.id.live_publisher_avtar);
			name = (TextView)  child.findViewById(R.id.liver_interaction_name);
			gender = (ImageView)  child.findViewById(R.id.liver_interaction_gender);
			level = (ImageView)  child.findViewById(R.id.liver_interaction_level);
			signature = (TextView) child.findViewById(R.id.liver_interaction_signature);
			location = (TextView)  child.findViewById(R.id.liver_interaction_location);
			videos = (TextView)  child.findViewById(R.id.liver_interaction_videos);
			fans = (TextView)  child.findViewById(R.id.liver_interaction_fans);
			follows = (TextView)  child.findViewById(R.id.liver_interaction_follows);
			followBtn = child.findViewById(R.id.liver_interaction_btn_ly);
			followBtn.setOnClickListener(listener);
			followBtnIV = (ImageView)child.findViewById(R.id.liver_interaction_btn);
			followBtnTV = (TextView)child.findViewById(R.id.liver_interaction_text);
			break;
		case R.id.publisher_personel_btn_layout:
			connectedBtnLayout = child.findViewById(R.id.audio_connection_watcher_btn_layout);
			videoRecordBtn = child.findViewById(R.id.p2p_audio_connection_watcher_btn_layout_record_btn);
			tipsBtn = child.findViewById(R.id.p2p_audio_connection_watcher_btn_layout_chatting_btn);
			chattingBtn = child.findViewById(R.id.p2p_audio_connection_watcher_btn_layout_tips_btn);
			
			normalBtnLayout = child.findViewById(R.id.live_publish_watcher_btn_layout);
			chatRequestBtn = child.findViewById(R.id.live_publish_watcher_btn_layout_audio_call_btn);
			videoChatBtn = child.findViewById(R.id.live_publish_watcher_btn_layout_video_call_btn);
			showMsgBtn = child.findViewById(R.id.live_publish_watcher_btn_layout_message_btn);
			chatRequestBtn.setOnClickListener(listener);
			videoChatBtn.setOnClickListener(listener);
			showMsgBtn.setOnClickListener(listener);
			break;
		case R.id.liver_interaction_box_layout:
			innerBox = child;
			innerBox.setVisibility(View.GONE);
			break;
		}
	}

	
	public void updateFollowBtnImageResource(int res) {
		followBtnIV.setImageResource(res);
	}

	public void updateFollowBtnTextResource(int res) {
		followBtnTV.setText(res);
	}

	
	
	public View getAudioRecordBtn() {
		return chatRequestBtn;
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (outListener == null) {
				return;
			}
			int id = v.getId();
			switch (id) {
			case R.id.live_publish_watcher_btn_layout_audio_call_btn:
				outListener.onChattingBtnClicked(v);
				break;
			case R.id.live_publish_watcher_btn_layout_message_btn:
				outListener.onMsgBtnClicked(v);
				break;
			case R.id.live_publish_watcher_btn_layout_video_call_btn:
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
	}




	public interface InterfactionBtnClickListener {
		
		public void onChattingBtnClicked(View v);
		
		public void onVideoCallBtnClicked(View v);
		
		public void onMsgBtnClicked(View v);
		
		public void onFollowBtnClick(View v);
	}

}
