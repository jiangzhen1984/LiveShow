package com.v2tech.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

public class RichEditText extends EditText {

	public RichEditText(Context context) {
		super(context);
	}

	public RichEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RichEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	public void appendEmoji(Drawable drawable, int idx) {
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		SpannableStringBuilder builder = new SpannableStringBuilder("[at]", 0, 4);
		
		builder.setSpan(new ImageSpan(drawable, idx+""), 0, 4,  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		int start = this.getSelectionStart();
		int end = this.getSelectionEnd();
		if (end == start) {
			this.getText().insert(start, builder);
		} else {
			this.getText().replace(start, end-start, builder);
		}
	}
	
	
	public void appendEmoji(Drawable drawable) {
		appendEmoji(drawable, -1);
	}
	
	public void appendEmoji(int res) {
		Drawable d = getContext().getResources().getDrawable(res);
		appendEmoji(d);
	}

}
