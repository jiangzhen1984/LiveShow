package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class FloatEditText extends EditText {

	public FloatEditText(Context context) {
		super(context);
	}

	public FloatEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FloatEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	
	
	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (mBackKeyListener != null) {
			if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
				mBackKeyListener.OnBackkeyClicked(this);
			}
		}
		return super.onKeyPreIme(keyCode, event);
	}
	
	private OnBackkeyClickedListener mBackKeyListener;
	
	
	public void setOnBackKeyClickedListener(OnBackkeyClickedListener backKeyListener) {
		this.mBackKeyListener = backKeyListener;
	}
	
	public interface OnBackkeyClickedListener {
		public void OnBackkeyClicked(View v);
	}
	

}
