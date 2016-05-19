package com.v2tech.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.V2.jni.util.V2Log;
import com.v2tech.v2liveshow.R;

public class VideoShareBtnLayout extends RelativeLayout {

	private Button shareBtn;

	private View dragView;
	
	private View mapBtn;

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
			shareBtn.setOnClickListener(clickListener);
		} else if (child.getId() == R.id.video_share_drag_view) {
			dragView = child;
			dragView.setOnTouchListener(touchListener);
		} else if (child.getId() == R.id.video_share_map_btn) {
			mapBtn = child;
			mapBtn.setOnClickListener(clickListener);
		}
	}

	
	float initY;
	float lastY;
	float offsetY;
	int mActivePointerId;
	
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = MotionEventCompat.getPointerId(event, 0);
				initY = MotionEventCompat.getY(event, 0);
				lastY = initY;
				if (listener != null) {
					listener.requestStartDrag();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				final int pointerIndex = MotionEventCompat.findPointerIndex(
						event, mActivePointerId);
				final float y = MotionEventCompat.getY(event, pointerIndex);
				final float dy = y - lastY;
				if (listener != null) {
					listener.requestUpdateOffset((int) dy);
				}
				lastY = y;
				break;
			case MotionEvent.ACTION_UP:
				V2Log.d("Start translate");
				if (listener != null) {
					listener.requestFlyingOut();
				}
				break;
			}
			return true;
		}

	};

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
		
		public void requestStartDrag();
		
		public void requestUpdateOffset(int dy);
		
		public void requestFlyingOut();
		
		public void onMapShareBtnClicked(View v);
	}
}
