package com.v2tech.widget;

import java.util.ArrayList;
import java.util.List;

import com.V2.jni.util.V2Log;

import android.content.Context;
import android.os.Debug;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.AbsListView.OnScrollListener;

public class VideoCombinedView extends FrameLayout implements OnTouchListener {

	public VideoCombinedView(Context context) {
		super(context);
		init();
	}

	public VideoCombinedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VideoCombinedView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	int mMinimumVelocity;
	int mMaximumVelocity;

	private void init() {
		this.setOnTouchListener(this);
		lChilds = new ArrayList<LocalView>();
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	public boolean performClick() {
		return false;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		vt = VelocityTracker.obtain();
		this.getWidth();
		this.getMeasuredWidth();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		vt.recycle();
	}

	VelocityTracker vt;
	int initX = 0;
	int initY = 0;
	int lastX = 0;
	int lastY = 0;
	int offsetX = 0;
	int offsetY = 0;
	int distanceX = 0;
	int distanceY = 0;

	Direction dr = null;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		vt.addMovement(event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			initX = (int) event.getX();
			initY = (int) event.getY();
			lastX = initX;
			lastY = initY;
			;
			break;
		case MotionEvent.ACTION_MOVE:
			offsetX = (int) event.getX() - lastX;
			offsetY = (int) event.getY() - lastY;
			if (Math.abs(offsetY) > Math.abs(offsetX)) {
				if (offsetY > 0) {
					dr = Direction.DOWN;
				} else {
					dr = Direction.UP;
				}
			} else {
				if (offsetX > 0) {
					dr = Direction.RIGHT;
				} else {
					dr = Direction.LEFT;
				}
			}

			lastX = (int) event.getX();
			lastY = (int) event.getY();

			

			if (dr == Direction.UP || dr == Direction.DOWN) {
				distanceY = lastY - initY;
			} else if (dr == Direction.LEFT || dr == Direction.RIGHT) {
				distanceX = lastX - initX;
			}

			layoutDistance(distanceX, distanceY);

			break;
		case MotionEvent.ACTION_UP:
//			final VelocityTracker velocityTracker = vt;
//			velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
//			final int initialVelocity = (int) velocityTracker.getXVelocity();
//
//			if (Math.abs(initialVelocity) > mMinimumVelocity) {
//				mFlingRunnable.start(-initialVelocity);
//			}

			break;
		}
		return true;
	}

	public void addView(int horIndex, int verIndex, View child,
			FrameLayout.LayoutParams fl) {
		this.addView(child, fl);
		lChilds.add(new LocalView(horIndex, verIndex, verIndex != 0 ? true
				: false, horIndex != 0 ? true : false, child));

	}

	private void layoutDistance(int dx, int dy) {
		int width = getWidth();
		int height = getHeight();

		for (LocalView lv : lChilds) {
			lv.view.layout(lv.horIndex * width + dx, lv.verIndex * height + dy,
					(lv.horIndex + 1) * width + dx, (lv.verIndex + 1) * height
							+ dy);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (changed) {
			int width = right - left;
			int height = bottom - top;
			for (LocalView lv : lChilds) {
				lv.view.layout(lv.horIndex * width, lv.verIndex * height,
						(lv.horIndex + 1) * width, (lv.verIndex + 1) * height);
			}
		}
	}

	private FlingRunnable mFlingRunnable = new FlingRunnable();

	private class FlingRunnable implements Runnable {

		private final OverScroller mScroller;

		private int mLastFlingY;

		FlingRunnable() {
			mScroller = new OverScroller(getContext());
		}

		void start(int initialVelocity) {
			int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
			mLastFlingY = initialY;
			mScroller.fling(0, initialY, 0, initialVelocity, 0,
					Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
			postOnAnimation(this);

		}

		@Override
		public void run() {
			boolean more = mScroller.computeScrollOffset();
			final int y = mScroller.getCurrY();

			// Flip sign to convert finger direction to list items direction
			// (e.g. finger moving down means list is moving towards the top)
			int delta = mLastFlingY - y;
			if (more && delta > 0) {
				layoutDistance(delta, 0);
				mLastFlingY = y;
				postOnAnimation(this);
			}

		}

	};

	private List<LocalView> lChilds;

	class LocalView {
		int horIndex;
		int verIndex;
		boolean isLockXOffset;
		boolean isLockYOffset;
		View view;

		public LocalView(int horIndex, int verIndex, boolean isLockXOffset,
				boolean isLockYOffset, View view) {
			super();
			this.horIndex = horIndex;
			this.verIndex = verIndex;
			this.isLockXOffset = isLockXOffset;
			this.isLockYOffset = isLockYOffset;
			this.view = view;
		}

	}

	enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

}
