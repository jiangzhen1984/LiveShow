package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.v2tech.R;

public class VoiceRecordDialogWidget extends RelativeLayout {
	
	
	private View tipRootRequireMoreDuration;
	private View tipRootLongDuration;
	private View tipRootTouchUpCancel;
	private View tiprootVolumn;
	private ImageView volumn;

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
			volumn = (ImageView)child.findViewById(R.id.voice_record_icon_volumn_iv);
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
	
	public void updateVolumnLevel(int level) {
		switch (level) {
		case 1:
			volumn.setImageResource(R.drawable.voice_record_volumn_1);
			break;
		case 2:
			volumn.setImageResource(R.drawable.voice_record_volumn_2);
			break;
		case 3:
			volumn.setImageResource(R.drawable.voice_record_volumn_3);
			break;
		case 4:
			volumn.setImageResource(R.drawable.voice_record_volumn_4);
			break;
		case 5:
			volumn.setImageResource(R.drawable.voice_record_volumn_5);
			break;
		case 6:
			volumn.setImageResource(R.drawable.voice_record_volumn_6);
			break;
		case 7:
			volumn.setImageResource(R.drawable.voice_record_volumn_7);
			break;
		case 8:
			volumn.setImageResource(R.drawable.voice_record_volumn_8);
			break;
		default:
			volumn.setImageResource(R.drawable.voice_record_volumn_0);
			break;
			
		}
	}

}
