package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.v2tech.v2liveshow.R;

public class P2PVideoMainLayout extends RelativeLayout {
	
	
	private View leftBtn;
	
	private View rightBtn;
	
	private SurfaceView surfaceView;
	
	private P2PVideoMainLayoutListener listener;
	

	public P2PVideoMainLayout(Context context) {
		super(context);
	}

	public P2PVideoMainLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public P2PVideoMainLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	

	
	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.p2p_video_main_surfaceview) {
			surfaceView = (SurfaceView)child;
		} else if (child.getId() == R.id.p2p_video_main_btn_ly) {
			leftBtn = child.findViewById(R.id.p2p_video_main_left_btn);
			rightBtn = child.findViewById(R.id.p2p_video_main_right_btn);
			leftBtn.setOnClickListener(click);
			rightBtn.setOnClickListener(click);
			this.bringChildToFront(child);
		}
	}


	
	public View getLeftBtn() {
		return leftBtn;
	}

	public View getRightBtn() {
		return rightBtn;
	}

	public SurfaceView getSurfaceView() {
		return surfaceView;
	}
	
	
	private OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			
			int id = v.getId();
			switch (id) {
			case R.id.p2p_video_main_left_btn:
				listener.onP2PVideoMainLeftBtnClicked(v);
				break;
			case R.id.p2p_video_main_right_btn:
				listener.onP2PVideoMainRightBtnClicked(v);
				break;
			}
		}
		
	};

	
	public P2PVideoMainLayoutListener getListener() {
		return listener;
	}

	public void setListener(P2PVideoMainLayoutListener listener) {
		this.listener = listener;
	}




	public interface P2PVideoMainLayoutListener {
		public void onP2PVideoMainLeftBtnClicked(View v);
		public void onP2PVideoMainRightBtnClicked(View v);
	}
	
	
	
}
