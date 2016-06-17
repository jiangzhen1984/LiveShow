package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewConfiguration;

public class TouchSurfaceView extends SurfaceView {

	private Translate translate;
	private FlyingX fx;

	private int mTouchSlop;
	private int mTouchTapTimeout;
	private int initX;
	private int initY;
	
	public TouchSurfaceView(Context context) {
		super(context);
		init();
	}

	public TouchSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TouchSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mTouchTapTimeout = ViewConfiguration.getTapTimeout();
	}

	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (translate == null) {
			return false;
		}
		int disX = (int)event.getX() - initX;
		int disY = (int)event.getY() - initY;
		
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			translate.onStartTranslate();
			initX = (int)event.getX();
			initY = (int)event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(disX) > mTouchSlop) {
				float cent = (float)disX / (float)getMeasuredWidth();
				translate.onTranslate(cent, 0F);
			}
			break;
		case MotionEvent.ACTION_UP:
			//do touch tap check
			if (Math.abs(disX) <= mTouchSlop && Math.abs(disY) <= mTouchSlop && (event.getEventTime() - event.getDownTime()) < mTouchTapTimeout) {
				this.performClick();
				break;
			}
			if (fx == null) {
				fx = new FlyingX();
			}
			fx.startFlying(disX, getMeasuredWidth());
			break;
		}
		
		return true;
	}

	public interface Translate {
		public void onStartTranslate();

		public void onTranslate(float x, float y);

		public void onFinishTranslate();
	}
	

	public Translate getTranslate() {
		return translate;
	}

	public void setTranslate(Translate translate) {
		this.translate = translate;
	}

	class FlyingX implements Runnable {

		int initVelocity;
		int distance = 0;
		float cent;
		int type = 1;
		float offset;
		float limition;

		public void startFlying(int offset, int limition) {
			this.offset = offset;
			this.limition = limition;
			this.cent = (float) offset / (float) limition;
			if (this.cent > 0.2F) {
				type = 2;
				distance = Math.abs(getMeasuredWidth() - offset);
			} else if (cent > 0.0F && cent < 0.2F) {
				distance = offset;
				type = 1;
			} else if (this.cent < -0.2F) {
				type = 1;
				distance = getMeasuredWidth() + offset;
			} else if (this.cent > -0.2F && this.cent < 0.0F) {
				type = 2;
				distance = -offset;
			}
			initVelocity = 95;
			postOnAnimation(this);
		}

		@Override
		public void run() {
			if (distance > 0) {
				if (distance - initVelocity < 0) {
					initVelocity = distance;
				}

				if (type == 1) {
					offset -= initVelocity;
				} else {
					offset += initVelocity;
				}

				translate.onTranslate(offset / limition, 0F);
				distance -= initVelocity;

				postOnAnimationDelayed(this, 15);
			} else {
				translate.onFinishTranslate();
			}
		}

	};

}
