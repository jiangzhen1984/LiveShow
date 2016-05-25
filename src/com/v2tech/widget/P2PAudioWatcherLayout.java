package com.v2tech.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.v2tech.v2liveshow.R;
import com.v2tech.widget.wheel.NumericArrayWheelAdapter;
import com.v2tech.widget.wheel.NumericWheelAdapter;
import com.v2tech.widget.wheel.WheelView;

public class P2PAudioWatcherLayout extends LinearLayout {

	private View recordBtn;
	private View chatBtn;
	private View tipsBtn;

	private WheelView wheelView;

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
		if (child.getId() == R.id.p2p_audio_watcher_root_layout) {
			recordBtn = child.findViewById(R.id.p2p_audio_record_btn);
			recordBtn.setOnClickListener(listener);

			chatBtn = child.findViewById(R.id.p2p_audio_chat_btn);
			chatBtn.setOnClickListener(listener);

			tipsBtn = child.findViewById(R.id.p2p_audio_tips_btn);
			tipsBtn.setOnClickListener(listener);

			wheelView = (WheelView) child.findViewById(R.id.tips_wheel_view);
			NumericArrayWheelAdapter nwa = new NumericArrayWheelAdapter(
					getContext(),
					new int[] { 1, 2, 3, 5, 8, 10, 15, 20, 50, 80 }, "¥%1d");
			nwa.setTextColor(Color.GRAY);
			nwa.setTextSize(12);
			wheelView.setViewAdapter(nwa);
			wheelView.setCurrentItem(10);
			wheelView.setCyclic(true);
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
