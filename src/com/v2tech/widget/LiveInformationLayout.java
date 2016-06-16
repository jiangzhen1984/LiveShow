package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v2tech.v2liveshow.R;

public class LiveInformationLayout extends LinearLayout {

	private View videoCloseBtn;
	private TextView recommandCountTV;
	private TextView tipsCountTV;
	private View tipsBtn;
	private View recommandBtn;

	private LiveInformationLayoutListener listener;

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
		int id = child.getId();
		if (id == R.id.video_close_btn) {
			videoCloseBtn = child;
			videoCloseBtn.setOnClickListener(clickListener);
		} else if (id == R.id.video_right_border_rd_ly) {
			recommandCountTV = (TextView) child
					.findViewById(R.id.recommendation_count_tv);
			recommandBtn = child.findViewById(R.id.recommendation_button);
			recommandBtn.setOnClickListener(clickListener);
		} else if (id == R.id.video_right_border_tips_ly) {
			tipsCountTV = (TextView) child.findViewById(R.id.tips_count_tv);
			tipsBtn = child.findViewById(R.id.tips_button);
			tipsBtn.setOnClickListener(clickListener);
		}
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			int id = v.getId();
			switch (id) {
			case R.id.recommendation_button:
				listener.onLiveInfoRecommandBtnClicked(v);
				break;
			case R.id.tips_button:
				listener.onLiveInfoTipsBtnClicked(v);
				break;
			case R.id.video_close_btn:
				listener.onCloseBtnClicked(v);
				break;
			}

		}

	};


	public void updateTips(String text) {
		if (tipsCountTV == null) {
			throw new NullPointerException("No R.id.tips_count_tv ?");
		}
		tipsCountTV.setText(text);
	}

	public void updateRecommands(String text) {
		if (recommandCountTV == null) {
			throw new NullPointerException("No R.id.recommendation_count_tv ?");
		}
		recommandCountTV.setText(text);
	}

	public LiveInformationLayoutListener getListener() {
		return listener;
	}

	public void setListener(LiveInformationLayoutListener listener) {
		this.listener = listener;
	}

	public interface LiveInformationLayoutListener {
		public void onLiveInfoTipsBtnClicked(View v);

		public void onLiveInfoRecommandBtnClicked(View v);

		public void onCloseBtnClicked(View v);
	}

}
