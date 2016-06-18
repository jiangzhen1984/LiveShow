package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.v2tech.v2liveshow.R;

public class InquiryBidWidget extends RelativeLayout implements OnClickListener {
	
	private EditText tipsEdit;
	private EditText wordEdit;
	private View inquiryLaunchBtn;

	public InquiryBidWidget(Context context) {
		super(context);
	}

	public InquiryBidWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InquiryBidWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		int id = child.getId();
		switch (id) {
		case R.id.inquiry_bid_tips_et:
			tipsEdit = (EditText)(child);
			break;
		case R.id.inquiry_bid_word_et:
			wordEdit = (EditText)(child);
			break;
		case R.id.inquiry_launch_btn:
			inquiryLaunchBtn = child;
			inquiryLaunchBtn.setOnClickListener(this);
			break;
			
		}
	}
	
	public EditText getTipsEditText() {
		return this.tipsEdit;
	}
	
	public EditText getWordEditText() {
		return this.wordEdit;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
		case R.id.inquiry_launch_btn:
			break;
		}
	}

	
	
	
	
}
