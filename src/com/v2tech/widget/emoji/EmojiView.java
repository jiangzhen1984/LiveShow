package com.v2tech.widget.emoji;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.v2tech.v2liveshow.R;

public class EmojiView extends LinearLayout {
	
	
	private TableLayout tableLayout;
	
	private EmojiViewListener listener;

	public EmojiView(Context context) {
		super(context);
	}

	public EmojiView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmojiView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.emoji_view_table) {
			tableLayout = (TableLayout)child;
		}
	}
	
	
	public void initEmojis(int start, int end, int[] resTab) {
		int cols = 10; 
		int size = end - start;
		int rows = size / cols + 1;
		TableRow row = null;
		Context ctx = getContext();
		for (int i = 0; i < rows && resTab.length > start; i++) {
			row = new TableRow(ctx);
			tableLayout.addView(row, new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.WRAP_CONTENT));
			ImageView iv = null;
			for (int j = 0; j < cols && resTab.length > start; j++) {
				iv = new ImageView(ctx);
				iv.setTag(start);
				iv.setImageResource(resTab[start++]);
				iv.setOnClickListener(clickListener);
				row.addView(iv, new TableRow.LayoutParams(j));
			}
		}
	}
	
	
	
	
	
	public EmojiViewListener getListener() {
		return listener;
	}

	public void setListener(EmojiViewListener listener) {
		this.listener = listener;
	}





	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			listener.onEmojiClicked(v);
		}
		
	};
	
	
	public interface EmojiViewListener {
		public void onEmojiClicked(View v);
	}

}
