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
	
	private boolean init;
	
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

	
	public void init() {
		if (init) {
			return;
		}
		if (leftBtn == null) {
			leftBtn = findViewById(R.id.p2p_video_main_left_btn);
		}
		if (rightBtn == null) {
			rightBtn = findViewById(R.id.p2p_video_main_right_btn);
		}
		
		if (surfaceView == null) {
			surfaceView = (SurfaceView)findViewById(R.id.p2p_video_main_surfaceview);
		}
		
		leftBtn.setOnClickListener(click);
		rightBtn.setOnClickListener(click);
		init = true;
	}
	
	public View getLeftBtn() {
		init();
		return leftBtn;
	}

	public View getRightBtn() {
		init();
		return rightBtn;
	}

	public SurfaceView getSurfaceView() {
		init();
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
				listener.onP2PVideoMainRightBtnClicked(v);
				break;
			case R.id.p2p_video_main_right_btn:
				listener.onP2PVideoMainLeftBtnClicked(v);
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
