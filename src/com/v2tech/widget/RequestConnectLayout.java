package com.v2tech.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v2tech.v2liveshow.R;

public class RequestConnectLayout extends LinearLayout {
	
	private ImageView avtar;
	private ImageView leftBtn;
	private ImageView rightBtn;
	private TextView text;
	private TextView name;
	

	public RequestConnectLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public RequestConnectLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RequestConnectLayout(Context context) {
		super(context);
	}

	public void updateAvtar(Bitmap bm) {
		
	}
	
	
	public void updateTipsText(String content) {
		if (text == null) {
			text = (TextView)findViewById(R.id.request_connect_text);
		}
		if (text == null) {
			return;
		} else {
			text.setText(content);
		}
	}
	
	public void updateNameText(String content) {
		if (name == null) {
			name = (TextView)findViewById(R.id.request_connect_name);
		}
		if (name == null) {
			return;
		} else {
			name.setText(content);
		}
	}
	
	public void updateLeftBtnIcon(int resId) {
		if (leftBtn == null) {
			leftBtn = (ImageView)findViewById(R.id.request_connect_left_btn);
		}
		if (leftBtn == null) {
			return;
		} else {
			leftBtn.setImageResource(resId);
		}
	}
	
	public void updateRightBtnIcon(int resId) {
		if (rightBtn == null) {
			rightBtn = (ImageView)findViewById(R.id.request_connect_right_btn);
		}
		if (rightBtn == null) {
			return;
		} else {
			rightBtn.setImageResource(resId);
		}
	}
	
	
	public View getLeftBtn() {
		if (leftBtn == null) {
			leftBtn = (ImageView)findViewById(R.id.request_connect_left_btn);
		}
		return this.leftBtn;
	}
	
	public View getRightBtn() {
		if (rightBtn == null) {
			rightBtn = (ImageView)findViewById(R.id.request_connect_right_btn);
		}
		return this.rightBtn;
	}
	

}
