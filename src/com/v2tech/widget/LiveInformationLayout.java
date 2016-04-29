package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.v2tech.v2liveshow.R;

public class LiveInformationLayout extends LinearLayout {

	private ImageView marqueeEnableBtn;
	
	public LiveInformationLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public LiveInformationLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LiveInformationLayout(Context context) {
		super(context);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.message_marquee_btn) {
			marqueeEnableBtn = (ImageView)child;
		}
	}

	
	public void enableLiveMessage(boolean flag) {
		marqueeEnableBtn.setImageResource(flag?R.drawable.message_marquee_enable : R.drawable.message_marquee_disable);
	}
	
}
