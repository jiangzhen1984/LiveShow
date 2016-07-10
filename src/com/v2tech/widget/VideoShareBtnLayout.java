package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.V2.jni.util.V2Log;
import com.v2tech.v2liveshow.R;

public class VideoShareBtnLayout extends RelativeLayout {

	private Button shareBtn;

	private View mapBtn;
	
	private View wechatShareBtn;

	private VideoShareBtnLayoutListener listener;

	public VideoShareBtnLayout(Context context) {
		super(context);
	}

	public VideoShareBtnLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoShareBtnLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.video_share_button) {
			shareBtn = (Button) child;
		} else if (child.getId() == R.id.video_share_map_btn) {
			mapBtn = child;
		} else if (child.getId() == R.id.video_share_outer_btn_ly) {
			wechatShareBtn = child.findViewById(R.id.video_share_wechat_share_btn);
			wechatShareBtn.setOnClickListener(clickListener);
		}
		child.setOnClickListener(clickListener);
	}

	
	float initY;
	float lastY;
	float offsetY;
	int mActivePointerId;
	
	


	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			int id = v.getId();
			switch (id) {
			case R.id.video_share_button:
				listener.onVideoSharedBtnClicked(v);
				break;
			case  R.id.video_share_map_btn:
				listener.onMapShareBtnClicked(v);
				break;
			case  R.id.video_share_wechat_share_btn:
				listener.onWechatShareBtnClicked(v);
				break;
			}

		}

	};

	public void updateSharedBtnBackground(int res) {
		if (shareBtn == null) {
			throw new NullPointerException(
					" No  R.id.video_share_button id in layout");
		}
		shareBtn.setBackgroundResource(res);
	}

	public VideoShareBtnLayoutListener getListener() {
		return listener;
	}

	public void setListener(VideoShareBtnLayoutListener listener) {
		this.listener = listener;
	}

	public interface VideoShareBtnLayoutListener {
		public void onVideoSharedBtnClicked(View v);
		
		public void onMapShareBtnClicked(View v);
		
		public void onWechatShareBtnClicked(View v);
	}
}
