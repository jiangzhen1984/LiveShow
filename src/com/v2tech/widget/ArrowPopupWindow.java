package com.v2tech.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.v2tech.v2liveshow.R;

public class ArrowPopupWindow extends PopupWindow {

	private View itemLayout;
	private View arrowUp;
	private int width;
	
	public ArrowPopupWindow(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(
				R.layout.title_bar_pop_up_window, null);
		
		itemLayout = layout.findViewById(R.id.common_pop_window_container);
		
		arrowUp = layout.findViewById(R.id.common_pop_up_arrow_up);
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		width = dm.widthPixels;
		
		init(layout);
	}
	
	
	private void init(View view) {
		this.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				dismiss();
			}

		});
		this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this.setFocusable(true);
		this.setTouchable(true);
		this.setOutsideTouchable(true);
	}


	@Override
	public void showAsDropDown(View anchor) {
		int[] pos = new int[2];
		anchor.getLocationInWindow(pos);
		pos[1] += anchor.getMeasuredHeight() - anchor.getPaddingBottom();
		arrowUp.bringToFront();

		RelativeLayout.LayoutParams arrowRL = (RelativeLayout.LayoutParams) arrowUp
				.getLayoutParams();
		arrowRL.rightMargin = width - pos[0]  - (anchor.getMeasuredWidth() / 2) 
				- arrowUp.getMeasuredWidth();
		arrowUp.setLayoutParams(arrowRL);

		setAnimationStyle(R.style.TitleBarPopupWindowAnim);
		showAtLocation(anchor, Gravity.RIGHT | Gravity.TOP , 8 , pos[1]);
	}
	
	
	
	

}
