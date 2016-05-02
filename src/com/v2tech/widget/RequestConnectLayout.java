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
	private boolean flag;
	
	private RequestConnectLayoutListener listener;
	

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
		init();
		if (leftBtn == null) {
			return;
		} else {
			leftBtn.setImageResource(resId);
		}
	}
	
	public void updateRightBtnIcon(int resId) {
		init();
		if (rightBtn == null) {
			return;
		} else {
			rightBtn.setImageResource(resId);
		}
	}
	
	
	public View getLeftBtn() {
		init();
		return this.leftBtn;
	}
	
	public View getRightBtn() {
		init();
		return this.rightBtn;
	}
	

	
	private OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			
			int id = v.getId();
			switch (id) {
			case R.id.request_connect_right_btn:
				listener.onRequestConnectRightBtnClicked(v);
				break;
			case R.id.request_connect_left_btn:
				listener.onRequestConnectLeftBtnClicked(v);
				break;
			}
		}
		
	};
	
	private void init() {
		if (flag) {
			return;
		}
		if (rightBtn == null) {
			rightBtn = (ImageView)findViewById(R.id.request_connect_right_btn);
			rightBtn.setOnClickListener(click);
		}
		
		if (leftBtn == null) {
			leftBtn = (ImageView)findViewById(R.id.request_connect_left_btn);
			leftBtn.setOnClickListener(click);
		}
		
		flag = true;
	}
	
	
	
	
	public RequestConnectLayoutListener getListener() {
		return listener;
	}

	public void setListener(RequestConnectLayoutListener listener) {
		this.listener = listener;
		init();
	}




	public interface RequestConnectLayoutListener {
		public void onRequestConnectLeftBtnClicked(View v);
		public void onRequestConnectRightBtnClicked(View v);
	}
}
