package com.v2tech.widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.v2tech.v2liveshow.R;

public class BottomButtonLayout extends RelativeLayout {
	
	private View mapSearchBtn;
	private View messageSendBtn;
	private EditText editText;
	private View locationBtn;
	
	private BottomButtonLayoutListener listener;

	public BottomButtonLayout(Context context) {
		super(context);
	}

	public BottomButtonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BottomButtonLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		int id = child.getId();
		if (id == R.id.map_button) {
			mapSearchBtn = child;
			mapSearchBtn.setOnClickListener(clickListenr);
		} else if (id == R.id.msg_button) {
			messageSendBtn = child;
			messageSendBtn.setOnClickListener(clickListenr);
		} else if (id == R.id.edit_text) {
			editText = (EditText)child;
			editText.setOnClickListener(clickListenr);
		} else if (id == R.id.map_locate_button) {
			locationBtn= child;
			locationBtn.setOnClickListener(clickListenr);
		}
	}
	
	
	private OnClickListener clickListenr = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			int id = v.getId();
			switch (id) {
			case R.id.map_button:
				listener.onMapSearchBtnClicked(v);
				break;
			case R.id.msg_button:
				listener.onMessageSendBtnClicked(v);
				break;
			case R.id.edit_text:
				listener.onEditTextClicked(v);
				break;
			case R.id.map_locate_button:
				listener.onLocationBtnClicked(v);
				break;
			}
			
		}
		
	};
	
	
	public Editable getEditText() {
		return editText.getEditableText();
	}
	
	
	public BottomButtonLayoutListener getListener() {
		return listener;
	}

	public void setListener(BottomButtonLayoutListener listener) {
		this.listener = listener;
	}





	public interface BottomButtonLayoutListener {
		public void onMapSearchBtnClicked(View v);
		public void onMessageSendBtnClicked(View v);
		public void onEditTextClicked(View v);
		public void onLocationBtnClicked(View v);
	}
	
	
	

}
