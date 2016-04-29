package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.v2tech.v2liveshow.R;

public class P2PAudioWatcherLayout extends LinearLayout {



	private View recordBtn;
	private View chatBtn;
	private View tipsBtn;
	
	
	private P2PAudioWatcherLayoutListener outListener;
	
	public P2PAudioWatcherLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public P2PAudioWatcherLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public P2PAudioWatcherLayout(Context context) {
		super(context);
		init();
	}

	private void init() {

	}

	

	
	
	
	

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.p2p_audio_record_btn) {
			recordBtn = child;
			recordBtn.setOnClickListener(listener);
		} else if (child.getId() == R.id.p2p_audio_chat_btn) {
			chatBtn = child;
			chatBtn.setOnClickListener(listener);
		}else if (child.getId() == R.id.p2p_audio_tips_btn) {
			tipsBtn = child;
			tipsBtn.setOnClickListener(listener);
		}
	}





	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (outListener == null) {
				return;
			}
			int id = v.getId();
			switch (id) {
			case R.id.p2p_audio_record_btn:
				break;
			case R.id.p2p_audio_chat_btn:
				break;
			case R.id.p2p_audio_tips_btn:
				break;
			}
		}
		
	};
	
	
	




	public P2PAudioWatcherLayoutListener getOutListener() {
		return outListener;
	}

	public void setOutListener(P2PAudioWatcherLayoutListener outListener) {
		this.outListener = outListener;
	}







	public interface P2PAudioWatcherLayoutListener {
		public void onRecordBtnClicked(View view);
		public void onChatBtnClicked(View view);
		public void onTipsBtnClicked(View view);
		
	}

}
