package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

public class CrossLayout extends FrameLayout implements OnTouchListener {

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
			initX = (int)event.getX();
			initY = (int)event.getY();
			lastX = initX;
			lastY = initY;
			break;
		case MotionEvent.ACTION_MOVE:
			deltaX = (int)event.getX() - lastX;
			deltaY = (int)event.getY() - lastY;
			int count = getChildCount();
			boolean canMove = true;
			for (int i =0; i <count; i++) {
				View child = getChildAt(i);
				if (i == 0 && (child.getLeft() > 0 && deltaX > 0)) {
					canMove = false;
				}
				if ((child.getRight() < getWidth() && deltaX < 0) && i == count -1) {
					canMove = false;
				}
				if (canMove) {
					child.offsetLeftAndRight(deltaX);
				}
				
			}
			//TODO update
			lastX = (int)event.getX();
			lastY = (int)event.getY();
			break;
			
		}
		return true;
	}
	
	

}
