package com.v2tech.widget.emoji;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.v2tech.v2liveshow.R;
import com.v2tech.widget.emoji.EmojiView.EmojiViewListener;

public class EmojiLayoutWidget extends LinearLayout {
	
	
	private static int[] EMOJI_RES_TABLE = new int[R.drawable.emo_75 - R.drawable.emo_01 + 2];
	private static final int VIEW_COLS = 10;
	private static final int VIEW_ROWS = 3;
	
	static {
		for (int i = 1 ; i <= R.drawable.emo_75 - R.drawable.emo_01; i++) {
			EMOJI_RES_TABLE[i] = R.drawable.emo_01 + (i - 1);
		}
	}
	
	
	ViewPager viewPager;
	LinearLayout pageIndicator;
	
	private LocalAdapter adapter;
	private LocalObject[] objs;
	
	
	private EmojiLayoutWidgetListener listener;

	public EmojiLayoutWidget(Context context) {
		super(context);
		init();
	}

	public EmojiLayoutWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EmojiLayoutWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		
	}
	
	
	private void init() {
		adapter = new LocalAdapter();
		objs = new LocalObject[EMOJI_RES_TABLE.length /( VIEW_ROWS *VIEW_COLS) + 1];
		for (int i = 0; i <objs.length; i++) {
			objs[i] = new LocalObject();
			objs[i].start = i * (VIEW_ROWS *VIEW_COLS) + 1;
			objs[i].end = (i + 1) * (VIEW_ROWS *VIEW_COLS);
		}
	}
	
	

	
	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child instanceof android.support.v4.view.ViewPager) {
			viewPager = (ViewPager) child;
			viewPager.setAdapter(adapter);
		}
	}



	private 


	class LocalAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return objs.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return ((LocalObject)obj).view == view;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			LocalObject lo = objs[position];
			LayoutInflater flater =LayoutInflater.from(getContext());
			EmojiView ev = (EmojiView)flater.inflate(R.layout.emoji_view_layout, null, false);
			ev.initEmojis(lo.start, lo.end, EMOJI_RES_TABLE);
			lo.view = ev;
			ev.setListener(evListener);
			container.addView(ev, position, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			return lo;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(objs[position].view);
		}
		
		
		
		
	}
	
	
	private EmojiViewListener evListener = new EmojiViewListener() {

		@Override
		public void onEmojiClicked(View v) {
			if (listener == null) {
				return;
			}
			listener.onEmojiClicked(v);
			
		}
		
	};
	
	
	class LocalObject {
		int start;
		int end;
		View view;
	}
	
	

	
	
	public EmojiLayoutWidgetListener getListener() {
		return listener;
	}

	public void setListener(EmojiLayoutWidgetListener listener) {
		this.listener = listener;
	}



	public interface EmojiLayoutWidgetListener {
		public void onEmojiClicked(View v);
	}

}
