package com.v2tech.widget;

import com.V2.jni.util.V2Log;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;

public class CircleViewPager extends ViewGroup {

	private static final boolean DEBUG = true;
	private static final String TAG = "CircleViewPager";

	private static final int DEFAULT_OFFSCREEN_PAGES = 1;

	private int mCurrItem;

	private int mOffscreenPageLimit;

	private boolean mIsBeingDragged;

	private boolean mFakeDragging;

	private OnPageChangeListener mOnPageChangeListener;

	public CircleViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CircleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CircleViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = r - l;
        int height = b - t;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
	    int offsetLeft = paddingLeft;
	    
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (DEBUG) {
				V2Log.d(TAG, "  MeasuredHeight:" +child.getMeasuredHeight()+"  MeasuredWidth:" + child.getMeasuredWidth());
			}
			//TODO add layout margin left
			offsetLeft += child.getPaddingLeft() + i * child.getMeasuredWidth();
			if (DEBUG) {
				V2Log.d(TAG, "layout index: " + i + "  l:" + offsetLeft
						+ "  t:" + (t + paddingTop) + "   r:"
						+ (offsetLeft + child.getMeasuredWidth()) + " b:"
						+ (t + paddingTop + child.getMeasuredHeight()));
			}
			child.layout(offsetLeft, t + paddingTop, offsetLeft + child.getMeasuredWidth(), t + paddingTop + child.getMeasuredHeight());
		}

	}

	@Override
	public void addView(View child, int index, LayoutParams params) {
		if (DEBUG) {
			V2Log.e(TAG, child + "  " + index + "   params");
		}
		super.addView(child, index, params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// For simple implementation, our internal size is always 0.
		// We depend on the container to specify the layout size of
		// our view. We can't really know what it is since we will be
		// adding and removing different arbitrary views and do not
		// want the layout to change as this happens.
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
				getDefaultSize(0, heightMeasureSpec));

		final int measuredWidth = getMeasuredWidth();

		// Children are just made to fill our space.
		int childWidthSize = measuredWidth - getPaddingLeft()
				- getPaddingRight();
		int childHeightSize = getMeasuredHeight() - getPaddingTop()
				- getPaddingBottom();


		V2Log.d(TAG, "childWidthSize:" + childWidthSize + "  childHeightSize:"
				+ childHeightSize);
		int size = getChildCount();
		for (int i = 0; i < size; ++i) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (lp != null) {
					int widthMode = MeasureSpec.AT_MOST;
					int heightMode = MeasureSpec.AT_MOST;

					int widthSize = childWidthSize;
					int heightSize = childHeightSize;
					if (lp.width != LayoutParams.WRAP_CONTENT) {
						widthMode = MeasureSpec.EXACTLY;
						if (lp.width != LayoutParams.MATCH_PARENT) {
							widthSize = lp.width;
						}
					}
					if (lp.height != LayoutParams.WRAP_CONTENT) {
						heightMode = MeasureSpec.EXACTLY;
						if (lp.height != LayoutParams.MATCH_PARENT) {
							heightSize = lp.height;
						}
					}
					final int widthSpec = MeasureSpec.makeMeasureSpec(
							widthSize, widthMode);
					final int heightSpec = MeasureSpec.makeMeasureSpec(
							heightSize, heightMode);
					child.measure(widthSpec, heightSpec);

				}
			}
		}

		int mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
				childWidthSize, MeasureSpec.EXACTLY);
		int mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
				childHeightSize, MeasureSpec.EXACTLY);

		size = getChildCount();
		for (int i = 0; i < size; ++i) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {

				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (lp == null) {
					final int widthSpec = MeasureSpec.makeMeasureSpec(
							(int) (childWidthSize), MeasureSpec.EXACTLY);
					child.measure(widthSpec, mChildHeightMeasureSpec);
				}
			}
		}
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mOnPageChangeListener = listener;
	}

	public void setAdapter(PagerAdapter adapter) {
		adapter.startUpdate(this);
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			adapter.instantiateItem(this, i);
		}
		adapter.setPrimaryItem(this, 0, null);
		adapter.finishUpdate(this);
	}

	public void setOffscreenPageLimit(int limit) {
		if (limit < DEFAULT_OFFSCREEN_PAGES) {
			Log.w(TAG, "Requested offscreen page limit " + limit
					+ " too small; defaulting to " + DEFAULT_OFFSCREEN_PAGES);
			limit = DEFAULT_OFFSCREEN_PAGES;
		}
		if (limit != mOffscreenPageLimit) {
			mOffscreenPageLimit = limit;
		}
	}

	public int getCurrentItem() {
		return mCurrItem;
	}

	public void setCurrentItem(int item, boolean smoothScroll) {
		// TODO Auto-generated constructor stub
	}

	public boolean beginFakeDrag() {
		if (mIsBeingDragged) {
			return false;
		}
		mFakeDragging = true;
		// TODO Auto-generated constructor stub
		return true;
	}

	public void fakeDragBy(float xOffset) {
		if (!mFakeDragging) {
			throw new IllegalStateException(
					"No fake drag in progress. Call beginFakeDrag first.");
		}
	}

	public void endFakeDrag() {
		if (!mFakeDragging) {
			throw new IllegalStateException(
					"No fake drag in progress. Call beginFakeDrag first.");
		}

	}

	public interface OnPageChangeListener {

		/**
		 * This method will be invoked when the current page is scrolled, either
		 * as part of a programmatically initiated smooth scroll or a user
		 * initiated touch scroll.
		 * 
		 * @param position
		 *            Position index of the first page currently being
		 *            displayed. Page position+1 will be visible if
		 *            positionOffset is nonzero.
		 * @param positionOffset
		 *            Value from [0, 1) indicating the offset from the page at
		 *            position.
		 * @param positionOffsetPixels
		 *            Value in pixels indicating the offset from position.
		 */
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);

		/**
		 * This method will be invoked when a new page becomes selected.
		 * Animation is not necessarily complete.
		 * 
		 * @param position
		 *            Position index of the new selected page.
		 */
		public void onPageSelected(int position);

		/**
		 * Called when the scroll state changes. Useful for discovering when the
		 * user begins dragging, when the pager is automatically settling to the
		 * current page, or when it is fully stopped/idle.
		 * 
		 * @param state
		 *            The new scroll state.
		 * @see CopyOfLoopViewPager#SCROLL_STATE_IDLE
		 * @see CopyOfLoopViewPager#SCROLL_STATE_DRAGGING
		 * @see CopyOfLoopViewPager#SCROLL_STATE_SETTLING
		 */
		public void onPageScrollStateChanged(int state);
	}

}
