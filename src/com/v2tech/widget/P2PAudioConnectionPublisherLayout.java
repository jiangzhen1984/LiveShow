package com.v2tech.widget;

import com.v2tech.v2liveshow.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class P2PAudioConnectionPublisherLayout extends RelativeLayout {
	
	private View hangoffBtn;

	
	private P2PAudioConnectionPublisherLayoutListener listener;
	
	public P2PAudioConnectionPublisherLayout(Context context) {
		super(context);
	}

	public P2PAudioConnectionPublisherLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public P2PAudioConnectionPublisherLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (R.id.p2p_audio_connection_publisher_layout_hang_off_btn == child.getId()) {
			hangoffBtn = child;
			hangoffBtn.setOnClickListener(localBtnListener);
		}
	}
	
	
	
	
	private OnClickListener localBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			int id = v.getId();
			switch(id) {
			case R.id.p2p_audio_connection_publisher_layout_hang_off_btn:
				listener.P2PAudioConnectionHangoffBtnClicked(v);
				break;
			}
		}
		
	};
	
	
	
	
	public P2PAudioConnectionPublisherLayoutListener getListener() {
		return listener;
	}

	public void setListener(P2PAudioConnectionPublisherLayoutListener listener) {
		this.listener = listener;
	}




	public interface P2PAudioConnectionPublisherLayoutListener {
		public void P2PAudioConnectionHangoffBtnClicked(View v);
	}

}
