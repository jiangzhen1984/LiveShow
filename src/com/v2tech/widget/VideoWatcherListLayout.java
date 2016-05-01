package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.v2tech.v2liveshow.R;

public class VideoWatcherListLayout extends RelativeLayout {
	
	private View publisher;
	
	private VideoWatcherListLayoutListener listener;

	public VideoWatcherListLayout(Context context) {
		super(context);
	}

	public VideoWatcherListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoWatcherListLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.liver_ly) {
			publisher = child;
			publisher.setOnClickListener(clickListener);
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
			case R.id.liver_ly:
				listener.onPublisherBtnClicked(v);
				break;
			}
		}
		
	};
	
	
	
	
	
	public VideoWatcherListLayoutListener getListener() {
		return listener;
	}

	public void setListener(VideoWatcherListLayoutListener listener) {
		this.listener = listener;
	}





	public interface VideoWatcherListLayoutListener {
		public void onPublisherBtnClicked(View v);
	}

}
