package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.V2.jni.util.V2Log;
import com.v2tech.R;

public class VerticalSpinWidget extends FrameLayout {
	
	private View volumnCurrent;
	private View volumnMax;
	private View volumeControlBtn;
	
	private OnSpinVolumeChangedListener listener;
	
	private Type type = Type.DOWN_TO_UP;
	
	private float cent;

	public VerticalSpinWidget(Context context) {
		super(context);
	}

	public VerticalSpinWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VerticalSpinWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.voice_volume_current_view) {
			volumnCurrent = child;
		} else if (child.getId() == R.id.voice_volume_control_btn) {
			volumeControlBtn = child;
		} else if(child.getId() == R.id.voice_volume_maximum_view) {
			volumnMax = child;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
	
	int initY = 0;
	int lastY = 0;
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			initY = (int)event.getY();
			lastY = initY;
			break;
		case MotionEvent.ACTION_MOVE:
			volumnCurrent.offsetTopAndBottom((int)event.getY() - lastY);
			volumeControlBtn.offsetTopAndBottom((int)event.getY() - lastY);
			lastY = (int)event.getY();
			break;
		case MotionEvent.ACTION_UP:
			if (type == Type.DOWN_TO_UP) {
				cent = ((float)(getBottom() - volumnCurrent.getTop())) / (float)getBottom();  
			} else {
				
			}
			requestLayout();
			if (listener != null) {
				listener.OnSpinVolumeChanged(cent);
			}
			break;
		}
		
		return true;
	}
	
	
	public void setCent(float cent) {
		if (cent < 0F || cent > 1.0F) {
			throw new RuntimeException(" Out of range  (0 -1)");
		}
		this.cent = cent;
		requestLayout();
	}
	
	public float getCent() {
		return cent;
	}
	
	
	
	
	public boolean fakeStartDrag() {
		return true;
	}
	
	
	public void fakeDrag(int offsetY) {
		volumnCurrent.offsetTopAndBottom(offsetY);
	}

	public boolean fakeEndDrag() {
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams)volumnCurrent.getLayoutParams();
		FrameLayout.LayoutParams controllBtnfl = (FrameLayout.LayoutParams)volumeControlBtn.getLayoutParams();
		if (type == Type.DOWN_TO_UP) {
			fl.topMargin = (int)((bottom - top) * (1.0F - cent));
			controllBtnfl.topMargin = fl.topMargin;
		} else {
			
		}
		super.onLayout(changed, left, top, right, bottom);
	}

	
	


	
	public OnSpinVolumeChangedListener getListener() {
		return listener;
	}

	public void setListener(OnSpinVolumeChangedListener listener) {
		this.listener = listener;
	}






	public interface OnSpinVolumeChangedListener {
		public void OnSpinVolumeChanged(float cent);
	}


	enum Type {
		DOWN_TO_UP, UP_TO_DOWN;
	}
	
	

}
