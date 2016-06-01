package com.v2tech.widget;

import com.v2tech.v2liveshow.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class VoiceRecordDialogWidget extends RelativeLayout {
	
	
	private View tipRootRequireMoreDuration;
	private View tipRootLongDuration;
	private View tipRootTouchUpCancel;
	private View tiprootVolumn;

	public VoiceRecordDialogWidget(Context context) {
		super(context);
	}

	public VoiceRecordDialogWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VoiceRecordDialogWidget(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		int id = child.getId();
		switch (id) {
		case R.id.voice_record_more_time_require_root:
			tipRootRequireMoreDuration = child;
			break;
		case R.id.voice_record_long_time_dur_root:
			tipRootLongDuration = child;
			break;
		case R.id.voice_record_touch_up_cancel_root:
			tipRootTouchUpCancel = child;
			break;
		case R.id.voice_record_volumn_root:
			tiprootVolumn = child;
			break;
		}
		
	}
	
	
	public void showVolumnView() {
		tiprootVolumn.setVisibility(View.VISIBLE);
		tipRootRequireMoreDuration.setVisibility(View.GONE);
		tipRootLongDuration.setVisibility(View.GONE);
		tipRootTouchUpCancel.setVisibility(View.GONE);
	}
	
	public void showRequireMoreDuration() {
		tiprootVolumn.setVisibility(View.GONE);
		tipRootRequireMoreDuration.setVisibility(View.VISIBLE);
		tipRootLongDuration.setVisibility(View.GONE);
		tipRootTouchUpCancel.setVisibility(View.GONE);
	}
	
	
	public void showLongDurationView() {
		tiprootVolumn.setVisibility(View.GONE);
		tipRootRequireMoreDuration.setVisibility(View.GONE);
		tipRootLongDuration.setVisibility(View.VISIBLE);
		tipRootTouchUpCancel.setVisibility(View.GONE);
	}
	
	public void showTouchUpCancelView() {
		tiprootVolumn.setVisibility(View.GONE);
		tipRootRequireMoreDuration.setVisibility(View.GONE);
		tipRootLongDuration.setVisibility(View.GONE);
		tipRootTouchUpCancel.setVisibility(View.VISIBLE);
	}

}
