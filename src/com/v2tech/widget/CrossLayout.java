package com.v2tech.widget;

import com.V2.jni.util.V2Log;
import com.example.camera.CameraView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

public class CrossLayout extends FrameLayout implements OnTouchListener {

	private View lView;
	
	private View midView;
	
	private View rView;
	
	private View rView2;
	
	private View cur;
	
	public CrossLayout(Context context) {
		super(context);
		this.setOnTouchListener(this);
	}

	public CrossLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
	}

	public CrossLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnTouchListener(this);
	}

	int initX;
	int initY;
	int deltaX;
	int deltaY;
	int lastX;
	int lastY;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			initX = (int) event.getX();
			initY = (int) event.getY();
			lastX = initX;
			lastY = initY;
			break;
		case MotionEvent.ACTION_MOVE:
			deltaX = (int) event.getX() - lastX;
			deltaY = (int) event.getY() - lastY;
			int count = getChildCount();
			boolean canMove = false;
			
			V2Log.e("=====" + deltaX);
			View left = lView;
			View right = rView2;
			if (deltaX < 0) {
				if (right != null) {
					FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) right.getLayoutParams();
					if (fl.leftMargin + deltaX > 0) {
						canMove = true;
					}
					
				}
			} else if (deltaX > 0) {
				if (left != null) {
					FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) left.getLayoutParams();
					if (fl.leftMargin + deltaX < 0) {
						canMove = true;
					}
				}
			}
			if (canMove && deltaX != 0) {
				for (int i = 0; i < count; i++) {
					View child =  getChildAt(i);
					FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) child.getLayoutParams();
					fl.leftMargin += deltaX;
					child.setLayoutParams(fl);
					if (child == rView2) {
						if (fl.leftMargin + deltaX >= 0 && fl.leftMargin + deltaX <= getWidth()) {
							((CameraView)child).startPreView();
						} else if (fl.leftMargin + deltaX > getWidth()) {
							((CameraView)child).stopPreView();
						}
					}
					
					if (fl.leftMargin > 0 && fl.leftMargin < getWidth() && deltaX < 0) {
						distance = fl.leftMargin;
					} 
					if (fl.leftMargin < 0 && fl.leftMargin + getWidth() > 0 && deltaX > 0) {
						distance = Math.abs(fl.leftMargin); 
					}
				}
			}
			
			
			

			// TODO update
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			break;
		case MotionEvent.ACTION_UP:
			this.post(Flying);
			break;

		}
		return true;
	}

	
	private int distance = 0;
	
	private Runnable Flying = new  Runnable() {

		@Override
		public void run() {
			
			
			int count = getChildCount();
			boolean canMove = false;
			
			View left = lView;
			View right = rView2;
			int dx = deltaX < 0 ? -20 : 20;
			if (deltaX < 0 && distance > 0) {
				if (right != null) {
					FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) right.getLayoutParams();
					if (fl.leftMargin + dx > 0) {
						canMove = true;
					}
					
				}
			} else if (deltaX > 0 && distance > 0) {
				if (left != null) {
					FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) left.getLayoutParams();
					if (fl.leftMargin + dx < 0) {
						canMove = true;
					}
				}
			}
			if (canMove) {
				for (int i = 0; i < count; i++) {
					View child =  getChildAt(i);
					FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) child.getLayoutParams();
					fl.leftMargin += dx;
					child.setLayoutParams(fl);
					if (child == rView2) {
						if (fl.leftMargin + dx >= 0 && fl.leftMargin + dx <= getWidth()) {
							((CameraView)child).startPreView();
						} else if (fl.leftMargin + dx > getWidth()) {
							((CameraView)child).stopPreView();
						}
					}
				}
			}
			distance -= Math.abs(dx);
			if (distance > 0) {
				post(Flying);
			}
			
		}
		
	};
	
	
	private View cv;
	

	public void setLeft(View left) {
		this.lView = left;
	}


	public void setMiddle(View middle) {
		this.midView = middle;
		cur = midView;
	}


	public void setRight(View right) {
		this.rView = right;
	}
	
	public void setrView2(View bo) {
		this.rView2 = bo;
	}
	

	
}
