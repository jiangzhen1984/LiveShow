package com.v2tech.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.V2.jni.util.V2Log;

public class CircleViewPager extends ViewGroup {

	private static final boolean DEBUG = true;

	private static final String TAG = "CircleViewPager";

	private static final int mDefaultVelocity = 40;

	private static final int MIN_FLING_VELOCITY = 200; // dips

	private int mMinimumVelocity;

	private int mMaximumVelocity;

	private int mInimumFlingVelocity;

	private VelocityTracker mVelocityTracker;

	private static final int DEFAULT_OFFSCREEN_PAGES = 1;

	private int mCurrItem;

	private int mMoveOffset;

	private int mLastMotionX;

	private long mFakeDragBeginTime;

	private int mUpOffset;

	private int mLastMotionY;

	private OnPageChangeListener mOnPageChangeListener;

	private PagerAdapter mPageAdapter;

	private Flying flying;

	private List<ItemInfo> mItems = new ArrayList<ItemInfo>();

	public CircleViewPager(Context context) {
		super(context);
		init(context);
	}

	public CircleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CircleViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		final float density = context.getResources().getDisplayMetrics().density;

		mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		mInimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
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
			// TODO add layout margin left
			offsetLeft = child.getPaddingLeft() + (i - mCurrItem)
					* child.getMeasuredWidth() + mMoveOffset;

			if (count > 2) {
				if (mCurrItem == 0) {
					if (i == count - 1) {
						offsetLeft = child.getPaddingLeft()
								+ -child.getMeasuredWidth() + mMoveOffset;
					}
				} else if (mCurrItem == count - 1) {
					if (i == 0) {
						offsetLeft = child.getPaddingLeft() + (i + 1)
								* child.getMeasuredWidth() + mMoveOffset;
					}
				}
			}

			if (DEBUG) {
				V2Log.d(TAG, "mCurrItem:" + mCurrItem + "   i:" + i
						+ "  offsetLeft:" + offsetLeft + "   mMoveOffset:"
						+ mMoveOffset + "  width" + child.getMeasuredWidth()
						+ "  padding:left:" + child.getPaddingLeft());
			}

			// TODO fix offsetLeft;
			child.layout(offsetLeft, t + paddingTop,
					offsetLeft + child.getMeasuredWidth(), t + paddingTop
							+ child.getMeasuredHeight());

		}
 
	}

	@Override
	public void addView(View child, int index, LayoutParams params) {
		if (DEBUG) {
			V2Log.w(TAG, child + "  " + index + "   params");
		}
		super.addView(child, index, params);
	}
	
	


	@Override
	public void removeView(View view) {
		super.removeView(view);
		if (DEBUG) {
			V2Log.e(TAG, "Remove view:" + view + "-----------------------new count:" + getChildCount());
		}
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

		if (DEBUG) {
			V2Log.d(TAG, "childWidthSize:" + childWidthSize
					+ "  childHeightSize:" + childHeightSize);
		}
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
		if (mPageAdapter != null) {
			mPageAdapter.startUpdate(this);
			mPageAdapter.unregisterDataSetObserver(observer);
			for (int i = 0; i < mItems.size(); i++) {
				final ItemInfo ii = mItems.get(i);
				mPageAdapter.destroyItem(this, i, ii.obj);
			}
			mPageAdapter.finishUpdate(this);
			mItems.clear();
		}

		adapter.startUpdate(this);
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			ItemInfo ii = new ItemInfo();
			ii.obj = adapter.instantiateItem(this, i);
			mItems.add(ii);
		}
		adapter.setPrimaryItem(this, mCurrItem, null);
		adapter.finishUpdate(this);

		mPageAdapter = adapter;
		mPageAdapter.registerDataSetObserver(observer);
	}

	public void setOffscreenPageLimit(int limit) {

	}

	public int getCurrentItem() {
		return mCurrItem;
	}

	public void setCurrentItem(int item, boolean smoothScroll) {
		this.mCurrItem = item;
		requestLayout();
	}

	public void fakeDragUpBy(int offsetY) {
		mLastMotionY += offsetY;
		// Synthesize an event for the VelocityTracker.
		final long time = SystemClock.uptimeMillis();
		final MotionEvent ev = MotionEvent.obtain(mFakeDragBeginTime, time,
				MotionEvent.ACTION_MOVE, 0, mLastMotionY, 0);
		mVelocityTracker.addMovement(ev);
		ev.recycle();

		mUpOffset += offsetY;
		// Update
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (i == mCurrItem) {
				child.offsetTopAndBottom(-offsetY);
			} else {
				child.offsetLeftAndRight(-offsetY);
			}
		}

	}

	public void endFakeDragUp() {

		endDrag();
	}

	public boolean beginFakeDrag() {
		mMoveOffset = 0;
		mUpOffset = 0;
		final long time = SystemClock.uptimeMillis();

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		} else {
			mVelocityTracker.clear();
		}

		final MotionEvent ev = MotionEvent.obtain(time, time,
				MotionEvent.ACTION_DOWN, 0, 0, 0);
		mVelocityTracker.addMovement(ev);
		ev.recycle();

		mFakeDragBeginTime = time;

		return true;
	}

	public void fakeDragBy(float xOffset) {
		mLastMotionX += xOffset;
		// Synthesize an event for the VelocityTracker.
		final long time = SystemClock.uptimeMillis();
		final MotionEvent ev = MotionEvent.obtain(mFakeDragBeginTime, time,
				MotionEvent.ACTION_MOVE, mLastMotionX, 0, 0);
		mVelocityTracker.addMovement(ev);
		ev.recycle();

		mMoveOffset += xOffset;
		// requestLayout();
		doDrag((int) xOffset);
	}

	public void endFakeDrag() {
		int childCount = getChildCount();
		int nextPage = this.mCurrItem;
		int dis = 0;
		final VelocityTracker velocityTracker = mVelocityTracker;
		velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
		int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(
				velocityTracker, 0);
		float pageOffsetPercent = (float) Math.abs(mMoveOffset)
				/ (float) getWidth();

		V2Log.e(pageOffsetPercent + "   " + getWidth() + "    " + mMoveOffset);
		if (pageOffsetPercent > 0.15 || initialVelocity > mInimumFlingVelocity) {
			if (mMoveOffset < 0) {
				nextPage += 1;
				dis = getWidth() - Math.abs(mMoveOffset);
			} else if (mMoveOffset > 0) {
				nextPage -= 1;
				dis = getWidth() - Math.abs(mMoveOffset);
			}
		}
		if (nextPage < 0) {
			nextPage = childCount - 1;
		} else if (nextPage >= childCount) {
			nextPage = 0;
		}

		// If nextPage still same with current
		// means we need to rollback page
		if (nextPage == this.mCurrItem) {
			dis = -mMoveOffset;
		}

		scrollToPage(nextPage, mMoveOffset > 0 ? -dis : dis, initialVelocity);

		endDrag();
	}

	private void scrollToPage(int page, int restDis, int velocity) {
		if (DEBUG) {
			V2Log.e("scrolling to :" + page + "  restDis:" + restDis
					+ "  velocity:" + velocity);
		}
		if (flying == null) {
			flying = new Flying();
		}
		flying.startFlying(restDis > 0 ? -mDefaultVelocity : mDefaultVelocity,
				restDis);
		this.postOnAnimation(flying);
		this.mCurrItem = page;
		mPageAdapter.setPrimaryItem(this, this.mCurrItem, null);
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageSelected(this.mCurrItem);
		}
	}

	private void doDrag(int xOffset) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			child.offsetLeftAndRight(xOffset);
		}
	}

	private void endDrag() {
		mMoveOffset = 0;
		mLastMotionX = 0;
		mUpOffset = 0;
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private void datasetChanged() {

		boolean isUpdating = false;
		for (int i = 0; i < mItems.size(); i++) {
			final ItemInfo ii = mItems.get(i);
			final int newPos = this.mPageAdapter.getItemPosition(ii.obj);

			if (newPos == PagerAdapter.POSITION_UNCHANGED) {
				continue;
			}

			if (newPos == PagerAdapter.POSITION_NONE) {
				if (DEBUG) {
					V2Log.i(TAG, "REMOVING ..... "+ii.obj);
				}
				mItems.remove(i);
				if (mCurrItem == i && mCurrItem > 0) {
					mCurrItem --;
				}
				i--;
				
				if (!isUpdating) {
					isUpdating = true;
					mPageAdapter.startUpdate(this);
				}
				mPageAdapter.destroyItem(this, i, ii.obj);
				continue;
			}
			
			
		}
		
		if (DEBUG) {
			for (int i = 0; i < mItems.size(); i++) {
				final ItemInfo ii = mItems.get(i);
				V2Log.i(TAG, "NEW POS .....    index : " + i+"  view:"+ii.obj);
			}
		}

		if (isUpdating) {
			mPageAdapter.finishUpdate(this);
		}
		
		
		mPageAdapter.startUpdate(this);
		int adapterCount = mPageAdapter.getCount();
		for (int i = mItems.size(); i < adapterCount; i++) {
				
			ItemInfo ii = new ItemInfo();
			ii.obj = mPageAdapter.instantiateItem(this, i);
			mItems.add(ii);
			if (DEBUG) {
				V2Log.d(TAG, "Inital new view:" + ii.obj);
			}
				
		}
		mPageAdapter.setPrimaryItem(this, this.mCurrItem, null);
		mPageAdapter.finishUpdate(this);
		
		
		//TODO smooth to scroll to next
		requestLayout();
	}

	static class ItemInfo {
		Object obj;

	}

	class Flying implements Runnable {

		int initVelocity;
		int dis;

		public void startFlying(int initVelocity, int dis) {
			this.initVelocity = initVelocity;
			this.dis = dis;
		}

		@Override
		public void run() {
			if (DEBUG) {
				V2Log.d(TAG, "[FLYING] : " + initVelocity + "   " + dis);
			}

			if (dis == 0) {
				requestLayout();
			} else {
				if (Math.abs(initVelocity) > Math.abs(dis)) {
					if (initVelocity > 0) {
						initVelocity = initVelocity
								- Math.abs(initVelocity + dis);
					} else {
						initVelocity = initVelocity
								+ Math.abs(initVelocity + dis);
					}
				}

				int count = getChildCount();
				for (int i = 0; i < count; i++) {
					View child = getChildAt(i);
					child.offsetLeftAndRight(initVelocity);
				}

				dis += initVelocity;

				if (initVelocity > 0) {
					initVelocity += 35;
				} else {
					initVelocity -= 35;
				}

				postOnAnimationDelayed(this, 50);
			}
		}

	};

	private DataSetObserver observer = new DataSetObserver() {

		@Override
		public void onChanged() {
			datasetChanged();
		}

		@Override
		public void onInvalidated() {
			datasetChanged();
		}

	};

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

		public void onPagePreapredRemove(int item);
	}

}
