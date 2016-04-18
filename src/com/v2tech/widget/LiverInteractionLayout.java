package com.v2tech.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v2tech.v2liveshow.R;

public class LiverInteractionLayout extends LinearLayout {

	private View root;

	private ImageView avatar;
	private TextView name;
	private ImageView gender;
	private ImageView level;
	private TextView signature;
	private TextView location;
	private TextView videos;
	private TextView fans;
	private TextView follows;
	private View personelBtn;
	private View innerBox;
	
	
	private InterfactionBtnClickListener outListener;

	public LiverInteractionLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LiverInteractionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LiverInteractionLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		root = LayoutInflater.from(getContext()).inflate(
				R.layout.liver_interaction_layout, null);
		this.addView(root, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		avatar = (ImageView) root.findViewById(R.id.liver_interaction_avtar);
		name = (TextView) root.findViewById(R.id.liver_interaction_name);
		gender = (ImageView) root.findViewById(R.id.liver_interaction_gender);
		level = (ImageView) root.findViewById(R.id.liver_interaction_level);
		signature = (TextView) root
				.findViewById(R.id.liver_interaction_signature);
		location = (TextView) root
				.findViewById(R.id.liver_interaction_location);
		videos = (TextView) root.findViewById(R.id.liver_interaction_videos);
		fans = (TextView) root.findViewById(R.id.liver_interaction_fans);
		follows = (TextView) root.findViewById(R.id.liver_interaction_follows);
		personelBtn = root.findViewById(R.id.liver_interaction_btn);
		personelBtn.setOnClickListener(listener);
		
		innerBox = root.findViewById(R.id.liver_interaction_box_layout);
		innerBox.setVisibility(View.GONE);
	}

	public void updateAvatarImg(Bitmap bm) {

	}

	public void updateGenderImg(int res) {

	}

	public void updateLevelImg(int res) {

	}

	public void updateNameText(String str) {

	}

	public void updateLocationText(String str) {

	}

	public void updateVidoesText(String str) {

	}
	
	public void updateFansText(String str) {

	}
	
	public void updateFollowsText(String str) {

	}

	
	public void showInnerBox(boolean flag) {
		innerBox.setVisibility(flag? View.VISIBLE : View.GONE);
	}
	
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.liver_interaction_btn:
				if (outListener != null) {
					outListener.onPersonelBtnClicked(v);
				}
				break;
			}
		}
		
	};
	
	
	public interface InterfactionBtnClickListener {
		public void onPersonelBtnClicked(View v);
		
		public void onChattingBtnClicked(View v);
		
		public void onVideoCallBtnClicked(View v);
		
		public void onMsgBtnClicked(View v);
	}

}
