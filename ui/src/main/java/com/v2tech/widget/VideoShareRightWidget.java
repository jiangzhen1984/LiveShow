package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v2tech.R;

public class VideoShareRightWidget extends LinearLayout {

	private TextView recommandCountTV;
	private TextView tipsCountTV;
	private View cameraSwitchBtn;
	private View videoLockBtn;

	private VideoShareRightWidgetListener listener;

	public VideoShareRightWidget(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public VideoShareRightWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoShareRightWidget(Context context) {
		super(context);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		int id = child.getId();
		 if (id == R.id.video_right_border_rd_ly) {
			recommandCountTV = (TextView) child
					.findViewById(R.id.recommendation_count_tv);
		} else if (id == R.id.video_right_border_tips_ly) {
			tipsCountTV = (TextView) child.findViewById(R.id.tips_count_tv);
		}else if (id == R.id.video_right_border_share_ly) {
			cameraSwitchBtn =  child.findViewById(R.id.camera_switch_btn);
			cameraSwitchBtn.setOnClickListener(clickListener);
		} else if (id == R.id.video_right_border_lock_ly) {
			videoLockBtn = child.findViewById(R.id.video_lock_btn);
			videoLockBtn.setOnClickListener(clickListener);
		}
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			int id = v.getId();
			switch (id) {
			case R.id.camera_switch_btn:
				listener.onCameraSwitchBtnClick(v);
				break;
			case R.id.video_lock_btn:
				listener.onVideoLockBtnClick(v);
				break;
			}

		}

	};


	public void updateTips(String text) {
		if (tipsCountTV == null) {
			throw new NullPointerException("No R.id.tips_count_tv ?");
		}
		tipsCountTV.setText(text);
	}

	public void updateRecommands(String text) {
		if (recommandCountTV == null) {
			throw new NullPointerException("No R.id.recommendation_count_tv ?");
		}
		recommandCountTV.setText(text);
	}
	

	public VideoShareRightWidgetListener getListener() {
		return listener;
	}

	public void setListener(VideoShareRightWidgetListener listener) {
		this.listener = listener;
	}

	public interface VideoShareRightWidgetListener {

		public void onCameraSwitchBtnClick(View v);
		
		public void onVideoLockBtnClick(View v);
	}

}
