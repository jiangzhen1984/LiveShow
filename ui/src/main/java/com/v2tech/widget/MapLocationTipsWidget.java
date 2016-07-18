package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.v2tech.R;

public class MapLocationTipsWidget extends RelativeLayout {
	
	private EditText tipsText;

	public MapLocationTipsWidget(Context context) {
		super(context);
	}

	public MapLocationTipsWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MapLocationTipsWidget(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.map_location_tips_search_text_et) {
			tipsText =(EditText)child;
		}
	}

	public EditText getTipsText() {
		return tipsText;
	}

	
	
	public void updateMapLocationAddress(String address) {
		tipsText.setText(address);
	}
	
}
