package com.v2tech.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.v2tech.R;
import com.v2tech.widget.wheel.NumericArrayWheelAdapter;
import com.v2tech.widget.wheel.WheelView;

public class BountyMarkerWidget extends RelativeLayout {
	
	
	private WheelView wheelView;

	public BountyMarkerWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BountyMarkerWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BountyMarkerWidget(Context context) {
		super(context);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
//		if (child.getId() == R.id.bounty_wheel_view) {
//			wheelView = (WheelView)child;
//			NumericArrayWheelAdapter nwa = new NumericArrayWheelAdapter(
//					getContext(),
//					new int[] { 1, 2, 3, 5, 8, 10, 15, 20, 50, 80 }, "¥%1d");
//			wheelView.setVisibleItems(3);
//			nwa.setTextColor(Color.GRAY);
//			nwa.setTextSize(12);
//			wheelView.setViewAdapter(nwa);
//			wheelView.setCurrentItem(10);
//			wheelView.setCyclic(true);
//		}
	}
	
	

}
