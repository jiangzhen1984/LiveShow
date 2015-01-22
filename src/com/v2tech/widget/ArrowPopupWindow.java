package com.v2tech.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.v2tech.v2liveshow.R;
import com.v2tech.view.LiveRecord;

public class ArrowPopupWindow extends PopupWindow {

	private View itemLayout;
	private View arrowUp;
	private int width;

	public ArrowPopupWindow(final Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.title_bar_pop_up_window, null);

		itemLayout = layout.findViewById(R.id.common_pop_window_container);

		arrowUp = layout.findViewById(R.id.common_pop_up_arrow_up);

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		width = dm.widthPixels;

		init(layout);

		itemLayout.findViewById(R.id.title_bar_item_share_video)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent();
						i.setClass(context, LiveRecord.class);
						context.startActivity(i);
						dismiss();
					}

				});
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
		view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		this.setWidth(view.getMeasuredWidth());
		this.setHeight(view.getMeasuredHeight());
		this.setContentView(view);

		arrowUp.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
	}

	@Override
	public void showAsDropDown(View anchor) {
		int[] pos = new int[2];
		anchor.getLocationInWindow(pos);
		pos[1] += anchor.getMeasuredHeight() - anchor.getPaddingBottom();
		arrowUp.bringToFront();

		RelativeLayout.LayoutParams arrowRL = (RelativeLayout.LayoutParams) arrowUp
				.getLayoutParams();
		arrowRL.rightMargin = width - pos[0] - (anchor.getMeasuredWidth() / 2)
				- arrowUp.getMeasuredWidth();
		arrowUp.setLayoutParams(arrowRL);

		setAnimationStyle(R.style.TitleBarPopupWindowAnim);
		super.showAsDropDown(anchor);
		// showAtLocation(anchor, Gravity.RIGHT | Gravity.TOP , 8 , pos[1]);
	}

}
